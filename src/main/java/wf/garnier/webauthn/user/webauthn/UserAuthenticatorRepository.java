package wf.garnier.webauthn.user.webauthn;

import java.util.List;

import com.webauthn4j.WebAuthnManager;
import org.springframework.data.repository.CrudRepository;
import wf.garnier.webauthn.user.User;

public interface UserAuthenticatorRepository extends CrudRepository<UserAuthenticator, String> {

    public List<UserAuthenticator> findUserAuthenticatorByUser(User user);

}
