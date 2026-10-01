alter table public.zee_t_pagamento_intencao
    add column if not exists data_hora_registo timestamp,
    add column if not exists data_expiracao timestamp;

update public.zee_t_pagamento_intencao
set data_hora_registo = coalesce(data_hora_registo, data_registo::timestamp),
    data_expiracao = coalesce(data_expiracao, data_registo::timestamp + interval '5 minutes')
where data_registo is not null
  and (data_hora_registo is null or data_expiracao is null);
