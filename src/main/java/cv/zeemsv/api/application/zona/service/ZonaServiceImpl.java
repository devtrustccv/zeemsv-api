package cv.zeemsv.api.application.zona.service;

import com.fasterxml.jackson.databind.JsonNode;
import cv.zeemsv.api.application.domain.DomainDescriptionHelper;
import cv.zeemsv.api.application.zona.dto.ZonaCoordenadaPontoDTO;
import cv.zeemsv.api.application.zona.dto.ZonaCoordenadasResponseDTO;
import cv.zeemsv.api.application.zona.dto.ZonaLoteCoordenadasDTO;
import cv.zeemsv.api.application.zona.dto.ZonaParqueDTO;
import cv.zeemsv.api.infrastructure.entity.ZeeTLoteEntity;
import cv.zeemsv.api.infrastructure.entity.ZeeTParkEntity;
import cv.zeemsv.api.infrastructure.entity.ZeeTZonaCordEntity;
import cv.zeemsv.api.infrastructure.entity.ZeeTZonaEntity;
import cv.zeemsv.api.infrastructure.repository.ZeeTLoteRepository;
import cv.zeemsv.api.infrastructure.repository.ZeeTParkRepository;
import cv.zeemsv.api.infrastructure.repository.ZeeTZonaCordRepository;
import cv.zeemsv.api.infrastructure.repository.ZeeTZonaRepository;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ZonaServiceImpl implements ZonaService {
    private final ZeeTZonaRepository zonaRepository;
    private final ZeeTZonaCordRepository zonaCordRepository;
    private final ZeeTLoteRepository loteRepository;
    private final ZeeTParkRepository parkRepository;
    private final DomainDescriptionHelper domainHelper;

    @Override
    @Transactional(readOnly = true)
    public List<ZonaCoordenadasResponseDTO> findZonasComCoordenadasELotes() {
        List<ZeeTZonaEntity> zonas = zonaRepository.findAllByOrderByNomeAsc();
        if (zonas.isEmpty()) {
            return Collections.emptyList();
        }

        List<Integer> idZonas = zonas.stream().map(ZeeTZonaEntity::getId).toList();
        Map<Integer, String> nomesZonasById = zonas.stream()
            .collect(Collectors.toMap(ZeeTZonaEntity::getId, ZeeTZonaEntity::getNome, (left, right) -> left));
        Map<Integer, List<ZonaCoordenadaPontoDTO>> pontosByZona = zonaCordRepository
            .findByIdZonaInOrderByIdZonaAscVerticeAsc(idZonas)
            .stream()
            .filter(ponto -> ponto.getIdZona() != null)
            .collect(Collectors.groupingBy(ZeeTZonaCordEntity::getIdZona, Collectors.mapping(this::toPonto, Collectors.toList())));

        List<ZeeTParkEntity> parques = parkRepository.findByIdZonaInOrderByIdZonaAscNomeAsc(idZonas);
        Map<Integer, List<ZonaParqueDTO>> parquesByZona = parques.stream()
            .filter(parque -> parque.getIdZona() != null)
            .collect(Collectors.groupingBy(ZeeTParkEntity::getIdZona, Collectors.mapping(this::toParque, Collectors.toList())));
        Map<Integer, ZeeTParkEntity> parquesById = parques.stream()
            .collect(Collectors.toMap(ZeeTParkEntity::getId, Function.identity(), (left, right) -> left));

        List<ZeeTLoteEntity> todosLotes = loteRepository.findByIdZonaInOrderByIdZonaAscRefLoteAsc(idZonas);
        Map<Integer, List<ZeeTLoteEntity>> todosLotesByZona = todosLotes.stream()
            .filter(lote -> lote.getIdZona() != null)
            .collect(Collectors.groupingBy(ZeeTLoteEntity::getIdZona));

        Map<Integer, List<ZonaLoteCoordenadasDTO>> lotesComCoordenadasByZona = loteRepository
            .findByIdZonaInAndCoordenadasIsNotNullOrderByIdZonaAscRefLoteAsc(idZonas)
            .stream()
            .filter(lote -> lote.getIdZona() != null)
            .collect(Collectors.groupingBy(
                ZeeTLoteEntity::getIdZona,
                Collectors.mapping(lote -> toLote(lote, parquesById, nomesZonasById), Collectors.toList())
            ));

        return zonas.stream()
            .map(zona -> toZona(
                zona,
                pontosByZona.getOrDefault(zona.getId(), Collections.emptyList()),
                parquesByZona.getOrDefault(zona.getId(), Collections.emptyList()),
                todosLotesByZona.getOrDefault(zona.getId(), Collections.emptyList()),
                lotesComCoordenadasByZona.getOrDefault(zona.getId(), Collections.emptyList())
            ))
            .filter(zona -> hasCoordenadas(zona.getCoordenadas()) || !zona.getPontos().isEmpty())
            .toList();
    }

    private boolean hasCoordenadas(JsonNode coordenadas) {
        return coordenadas != null && !coordenadas.isNull() && !coordenadas.isEmpty();
    }

    private ZonaCoordenadasResponseDTO toZona(
        ZeeTZonaEntity entity,
        List<ZonaCoordenadaPontoDTO> pontos,
        List<ZonaParqueDTO> parques,
        List<ZeeTLoteEntity> todosLotes,
        List<ZonaLoteCoordenadasDTO> lotes
    ) {
        ZonaCoordenadasResponseDTO dto = new ZonaCoordenadasResponseDTO();
        dto.setId(entity.getId());
        dto.setNome(entity.getNome());
        dto.setAreaTotal(sumArea(todosLotes));
        dto.setAreaTotalDisponivel(sumArea(todosLotes.stream().filter(this::isLoteLivre).toList()));
        dto.setAreaTotalOcupada(sumArea(todosLotes.stream().filter(this::isLoteOcupado).toList()));
        dto.setQuantidadeLotesLivres(todosLotes.stream().filter(this::isLoteLivre).count());
        dto.setQuantidadeLotesOcupadas(todosLotes.stream().filter(this::isLoteOcupado).count());
        dto.setSigla(entity.getSigla());
        dto.setEstado(entity.getEstado());
        dto.setLocalGeogId(entity.getLocalGeogId());
        dto.setNomeNorm(entity.getNomeNorm());
        dto.setDateCreate(entity.getDateCreate());
        dto.setCoordenadas(entity.getCoordenadas());
        dto.setParques(parques);
        dto.setPontos(pontos);
        dto.setLotes(lotes);
        return dto;
    }

    private BigDecimal sumArea(List<ZeeTLoteEntity> lotes) {
        return lotes.stream()
            .map(ZeeTLoteEntity::getArea)
            .filter(Objects::nonNull)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private boolean isLoteLivre(ZeeTLoteEntity lote) {
        return containsAny(lote.getDmSituacaoCd(), "LIVRE", "DISPONIVEL")
            || containsAny(lote.getDmDisponibilidade(), "LIVRE", "DISPONIVEL");
    }

    private boolean isLoteOcupado(ZeeTLoteEntity lote) {
        return containsAny(lote.getDmSituacaoCd(), "OCUP", "RESERVADO", "CEDIDO", "INDISPONIVEL")
            || containsAny(lote.getDmDisponibilidade(), "OCUP", "RESERVADO", "CEDIDO", "INDISPONIVEL");
    }

    private boolean containsAny(String value, String... expected) {
        if (value == null || value.isBlank()) {
            return false;
        }
        String normalized = value.trim().toUpperCase(Locale.ROOT);
        for (String item : expected) {
            if (normalized.contains(item)) {
                return true;
            }
        }
        return false;
    }

    private ZonaCoordenadaPontoDTO toPonto(ZeeTZonaCordEntity entity) {
        ZonaCoordenadaPontoDTO dto = new ZonaCoordenadaPontoDTO();
        dto.setId(entity.getId());
        dto.setVertice(entity.getVertice());
        dto.setCoordenadaX(entity.getCoordenadaX());
        dto.setCoordenadaY(entity.getCoordenadaY());
        dto.setDmEstado(entity.getDmEstado());
        dto.setDataRegisto(entity.getDataRegisto());
        return dto;
    }

    private ZonaParqueDTO toParque(ZeeTParkEntity entity) {
        ZonaParqueDTO dto = new ZonaParqueDTO();
        dto.setId(entity.getId());
        dto.setIdZona(entity.getIdZona());
        dto.setNome(entity.getNome());
        dto.setSigla(entity.getSigla());
        dto.setEstado(entity.getEstado());
        dto.setDateCreate(entity.getDateCreate());
        return dto;
    }

    private ZonaLoteCoordenadasDTO toLote(ZeeTLoteEntity entity, Map<Integer, ZeeTParkEntity> parquesById, Map<Integer, String> nomesZonasById) {
        ZonaLoteCoordenadasDTO dto = new ZonaLoteCoordenadasDTO();
        ZeeTParkEntity parque = entity.getIdPark() == null ? null : parquesById.get(entity.getIdPark());
        dto.setId(entity.getId());
        dto.setIdZona(entity.getIdZona());
        dto.setZona(nomesZonasById.get(entity.getIdZona()));
        dto.setIdPark(entity.getIdPark());
        dto.setParque(parque == null ? null : parque.getNome());
        dto.setRefLote(entity.getRefLote());
        dto.setRefCm(entity.getRefCm());
        dto.setNip(entity.getNip());
        dto.setArea(entity.getArea());
        dto.setAreaInicial(entity.getAreaInicial());
        dto.setDmSituacaoCd(entity.getDmSituacaoCd());
        dto.setSituacao(domainHelper.describe(DomainDescriptionHelper.SITUACAO_LOTE, entity.getDmSituacaoCd()));
        dto.setDmDisponibilidade(entity.getDmDisponibilidade());
        dto.setDisponibilidade(describeOrCode(DomainDescriptionHelper.DISPONIBILIDADE, entity.getDmDisponibilidade()));
        dto.setDmFormalizacao(entity.getDmFormalizacao());
        dto.setEstado(entity.getEstado());
        dto.setPublicado(entity.getPublicado());
        dto.setValidado(entity.getValidado());
        dto.setValorComercial(entity.getValorComercial());
        dto.setValorDireitoSuperficie(entity.getValorDireitoSuperficie());
        dto.setCoordenadas(entity.getCoordenadas());
        return dto;
    }

    private String describeOrCode(String dominio, String codigo) {
        String descricao = domainHelper.describe(dominio, codigo);
        return descricao == null ? codigo : descricao;
    }
}
