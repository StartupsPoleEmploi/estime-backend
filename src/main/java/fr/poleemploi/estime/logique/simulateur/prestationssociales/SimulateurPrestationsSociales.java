package fr.poleemploi.estime.logique.simulateur.prestationssociales;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fr.poleemploi.estime.commun.utile.DateUtile;
import fr.poleemploi.estime.commun.utile.demandeuremploi.RessourcesFinancieresUtile;
import fr.poleemploi.estime.logique.simulateur.prestationssociales.caf.SimulateurPrestationsCAF;
import fr.poleemploi.estime.logique.simulateur.prestationssociales.poleemploi.SimulateurPrestationsPoleEmploi;
import fr.poleemploi.estime.logique.simulateur.prestationssociales.utile.SimulateurPrestationsSocialesUtile;
import fr.poleemploi.estime.services.ressources.DemandeurEmploi;
import fr.poleemploi.estime.services.ressources.PrestationSociale;
import fr.poleemploi.estime.services.ressources.SimulationMensuelle;
import fr.poleemploi.estime.services.ressources.SimulationPrestationsSociales;

@Component
public class SimulateurPrestationsSociales {

    @Autowired
    private DateUtile dateUtile;

    @Autowired
    private SimulateurPrestationsSocialesUtile simulateurPrestationsSocialesUtile;

    @Autowired
    private RessourcesFinancieresUtile ressourcesFinancieresUtile;
    
    @Autowired
    private SimulateurPrestationsCAF simulateurPrestationsCAF;

    @Autowired
    private SimulateurPrestationsPoleEmploi simulateurPrestationsPoleEmploi;

    
    public SimulationPrestationsSociales simuler(DemandeurEmploi demandeurEmploi) {
        SimulationPrestationsSociales simulationPrestationsSociales = new SimulationPrestationsSociales();       
        simulationPrestationsSociales.setSimulationsMensuelles(new ArrayList<>());

        LocalDate dateDemandeSimulation = dateUtile.getDateJour();
        LocalDate dateDebutSimulation = simulateurPrestationsSocialesUtile.getDateDebutSimulation(dateDemandeSimulation);

        simulationPrestationsSociales.setMontantRessourcesFinancieresMoisAvantSimulation(ressourcesFinancieresUtile.calculerMontantRessourcesFinancieresMoisAvantSimulation(demandeurEmploi));

        int nombreMoisASimuler = simulateurPrestationsSocialesUtile.getNombreMoisASimuler(demandeurEmploi);

        for (int numeroMoisSimule = 1; numeroMoisSimule <= nombreMoisASimuler; numeroMoisSimule++) {    
            simulerPrestationsSocialesPourCeMois(simulationPrestationsSociales, dateDebutSimulation, numeroMoisSimule, demandeurEmploi);
        }
        return simulationPrestationsSociales;
    }

    private void simulerPrestationsSocialesPourCeMois(SimulationPrestationsSociales simulationPrestationsSociales, LocalDate dateDebutSimulation, int numeroMoisSimule, DemandeurEmploi demandeurEmploi) {
        LocalDate dateMoisASimuler = getDateMoisASimuler(dateDebutSimulation, numeroMoisSimule);

        SimulationMensuelle simulationMensuelle = new SimulationMensuelle();
        simulationMensuelle.setDatePremierJourMoisSimule(dateMoisASimuler);                           
        simulationPrestationsSociales.getSimulationsMensuelles().add(simulationMensuelle);

        HashMap<String, PrestationSociale> prestationsSocialesPourCeMois = new HashMap<>();
        simulationMensuelle.setMesPrestationsSociales(prestationsSocialesPourCeMois);

        simulateurPrestationsCAF.simuler(simulationPrestationsSociales, prestationsSocialesPourCeMois, dateDebutSimulation, numeroMoisSimule, demandeurEmploi);
        simulateurPrestationsPoleEmploi.simuler(prestationsSocialesPourCeMois, numeroMoisSimule, dateMoisASimuler, demandeurEmploi, dateDebutSimulation);
    }
    
    private LocalDate getDateMoisASimuler(LocalDate dateDebutSimulation, int numeroMoisSimule) {
        int nombreMoisToAdd = numeroMoisSimule - 1;
        return dateUtile.ajouterMoisALocalDate(dateDebutSimulation, nombreMoisToAdd);
    }
}
