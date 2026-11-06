# Comprovativos de pagamento

## Submeter

`POST /api/v1/cobrancas/{idCobranca}/comprovativo-pagamento`

Enviar `multipart/form-data` com duas partes:

- `ficheiro`: comprovativo.
- `dados`: JSON com `Content-Type: application/json`.

```json
{
  "id_prestacao": 12,
  "valor": 5000.00,
  "data_pagamento": "2026-11-06",
  "forma_pagamento": "TRANSFERENCIA",
  "user": "operador",
  "referencia": "TRF-123",
  "banco": "Banco"
}
```

`id_prestacao` e opcional; quando indicado, deve pertencer a cobranca da URL.
`referencia` e `banco` sao opcionais. O valor deve ser positivo e nao exceder a
divida do destino. A data nao pode ser futura.

O endpoint devolve HTTP 201 com o envelope `ApiResponse` e os campos
`id_pagamento`, `id_cobranca`, `id_prestacao`, `dm_estado_pag` e
`path_comprovativo_pagamento`.

O pagamento e criado como `PENDENTE`, com `valor_pago = 0`. O documento fica
no MinIO e em `zee_t_doc_relacao`, com `tipo_relacao = PAGAMENTO` e
`id_relacao = id_pagamento`. O caminho fica em `zee_t_pagamento`.
Nao sao alterados o saldo da cobranca, o estado da parcela ou o seu
`id_pagamento` durante a submissao.

## Validacao no backoffice

A confirmacao e a rejeicao sao realizadas por outro sistema, o backoffice.
Esta API recebe o comprovativo e cria o pagamento pendente; nao disponibiliza
endpoints para validar esse comprovativo.

O backoffice fica responsavel por validar o documento, revalidar a divida,
confirmar ou rejeitar o pagamento e atualizar os saldos, taxas e parcelas.
Os campos `motivo_rejeicao`, `user_validacao` e `data_validacao` ficam
no modelo para guardar e apresentar o resultado dessa validacao.
Esta alteracao nao implementa uma integracao entre os dois sistemas.

## Analise e disponibilidade no portal

A listagem de cobrancas devolve, na cobranca e em cada prestacao:

- `em_analise`: existe comprovativo com `dm_estado_pag = PENDENTE` que bloqueia esse destino.
- `pode_pagar`: ha divida pendente e o destino nao esta bloqueado.
- `motivo_bloqueio_pagamento`: mensagem de analise ou ausencia de divida; nulo quando pode pagar.

Um comprovativo pendente sem `id_prestacao` bloqueia a cobranca e todas as
prestacoes. Um comprovativo pendente de uma prestacao bloqueia essa prestacao
e o pagamento integral da cobranca; as outras prestacoes continuam disponiveis.
O bloqueio aplica-se tambem a comprovativos de valores parciais.

Os indicadores sao calculados a partir dos pagamentos, sem alterar o estado
financeiro da cobranca ou criar colunas adicionais. A confirmacao ou rejeicao
registada pelo backoffice remove o bloqueio de analise; a disponibilidade
continua dependente do saldo restante e de outros comprovativos pendentes.

A API verifica o bloqueio antes de criar uma nova intencao no gateway e antes
de receber outro comprovativo para o mesmo destino. As operacoes bloqueiam a
linha da cobranca durante a transacao para serializar pedidos concorrentes.
Callbacks de pagamentos ja efetuados continuam a ser processados. Este fluxo
nao cancela intencoes de pagamento emitidas antes da submissao do comprovativo.

## Modelo e compatibilidade

A migracao V61 acrescenta os campos de comprovativo e validacao ao pagamento.
A V60 e os campos anteriores da cobranca foram preservados para nao modificar
uma migracao que possa ja ter sido aplicada. O novo fluxo nao escreve o
comprovativo na cobranca. Documentos antigos associados a cobrancas nao sao
reatribuidos automaticamente a pagamentos.

Os calculos de valores pagos passam a contar apenas `dm_estado_pag = PAGO`.
Registos historicos com outro estado ou estado nulo devem ser revistos antes
da entrada em producao.

Cada submissao cria um pagamento e tem como destino uma cobranca ou uma parcela.
Uma transferencia distribuida por varias parcelas requer um modelo adicional
de alocacao e nao esta incluida neste fluxo.
