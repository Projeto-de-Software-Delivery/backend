-- Cria um banco e um usuário por serviço, isolando o acesso de cada um ao
-- seu próprio schema (cliente_db, loja_db, entregador_db).

CREATE USER cliente_user WITH PASSWORD 'cliente_pass';
CREATE DATABASE cliente_db OWNER cliente_user;

CREATE USER loja_user WITH PASSWORD 'loja_pass';
CREATE DATABASE loja_db OWNER loja_user;

CREATE USER entregador_user WITH PASSWORD 'entregador_pass';
CREATE DATABASE entregador_db OWNER entregador_user;
