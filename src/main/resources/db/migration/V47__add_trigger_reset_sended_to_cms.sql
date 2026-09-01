create or replace function public.zee_f_mark_tp_solicitacao_pending_cms(p_id_tp_solicitacao integer)
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

create or replace function public.zee_trg_mark_cms_pending_by_id_tp_solic()
returns trigger
language plpgsql
as $$
begin
    if TG_OP = 'DELETE' then
        perform public.zee_f_mark_tp_solicitacao_pending_cms(OLD.id_tp_solic);
        return OLD;
    end if;

    perform public.zee_f_mark_tp_solicitacao_pending_cms(NEW.id_tp_solic);

    if TG_OP = 'UPDATE' and NEW.id_tp_solic is distinct from OLD.id_tp_solic then
        perform public.zee_f_mark_tp_solicitacao_pending_cms(OLD.id_tp_solic);
    end if;

    return NEW;
end;
$$;

create or replace function public.zee_trg_mark_cms_pending_by_id_solicitacao()
returns trigger
language plpgsql
as $$
declare
    v_id_tp_solicitacao integer;
begin
    if TG_OP in ('INSERT', 'UPDATE') then
        select s.id_tp_solicitacao
          into v_id_tp_solicitacao
          from public.zee_t_solicitacao s
         where s.id = NEW.id_solicitacao;

        perform public.zee_f_mark_tp_solicitacao_pending_cms(v_id_tp_solicitacao);
    end if;

    if TG_OP in ('UPDATE', 'DELETE') then
        select s.id_tp_solicitacao
          into v_id_tp_solicitacao
          from public.zee_t_solicitacao s
         where s.id = OLD.id_solicitacao;

        perform public.zee_f_mark_tp_solicitacao_pending_cms(v_id_tp_solicitacao);
    end if;

    if TG_OP = 'DELETE' then
        return OLD;
    end if;

    return NEW;
end;
$$;

create or replace function public.zee_trg_mark_cms_pending_by_solicitacao()
returns trigger
language plpgsql
as $$
begin
    if TG_OP = 'DELETE' then
        perform public.zee_f_mark_tp_solicitacao_pending_cms(OLD.id_tp_solicitacao);
        return OLD;
    end if;

    perform public.zee_f_mark_tp_solicitacao_pending_cms(NEW.id_tp_solicitacao);

    if TG_OP = 'UPDATE' and NEW.id_tp_solicitacao is distinct from OLD.id_tp_solicitacao then
        perform public.zee_f_mark_tp_solicitacao_pending_cms(OLD.id_tp_solicitacao);
    end if;

    return NEW;
end;
$$;

create or replace function public.zee_trg_mark_cms_pending_by_tp_solicitacao()
returns trigger
language plpgsql
as $$
begin
    if TG_OP = 'DELETE' then
        perform public.zee_f_mark_tp_solicitacao_pending_cms(OLD.id);
        return OLD;
    end if;

    perform public.zee_f_mark_tp_solicitacao_pending_cms(NEW.id);
    return NEW;
end;
$$;

drop trigger if exists trg_zee_t_tp_solicitacao_mark_cms_pending on public.zee_t_tp_solicitacao;
create trigger trg_zee_t_tp_solicitacao_mark_cms_pending
after update of nome, dm_tipo_solicitacao, dm_categoria, descricao, msg_pedido, prazo_dia,
                flag_obrigatorio, codigo, dm_estado, id_ent_externa, possui_taxa, possui_onboarding
on public.zee_t_tp_solicitacao
for each row
when (
    OLD.nome is distinct from NEW.nome
    or OLD.dm_tipo_solicitacao is distinct from NEW.dm_tipo_solicitacao
    or OLD.dm_categoria is distinct from NEW.dm_categoria
    or OLD.descricao is distinct from NEW.descricao
    or OLD.msg_pedido is distinct from NEW.msg_pedido
    or OLD.prazo_dia is distinct from NEW.prazo_dia
    or OLD.flag_obrigatorio is distinct from NEW.flag_obrigatorio
    or OLD.codigo is distinct from NEW.codigo
    or OLD.dm_estado is distinct from NEW.dm_estado
    or OLD.id_ent_externa is distinct from NEW.id_ent_externa
    or OLD.possui_taxa is distinct from NEW.possui_taxa
    or OLD.possui_onboarding is distinct from NEW.possui_onboarding
)
execute function public.zee_trg_mark_cms_pending_by_tp_solicitacao();

drop trigger if exists trg_zee_t_solicitacao_mark_cms_pending on public.zee_t_solicitacao;
create trigger trg_zee_t_solicitacao_mark_cms_pending
after insert or update or delete on public.zee_t_solicitacao
for each row
execute function public.zee_trg_mark_cms_pending_by_solicitacao();

drop trigger if exists trg_zee_t_solic_onboarding_mark_cms_pending on public.zee_t_solic_onboarding;
create trigger trg_zee_t_solic_onboarding_mark_cms_pending
after insert or update or delete on public.zee_t_solic_onboarding
for each row
execute function public.zee_trg_mark_cms_pending_by_id_tp_solic();

drop trigger if exists trg_zee_t_tp_solic_relacao_mark_cms_pending on public.zee_t_tp_solic_relacao;
create trigger trg_zee_t_tp_solic_relacao_mark_cms_pending
after insert or update or delete on public.zee_t_tp_solic_relacao
for each row
execute function public.zee_trg_mark_cms_pending_by_id_tp_solic();

drop trigger if exists trg_zee_t_tp_solic_repre_mark_cms_pending on public.zee_t_tp_solic_repre;
create trigger trg_zee_t_tp_solic_repre_mark_cms_pending
after insert or update or delete on public.zee_t_tp_solic_repre
for each row
execute function public.zee_trg_mark_cms_pending_by_id_tp_solic();

drop trigger if exists trg_zee_t_tp_solic_taxa_mark_cms_pending on public.zee_t_tp_solic_taxa;
create trigger trg_zee_t_tp_solic_taxa_mark_cms_pending
after insert or update or delete on public.zee_t_tp_solic_taxa
for each row
execute function public.zee_trg_mark_cms_pending_by_id_tp_solic();

drop trigger if exists trg_zee_t_tp_solic_tp_doc_mark_cms_pending on public.zee_t_tp_solic_tp_doc;
create trigger trg_zee_t_tp_solic_tp_doc_mark_cms_pending
after insert or update or delete on public.zee_t_tp_solic_tp_doc
for each row
execute function public.zee_trg_mark_cms_pending_by_id_tp_solic();

drop trigger if exists trg_zee_t_tp_ent_tp_solic_mark_cms_pending on public.zee_t_tp_ent_tp_solic;
create trigger trg_zee_t_tp_ent_tp_solic_mark_cms_pending
after insert or update or delete on public.zee_t_tp_ent_tp_solic
for each row
execute function public.zee_trg_mark_cms_pending_by_id_tp_solic();

drop trigger if exists trg_zee_t_solicitacao_doc_mark_cms_pending on public.zee_t_solicitacao_doc;
create trigger trg_zee_t_solicitacao_doc_mark_cms_pending
after insert or update or delete on public.zee_t_solicitacao_doc
for each row
execute function public.zee_trg_mark_cms_pending_by_id_solicitacao();

drop trigger if exists trg_zee_t_solicitacao_taxa_mark_cms_pending on public.zee_t_solicitacao_taxa;
create trigger trg_zee_t_solicitacao_taxa_mark_cms_pending
after insert or update or delete on public.zee_t_solicitacao_taxa
for each row
execute function public.zee_trg_mark_cms_pending_by_id_solicitacao();

drop trigger if exists trg_zee_t_solicitacao_lote_mark_cms_pending on public.zee_t_solicitacao_lote;
create trigger trg_zee_t_solicitacao_lote_mark_cms_pending
after insert or update or delete on public.zee_t_solicitacao_lote
for each row
execute function public.zee_trg_mark_cms_pending_by_id_solicitacao();

drop trigger if exists trg_zee_t_solicitacao_cobranca_mark_cms_pending on public.zee_t_solicitacao_cobranca;
create trigger trg_zee_t_solicitacao_cobranca_mark_cms_pending
after insert or update or delete on public.zee_t_solicitacao_cobranca
for each row
execute function public.zee_trg_mark_cms_pending_by_id_solicitacao();

drop trigger if exists trg_zee_t_cobranca_mark_cms_pending on public.zee_t_cobranca;
create trigger trg_zee_t_cobranca_mark_cms_pending
after insert or update or delete on public.zee_t_cobranca
for each row
execute function public.zee_trg_mark_cms_pending_by_id_solicitacao();

drop trigger if exists trg_zee_t_pagamento_mark_cms_pending on public.zee_t_pagamento;
create trigger trg_zee_t_pagamento_mark_cms_pending
after insert or update or delete on public.zee_t_pagamento
for each row
execute function public.zee_trg_mark_cms_pending_by_id_solicitacao();
