alter table if exists public.zee_t_taxa_condicoes
    add column if not exists id_zona integer;

do $$
begin
    if not exists (
        select 1
        from pg_constraint
        where conname = 'fk_zee_t_taxa_condicoes_zona'
    ) then
        alter table public.zee_t_taxa_condicoes
            add constraint fk_zee_t_taxa_condicoes_zona
                foreign key (id_zona)
                references public.zee_t_zona (id);
    end if;
end $$;
