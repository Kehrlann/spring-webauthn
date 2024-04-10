package wf.garnier.webauthn.models;

import java.io.Serializable;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;

public class UserAuthenticationToken extends AbstractAuthenticationToken implements Serializable {

	private final User user;

	public UserAuthenticationToken(User user) {
		super(AuthorityUtils.NO_AUTHORITIES);
		this.user = user;
	}

	@Override
	public Object getCredentials() {
		return null;
	}

	@Override
	public Object getPrincipal() {
		return user;
	}

	@Override
	public boolean isAuthenticated() {
		return true;
	}

	@Override
	public void setAuthenticated(boolean authenticated) {
		throw new RuntimeException("Can't touch this 🎶");
	}

}
