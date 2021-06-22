package fr.poleemploi.estime.logique.simulateuraidessociales.caf;

import java.time.LocalDate;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fr.poleemploi.estime.commun.utile.demandeuremploi.BeneficiaireAidesSocialesUtile;
import fr.poleemploi.estime.commun.utile.demandeuremploi.InformationsPersonnellesUtile;
import fr.poleemploi.estime.logique.simulateuraidessociales.caf.aides.AllocationAdultesHandicapes;
import fr.poleemploi.estime.logique.simulateuraidessociales.caf.aides.PrimeActivite;
import fr.poleemploi.estime.logique.simulateuraidessociales.caf.aides.RSA;
import fr.poleemploi.estime.services.ressources.AideSociale;
import fr.poleemploi.estime.services.ressources.DemandeurEmploi;
import fr.poleemploi.estime.services.ressources.SimulationAidesSociales;

@Component
public class SimulateurAidesCAF {
    
    @Autowired
    private AllocationAdultesHandicapes allocationAdultesHandicapes;
    
    @Autowired
    private BeneficiaireAidesSocialesUtile beneficiaireAidesSocialesUtile;
    
    @Autowired
    private InformationsPersonnellesUtile informationsPersonnellesUtile;
    
    @Autowired
    private PrimeActivite primeActivite;
    
    @Autowired
    private RSA rsa;
    
    public void simulerAidesCAF(SimulationAidesSociales simulationAidesSociales, Map<String, AideSociale>  aidesEligiblesPourCeMois, LocalDate dateDebutSimulation, int numeroMoisSimule, DemandeurEmploi demandeurEmploi) {
        if(isEligibleAidesCAF(demandeurEmploi)) {
            
            primeActivite.simulerPrimeActivite(simulationAidesSociales, aidesEligiblesPourCeMois, dateDebutSimulation, numeroMoisSimule, demandeurEmploi);
            
            if(beneficiaireAidesSocialesUtile.isBeneficiaireAAH(demandeurEmploi)) {
                allocationAdultesHandicapes.simulerAAH(aidesEligiblesPourCeMois, numeroMoisSimule, demandeurEmploi);
            }
            if(beneficiaireAidesSocialesUtile.isBeneficiaireRSA(demandeurEmploi)) {
                rsa.simulerRSA(simulationAidesSociales, aidesEligiblesPourCeMois, dateDebutSimulation, numeroMoisSimule, demandeurEmploi);
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
