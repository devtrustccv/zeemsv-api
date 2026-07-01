alter table if exists public.zee_t_pedido_acesso_investidor
    add column if not exists user_task varchar(100);
