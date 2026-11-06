alter table if exists public.zee_t_cobranca
    add column if not exists path_fatura varchar(1000),
    add column if not exists path_recibo varchar(1000),
    add column if not exists path_comprovativo_pagamento varchar(1000);
