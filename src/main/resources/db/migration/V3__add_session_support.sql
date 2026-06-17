alter table if exists public.zee_t_user
    add column if not exists pessoa_id integer,
    add column if not exists sub_cmdcv varchar(255);

create table if not exists public.zee_t_session (
    id serial primary key,
    user_id integer not null,
    status varchar(50) not null,
    start_date timestamp not null,
    end_date timestamp,
    session_token varchar(4000) not null unique,
    provider varchar(100)
);
