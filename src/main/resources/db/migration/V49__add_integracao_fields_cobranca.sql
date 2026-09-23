alter table if exists public.zee_t_cobranca
    add column if not exists flag_integracao boolean,
    add column if not exists data_integracao date,
    add column if not exists user_integracao varchar(255);
