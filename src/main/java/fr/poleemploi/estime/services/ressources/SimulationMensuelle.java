package fr.poleemploi.estime.services.ressources;

import java.time.LocalDate;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonFormat;

public class SimulationMensuelle {
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate datePremierJourMoisSimule;
    private Map<String, AideSociale> mesAides;
    
    public LocalDate getDatePremierJourMoisSimule() {
        return datePremierJourMoisSimule;
    }
    public void setDatePremierJourMoisSimule(LocalDate datePremierJourMoisSimule) {
        this.datePremierJourMoisSimule = datePremierJourMoisSimule;
    }
    public Map<String, AideSociale> getMesAides() {
        return mesAides;
    }
    public void setMesAides(Map<String, AideSociale> mesAides) {
        this.mesAides = mesAides;
    }
    @Override
    public String toString() {
        return "SimulationMensuelle [datePremierJourMoisSimule=" + datePremierJourMoisSimule + ", mesAides=" + mesAides
                + "]";
    }
}
