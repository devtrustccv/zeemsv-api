alter table if exists public.zee_t_lote
    add column if not exists coordenadas jsonb;

alter table if exists public.zee_t_zona
    add column if not exists coordenadas jsonb;
