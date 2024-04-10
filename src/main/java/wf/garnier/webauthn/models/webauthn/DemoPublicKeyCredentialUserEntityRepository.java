package wf.garnier.webauthn.models.webauthn;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

public interface DemoPublicKeyCredentialUserEntityRepository extends CrudRepository<DemoPublicKeyCredentialUserEntity, String> {

    public Optional<DemoPublicKeyCredentialUserEntity> findDemoPublicKeyCredentialUserEntityByUsername(String username);

}
