package wf.garnier.webauthn;

import jakarta.servlet.DispatcherType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.WebauthnConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
class SecurityConfiguration {

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		return http.authorizeHttpRequests(authorize -> {
			authorize.dispatcherTypeMatchers(DispatcherType.ERROR).permitAll();
			authorize.requestMatchers("/").permitAll();
			authorize.requestMatchers("/user/register").permitAll();
			authorize.requestMatchers("/landing").permitAll();
			authorize.requestMatchers("/login-mail").permitAll();
			authorize.requestMatchers("/style.css").permitAll();
			authorize.requestMatchers("/favicon.ico").permitAll();
			authorize.anyRequest().authenticated();
		})
			.csrf(Customizer.withDefaults())
			.formLogin(Customizer.withDefaults()) // TODO: eventually this should go away
			.logout(logout -> logout.logoutSuccessUrl("/"))
			.with(new WebauthnConfigurer<>(),
					(passkeys) -> passkeys.rpName("WebAuthN demo")
						.rpId("localhost")
						.allowedOrigins("http://localhost:8080"))
			// .exceptionHandling(
			// exception -> exception.authenticationEntryPoint(new
			// LoginUrlAuthenticationEntryPoint("/")))
			.build();
	}

}
