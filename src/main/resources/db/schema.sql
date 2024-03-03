CREATE TABLE IF NOT EXISTS "app_user"
(
    id       UUID PRIMARY KEY NOT NULL,
    username TEXT UNIQUE,
    email    TEXT UNIQUE
);

CREATE TABLE IF NOT EXISTS "login_code"
(
    id              UUID PRIMARY KEY NOT NULL,
    user_id         UUID             NOT NULL REFERENCES app_user (id),
    expiration_time TIMESTAMP DEFAULT (now() + INTERVAL '5 minutes')
);

CREATE TABLE IF NOT EXISTS "authenticator"
(
    id                 TEXT PRIMARY KEY NOT NULL,
    user_id            UUID             NOT NULL REFERENCES app_user (id),
    credentials_name   TEXT             NOT NULL,
    attestation_object BYTEA DEFAULT NULL
)