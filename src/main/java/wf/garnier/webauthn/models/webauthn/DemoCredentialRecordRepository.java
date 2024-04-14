package wf.garnier.webauthn.models.webauthn;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface DemoCredentialRecordRepository extends CrudRepository<DemoCredentialRecord, String> {

	public List<DemoCredentialRecord> findDemoCredentialRecordByUserEntityId(String userEntityId);

	@Query("""
            select r from DemoCredentialRecord r
            	join DemoPublicKeyCredentialUserEntity pk on r.userEntityId = pk.id
            where pk.username = ?1""")
	public List<DemoCredentialRecord> findByUsername(String username);

}
