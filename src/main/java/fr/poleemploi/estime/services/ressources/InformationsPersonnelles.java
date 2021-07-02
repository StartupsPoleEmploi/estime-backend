package fr.poleemploi.estime.services.ressources;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;

public class InformationsPersonnelles {
    
    private String codePostal;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate dateNaissance;
    private String email;
    private boolean hasRevenusImmobilier;
    private boolean travailleurIndependant;
    private boolean microEntrepreneur;
    private boolean isSalarie;
    private boolean isSansRessource;
    private String nationalite;
    private String nom;
    private String prenom;
    private Boolean titreSejourEnFranceValide;
    private Boolean isProprietaireSansPretOuLogeGratuit;
    
    
    public String getCodePostal() {
        return codePostal;
    }
    public void setCodePostal(String codePostal) {
        this.codePostal = codePostal;
    }
    public LocalDate getDateNaissance() {
        return dateNaissance;
    }
    public void setDateNaissance(LocalDate dateNaissance) {
        this.dateNaissance = dateNaissance;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public boolean isHasRevenusImmobilier() {
        return hasRevenusImmobilier;
    }
    public void setHasRevenusImmobilier(boolean hasRevenusImmobilier) {
        this.hasRevenusImmobilier = hasRevenusImmobilier;
    }
    public boolean isTravailleurIndependant() {
        return travailleurIndependant;
    }
    public void setTravailleurIndependant(boolean travailleurIndependant) {
        this.travailleurIndependant = travailleurIndependant;
    }
    public boolean isMicroEntrepreneur() {
        return microEntrepreneur;
    }
    public void setMicroEntrepreneur(boolean microEntrepreneur) {
        this.microEntrepreneur = microEntrepreneur;
    }
    public boolean isSalarie() {
        return isSalarie;
    }
    public void setSalarie(boolean isSalarie) {
        this.isSalarie = isSalarie;
    }
    public boolean isSansRessource() {
        return isSansRessource;
    }
    public void setSansRessource(boolean isSansRessource) {
        this.isSansRessource = isSansRessource;
    }
    public String getNationalite() {
        return nationalite;
    }
    public void setNationalite(String nationalite) {
        this.nationalite = nationalite;
    }
    public String getNom() {
        return nom;
    }
    public void setNom(String nom) {
        this.nom = nom;
    }
    public String getPrenom() {
        return prenom;
    }
    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }
    public Boolean getTitreSejourEnFranceValide() {
        return titreSejourEnFranceValide;
    }
    public void setTitreSejourEnFranceValide(Boolean titreSejourEnFranceValide) {
        this.titreSejourEnFranceValide = titreSejourEnFranceValide;
    }    
    public Boolean getIsProprietaireSansPretOuLogeGratuit() {
        return isProprietaireSansPretOuLogeGratuit;
    }
    public void setIsProprietaireSansPretOuLogeGratuit(Boolean isProprietaireSansPretOuLogeGratuit) {
        this.isProprietaireSansPretOuLogeGratuit = isProprietaireSansPretOuLogeGratuit;
    }
    @Override
    public String toString() {
        return "InformationsPersonnelles [codePostal=" + codePostal + ", dateNaissance=" + dateNaissance + ", email="
                + email + ", hasRevenusImmobilier=" + hasRevenusImmobilier + ", travailleurIndependant="
                + travailleurIndependant + ", microEntrepreneur=" + microEntrepreneur + ", isSalarie=" + isSalarie
                + ", isSansRessource=" + isSansRessource + ", nationalite=" + nationalite + ", nom=" + nom + ", prenom="
                + prenom + ", titreSejourEnFranceValide=" + titreSejourEnFranceValide
                + ", isProprietaireSansPretOuLogeGratuit=" + isProprietaireSansPretOuLogeGratuit + "]";
    }
}
