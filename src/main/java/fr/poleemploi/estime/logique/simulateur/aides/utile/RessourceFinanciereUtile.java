package fr.poleemploi.estime.logique.simulateur.aides.utile;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;

import fr.poleemploi.estime.commun.enumerations.AideEnum;
import fr.poleemploi.estime.services.ressources.RessourceFinanciere;

@Component
public class RessourceFinanciereUtile {

    public RessourceFinanciere creerRessourceFinanciere(AideEnum aideEnum, Optional<List<String>> messageAlerteOptional, float montantRessourceFinanciere) {
	RessourceFinanciere ressourceFinanciere = new RessourceFinanciere();
	ressourceFinanciere.setCode(aideEnum.getCode());
	ressourceFinanciere.setMontant(montantRessourceFinanciere);
	ressourceFinanciere.setNom(aideEnum.getNom());
	if (messageAlerteOptional.isPresent()) {
	    ressourceFinanciere.setMessagesAlerte(messageAlerteOptional.get());
	}
	return ressourceFinanciere;
    }
}
