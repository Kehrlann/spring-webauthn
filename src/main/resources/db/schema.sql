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
);


CREATE TABLE IF NOT EXISTS "credential_record"
(
    id                 TEXT PRIMARY KEY NOT NULL,
    user_entity_id     TEXT             NOT NULL REFERENCES credentials_user_entity (id),
    label              TEXT             NOT NULL,
    sign_count         BIGINT           NOT NULL,
    attestation_object BYTEA            NOT NULL,
    created            TIMESTAMP,
    last_used          TIMESTAMP

);

CREATE TABLE IF NOT EXISTS "credentials_user_entity"
(
    id           TEXT PRIMARY KEY NOT NULL,
    username     TEXT             NOT NULL, /* references app_user.username */
    display_name TEXT             NOT NULL
)