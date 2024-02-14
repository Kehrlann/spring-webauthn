package wf.garnier.webauthn.user.webauthn;

public record CredentialsVerification(String id, String rawId, Response response) {
	public record Response(String authenticatorData, String clientDataJSON, String signature) {
	}
}
