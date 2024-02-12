package wf.garnier.webauthn;

import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
class UserController {

	private final UserRepository userRepository;

	private final LoginCodeRepository loginCodeRepository;

	public UserController(UserRepository userRepository, LoginCodeRepository loginCodeRepository) {
		this.userRepository = userRepository;
		this.loginCodeRepository = loginCodeRepository;
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

	@GetMapping("/user")
	@ResponseBody
	public Iterable<User> users() {
		return userRepository.findAll();
	}

	@PostMapping("/login-mail")
	public String login(@RequestParam String email, RedirectAttributes redirectAttributes) {
		var user = userRepository.findUserByEmail(email);
		if (user.isPresent()) {
			var newCode = loginCodeRepository.save(new LoginCode(user.get()));
			System.out.println(newCode.getId());
			redirectAttributes.addFlashAttribute("alert",
					"You have requested a login code for [%s]. Check your inbox!".formatted(email));
		}
		else {
			redirectAttributes.addFlashAttribute("alert",
					"You have requested a login code for [%s]. User does not exist, please register first!"
						.formatted(email));
		}
		return "redirect:/";
	}

}
