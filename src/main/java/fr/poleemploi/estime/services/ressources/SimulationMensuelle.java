package fr.poleemploi.estime.services.ressources;

import java.time.LocalDate;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonFormat;

public class SimulationMensuelle {

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate datePremierJourMoisSimule;
    private Map<String, Aide> mesAides;

    public LocalDate getDatePremierJourMoisSimule() {
	return datePremierJourMoisSimule;
    }
    public void setDatePremierJourMoisSimule(LocalDate datePremierJourMoisSimule) {
	this.datePremierJourMoisSimule = datePremierJourMoisSimule;
    }
    public Map<String, Aide> getMesAides() {
	return mesAides;
    }
    public void setMesAides(Map<String, Aide> mesAides) {
	this.mesAides = mesAides;
    }

    @Override
    public String toString() {
	return "SimulationMensuelle [datePremierJourMoisSimule=" + datePremierJourMoisSimule + ", mesAides=" + mesAides
		+ "]";
    }   
}
