package wf.garnier.webauthn.models.webauthn;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import org.springframework.security.webauthn.api.Base64Url;
import org.springframework.security.webauthn.api.PublicKeyCredentialUserEntity;

@Entity
@Table(name = "credentials_user_entity")
public class DemoPublicKeyCredentialUserEntity {

	@Id
	private String id; // Generated on "credential creation options" request

	@NotBlank
	private String username;

	@NotBlank
	private String displayName;

	public DemoPublicKeyCredentialUserEntity() {
	}

	public DemoPublicKeyCredentialUserEntity(String id, String username, String displayName) {
		this.id = id;
		this.username = username;
		this.displayName = displayName;
	}

	public PublicKeyCredentialUserEntity toPublicKeyCredentialUserEntity() {
		return PublicKeyCredentialUserEntity.builder()
			.id(Base64Url.fromBase64(id))
			.name(username)
			.displayName(displayName)
			.build();
	}

	public static DemoPublicKeyCredentialUserEntity fromPublicKeyCredentialUserEntity(
			PublicKeyCredentialUserEntity publicKeyCredentialUserEntity) {
		return new DemoPublicKeyCredentialUserEntity(publicKeyCredentialUserEntity.getId().getBytesAsBase64(),
				publicKeyCredentialUserEntity.getName(), publicKeyCredentialUserEntity.getDisplayName());
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

}