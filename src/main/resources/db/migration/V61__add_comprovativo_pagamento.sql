alter table if exists public.zee_t_pagamento
    add column if not exists path_comprovativo_pagamento varchar(1000),
    add column if not exists motivo_rejeicao text,
    add column if not exists user_validacao varchar(255),
    add column if not exists data_validacao date;
