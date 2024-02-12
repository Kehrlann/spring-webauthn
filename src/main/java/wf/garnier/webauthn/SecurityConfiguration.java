package wf.garnier.webauthn;

import jakarta.servlet.DispatcherType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
class SecurityConfiguration {

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		return http.authorizeHttpRequests(authorize -> {
			authorize.dispatcherTypeMatchers(DispatcherType.ERROR).permitAll();
			authorize.requestMatchers("/").permitAll();
			authorize.requestMatchers("/user").permitAll();
			authorize.requestMatchers("/user/register").permitAll();
			authorize.requestMatchers("/webauthn-login").permitAll();
			authorize.requestMatchers("/login-mail").permitAll();
			authorize.requestMatchers("/favicon.ico").permitAll();
			authorize.anyRequest().authenticated();
		})
			.formLogin(login -> login.defaultSuccessUrl("/account"))
			.logout(logout -> logout.logoutSuccessUrl("/"))
			.build();
	}

	@Bean
	public UserDetailsService userDetailsService() {
		return new InMemoryUserDetailsManager(
				User.withDefaultPasswordEncoder().username("daniel").password("password").roles("user").build());
	}

}
