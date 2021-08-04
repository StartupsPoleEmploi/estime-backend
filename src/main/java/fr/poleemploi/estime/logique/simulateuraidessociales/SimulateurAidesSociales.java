package fr.poleemploi.estime.logique.simulateuraidessociales;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fr.poleemploi.estime.commun.utile.DateUtile;
import fr.poleemploi.estime.commun.utile.demandeuremploi.RessourcesFinancieresUtile;
import fr.poleemploi.estime.logique.simulateuraidessociales.caf.SimulateurAidesCAF;
import fr.poleemploi.estime.logique.simulateuraidessociales.poleemploi.SimulateurAidesPoleEmploi;
import fr.poleemploi.estime.logique.simulateuraidessociales.utile.SimulateurAidesSocialesUtile;
import fr.poleemploi.estime.services.ressources.AideSociale;
import fr.poleemploi.estime.services.ressources.DemandeurEmploi;
import fr.poleemploi.estime.services.ressources.SimulationAidesSociales;
import fr.poleemploi.estime.services.ressources.SimulationMensuelle;

@Component
public class SimulateurAidesSociales {

    @Autowired
    private DateUtile dateUtile;

    @Autowired
    private SimulateurAidesSocialesUtile simulateurAidesSocialesUtile;

    @Autowired
    private RessourcesFinancieresUtile ressourcesFinancieresUtile;
    
    @Autowired
    private SimulateurAidesCAF simulateurAidesCAF;

    @Autowired
    private SimulateurAidesPoleEmploi simulateurAidesPoleEmploi;

    
    public SimulationAidesSociales simuler(DemandeurEmploi demandeurEmploi) {
        SimulationAidesSociales simulationAidesSociales = new SimulationAidesSociales();       
        simulationAidesSociales.setSimulationsMensuelles(new ArrayList<>());

        LocalDate dateDemandeSimulation = dateUtile.getDateJour();
        LocalDate dateDebutSimulation = simulateurAidesSocialesUtile.getDateDebutSimulation(dateDemandeSimulation);

        simulationAidesSociales.setMontantRessourcesFinancieresMoisAvantSimulation(ressourcesFinancieresUtile.calculerMontantRessourcesFinancieresMoisAvantSimulation(demandeurEmploi));

        int nombreMoisASimuler = simulateurAidesSocialesUtile.getNombreMoisASimuler(demandeurEmploi);

        for (int numeroMoisSimule = 1; numeroMoisSimule <= nombreMoisASimuler; numeroMoisSimule++) {    
            simulerAidesPourCeMois(simulationAidesSociales, dateDebutSimulation, numeroMoisSimule, demandeurEmploi);
        }
        return simulationAidesSociales;
    }

    private void simulerAidesPourCeMois(SimulationAidesSociales simulationAidesSociales, LocalDate dateDebutSimulation, int numeroMoisSimule, DemandeurEmploi demandeurEmploi) {
        LocalDate dateMoisASimuler = getDateMoisASimuler(dateDebutSimulation, numeroMoisSimule);

        SimulationMensuelle simulationMensuelle = new SimulationMensuelle();
        simulationMensuelle.setDatePremierJourMoisSimule(dateMoisASimuler);                           
        simulationAidesSociales.getSimulationsMensuelles().add(simulationMensuelle);

        HashMap<String, AideSociale> aidesEligiblesPourCeMois = new HashMap<>();
        simulationMensuelle.setMesAides(aidesEligiblesPourCeMois);

        simulateurAidesCAF.simulerAidesCAF(simulationAidesSociales, aidesEligiblesPourCeMois, dateDebutSimulation, numeroMoisSimule, demandeurEmploi);
        simulateurAidesPoleEmploi.simulerAidesPoleEmploi(aidesEligiblesPourCeMois, numeroMoisSimule, dateMoisASimuler, demandeurEmploi, dateDebutSimulation);


    }
    
    private LocalDate getDateMoisASimuler(LocalDate dateDebutSimulation, int numeroMoisSimule) {
        int nombreMoisToAdd = numeroMoisSimule - 1;
        return dateUtile.ajouterMoisALocalDate(dateDebutSimulation, nombreMoisToAdd);
    }
}
