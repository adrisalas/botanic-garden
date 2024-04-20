CREATE TABLE botanic_user
(
    username VARCHAR(255) PRIMARY KEY,
    password VARCHAR(255) NOT NULL,
    salt     VARCHAR(255) NOT NULL,
    is_admin BOOLEAN      NOT NULL DEFAULT false
);