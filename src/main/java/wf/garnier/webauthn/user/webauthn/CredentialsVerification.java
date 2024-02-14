package wf.garnier.webauthn.user.webauthn;

import java.util.Base64;

public record CredentialsVerification(String id, String rawId, Response response) {
	public record Response(String authenticatorData, String clientDataJSON, String signature, String userHandle) {

		public Response(String authenticatorData, String clientDataJSON, String signature, String userHandle) {
			this.authenticatorData = authenticatorData;
			this.clientDataJSON = clientDataJSON;
			this.signature = signature;
			this.userHandle = new String(Base64.getUrlDecoder().decode(userHandle));
		}
	}
}
