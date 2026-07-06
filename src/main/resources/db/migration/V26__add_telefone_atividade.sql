alter table if exists public.zee_t_atividade
    add column if not exists telefone varchar(50);
