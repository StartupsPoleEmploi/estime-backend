package fr.poleemploi.estime.logique.simulateur.aides.caf;

import java.time.LocalDate;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fr.poleemploi.estime.commun.utile.demandeuremploi.BeneficiaireAidesUtile;
import fr.poleemploi.estime.commun.utile.demandeuremploi.InformationsPersonnellesUtile;
import fr.poleemploi.estime.commun.utile.demandeuremploi.RessourcesFinancieresUtile;
import fr.poleemploi.estime.commun.utile.demandeuremploi.SituationFamilialeUtile;
import fr.poleemploi.estime.logique.simulateur.aides.caf.utile.AidesFamilialesUtile;
import fr.poleemploi.estime.logique.simulateur.aides.caf.utile.AllocationAdultesHandicapesUtile;
import fr.poleemploi.estime.logique.simulateur.aides.caf.utile.PrimeActiviteAvecAidesFamilialesUtile;
import fr.poleemploi.estime.logique.simulateur.aides.caf.utile.PrimeActiviteUtile;
import fr.poleemploi.estime.logique.simulateur.aides.caf.utile.RsaAvecPrimeActiviteUtile;
import fr.poleemploi.estime.services.ressources.Aide;
import fr.poleemploi.estime.services.ressources.DemandeurEmploi;
import fr.poleemploi.estime.services.ressources.SimulationAides;

@Component
public class SimulateurAidesCAF {

    // Pour la prime d'activité et le RSA , le majeur âgé de plus de 25 ans ne peut pas être considéré comme à charge
    public static final int AGE_MAX_PERSONNE_A_CHARGE_PPA_RSA = 25;

    @Autowired
    private AllocationAdultesHandicapesUtile allocationAdultesHandicapes;

    @Autowired
    private AidesFamilialesUtile aidesFamilialesUtile;

    @Autowired
    private BeneficiaireAidesUtile beneficiaireAidesUtile;

    @Autowired
    private InformationsPersonnellesUtile informationsPersonnellesUtile;

    @Autowired
    private PrimeActiviteUtile primeActivite;
    
    @Autowired
    private PrimeActiviteAvecAidesFamilialesUtile primeActiviteAvecAidesFamiliales;

    @Autowired
    private RessourcesFinancieresUtile ressourcesFinancieresUtile;

    @Autowired
    private RsaAvecPrimeActiviteUtile rsaAvecPrimeActivite;

    @Autowired
    private SituationFamilialeUtile situationFamilialeUtile;

    public void simuler(SimulationAides simulationAides, Map<String, Aide> aidesPourCeMois, LocalDate dateDebutSimulation, int numeroMoisSimule, DemandeurEmploi demandeurEmploi) {
        if (isEligibleAidesCAF(demandeurEmploi)) {
            if (beneficiaireAidesUtile.isBeneficiaireAAH(demandeurEmploi)) {
                allocationAdultesHandicapes.simulerAide(aidesPourCeMois, numeroMoisSimule, demandeurEmploi);
            }
            if (beneficiaireAidesUtile.isBeneficiaireRSA(demandeurEmploi)) {
                rsaAvecPrimeActivite.simulerAides(simulationAides, aidesPourCeMois, dateDebutSimulation, numeroMoisSimule, demandeurEmploi);
            } else if (beneficiaireAidesUtile.isBeneficiaireAidesFamiliales(demandeurEmploi)) {
                primeActiviteAvecAidesFamiliales.simulerAide(simulationAides, aidesPourCeMois, dateDebutSimulation, numeroMoisSimule, demandeurEmploi);
            } else {
                primeActivite.simulerAide(simulationAides, aidesPourCeMois, dateDebutSimulation, numeroMoisSimule, demandeurEmploi);
            }
            if (isEligibleAllocationsFamiliales(demandeurEmploi)) {
                aidesFamilialesUtile.simulerAllocationsFamiliales(aidesPourCeMois, demandeurEmploi);
            }
            if (isEligibleAllocationSoutienFamilial(demandeurEmploi)) {
                aidesFamilialesUtile.simulerAllocationSoutienFamilial(aidesPourCeMois, demandeurEmploi);
            }
            if (isEligibleComplementFamilial(demandeurEmploi)) {
                aidesFamilialesUtile.simulerComplementFamilial(aidesPourCeMois, demandeurEmploi);
            }
            if (isEligiblePrestationAccueilJeuneEnfant(demandeurEmploi, numeroMoisSimule)) {
                aidesFamilialesUtile.simulerPrestationAccueilJeuneEnfant(aidesPourCeMois, demandeurEmploi);
            } 
        }
    }

    private boolean isEligibleAidesCAF(DemandeurEmploi demandeurEmploi) {
        return informationsPersonnellesUtile.isFrancais(demandeurEmploi.getInformationsPersonnelles())
                || informationsPersonnellesUtile.isEuropeenOuSuisse(demandeurEmploi.getInformationsPersonnelles())
                || (informationsPersonnellesUtile.isNotFrancaisOuEuropeenOuSuisse(demandeurEmploi.getInformationsPersonnelles())
                        && informationsPersonnellesUtile.isTitreSejourEnFranceValide(demandeurEmploi.getInformationsPersonnelles()));
    }

    private boolean isEligibleAllocationsFamiliales(DemandeurEmploi demandeurEmploi) {
        return ressourcesFinancieresUtile.hasAllocationsFamiliales(demandeurEmploi);
    }

    private boolean isEligibleAllocationSoutienFamilial(DemandeurEmploi demandeurEmploi) {
        return ressourcesFinancieresUtile.hasAllocationSoutienFamilial(demandeurEmploi);
    }

    private boolean isEligibleComplementFamilial(DemandeurEmploi demandeurEmploi) {
        return ressourcesFinancieresUtile.hasComplementFamilial(demandeurEmploi);
    }

    private boolean isEligiblePrestationAccueilJeuneEnfant(DemandeurEmploi demandeurEmploi, int numeroMoisSimule) {
        return ressourcesFinancieresUtile.hasPrestationAccueilJeuneEnfant(demandeurEmploi) && situationFamilialeUtile.hasEnfantMoinsDe3AnsPourMoisSimule(demandeurEmploi, numeroMoisSimule);
    }
}
