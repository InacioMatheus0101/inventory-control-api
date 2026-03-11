-- src/test/resources/data-test.sql (VERSÃO CORRIGIDA PARA H2)

-- Inserir perfis (roles) usando a sintaxe MERGE do H2
MERGE INTO roles (id, name) KEY(id) VALUES (1, 'ROLE_ADMIN');
MERGE INTO roles (id, name) KEY(id) VALUES (2, 'ROLE_TECHNICIAN');

-- Inserir usuários
-- Senha para 'admin123' criptografada com BCrypt
MERGE INTO users (id, username, password) KEY(id) VALUES (1, 'admin', '$2a$10$3g5v3.gO9.3B0.Uv5b2a5uJ/iX.a/iX.a/iX.a/iX.a/iX.a');
-- Senha para 'tech123' criptografada com BCrypt
MERGE INTO users (id, username, password) KEY(id) VALUES (2, 'tech', '$2a$10$4h6v4.hO0.4B1.Vv6c3b6vK/jY.b/jY.b/jY.b/jY.b/jY.b');

-- Associar perfis aos usuários
MERGE INTO user_roles (user_id, role_id) KEY(user_id, role_id) VALUES (1, 1); -- admin -> ROLE_ADMIN
MERGE INTO user_roles (user_id, role_id) KEY(user_id, role_id) VALUES (1, 2); -- admin -> ROLE_TECHNICIAN
MERGE INTO user_roles (user_id, role_id) KEY(user_id, role_id) VALUES (2, 2); -- tech -> ROLE_TECHNICIAN
