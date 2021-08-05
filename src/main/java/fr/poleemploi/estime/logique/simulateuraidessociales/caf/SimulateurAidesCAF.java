package fr.poleemploi.estime.logique.simulateuraidessociales.caf;

import java.time.LocalDate;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fr.poleemploi.estime.commun.utile.demandeuremploi.BeneficiaireAidesSocialesUtile;
import fr.poleemploi.estime.commun.utile.demandeuremploi.InformationsPersonnellesUtile;
import fr.poleemploi.estime.logique.simulateuraidessociales.caf.aides.AllocationAdultesHandicapes;
import fr.poleemploi.estime.logique.simulateuraidessociales.caf.aides.PrimeActivite;
import fr.poleemploi.estime.logique.simulateuraidessociales.caf.aides.RsaAvecPrimeActivite;
import fr.poleemploi.estime.services.ressources.AideSociale;
import fr.poleemploi.estime.services.ressources.DemandeurEmploi;
import fr.poleemploi.estime.services.ressources.SimulationAidesSociales;

@Component
public class SimulateurAidesCAF {
    
    //Pour la prime d'activité et le RSA , le majeur âgé de plus de 25 ans ne peut pas être considéré comme à charge
    public static final int AGE_MAX_PERSONNE_A_CHARGE_PPA_RSA = 25;
    
    @Autowired
    private AllocationAdultesHandicapes allocationAdultesHandicapes;
    
    @Autowired
    private BeneficiaireAidesSocialesUtile beneficiaireAidesSocialesUtile;
    
    @Autowired
    private InformationsPersonnellesUtile informationsPersonnellesUtile;
    
    @Autowired
    private PrimeActivite primeActivite;
    
    @Autowired
    private RsaAvecPrimeActivite rsaAvecPrimeActivite;
    
    public void simulerAidesCAF(SimulationAidesSociales simulationAidesSociales, Map<String, AideSociale>  aidesEligiblesPourCeMois, LocalDate dateDebutSimulation, int numeroMoisSimule, DemandeurEmploi demandeurEmploi) {
        if(isEligibleAidesCAF(demandeurEmploi)) {
            
            if(beneficiaireAidesSocialesUtile.isBeneficiaireAAH(demandeurEmploi)) {
                allocationAdultesHandicapes.simulerAAH(aidesEligiblesPourCeMois, numeroMoisSimule, demandeurEmploi);
            }
            
            
            if(beneficiaireAidesSocialesUtile.isBeneficiaireRSA(demandeurEmploi)) {
                rsaAvecPrimeActivite.simulerRsaAvecPrimeActivite(simulationAidesSociales, aidesEligiblesPourCeMois, dateDebutSimulation, numeroMoisSimule, demandeurEmploi);
            } else {
                primeActivite.simulerPrimeActivite(simulationAidesSociales, aidesEligiblesPourCeMois, dateDebutSimulation, numeroMoisSimule, demandeurEmploi);                
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
