alter table if exists public.zee_t_pedido_acesso_investidor
    alter column id_investidor drop not null,
    add column if not exists nif_entidade varchar(20);
