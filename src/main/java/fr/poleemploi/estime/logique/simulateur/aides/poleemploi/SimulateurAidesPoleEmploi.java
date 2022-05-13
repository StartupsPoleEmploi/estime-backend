package fr.poleemploi.estime.logique.simulateur.aides.poleemploi;

import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fr.poleemploi.estime.commun.enumerations.AideEnum;
import fr.poleemploi.estime.commun.utile.StagingEnvironnementUtile;
import fr.poleemploi.estime.commun.utile.demandeuremploi.BeneficiaireAidesUtile;
import fr.poleemploi.estime.logique.simulateur.aides.poleemploi.utile.AgepiUtile;
import fr.poleemploi.estime.logique.simulateur.aides.poleemploi.utile.AideMobiliteUtile;
import fr.poleemploi.estime.logique.simulateur.aides.poleemploi.utile.AllocationSolidariteSpecifiqueUtile;
import fr.poleemploi.estime.logique.simulateur.aides.poleemploi.utile.AreUtile;
import fr.poleemploi.estime.services.ressources.Aide;
import fr.poleemploi.estime.services.ressources.DemandeurEmploi;

@Component
public class SimulateurAidesPoleEmploi {

    @Autowired
    private AgepiUtile agepiUtile;

    @Autowired
    private AideMobiliteUtile aideMobiliteUtile;

    @Autowired
    private AreUtile areUtile;

    @Autowired
    private AllocationSolidariteSpecifiqueUtile allocationSolidariteSpecifiqueUtile;

    @Autowired
    private BeneficiaireAidesUtile beneficiaireAidesUtile;

    @Autowired
    private StagingEnvironnementUtile stagingEnvironnementUtile;

    public void simuler(Map<String, Aide> aidesPourCeMois, int numeroMoisSimule, LocalDate moisSimule, DemandeurEmploi demandeurEmploi, LocalDate dateDebutSimulation) {

	//si l'utilisateur est un demandeur fictif, on ne peut pas simuler les aides necessitant un appel à une API peio sécurisée individu
	if (stagingEnvironnementUtile.isNotDemandeurFictif(demandeurEmploi)) {
	    simulerAidesByCallingApiPEIO(aidesPourCeMois, numeroMoisSimule, demandeurEmploi);
	}

	if (beneficiaireAidesUtile.isBeneficiaireASS(demandeurEmploi) && allocationSolidariteSpecifiqueUtile.isEligible(numeroMoisSimule, demandeurEmploi, dateDebutSimulation)) {
	    Optional<Aide> aideOptional = allocationSolidariteSpecifiqueUtile.simulerAide(demandeurEmploi, moisSimule, dateDebutSimulation);
	    if (aideOptional.isPresent()) {
		aidesPourCeMois.put(AideEnum.ALLOCATION_SOLIDARITE_SPECIFIQUE.getCode(), aideOptional.get());
	    }
	}
    }

    private void simulerAidesByCallingApiPEIO(Map<String, Aide> aidesPourCeMois, int numeroMoisSimule, DemandeurEmploi demandeurEmploi) {
	if (agepiUtile.isEligible(numeroMoisSimule, demandeurEmploi)) {
	    Optional<Aide> agepiOptional = agepiUtile.simulerAide(demandeurEmploi);
	    if (agepiOptional.isPresent()) {
		aidesPourCeMois.put(AideEnum.AGEPI.getCode(), agepiOptional.get());
	    }
	}

	if (aideMobiliteUtile.isEligible(numeroMoisSimule, demandeurEmploi)) {
	    Optional<Aide> aideMobiliteOptional = aideMobiliteUtile.simulerAide(demandeurEmploi);
	    if (aideMobiliteOptional.isPresent()) {
		aidesPourCeMois.put(AideEnum.AIDE_MOBILITE.getCode(), aideMobiliteOptional.get());
	    }
	}

	if (beneficiaireAidesUtile.isBeneficiaireARE(demandeurEmploi)) {
	    areUtile.simuler(aidesPourCeMois, demandeurEmploi, numeroMoisSimule);
	}
    }
}
