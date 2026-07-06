alter table if exists public.zee_t_atividade
    alter column user_responsavel drop not null,
    alter column user_registo drop not null;
