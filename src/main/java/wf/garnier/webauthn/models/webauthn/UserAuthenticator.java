package wf.garnier.webauthn.models.webauthn;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import wf.garnier.webauthn.models.User;

@Entity
@Table(name = "authenticator")
public class UserAuthenticator {

	@Id
	private String id;

	@ManyToOne(fetch = FetchType.EAGER, optional = false)
	private User user;

	private String credentialsName; // unique per user, meh

	private byte[] attestedCredentialsData;

	private byte[] attestationStatement;

	private long counter;

	public UserAuthenticator() {
	}

	public UserAuthenticator(String id, User user, String credentialsName, byte[] attestedCredentialsData,
			byte[] attestationStatement, long counter) {
		this.id = id;
		this.user = user;
		this.credentialsName = credentialsName;
		this.attestedCredentialsData = attestedCredentialsData;
		this.attestationStatement = attestationStatement;
		this.counter = counter;
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

	public byte[] getAttestedCredentialsData() {
		return attestedCredentialsData;
	}

	public void setAttestedCredentialsData(byte[] attestedCredentialsData) {
		this.attestedCredentialsData = attestedCredentialsData;
	}

	public byte[] getAttestationStatement() {
		return attestationStatement;
	}

	public void setAttestationStatement(byte[] attestationStatement) {
		this.attestationStatement = attestationStatement;
	}

	public long getCounter() {
		return counter;
	}

	public void setCounter(long counter) {
		this.counter = counter;
	}

}
