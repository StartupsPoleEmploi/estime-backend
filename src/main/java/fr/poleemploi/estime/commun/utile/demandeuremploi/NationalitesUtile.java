package fr.poleemploi.estime.commun.utile.demandeuremploi;

import java.util.Arrays;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import fr.poleemploi.estime.commun.enumerations.NationaliteEnum;

@Component
public class NationalitesUtile {

    public String getListeFormateeNationalitesPossibles() {
        return String.join(" / ", Arrays.asList(NationaliteEnum.values()).stream().map(Object::toString).collect(Collectors.toList()));
    }
}
