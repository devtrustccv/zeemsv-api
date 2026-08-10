alter table if exists public.zee_t_taxa
    add column if not exists user_registo varchar(255),
    add column if not exists data_registo date;

create table if not exists public.zee_t_taxa_condicoes (
    id serial primary key,
    id_taxa integer not null,
    valor varchar(255),
    desconto varchar(255),
    duracao varchar(255),
    zona varchar(255),
    reserva_apli varchar(255),
    edificio varchar(255),
    area_min varchar(255),
    area_max varchar(255),
    atividade varchar(255),
    pri_vistoria varchar(255),
    user_registo varchar(255),
    data_registo date,
    constraint fk_zee_t_taxa_condicoes_taxa
        foreign key (id_taxa)
        references public.zee_t_taxa (id)
);

create table if not exists public.zee_t_taxa_desconto (
    id serial primary key,
    id_taxa integer not null,
    duracao varchar(255),
    desconto varchar(255),
    user_registo varchar(255),
    data_registo date,
    constraint fk_zee_t_taxa_desconto_taxa
        foreign key (id_taxa)
        references public.zee_t_taxa (id)
);
