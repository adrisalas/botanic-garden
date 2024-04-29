CREATE TABLE poi
(
    id          SERIAL PRIMARY KEY,
    name        VARCHAR,
    description VARCHAR(2550) NOT NULL,
    image       VARCHAR(510)
);