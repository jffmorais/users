CREATE TABLE tb_users (
    user_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL
);

CREATE TABLE tb_roles (
    role_id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);

CREATE TABLE tb_users_roles (
    user_id UUID NOT NULL,
    role_id INTEGER NOT NULL,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES tb_users (user_id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES tb_roles (role_id) ON DELETE CASCADE
);

INSERT INTO tb_roles (role_id, name) VALUES (1, 'ADMIN');
INSERT INTO tb_roles (role_id, name) VALUES (2, 'BASIC');
