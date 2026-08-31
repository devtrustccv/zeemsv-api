create table if not exists public.zee_t_cobranca_taxa (
    id serial primary key,
    id_cobranca int4,
    id_taxa int4,
    id_taxa_cond int4,
    valor float8,
    dm_estado varchar(255),
    user_registo varchar(255),
    data_registo date,
    constraint fk_zee_t_cobranca_taxa_cobranca
        foreign key (id_cobranca)
        references public.zee_t_cobranca (id),
    constraint fk_zee_t_cobranca_taxa_taxa
        foreign key (id_taxa)
        references public.zee_t_taxa (id),
    constraint fk_zee_t_cobranca_taxa_taxa_cond
        foreign key (id_taxa_cond)
        references public.zee_t_taxa_condicoes (id)
);

create index if not exists idx_zee_t_cobranca_taxa_id_cobranca
    on public.zee_t_cobranca_taxa (id_cobranca);

create index if not exists idx_zee_t_cobranca_taxa_id_taxa
    on public.zee_t_cobranca_taxa (id_taxa);

create table if not exists public.zee_t_pagamento_taxa (
    id serial primary key,
    id_pagamento int4,
    id_taxa int4,
    id_taxa_cond int4,
    valor float8,
    dm_estado varchar(255),
    user_registo varchar(255),
    data_registo date,
    constraint fk_zee_t_pagamento_taxa_pagamento
        foreign key (id_pagamento)
        references public.zee_t_pagamento (id),
    constraint fk_zee_t_pagamento_taxa_taxa
        foreign key (id_taxa)
        references public.zee_t_taxa (id),
    constraint fk_zee_t_pagamento_taxa_taxa_cond
        foreign key (id_taxa_cond)
        references public.zee_t_taxa_condicoes (id)
);

create index if not exists idx_zee_t_pagamento_taxa_id_pagamento
    on public.zee_t_pagamento_taxa (id_pagamento);

create index if not exists idx_zee_t_pagamento_taxa_id_taxa
    on public.zee_t_pagamento_taxa (id_taxa);

alter table if exists public.zee_t_pagamento
    add column if not exists id_processo integer,
    add column if not exists forma_pagamento varchar(255),
    add column if not exists referencia varchar(255),
    add column if not exists data_registo date;
