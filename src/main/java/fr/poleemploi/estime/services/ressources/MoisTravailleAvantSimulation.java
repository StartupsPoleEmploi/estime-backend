package fr.poleemploi.estime.services.ressources;

import java.time.LocalDate;

public class MoisTravailleAvantSimulation {

    private Salaire salaire;
    private boolean isSansSalaire;
    private LocalDate date;

    public Salaire getSalaire() {
	return salaire;
    }

    public void setSalaire(Salaire salaire) {
	this.salaire = salaire;
    }

    public boolean isSansSalaire() {
	return isSansSalaire;
    }

    public void setSansSalaire(boolean isSansSalaire) {
	this.isSansSalaire = isSansSalaire;
    }

    public LocalDate getDate() {
	return date;
    }

    public void setDate(LocalDate date) {
	this.date = date;
    }

    @Override
    public String toString() {
	return "MoisTravailleAvantSimulation [salaire=" + salaire + ", isSansSalaire=" + isSansSalaire + ", date=" + date + "]";
    }
}
