package fr.poleemploi.estime.logique.simulateur.aides.poleemploi;

import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fr.poleemploi.estime.commun.enumerations.AideEnum;
import fr.poleemploi.estime.commun.utile.demandeuremploi.BeneficiaireAidesUtile;
import fr.poleemploi.estime.logique.simulateur.aides.poleemploi.utile.AgepiUtile;
import fr.poleemploi.estime.logique.simulateur.aides.poleemploi.utile.AideMobiliteUtile;
import fr.poleemploi.estime.logique.simulateur.aides.poleemploi.utile.AllocationSolidariteSpecifiqueUtile;
import fr.poleemploi.estime.services.ressources.Aide;
import fr.poleemploi.estime.services.ressources.DemandeurEmploi;

@Component
public class SimulateurAidesPoleEmploi {
    
    @Autowired
    private AgepiUtile agepi;
    
    @Autowired
    private AideMobiliteUtile aideMobilite;
    
    @Autowired
    private AllocationSolidariteSpecifiqueUtile allocationSolidariteSpecifique;
    
    @Autowired
    private BeneficiaireAidesUtile beneficiaireAidesUtile;
    
    public void simuler(Map<String, Aide>  aidesPourCeMois, int numeroMoisSimule, LocalDate moisSimule, DemandeurEmploi demandeurEmploi, LocalDate dateDebutSimulation) {
        if(agepi.isEligible(numeroMoisSimule, demandeurEmploi)) {
            aidesPourCeMois.put(AideEnum.AGEPI.getCode(), agepi.simulerAide(demandeurEmploi));
        }
        if(aideMobilite.isEligible(numeroMoisSimule, demandeurEmploi)) {
            aidesPourCeMois.put(AideEnum.AIDE_MOBILITE.getCode(), aideMobilite.simulerAide(demandeurEmploi));
        }
        if(beneficiaireAidesUtile.isBeneficiaireASS(demandeurEmploi)
           && allocationSolidariteSpecifique.isEligible(numeroMoisSimule, demandeurEmploi)) {            
            Optional<Aide> aideOptional = allocationSolidariteSpecifique.simulerAide(demandeurEmploi, moisSimule, dateDebutSimulation);
            if(aideOptional.isPresent()) {
                aidesPourCeMois.put(AideEnum.ALLOCATION_SOLIDARITE_SPECIFIQUE.getCode(), aideOptional.get());                
            }
        }
    }
}
