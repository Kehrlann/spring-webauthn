package wf.garnier.webauthn;

import java.util.UUID;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.web.util.UrlUtils;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.UriComponentsBuilder;

@Controller
class UserController {

	private final UserRepository userRepository;

	private final LoginCodeRepository loginCodeRepository;

	private final MacOsMailNotifier mailNotifier;

	private final SecurityContextRepository securityContextRepository = new HttpSessionSecurityContextRepository();

	public UserController(UserRepository userRepository, LoginCodeRepository loginCodeRepository,
			MacOsMailNotifier mailNotifier) {
		this.userRepository = userRepository;
		this.loginCodeRepository = loginCodeRepository;
		this.mailNotifier = mailNotifier;
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
	public String requestCode(@RequestParam String email, RedirectAttributes redirectAttributes,
			HttpServletRequest request) {
		var user = userRepository.findUserByEmail(email);
		if (user.isPresent()) {
			var newCode = loginCodeRepository.save(new LoginCode(user.get()));
			var url = UriComponentsBuilder.fromHttpUrl(UrlUtils.buildFullRequestUrl(request))
				.replacePath("/login-mail")
				.queryParam("code", newCode.getId())
				.toUriString();
			mailNotifier.notify("You got Mail", "Login to 🐶 WAN demo. Follow this link to log in: " + url, url);
			System.out.println("🔗🔗🔗🔗🔗🔗🔗🔗🔗🔗 " + url);
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
			var auth = new AppAuthenticationToken(user);

			var newContext = SecurityContextHolder.createEmptyContext();
			newContext.setAuthentication(auth);
			SecurityContextHolder.setContext(newContext);
			securityContextRepository.saveContext(newContext, request, response);

			loginCodeRepository.delete(loginCode.get());

			return "redirect:/account";
		}
		else {
			redirectAttributes.addFlashAttribute("alert", "Wrong code, try again!");
			return "redirect:/";
		}
	}

}
