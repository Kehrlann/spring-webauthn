package wf.garnier.webauthn.models;

import java.io.Serializable;
import java.util.Collection;
import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;

@Entity
@Table(name = "app_user")
public class User implements Serializable, UserDetails {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@NotBlank
	private String username;

	@NotBlank
	private String email;

	public User() {
	}

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return AuthorityUtils.NO_AUTHORITIES;
	}

	@Override
	public String getPassword() {
		return null;
	}

	@NotBlank
	public String getUsername() {
		return username;
	}

	public void setUsername(@NotBlank String username) {
		this.username = username;
	}

	@NotBlank
	public String getEmail() {
		return email;
	}

	public void setEmail(@NotBlank String email) {
		this.email = email;
	}

	@Override
	public String toString() {
		return "User{" + "id='" + id + '\'' + ", username='" + username + '\'' + ", email='" + email + '\'' + '}';
	}

}
