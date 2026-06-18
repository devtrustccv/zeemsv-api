DO $$
BEGIN
    IF EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_schema = 'public'
          AND table_name = 'zee_t_atividade'
          AND column_name = 'id_investidor'
          AND data_type = 'numeric'
    ) THEN
        IF EXISTS (
            SELECT 1
            FROM public.zee_t_atividade
            WHERE id_investidor IS NOT NULL
              AND id_investidor <> trunc(id_investidor)
        ) THEN
            RAISE EXCEPTION 'public.zee_t_atividade.id_investidor contains non-integer numeric values';
        END IF;

        ALTER TABLE public.zee_t_atividade
            ALTER COLUMN id_investidor TYPE integer
            USING id_investidor::integer;
    END IF;
END $$;
