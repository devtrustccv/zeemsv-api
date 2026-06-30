package cv.zeemsv.api.application.investidor.service;

import cv.zeemsv.api.application.domain.DomainDescriptionHelper;
import cv.zeemsv.api.application.geografia.service.NacionalidadeResolver;
import cv.zeemsv.api.application.investidor.dto.RepresentanteInvestidorResponseDTO;
import cv.zeemsv.api.application.investidor.dto.SocioRepresentanteRequestDTO;
import cv.zeemsv.api.application.investidor.dto.SocioRepresentanteResponseDTO;
import cv.zeemsv.api.domain.user.business.UserBus;
import cv.zeemsv.api.domain.user.model.UserModel;
import cv.zeemsv.api.exceptions.BusinessException;
import cv.zeemsv.api.infrastructure.entity.ZeeTSocioRepresEntity;
import cv.zeemsv.api.infrastructure.repository.ZeeTRepresInvestidorRepository;
import cv.zeemsv.api.infrastructure.repository.ZeeTSocioRepresRepository;
import cv.zeemsv.api.infrastructure.repository.projection.RepresentanteInvestidorProjection;
import cv.zeemsv.api.utils.Messages;
import cv.zeemsv.api.utils.enums.LoginProvider;
import cv.zeemsv.api.utils.enums.UserStatus;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class SocioRepresentanteServiceImpl implements SocioRepresentanteService {
    private static final String ESTADO_ATIVO = "A";
    private static final String ESTADO_PENDENTE = "PENDENTE";

    private final ZeeTSocioRepresRepository repository;
    private final ZeeTRepresInvestidorRepository represInvestidorRepository;
    private final DomainDescriptionHelper domainHelper;
    private final UserBus userBus;
    private final NacionalidadeResolver nacionalidadeResolver;

    @Override
    @Transactional
    public SocioRepresentanteResponseDTO create(SocioRepresentanteRequestDTO dto) {
        validateUniqueFields(dto);
        UserModel user = resolveUserByEmail(dto.getEmail(), dto.getNome(), UserStatus.ATIVO, true);

        return toResponse(repository.save(toEntity(dto, user, ESTADO_ATIVO)));
    }

    @Override
    @Transactional
    public SocioRepresentanteResponseDTO createPendente(SocioRepresentanteRequestDTO dto) {
        validateUniqueFields(dto);
        UserModel user = resolveUserByEmail(dto.getEmail(), dto.getNome(), UserStatus.PENDENTE, false);

        return toResponse(repository.save(toEntity(dto, user, ESTADO_PENDENTE)));
    }

    @Override
    @Transactional(readOnly = true)
    public List<RepresentanteInvestidorResponseDTO> findSociosByInvestidorId(Integer idInvestidor) {
        return represInvestidorRepository.findSociosByInvestidorId(idInvestidor)
            .stream()
            .map(this::toRepresentanteResponse)
            .toList();
    }

    private UserModel resolveUserByEmail(String email, String nome, UserStatus status, boolean mustExist) {
        String normalizedEmail = trim(email).toLowerCase();
        UserModel user = userBus.findByEmail(normalizedEmail).orElse(null);
        if (user == null) {
            if (mustExist) {
                throw new BusinessException(Messages.USER_NOT_FOUND, new RuntimeException(Messages.USER_NOT_FOUND));
            }
            user = UserModel.builder()
                .email(normalizedEmail)
                .name(trim(nome))
                .provider(LoginProvider.LOCAL.name())
                .build();
        }
        if (!StringUtils.hasText(user.getName())) {
            user.setName(trim(nome));
        }
        user.setStatus(status);
        user.setProvider(StringUtils.hasText(user.getProvider()) ? user.getProvider() : LoginProvider.LOCAL.name());
        return userBus.save(user);
    }

    private ZeeTSocioRepresEntity toEntity(SocioRepresentanteRequestDTO dto, UserModel user, String estado) {
        ZeeTSocioRepresEntity entity = new ZeeTSocioRepresEntity();
        entity.setNome(trim(dto.getNome()));
        entity.setNacionalidade(trim(dto.getNacionalidade()));
        entity.setNif(trim(dto.getNif()));
        entity.setTipoDoc(trim(dto.getTipoDoc()));
        entity.setNrDoc(trim(dto.getNrDoc()));
        entity.setTelefone(dto.getTelefone());
        entity.setTelemovel(dto.getTelemovel());
        entity.setEmail(trim(dto.getEmail()));
        entity.setEstado(estado);
        entity.setDateCreate(LocalDate.now());
        entity.setIndicativoPais(trim(dto.getIndicativoPais()));
        entity.setIdUser(user.getId());
        return entity;
    }

    private void validateUniqueFields(SocioRepresentanteRequestDTO dto) {
        if (StringUtils.hasText(dto.getNif()) && !repository.findByNif(trim(dto.getNif())).isEmpty()) {
            throw new BusinessException("Ja existe socio/representante com este NIF.",
                new RuntimeException("Ja existe socio/representante com este NIF."));
        }

        if (StringUtils.hasText(dto.getNrDoc()) && !repository.findByNrDoc(trim(dto.getNrDoc())).isEmpty()) {
            throw new BusinessException("Ja existe socio/representante com este numero de documento.",
                new RuntimeException("Ja existe socio/representante com este numero de documento."));
        }

        if (StringUtils.hasText(dto.getEmail()) && !repository.findByEmailIgnoreCase(trim(dto.getEmail())).isEmpty()) {
            throw new BusinessException("Ja existe socio/representante com este email.",
                new RuntimeException("Ja existe socio/representante com este email."));
        }
    }

    private String trim(String value) {
        return value != null ? value.trim() : null;
    }

    private SocioRepresentanteResponseDTO toResponse(ZeeTSocioRepresEntity entity) {
        SocioRepresentanteResponseDTO dto = new SocioRepresentanteResponseDTO();
        dto.setId(entity.getId());
        dto.setIdInvestidor(entity.getIdInvestidor());
        dto.setNome(entity.getNome());
        String nacionalidade = entity.getNacionalidade();
        dto.setNacionalidade(nacionalidadeResolver.resolveDescricao(nacionalidade));
        dto.setNacionalidadeId(nacionalidadeResolver.resolveId(nacionalidade));
        dto.setNif(entity.getNif());
        dto.setTipoDoc(entity.getTipoDoc());
        dto.setNrDoc(entity.getNrDoc());
        dto.setDmTpRepresentante(entity.getDmTpRepresentante());
        dto.setTelefone(entity.getTelefone());
        dto.setTelemovel(entity.getTelemovel());
        dto.setEmail(entity.getEmail());
        dto.setFlagSocio(entity.getFlagSocio());
        dto.setFlagRepresentante(entity.getFlagRepresentante());
        dto.setDmPrincipal(entity.getDmPrincipal());
        dto.setEstado(entity.getEstado());
        dto.setDateCreate(entity.getDateCreate());
        dto.setUserCreate(entity.getUserCreate());
        dto.setIndicativoPais(entity.getIndicativoPais());
        dto.setIdUser(entity.getIdUser());
        return dto;
    }

    private RepresentanteInvestidorResponseDTO toRepresentanteResponse(RepresentanteInvestidorProjection projection) {
        RepresentanteInvestidorResponseDTO dto = new RepresentanteInvestidorResponseDTO();
        dto.setId(projection.getId());
        dto.setIdInvestidor(projection.getIdInvestidor());
        dto.setIdSocioRepres(projection.getIdSocioRepres());
        dto.setIdOrdem(projection.getIdOrdem());
        dto.setDmTpRepresentante(projection.getDmTpRepresentante());
        dto.setDmTpRepresentanteDesc(domainHelper.describe(DomainDescriptionHelper.TIPO_REPRESENTANTE, projection.getDmTpRepresentante()));
        dto.setFlagRepresentante(projection.getFlagRepresentante());
        dto.setFlagSocio(projection.getFlagSocio());
        dto.setDmPrincipal(projection.getDmPrincipal());
        dto.setDmPrincipalDesc(domainHelper.describe(DomainDescriptionHelper.SIM_NAO, projection.getDmPrincipal()));
        dto.setDmEstado(projection.getDmEstado());
        dto.setDmEstadoDesc(domainHelper.describe(DomainDescriptionHelper.ESTADO, projection.getDmEstado()));
        dto.setDataRegisto(projection.getDataRegisto());
        dto.setUserRegisto(projection.getUserRegisto());
        dto.setIdUser(projection.getIdUser());
        dto.setNome(projection.getNome());
        String nacionalidade = projection.getNacionalidade();
        dto.setNacionalidade(nacionalidadeResolver.resolveDescricao(nacionalidade));
        dto.setNacionalidadeId(nacionalidadeResolver.resolveId(nacionalidade));
        dto.setNif(projection.getNif());
        dto.setTipoDoc(projection.getTipoDoc());
        dto.setTipoDocDesc(domainHelper.describe(DomainDescriptionHelper.TIPO_DOCUMENTO, projection.getTipoDoc()));
        dto.setNrDoc(projection.getNrDoc());
        dto.setTelefone(projection.getTelefone());
        dto.setTelemovel(projection.getTelemovel());
        dto.setEmail(projection.getEmail());
        dto.setIndicativoPais(projection.getIndicativoPais());
        return dto;
    }
}
