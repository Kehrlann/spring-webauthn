package wf.garnier.webauthn.login.webauthn;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
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
	public String register(@RequestBody CredentialsRegistration credentials, @AuthenticationPrincipal User user,
			RedirectAttributes redirectAttributes) {
		authenticatorService.saveCredentials(credentials, user);

		// TODO: this is not used because we use fetch and we get a double-call on /account :|
		redirectAttributes.addFlashAttribute("alert", "You have registered a new passkey!");
		return "redirect:/account";
	}

	@PostMapping("/passkey/login")
	public String login(@RequestBody CredentialsVerification verification, SessionStatus sessionStatus,
			@SessionAttribute("challenge") String challenge, HttpServletRequest request, HttpServletResponse response) {
		// TODO: this could be in a service layer?
		var user = authenticatorService.authenticate(verification, challenge);
		var auth = new UserAuthenticationToken(user);
		var newContext = SecurityContextHolder.createEmptyContext();
		newContext.setAuthentication(auth);
		SecurityContextHolder.setContext(newContext);
		securityContextRepository.saveContext(newContext, request, response);

		sessionStatus.setComplete();
		return "redirect:/account";
	}

	@PostMapping("/passkey/delete")
	public String login(@NotNull @RequestParam("credential-id") String credentialId, @AuthenticationPrincipal User user,
			RedirectAttributes redirectAttributes) {
		System.out.println("DELETING " + credentialId);
		if (authenticatorService.deleteCredential(user, credentialId)) {
			redirectAttributes.addFlashAttribute("alert", "Passkey deleted.");
		}
		else {
			// TODO: deleting something that is not yours?
		}
		return "redirect:/account";
	}

}
