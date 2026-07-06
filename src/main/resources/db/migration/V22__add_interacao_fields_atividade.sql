alter table if exists public.zee_t_atividade
    add column if not exists tipo_interacao varchar(255),
    add column if not exists dm_estado_interacao varchar(50),
    add column if not exists assunto_interacao varchar(500),
    add column if not exists mensagem_interacao varchar(4000),
    add column if not exists user_resposta integer,
    add column if not exists data_resposta date,
    add column if not exists id_user integer,
    add column if not exists email varchar(255),
    add column if not exists nome varchar(255);
