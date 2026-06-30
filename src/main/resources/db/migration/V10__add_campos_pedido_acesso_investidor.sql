alter table if exists public.zee_t_pedido_acesso_investidor
    add column if not exists dm_tipo_pedido varchar(50),
    add column if not exists id_socio_repres integer,
    add column if not exists nif_investidor varchar(20);

update public.zee_t_pedido_acesso_investidor
set dm_tipo_pedido = 'ACESSO'
where dm_tipo_pedido is null;

alter table if exists public.zee_t_pedido_acesso_investidor
    alter column dm_tipo_pedido set not null;
