alter table if exists public.zee_t_pagamento
    add column if not exists origem_pagamento varchar(255);
