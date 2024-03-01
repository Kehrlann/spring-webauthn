package wf.garnier.webauthn.models;

import java.util.UUID;

import org.springframework.data.repository.CrudRepository;

public interface LoginCodeRepository extends CrudRepository<LoginCode, UUID> {

}
