package fr.poleemploi.estime.logique.simulateur.aides.poleemploi;

import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fr.poleemploi.estime.commun.enumerations.Aides;
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
	private AgepiUtile agepi;

	@Autowired
	private AideMobiliteUtile aideMobilite;

	@Autowired
	private AreUtile are;

	@Autowired
	private AllocationSolidariteSpecifiqueUtile allocationSolidariteSpecifique;

	@Autowired
	private BeneficiaireAidesUtile beneficiaireAidesUtile;

	public void simuler(Map<String, Aide>  aidesPourCeMois, int numeroMoisSimule, LocalDate moisSimule, DemandeurEmploi demandeurEmploi, LocalDate dateDebutSimulation) {
		if(agepi.isEligible(numeroMoisSimule, demandeurEmploi)) {
			Optional<Aide> agepiOptional = agepi.simulerAide(demandeurEmploi);
			if(agepiOptional.isPresent()) {				
				aidesPourCeMois.put(Aides.AGEPI.getCode(), agepiOptional.get());
			}
		}
		Optional<Aide> aideMobiliteOptional = aideMobilite.simulerAide(demandeurEmploi);
		if(//aideMobilite.isEligible(numeroMoisSimule, demandeurEmploi) &&
				aideMobiliteOptional.isPresent()) {
			aidesPourCeMois.put(Aides.AIDE_MOBILITE.getCode(), aideMobiliteOptional.get());

			if(beneficiaireAidesUtile.isBeneficiaireASS(demandeurEmploi)
					&& allocationSolidariteSpecifique.isEligible(numeroMoisSimule, demandeurEmploi)) {            
				Optional<Aide> aideOptional = allocationSolidariteSpecifique.simulerAide(demandeurEmploi, moisSimule, dateDebutSimulation);
				if(aideOptional.isPresent()) {
					aidesPourCeMois.put(Aides.ALLOCATION_SOLIDARITE_SPECIFIQUE.getCode(), aideOptional.get());                
				}
			}
			if(beneficiaireAidesUtile.isBeneficiaireARE(demandeurEmploi)) {
				Optional<Aide> areOptional = are.simulerAide(demandeurEmploi);
				if(areOptional.isPresent()) {
					aidesPourCeMois.put(Aides.ALLOCATION_RETOUR_EMPLOI.getCode(), areOptional.get());
				}            
			}
		}
	}
}
