create table if not exists public.zee_t_solicitacao_cobranca (
    id serial4 primary key,
    id_solicitacao integer not null,
    id_cobranca integer not null,
    constraint fk_solicitacao_cobranca_solicitacao
        foreign key (id_solicitacao)
        references public.zee_t_solicitacao (id),
    constraint fk_solicitacao_cobranca_cobranca
        foreign key (id_cobranca)
        references public.zee_t_cobranca (id),
    constraint ux_solicitacao_cobranca
        unique (id_solicitacao, id_cobranca)
);

create index if not exists idx_solicitacao_cobranca_id_solicitacao
    on public.zee_t_solicitacao_cobranca (id_solicitacao);

create index if not exists idx_solicitacao_cobranca_id_cobranca
    on public.zee_t_solicitacao_cobranca (id_cobranca);

insert into public.zee_t_solicitacao_cobranca (id_solicitacao, id_cobranca)
select distinct c.id_solicitacao, c.id
from public.zee_t_cobranca c
join public.zee_t_solicitacao s on s.id = c.id_solicitacao
where c.id_solicitacao is not null
on conflict (id_solicitacao, id_cobranca) do nothing;
