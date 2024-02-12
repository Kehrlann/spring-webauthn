package wf.garnier.webauthn;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Set;
import java.util.UUID;

import com.webauthn4j.WebAuthnManager;
import com.webauthn4j.authenticator.Authenticator;
import com.webauthn4j.authenticator.AuthenticatorImpl;
import com.webauthn4j.converter.exception.DataConversionException;
import com.webauthn4j.data.RegistrationData;
import com.webauthn4j.data.RegistrationParameters;
import com.webauthn4j.data.RegistrationRequest;
import com.webauthn4j.data.client.Origin;
import com.webauthn4j.data.client.challenge.Challenge;
import com.webauthn4j.server.ServerProperty;
import com.webauthn4j.validator.exception.ValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;

@Controller
@SessionAttributes("challenge")
class AppController {

	private final CredentialsRepository credentialsRepository;

	public AppController(CredentialsRepository credentialsRepository) {
		this.credentialsRepository = credentialsRepository;
	}

	@GetMapping
	public String landingPage(Model model) {
		model.addAttribute("challenge", UUID.randomUUID().toString());
		return "landing-page";
	}

	@GetMapping("/account")
	public String accountPage(Authentication authentication, Model model) {
		model.addAttribute("username", authentication.getName());
		model.addAttribute("challenge", UUID.randomUUID().toString());
		return "account-page";
	}

	@PostMapping("/register")
	public ResponseEntity<CredentialsRepository.Credentials> register(
			@RequestBody CredentialsRepository.Credentials credentials, SessionStatus sessionStatus)
			throws IOException {
		// TODO: verify credentials
		credentialsRepository.saveCredentials(credentials);
		sessionStatus.setComplete();
		return new ResponseEntity<>(credentials, HttpStatus.CREATED);
	}

	@PostMapping("/webauthn-login")
	public ResponseEntity<CredentialsRepository.CredentialsSign> login(
			@RequestBody CredentialsRepository.CredentialsSign signedResponse, SessionStatus sessionStatus,
			@SessionAttribute("challenge") String challenge) throws IOException, NoSuchAlgorithmException {
		credentialsRepository.saveResponse(signedResponse);

		var webAuthnManager = WebAuthnManager.createNonStrictWebAuthnManager();
		// Client properties
		byte[] attestationObject = null /* set attestationObject */;
		byte[] clientDataJSON = null /* set clientDataJSON */;
		String clientExtensionJSON = null; /* set clientExtensionJSON */
		Set<String> transports = null /* set transports */;

		// Server properties
		String rpId = null /* set rpId */;
		Origin origin = null;
		Challenge c = null /* set c */;
		byte[] tokenBindingId = null /* set tokenBindingId */;
		ServerProperty serverProperty = new ServerProperty(origin, rpId, c, tokenBindingId);

		// expectations
		boolean userVerificationRequired = false;
		boolean userPresenceRequired = true;

		RegistrationRequest registrationRequest = new RegistrationRequest(attestationObject, clientDataJSON,
				clientExtensionJSON, transports);
		RegistrationParameters registrationParameters = new RegistrationParameters(serverProperty,
				userVerificationRequired, userPresenceRequired);

		RegistrationData registrationData;
		try {
			registrationData = webAuthnManager.parse(registrationRequest);
		}
		catch (DataConversionException e) {
			// If you would like to handle WebAuthn data structure parse error, please
			// catch DataConversionException
			throw e;
		}
		try {
			webAuthnManager.validate(registrationData, registrationParameters);
		}
		catch (ValidationException e) {
			// If you would like to handle WebAuthn data validation error, please catch
			// ValidationException
			throw e;
		}

		// please persist Authenticator object, which will be used in the authentication
		// process.
		Authenticator authenticator = new AuthenticatorImpl( // You may create your own
																// Authenticator
																// implementation to save
																// friendly authenticator
																// name
				registrationData.getAttestationObject().getAuthenticatorData().getAttestedCredentialData(),
				registrationData.getAttestationObject().getAttestationStatement(),
				registrationData.getAttestationObject().getAuthenticatorData().getSignCount());
		var storedCredentials = credentialsRepository.loadCredentials();

		var decodeFromBytes = Base64.getUrlDecoder().decode(storedCredentials.response().attestationObject());
		System.out.println(signedResponse);
		sessionStatus.setComplete();
		return new ResponseEntity<>(signedResponse, HttpStatus.OK);
	}

	@GetMapping(value = "/show-me", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public CredentialsRepository.Credentials showMe() throws IOException {
		return credentialsRepository.loadCredentials();
	}

	public static class CBORThingy {

		public String fmt;

		public Object attStmt;

		public byte[] authData;

		public CBORThingy() {
		}

		public String getFmt() {
			return fmt;
		}

		public void setFmt(String fmt) {
			this.fmt = fmt;
		}

		public Object getAttStmt() {
			return attStmt;
		}

		public void setAttStmt(Object attStmt) {
			this.attStmt = attStmt;
		}

		public byte[] getAuthData() {
			return authData;
		}

		public void setAuthData(byte[] authData) {
			this.authData = authData;
		}

	}

}
