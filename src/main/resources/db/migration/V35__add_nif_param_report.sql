alter table if exists public.zee_t_param_report
    add column if not exists nif varchar(50);
