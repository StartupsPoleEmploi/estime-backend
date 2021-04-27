package fr.poleemploi.estime.logique.simulateuraidessociales.poleemploi;

import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fr.poleemploi.estime.commun.enumerations.AidesSociales;
import fr.poleemploi.estime.logique.simulateuraidessociales.poleemploi.aides.Agepi;
import fr.poleemploi.estime.logique.simulateuraidessociales.poleemploi.aides.AideMobilite;
import fr.poleemploi.estime.logique.simulateuraidessociales.poleemploi.aides.AllocationSolidariteSpecifique;
import fr.poleemploi.estime.services.ressources.AideSociale;
import fr.poleemploi.estime.services.ressources.DemandeurEmploi;

@Component
public class SimulateurAidesPoleEmploi {
    
    @Autowired
    private Agepi agepi;
    
    @Autowired
    private AideMobilite aideMobilite;
    
    @Autowired
    private AllocationSolidariteSpecifique allocationSolidariteSpecifique;
    
    
    public void simulerAidesPoleEmploi(Map<String, AideSociale>  aidesEligiblesPourCeMois, int numeroMoisSimule, LocalDate moisSimule, DemandeurEmploi demandeurEmploi, LocalDate dateDebutSimulation) {
        if(agepi.isEligible(numeroMoisSimule, demandeurEmploi)) {
            aidesEligiblesPourCeMois.put(AidesSociales.AGEPI.getCode(), agepi.calculer(demandeurEmploi));
        }
        if(aideMobilite.isEligible(numeroMoisSimule, demandeurEmploi)) {
            aidesEligiblesPourCeMois.put(AidesSociales.AIDE_MOBILITE.getCode(), aideMobilite.calculer(demandeurEmploi));
        }
        if(allocationSolidariteSpecifique.isEligible(numeroMoisSimule, demandeurEmploi)) {
            Optional<AideSociale> aideSocialOptional = allocationSolidariteSpecifique.calculer(demandeurEmploi, moisSimule, dateDebutSimulation);
            if(aideSocialOptional.isPresent()) {
                aidesEligiblesPourCeMois.put(AidesSociales.ALLOCATION_SOLIDARITE_SPECIFIQUE.getCode(), aideSocialOptional.get());                
            }
        }
    }
}
