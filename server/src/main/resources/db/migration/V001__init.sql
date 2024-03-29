CREATE TABLE beacon
(
    id VARCHAR(255) PRIMARY KEY
);

CREATE TABLE beacon_item
(
    id      VARCHAR(255) PRIMARY KEY,
    item_id INTEGER NOT NULL
);

CREATE TABLE plant
(
    id              SERIAL PRIMARY KEY,
    common_name     VARCHAR(255),
    scientific_name VARCHAR(255),
    description     VARCHAR(2550),
    image           VARCHAR(255),
    plant_type      VARCHAR(255),
    season          VARCHAR(50),
    leaf_type       VARCHAR(255),
    water           VARCHAR(255),
    flowering_begin VARCHAR(50),
    flowering_end   VARCHAR(50)
);