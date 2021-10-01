package fr.poleemploi.estime.logique.simulateur.aides.caf.utile;

import java.time.LocalDate;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fr.poleemploi.estime.services.ressources.Aide;
import fr.poleemploi.estime.services.ressources.DemandeurEmploi;
import fr.poleemploi.estime.services.ressources.SimulationAides;

@Component
public class PrimeActiviteAAHUtile {

    @Autowired
    private PrimeActiviteUtile primeActiviteUtile;

    public void simulerAide(SimulationAides simulationAides, Map<String, Aide> aidesPourCeMois, LocalDate dateDebutSimulation, int numeroMoisSimule, DemandeurEmploi demandeurEmploi) {

        int prochaineDeclarationTrimestrielle = demandeurEmploi.getRessourcesFinancieres().getAidesCAF().getProchaineDeclarationTrimestrielle();

        if (isPrimeActiviteACalculer(numeroMoisSimule, prochaineDeclarationTrimestrielle)) {
            primeActiviteUtile.reporterPrimeActivite(simulationAides, aidesPourCeMois, numeroMoisSimule);
        } else if (isPrimeActiviteAVerser(numeroMoisSimule, prochaineDeclarationTrimestrielle)) {
            primeActiviteUtile.calculerPrimeActivite(simulationAides, aidesPourCeMois, dateDebutSimulation, numeroMoisSimule, demandeurEmploi);
        } else {
            primeActiviteUtile.reporterPrimeActivite(simulationAides, aidesPourCeMois, numeroMoisSimule);
        }
    }

    private boolean isPrimeActiviteACalculer(int numeroMoisSimule, int prochaineDeclarationTrimestrielle) {
        return numeroMoisSimule == 1 || ((prochaineDeclarationTrimestrielle == numeroMoisSimule) || (prochaineDeclarationTrimestrielle == numeroMoisSimule - 3)
                || (prochaineDeclarationTrimestrielle == numeroMoisSimule - 6));

    }

    private boolean isPrimeActiviteAVerser(int numeroMoisSimule, int prochaineDeclarationTrimestrielle) {
        return numeroMoisSimule == 2 || ((prochaineDeclarationTrimestrielle == numeroMoisSimule - 1) || (prochaineDeclarationTrimestrielle == numeroMoisSimule - 4));
    }
}
