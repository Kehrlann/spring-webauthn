package wf.garnier.webauthn.models.webauthn;

public record CredentialsVerification(String id, Response response) {

    public record Response(String authenticatorData, String clientDataJSON, String signature) {}

}
