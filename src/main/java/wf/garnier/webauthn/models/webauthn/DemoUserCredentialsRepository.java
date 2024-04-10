package wf.garnier.webauthn.models.webauthn;

import java.util.Base64;
import java.util.List;
import java.util.UUID;

import org.springframework.security.webauthn.api.Base64Url;
import org.springframework.security.webauthn.management.CredentialRecord;
import org.springframework.security.webauthn.management.UserCredentialRepository;
import org.springframework.stereotype.Component;

@Component
public class DemoUserCredentialsRepository implements UserCredentialRepository {

	private final DemoCredentialRecordRepository credentialRecordRepo;

	public DemoUserCredentialsRepository(DemoCredentialRecordRepository credentialRecordRepo) {
		this.credentialRecordRepo = credentialRecordRepo;
	}

	@Override
	public void delete(Base64Url credentialId) {
		// TODO
	}

	@Override
	public void save(CredentialRecord credentialRecord) {
		var creds = DemoCredentialRecord.fromCredentialRecord(credentialRecord);
		credentialRecordRepo.save(creds);
	}

	@Override
	public CredentialRecord findByCredentialId(Base64Url credentialId) {
		return credentialRecordRepo.findById(credentialId.getBytesAsBase64())
			.map(DemoCredentialRecord::toCredentialsRecord)
			.orElse(null);
	}

	@Override
	public List<CredentialRecord> findByUserId(Base64Url userId) {
		return credentialRecordRepo.findDemoCredentialRecordByUserEntityId(userId.getBytesAsBase64())
			.stream()
			.map(DemoCredentialRecord::toCredentialsRecord)
			.toList();
	}

}
