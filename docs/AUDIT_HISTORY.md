# Historico de auditoria transacional

`GET /api/v1/auditorias/transacoes` aceita os seguintes filtros de contexto:

| Parametro | Significado |
| --- | --- |
| `idInvestidor` | Eventos relacionados com o investidor, incluindo eventos automaticos do gateway. |
| `userId` | Autor que executou a acao. Os eventos do gateway continuam com `payment-gateway`. |
| `relatedUserId` | Utilizador associado a solicitacao/processo; na ausencia dessa relacao, o autor humano do evento. |

Os filtros fornecidos sao combinados com AND. Para apresentar o historico completo de um investidor, usar:

```http
GET /api/v1/auditorias/transacoes?idInvestidor=10
```

Para incluir os eventos de pagamento relacionados com as solicitacoes de um utilizador:

```http
GET /api/v1/auditorias/transacoes?idInvestidor=10&relatedUserId=30
```

Nao acrescentar `userId=30` se o objetivo for incluir eventos executados pelo gateway.

## Informacao gravada

O autor original (`userId`) e preservado. A resposta inclui `relatedUserId` quando existe um unico utilizador relacionado. O contexto e recuperado pelas referencias a solicitacao, cobranca, prestacao, projeto, pagamento e intencao de pagamento. Para solicitacoes, usa-se `userSolic`; para projetos, `userCreate`.

`metadata.idInvestidor` e preenchido quando existe um unico investidor. `metadata.idsInvestidor` e `metadata.relatedUserIds` permitem pesquisar eventos que abrangem varias cobrancas, investidores ou utilizadores, sem escolher arbitrariamente um deles. O filtro `idInvestidor` consulta ambos os campos de investidor; `relatedUserId` consulta o campo principal e a lista de utilizadores.

A auditoria automatica aproveita os identificadores da rota, dos parametros e dos campos de referencias no JSON processado. Apenas esses identificadores sao acrescentados aos metadados; o corpo completo do pedido nao e guardado. Rotas sem referencias suficientes continuam sem associacao a um investidor. Nao se infere o investidor apenas a partir do utilizador, que pode representar varios investidores.

Nas chamadas DELETE, o contexto e capturado antes da eliminacao, enquanto as relacoes ainda existem.

## Recuperacao dos registos antigos

No ambiente com acesso ao PostgreSQL e MongoDB, iniciar uma vez a aplicacao com:

```text
--application.audit.backfill-context-enabled=true
```

Tambem pode ser usada a variavel de ambiente `APPLICATION_AUDIT_BACKFILL_CONTEXT_ENABLED=true`. Remover a opcao depois da execucao.

A recuperacao processa lotes de 200 registos e adiciona apenas contexto, preservando autor, data, acao e restantes metadados. Os registos processados recebem `contextVersion=1`, permitindo repetir a execucao sem processar novamente os ja atualizados. Falhas de resolucao nao recebem a versao e podem ser tentadas novamente. O log final indica registos atualizados, registos sem investidor recuperavel e falhas.

As relacoes sao recuperadas a partir dos dados atualmente existentes. Recursos eliminados ou referencias ausentes podem impedir a recuperacao; nao e possivel reconstruir com certeza relacoes historicas que tenham sido alteradas. A recuperacao nao cria eventos que nunca foram registados.

Esta opcao esta desativada por defeito e a alteracao de codigo nao executa a recuperacao na base de dados.
