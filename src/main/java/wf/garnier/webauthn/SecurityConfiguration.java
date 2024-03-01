package wf.garnier.webauthn;

import jakarta.servlet.DispatcherType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.ui.DefaultLogoutPageGeneratingFilter;

@Configuration
@EnableWebSecurity
class SecurityConfiguration {

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		return http.authorizeHttpRequests(authorize -> {
			authorize.dispatcherTypeMatchers(DispatcherType.ERROR).permitAll();
			authorize.requestMatchers("/").permitAll();
			authorize.requestMatchers("/user").permitAll();
			authorize.requestMatchers("/user/register").permitAll();
			authorize.requestMatchers("/passkey/login").permitAll();
			authorize.requestMatchers("/login-mail").permitAll(); // TODO: consistent
			authorize.requestMatchers("/favicon.ico").permitAll();
			authorize.anyRequest().authenticated();
		})
			.addFilter(new DefaultLogoutPageGeneratingFilter())
			.csrf(csrf -> csrf.ignoringRequestMatchers("/logout"))
			.logout(logout -> logout.logoutSuccessUrl("/"))
			.build();
	}

}
