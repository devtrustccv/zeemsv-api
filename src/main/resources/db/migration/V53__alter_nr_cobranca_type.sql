alter table if exists public.zee_t_cobranca
    drop column if exists nr_cobranca;

alter table if exists public.zee_t_cobranca
    add column if not exists nr_cobranca varchar null;
