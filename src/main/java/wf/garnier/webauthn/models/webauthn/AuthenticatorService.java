package wf.garnier.webauthn.models.webauthn;

import java.util.Base64;

import com.webauthn4j.WebAuthnManager;
import com.webauthn4j.authenticator.AuthenticatorImpl;
import com.webauthn4j.converter.AttestationObjectConverter;
import com.webauthn4j.converter.util.ObjectConverter;
import com.webauthn4j.data.RegistrationParameters;
import com.webauthn4j.data.RegistrationRequest;
import com.webauthn4j.data.client.Origin;
import com.webauthn4j.data.client.challenge.DefaultChallenge;
import com.webauthn4j.server.ServerProperty;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;
import wf.garnier.webauthn.models.User;

@Component
public class AuthenticatorService {

	private final UserAuthenticatorRepository repository;

	private final WebAuthnManager webAuthnManager = WebAuthnManager.createNonStrictWebAuthnManager();

	public AuthenticatorService(UserAuthenticatorRepository repository) {
		this.repository = repository;
	}

	@Transactional
	public boolean deleteCredential(User user, String credentialId) {
		return repository.deleteByIdAndUser(credentialId, user) > 0;
	}

	/**
	 * 🧙🪄✨ HERE BE MAGIC. Or at least pre-coded data munching.
	 *
	 * Transform authenticator data that's saved as {@code attestationObject} bytes into a
	 * {@link AuthenticatorImpl} object.
	 */
	private static AuthenticatorImpl toAuthenticatorObject(byte[] attestation) {
		var converter = new AttestationObjectConverter(new ObjectConverter());
		var attestationObject = converter.convert(attestation);
		return new AuthenticatorImpl(attestationObject.getAuthenticatorData().getAttestedCredentialData(), null,
				attestationObject.getAuthenticatorData().getSignCount());
	}

	/**
	 * 🧙🪄✨ HERE BE MAGIC. Or at least pre-coded data munching.
	 *
	 * Transform data the web server knows about the incoming request (origin, domain name
	 * and challenge) into a {@link ServerProperty} object.
	 */
	private static ServerProperty toServerProperty(String origin, String challenge) {
		var b64Challenge = Base64.getUrlEncoder().encodeToString(challenge.getBytes());
		var rpId = UriComponentsBuilder.fromHttpUrl(origin).build().getHost();
		return new ServerProperty(new Origin(origin), rpId, new DefaultChallenge(b64Challenge), null);
	}

	public void save(User user, CredentialsRegistration credentials, String challenge) {
		RegistrationParameters registrationParameters = new RegistrationParameters(
				toServerProperty("http://localhost:8080", challenge), false);
		var registrationRequest = new RegistrationRequest(
				Base64.getUrlDecoder().decode(credentials.credentials().response().attestationObject()),
				Base64.getUrlDecoder().decode(credentials.credentials().response().clientDataJSON()));
		webAuthnManager.validate(registrationRequest, registrationParameters);

		var userAuthenticator = new UserAuthenticator(
				credentials.credentials().id(),
				user,
				credentials.name(),
				Base64.getUrlDecoder().decode(credentials.credentials().response().attestationObject())
		);
		repository.save(userAuthenticator);
	}

}
