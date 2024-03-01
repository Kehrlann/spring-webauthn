package wf.garnier.webauthn.models.webauthn;

import java.util.Base64;

import com.webauthn4j.WebAuthnManager;
import com.webauthn4j.authenticator.AuthenticatorImpl;
import com.webauthn4j.converter.AttestedCredentialDataConverter;
import com.webauthn4j.converter.exception.DataConversionException;
import com.webauthn4j.converter.util.ObjectConverter;
import com.webauthn4j.data.AuthenticationData;
import com.webauthn4j.data.AuthenticationParameters;
import com.webauthn4j.data.AuthenticationRequest;
import com.webauthn4j.data.RegistrationData;
import com.webauthn4j.data.RegistrationParameters;
import com.webauthn4j.data.RegistrationRequest;
import com.webauthn4j.data.attestation.statement.NoneAttestationStatement;
import com.webauthn4j.data.client.Origin;
import com.webauthn4j.data.client.challenge.DefaultChallenge;
import com.webauthn4j.server.ServerProperty;
import com.webauthn4j.validator.exception.ValidationException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;
import wf.garnier.webauthn.models.User;

@Component
public class AuthenticatorService {

	private final UserAuthenticatorRepository repository;

	private final WebAuthnManager webAuthnManager = WebAuthnManager.createNonStrictWebAuthnManager();

	private final HttpServletRequest request;

	AttestedCredentialDataConverter attestedCredentialDataConverter = new AttestedCredentialDataConverter(
			new ObjectConverter());

	public AuthenticatorService(UserAuthenticatorRepository repository, HttpServletRequest request) {
		this.repository = repository;
		this.request = request;
	}

	public void saveCredentials(CredentialsRegistration registration, String challenge, User user) {
		var attestationObject = Base64.getUrlDecoder().decode(registration.response().attestationObject());
		var clientDataJSON = Base64.getUrlDecoder().decode(registration.response().clientDataJSON());
		var base64Challenge = Base64.getUrlEncoder().encodeToString(challenge.getBytes());

		ServerProperty serverProperty = new ServerProperty(getOrigin(), getRpId(),
				new DefaultChallenge(base64Challenge), null);
		RegistrationRequest registrationRequest = new RegistrationRequest(attestationObject, clientDataJSON, null,
				null);
		RegistrationParameters registrationParameters = new RegistrationParameters(serverProperty, false, true);
		RegistrationData registrationData;
		try {
			registrationData = webAuthnManager.parse(registrationRequest);
			webAuthnManager.validate(registrationData, registrationParameters);
		}
		catch (DataConversionException e) {
			// If you would like to handle WebAuthn data structure parse error, please
			// catch DataConversionException
			// TODO: better
			throw e;
		}
		catch (ValidationException e) {
			// If you would like to handle WebAuthn data validation error, please catch
			// ValidationException
			// TODO: better
			throw e;
		}
		var authenticator = new AuthenticatorImpl(
				registrationData.getAttestationObject().getAuthenticatorData().getAttestedCredentialData(),
				registrationData.getAttestationObject().getAttestationStatement(),
				registrationData.getAttestationObject().getAuthenticatorData().getSignCount());

		var attestedCredentialsData = attestedCredentialDataConverter
			.convert(authenticator.getAttestedCredentialData());
		// TODO: attestation statement, is it always NoneAttestationStatement?
		// https://webauthn4j.github.io/webauthn4j/en/#attestationstatement
		var serializedAuthenticator = new UserAuthenticator(registration.id(), user, registration.name(),
				attestedCredentialsData, null, authenticator.getCounter());
		repository.save(serializedAuthenticator);
	}

	public User authenticate(CredentialsVerification verification, String challenge) throws AuthenticationException {
		var userAuthenticator = repository.findById(verification.id())
			.orElseThrow(() -> new BadCredentialsException("Unknown credentials"));
		var authenticator = new AuthenticatorImpl(
				attestedCredentialDataConverter.convert(userAuthenticator.getAttestedCredentialsData()),
				new NoneAttestationStatement(), userAuthenticator.getCounter());

		var b64Challenge = Base64.getUrlEncoder().encodeToString(challenge.getBytes());
		ServerProperty serverProperty = new ServerProperty(getOrigin(), getRpId(), new DefaultChallenge(b64Challenge),
				null);

		var clientDataJSON = Base64.getUrlDecoder().decode(verification.response().clientDataJSON());
		var authenticatorData = Base64.getUrlDecoder().decode(verification.response().authenticatorData());
		var signature = Base64.getUrlDecoder().decode(verification.response().signature());

		AuthenticationRequest authenticationRequest = new AuthenticationRequest(verification.id().getBytes(),
				authenticatorData, clientDataJSON, signature);
		AuthenticationParameters authenticationParameters = new AuthenticationParameters(serverProperty, authenticator,
				null, false, false);

		try {
			AuthenticationData authenticationData = webAuthnManager.parse(authenticationRequest);
			webAuthnManager.validate(authenticationData, authenticationParameters);

			// TODO: required per spec, to allow for authenticator clone detection, see:
			// https://www.w3.org/TR/webauthn-3/#sctn-sign-counter
			// userAuthenticator.setCounter(authenticationData.getAuthenticatorData().getSignCount());
		}
		catch (DataConversionException e) {
			// If you would like to handle WebAuthn data structure parse error, please
			// catch DataConversionException
			throw e;
		}
		catch (ValidationException e) {
			// If you would like to handle WebAuthn data validation error, please catch
			// ValidationException
			throw e;
		}
		return userAuthenticator.getUser();
	}

	@Transactional
	public boolean deleteCredential(User user, String credentialId) {
		return repository.deleteByIdAndUser(credentialId, user) > 0;
	}

	private String getRpId() {
		return UriComponentsBuilder.fromHttpUrl(request.getRequestURL().toString()).build().getHost();
	}

	private Origin getOrigin() {
		var origin = UriComponentsBuilder.fromHttpUrl(request.getRequestURL().toString())
			.replacePath(null)
			.toUriString();
		return new Origin(origin);
	}

}
