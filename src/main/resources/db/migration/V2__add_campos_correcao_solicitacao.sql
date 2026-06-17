ALTER TABLE public.zee_t_solicitacao
    ADD COLUMN IF NOT EXISTS flag_correcao boolean DEFAULT false NULL,
    ADD COLUMN IF NOT EXISTS data_envio_correcao date NULL,
    ADD COLUMN IF NOT EXISTS data_fim_prevista_correcao date NULL,
    ADD COLUMN IF NOT EXISTS data_correcao date NULL,
    ADD COLUMN IF NOT EXISTS user_corecao varchar NULL;
