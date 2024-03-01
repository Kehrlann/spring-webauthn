package wf.garnier.webauthn.models.webauthn;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import wf.garnier.webauthn.models.User;

public interface UserAuthenticatorRepository extends CrudRepository<UserAuthenticator, String> {

    List<UserAuthenticator> findUserAuthenticatorByUser(User user);

    long deleteByIdAndUser(String id, User user);

}
