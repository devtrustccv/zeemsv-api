alter table if exists public.zee_t_proj_invest
    add column if not exists user_update numeric,
    add column if not exists date_update date;
