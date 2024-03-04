package wf.garnier.webauthn;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Base64;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.webauthn4j.WebAuthnManager;
import com.webauthn4j.authenticator.AuthenticatorImpl;
import com.webauthn4j.converter.AttestationObjectConverter;
import com.webauthn4j.converter.util.ObjectConverter;
import com.webauthn4j.data.AuthenticationParameters;
import com.webauthn4j.data.AuthenticationRequest;
import com.webauthn4j.data.RegistrationParameters;
import com.webauthn4j.data.RegistrationRequest;
import com.webauthn4j.data.attestation.statement.NoneAttestationStatement;
import com.webauthn4j.data.client.Origin;
import com.webauthn4j.data.client.challenge.DefaultChallenge;
import com.webauthn4j.server.ServerProperty;
import com.webauthn4j.validator.exception.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import wf.garnier.webauthn.models.webauthn.CredentialsRegistration;
import wf.garnier.webauthn.models.webauthn.CredentialsVerification;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatNoException;

class OfflineTests {

	private final ObjectMapper objectMapper = new ObjectMapper();

	private final WebAuthnManager webAuthnManager = WebAuthnManager.createNonStrictWebAuthnManager();

	private final File registrationFile = Paths.get("./registration.json").toFile();

	private final File authenticationFile = Paths.get("./authentication.json").toFile();

	private final String registrationChallenge = "c9e2fde1-56f1-4e4e-83b9-f99997a460ad";

	private final String authenticationChallenge = "faf484f2-1672-4a82-b781-319855069885";

	public OfflineTests() {
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	}

	@Test
	void onlyAttestation() throws IOException {
		var registration = objectMapper.readValue(registrationFile, CredentialsRegistration.class);
		var attestationBytes = Base64.getUrlDecoder().decode(registration.response().attestationObject());

		// STEP 2: verify!
		var authentication = objectMapper.readValue(authenticationFile, CredentialsVerification.class);
		var authClientDataJsonBytes = Base64.getUrlDecoder().decode(authentication.response().clientDataJSON());
		var signautreBytes = Base64.getUrlDecoder().decode(authentication.response().signature());
		var authenticatorData = Base64.getUrlDecoder().decode(authentication.response().authenticatorData());

		var converter = new AttestationObjectConverter(new ObjectConverter());
		var hydrated = converter.convert(attestationBytes);
		var authenticator = new AuthenticatorImpl(hydrated.getAuthenticatorData().getAttestedCredentialData(),
				null, hydrated.getAuthenticatorData().getSignCount());

		var authenticationRequest = new AuthenticationRequest(authentication.id().getBytes(), authenticatorData,
				authClientDataJsonBytes, signautreBytes);

		//@formatter:off
		assertThatNoException()
			.describedAs("valid signature")
			.isThrownBy(() -> {
				var serverProperty = getServerProperty(authenticationChallenge);
				var authenticationParameters = new AuthenticationParameters(serverProperty, authenticator, false);
				webAuthnManager.validate(authenticationRequest, authenticationParameters);
		});

		assertThatExceptionOfType(ValidationException.class)
			.describedAs("invalid signature because")
			.isThrownBy(() -> {
				var serverProperty = getServerProperty("incorrect-challenge");
				var authenticationParameters = new AuthenticationParameters(serverProperty, authenticator, false);
				webAuthnManager.validate(authenticationRequest, authenticationParameters);
		});
		//@formatter:on
	}

	@Test
	void fullAuthenticatorImplementation() throws IOException {
		var registration = objectMapper.readValue(registrationFile, CredentialsRegistration.class);
		var clientDataJsonBytes = Base64.getUrlDecoder().decode(registration.response().clientDataJSON());
		var attestationBytes = Base64.getUrlDecoder().decode(registration.response().attestationObject());

		var registrationServerProperty = getServerProperty(registrationChallenge);
		var registrationRequest = new RegistrationRequest(attestationBytes, clientDataJsonBytes);
		var registrationParameters = new RegistrationParameters(registrationServerProperty, false);

		// parse-and-validate, throws if wrong
		var registrationData = webAuthnManager.validate(registrationRequest, registrationParameters);
		var authenticator = new AuthenticatorImpl(
				registrationData.getAttestationObject().getAuthenticatorData().getAttestedCredentialData(),
				null,
				registrationData.getAttestationObject().getAuthenticatorData().getSignCount());

		// STEP 2: verify!
		var authentication = objectMapper.readValue(authenticationFile, CredentialsVerification.class);
		var authClientDataJsonBytes = Base64.getUrlDecoder().decode(authentication.response().clientDataJSON());
		var signature = Base64.getUrlDecoder().decode(authentication.response().signature());
		var authenticatorData = Base64.getUrlDecoder().decode(authentication.response().authenticatorData());

		var authenticationRequest = new AuthenticationRequest(authentication.id().getBytes(), authenticatorData,
				authClientDataJsonBytes, signature);

		//@formatter:off
		assertThatNoException()
				.describedAs("valid signature")
				.isThrownBy(() -> {
					var serverProperty = getServerProperty(authenticationChallenge);
					AuthenticationParameters authenticationParameters = new AuthenticationParameters(serverProperty, authenticator, false);
					webAuthnManager.validate(authenticationRequest, authenticationParameters);
				});

		assertThatExceptionOfType(ValidationException.class)
				.describedAs("invalid signature because")
				.isThrownBy(() -> {
					var serverProperty = getServerProperty("incorrect-challenge");
					AuthenticationParameters authenticationParameters = new AuthenticationParameters(serverProperty, authenticator, false);
					webAuthnManager.validate(authenticationRequest, authenticationParameters);
				});
		//@formatter:on
	}

	private ServerProperty getServerProperty(String challenge) {
		var base64Challenge = Base64.getUrlEncoder().encodeToString(challenge.getBytes());
		return new ServerProperty(getOrigin(), getRpId(), new DefaultChallenge(base64Challenge), null);
	}

	private String getRpId() {
		return "localhost";
	}

	private Origin getOrigin() {
		return new Origin("http://localhost:8080");
	}

}
