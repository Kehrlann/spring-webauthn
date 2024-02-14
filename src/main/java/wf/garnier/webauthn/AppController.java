package wf.garnier.webauthn;

import java.io.IOException;
import java.util.Base64;
import java.util.UUID;

import com.webauthn4j.WebAuthnManager;
import com.webauthn4j.converter.exception.DataConversionException;
import com.webauthn4j.data.AuthenticationData;
import com.webauthn4j.data.AuthenticationParameters;
import com.webauthn4j.data.AuthenticationRequest;
import com.webauthn4j.data.client.Origin;
import com.webauthn4j.data.client.challenge.DefaultChallenge;
import com.webauthn4j.server.ServerProperty;
import com.webauthn4j.validator.exception.ValidationException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import wf.garnier.webauthn.user.User;
import wf.garnier.webauthn.user.UserAuthenticationToken;
import wf.garnier.webauthn.user.UserRepository;
import wf.garnier.webauthn.user.webauthn.CredentialsRepository;
import wf.garnier.webauthn.user.webauthn.UserAuthenticator;
import wf.garnier.webauthn.user.webauthn.WebAuthNAuthenticatorRepository;

@Controller
@SessionAttributes("challenge")
class AppController {

	private final CredentialsRepository credentialsRepository;

	private final WebAuthNAuthenticatorRepository authenticatorRepository;

	private final WebAuthnManager webAuthnManager = WebAuthnManager.createNonStrictWebAuthnManager();

	private final UserRepository userRepository;

	private final SecurityContextRepository securityContextRepository = new HttpSessionSecurityContextRepository();

	public AppController(CredentialsRepository credentialsRepository,
			WebAuthNAuthenticatorRepository authenticatorRepository, UserRepository userRepository) {
		this.credentialsRepository = credentialsRepository;
		this.authenticatorRepository = authenticatorRepository;
		this.userRepository = userRepository;
	}

	@GetMapping
	public String landingPage(Model model) {
		model.addAttribute("challenge", UUID.randomUUID().toString());
		return "landing-page";
	}

	@GetMapping("/account")
	public String accountPage(@AuthenticationPrincipal User user, Model model) {
		model.addAttribute("username", user.getUsername());
		model.addAttribute("challenge", UUID.randomUUID().toString());
		return "account-page";
	}

	@PostMapping("/register")
	public ResponseEntity<CredentialsRepository.Credentials> register(
			@RequestBody CredentialsRepository.Credentials credentials, @SessionAttribute("challenge") String challenge,
			@AuthenticationPrincipal User user, SessionStatus sessionStatus) throws IOException {
		authenticatorRepository.save(credentials.id(), new UserAuthenticator(credentials,
				Base64.getUrlEncoder().encodeToString(challenge.getBytes()), user.getId()));
		sessionStatus.setComplete();
		return new ResponseEntity<>(credentials, HttpStatus.CREATED);
	}

	@PostMapping("/webauthn-login")
	public String login(@RequestBody CredentialsRepository.CredentialsSign signedResponse, SessionStatus sessionStatus,
			@SessionAttribute("challenge") String challenge, HttpServletRequest request, HttpServletResponse response) {

		// Client properties
		var credentialId = signedResponse.id().getBytes();
		var clientDataJSON = Base64.getUrlDecoder().decode(signedResponse.response().clientDataJSON());
		var authenticatorData = Base64.getUrlDecoder().decode(signedResponse.response().authenticatorData());
		var signature = Base64.getUrlDecoder().decode(signedResponse.response().signature());

		Origin origin = new Origin("http://localhost:8080");
		String rpId = "localhost" /* set rpId */;

		ServerProperty serverProperty = new ServerProperty(origin, rpId,
				new DefaultChallenge(Base64.getUrlEncoder().encodeToString(challenge.getBytes())), null);
		var authenticator = authenticatorRepository.load(signedResponse.id());

		AuthenticationRequest authenticationRequest = new AuthenticationRequest(credentialId, authenticatorData,
				clientDataJSON, signature);
		AuthenticationParameters authenticationParameters = new AuthenticationParameters(serverProperty, authenticator,
				null, false, false);
		AuthenticationData authenticationData;
		try {
			authenticationData = webAuthnManager.parse(authenticationRequest);
			var thing = webAuthnManager.validate(authenticationData, authenticationParameters);
			System.out.println(thing);
		}
		catch (DataConversionException e) {
			// If you would like to handle WebAuthn data structure parse error, please
			// catch DataConversionException
			throw e;
		}
		catch (ValidationException e) {
			// If you would like to handle WebAuthn data validation error, please catch
			// ValidationException
			throw e;
		}

		var user = userRepository.findById(authenticator.getUserId());
		var auth = new UserAuthenticationToken(user.get()); // #yolo

		var newContext = SecurityContextHolder.createEmptyContext();
		newContext.setAuthentication(auth);
		SecurityContextHolder.setContext(newContext);
		securityContextRepository.saveContext(newContext, request, response);

		sessionStatus.setComplete();
		return "redirect:/account";
	}

	@GetMapping(value = "/show-me", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public CredentialsRepository.Credentials showMe() throws IOException {
		return credentialsRepository.loadCredentials();
	}

}
