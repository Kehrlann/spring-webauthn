package wf.garnier.webauthn;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Base64;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

@Component
public class CredentialsRepository {

	private final ObjectMapper objectMapper = new ObjectMapper();

	private final File credentialsFile = Paths.get("./credentials.json").toFile();
	private final File credentialsResponseFile = Paths.get("./credentials-response.json").toFile();

	public void saveCredentials(Credentials credentials) throws IOException {
		objectMapper.writeValue(credentialsFile, credentials);
	}

	public Credentials loadCredentials() throws IOException {
		return objectMapper.readValue(credentialsFile, Credentials.class);
	}

	public void saveResponse(CredentialsSign credentialsSign) throws IOException {
		objectMapper.writeValue(credentialsResponseFile, credentialsSign);
	}

    public CredentialsSign loadResponse() throws IOException {
		return objectMapper.readValue(credentialsResponseFile, CredentialsSign.class);
    }

    public record Credentials(String id, String rawId, CredentialsCreateResponse response, String type) {
	}

	public record CredentialsCreateResponse(String attestationObject, String clientDataJSON, String authenticatorData,
			String publicKey) {
	}

	public record CredentialsSign(String id, String rawId, CredentialSignResponse response) {
	}

	public record CredentialSignResponse(String authenticatorData, String clientDataJSON, String signature,
			String userHandle) {

		public CredentialSignResponse(String authenticatorData, String clientDataJSON, String signature,
				String userHandle) {
			this.authenticatorData = authenticatorData;
			this.clientDataJSON = clientDataJSON;
			this.signature = signature;
			this.userHandle = new String(Base64.getUrlDecoder().decode(userHandle));
		}
	}

}
