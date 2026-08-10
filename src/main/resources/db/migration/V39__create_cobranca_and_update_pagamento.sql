create table if not exists public.zee_t_cobranca (
    id serial primary key,
    nr_cobranca integer,
    id_solicitacao integer,
    id_solic_taxa integer,
    id_investidor integer,
    id_projeto integer,
    nr_processo integer,
    data_emissao date,
    data_vencimento date,
    valor_total varchar(255),
    valor_pago varchar(255),
    valor_divida varchar(255),
    tipo_liquidacao varchar(255),
    nr_prestacao integer,
    dm_estado varchar(255),
    user_registo varchar(255),
    data_registo date
);

create table if not exists public.zee_t_cobranca_prestacao (
    id serial primary key,
    id_cobranca integer,
    nr_prestacao varchar(255),
    valor varchar(255),
    data_vencimento date,
    dm_estado varchar(255),
    user_registo varchar(255),
    data_registo date,
    constraint fk_zee_t_cobranca_prestacao_cobranca
        foreign key (id_cobranca)
        references public.zee_t_cobranca (id)
);

alter table if exists public.zee_t_pagamento
    add column if not exists id_cobranca integer,
    add column if not exists id_prestacao integer,
    add column if not exists id_processo integer,
    add column if not exists valor_pago varchar(255),
    add column if not exists origem_pagamento varchar(255),
    add column if not exists nr_cheque varchar(255),
    add column if not exists flag_integracao varchar(255),
    add column if not exists data_integracao date,
    add column if not exists user_integracao varchar(255),
    add column if not exists dm_estado varchar(255);

do $$
begin
    if not exists (
        select 1
        from pg_constraint
        where conname = 'fk_zee_t_pagamento_cobranca'
    ) then
        alter table public.zee_t_pagamento
            add constraint fk_zee_t_pagamento_cobranca
                foreign key (id_cobranca)
                references public.zee_t_cobranca (id);
    end if;
end $$;

do $$
begin
    if not exists (
        select 1
        from pg_constraint
        where conname = 'fk_zee_t_pagamento_prestacao'
    ) then
        alter table public.zee_t_pagamento
            add constraint fk_zee_t_pagamento_prestacao
                foreign key (id_prestacao)
                references public.zee_t_cobranca_prestacao (id);
    end if;
end $$;
