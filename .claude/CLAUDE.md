# Fluxo de trabalho com Jira

- Cada commit deve corresponder a uma task do Jira (projeto KAN) e começar com
  o prefixo `KAN-<numero>:` na mensagem.
- Depois de commitar, dar `git push origin main` — o push é obrigatório,
  não basta commitar localmente.
- Depois do push, transicionar a issue correspondente no Jira para
  "Concluído" (transition id 41, no projeto KAN) usando o MCP da Atlassian.
- Nunca adicionar `Co-Authored-By` nas mensagens de commit.
