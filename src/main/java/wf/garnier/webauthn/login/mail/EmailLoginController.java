package wf.garnier.webauthn.login.mail;

import java.util.UUID;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import org.springframework.security.web.util.UrlUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.UriComponentsBuilder;
import wf.garnier.webauthn.login.LoginService;
import wf.garnier.webauthn.models.LoginCode;
import wf.garnier.webauthn.models.LoginCodeRepository;
import wf.garnier.webauthn.models.UserRepository;

@Controller
class EmailLoginController {

	private final UserRepository userRepository;

	private final LoginCodeRepository loginCodeRepository;

	private final MacOsMailNotifier mailNotifier;

	private final LoginService loginService;

	public EmailLoginController(UserRepository userRepository, LoginCodeRepository loginCodeRepository,
                                MacOsMailNotifier mailNotifier, LoginService loginService) {
		this.userRepository = userRepository;
		this.loginCodeRepository = loginCodeRepository;
		this.mailNotifier = mailNotifier;
        this.loginService = loginService;
    }

	@PostMapping("/login-mail")
	public String requestLoginEmail(@RequestParam String email, RedirectAttributes redirectAttributes,
			HttpServletRequest request) {
		var user = userRepository.findUserByEmail(email);
		if (user.isPresent()) {
			var newCode = loginCodeRepository.save(new LoginCode(user.get()));
			var url = UriComponentsBuilder.fromHttpUrl(UrlUtils.buildFullRequestUrl(request))
				.replacePath("/login-mail")
				.queryParam("code", newCode.getId())
				.toUriString();
			mailNotifier.notify("You got Mail", "Login to 🐶 WAN demo. Follow this link to log in: " + url, url);
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

	@GetMapping("/login-mail")
	@Transactional
	public String login(String code, RedirectAttributes redirectAttributes, HttpServletRequest request,
			HttpServletResponse response) {
		var loginCode = loginCodeRepository.findById(UUID.fromString(code));
		if (loginCode.isPresent()) {
			var user = loginCode.get().getUser();
			loginService.login(user);
			loginCodeRepository.delete(loginCode.get());

			return "redirect:/account";
		}
		else {
			redirectAttributes.addFlashAttribute("alert", "Wrong code, try again!");
			return "redirect:/";
		}
	}

}
