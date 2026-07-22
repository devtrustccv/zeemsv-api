alter table if exists t_historico_pedido
    add column if not exists xml bytea;
