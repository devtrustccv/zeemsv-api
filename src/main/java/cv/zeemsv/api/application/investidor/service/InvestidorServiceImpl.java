package cv.zeemsv.api.application.investidor.service;

import cv.zeemsv.api.application.domain.DomainDescriptionHelper;
import cv.zeemsv.api.application.investidor.dto.InvestidorRequestDTO;
import cv.zeemsv.api.application.investidor.dto.InvestidorResponseDTO;
import cv.zeemsv.api.application.investidor.dto.InvestidorUserResponseDTO;
import cv.zeemsv.api.application.investidor.mapper.InvestidorDtoMapper;
import cv.zeemsv.api.domain.investidor.business.InvestidorBus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InvestidorServiceImpl implements InvestidorService {
    private final InvestidorBus bus;
    private final InvestidorDtoMapper mapper;
    private final DomainDescriptionHelper domainHelper;

    @Override @Transactional
    public InvestidorResponseDTO create(InvestidorRequestDTO dto) { return mapper.toResponse(bus.create(mapper.toModel(dto))); }

    @Override @Transactional
    public InvestidorResponseDTO update(Integer id, InvestidorRequestDTO dto) { return mapper.toResponse(bus.update(id, mapper.toModel(dto))); }

    @Override @Transactional(readOnly = true)
    public InvestidorResponseDTO findById(Integer id) { return mapper.toResponse(bus.findById(id)); }

    @Override @Transactional(readOnly = true)
    public List<InvestidorResponseDTO> findAll() { return bus.findAll().stream().map(mapper::toResponse).toList(); }

    @Override @Transactional(readOnly = true)
    public List<InvestidorUserResponseDTO> findByUserEmail(String email) {
        return bus.findByUserEmail(email).stream().map(this::toUserResponse).toList();
    }

    @Override @Transactional
    public void delete(Integer id) { bus.delete(id); }

    private InvestidorUserResponseDTO toUserResponse(cv.zeemsv.api.domain.investidor.model.InvestidorUser model) {
        InvestidorUserResponseDTO dto = mapper.toUserResponse(model);
        dto.setDmEstadoDesc(domainHelper.describe(DomainDescriptionHelper.ESTADO, dto.getDmEstado()));
        dto.setDmTipoInvestidorDesc(domainHelper.describe(DomainDescriptionHelper.TIPO_INVESTIDOR, dto.getDmTipoInvestidor()));
        dto.setDmTpRepresentanteDesc(domainHelper.describe(DomainDescriptionHelper.TIPO_REPRESENTANTE, dto.getDmTpRepresentante()));
        dto.setDmPrincipalDesc(domainHelper.describe(DomainDescriptionHelper.SIM_NAO, dto.getDmPrincipal()));
        return dto;
    }
}
