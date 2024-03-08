package wf.garnier.webauthn;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import wf.garnier.webauthn.login.mail.ConsoleMailNotifier;
import wf.garnier.webauthn.login.mail.MacOsMailNotifier;
import wf.garnier.webauthn.login.mail.MailNotifier;

@Configuration
class MailNotifierConfiguration {

	@Bean
	public MailNotifier mailNotifier() {
		if (MacOsMailNotifier.isSupported()) {
			return new MacOsMailNotifier();
		}
		else {
			return new ConsoleMailNotifier();
		}
	}

}
