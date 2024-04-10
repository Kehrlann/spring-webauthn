package wf.garnier.webauthn.models;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, UUID> {

	public Optional<User> findUserByEmail(String email);

	public Optional<User> findUserByUsername(String username);

	public boolean existsUserByEmail(String email);

}
