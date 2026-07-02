alter table if exists public.zee_t_tp_solicitacao
    add column if not exists possui_onboarding boolean;
