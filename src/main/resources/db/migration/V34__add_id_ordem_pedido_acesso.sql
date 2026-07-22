alter table if exists public.zee_t_pedido_acesso_investidor
    add column if not exists id_ordem integer;

drop index if exists public.ux_zee_t_pedido_acesso_investidor_utilizador_investidor_nao_rejeitado;

create unique index if not exists ux_zee_t_pedido_acesso_investidor_utilizador_investidor_nao_rejeitado
    on public.zee_t_pedido_acesso_investidor (id_utilizador, id_investidor)
    where coalesce(dm_estado, '') <> 'REJEITADO'
        and coalesce(dm_tipo_pedido, '') <> 'REPRES_INVESTIDOR';

create unique index if not exists ux_zee_t_pedido_acesso_investidor_socio_investidor_nao_rejeitado
    on public.zee_t_pedido_acesso_investidor (id_socio_repres, id_investidor)
    where id_socio_repres is not null
        and coalesce(dm_estado, '') <> 'REJEITADO'
        and dm_tipo_pedido = 'REPRES_INVESTIDOR';

create unique index if not exists ux_zee_t_pedido_acesso_investidor_ordem_investidor_nao_rejeitado
    on public.zee_t_pedido_acesso_investidor (id_ordem, id_investidor)
    where id_ordem is not null
        and coalesce(dm_estado, '') <> 'REJEITADO'
        and dm_tipo_pedido = 'REPRES_INVESTIDOR';
