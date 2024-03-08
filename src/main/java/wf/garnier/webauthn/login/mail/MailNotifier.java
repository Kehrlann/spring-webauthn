package wf.garnier.webauthn.login.mail;

public interface MailNotifier {

	void notify(String title, String message, String link);

}
