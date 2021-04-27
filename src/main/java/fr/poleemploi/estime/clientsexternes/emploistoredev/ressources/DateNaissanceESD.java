package fr.poleemploi.estime.clientsexternes.emploistoredev.ressources;

import java.util.Date;

public class DateNaissanceESD {

    private String codeCivilite;
    private String libelleCivilite;
    private String nomPatronymique;
    private String nomMarital;
    private String prenom;
    private Date dateDeNaissance;
    public String getCodeCivilite() {
        return codeCivilite;
    }
    public void setCodeCivilite(String codeCivilite) {
        this.codeCivilite = codeCivilite;
    }
    public String getLibelleCivilite() {
        return libelleCivilite;
    }
    public void setLibelleCivilite(String libelleCivilite) {
        this.libelleCivilite = libelleCivilite;
    }
    public String getNomPatronymique() {
        return nomPatronymique;
    }
    public void setNomPatronymique(String nomPatronymique) {
        this.nomPatronymique = nomPatronymique;
    }
    public String getNomMarital() {
        return nomMarital;
    }
    public void setNomMarital(String nomMarital) {
        this.nomMarital = nomMarital;
    }
    public String getPrenom() {
        return prenom;
    }
    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }
    public Date getDateDeNaissance() {
        return dateDeNaissance;
    }
    public void setDateDeNaissance(Date dateDeNaissance) {
        this.dateDeNaissance = dateDeNaissance;
    }
    @Override
    public String toString() {
        return "DateNaissanceESD [codeCivilite=" + codeCivilite + ", libelleCivilite=" + libelleCivilite
                + ", nomPatronymique=" + nomPatronymique + ", nomMarital=" + nomMarital + ", prenom=" + prenom
                + ", dateDeNaissance=" + dateDeNaissance + "]";
    }
}
