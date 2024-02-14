package wf.garnier.webauthn.user.webauthn;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

@Component
public class CredentialsRepository {

	private final ObjectMapper objectMapper = new ObjectMapper();

	private final File credentialsFile = Paths.get("./credentials.json").toFile();

	private final File credentialsResponseFile = Paths.get("./credentials-response.json").toFile();

	public void saveCredentials(CredentialsRegistration credentials) throws IOException {
		objectMapper.writeValue(credentialsFile, credentials);
	}

	public CredentialsRegistration loadCredentials() throws IOException {
		return objectMapper.readValue(credentialsFile, CredentialsRegistration.class);
	}

	public void saveResponse(CredentialsVerification credentialsSign) throws IOException {
		objectMapper.writeValue(credentialsResponseFile, credentialsSign);
	}

	public CredentialsVerification loadResponse() throws IOException {
		return objectMapper.readValue(credentialsResponseFile, CredentialsVerification.class);
	}

}
