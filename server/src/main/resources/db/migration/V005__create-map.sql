CREATE TABLE map_point
(
    id  SERIAL PRIMARY KEY,
    lat DOUBLE PRECISION NOT NULL,
    lon DOUBLE PRECISION NOT NULL
);

CREATE TABLE map_point_item
(
    point_id  INTEGER     NOT NULL,
    item_type VARCHAR(50) NOT NULL,
    item_id   INTEGER     NOT NULL,
    PRIMARY KEY (point_id, item_type, item_id)
);

CREATE TABLE map_path
(
    point_a_id INTEGER          NOT NULL,
    point_b_id INTEGER          NOT NULL,
    meters     DOUBLE PRECISION NOT NULL,
    PRIMARY KEY (point_a_id, point_b_id)
);

CREATE TABLE map_route
(
    id              SERIAL      NOT NULL,
    name            VARCHAR(50) NOT NULL,
    associated_user VARCHAR(50) NULL
);

CREATE TABLE map_route_point
(
    route_id INTEGER NOT NULL,
    point_id INTEGER NOT NULL,
    position INTEGER NOT NULL,
    PRIMARY KEY (route_id, point_id, position)
);