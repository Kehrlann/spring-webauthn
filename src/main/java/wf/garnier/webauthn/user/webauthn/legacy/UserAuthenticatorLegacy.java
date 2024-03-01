package wf.garnier.webauthn.user.webauthn.legacy;

import java.util.Base64;
import java.util.UUID;

import com.webauthn4j.WebAuthnManager;
import com.webauthn4j.authenticator.Authenticator;
import com.webauthn4j.authenticator.AuthenticatorImpl;
import com.webauthn4j.converter.exception.DataConversionException;
import com.webauthn4j.data.RegistrationData;
import com.webauthn4j.data.RegistrationParameters;
import com.webauthn4j.data.RegistrationRequest;
import com.webauthn4j.data.attestation.authenticator.AttestedCredentialData;
import com.webauthn4j.data.attestation.statement.AttestationStatement;
import com.webauthn4j.data.client.Origin;
import com.webauthn4j.data.client.challenge.DefaultChallenge;
import com.webauthn4j.data.extension.authenticator.AuthenticationExtensionsAuthenticatorOutputs;
import com.webauthn4j.data.extension.authenticator.RegistrationExtensionAuthenticatorOutput;
import com.webauthn4j.server.ServerProperty;
import com.webauthn4j.validator.exception.ValidationException;
import wf.garnier.webauthn.user.webauthn.CredentialsRegistration;

public class UserAuthenticatorLegacy implements Authenticator {

	private final Authenticator authenticator;

	private final WebAuthnManager webAuthnManager = WebAuthnManager.createNonStrictWebAuthnManager();

	private final UUID userId;

	public UserAuthenticatorLegacy(CredentialsRegistration credentials, String challenge, UUID userId) {
		this.userId = userId;
		byte[] attestationObject = Base64.getUrlDecoder().decode(credentials.response().attestationObject());
		byte[] clientDataJSON = Base64.getUrlDecoder().decode(credentials.response().clientDataJSON());
		Origin origin = new Origin("http://localhost:8080");
		String rpId = "localhost";
		ServerProperty serverProperty = new ServerProperty(origin, rpId, new DefaultChallenge(challenge), null);
		RegistrationRequest registrationRequest = new RegistrationRequest(attestationObject, clientDataJSON, null,
				null);
		RegistrationParameters registrationParameters = new RegistrationParameters(serverProperty, false, true);
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
		authenticator = new AuthenticatorImpl(
				registrationData.getAttestationObject().getAuthenticatorData().getAttestedCredentialData(),
				registrationData.getAttestationObject().getAttestationStatement(),
				registrationData.getAttestationObject().getAuthenticatorData().getSignCount()
		);

	}

	@Override
	public AttestedCredentialData getAttestedCredentialData() {
		return authenticator.getAttestedCredentialData();
	}

	@Override
	public AttestationStatement getAttestationStatement() {
		return authenticator.getAttestationStatement();
	}

	@Override
	public long getCounter() {
		return authenticator.getCounter();
	}

	@Override
	public void setCounter(long value) {
		authenticator.setCounter(value);
	}

	@Override
	public AuthenticationExtensionsAuthenticatorOutputs<RegistrationExtensionAuthenticatorOutput> getAuthenticatorExtensions() {
		return authenticator.getAuthenticatorExtensions();
	}

	public UUID getUserId() {
		return userId;
	}

}
