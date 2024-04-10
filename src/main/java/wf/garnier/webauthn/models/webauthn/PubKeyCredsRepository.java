package wf.garnier.webauthn.models.webauthn;

import org.springframework.security.webauthn.api.Base64Url;
import org.springframework.security.webauthn.api.PublicKeyCredentialUserEntity;
import org.springframework.security.webauthn.management.PublicKeyCredentialUserEntityRepository;
import org.springframework.stereotype.Component;

@Component
class PubKeyCredsRepository implements PublicKeyCredentialUserEntityRepository {

	private final DemoPublicKeyCredentialUserEntityRepository pubKeyRepo;

	public PubKeyCredsRepository(DemoPublicKeyCredentialUserEntityRepository pubKeyRepo) {
		this.pubKeyRepo = pubKeyRepo;
	}

	@Override
	public String findUsernameByUserEntityId(Base64Url id) {
		return pubKeyRepo.findById(id.getBytesAsBase64())
			.map(DemoPublicKeyCredentialUserEntity::getUsername)
			.orElse(null);
	}

	@Override
	public PublicKeyCredentialUserEntity findByUsername(String username) {
		return pubKeyRepo.findDemoPublicKeyCredentialUserEntityByUsername(username)
			.map(DemoPublicKeyCredentialUserEntity::toPublicKeyCredentialUserEntity)
			.orElse(null);
	}

	@Override
	public void save(String username, PublicKeyCredentialUserEntity userEntity) {
		var entity = DemoPublicKeyCredentialUserEntity.fromPublicKeyCredentialUserEntity(userEntity);
		pubKeyRepo.save(entity);
	}

}
