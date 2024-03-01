package wf.garnier.webauthn.login.webauthn;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.bind.support.SessionStatus;
import wf.garnier.webauthn.models.User;
import wf.garnier.webauthn.models.UserAuthenticationToken;
import wf.garnier.webauthn.models.webauthn.AuthenticatorService;
import wf.garnier.webauthn.models.webauthn.CredentialsRegistration;
import wf.garnier.webauthn.models.webauthn.CredentialsVerification;

@Controller
class WebAuthnController {

	private final AuthenticatorService authenticatorService;

	private final SecurityContextRepository securityContextRepository = new HttpSessionSecurityContextRepository();

	public WebAuthnController(AuthenticatorService authenticatorService) {
		this.authenticatorService = authenticatorService;
	}

	@PostMapping("/passkey/register")
	public ResponseEntity<CredentialsRegistration> register(@RequestBody CredentialsRegistration credentials,
			@SessionAttribute("challenge") String challenge, @AuthenticationPrincipal User user,
			SessionStatus sessionStatus) {
		authenticatorService.saveCredentials(credentials, challenge, user, user + "-" + credentials.id());

		sessionStatus.setComplete();
		// TODO: redirect
		// TODO: flash message
		return new ResponseEntity<>(credentials, HttpStatus.CREATED);
	}

	@PostMapping("/passkey/login")
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
