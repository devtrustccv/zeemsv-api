alter table if exists gestao_notificacao.t_notificacao
    add column if not exists data_leitura timestamp;
