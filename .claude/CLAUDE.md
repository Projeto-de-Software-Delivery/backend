# Fluxo de trabalho com Jira

O Jira já tem automação nativa (dev panel/GitHub integration) que transiciona
a issue sozinha: criar a branch move para "Em andamento", abrir PR move para
"Em análise", e o merge do PR move para "Concluído". Esse é o único mecanismo
de transição — não fazer transição manual por MCP, exceto para corrigir um
caso que a automação claramente não cobriu (ver último item).

- Toda issue trabalhada precisa de uma branch **própria e dedicada**, nomeada
  `KAN-<numero>-<slug>` (o slug sugerido pela própria issue no Jira).
- Nunca commitar ou dar push direto em `main`. Todo trabalho vai por PR da
  branch da issue, mergeado no GitHub.
- Commits nessa branch começam com o prefixo `KAN-<numero>:` na mensagem.
- **1 branch/PR = 1 issue.** Se uma mudança acabar cobrindo mais de uma issue
  (ex.: um contrato definido "de brinde" ao implementar outra task), abrir
  branch/PR separado para essa issue extra, ou pelo menos comentar e
  transicionar essa issue manualmente depois do merge — nunca deixar uma
  issue "carona" sem rastro algum. Uma issue que fica presa em "Tarefas
  pendentes" mesmo com o trabalho pronto e mergeado é sinal desse problema.
- Nunca adicionar `Co-Authored-By` nas mensagens de commit.

# Contexto do projeto

Este repositório (`pedidos-lojas`) é um de quatro microsserviços independentes
do app de delivery — ver [README.md](../README.md) para a arquitetura
completa, a lista dos outros serviços (`gateway`, `delivery-entregador`,
`notificacoes`), a máquina de estados do pedido e o contrato de eventos do
RabbitMQ (`pedido.criado`, `pedido.validado`, `entrega.aceita`,
`pedido.retirado`, `pedido.entregue` — detalhado em
[`src/main/java/br/insper/delivery/pedido/payloads.md`](../src/main/java/br/insper/delivery/pedido/payloads.md)).

Pontos que não são óbvios a partir do código sozinho:

- A atribuição de entregador é **síncrona via HTTP**, não por fila: o
  `delivery-entregador` só expõe CRUD + status, sem fila nem endpoint de
  oferta de corrida. O backend consulta `GET /entregadores`, escolhe o
  primeiro `DISPONIVEL` e marca como `EM_ENTREGA` via `PATCH`, tudo dentro de
  `PedidoService.aceitar()`.
- Publicação de evento no RabbitMQ é sempre *best-effort*: uma falha do
  broker é logada, nunca propagada como erro HTTP, porque a operação de
  negócio (baixa de estoque, transição de status) já foi persistida antes da
  tentativa de publicação.
- `gateway`, `notificacoes` e `delivery-entregador` vivem em repositórios
  próprios na mesma organização GitHub
  (`Projeto-de-Software-Delivery`) — não há submódulos nem monorepo.
