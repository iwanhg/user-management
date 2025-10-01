-- Create the roles table
CREATE TABLE roles (
    id   INTEGER      NOT NULL AUTO_INCREMENT,
    name VARCHAR(20)  NOT NULL UNIQUE,
    PRIMARY KEY (id)
);

-- Create the join table for the many-to-many relationship
CREATE TABLE user_roles (
    user_id BIGINT  NOT NULL,
    role_id INTEGER NOT NULL,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES users (id),
    FOREIGN KEY (role_id) REFERENCES roles (id)
);