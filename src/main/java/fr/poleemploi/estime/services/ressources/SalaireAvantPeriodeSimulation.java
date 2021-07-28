package fr.poleemploi.estime.services.ressources;

public class SalaireAvantPeriodeSimulation {

    private Salaire salaire;
    private boolean isSansSalaire;

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

    @Override
    public String toString() {
        return "SalaireAvantPeriodeSimulation [salaire=" + salaire + ", isSansSalaire=" + isSansSalaire + "]";
    }
}
