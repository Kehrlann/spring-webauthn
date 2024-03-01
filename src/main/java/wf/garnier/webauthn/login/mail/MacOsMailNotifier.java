package wf.garnier.webauthn.login.mail;

import java.io.IOException;

import org.springframework.stereotype.Component;

@Component
class MacOsMailNotifier {

	public void notify(String title, String message, String link) {
		try {
			Runtime.getRuntime().exec(new String[] {
					//@formatter:off
					"terminal-notifier",
					"-message", message,
					"-title", title,
					"-open", link,
					"-sound", "bell"
					//@formatter:on
			});
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}
