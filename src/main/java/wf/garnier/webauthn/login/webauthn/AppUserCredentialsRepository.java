package wf.garnier.webauthn.login.webauthn;

import java.util.Base64;
import java.util.List;
import java.util.UUID;

import org.springframework.security.webauthn.api.Base64Url;
import org.springframework.security.webauthn.management.CredentialRecord;
import org.springframework.security.webauthn.management.UserCredentialRepository;
import org.springframework.stereotype.Component;
import wf.garnier.webauthn.models.webauthn.UserAuthenticatorRepository;

@Component
public class AppUserCredentialsRepository implements UserCredentialRepository {

	private final UserAuthenticatorRepository userAuthenticatorRepository;

	public AppUserCredentialsRepository(UserAuthenticatorRepository userAuthenticatorRepository) {
		this.userAuthenticatorRepository = userAuthenticatorRepository;
	}

	@Override
	public void delete(Base64Url credentialId) {
		// noop ... for now
	}

	@Override
	public void save(CredentialRecord credentialRecord) {
		// noop
	}

	@Override
	public CredentialRecord findByCredentialId(Base64Url credentialId) {
		return userAuthenticatorRepository.findById(credentialId.getBytesAsBase64()).get();
	}

	@Override
	public List<CredentialRecord> findByUserId(Base64Url userId) {
		return this.userAuthenticatorRepository
			.findUserAuthenticatorByUserId(
					UUID.fromString(new String(Base64.getUrlDecoder().decode(userId.getBytesAsBase64()))))
			.stream()
			.map(CredentialRecord.class::cast)
			.toList();
	}

}
