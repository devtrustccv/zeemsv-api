alter table if exists public.zee_t_pedido_acesso_investidor
    add column if not exists denominacao_entidade varchar(255);
