package fr.poleemploi.estime.services.ressources;


public class Aide {

    private String code;
    private String detail;
    //true si l'aide n'a pas été créée mais reportée depuis un mois précédent 
    //ex : prime d'acivité, l'aide créée au mois N est reportée au mois N+1 et N+2 
    private boolean isReportee;
    private String messageAlerte;
    private Float montant;
    private String nom;
    private String organisme;
   
    
    public String getMessageAlerte() {
        return messageAlerte;
    }
    public void setMessageAlerte(String messageAlerte) {
        this.messageAlerte = messageAlerte;
    }
    public boolean isReportee() {
        return isReportee;
    }
    public void setReportee(boolean isReportee) {
        this.isReportee = isReportee;
    }
    public String getDetail() {
        return detail;
    }
    public void setDetail(String detail) {
        this.detail = detail;
    }
    public String getOrganisme() {
        return organisme;
    }
    public void setOrganisme(String organisme) {
        this.organisme = organisme;
    }
    public String getCode() {
        return code;
    }
    public void setCode(String code) {
        this.code = code;
    }
    public String getNom() {
        return nom;
    }
    public void setNom(String nom) {
        this.nom = nom;
    }
    public Float getMontant() {
        return montant;
    }
    public void setMontant(Float montant) {
        this.montant = montant;
    }
    @Override
    public String toString() {
        return "Aidee [code=" + code + ", detail=" + detail + ", montant=" + montant + ", nom=" + nom
                + ", organisme=" + organisme + ", isReportee=" + isReportee + "]";
    }
}
