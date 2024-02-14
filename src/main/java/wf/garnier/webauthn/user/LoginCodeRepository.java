package wf.garnier.webauthn.user;

import java.util.UUID;

import org.springframework.data.repository.CrudRepository;

public interface LoginCodeRepository extends CrudRepository<LoginCode, UUID> {

}
