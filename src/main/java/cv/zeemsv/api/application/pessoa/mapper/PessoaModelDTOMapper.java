package cv.zeemsv.api.application.pessoa.mapper;

import cv.zeemsv.api.application.pessoa.dto.PessoaResponseDTO;
import cv.zeemsv.api.domain.external.model.CniResponseModel;
import cv.zeemsv.api.domain.pessoa.model.PessoaModel;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface PessoaModelDTOMapper {
    PessoaResponseDTO toResponseDTO(PessoaModel pessoa);

    @Mapping(target = "dataNasc", source = "dataNasc", qualifiedByName = "stringToLocalDate")
    @Mapping(target = "telefone", expression = "java(dto.getTelefone() != null ? dto.getTelefone().toString() : null)")
    PessoaModel toModel(CniResponseModel dto);

    @Named("stringToLocalDate")
    static LocalDate stringToLocalDate(String date) {
        if (date == null || date.isBlank()) {
            return null;
        }
        return LocalDate.parse(date, DateTimeFormatter.ofPattern("dd-MM-yyyy"));
    }
}
