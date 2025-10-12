package it.paoloadesso.gestionetavoli.mapper;

import it.paoloadesso.gestionetavoli.dto.TavoloConOrdiniChiusiDTO;
import it.paoloadesso.gestionetavoli.entities.OrdiniEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface OrdiniMapper {

    @Mapping(source = "tavolo.id", target = "idTavolo")
    @Mapping(source = "tavolo.numeroNomeTavolo", target = "numeroNomeTavolo")
    @Mapping(source = "tavolo.statoTavolo", target = "statoTavolo")
    TavoloConOrdiniChiusiDTO entityToDto(OrdiniEntity ordiniEntity);

    OrdiniEntity dtoToEntity(TavoloConOrdiniChiusiDTO ordiniResponseDTO);


}
