package wf.garnier.webauthn.models.webauthn;

public record CredentialsRegistration(String id, Response response, String name) {

	public record Response(String attestationObject, String clientDataJSON) {
	}
}
