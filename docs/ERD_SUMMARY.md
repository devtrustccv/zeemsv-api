# ZEEMSV ERD Summary

Total de tabelas identificadas no ficheiro `zeemsv_v2.erd`: 47.

- t_historico_pedido -> THistoricoPedidoEntity / THistoricoPedidoRepository
- t_pedido -> TPedidoEntity / TPedidoRepository
- tbl_cae -> TblCaeEntity / TblCaeRepository
- zee_t_atividade -> ZeeTAtividadeEntity / ZeeTAtividadeRepository
- zee_t_config_template_notif -> ZeeTConfigTemplateNotifEntity / ZeeTConfigTemplateNotifRepository
- zee_t_controlo_acesso -> ZeeTControloAcessoEntity / ZeeTControloAcessoRepository
- zee_t_doc_relacao -> ZeeTDocRelacaoEntity / ZeeTDocRelacaoRepository
- zee_t_emails -> ZeeTEmailsEntity / ZeeTEmailsRepository
- zee_t_entidade_externa -> ZeeTEntidadeExternaEntity / ZeeTEntidadeExternaRepository
- zee_t_geografia -> ZeeTGeografiaEntity / ZeeTGeografiaRepository
- zee_t_inf_projeto -> ZeeTInfProjetoEntity / ZeeTInfProjetoRepository
- zee_t_investidor -> ZeeTInvestidorEntity / ZeeTInvestidorRepository
- zee_t_investidor_cae -> ZeeTInvestidorCaeEntity / ZeeTInvestidorCaeRepository
- zee_t_lead -> ZeeTLeadEntity / ZeeTLeadRepository
- zee_t_lead_fases -> ZeeTLeadFasesEntity / ZeeTLeadFasesRepository
- zee_t_lead_promotor -> ZeeTLeadPromotorEntity / ZeeTLeadPromotorRepository
- zee_t_lote -> ZeeTLoteEntity / ZeeTLoteRepository
- zee_t_lote_enquad -> ZeeTLoteEnquadEntity / ZeeTLoteEnquadRepository
- zee_t_lote_proj -> ZeeTLoteProjEntity / ZeeTLoteProjRepository
- zee_t_lote_proprietario -> ZeeTLoteProprietarioEntity / ZeeTLoteProprietarioRepository
- zee_t_lote_relacao -> ZeeTLoteRelacaoEntity / ZeeTLoteRelacaoRepository
- zee_t_ordem -> ZeeTOrdemEntity / ZeeTOrdemRepository
- zee_t_p_focal_tp_solic -> ZeeTPFocalTpSolicEntity / ZeeTPFocalTpSolicRepository
- zee_t_pagamento -> ZeeTPagamentoEntity / ZeeTPagamentoRepository
- zee_t_param_report -> ZeeTParamReportEntity / ZeeTParamReportRepository
- zee_t_park -> ZeeTParkEntity / ZeeTParkRepository
- zee_t_ponto_focal -> ZeeTPontoFocalEntity / ZeeTPontoFocalRepository
- zee_t_proj_atividade -> ZeeTProjAtividadeEntity / ZeeTProjAtividadeRepository
- zee_t_proj_invest -> ZeeTProjInvestEntity / ZeeTProjInvestRepository
- zee_t_proj_lote_enquad -> ZeeTProjLoteEnquadEntity / ZeeTProjLoteEnquadRepository
- zee_t_projeto_repres -> ZeeTProjetoRepresEntity / ZeeTProjetoRepresRepository
- zee_t_repres_investidor -> ZeeTRepresInvestidorEntity / ZeeTRepresInvestidorRepository
- zee_t_socio_repres -> ZeeTSocioRepresEntity / ZeeTSocioRepresRepository
- zee_t_solicitacao -> ZeeTSolicitacaoEntity / ZeeTSolicitacaoRepository
- zee_t_solicitacao_doc -> ZeeTSolicitacaoDocEntity / ZeeTSolicitacaoDocRepository
- zee_t_solicitacao_taxa -> ZeeTSolicitacaoTaxaEntity / ZeeTSolicitacaoTaxaRepository
- zee_t_taxa -> ZeeTTaxaEntity / ZeeTTaxaRepository
- zee_t_tp_doc -> ZeeTTpDocEntity / ZeeTTpDocRepository
- zee_t_tp_ent_tp_solic -> ZeeTTpEntTpSolicEntity / ZeeTTpEntTpSolicRepository
- zee_t_tp_solic_relacao -> ZeeTTpSolicRelacaoEntity / ZeeTTpSolicRelacaoRepository
- zee_t_tp_solic_taxa -> ZeeTTpSolicTaxaEntity / ZeeTTpSolicTaxaRepository
- zee_t_tp_solic_tp_doc -> ZeeTTpSolicTpDocEntity / ZeeTTpSolicTpDocRepository
- zee_t_tp_solicitacao -> ZeeTTpSolicitacaoEntity / ZeeTTpSolicitacaoRepository
- zee_t_user -> ZeeTUserEntity / ZeeTUserRepository
- zee_t_zona -> ZeeTZonaEntity / ZeeTZonaRepository
- zee_t_zona_cord -> ZeeTZonaCordEntity / ZeeTZonaCordRepository
- zee_t_zona_enquad -> ZeeTZonaEnquadEntity / ZeeTZonaEnquadRepository

> Nota: o ficheiro ERD fornecido contém nomes de entidades e relações, mas não expõe o DDL completo das colunas. Por isso, as entidades foram criadas como esqueleto inicial. O próximo passo correto é gerar as colunas a partir do `CREATE TABLE` real ou introspeção direta da base PostgreSQL.
