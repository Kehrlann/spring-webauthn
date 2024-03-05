package wf.garnier.webauthn;

import java.util.UUID;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import wf.garnier.webauthn.models.User;
import wf.garnier.webauthn.models.webauthn.UserAuthenticatorRepository;

@Controller
class AppController {

	private final UserAuthenticatorRepository authenticatorRepository;

	public AppController(UserAuthenticatorRepository authenticatorRepository) {
		this.authenticatorRepository = authenticatorRepository;
	}

	@GetMapping
	public String landingPage(Model model) {
		return "landing-page";
	}

	@GetMapping("/account")
	public String accountPage(@AuthenticationPrincipal User user, Model model) {
		var authenticators = authenticatorRepository.findUserAuthenticatorByUser(user);
		model.addAttribute("username", user.getUsername());
		model.addAttribute("email", user.getEmail());
		model.addAttribute("authenticators", authenticators);

		return "account-page";
	}

}
