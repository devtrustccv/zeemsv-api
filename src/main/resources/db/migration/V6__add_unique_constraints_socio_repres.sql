create unique index if not exists ux_zee_t_socio_repres_nif
    on public.zee_t_socio_repres (nif)
    where nif is not null and trim(nif) <> '';

create unique index if not exists ux_zee_t_socio_repres_nr_doc
    on public.zee_t_socio_repres (nr_doc)
    where nr_doc is not null and trim(nr_doc) <> '';
