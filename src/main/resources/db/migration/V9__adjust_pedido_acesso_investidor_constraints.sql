drop index if exists public.ux_zee_t_pedido_acesso_investidor_utilizador_investidor;

create unique index if not exists ux_zee_t_pedido_acesso_investidor_utilizador_investidor_nao_rejeitado
    on public.zee_t_pedido_acesso_investidor (id_utilizador, id_investidor)
    where coalesce(dm_estado, '') <> 'REJEITADO';

do $$
begin
    if not exists (
        select 1
        from pg_constraint
        where conname = 'fk_pedido_acesso_investidor_utilizador'
    ) then
        alter table if exists public.zee_t_pedido_acesso_investidor
            add constraint fk_pedido_acesso_investidor_utilizador
            foreign key (id_utilizador)
            references public.zee_t_user (id)
            not valid;
    end if;
end $$;

do $$
begin
    if not exists (
        select 1
        from pg_constraint
        where conname = 'fk_pedido_acesso_investidor_investidor'
    ) then
        alter table if exists public.zee_t_pedido_acesso_investidor
            add constraint fk_pedido_acesso_investidor_investidor
            foreign key (id_investidor)
            references public.zee_t_investidor (id)
            not valid;
    end if;
end $$;
