package fr.poleemploi.estime.commun.utile.demandeuremploi;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fr.poleemploi.estime.services.ressources.Personne;

@Component
public class PersonneUtile {

    @Autowired
    private BeneficiaireAidesUtile beneficiaireAidesUtile;

    public boolean hasAllocationRSA(Personne personne) {
	return personne.getRessourcesFinancieres() != null && personne.getRessourcesFinancieres().getAidesCAF() != null
		&& personne.getRessourcesFinancieres().getAidesCAF().getAllocationRSA() != null && personne.getRessourcesFinancieres().getAidesCAF().getAllocationRSA() > 0;
    }

    public boolean hasAllocationARE(Personne personne) {
	return beneficiaireAidesUtile.isBeneficiaireARE(personne) && personne.getRessourcesFinancieres() != null && personne.getRessourcesFinancieres().getAidesPoleEmploi() != null
		&& personne.getRessourcesFinancieres().getAidesPoleEmploi().getAllocationARE() != null
		&& personne.getRessourcesFinancieres().getAidesPoleEmploi().getAllocationARE().getAllocationMensuelleNet() != null
		&& personne.getRessourcesFinancieres().getAidesPoleEmploi().getAllocationARE().getAllocationMensuelleNet() > 0;
    }

    public boolean hasAllocationASS(Personne personne) {
	return beneficiaireAidesUtile.isBeneficiaireASS(personne) && personne.getRessourcesFinancieres() != null && personne.getRessourcesFinancieres().getAidesPoleEmploi() != null
		&& personne.getRessourcesFinancieres().getAidesPoleEmploi().getAllocationASS() != null
		&& personne.getRessourcesFinancieres().getAidesPoleEmploi().getAllocationASS().getAllocationMensuelleNet() != null
		&& personne.getRessourcesFinancieres().getAidesPoleEmploi().getAllocationASS().getAllocationMensuelleNet() > 0;
    }
}
