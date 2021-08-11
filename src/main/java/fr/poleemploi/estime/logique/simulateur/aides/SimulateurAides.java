package fr.poleemploi.estime.logique.simulateur.aides;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fr.poleemploi.estime.commun.utile.DateUtile;
import fr.poleemploi.estime.commun.utile.demandeuremploi.RessourcesFinancieresUtile;
import fr.poleemploi.estime.logique.simulateur.aides.caf.SimulateurAidesCAF;
import fr.poleemploi.estime.logique.simulateur.aides.poleemploi.SimulateurAidesPoleEmploi;
import fr.poleemploi.estime.logique.simulateur.aides.utile.SimulateurAidesUtile;
import fr.poleemploi.estime.services.ressources.Aide;
import fr.poleemploi.estime.services.ressources.DemandeurEmploi;
import fr.poleemploi.estime.services.ressources.SimulationAides;
import fr.poleemploi.estime.services.ressources.SimulationMensuelle;

@Component
public class SimulateurAides {

    @Autowired
    private DateUtile dateUtile;

    @Autowired
    private SimulateurAidesUtile simulateurAidesUtile;

    @Autowired
    private RessourcesFinancieresUtile ressourcesFinancieresUtile;
    
    @Autowired
    private SimulateurAidesCAF simulateurAidesCAF;

    @Autowired
    private SimulateurAidesPoleEmploi simulateurAidesPoleEmploi;

    
    public SimulationAides simuler(DemandeurEmploi demandeurEmploi) {
        SimulationAides simulationAides = new SimulationAides();       
        simulationAides.setSimulationsMensuelles(new ArrayList<>());

        LocalDate dateDemandeSimulation = dateUtile.getDateJour();
        LocalDate dateDebutSimulation = simulateurAidesUtile.getDateDebutSimulation(dateDemandeSimulation);

        simulationAides.setMontantRessourcesFinancieresMoisAvantSimulation(ressourcesFinancieresUtile.calculerMontantRessourcesFinancieresMoisAvantSimulation(demandeurEmploi));

        int nombreMoisASimuler = simulateurAidesUtile.getNombreMoisASimuler(demandeurEmploi);

        for (int numeroMoisSimule = 1; numeroMoisSimule <= nombreMoisASimuler; numeroMoisSimule++) {    
            simulerAidesPourCeMois(simulationAides, dateDebutSimulation, numeroMoisSimule, demandeurEmploi);
        }
        return simulationAides;
    }

    private void simulerAidesPourCeMois(SimulationAides simulationAides, LocalDate dateDebutSimulation, int numeroMoisSimule, DemandeurEmploi demandeurEmploi) {
        LocalDate dateMoisASimuler = getDateMoisASimuler(dateDebutSimulation, numeroMoisSimule);

        SimulationMensuelle simulationMensuelle = new SimulationMensuelle();
        simulationMensuelle.setDatePremierJourMoisSimule(dateMoisASimuler);                           
        simulationAides.getSimulationsMensuelles().add(simulationMensuelle);

        HashMap<String, Aide> aidesPourCeMois = new HashMap<>();
        simulationMensuelle.setMesAides(aidesPourCeMois);

        simulateurAidesCAF.simuler(simulationAides, aidesPourCeMois, dateDebutSimulation, numeroMoisSimule, demandeurEmploi);
        simulateurAidesPoleEmploi.simuler(aidesPourCeMois, numeroMoisSimule, dateMoisASimuler, demandeurEmploi, dateDebutSimulation);
    }
    
    private LocalDate getDateMoisASimuler(LocalDate dateDebutSimulation, int numeroMoisSimule) {
        int nombreMoisToAdd = numeroMoisSimule - 1;
        return dateUtile.ajouterMoisALocalDate(dateDebutSimulation, nombreMoisToAdd);
    }
}
