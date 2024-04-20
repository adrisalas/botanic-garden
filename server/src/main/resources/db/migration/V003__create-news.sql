CREATE TABLE news
(
    id          SERIAL PRIMARY KEY,
    title       VARCHAR(255)  NOT NULL,
    subtitle    VARCHAR(255)  NOT NULL,
    description VARCHAR(2550) NOT NULL,
    "date"      TIMESTAMP     NOT NULL,
    image       VARCHAR(510)
);