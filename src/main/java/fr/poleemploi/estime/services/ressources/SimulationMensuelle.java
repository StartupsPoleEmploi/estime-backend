package fr.poleemploi.estime.services.ressources;

import java.time.LocalDate;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonFormat;

public class SimulationMensuelle {
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate datePremierJourMoisSimule;
    private Map<String, PrestationSociale> mesPrestationsSociales;
    
    public LocalDate getDatePremierJourMoisSimule() {
        return datePremierJourMoisSimule;
    }
    public void setDatePremierJourMoisSimule(LocalDate datePremierJourMoisSimule) {
        this.datePremierJourMoisSimule = datePremierJourMoisSimule;
    }
    public Map<String, PrestationSociale> getMesPrestationsSociales() {
        return mesPrestationsSociales;
    }
    public void setMesPrestationsSociales(Map<String, PrestationSociale> mesPrestationsSociales) {
        this.mesPrestationsSociales = mesPrestationsSociales;
    }
    
    @Override
    public String toString() {
        return "SimulationMensuelle [datePremierJourMoisSimule=" + datePremierJourMoisSimule
                + ", mesPrestationsSociales=" + mesPrestationsSociales + "]";
    }
}
