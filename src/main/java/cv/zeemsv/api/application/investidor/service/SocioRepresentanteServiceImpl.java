package cv.zeemsv.api.application.investidor.service;

import cv.zeemsv.api.application.domain.DomainDescriptionHelper;
import cv.zeemsv.api.application.geografia.service.NacionalidadeResolver;
import cv.zeemsv.api.application.generic.service.OTPService;
import cv.zeemsv.api.application.audit.dto.AuditContext;
import cv.zeemsv.api.application.audit.entity.ChangeLogsItem;
import cv.zeemsv.api.application.audit.service.ChangeLogsService;
import cv.zeemsv.api.application.investidor.dto.RepresentanteInvestidorResponseDTO;
import cv.zeemsv.api.application.investidor.dto.SocioRepresentanteRequestDTO;
import cv.zeemsv.api.application.investidor.dto.SocioRepresentanteResponseDTO;
import cv.zeemsv.api.application.investidor.dto.SocioRepresentanteUpdateRequestDTO;
import cv.zeemsv.api.application.generic.service.EmailService;
import cv.zeemsv.api.domain.documento.business.DocumentViewerUrlService;
import cv.zeemsv.api.domain.documento.business.DocumentoBus;
import cv.zeemsv.api.domain.documento.dto.UploadDTO;
import cv.zeemsv.api.domain.user.business.UserBus;
import cv.zeemsv.api.domain.user.model.UserModel;
import cv.zeemsv.api.exceptions.BusinessException;
import cv.zeemsv.api.exceptions.OtpRequiredException;
import cv.zeemsv.api.infrastructure.entity.ZeeTDocRelacaoEntity;
import cv.zeemsv.api.infrastructure.entity.ZeeTSocioRepresEntity;
import cv.zeemsv.api.infrastructure.repository.ZeeTRepresInvestidorRepository;
import cv.zeemsv.api.infrastructure.repository.ZeeTSocioRepresRepository;
import cv.zeemsv.api.infrastructure.repository.projection.RepresentanteInvestidorProjection;
import cv.zeemsv.api.utils.Messages;
import cv.zeemsv.api.utils.enums.LoginProvider;
import cv.zeemsv.api.utils.enums.UserStatus;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Log4j2
public class SocioRepresentanteServiceImpl implements SocioRepresentanteService {
    private static final String ESTADO_ATIVO = "A";
    private static final String ESTADO_PENDENTE = "PENDENTE";
    private static final String TIPO_RELACAO_SOCIO_REPRES = "SOCIO_REPRES";
    private static final String NOME_FICHEIRO_FOTO = "foto";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final ZeeTSocioRepresRepository repository;
    private final ZeeTRepresInvestidorRepository represInvestidorRepository;
    private final DomainDescriptionHelper domainHelper;
    private final UserBus userBus;
    private final NacionalidadeResolver nacionalidadeResolver;
    private final DocumentoBus documentoBus;
    private final DocumentViewerUrlService documentViewerUrlService;
    private final EmailService emailService;
    private final OTPService otpService;
    private final ChangeLogsService changeLogsService;

    @Override
    @Transactional
    public SocioRepresentanteResponseDTO create(SocioRepresentanteRequestDTO dto, MultipartFile foto) {
        validateUniqueFields(dto);
        UserModel user = resolveUserByEmail(dto.getEmail(), dto.getNome(), UserStatus.A, true);

        ZeeTSocioRepresEntity entity = repository.save(toEntity(dto, user, ESTADO_ATIVO));
        if (foto != null && !foto.isEmpty()) {
            UploadDTO upload = buildFotoUpload(entity, foto);
            documentoBus.saveOrUpdate(upload, String.valueOf(user.getId()));
            entity.setFotoUrl(null);
            entity.setFotoPath(upload.getZeeTDocRelacao().getPath());
            entity = repository.save(entity);
        }
        return toResponse(entity);
    }

    @Override
    @Transactional
    public SocioRepresentanteResponseDTO update(Integer idSocioRepres, SocioRepresentanteUpdateRequestDTO dto, MultipartFile foto) {
        ZeeTSocioRepresEntity entity = repository.findById(idSocioRepres)
            .orElseThrow(() -> new BusinessException("Socio/representante nao encontrado."));
        ZeeTSocioRepresEntity before = copySocioRepres(entity);

        if (dto.getTelefone() != null) {
            entity.setTelefone(dto.getTelefone());
        }
        if (dto.getTelemovel() != null) {
            entity.setTelemovel(dto.getTelemovel());
        }
        if (StringUtils.hasText(dto.getIndicativoPais())) {
            entity.setIndicativoPais(trim(dto.getIndicativoPais()));
        }
        if (StringUtils.hasText(dto.getEndereco())) {
            entity.setEndereco(trim(dto.getEndereco()));
        }
        boolean emailChanged = false;
        UserModel previousUser = null;
        UserModel newUser = null;
        if (StringUtils.hasText(dto.getEmail()) && !trim(dto.getEmail()).equalsIgnoreCase(trim(entity.getEmail()))) {
            String newEmail = normalizeEmail(dto.getEmail());
            validateUpdateEmailAvailable(newEmail, entity);
            validateEmailChangeOtp(newEmail, dto.getOtp());
            previousUser = entity.getIdUser() != null ? userBus.findById(entity.getIdUser()).orElse(null) : null;
            newUser = resolveUpdateUserByEmail(newEmail, entity, previousUser);
            entity.setEmail(newEmail);
            entity.setIdUser(newUser.getId());
            emailChanged = true;
        }
        if (foto != null && !foto.isEmpty()) {
            UploadDTO upload = buildFotoUpload(entity, foto);
            documentoBus.saveOrUpdate(upload, entity.getIdUser() != null ? String.valueOf(entity.getIdUser()) : null);
            entity.setFotoUrl(null);
            entity.setFotoPath(upload.getZeeTDocRelacao().getPath());
        }
        ZeeTSocioRepresEntity saved = repository.save(entity);
        if (emailChanged) {
            deactivatePreviousUser(previousUser, newUser);
            notifyEmailAssociado(saved);
        }
        auditSocioRepresentanteUpdate(before, saved);
        return toResponse(saved);
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
    public SocioRepresentanteResponseDTO findById(Integer idSocioRepres) {
        return repository.findById(idSocioRepres)
            .map(this::toResponse)
            .orElseThrow(() -> new BusinessException("Socio/representante nao encontrado."));
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

    private UserModel resolveUpdateUserByEmail(String email, ZeeTSocioRepresEntity currentSocioRepres, UserModel previousUser) {
        String normalizedEmail = normalizeEmail(email);
        repository.findByEmailIgnoreCase(normalizedEmail).stream()
            .filter(socioRepres -> !Objects.equals(socioRepres.getId(), currentSocioRepres.getId()))
            .findFirst()
            .ifPresent(socioRepres -> {
                throw new BusinessException("Este email pertence a um outro socio/representante.");
            });

        UserModel user = userBus.findByEmail(normalizedEmail).orElse(null);
        if (user != null) {
            repository.findByIdUser(user.getId()).stream()
                .filter(socioRepres -> !Objects.equals(socioRepres.getId(), currentSocioRepres.getId()))
                .findFirst()
                .ifPresent(socioRepres -> {
                    throw new BusinessException("Este email pertence a um outro socio/representante.");
                });
            user.setName(StringUtils.hasText(user.getName()) ? user.getName() : currentSocioRepres.getNome());
            user.setProvider(StringUtils.hasText(user.getProvider()) ? user.getProvider() : LoginProvider.LOCAL.name());
        } else {
            user = UserModel.builder()
                .email(normalizedEmail)
                .name(currentSocioRepres.getNome())
                .provider(LoginProvider.LOCAL.name())
                .build();
        }

        user.setStatus(UserStatus.ATIVO);
        String previousPasswordHash = resolvePasswordHashForEmailChange(previousUser);
        if (StringUtils.hasText(previousPasswordHash)) {
            user.setPasswordHash(previousPasswordHash);
        }
        return userBus.save(user);
    }

    private void deactivatePreviousUser(UserModel previousUser, UserModel newUser) {
        if (previousUser == null || newUser == null || Objects.equals(previousUser.getId(), newUser.getId())) {
            return;
        }
        previousUser.setStatus(UserStatus.PENDENTE);
        userBus.save(previousUser);
    }

    private String resolvePasswordHashForEmailChange(UserModel previousUser) {
        return previousUser != null && StringUtils.hasText(previousUser.getPasswordHash()) ? previousUser.getPasswordHash() : null;
    }

    private void validateUpdateEmailAvailable(String normalizedEmail, ZeeTSocioRepresEntity currentSocioRepres) {
        repository.findByEmailIgnoreCase(normalizedEmail).stream()
            .filter(socioRepres -> !Objects.equals(socioRepres.getId(), currentSocioRepres.getId()))
            .findFirst()
            .ifPresent(socioRepres -> {
                throw new BusinessException("Este email pertence a um outro socio/representante.");
            });

        userBus.findByEmail(normalizedEmail)
            .ifPresent(user -> repository.findByIdUser(user.getId()).stream()
                .filter(socioRepres -> !Objects.equals(socioRepres.getId(), currentSocioRepres.getId()))
                .findFirst()
                .ifPresent(socioRepres -> {
                    throw new BusinessException("Este email pertence a um outro socio/representante.");
                }));
    }

    private void validateEmailChangeOtp(String normalizedEmail, String otp) {
        if (!StringUtils.hasText(otp)) {
            otpService.sendOTP(normalizedEmail);
            throw new OtpRequiredException("OTP enviado para o novo email. Informe o OTP para confirmar a alteração.");
        }
        if (!otpService.validateOtp(normalizedEmail, trim(otp))) {
            throw new BusinessException("OTP inválido ou expirado.");
        }
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
        entity.setFotoUrl(trim(dto.getFotoUrl()));
        entity.setEstado(estado);
        entity.setDateCreate(LocalDate.now());
        entity.setIndicativoPais(trim(dto.getIndicativoPais()));
        entity.setEndereco(trim(dto.getEndereco()));
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

    private String normalizeEmail(String email) {
        return trim(email).toLowerCase();
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
        dto.setFotoUrl(resolveFotoUrl(entity.getFotoUrl(), entity.getFotoPath()));
        dto.setFotoPath(null);
        dto.setFlagSocio(entity.getFlagSocio());
        dto.setFlagRepresentante(entity.getFlagRepresentante());
        dto.setDmPrincipal(entity.getDmPrincipal());
        dto.setEstado(entity.getEstado());
        dto.setDateCreate(entity.getDateCreate());
        dto.setUserCreate(entity.getUserCreate());
        dto.setIndicativoPais(entity.getIndicativoPais());
        dto.setEndereco(entity.getEndereco());
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
        dto.setFotoUrl(resolveFotoUrl(projection.getFotoUrl(), projection.getFotoPath()));
        dto.setFotoPath(null);
        dto.setIndicativoPais(projection.getIndicativoPais());
        dto.setEndereco(projection.getEndereco());
        return dto;
    }

    private UploadDTO buildFotoUpload(ZeeTSocioRepresEntity socioRepres, MultipartFile foto) {
        ZeeTDocRelacaoEntity docRelacao = new ZeeTDocRelacaoEntity();
        docRelacao.setTipoRelacao(TIPO_RELACAO_SOCIO_REPRES);
        docRelacao.setIdRelacao(java.math.BigDecimal.valueOf(socioRepres.getId()));

        String basePath = DocumentoBus.getBasePathForModuloOrObject(
            TIPO_RELACAO_SOCIO_REPRES,
            socioRepres.getId().toString()
        );
        return new UploadDTO(foto, NOME_FICHEIRO_FOTO, basePath, docRelacao);
    }

    private String resolveFotoUrl(String fotoUrl, String fotoPath) {
        return StringUtils.hasText(fotoUrl) ? fotoUrl : documentViewerUrlService.toViewerUrl(fotoPath);
    }

    private void notifyEmailAssociado(ZeeTSocioRepresEntity socioRepres) {
        String subject = "Email associado à conta ZEEMSV";
        String body = "Informamos que este email foi associado à conta de "
            + socioRepres.getNome()
            + " no dia "
            + LocalDate.now().format(DATE_FORMATTER)
            + ".";
        emailService.sendText(socioRepres.getEmail(), subject, body);
    }

    private void auditSocioRepresentanteUpdate(ZeeTSocioRepresEntity before, ZeeTSocioRepresEntity after) {
        try {
            List<ChangeLogsItem> items = List.of(
                logItem("telefone", before.getTelefone(), after.getTelefone()),
                logItem("telemovel", before.getTelemovel(), after.getTelemovel()),
                logItem("indicativo_pais", before.getIndicativoPais(), after.getIndicativoPais()),
                logItem("endereco", before.getEndereco(), after.getEndereco()),
                logItem("email", before.getEmail(), after.getEmail()),
                logItem("id_user", before.getIdUser(), after.getIdUser()),
                logItem("foto_path", before.getFotoPath(), after.getFotoPath())
            ).stream().filter(this::hasChanged).toList();
            if (items.isEmpty()) {
                return;
            }
            changeLogsService.createLogsAsyncSafe(
                items,
                "UPDATE",
                "zee_t_socio_repres",
                String.valueOf(after.getId()),
                "Atualizar socio/representante",
                AuditContext.builder()
                    .userId(after.getIdUser() != null ? String.valueOf(after.getIdUser()) : null)
                    .userEmail(after.getEmail())
                    .build()
            );
        } catch (RuntimeException ex) {
            log.warn("Nao foi possivel gravar auditoria da atualizacao do socio/representante {}.", after.getId(), ex);
        }
    }

    private ZeeTSocioRepresEntity copySocioRepres(ZeeTSocioRepresEntity source) {
        ZeeTSocioRepresEntity copy = new ZeeTSocioRepresEntity();
        copy.setId(source.getId());
        copy.setTelefone(source.getTelefone());
        copy.setTelemovel(source.getTelemovel());
        copy.setIndicativoPais(source.getIndicativoPais());
        copy.setEndereco(source.getEndereco());
        copy.setEmail(source.getEmail());
        copy.setIdUser(source.getIdUser());
        copy.setFotoPath(source.getFotoPath());
        return copy;
    }

    private ChangeLogsItem logItem(String column, Object oldValue, Object newValue) {
        ChangeLogsItem item = new ChangeLogsItem();
        item.setColumn(column);
        item.setOldValue(oldValue);
        item.setNewValue(newValue);
        return item;
    }

    private boolean hasChanged(ChangeLogsItem item) {
        return !Objects.equals(item.getOldValue(), item.getNewValue());
    }
}
