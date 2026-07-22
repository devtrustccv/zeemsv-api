alter table if exists t_pedido
    add column if not exists path_recibo varchar(1000);
