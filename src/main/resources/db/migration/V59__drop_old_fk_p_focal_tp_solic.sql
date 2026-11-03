-- Remover a FK antiga; a associacao usa diretamente zee_t_tp_solicitacao.
ALTER TABLE public.zee_t_p_focal_tp_solic
    DROP CONSTRAINT IF EXISTS zee_t_p_focal_tp_solic_zee_t_tp_ent_tp_solic_fk;
