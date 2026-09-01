create or replace function public.zee_f_mark_tp_solicitacao_pending_cms(p_id_tp_solicitacao numeric)
returns void
language plpgsql
as $$
begin
    if p_id_tp_solicitacao is null then
        return;
    end if;

    update public.zee_t_tp_solicitacao
       set sended_to_cms = false
     where id = p_id_tp_solicitacao
       and sended_to_cms is distinct from false;
end;
$$;
