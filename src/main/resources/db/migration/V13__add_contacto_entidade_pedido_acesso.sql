alter table if exists public.zee_t_pedido_acesso_investidor
    add column if not exists email_contacto_entidade varchar(255),
    add column if not exists telemovel_contacto_entidade numeric;
