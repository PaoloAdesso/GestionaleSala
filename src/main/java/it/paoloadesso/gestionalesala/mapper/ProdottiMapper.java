package it.paoloadesso.gestionalesala.mapper;

import it.paoloadesso.gestionalesala.dto.CreaProdottiDTO;
import it.paoloadesso.gestionalesala.dto.ProdottiConDettaglioDeleteDTO;
import it.paoloadesso.gestionalesala.dto.ProdottiDTO;
import it.paoloadesso.gestionalesala.dto.RisultatoModificaProdottoDTO;
import it.paoloadesso.gestionalesala.entities.ProdottiEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProdottiMapper {

    @Mapping(target = "id", ignore = true)
    ProdottiEntity prodottiDtoToEntity (ProdottiDTO prodottiDto);

    @Mapping(source = "id", target = "idProdotto")
    ProdottiDTO prodottiEntityToProdottiDto(ProdottiEntity prodottiEntity);

    @Mapping(target = "id", ignore = true)
    ProdottiEntity createProdottiDtoToEntity (CreaProdottiDTO creaProdottiDto);

    CreaProdottiDTO createProdottiEntityToDto (ProdottiEntity prodottiEntity);

    @Mapping(source = "id", target = "idProdotto")
    ProdottiConDettaglioDeleteDTO prodottiEntityToProdottiConDettaglioDeleteDto(ProdottiEntity prodottiEntity);

    @Mapping(source = "id", target = "prodotto.idProdotto")
    RisultatoModificaProdottoDTO prodottiEntityToRisultatoModificaProdottoDto(ProdottiEntity prodottiEntity);
}
