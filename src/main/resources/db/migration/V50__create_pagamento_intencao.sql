create table if not exists public.zee_t_pagamento_intencao (
    id serial primary key,
    intention_id varchar(255),
    transaction_id varchar(255),
    link_payment varchar(1024),
    valor_total numeric(19, 2),
    dm_estado varchar(255),
    channel_code varchar(255),
    merchant_resp_error_description varchar(1024),
    merchant_resp_merchant_ref varchar(255),
    merchant_resp_merchant_session varchar(255),
    fingerprint varchar(1024),
    user_registo varchar(255),
    data_registo date,
    data_pagamento date
);

create unique index if not exists ux_zee_t_pagamento_intencao_intention_id
    on public.zee_t_pagamento_intencao (intention_id)
    where intention_id is not null;

create unique index if not exists ux_zee_t_pagamento_intencao_transaction_id
    on public.zee_t_pagamento_intencao (transaction_id)
    where transaction_id is not null;

create table if not exists public.zee_t_pagamento_intencao_cobranca (
    id serial primary key,
    id_intencao int4 not null,
    id_cobranca int4 not null,
    id_pagamento int4,
    valor_cobranca numeric(19, 2),
    constraint fk_pagamento_intencao_cobranca_intencao
        foreign key (id_intencao)
        references public.zee_t_pagamento_intencao (id),
    constraint fk_pagamento_intencao_cobranca_cobranca
        foreign key (id_cobranca)
        references public.zee_t_cobranca (id),
    constraint fk_pagamento_intencao_cobranca_pagamento
        foreign key (id_pagamento)
        references public.zee_t_pagamento (id),
    constraint ux_pagamento_intencao_cobranca
        unique (id_intencao, id_cobranca)
);

create index if not exists idx_pagamento_intencao_cobranca_id_intencao
    on public.zee_t_pagamento_intencao_cobranca (id_intencao);

create index if not exists idx_pagamento_intencao_cobranca_id_cobranca
    on public.zee_t_pagamento_intencao_cobranca (id_cobranca);
