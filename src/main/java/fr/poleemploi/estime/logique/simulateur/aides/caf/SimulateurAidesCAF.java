package fr.poleemploi.estime.logique.simulateur.aides.caf;

import java.time.LocalDate;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fr.poleemploi.estime.commun.utile.demandeuremploi.BeneficiaireAidesUtile;
import fr.poleemploi.estime.commun.utile.demandeuremploi.InformationsPersonnellesUtile;
import fr.poleemploi.estime.logique.simulateur.aides.caf.utile.AllocationAdultesHandicapesUtile;
import fr.poleemploi.estime.logique.simulateur.aides.caf.utile.PrimeActiviteUtile;
import fr.poleemploi.estime.logique.simulateur.aides.caf.utile.RsaAvecPrimeActiviteUtile;
import fr.poleemploi.estime.services.ressources.Aide;
import fr.poleemploi.estime.services.ressources.DemandeurEmploi;
import fr.poleemploi.estime.services.ressources.SimulationAides;

@Component
public class SimulateurAidesCAF {
    
    //Pour la prime d'activité et le RSA , le majeur âgé de plus de 25 ans ne peut pas être considéré comme à charge
    public static final int AGE_MAX_PERSONNE_A_CHARGE_PPA_RSA = 25;
    
    @Autowired
    private AllocationAdultesHandicapesUtile allocationAdultesHandicapes;
    
    @Autowired
    private BeneficiaireAidesUtile beneficiaireAidesUtile;
    
    @Autowired
    private InformationsPersonnellesUtile informationsPersonnellesUtile;
    
    @Autowired
    private PrimeActiviteUtile primeActivite;
    
    @Autowired
    private RsaAvecPrimeActiviteUtile rsaAvecPrimeActivite;
    
    public void simuler(SimulationAides simulationAides, Map<String, Aide>  aidesPourCeMois, LocalDate dateDebutSimulation, int numeroMoisSimule, DemandeurEmploi demandeurEmploi) {
        if(isEligibleAidesCAF(demandeurEmploi)) {            
            if(beneficiaireAidesUtile.isBeneficiaireAAH(demandeurEmploi)) {
                allocationAdultesHandicapes.simulerAide(aidesPourCeMois, numeroMoisSimule, demandeurEmploi);
            }                       
            if(beneficiaireAidesUtile.isBeneficiaireRSA(demandeurEmploi)) {
                rsaAvecPrimeActivite.simulerAides(simulationAides, aidesPourCeMois, dateDebutSimulation, numeroMoisSimule, demandeurEmploi);
            } else {
                primeActivite.simulerAide(simulationAides, aidesPourCeMois, dateDebutSimulation, numeroMoisSimule, demandeurEmploi);                
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