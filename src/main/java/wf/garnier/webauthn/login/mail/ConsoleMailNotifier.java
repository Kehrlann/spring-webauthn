package wf.garnier.webauthn.login.mail;

public class ConsoleMailNotifier implements MailNotifier {

	@Override
	public void notify(String title, String message, String link) {
		System.out.println("📥 %s: %s".formatted(title, message));
	}

}
