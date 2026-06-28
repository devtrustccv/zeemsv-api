package cv.zeemsv.api.application.investidor.service;

import cv.zeemsv.api.application.investidor.dto.PedidoAcessoInvestidorRequestDTO;
import cv.zeemsv.api.application.investidor.dto.PedidoAcessoInvestidorResponseDTO;
import cv.zeemsv.api.domain.documento.business.DocumentoBus;
import cv.zeemsv.api.domain.documento.dto.UploadDTO;
import cv.zeemsv.api.exceptions.BusinessException;
import cv.zeemsv.api.infrastructure.entity.ZeeTDocRelacaoEntity;
import cv.zeemsv.api.infrastructure.entity.ZeeTPedidoAcessoInvestidorEntity;
import cv.zeemsv.api.infrastructure.repository.ZeeTInvestidorRepository;
import cv.zeemsv.api.infrastructure.repository.ZeeTPedidoAcessoInvestidorRepository;
import cv.zeemsv.api.infrastructure.repository.ZeeTUserRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class PedidoAcessoInvestidorServiceImpl implements PedidoAcessoInvestidorService {
    private static final String TIPO_RELACAO_PEDIDO_ACESSO = "PEDIDO_ACESSO";
    private static final String NOME_FICHEIRO_COMPRAVATIVO = "ficheiro_compravativo";

    private final ZeeTPedidoAcessoInvestidorRepository repository;
    private final ZeeTUserRepository userRepository;
    private final ZeeTInvestidorRepository investidorRepository;
    private final DocumentoBus documentoBus;

    @Override
    @Transactional
    public PedidoAcessoInvestidorResponseDTO create(PedidoAcessoInvestidorRequestDTO dto, MultipartFile ficheiroCompravativo) {
        if (ficheiroCompravativo == null || ficheiroCompravativo.isEmpty()) {
            throw new BusinessException("O campo ficheiro_compravativo e obrigatorio.");
        }

        if (!userRepository.existsById(dto.getIdUser())) {
            throw new BusinessException("Utilizador nao encontrado.");
        }

        if (!investidorRepository.existsById(dto.getIdInvestidor())) {
            throw new BusinessException("Investidor nao encontrado.");
        }

        String dmEstado = trim(dto.getDmEstado());
        if (isNaoRejeitado(dmEstado)
            && repository.existsNaoRejeitadoByIdUtilizadorAndIdInvestidor(dto.getIdUser(), dto.getIdInvestidor())) {
            throw new BusinessException("Ja existe pedido de acesso para este utilizador e investidor.");
        }

        ZeeTPedidoAcessoInvestidorEntity entity = new ZeeTPedidoAcessoInvestidorEntity();
        entity.setIdUtilizador(dto.getIdUser());
        entity.setIdInvestidor(dto.getIdInvestidor());
        entity.setDmTpRepresentante(trim(dto.getDmTpRepresentante()));
        entity.setDmEstado(dmEstado);
        entity.setObs(trim(dto.getObs()));
        entity.setDataRegisto(LocalDate.now());

        try {
            ZeeTPedidoAcessoInvestidorEntity saved = repository.save(entity);
            UploadDTO upload = buildUpload(saved, ficheiroCompravativo);
            documentoBus.saveOrUpdate(upload, String.valueOf(dto.getIdUser()));
            saved.setFicheiroCompravativo(upload.getFullPath());
            return toResponse(repository.save(saved));
        } catch (DataIntegrityViolationException ex) {
            throw new BusinessException("Ja existe pedido de acesso para este utilizador e investidor.", ex);
        }
    }

    private String trim(String value) {
        return value != null ? value.trim() : null;
    }

    private boolean isNaoRejeitado(String dmEstado) {
        return dmEstado == null || !"REJEITADO".equalsIgnoreCase(dmEstado);
    }

    private UploadDTO buildUpload(ZeeTPedidoAcessoInvestidorEntity pedidoAcesso, MultipartFile ficheiroCompravativo) {
        ZeeTDocRelacaoEntity docRelacao = new ZeeTDocRelacaoEntity();
        docRelacao.setTipoRelacao(TIPO_RELACAO_PEDIDO_ACESSO);
        docRelacao.setIdRelacao(BigDecimal.valueOf(pedidoAcesso.getId()));

        String basePath = DocumentoBus.getBasePathForModuloOrObject(
            TIPO_RELACAO_PEDIDO_ACESSO,
            pedidoAcesso.getId().toString()
        );
        return new UploadDTO(ficheiroCompravativo, NOME_FICHEIRO_COMPRAVATIVO, basePath, docRelacao);
    }

    private PedidoAcessoInvestidorResponseDTO toResponse(ZeeTPedidoAcessoInvestidorEntity entity) {
        PedidoAcessoInvestidorResponseDTO dto = new PedidoAcessoInvestidorResponseDTO();
        dto.setId(entity.getId());
        dto.setIdUser(entity.getIdUtilizador());
        dto.setIdInvestidor(entity.getIdInvestidor());
        dto.setDmTpRepresentante(entity.getDmTpRepresentante());
        dto.setFicheiroCompravativo(entity.getFicheiroCompravativo());
        dto.setDmEstado(entity.getDmEstado());
        dto.setObs(entity.getObs());
        dto.setDataRegisto(entity.getDataRegisto());
        dto.setDataResposta(entity.getDataResposta());
        dto.setUserResposta(entity.getUserResposta());
        return dto;
    }
}
