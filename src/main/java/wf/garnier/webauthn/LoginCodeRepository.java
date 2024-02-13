package wf.garnier.webauthn;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

interface LoginCodeRepository extends CrudRepository<LoginCode, UUID> {

}
