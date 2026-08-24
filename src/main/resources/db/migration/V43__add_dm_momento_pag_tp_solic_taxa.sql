alter table if exists public.zee_t_tp_solic_taxa
    add column if not exists dm_momento_pag varchar(255);
