package wf.garnier.webauthn.models.webauthn;

public record CredentialsRegistration(String id, Response response, String name) {

	// Note: authenticatorData + publicKey unused? They are in the attestationObject
	public record Response(String attestationObject, String clientDataJSON, String authenticatorData,
			String publicKey) {
	}
}
