package wf.garnier.webauthn.user.webauthn;

public record CredentialsRegistration(String id, String rawId, Response response, String type) {
	public record Response(String attestationObject, String clientDataJSON, String authenticatorData,
			String publicKey) {
	}
}
