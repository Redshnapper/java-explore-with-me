DROP TABLE IF EXISTS users,categories,locations,events,participation_requests,compilations,compilations_events CASCADE;

CREATE TABLE IF NOT EXISTS users
(
    id    BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name  VARCHAR NOT NULL,
    email VARCHAR NOT NULL,
    CONSTRAINT unique_user_email UNIQUE (email)
);

CREATE TABLE IF NOT EXISTS categories
(
    id   BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name VARCHAR NOT NULL,
    CONSTRAINT unique_category_name UNIQUE (name)
);

CREATE TABLE IF NOT EXISTS locations
(
    id  BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    lat FLOAT NOT NULL,
    lon FLOAT NOT NULL
);

CREATE TABLE IF NOT EXISTS events
(
    id                 BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    annotation         VARCHAR                     NOT NULL,
    title              VARCHAR                     NOT NULL,
    description        VARCHAR                     NOT NULL,
    category_id        BIGINT                      NOT NULL,
    initiator_id       BIGINT                      NOT NULL,
    location_id        BIGINT                      NOT NULL,
    created_on         TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    published_on       TIMESTAMP WITHOUT TIME ZONE,
    date               TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    state              VARCHAR                     NOT NULL,
    participant_limit  INTEGER default 0,
    paid               BOOLEAN DEFAULT false,
    request_moderation BOOLEAN default false,
    CONSTRAINT fk_events_to_users FOREIGN KEY (initiator_id) REFERENCES users (id),
    CONSTRAINT fk_events_to_categories FOREIGN KEY (category_id) REFERENCES categories (id),
    CONSTRAINT fk_events_to_locations FOREIGN KEY (location_id) REFERENCES locations (id)
);

CREATE TABLE IF NOT EXISTS participation_requests
(
    id           BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    requester_id BIGINT                      NOT NULL,
    event_id     BIGINT                      NOT NULL,
    status       VARCHAR                     NOT NULL,
    created_date TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    UNIQUE (requester_id, event_id),
    CONSTRAINT fk_attendee_request_to_users FOREIGN KEY (requester_id) REFERENCES users (id),
    CONSTRAINT fk_attendee_request_to_events FOREIGN KEY (event_id) REFERENCES events (id)
);

CREATE TABLE IF NOT EXISTS compilations
(
    id        BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    title     VARCHAR NOT NULL,
    is_pinned BOOLEAN NOT NULL
);

CREATE TABLE IF NOT EXISTS compilations_events
(
    id       BIGINT,
    event_id BIGINT,
    PRIMARY KEY (id, event_id),
    CONSTRAINT fk_compilation_id FOREIGN KEY (id) REFERENCES compilations (id),
    CONSTRAINT fk_event_id FOREIGN KEY (event_id) REFERENCES events (id)
);



