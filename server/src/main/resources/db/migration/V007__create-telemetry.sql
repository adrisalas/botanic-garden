CREATE TABLE telemetry_data
(
    id        BIGSERIAL PRIMARY KEY,
    beacon_id VARCHAR(255) NOT NULL,
    item_type VARCHAR(50),
    item_id   INTEGER,
    username  VARCHAR(255) NOT NULL,
    date_time TIMESTAMP    NOT NULL
);
