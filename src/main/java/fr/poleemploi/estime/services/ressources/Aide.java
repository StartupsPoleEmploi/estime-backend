package fr.poleemploi.estime.services.ressources;

public class Aide extends RessourceFinanciere {

    //true si l'aide n'a pas été créée mais reportée depuis un mois précédent 
    //ex : prime d'acivité, l'aide créée au mois N est reportée au mois N+1 et N+2 
    private boolean isReportee;
    private String organisme;

    public boolean isReportee() {
	return isReportee;
    }

    public void setReportee(boolean isReportee) {
	this.isReportee = isReportee;
    }

    public String getOrganisme() {
	return organisme;
    }

    public void setOrganisme(String organisme) {
	this.organisme = organisme;
    }

    @Override
    public String toString() {
	return "Aide [ montant=" + getMontant() + ", isReportee=" + isReportee + ", organisme=" + organisme + "]";
    }
}
