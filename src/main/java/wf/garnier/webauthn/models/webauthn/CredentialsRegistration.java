package wf.garnier.webauthn.models.webauthn;

public record CredentialsRegistration(String name, AuthenticatorCredentials credentials) {

	public record AuthenticatorCredentials(String id, Response response) {
	}

	public record Response(String attestationObject, String clientDataJSON) {
	}
}
