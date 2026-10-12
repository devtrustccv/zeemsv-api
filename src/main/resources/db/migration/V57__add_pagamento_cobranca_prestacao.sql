alter table if exists public.zee_t_cobranca_prestacao
    add column if not exists id_pagamento integer;

update public.zee_t_cobranca_prestacao prestacao
set id_pagamento = pagamento.id
from (
    select distinct on (id_prestacao)
        id,
        id_prestacao
    from public.zee_t_pagamento
    where id_prestacao is not null
    order by id_prestacao, data_pagamento desc nulls last, id desc
) pagamento
where prestacao.id = pagamento.id_prestacao
  and prestacao.id_pagamento is null;

do $$
begin
    if not exists (
        select 1
        from pg_constraint
        where conname = 'fk_zee_t_cobranca_prestacao_pagamento'
    ) then
        alter table public.zee_t_cobranca_prestacao
            add constraint fk_zee_t_cobranca_prestacao_pagamento
                foreign key (id_pagamento)
                references public.zee_t_pagamento (id);
    end if;
end $$;

create index if not exists ix_zee_t_cobranca_prestacao_id_pagamento
    on public.zee_t_cobranca_prestacao (id_pagamento);
