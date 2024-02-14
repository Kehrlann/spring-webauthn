package wf.garnier.webauthn;

import java.io.IOException;
import java.util.Base64;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.webauthn4j.WebAuthnManager;
import com.webauthn4j.authenticator.Authenticator;
import com.webauthn4j.authenticator.AuthenticatorImpl;
import com.webauthn4j.converter.exception.DataConversionException;
import com.webauthn4j.data.AuthenticationData;
import com.webauthn4j.data.AuthenticationParameters;
import com.webauthn4j.data.AuthenticationRequest;
import com.webauthn4j.data.RegistrationData;
import com.webauthn4j.data.RegistrationParameters;
import com.webauthn4j.data.RegistrationRequest;
import com.webauthn4j.data.client.Origin;
import com.webauthn4j.data.client.challenge.Challenge;
import com.webauthn4j.data.client.challenge.DefaultChallenge;
import com.webauthn4j.server.ServerProperty;
import com.webauthn4j.validator.exception.ValidationException;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import wf.garnier.webauthn.user.webauthn.CredentialsRepository;

import static org.assertj.core.api.Assertions.assertThat;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class OfflineTests {

	private final WebAuthnManager webAuthnManager = WebAuthnManager.createNonStrictWebAuthnManager();

	private final CredentialsRepository credentialsRepository = new CredentialsRepository();

	private final ObjectMapper objectMapper = new ObjectMapper();

	private static Authenticator authenticator;

	@Test
	@Order(1)
	void register() throws IOException {
		var savedCreds = credentialsRepository.loadCredentials();
		var savedCredsResponse = savedCreds.response();

		var decodedClientDataJSON = Base64.getUrlDecoder().decode(savedCreds.response().clientDataJSON());
		Map<String, String> clientDataJson = objectMapper.readValue(decodedClientDataJSON, Map.class);
		// Client properties
		byte[] attestationObject = Base64.getUrlDecoder()
			.decode(savedCredsResponse.attestationObject()) /* set attestationObject */;
		byte[] clientDataJSON = decodedClientDataJSON /* set clientDataJSON */;
		String clientExtensionJSON = null; /* set clientExtensionJSON */
		Set<String> transports = null /* set transports */;

		// Server properties
		Origin origin = new Origin("http://localhost:8080");
		String rpId = "localhost" /* set rpId */;
		Challenge challenge = new DefaultChallenge(
				clientDataJson.get("challenge")) /* set challenge */;
		byte[] tokenBindingId = null /* set tokenBindingId */;
		ServerProperty serverProperty = new ServerProperty(origin, rpId, challenge, tokenBindingId);

		// expectations
		boolean userVerificationRequired = false;
		boolean userPresenceRequired = true;

		RegistrationRequest registrationRequest = new RegistrationRequest(attestationObject, clientDataJSON,
				clientExtensionJSON, transports);
		RegistrationParameters registrationParameters = new RegistrationParameters(serverProperty,
				userVerificationRequired, userPresenceRequired);
		RegistrationData registrationData;
		try {
			registrationData = webAuthnManager.parse(registrationRequest);
		}
		catch (DataConversionException e) {
			// If you would like to handle WebAuthn data structure parse error, please
			// catch DataConversionException
			throw e;
		}
		try {
			webAuthnManager.validate(registrationData, registrationParameters);
		}
		catch (ValidationException e) {
			// If you would like to handle WebAuthn data validation error, please catch
			// ValidationException
			throw e;
		}

		// please persist Authenticator object, which will be used in the authentication
		// process.
		authenticator = new AuthenticatorImpl(
				registrationData.getAttestationObject().getAuthenticatorData().getAttestedCredentialData(),
				registrationData.getAttestationObject().getAttestationStatement(),
				registrationData.getAttestationObject().getAuthenticatorData().getSignCount());
	}

	@Test
	@Order(2)
	void verify() throws IOException {
		var credsResponse = credentialsRepository.loadResponse();
		var clientDataJSONbytes = Base64.getUrlDecoder().decode(credsResponse.response().clientDataJSON());
		Map<String, String> clientDataJson = objectMapper.readValue(clientDataJSONbytes, Map.class);

		// Client properties
		byte[] credentialId = credsResponse.id().getBytes() /* set credentialId */;
		byte[] userHandle = credsResponse.response()
			.userHandle()
			.getBytes() /* set userHandle */;
		byte[] authenticatorData = Base64.getUrlDecoder()
			.decode(credsResponse.response()
				.authenticatorData()) /* set authenticatorData */;
		byte[] clientDataJSON = clientDataJSONbytes/* set clientDataJSON */;
		String clientExtensionJSON = null /* set clientExtensionJSON */;
		byte[] signature = Base64.getUrlDecoder()
			.decode(credsResponse.response().signature()) /* set signature */;

		// Server properties
		Origin origin = new Origin("http://localhost:8080");
		String rpId = "localhost" /* set rpId */;
		Challenge challenge = new DefaultChallenge(
				clientDataJson.get("challenge")) /* set challenge */;
		ServerProperty serverProperty = new ServerProperty(origin, rpId, challenge, null);

		AuthenticationRequest authenticationRequest = new AuthenticationRequest(credentialId, authenticatorData,
				clientDataJSON, signature);
		AuthenticationParameters authenticationParameters = new AuthenticationParameters(serverProperty, authenticator,
				null, false, false);

		AuthenticationData authenticationData;
		try {
			authenticationData = webAuthnManager.parse(authenticationRequest);
			assertThat(authenticatorData).isNotNull();
		}
		catch (DataConversionException e) {
			// If you would like to handle WebAuthn data structure parse error, please
			// catch DataConversionException
			throw e;
		}
		try {
			var thing = webAuthnManager.validate(authenticationData, authenticationParameters);

			// TODO: sign multiple times :arghl:
			assertThat(thing).isNotNull();
		}
		catch (ValidationException e) {
			// If you would like to handle WebAuthn data validation error, please catch
			// ValidationException
			throw e;
		}

		// ?!
		// please update the counter of the authenticator record
		// updateCounter(
		// authenticationData.getCredentialId(),
		// authenticationData.getAuthenticatorData().getSignCount()
		// );

		assertThat(authenticator).isNotNull();
	}

}
