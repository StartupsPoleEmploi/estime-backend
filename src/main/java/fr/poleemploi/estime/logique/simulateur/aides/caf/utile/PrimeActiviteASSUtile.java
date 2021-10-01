package fr.poleemploi.estime.logique.simulateur.aides.caf.utile;

import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fr.poleemploi.estime.commun.utile.demandeuremploi.BeneficiaireAidesUtile;
import fr.poleemploi.estime.commun.utile.demandeuremploi.PeriodeTravailleeAvantSimulationUtile;
import fr.poleemploi.estime.commun.utile.demandeuremploi.RessourcesFinancieresUtile;
import fr.poleemploi.estime.services.ressources.Aide;
import fr.poleemploi.estime.services.ressources.DemandeurEmploi;
import fr.poleemploi.estime.services.ressources.SimulationAides;

@Component
public class PrimeActiviteASSUtile {

    @Autowired
    private BeneficiaireAidesUtile beneficiaireAidesUtile;

    @Autowired
    private PeriodeTravailleeAvantSimulationUtile periodeTravailleeAvantSimulationUtile;

    @Autowired
    private RessourcesFinancieresUtile ressourcesFinancieresUtile;
    
    @Autowired
    private PrimeActiviteUtile primeActiviteUtile;

    public void simulerAide(SimulationAides simulationAides, Map<String, Aide> aidesPourCeMois, LocalDate dateDebutSimulation, int numeroMoisSimule, DemandeurEmploi demandeurEmploi) {
        Optional<Aide> primeActiviteMoisPrecedent = primeActiviteUtile.getPrimeActiviteMoisPrecedent(simulationAides, numeroMoisSimule);
        if (primeActiviteMoisPrecedent.isPresent()) {
            primeActiviteUtile.reporterPrimeActiviteMoisPrecedent(simulationAides, aidesPourCeMois, dateDebutSimulation, numeroMoisSimule, demandeurEmploi, primeActiviteMoisPrecedent);
        } else if (isPrimeActiviteACalculer(primeActiviteMoisPrecedent, numeroMoisSimule, demandeurEmploi)) {
            primeActiviteUtile.calculerPrimeActivite(simulationAides, aidesPourCeMois, dateDebutSimulation, numeroMoisSimule, demandeurEmploi);
        }
    }

    private boolean isPrimeActiviteACalculer(Optional<Aide> primeActiviteMoisPrecedent, int numeroMoisSimule, DemandeurEmploi demandeurEmploi) {
        if (primeActiviteMoisPrecedent.isEmpty()) {
            // Si le demandeur est bénéficiare de l'ASS et de l'AAH, c'est la temporatlité de l'ASS qui est appliquée
            if (beneficiaireAidesUtile.isBeneficiaireASS(demandeurEmploi) || (beneficiaireAidesUtile.isBeneficiaireASS(demandeurEmploi) && beneficiaireAidesUtile.isBeneficiaireAAH(demandeurEmploi))) {
                return isPrimeActiviteACalculerDemandeurASS(demandeurEmploi, numeroMoisSimule);
            } else if (beneficiaireAidesUtile.isBeneficiaireAAH(demandeurEmploi)) {
                return isPrimeActiviteACalculerDemandeurAAH(numeroMoisSimule);
            }
        }
        return false;
    }

    private boolean isPrimeActiviteACalculerDemandeurASS(DemandeurEmploi demandeurEmploi, int numeroMoisSimule) {
        return !ressourcesFinancieresUtile.hasTravailleAuCoursDerniersMoisAvantSimulation(demandeurEmploi) && numeroMoisSimule == 5
                || (ressourcesFinancieresUtile.hasTravailleAuCoursDerniersMoisAvantSimulation(demandeurEmploi) && isMoisPourCalculPrimeActiviteASS(numeroMoisSimule, demandeurEmploi));
    }

    private boolean isPrimeActiviteACalculerDemandeurAAH(int numeroMoisSimule) {
        return numeroMoisSimule == 2 || numeroMoisSimule == 5;
    }

    private boolean isMoisPourCalculPrimeActiviteASS(int numeroMoisSimule, DemandeurEmploi demandeurEmploi) {
        int nombreMoisTravaillesDerniersMois = periodeTravailleeAvantSimulationUtile.getNombreMoisTravaillesAuCoursDes3DerniersMoisAvantSimulation(demandeurEmploi);
        return nombreMoisTravaillesDerniersMois == 1 && numeroMoisSimule == 4 || (nombreMoisTravaillesDerniersMois == 2 && (numeroMoisSimule == 3 || numeroMoisSimule == 6))
                || (nombreMoisTravaillesDerniersMois == 3 && (numeroMoisSimule == 2 || numeroMoisSimule == 5));
    }

}
