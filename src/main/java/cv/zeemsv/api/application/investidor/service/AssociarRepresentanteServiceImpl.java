package cv.zeemsv.api.application.investidor.service;

import cv.zeemsv.api.application.domain.DomainDescriptionHelper;
import cv.zeemsv.api.application.geografia.service.NacionalidadeResolver;
import cv.zeemsv.api.application.investidor.dto.AssociarRepresentanteRequestDTO;
import cv.zeemsv.api.application.investidor.dto.RepresentanteInvestidorResponseDTO;
import cv.zeemsv.api.domain.documento.business.DocumentViewerUrlService;
import cv.zeemsv.api.domain.user.business.UserBus;
import cv.zeemsv.api.domain.user.model.UserModel;
import cv.zeemsv.api.exceptions.BusinessException;
import cv.zeemsv.api.infrastructure.entity.ZeeTOrdemEntity;
import cv.zeemsv.api.infrastructure.entity.ZeeTRepresInvestidorEntity;
import cv.zeemsv.api.infrastructure.entity.ZeeTSocioRepresEntity;
import cv.zeemsv.api.infrastructure.repository.ZeeTInvestidorRepository;
import cv.zeemsv.api.infrastructure.repository.ZeeTOrdemRepository;
import cv.zeemsv.api.infrastructure.repository.ZeeTRepresInvestidorRepository;
import cv.zeemsv.api.infrastructure.repository.ZeeTSocioRepresRepository;
import cv.zeemsv.api.utils.Messages;
import cv.zeemsv.api.utils.enums.LoginProvider;
import cv.zeemsv.api.utils.enums.UserStatus;
import jakarta.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class AssociarRepresentanteServiceImpl implements AssociarRepresentanteService {
    private static final String ESTADO_ATIVO = "A";
    private static final String NAO = "N";
    private static final String TIPO_SOCIO = "SOCIO";
    private static final String TIPO_SOCIO_GERENTE = "SOCIOGERENTE";

    private final ZeeTInvestidorRepository investidorRepository;
    private final ZeeTSocioRepresRepository socioRepresRepository;
    private final ZeeTOrdemRepository ordemRepository;
    private final ZeeTRepresInvestidorRepository represInvestidorRepository;
    private final UserBus userBus;
    private final DomainDescriptionHelper domainHelper;
    private final NacionalidadeResolver nacionalidadeResolver;
    private final DocumentViewerUrlService documentViewerUrlService;

    @Override
    @Transactional
    public RepresentanteInvestidorResponseDTO associar(Integer idInvestidor, AssociarRepresentanteRequestDTO dto) {
        investidorRepository.findById(idInvestidor)
            .orElseThrow(() -> new EntityNotFoundException("Investidor nao encontrado."));

        ZeeTOrdemEntity ordem = findOrdem(dto.getIdOrdem());
        ZeeTSocioRepresEntity socioRepres = ordem == null ? findSocioRepres(dto) : null;

        Integer idSocioRepres = socioRepres != null ? socioRepres.getId() : null;
        Integer idOrdem = ordem != null ? ordem.getId() : null;
        ZeeTRepresInvestidorEntity represInvestidor = represInvestidorRepository
            .findAssociation(idInvestidor, idSocioRepres, idOrdem)
            .orElseGet(ZeeTRepresInvestidorEntity::new);

        validatePrincipal(idInvestidor, dto, represInvestidor.getId());
        if (ordem == null) {
            validateUniqueSocioFields(dto, idSocioRepres);
        }

        UserModel user = findOrCreateUser(dto);
        if (socioRepres != null) {
            socioRepres = saveSocioRepres(idInvestidor, dto, user, socioRepres);
            idSocioRepres = socioRepres.getId();
        }

        represInvestidor.setIdInvestidor(idInvestidor);
        represInvestidor.setIdSocioRepres(idSocioRepres);
        represInvestidor.setIdOrdem(idOrdem);
        represInvestidor.setDmEstado(ESTADO_ATIVO);
        represInvestidor.setFlagSocio(isSocio(dto));
        represInvestidor.setDmTpRepresentante(trim(dto.getTipo()));
        represInvestidor.setUserRegisto(resolveUserRegisto(dto, user));
        if (represInvestidor.getDataRegisto() == null) {
            represInvestidor.setDataRegisto(LocalDate.now());
        }
        if (user != null) {
            represInvestidor.setIdUser(user.getId());
        }
        represInvestidor.setFlagRepresentante(true);
        represInvestidor.setDmPrincipal(StringUtils.hasText(dto.getPrincipal()) ? trim(dto.getPrincipal()) : NAO);

        return toResponse(represInvestidorRepository.save(represInvestidor), socioRepres, ordem);
    }

    private ZeeTSocioRepresEntity saveSocioRepres(
        Integer idInvestidor,
        AssociarRepresentanteRequestDTO dto,
        UserModel user,
        ZeeTSocioRepresEntity socioRepres
    ) {
        socioRepres.setIdInvestidor(idInvestidor);
        socioRepres.setNome(trim(dto.getNome()));
        socioRepres.setNacionalidade(trim(dto.getNacionalidade()));
        socioRepres.setNif(trim(dto.getNif()));
        socioRepres.setTipoDoc(trim(dto.getTipoDoc()));
        socioRepres.setNrDoc(trim(dto.getNrDoc()));
        socioRepres.setTelefone(dto.getTelefone());
        socioRepres.setEmail(StringUtils.hasText(dto.getEmail()) ? trim(dto.getEmail()) : "");
        socioRepres.setIndicativoPais(trim(dto.getIndicativoPais()));
        socioRepres.setTelemovel(dto.getTelemovel());
        socioRepres.setFotoUrl(trim(dto.getFotoUrl()));
        socioRepres.setEstado(ESTADO_ATIVO);
        if (socioRepres.getDateCreate() == null) {
            socioRepres.setDateCreate(LocalDate.now());
        }
        socioRepres.setUserCreate(resolveUserRegisto(dto, user));
        socioRepres.setDmTpRepresentante(trim(dto.getTipo()));
        socioRepres.setFlagRepresentante(true);
        socioRepres.setDmPrincipal(StringUtils.hasText(dto.getPrincipal()) ? trim(dto.getPrincipal()) : NAO);
        if (user != null) {
            socioRepres.setIdUser(user.getId());
        }
        return socioRepresRepository.save(socioRepres);
    }

    private ZeeTSocioRepresEntity findSocioRepres(AssociarRepresentanteRequestDTO dto) {
        if (dto.getIdSocioRepres() != null) {
            return socioRepresRepository.findById(dto.getIdSocioRepres())
                .orElseThrow(() -> new EntityNotFoundException("Socio/representante nao encontrado."));
        }
        return new ZeeTSocioRepresEntity();
    }

    private void validatePrincipal(Integer idInvestidor, AssociarRepresentanteRequestDTO dto, Integer idRepresentante) {
        if (isPrincipal(dto.getPrincipal()) && represInvestidorRepository.countOutrosPrincipaisAtivos(idInvestidor, idRepresentante) > 0) {
            throw new BusinessException("Investidor ja possui um representante principal.",
                new RuntimeException("Investidor ja possui um representante principal."));
        }
    }

    private void validateUniqueSocioFields(AssociarRepresentanteRequestDTO dto, Integer idSocioRepres) {
        if (hasOtherSocioWithNrDoc(dto.getNrDoc(), idSocioRepres)) {
            throw new BusinessException("Ja existe socio/representante com este numero de documento.",
                new RuntimeException("Ja existe socio/representante com este numero de documento."));
        }
        if (hasOtherSocioWithNif(dto.getNif(), idSocioRepres)) {
            throw new BusinessException("Ja existe socio/representante com este NIF.",
                new RuntimeException("Ja existe socio/representante com este NIF."));
        }
        if (hasOtherSocioWithEmail(dto.getEmail(), idSocioRepres)) {
            throw new BusinessException("Ja existe socio/representante com este email.",
                new RuntimeException("Ja existe socio/representante com este email."));
        }
    }

    private boolean hasOtherSocioWithNrDoc(String nrDoc, Integer idSocioRepres) {
        return StringUtils.hasText(nrDoc) && socioRepresRepository.findByNrDoc(trim(nrDoc)).stream()
            .anyMatch(socio -> !Objects.equals(socio.getId(), idSocioRepres));
    }

    private boolean hasOtherSocioWithNif(String nif, Integer idSocioRepres) {
        return StringUtils.hasText(nif) && socioRepresRepository.findByNif(trim(nif)).stream()
            .anyMatch(socio -> !Objects.equals(socio.getId(), idSocioRepres));
    }

    private boolean hasOtherSocioWithEmail(String email, Integer idSocioRepres) {
        return StringUtils.hasText(email) && socioRepresRepository.findByEmailIgnoreCase(trim(email)).stream()
            .anyMatch(socio -> !Objects.equals(socio.getId(), idSocioRepres));
    }

    private boolean isPrincipal(String principal) {
        if (!StringUtils.hasText(principal)) {
            return false;
        }
        String value = trim(principal);
        return "S".equalsIgnoreCase(value) || "SIM".equalsIgnoreCase(value);
    }

    private ZeeTOrdemEntity findOrdem(Integer idOrdem) {
        if (idOrdem == null) {
            return null;
        }
        return ordemRepository.findById(idOrdem)
            .orElseThrow(() -> new EntityNotFoundException("Ordem nao encontrada."));
    }

    private UserModel findOrCreateUser(AssociarRepresentanteRequestDTO dto) {
        if (!StringUtils.hasText(dto.getEmail())) {
            return null;
        }
        String email = trim(dto.getEmail()).toLowerCase();
        UserModel user = userBus.findByEmail(email).orElseGet(() -> UserModel.builder()
            .email(email)
            .name(trim(dto.getNome()))
            .provider(LoginProvider.LOCAL.name())
            .build());
        user.setEmail(email);
        user.setName(trim(dto.getNome()));
        user.setStatus(UserStatus.ATIVO);
        user.setProvider(StringUtils.hasText(user.getProvider()) ? user.getProvider() : LoginProvider.LOCAL.name());
        return userBus.save(user);
    }

    private BigDecimal resolveUserRegisto(AssociarRepresentanteRequestDTO dto, UserModel user) {
        if (dto.getUserRegisto() != null) {
            return dto.getUserRegisto();
        }
        if (user != null && user.getId() != null) {
            return BigDecimal.valueOf(user.getId());
        }
        throw new BusinessException(Messages.USER_NOT_FOUND, new RuntimeException(Messages.USER_NOT_FOUND));
    }

    private boolean isSocio(AssociarRepresentanteRequestDTO dto) {
        String tipo = trim(dto.getTipo());
        return dto.getIdSocioRepres() != null
            || TIPO_SOCIO.equalsIgnoreCase(tipo)
            || TIPO_SOCIO_GERENTE.equalsIgnoreCase(tipo);
    }

    private RepresentanteInvestidorResponseDTO toResponse(
        ZeeTRepresInvestidorEntity represInvestidor,
        ZeeTSocioRepresEntity socioRepres,
        ZeeTOrdemEntity ordem
    ) {
        RepresentanteInvestidorResponseDTO dto = new RepresentanteInvestidorResponseDTO();
        dto.setId(represInvestidor.getId());
        dto.setIdInvestidor(represInvestidor.getIdInvestidor());
        dto.setIdSocioRepres(represInvestidor.getIdSocioRepres());
        dto.setIdOrdem(represInvestidor.getIdOrdem());
        dto.setDmTpRepresentante(represInvestidor.getDmTpRepresentante());
        dto.setDmTpRepresentanteDesc(domainHelper.describe(DomainDescriptionHelper.TIPO_REPRESENTANTE, represInvestidor.getDmTpRepresentante()));
        dto.setFlagRepresentante(represInvestidor.getFlagRepresentante());
        dto.setFlagSocio(represInvestidor.getFlagSocio());
        dto.setDmPrincipal(represInvestidor.getDmPrincipal());
        dto.setDmPrincipalDesc(domainHelper.describe(DomainDescriptionHelper.SIM_NAO, represInvestidor.getDmPrincipal()));
        dto.setDmEstado(represInvestidor.getDmEstado());
        dto.setDmEstadoDesc(domainHelper.describe(DomainDescriptionHelper.ESTADO, represInvestidor.getDmEstado()));
        dto.setDataRegisto(represInvestidor.getDataRegisto());
        dto.setUserRegisto(represInvestidor.getUserRegisto());
        dto.setIdUser(represInvestidor.getIdUser());

        if (socioRepres != null) {
            dto.setNome(socioRepres.getNome());
            String nacionalidade = socioRepres.getNacionalidade();
            dto.setNacionalidade(nacionalidadeResolver.resolveDescricao(nacionalidade));
            dto.setNacionalidadeId(nacionalidadeResolver.resolveId(nacionalidade));
            dto.setNif(socioRepres.getNif());
            dto.setTipoDoc(socioRepres.getTipoDoc());
            dto.setNrDoc(socioRepres.getNrDoc());
            dto.setTelefone(socioRepres.getTelefone());
            dto.setTelemovel(socioRepres.getTelemovel());
            dto.setEmail(socioRepres.getEmail());
            dto.setFotoUrl(StringUtils.hasText(socioRepres.getFotoUrl()) ? socioRepres.getFotoUrl() : documentViewerUrlService.toViewerUrl(socioRepres.getFotoPath()));
            dto.setFotoPath(null);
            dto.setIndicativoPais(socioRepres.getIndicativoPais());
        } else if (ordem != null) {
            dto.setNome(ordem.getNome());
            String nacionalidade = ordem.getNacionalidade();
            dto.setNacionalidade(nacionalidadeResolver.resolveDescricao(nacionalidade));
            dto.setNacionalidadeId(nacionalidadeResolver.resolveId(nacionalidade));
            dto.setNif(ordem.getNif() != null ? ordem.getNif().toPlainString() : null);
            dto.setTipoDoc(ordem.getDmTpDoc());
            dto.setNrDoc(ordem.getNrDocumento());
            dto.setTelemovel(ordem.getTelemovel());
            dto.setEmail(ordem.getEmail());
            dto.setIndicativoPais(ordem.getIndicativoPais());
        }
        dto.setTipoDocDesc(domainHelper.describe(DomainDescriptionHelper.TIPO_DOCUMENTO, dto.getTipoDoc()));
        return dto;
    }

    private String trim(String value) {
        return value != null ? value.trim() : null;
    }
}
