package wf.garnier.webauthn;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import wf.garnier.webauthn.models.User;

@Controller
class AppController {

	@GetMapping("landing")
	public String landingPage() {
		return "landing-page";
	}

	@GetMapping("/account")
	public String accountPage(@AuthenticationPrincipal User user, Model model) {
		model.addAttribute("username", user.getUsername());
		model.addAttribute("email", user.getEmail());
		model.addAttribute("authenticators", List.of()); // TODO

		return "account-page";
	}

}
