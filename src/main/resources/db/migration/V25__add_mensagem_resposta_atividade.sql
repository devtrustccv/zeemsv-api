alter table if exists public.zee_t_atividade
    add column if not exists mensagem_resposta varchar(4000);
