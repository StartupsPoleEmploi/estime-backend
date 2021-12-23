package fr.poleemploi.estime.commun.utile.demandeuremploi;

import java.util.Arrays;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import fr.poleemploi.estime.commun.enumerations.TypesContratTravailEnum;

@Component
public class TypeContratUtile {

    public String getListeFormateeTypesContratPossibles() {
        return String.join(" / ", Arrays.asList(TypesContratTravailEnum.values()).stream().map(Object::toString).collect(Collectors.toList()));
    }
}
