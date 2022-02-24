package fr.poleemploi.estime.logique.simulateur.aides.utile;

import org.springframework.stereotype.Component;

import fr.poleemploi.estime.commun.enumerations.AideEnum;
import fr.poleemploi.estime.services.ressources.RessourceFinanciere;

@Component
public class RessourceFinanciereUtile {

    public RessourceFinanciere creerRessourceFinanciere(AideEnum aideEnum, float montantRessourceFinanciere) {
	RessourceFinanciere ressourceFinanciere = new RessourceFinanciere();
	ressourceFinanciere.setCode(aideEnum.getCode());
	ressourceFinanciere.setMontant(montantRessourceFinanciere);
	ressourceFinanciere.setNom(aideEnum.getNom());
	return ressourceFinanciere;
    }
}
