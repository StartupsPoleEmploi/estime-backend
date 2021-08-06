package fr.poleemploi.estime.logique.simulateur.prestationssociales.caf;

import java.time.LocalDate;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fr.poleemploi.estime.commun.utile.demandeuremploi.BeneficiairePrestationsSocialesUtile;
import fr.poleemploi.estime.commun.utile.demandeuremploi.InformationsPersonnellesUtile;
import fr.poleemploi.estime.logique.simulateur.prestationssociales.caf.utile.AllocationAdultesHandicapesUtile;
import fr.poleemploi.estime.logique.simulateur.prestationssociales.caf.utile.PrimeActiviteUtile;
import fr.poleemploi.estime.logique.simulateur.prestationssociales.caf.utile.RsaAvecPrimeActiviteUtile;
import fr.poleemploi.estime.services.ressources.DemandeurEmploi;
import fr.poleemploi.estime.services.ressources.PrestationSociale;
import fr.poleemploi.estime.services.ressources.SimulationPrestationsSociales;

@Component
public class SimulateurPrestationsCAF {
    
    //Pour la prime d'activité et le RSA , le majeur âgé de plus de 25 ans ne peut pas être considéré comme à charge
    public static final int AGE_MAX_PERSONNE_A_CHARGE_PPA_RSA = 25;
    
    @Autowired
    private AllocationAdultesHandicapesUtile allocationAdultesHandicapes;
    
    @Autowired
    private BeneficiairePrestationsSocialesUtile beneficiairePrestationsSocialesUtile;
    
    @Autowired
    private InformationsPersonnellesUtile informationsPersonnellesUtile;
    
    @Autowired
    private PrimeActiviteUtile primeActivite;
    
    @Autowired
    private RsaAvecPrimeActiviteUtile rsaAvecPrimeActivite;
    
    public void simuler(SimulationPrestationsSociales simulationPrestationsSociales, Map<String, PrestationSociale>  prestationsSocialesPourCeMois, LocalDate dateDebutSimulation, int numeroMoisSimule, DemandeurEmploi demandeurEmploi) {
        if(isEligiblePrestationsCAF(demandeurEmploi)) {            
            if(beneficiairePrestationsSocialesUtile.isBeneficiaireAAH(demandeurEmploi)) {
                allocationAdultesHandicapes.simulerPrestationSociale(prestationsSocialesPourCeMois, numeroMoisSimule, demandeurEmploi);
            }                       
            if(beneficiairePrestationsSocialesUtile.isBeneficiaireRSA(demandeurEmploi)) {
                rsaAvecPrimeActivite.simulerPrestationsSociales(simulationPrestationsSociales, prestationsSocialesPourCeMois, dateDebutSimulation, numeroMoisSimule, demandeurEmploi);
            } else {
                primeActivite.simulerPrestationSociale(simulationPrestationsSociales, prestationsSocialesPourCeMois, dateDebutSimulation, numeroMoisSimule, demandeurEmploi);                
            }
        }           
    }
    
    private boolean isEligiblePrestationsCAF(DemandeurEmploi demandeurEmploi) {
        return informationsPersonnellesUtile.isFrancais(demandeurEmploi.getInformationsPersonnelles())
                || informationsPersonnellesUtile.isEuropeenOuSuisse(demandeurEmploi.getInformationsPersonnelles())
                || (informationsPersonnellesUtile.isNotFrancaisOuEuropeenOuSuisse(demandeurEmploi.getInformationsPersonnelles())
                        && informationsPersonnellesUtile.isTitreSejourEnFranceValide(demandeurEmploi.getInformationsPersonnelles()));            
    }
}
