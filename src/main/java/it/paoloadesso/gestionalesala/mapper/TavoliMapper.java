package it.paoloadesso.gestionalesala.mapper;

import it.paoloadesso.gestionalesala.dto.CreaTavoliRequestDTO;
import it.paoloadesso.gestionalesala.dto.TavoliDTO;
import it.paoloadesso.gestionalesala.dto.TavoloApertoDTO;
import it.paoloadesso.gestionalesala.entities.TavoliEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TavoliMapper {

    TavoloApertoDTO entityToDto(TavoliEntity tavoliEntity);

    @Mapping(target = "id", ignore = true)
    TavoliEntity dtoToEntity(TavoloApertoDTO tavoloApertoDTO);

    CreaTavoliRequestDTO createTavoliEntityToDto(TavoliEntity tavoliEntity);

    @Mapping(target = "id", ignore = true)
    TavoliEntity createTavoliDtoToEntity(CreaTavoliRequestDTO creaTavoliRequestDto);

    @Mapping(target = "id", ignore = true)
    TavoliEntity dtoToEntity(TavoliDTO aggiornaTavoliRequestDto);

    TavoliDTO simpleEntityToDto(TavoliEntity tavoliEntity);
}
