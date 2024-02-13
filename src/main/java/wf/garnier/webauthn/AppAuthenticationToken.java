package wf.garnier.webauthn;

import org.springframework.security.authentication.AbstractAuthenticationToken;

class AppAuthenticationToken extends AbstractAuthenticationToken {

	private final User user;

	public AppAuthenticationToken(User user) {
		super(null);
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
