package wf.garnier.webauthn.models.webauthn;

import java.time.Instant;
import java.util.Base64;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import org.springframework.security.webauthn.api.AuthenticatorTransport;
import org.springframework.security.webauthn.api.Base64Url;
import org.springframework.security.webauthn.api.PublicKeyCredentialType;
import org.springframework.security.webauthn.management.CredentialRecord;
import org.springframework.security.webauthn.management.PublicKeyCose;
import wf.garnier.webauthn.models.User;

@Entity
@Table(name = "authenticator")
public class UserAuthenticator implements CredentialRecord {

	@Id
	private String id;

	@ManyToOne(fetch = FetchType.EAGER, optional = false)
	private User user;

	private String credentialsName;

	private byte[] attestationObject;

	public UserAuthenticator() {
	}

	public UserAuthenticator(String id, User user, String credentialsName, byte[] attestationObject) {
		this.id = id;
		this.user = user;
		this.credentialsName = credentialsName;
		this.attestationObject = attestationObject;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getCredentialsName() {
		return credentialsName;
	}

	public void setCredentialsName(String credentialsName) {
		this.credentialsName = credentialsName;
	}

	public byte[] attestationObject() {
		return attestationObject;
	}

	public void setAttestationObject(byte[] attestationStatement) {
		this.attestationObject = attestationStatement;
	}

	// Only "public key" is recognized as a type anyway
	@Override
	public PublicKeyCredentialType getCredentialType() {
		return PublicKeyCredentialType.PUBLIC_KEY;
	}

	@Override
	public Base64Url getCredentialId() {
		return Base64Url.fromBase64(id);
	}

	// This is currently not used ; we rely on the sole AttestationObject
	@Override
	public PublicKeyCose getPublicKey() {
		return null;
	}

	// Cheating: we should be returning the "real" signature count
	// But we don't even store it in the first place. We _could_ use the signature count
	// in the attestation object but ¯\_(ツ)_/¯
	@Override
	public long getSignatureCount() {
		return 0;
	}

	// Unused
	@Override
	public boolean isUvInitialized() {
		return false;
	}

	// Unused
	@Override
	public List<AuthenticatorTransport> getTransports() {
		return null;
	}

	// Unused
	@Override
	public boolean isBackupEligible() {
		return false;
	}

	// Unused
	@Override
	public boolean isBackupState() {
		return false;
	}

	@Override
	public Base64Url getUserEntityUserId() {
        var base64UserId = Base64.getUrlEncoder().encodeToString(user.getId().toString().getBytes());
		return Base64Url.fromBase64(base64UserId);
	}

	@Override
	public Base64Url getAttestationObject() {
		return Base64Url.fromBase64(Base64.getUrlEncoder().encodeToString(attestationObject()));
	}

	// Unused
	@Override
	public Base64Url getAttestationClientDataJSON() {
		return null;
	}

	@Override
	public String getLabel() {
		return credentialsName;
	}

	// Only showcased in the table displaying authenticators
	@Override
	public Instant getLastUsed() {
		return null;
	}

	// Only showcased in the table displaying authenticators
	@Override
	public Instant getCreated() {
		return null;
	}

}
