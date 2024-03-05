package wf.garnier.webauthn.login.webauthn;

import jakarta.validation.constraints.NotNull;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import wf.garnier.webauthn.login.LoginService;
import wf.garnier.webauthn.models.User;
import wf.garnier.webauthn.models.webauthn.AuthenticatorService;
import wf.garnier.webauthn.models.webauthn.CredentialsRegistration;
import wf.garnier.webauthn.models.webauthn.CredentialsVerification;

@Controller
class WebAuthnController {

	private final AuthenticatorService authenticatorService;

	private final LoginService loginService;

	public WebAuthnController(AuthenticatorService authenticatorService, LoginService loginService) {
		this.authenticatorService = authenticatorService;
		this.loginService = loginService;
	}

	@PostMapping("/passkey/register")
	public String register(@RequestBody CredentialsRegistration credentials) {
		return "redirect:/account";
	}

	@PostMapping("/passkey/login")
	public String authenticate(@RequestBody CredentialsVerification credentials) {
		return "redirect:/account";
	}

	@PostMapping("/passkey/delete")
	public String login(@NotNull @RequestParam("credential-id") String credentialId, @AuthenticationPrincipal User user,
			RedirectAttributes redirectAttributes) {
		System.out.println("DELETING " + credentialId);
		if (authenticatorService.deleteCredential(user, credentialId)) {
			redirectAttributes.addFlashAttribute("alert", "Passkey deleted.");
		}
		return "redirect:/account";
	}

}
