alter table if exists zee_t_mensagem_interacao
    add column if not exists id_relacao integer,
    add column if not exists tp_relacao varchar(100);

update zee_t_mensagem_interacao
set id_relacao = id,
    tp_relacao = 'INTERACAO_RESPOSTA'
where id_relacao is null
   or tp_relacao is null;
