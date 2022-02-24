package fr.poleemploi.estime.services.ressources;

public class Aide extends RessourceFinanciere {

    private String detail;
    //true si l'aide n'a pas été créée mais reportée depuis un mois précédent 
    //ex : prime d'acivité, l'aide créée au mois N est reportée au mois N+1 et N+2 
    private boolean isReportee;
    private String messageAlerte;
    private String organisme;
    private String lienExterne;

    public String getDetail() {
	return detail;
    }

    public void setDetail(String detail) {
	this.detail = detail;
    }

    public boolean isReportee() {
	return isReportee;
    }

    public void setReportee(boolean isReportee) {
	this.isReportee = isReportee;
    }

    public String getMessageAlerte() {
	return messageAlerte;
    }

    public void setMessageAlerte(String messageAlerte) {
	this.messageAlerte = messageAlerte;
    }

    public String getOrganisme() {
	return organisme;
    }

    public void setOrganisme(String organisme) {
	this.organisme = organisme;
    }

    public String getLienExterne() {
	return lienExterne;
    }

    public void setLienExterne(String lienExterne) {
	this.lienExterne = lienExterne;
    }

    @Override
    public String toString() {
	return "Aide [detail=" + detail + ", isReportee=" + isReportee + ", messageAlerte=" + messageAlerte + ", organisme=" + organisme + ", lienExterne=" + lienExterne + "]";
    }

}
