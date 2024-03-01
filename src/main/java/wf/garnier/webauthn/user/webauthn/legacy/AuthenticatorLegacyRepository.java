package wf.garnier.webauthn.user.webauthn.legacy;

import jakarta.annotation.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@Component
public class AuthenticatorLegacyRepository {

	private final MultiValueMap<String, UserAuthenticatorLegacy> authenticators = new LinkedMultiValueMap<>();

	public void save(String credentialId, UserAuthenticatorLegacy authenticator) {
		System.out.println("💾💾💾💾💾💾💾💾💾 " + credentialId);
		authenticators.add(credentialId, authenticator);
	}

	@Nullable
	public UserAuthenticatorLegacy load(String credentialId) {
		System.out.println("💿💿💿💿💿💿💿💿💿 " + credentialId);
		return authenticators.getFirst(credentialId);
	}

}
