# Proposta de Arquitetura — App de Delivery

## 1. Visão Geral da Ideia

A plataforma conecta de ponta a ponta os três atores do ecossistema: **Cliente**, **Loja (Parceiro)** e **Entregador**. A arquitetura adota um modelo híbrido de comunicação: **síncrono (REST/HTTPS)** na borda para operações de comando e consulta, e **assíncrono (Event-Driven)** entre os serviços para desacoplamento, consistência eventual e broadcast de atualizações de telemetria e status.

## 2. Modelagem de Entidades e Domínios Isolados

Seguindo as diretrizes de microsserviços, cada serviço é proprietário exclusivo de seus dados (schemas/bancos lógicos isolados no PostgreSQL, sem queries diretas entre tabelas de domínios diferentes):

| Microsserviço | Stack | Entidades / Atributos Principais | Responsabilidade Primária |
|---|---|---|---|
| **Serviço Cliente** | Java / Spring Boot | **Cliente:** `id`, `nome`, `email`, `enderecos[]`<br>**Pedido:** `id`, `cliente_id`, `itens[]`, `total`, `status` | Gestão de contas de usuários, agregação de itens do carrinho e ciclo de vida do pedido. |
| **Serviço Loja** | Java / Spring Boot | **Loja:** `id`, `nome`, `cnpj`, `endereco`<br>**Produto:** `id`, `loja_id`, `nome`, `preco`, `estoque`, `foto_url` | Gestão de cardápios/catálogos, controle de estoque e confirmação/preparo da cozinha. |
| **Serviço Entregador** | Python / FastAPI | **Entregador:** `id`, `nome`, `veiculo`, `status`<br>**Corrida/Tracking:** `id`, `pedido_id`, `entregador_id`, `latitude`, `longitude` | Geolocalização em tempo real, aceite de chamados, telemetria e cálculo de rotas/ETA. |
| **Serviço Notificações** | Node.js / Go | **Sessões Push/Socket:** `user_id`, `device_token`, `socket_id` | Envio de mensagens em tempo real via WebSocket e Push Notifications (FCM/APNs). |

## 3. Máquina de Estados do Pedido

Cada transição de estado persiste o novo registro no banco e publica um evento no Broker:

1. **`AGUARDANDO_VALIDACAO`**: O cliente submete o carrinho; o serviço cria o pedido e publica `pedido.criado`.
2. **`EM_PREPARO`**: A loja aceita o pedido no painel e inicia a produção, publicando `pedido.validado`.
3. **`AGUARDANDO_RETIRADA`**: O sistema de logística localiza um entregador parceiro que aceita a corrida; publica `entrega.aceita`.
4. **`A_CAMINHO`**: O entregador retira a encomenda na loja física e inicia o trajeto até o endereço final; publica `pedido.retirado`.
5. **`ENTREGUE`**: A corrida é finalizada com validação (ex: código/PIN); publica `pedido.entregue`.

## 4. Arquitetura de Microsserviços e Infraestrutura

### 4.1 Borda e Segurança (API Gateway)

- Ponto centralizado de entrada para os aplicativos (App Cliente, App Loja, App Entregador).
- Responsável por terminação TLS (HTTPS), autenticação/autorização unificada (OAuth2 / JWT), controle de vazão (Rate Limiting) e observabilidade (métricas e logs de acesso).

### 4.2 Camada de Domínio e Comunicação Síncrona

- O API Gateway roteia chamadas síncronas REST para o Serviço Cliente, Serviço Loja e Serviço Entregador.
- O Serviço Entregador consome sincronamente via API serviços externos de Mapas e Rotas (geocodificação, malha viária e estimativa de tempo de chegada).

### 4.3 Mensageria e Eventos (Broker Assíncrono)

- **Tecnologias:** Apache Kafka ou RabbitMQ.
- Utilizado para desacoplar a orquestração do pedido.
- **Tópicos principais:** `pedido.criado`, `pedido.validado`, `entrega.aceita`, `pedido.retirado`, `pedido.entregue`.
- O Serviço de Notificações escuta esses tópicos e entrega o status atualizado aos respectivos aplicativos via WebSocket e Push Notifications.

### 4.4 Persistência Poliglota de Dados

- Instância gerenciada do PostgreSQL com isolamento lógico de bases/schemas por serviço (`cliente_db`, `loja_db`, `entregador_db`).
- Garantia do princípio **Database-per-Service**: nenhuma aplicação acessa diretamente dados de outro domínio.
