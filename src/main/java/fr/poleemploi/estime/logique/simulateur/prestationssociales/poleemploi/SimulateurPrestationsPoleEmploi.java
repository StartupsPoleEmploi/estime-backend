package fr.poleemploi.estime.logique.simulateur.prestationssociales.poleemploi;

import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fr.poleemploi.estime.commun.enumerations.PrestationsSociales;
import fr.poleemploi.estime.logique.simulateur.prestationssociales.poleemploi.utile.AgepiUtile;
import fr.poleemploi.estime.logique.simulateur.prestationssociales.poleemploi.utile.AideMobiliteUtile;
import fr.poleemploi.estime.logique.simulateur.prestationssociales.poleemploi.utile.AllocationSolidariteSpecifiqueUtile;
import fr.poleemploi.estime.services.ressources.DemandeurEmploi;
import fr.poleemploi.estime.services.ressources.PrestationSociale;

@Component
public class SimulateurPrestationsPoleEmploi {
    
    @Autowired
    private AgepiUtile agepi;
    
    @Autowired
    private AideMobiliteUtile aideMobilite;
    
    @Autowired
    private AllocationSolidariteSpecifiqueUtile allocationSolidariteSpecifique;
    
    
    public void simuler(Map<String, PrestationSociale>  prestationsSocialesPourCeMois, int numeroMoisSimule, LocalDate moisSimule, DemandeurEmploi demandeurEmploi, LocalDate dateDebutSimulation) {
        if(agepi.isEligible(numeroMoisSimule, demandeurEmploi)) {
            prestationsSocialesPourCeMois.put(PrestationsSociales.AGEPI.getCode(), agepi.simulerPrestationSociale(demandeurEmploi));
        }
        if(aideMobilite.isEligible(numeroMoisSimule, demandeurEmploi)) {
            prestationsSocialesPourCeMois.put(PrestationsSociales.AIDE_MOBILITE.getCode(), aideMobilite.simulerPrestationSociale(demandeurEmploi));
        }
        if(allocationSolidariteSpecifique.isEligible(numeroMoisSimule, demandeurEmploi)) {
            Optional<PrestationSociale> prestationSocialeOptional = allocationSolidariteSpecifique.simulerPrestationSociale(demandeurEmploi, moisSimule, dateDebutSimulation);
            if(prestationSocialeOptional.isPresent()) {
                prestationsSocialesPourCeMois.put(PrestationsSociales.ALLOCATION_SOLIDARITE_SPECIFIQUE.getCode(), prestationSocialeOptional.get());                
            }
        }
    }
}
