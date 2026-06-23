alter table if exists public.zee_t_user
    add column if not exists password_hash varchar(255);
