package it.paoloadesso.gestionalesala.mapper;

import it.paoloadesso.gestionalesala.dto.ListaOrdiniEProdottiByTavoloResponseDTO;
import it.paoloadesso.gestionalesala.dto.OrdiniDTO;
import it.paoloadesso.gestionalesala.dto.TavoloConOrdiniChiusiDTO;
import it.paoloadesso.gestionalesala.entities.OrdiniEntity;
import it.paoloadesso.gestionalesala.entities.OrdiniProdottiEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface OrdiniMapper {
    @Mapping(target = "idTavolo", source = "tavolo.id")
    OrdiniDTO ordiniEntityToDto (OrdiniEntity ordiniEntity);

    @Mapping(target = "idOrdine", source = "ordine.idOrdine")
    @Mapping(target = "idTavolo", source = "ordine.tavolo.id")
    @Mapping(target = "dataOrdine", source = "ordine.dataOrdine")
    @Mapping(target = "statoOrdine", source = "ordine.statoOrdine")
    @Mapping(target = "listaOrdineERelativiProdotti", ignore = true) // Perchè la popolo nel service
    ListaOrdiniEProdottiByTavoloResponseDTO ordiniProdottiEntityToDto(OrdiniProdottiEntity ordiniProdottiEntity);

    @Mapping(target = "idOrdine", ignore = true)
    OrdiniEntity ordiniDtoToEntity (OrdiniDTO ordiniDto);

    @Mapping(source = "tavolo.id", target = "idTavolo")
    @Mapping(source = "tavolo.numeroNomeTavolo", target = "numeroNomeTavolo")
    @Mapping(source = "tavolo.statoTavolo", target = "statoTavolo")
    TavoloConOrdiniChiusiDTO entityToDto(OrdiniEntity ordiniEntity);

    OrdiniEntity dtoToEntity(TavoloConOrdiniChiusiDTO ordiniResponseDTO);
}
