package it.paoloadesso.gestionetavoli.mapper;

import it.paoloadesso.gestionetavoli.dto.CreaProdottiDTO;
import it.paoloadesso.gestionetavoli.dto.ProdottiDTO;
import it.paoloadesso.gestionetavoli.entities.ProdottiEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProdottiMapper {

    @Mapping(target = "id", ignore = true)
    ProdottiEntity prodottiDtoToEntity (ProdottiDTO prodottiDto);

    @Mapping(source = "id", target = "idProdotto")
    ProdottiDTO prodottiEntityToDto (ProdottiEntity prodottiEntity);

    @Mapping(target = "id", ignore = true)
    ProdottiEntity createProdottiDtoToEntity (CreaProdottiDTO creaProdottiDto);

    CreaProdottiDTO createProdottiEntityToDto (ProdottiEntity prodottiEntity);
}
