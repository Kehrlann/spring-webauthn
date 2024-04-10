package wf.garnier.webauthn.models.webauthn;

import java.util.List;
import java.util.UUID;

import org.springframework.data.repository.CrudRepository;

public interface DemoCredentialRecordRepository extends CrudRepository<DemoCredentialRecord, String> {

    public List<DemoCredentialRecord> findDemoCredentialRecordByUserEntityId(String userEntityId);

}
