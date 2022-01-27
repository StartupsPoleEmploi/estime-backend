package fr.poleemploi.estime.services.ressources;

import java.util.List;

public class SimulationAides {

    private float montantRessourcesFinancieresMoisAvantSimulation;
    private List<SimulationMensuelle> simulationsMensuelles;

    public List<SimulationMensuelle> getSimulationsMensuelles() {
	return simulationsMensuelles;
    }
    public void setSimulationsMensuelles(List<SimulationMensuelle> simulationsMensuelles) {
	this.simulationsMensuelles = simulationsMensuelles;
    }
    public float getMontantRessourcesFinancieresMoisAvantSimulation() {
	return montantRessourcesFinancieresMoisAvantSimulation;
    }
    public void setMontantRessourcesFinancieresMoisAvantSimulation(float montantRessourcesFinancieresMoisAvantSimulation) {
	this.montantRessourcesFinancieresMoisAvantSimulation = montantRessourcesFinancieresMoisAvantSimulation;
    }

    @Override
    public String toString() {
	return "SimulationAides [montantRessourcesFinancieresMoisAvantSimulation="
		+ montantRessourcesFinancieresMoisAvantSimulation + ", simulationsMensuelles=" + simulationsMensuelles
		+ "]";
    }
}
