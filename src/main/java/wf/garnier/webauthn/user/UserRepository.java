package wf.garnier.webauthn.user;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.repository.CrudRepository;
import wf.garnier.webauthn.user.User;

public interface UserRepository extends CrudRepository<User, UUID> {

	public Optional<User> findUserByEmail(String email);

	public boolean existsUserByEmail(String email);

}
