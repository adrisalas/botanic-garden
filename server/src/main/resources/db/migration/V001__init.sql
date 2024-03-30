CREATE TABLE beacon
(
    id        VARCHAR(255) PRIMARY KEY,
    item_type VARCHAR(50),
    item_id   INTEGER
);

CREATE TABLE plant
(
    id              SERIAL PRIMARY KEY,
    common_name     VARCHAR(255)  NOT NULL,
    scientific_name VARCHAR(255)  NOT NULL,
    description     VARCHAR(2550) NOT NULL,
    image           VARCHAR(255)  NOT NULL,
    plant_type      VARCHAR(255)  NOT NULL,
    season          VARCHAR(50),
    leaf_type       VARCHAR(255),
    water           VARCHAR(255),
    flowering_begin VARCHAR(50),
    flowering_end   VARCHAR(50)
);