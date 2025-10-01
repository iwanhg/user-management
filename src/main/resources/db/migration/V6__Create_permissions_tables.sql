-- Create the permissions table
CREATE TABLE permissions (
    id   INTEGER      NOT NULL AUTO_INCREMENT,
    name VARCHAR(50)  NOT NULL UNIQUE,
    PRIMARY KEY (id)
);

-- Create the join table for the many-to-many relationship between roles and permissions
CREATE TABLE role_permissions (
    role_id       INTEGER NOT NULL,
    permission_id INTEGER NOT NULL,
    PRIMARY KEY (role_id, permission_id),
    FOREIGN KEY (role_id) REFERENCES roles (id),
    FOREIGN KEY (permission_id) REFERENCES permissions (id)
);