package wf.garnier.webauthn;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import wf.garnier.webauthn.models.User;
import wf.garnier.webauthn.models.webauthn.DemoCredentialRecord;
import wf.garnier.webauthn.models.webauthn.DemoCredentialRecordRepository;

@Controller
class AppController {

	private final DemoCredentialRecordRepository credsRepo;

    AppController(DemoCredentialRecordRepository credsRepo) {
        this.credsRepo = credsRepo;
    }

    @GetMapping
	public String landingPage() {
		return "landing-page";
	}

	@GetMapping("/account")
	public String accountPage(@AuthenticationPrincipal User user, Model model) {
		var authenticators = credsRepo.findByUsername(user.getUsername())
			.stream()
			.map(DemoCredentialRecord::getLabel)
			.toList();
		model.addAttribute("username", user.getUsername());
		model.addAttribute("email", user.getEmail());
		model.addAttribute("authenticators", authenticators); // TODO

		return "account-page";
	}

}
