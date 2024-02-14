package wf.garnier.webauthn;

import java.util.List;
import java.util.UUID;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
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
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import wf.garnier.webauthn.user.User;
import wf.garnier.webauthn.user.UserAuthenticationToken;
import wf.garnier.webauthn.user.webauthn.AuthenticatorService;
import wf.garnier.webauthn.user.webauthn.CredentialsRegistration;
import wf.garnier.webauthn.user.webauthn.CredentialsVerification;
import wf.garnier.webauthn.user.webauthn.UserAuthenticator;
import wf.garnier.webauthn.user.webauthn.UserAuthenticatorRepository;

@Controller
@SessionAttributes("challenge")
class AppController {

	private final AuthenticatorService authenticatorService;

	private final UserAuthenticatorRepository authenticatorRepository;

	private final SecurityContextRepository securityContextRepository = new HttpSessionSecurityContextRepository();

	public AppController(AuthenticatorService authenticatorService,
			UserAuthenticatorRepository authenticatorRepository) {
		this.authenticatorService = authenticatorService;
		this.authenticatorRepository = authenticatorRepository;
	}

	@GetMapping
	public String landingPage(Model model) {
		model.addAttribute("challenge", UUID.randomUUID().toString());
		return "landing-page";
	}

	@GetMapping("/account")
	public String accountPage(@AuthenticationPrincipal User user, Model model) {
		var authenticators = authenticatorRepository.findUserAuthenticatorByUser(user).stream().map(UserAuthenticator::getCredentialsName).toList();
		model.addAttribute("username", user.getUsername());
		model.addAttribute("challenge", UUID.randomUUID().toString());
		model.addAttribute("authenticators", authenticators);

		return "account-page";
	}

	@PostMapping("/register")
	public ResponseEntity<CredentialsRegistration> register(@RequestBody CredentialsRegistration credentials,
			@SessionAttribute("challenge") String challenge, @AuthenticationPrincipal User user,
			SessionStatus sessionStatus) {
		authenticatorService.saveCredentials(credentials, challenge, user, user + "-" + credentials.id());

		sessionStatus.setComplete();
		return new ResponseEntity<>(credentials, HttpStatus.CREATED);
	}

	@PostMapping("/webauthn-login")
	public String login(@RequestBody CredentialsVerification verification, SessionStatus sessionStatus,
			@SessionAttribute("challenge") String challenge, HttpServletRequest request, HttpServletResponse response) {
		var user = authenticatorService.authenticate(verification, challenge);

		var auth = new UserAuthenticationToken(user);
		var newContext = SecurityContextHolder.createEmptyContext();
		newContext.setAuthentication(auth);
		SecurityContextHolder.setContext(newContext);
		securityContextRepository.saveContext(newContext, request, response);

		sessionStatus.setComplete();
		return "redirect:/account";
	}

}
