package wf.garnier.webauthn.registration;

import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import wf.garnier.webauthn.models.User;
import wf.garnier.webauthn.models.UserRepository;

@Controller
class UserRegistrationController {

	private final UserRepository userRepository;

	public UserRegistrationController(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@GetMapping("/user/register")
	public String registerPage() {
		return "user/register";
	}

	@PostMapping("/user/register")
	public String registerAction(@Validated User user, RedirectAttributes redirectAttributes) {
		userRepository.save(user);
		redirectAttributes.addFlashAttribute("alert", "You have been registered");
		return "redirect:/";
	}

}
