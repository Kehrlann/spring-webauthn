package wf.garnier.webauthn;

import java.io.IOException;

import org.springframework.stereotype.Component;

@Component
class MacOsMailNotifier {

	public void notify(String message, String link) {
		try {
			Runtime.getRuntime()
				.exec(new String[] { "terminal-notifier", "-message", message, "-title", "Java", "-open", link });
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}
