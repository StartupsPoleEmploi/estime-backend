package fr.poleemploi.estime.logique.simulateur.aides.poleemploi.utile;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fr.poleemploi.estime.commun.enumerations.AideEnum;
import fr.poleemploi.estime.commun.enumerations.OrganismeEnum;
import fr.poleemploi.estime.logique.simulateur.aides.utile.AideUtile;
import fr.poleemploi.estime.services.ressources.Aide;

@Component
public class AideMobiliteUtile {

    @Autowired
    private AideUtile aideUtile;

    public Aide creerAideMobilite(float montantAide) {
	return aideUtile.creerAide(AideEnum.AIDE_MOBILITE, Optional.of(OrganismeEnum.PE), Optional.empty(), false, montantAide);
    }
}
