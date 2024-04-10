package wf.garnier.webauthn.models.webauthn;

import java.time.Instant;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.security.webauthn.api.Base64Url;
import org.springframework.security.webauthn.api.PublicKeyCredentialType;
import org.springframework.security.webauthn.management.CredentialRecord;
import org.springframework.security.webauthn.management.ImmutableCredentialRecord;

@Entity
@Table(name = "credential_record")
public class DemoCredentialRecord {

	@Id
	private String id; // base64

	@NotNull
	private String userEntityId;

	@NotBlank
	private String label;

	@NotNull
	private Long signCount;

	@NotNull
	private byte[] attestationObject;

	private Instant created;

	private Instant lastUsed;

	public DemoCredentialRecord() {

	}

	public DemoCredentialRecord(String base64id, long signCount, String label, byte[] attestationObject,
			String userEntityId) {
		this.id = base64id;
		this.signCount = signCount;
		this.label = label;
		this.attestationObject = attestationObject;
		this.userEntityId = userEntityId;
		this.created = Instant.now();
		this.lastUsed = Instant.now();
	}

	public CredentialRecord toCredentialsRecord() {
		return ImmutableCredentialRecord.builder()
			.userEntityUserId(Base64Url.fromBase64(userEntityId))
			.credentialType(PublicKeyCredentialType.PUBLIC_KEY)
			.credentialId(Base64Url.fromBase64(id))
			.signatureCount(signCount)
			.label(label)
			.attestationObject(new Base64Url(attestationObject))
			.uvInitialized(false)
			.build();
	}

	public static DemoCredentialRecord fromCredentialRecord(CredentialRecord credentialRecord) {
		//@formatter:off
		return new DemoCredentialRecord(
				credentialRecord.getCredentialId().getBytesAsBase64(),
				credentialRecord.getSignatureCount(),
				credentialRecord.getLabel(),
				credentialRecord.getAttestationObject().getBytes(),
				credentialRecord.getUserEntityUserId().getBytesAsBase64()
		);
		//@formatter:on
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getEntityUserId() {
		return userEntityId;
	}

	public void setEntityUserId(String userEntityId) {
		this.userEntityId = DemoCredentialRecord.this.userEntityId;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public Long getSignCount() {
		return signCount;
	}

	public void setSignCount(Long signCount) {
		this.signCount = signCount;
	}

	public byte[] getAttestationObject() {
		return attestationObject;
	}

	public void setAttestationObject(byte[] attestationObject) {
		this.attestationObject = attestationObject;
	}

	public Instant getCreated() {
		return created;
	}

	public void setCreated(Instant created) {
		this.created = created;
	}

	public Instant getLastUsed() {
		return lastUsed;
	}

	public void setLastUsed(Instant lastUsed) {
		this.lastUsed = lastUsed;
	}

}
