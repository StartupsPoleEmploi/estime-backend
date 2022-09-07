package fr.poleemploi.estime.logique.simulateur.aides.caf;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fr.poleemploi.estime.commun.utile.demandeuremploi.BeneficiaireAidesUtile;
import fr.poleemploi.estime.commun.utile.demandeuremploi.InformationsPersonnellesUtile;
import fr.poleemploi.estime.logique.simulateur.aides.caf.utile.AidesFamilialesUtile;
import fr.poleemploi.estime.logique.simulateur.aides.caf.utile.AllocationAdultesHandicapesUtile;
import fr.poleemploi.estime.services.ressources.Aide;
import fr.poleemploi.estime.services.ressources.DemandeurEmploi;

@Component
public class SimulateurAidesCAF {

    // Pour la prime d'activité et le RSA , le majeur âgé de plus de 25 ans ne peut pas être considéré comme à charge
    public static final int AGE_MAX_PERSONNE_A_CHARGE_PPA_RSA = 25;

    @Autowired
    private AllocationAdultesHandicapesUtile allocationAdultesHandicapesUtile;

    @Autowired
    private AidesFamilialesUtile aidesFamilialesUtile;

    @Autowired
    private BeneficiaireAidesUtile beneficiaireAidesUtile;

    @Autowired
    private InformationsPersonnellesUtile informationsPersonnellesUtile;

    public void simuler(Map<String, Aide> aidesPourCeMois, int numeroMoisSimule, DemandeurEmploi demandeurEmploi) {
	if (isEligibleAidesCAF(demandeurEmploi)) {
	    if (beneficiaireAidesUtile.isBeneficiaireAAH(demandeurEmploi)) {
		allocationAdultesHandicapesUtile.simulerAide(aidesPourCeMois, numeroMoisSimule, demandeurEmploi);
	    }
	    if (aidesFamilialesUtile.isEligibleAidesFamiliales(demandeurEmploi, numeroMoisSimule)) {
		aidesFamilialesUtile.simulerAidesFamiliales(aidesPourCeMois, demandeurEmploi, numeroMoisSimule);
	    }
	}
    }

    private boolean isEligibleAidesCAF(DemandeurEmploi demandeurEmploi) {
	return informationsPersonnellesUtile.isFrancais(demandeurEmploi.getInformationsPersonnelles())
		|| informationsPersonnellesUtile.isEuropeenOuSuisse(demandeurEmploi.getInformationsPersonnelles())
		|| (informationsPersonnellesUtile.isNotFrancaisOuEuropeenOuSuisse(demandeurEmploi.getInformationsPersonnelles())
			&& informationsPersonnellesUtile.isTitreSejourEnFranceValide(demandeurEmploi.getInformationsPersonnelles()));
    }
}
