package cv.zeemsv.api.application.user.service;

import cv.zeemsv.api.application.generic.dto.OtpResponseDto;
import cv.zeemsv.api.application.generic.service.OTPService;
import cv.zeemsv.api.application.pessoa.mapper.PessoaModelDTOMapper;
import cv.zeemsv.api.application.pessoa.service.ContatoService;
import cv.zeemsv.api.application.pessoa.service.InfoAcademicaService;
import cv.zeemsv.api.application.user.dto.LoginRequestDTO;
import cv.zeemsv.api.application.user.dto.LoginResponseDTO;
import cv.zeemsv.api.application.user.dto.SessionValidationResponseDTO;
import cv.zeemsv.api.application.user.dto.UserAccountDetailResponseDTO;
import cv.zeemsv.api.domain.external.business.AutentikaBus;
import cv.zeemsv.api.domain.external.model.AuthenticatedUserInfo;
import cv.zeemsv.api.domain.external.model.CniResponseModel;
import cv.zeemsv.api.domain.pessoa.business.PessoaBus;
import cv.zeemsv.api.domain.pessoa.model.PessoaModel;
import cv.zeemsv.api.domain.user.business.SessionBus;
import cv.zeemsv.api.domain.user.business.UserBus;
import cv.zeemsv.api.domain.user.model.SessionModel;
import cv.zeemsv.api.domain.user.model.UserModel;
import cv.zeemsv.api.exceptions.BusinessException;
import cv.zeemsv.api.exceptions.ExternalApiException;
import cv.zeemsv.api.utils.Constants;
import cv.zeemsv.api.utils.Helpers;
import cv.zeemsv.api.utils.JwtUtil;
import cv.zeemsv.api.utils.Messages;
import cv.zeemsv.api.utils.enums.DefaultStatusApp;
import cv.zeemsv.api.utils.enums.UserStatus;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Log4j2
public class SessionService {
    private final UserBus userBus;
    private final SessionBus sessionBus;
    private final AutentikaBus autentikaBus;
    private final PessoaBus pessoaBus;
    private final OTPService otpService;
    private final ContatoService contatoService;
    private final InfoAcademicaService infoAcademicaService;
    private final PessoaModelDTOMapper pessoaDTOMapper;

    @Value("${application.session.jwt-secret:01234567890123456789012345678901}")
    private String jwtSecret;

    @Value("${application.session.jwt-expiration-in-hours:8}")
    private long jwtExpirationInHours;

    @Transactional
    public LoginResponseDTO login(LoginRequestDTO request) {
        AuthenticatedUserInfo userInfo;
        try {
            userInfo = autentikaBus.getUserInfo(request.getAutentikaToken(), request.getLoginProvider());
        } catch (ExternalApiException e) {
            log.error(Messages.AUTENTIKA_ERROR, e);
            throw new BusinessException(Messages.AUTENTIKA_ERROR, e);
        }

        Optional<UserModel> optUser = userBus.findByEmailOrSubCmdcv(userInfo.getEmail(), userInfo.getSub());
        if (optUser.isEmpty()) {
            log.info("Utilizador nao encontrado na base de dados. Criando um novo utilizador.");
            UserModel user = UserModel.builder()
                .email(userInfo.getEmail())
                .status(UserStatus.PENDENTE)
                .name(userInfo.getName())
                .subCmdcv(userInfo.getSub())
                .provider(request.getLoginProvider().name())
                .build();
            optUser = Optional.of(userBus.save(user));
        }

        UserModel user = optUser.get();
        String jwtToken = JwtUtil.generateToken(
            jwtSecret,
            userInfo.getEmail() == null ? userInfo.getSub() : userInfo.getEmail(),
            jwtExpirationInHours,
            userInfo.getEmail(),
            userInfo.getName(),
            request.getFingerprint()
        );

        SessionModel session = SessionModel.builder()
            .userId(user.getId())
            .status(DefaultStatusApp.ATIVO.getKey())
            .startDate(LocalDateTime.now())
            .endDate(LocalDateTime.now().plusHours(jwtExpirationInHours))
            .sessionToken(jwtToken)
            .provider(request.getLoginProvider().name())
            .build();
        sessionBus.save(session);

        Optional<PessoaModel> pessoaOpt;
        PessoaModel pessoa = null;
        if (user.getPessoaId() != null) {
            pessoaOpt = pessoaBus.findById(user.getPessoaId());
        } else {
            pessoaOpt = pessoaBus.findByEmail(user.getEmail());
            if (pessoaOpt.isEmpty() && userInfo.isCmdActive() && userInfo.getSub() != null) {
                pessoaOpt = pessoaBus.findByNumDocumento(userInfo.getSub());
                if (pessoaOpt.isEmpty()) {
                    try {
                        CniResponseModel cniInfo = autentikaBus.searchPersonByCNIorTRE(userInfo.getSub());
                        pessoa = cniInfo != null ? pessoaDTOMapper.toModel(cniInfo) : null;
                        if (pessoa != null) {
                            pessoa = pessoaBus.save(pessoa);
                            pessoaOpt = Optional.of(pessoa);
                        }
                    } catch (ExternalApiException e) {
                        throw new BusinessException("Erro ao obter informacoes da pessoa a partir da integracao com CNI.", e);
                    }
                }
            }
        }

        LoginResponseDTO loginResponseDTO = new LoginResponseDTO();
        if (pessoaOpt.isPresent()) {
            pessoa = pessoaOpt.get();
            UserModel userValidate = userBus.findByPessoaId(pessoa.getId()).orElse(null);
            if (userValidate != null && !Objects.equals(userValidate.getId(), user.getId())) {
                throw new BusinessException(Messages.PERSON_ASSOCIATED_TO_USER,
                    new RuntimeException(Messages.PERSON_ASSOCIATED_TO_USER));
            }

            loginResponseDTO.setNumDocumento(pessoa.getNumDocumento());
            loginResponseDTO.setTipoDocumento(pessoa.getTipoDocumento());
            if (user.getPessoaId() == null || user.getName().matches(Constants.EMAIL_REGEX)) {
                user.setPessoaId(pessoa.getId());
                user.setName(pessoa.getNome());
                user.setEmail(Helpers.nvl(user.getEmail(), pessoa.getEmail()));
                user = userBus.save(user);
            }
        }

        loginResponseDTO.setSubCmdcv(user.getSubCmdcv());
        loginResponseDTO.setSessionToken(jwtToken);
        loginResponseDTO.setUserId(user.getId());
        loginResponseDTO.setPessoaId(user.getPessoaId());
        loginResponseDTO.setEmail(user.getEmail());
        loginResponseDTO.setNome(user.getName());
        loginResponseDTO.setStatus(user.getStatus());
        return loginResponseDTO;
    }

    public SessionValidationResponseDTO validateSession(String accessToken, String fingerprint) {
        Optional<SessionModel> optSession = sessionBus.findBySessionToken(accessToken);
        if (optSession.isEmpty()) {
            return SessionValidationResponseDTO.builder().valid(false).message("Sessao nao encontrada").build();
        }

        SessionModel session = optSession.get();
        if (session.getEndDate().isBefore(LocalDateTime.now())) {
            return SessionValidationResponseDTO.builder().valid(false).message("Sessao expirada!").build();
        }

        var tokenInfo = JwtUtil.validateToken(accessToken, jwtSecret, fingerprint);
        String message = tokenInfo.isValid() ? "Sessao valida" : Messages.SESSION_INVALID;
        return SessionValidationResponseDTO.builder()
            .accessToken(accessToken)
            .valid(tokenInfo.isValid())
            .message(message)
            .build();
    }

    public SessionValidationResponseDTO logout(String accessToken, String fingerprint) {
        Optional<SessionModel> optSession = sessionBus.findBySessionToken(accessToken);
        if (optSession.isEmpty()) {
            return SessionValidationResponseDTO.builder().valid(false).message("Sessao nao encontrada").build();
        }

        SessionModel session = optSession.get();
        if (session.getEndDate().isBefore(LocalDateTime.now())) {
            return SessionValidationResponseDTO.builder().valid(true).message("Sessao expirada!").build();
        }

        var tokenInfo = JwtUtil.validateToken(accessToken, jwtSecret, fingerprint);
        if (!tokenInfo.isValid()) {
            return SessionValidationResponseDTO.builder().valid(false)
                .message("Sessao invalida ou fingerprint nao confere!")
                .build();
        }

        session.setEndDate(LocalDateTime.now());
        sessionBus.save(session);
        return SessionValidationResponseDTO.builder().valid(true).message("Sessao encerrada.").build();
    }

    @Transactional
    public UserAccountDetailResponseDTO userAccountDetailByAccessToken(String accessToken, String fingerprint,
            boolean loadContacts, boolean loadInfoSchool) {
        Optional<SessionModel> optSession = sessionBus.findBySessionToken(accessToken);
        if (optSession.isEmpty()) {
            throw new BusinessException(Messages.SESSION_NOT_FOUND, new RuntimeException(Messages.SESSION_NOT_FOUND));
        }

        SessionModel session = optSession.get();
        if (session.getEndDate().isBefore(LocalDateTime.now())) {
            throw new BusinessException(Messages.SESSION_EXPIRED, new RuntimeException(Messages.SESSION_EXPIRED));
        }

        var token = JwtUtil.validateToken(accessToken, jwtSecret, fingerprint);
        if (!token.isValid()) {
            throw new BusinessException(Messages.SESSION_INVALID, new RuntimeException(Messages.SESSION_INVALID));
        }

        UserModel user = userBus.findByEmailOrSubCmdcv(token.getEmail(), token.getSub())
            .orElseThrow(() -> new BusinessException(Messages.USER_NOT_FOUND, new RuntimeException(Messages.USER_NOT_FOUND)));

        UserAccountDetailResponseDTO response = UserAccountDetailResponseDTO.builder()
            .userId(user.getId())
            .sessionToken(accessToken)
            .email(user.getEmail())
            .name(user.getName())
            .status(user.getStatus())
            .subCmdcv(user.getSubCmdcv())
            .build();

        Optional<PessoaModel> pessoaOpt;
        if (user.getPessoaId() != null) {
            pessoaOpt = pessoaBus.findById(user.getPessoaId());
        } else {
            pessoaOpt = pessoaBus.findByEmail(user.getEmail());
            pessoaOpt.ifPresent(pessoaModel -> userBus.updatePessoaInfoById(user.getId(), pessoaModel.getId(), pessoaModel.getNome()));
        }

        if (pessoaOpt.isPresent()) {
            var pessoaDto = pessoaDTOMapper.toResponseDTO(pessoaOpt.get());
            response.setPessoaInfo(pessoaDto);
            if (loadContacts) {
                response.setContacts(contatoService.findContactsByPessoaId(pessoaDto.getId()));
            }
            if (loadInfoSchool) {
                response.setInfoSchool(infoAcademicaService.findInfoAcademicasByPessoaId(pessoaDto.getId()));
            }
        }

        return response;
    }

    @Transactional
    public OtpResponseDto sendOtpUsingSession(String accessToken, String fingerprint, String email) {
        var token = validateSessionOrThrow(accessToken, fingerprint);
        String userEmail = Helpers.nvl(token.getEmail(), email);
        if (userEmail == null) {
            throw new BusinessException(Messages.EMAIL_NOT_EXIST, new RuntimeException(Messages.EMAIL_NOT_EXIST));
        }

        UserModel user = userBus.findByEmailOrSubCmdcv(token.getEmail(), token.getSub())
            .orElseThrow(() -> new BusinessException(Messages.USER_NOT_FOUND, new RuntimeException(Messages.USER_NOT_FOUND)));
        if (user.getEmail() == null) {
            user.setEmail(userEmail);
            userBus.save(user);
        }

        var otpDto = otpService.sendOTP(userEmail);
        return OtpResponseDto.builder()
            .otpLength(otpDto.getOtpLength())
            .expirationMinutes(otpDto.getExpirationMinutes())
            .expiresAt(otpDto.getExpiresAt())
            .build();
    }

    @Transactional
    public boolean validateOtpUsingSession(String accessToken, String fingerprint, String otp) {
        var token = validateSessionOrThrow(accessToken, fingerprint);
        UserModel user = userBus.findByEmailOrSubCmdcv(token.getEmail(), token.getSub())
            .orElseThrow(() -> new BusinessException(Messages.USER_NOT_FOUND, new RuntimeException(Messages.USER_NOT_FOUND)));

        if (otpService.validateOtp(user.getEmail(), otp)) {
            return true;
        }
        throw new BusinessException("OTP invalido", new RuntimeException("OTP invalido"));
    }

    private cv.zeemsv.api.application.generic.dto.SessionTokenDto validateSessionOrThrow(String accessToken, String fingerprint) {
        SessionModel session = sessionBus.findBySessionToken(accessToken)
            .orElseThrow(() -> new BusinessException(Messages.SESSION_NOT_FOUND, new RuntimeException(Messages.SESSION_NOT_FOUND)));
        if (session.getEndDate().isBefore(LocalDateTime.now())) {
            throw new BusinessException(Messages.SESSION_EXPIRED, new RuntimeException(Messages.SESSION_EXPIRED));
        }
        var token = JwtUtil.validateToken(accessToken, jwtSecret, fingerprint);
        if (!token.isValid()) {
            throw new BusinessException(Messages.SESSION_INVALID, new RuntimeException(Messages.SESSION_INVALID));
        }
        return token;
    }
}
