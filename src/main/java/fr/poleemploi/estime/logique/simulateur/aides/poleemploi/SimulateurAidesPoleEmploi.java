package fr.poleemploi.estime.logique.simulateur.aides.poleemploi;

import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fr.poleemploi.estime.commun.enumerations.AideEnum;
import fr.poleemploi.estime.commun.utile.demandeuremploi.BeneficiaireAidesUtile;
import fr.poleemploi.estime.logique.simulateur.aides.poleemploi.utile.AllocationSolidariteSpecifiqueUtile;
import fr.poleemploi.estime.services.ressources.Aide;
import fr.poleemploi.estime.services.ressources.DemandeurEmploi;

@Component
public class SimulateurAidesPoleEmploi {

    @Autowired
    private AllocationSolidariteSpecifiqueUtile allocationSolidariteSpecifiqueUtile;

    @Autowired
    private BeneficiaireAidesUtile beneficiaireAidesUtile;

    public void simuler(Map<String, Aide> aidesPourCeMois, int numeroMoisSimule, DemandeurEmploi demandeurEmploi, LocalDate dateDebutSimulation) {

	if (beneficiaireAidesUtile.isBeneficiaireASS(demandeurEmploi) && allocationSolidariteSpecifiqueUtile.isEligible(numeroMoisSimule, demandeurEmploi, dateDebutSimulation)) {
	    Optional<Aide> aideOptional = allocationSolidariteSpecifiqueUtile.simulerAide(demandeurEmploi, numeroMoisSimule, dateDebutSimulation);
	    if (aideOptional.isPresent()) {
		aidesPourCeMois.put(AideEnum.ALLOCATION_SOLIDARITE_SPECIFIQUE.getCode(), aideOptional.get());
	    }
	}
    }
}
