ALTER TABLE tb_users
ADD manager_id UUID;

ALTER TABLE tb_users
ADD CONSTRAINT fk_manager_id
    FOREIGN KEY (manager_id)
    REFERENCES tb_users(user_id);