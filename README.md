Só pra rodar o pipeline

## Banco de dados local

`docker compose up -d` sobe um PostgreSQL local com um banco e um usuário
por serviço: `cliente_db`/`cliente_user`, `loja_db`/`loja_user` e
`entregador_db`/`entregador_user` (senhas em `db/init/01-schemas-e-usuarios.sql`,
só para uso local).

## Documentação da API

Com a aplicação rodando: Swagger UI em `/swagger-ui.html`, OpenAPI JSON em
`/v3/api-docs`.
