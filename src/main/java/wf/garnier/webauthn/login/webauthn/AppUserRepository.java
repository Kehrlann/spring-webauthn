package wf.garnier.webauthn.login.webauthn;

import java.util.Base64;
import java.util.UUID;

import org.springframework.security.webauthn.api.Base64Url;
import org.springframework.security.webauthn.api.PublicKeyCredentialUserEntity;
import org.springframework.security.webauthn.management.PublicKeyCredentialUserEntityRepository;
import org.springframework.stereotype.Component;
import wf.garnier.webauthn.models.User;
import wf.garnier.webauthn.models.UserRepository;

@Component
class AppUserRepository implements PublicKeyCredentialUserEntityRepository {

	private final UserRepository userRepo;

	public AppUserRepository(UserRepository userRepo) {
		this.userRepo = userRepo;
	}

	@Override
	public String findUsernameByUserEntityId(Base64Url id) {
		var userId = new String(Base64.getUrlDecoder().decode(id.getBytesAsBase64()));
		return userRepo.findById(UUID.fromString(userId)).map(User::getEmail).get();
	}

	@Override
	public PublicKeyCredentialUserEntity findByUsername(String username) {
		var user = userRepo.findUserByUsername(username).get();
		var id = Base64Url.fromBase64(Base64.getUrlEncoder().encodeToString(user.getId().toString().getBytes()));
		return PublicKeyCredentialUserEntity.builder()
			.id(id)
			.name(user.getEmail())
			.displayName(user.getUsername())
			.build();
	}

	@Override
	public void save(String username, PublicKeyCredentialUserEntity userEntity) {

	}

}
