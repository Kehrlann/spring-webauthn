package wf.garnier.webauthn;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

interface UserRepository extends CrudRepository<User, String> {

	Optional<User> findUserByEmail(String email);

	boolean existsUserByEmail(String email);

}
