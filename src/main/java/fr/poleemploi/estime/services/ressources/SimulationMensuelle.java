package fr.poleemploi.estime.services.ressources;

import java.time.LocalDate;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonFormat;

public class SimulationMensuelle {

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate datePremierJourMoisSimule;
    private Map<String, RessourceFinanciere> ressourcesFinancieres;
    private Map<String, Aide> aides;

    public LocalDate getDatePremierJourMoisSimule() {
	return datePremierJourMoisSimule;
    }

    public void setDatePremierJourMoisSimule(LocalDate datePremierJourMoisSimule) {
	this.datePremierJourMoisSimule = datePremierJourMoisSimule;
    }

    public Map<String, RessourceFinanciere> getRessourcesFinancieres() {
	return ressourcesFinancieres;
    }

    public void setRessourcesFinancieres(Map<String, RessourceFinanciere> ressourcesFinancieres) {
	this.ressourcesFinancieres = ressourcesFinancieres;
    }

    public Map<String, Aide> getAides() {
	return aides;
    }

    public void setAides(Map<String, Aide> aides) {
	this.aides = aides;
    }

    @Override
    public String toString() {
	return "SimulationMensuelle [datePremierJourMoisSimule=" + datePremierJourMoisSimule + ", ressourcesFinancieres=" + ressourcesFinancieres + ", aides=" + aides + "]";
    }

}
