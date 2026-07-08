create table if not exists zee_t_mensagem_interacao (
    id serial4 primary key,
    id_interacao integer not null,
    mensagem varchar(4000) not null,
    user_envio integer,
    data_envio timestamp not null,
    path_doc varchar(1000),
    constraint fk_mensagem_interacao_atividade
        foreign key (id_interacao)
        references zee_t_atividade (id)
);

create index if not exists idx_mensagem_interacao_id_interacao
    on zee_t_mensagem_interacao (id_interacao);
