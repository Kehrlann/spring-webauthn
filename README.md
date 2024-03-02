# spring-webauthn

Demo for Spring + WebAuthN


## Dynamics

### Account creation

### Magic-link login

### WebAuthN key linking

### WebAuthN log in

## Important stuff to mention

- CBOR: Compact Binary Object Representation
- COSE: COBR Object Signing and Encryption
- Attestation: verifiable evidence as to the origin of an authenticator and the data it emits
  - Credential IDs, creds Key Pair, Signature Counter
- Ceremony: protocol + human interactions (out of band for protocol)
- Authentication Ceremony: 
  - Test of user presence or user verification
  - "Authorization gesture", physical interaction
- Authenticator types:
  - Hardware or Software
  - In the client device (platform authenticators) or outside (roaming authenticator)
  - Stores key locally or on a server
- Discoverable keys
  - Stored on device, per RP-id
  - Contrary to server-side public key credentials source?

## TODO

- [x] registration
- [x] "email" log-in
  - [x] code table
- [x] Authenticator registration (revocation ?)
- [x] Authentication
- [x] persist authenticator
- [x] name authenticator
- [ ] Clean up the request to register a new authenticator (what's base64 encoded...)
- [ ] refresh page when registering an authenticator + flash
  - /!\ fetch API is very limited for this
- [ ] remove dead code
- [x] clean up routes
- [ ] css styling
- [ ] code expiry
- [x] delete authenticator

- [ ] mkcert for auth.localhost.garnier.wf (?)
- [ ] deploy online (?)
- [ ] ykman / safari password management
  - `ykman credentials list`