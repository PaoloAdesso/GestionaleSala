package it.paoloadesso.gestionetavoli.mapper;

import it.paoloadesso.gestionetavoli.dto.CreaTavoliRequestDTO;
import it.paoloadesso.gestionetavoli.dto.TavoliDTO;
import it.paoloadesso.gestionetavoli.dto.TavoloApertoDTO;
import it.paoloadesso.gestionetavoli.entities.TavoliEntity;
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
