package wf.garnier.webauthn.user.webauthn;

import jakarta.annotation.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@Component
public class WebAuthNAuthenticatorRepository {

	private final MultiValueMap<String, UserAuthenticator> authenticators = new LinkedMultiValueMap<>();

	public void save(String credentialId, UserAuthenticator authenticator) {
		System.out.println("💾💾💾💾💾💾💾💾💾 " + credentialId);
		authenticators.add(credentialId, authenticator);
	}

	@Nullable
	public UserAuthenticator load(String credentialId) {
		System.out.println("💿💿💿💿💿💿💿💿💿 " + credentialId);
		return authenticators.getFirst(credentialId);
	}

}
