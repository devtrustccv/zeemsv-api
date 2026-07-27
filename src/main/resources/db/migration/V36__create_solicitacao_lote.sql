create table if not exists public.zee_t_solicitacao_lote (
    id serial4 primary key,
    id_solicitacao integer not null,
    id_lote integer not null,
    constraint fk_solicitacao_lote_solicitacao
        foreign key (id_solicitacao)
        references public.zee_t_solicitacao (id),
    constraint fk_solicitacao_lote_lote
        foreign key (id_lote)
        references public.zee_t_lote (id),
    constraint ux_solicitacao_lote
        unique (id_solicitacao, id_lote)
);

create index if not exists idx_solicitacao_lote_id_solicitacao
    on public.zee_t_solicitacao_lote (id_solicitacao);

create index if not exists idx_solicitacao_lote_id_lote
    on public.zee_t_solicitacao_lote (id_lote);
