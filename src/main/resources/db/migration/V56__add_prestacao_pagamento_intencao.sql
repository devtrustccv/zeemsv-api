alter table if exists public.zee_t_pagamento_intencao_cobranca
    add column if not exists id_prestacao int4;

alter table if exists public.zee_t_pagamento_intencao_cobranca
    drop constraint if exists ux_pagamento_intencao_cobranca;

do $$
begin
    if not exists (
        select 1
        from pg_constraint
        where conname = 'fk_pagamento_intencao_cobranca_prestacao'
    ) then
        alter table public.zee_t_pagamento_intencao_cobranca
            add constraint fk_pagamento_intencao_cobranca_prestacao
                foreign key (id_prestacao)
                references public.zee_t_cobranca_prestacao (id);
    end if;
end $$;

create unique index if not exists ux_pagamento_intencao_cobranca_total
    on public.zee_t_pagamento_intencao_cobranca (id_intencao, id_cobranca)
    where id_prestacao is null;

create unique index if not exists ux_pagamento_intencao_cobranca_prestacao
    on public.zee_t_pagamento_intencao_cobranca (id_intencao, id_cobranca, id_prestacao)
    where id_prestacao is not null;
