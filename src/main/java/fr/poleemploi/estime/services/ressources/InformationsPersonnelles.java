package fr.poleemploi.estime.services.ressources;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

public class InformationsPersonnelles {

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate dateNaissance;
    private String email;
    @JsonProperty("hasPensionRetraite")
    private boolean hasPensionRetraite;
    @JsonProperty("hasRevenusImmobilier")
    private boolean hasRevenusImmobilier;
    private boolean travailleurIndependant;
    private boolean microEntrepreneur;
    private boolean isSalarie;
    private boolean isSansRessource;
    @JsonProperty("isBeneficiaireACRE")
    private Boolean isBeneficiaireACRE;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate dateRepriseCreationEntreprise;
    private String nationalite;
    private String nom;
    private String prenom;
    private Boolean titreSejourEnFranceValide;
    private Logement logement;

    public boolean hasPensionRetraite() {
	return hasPensionRetraite;
    }

    @JsonProperty("hasPensionRetraite")
    public void setHasPensionRetraite(boolean hasPensionRetraite) {
	this.hasPensionRetraite = hasPensionRetraite;
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

    public boolean hasRevenusImmobilier() {
	return hasRevenusImmobilier;
    }

    @JsonProperty("hasRevenusImmobilier")
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

    @JsonProperty("isBeneficiaireACRE")
    public Boolean isBeneficiaireACRE() {
	return isBeneficiaireACRE;
    }

    public void setBeneficiaireACRE(Boolean isBeneficiaireACRE) {
	this.isBeneficiaireACRE = isBeneficiaireACRE;
    }

    public LocalDate getDateRepriseCreationEntreprise() {
	return dateRepriseCreationEntreprise;
    }

    public void setDateRepriseCreationEntreprise(LocalDate dateRepriseCreationEntreprise) {
	this.dateRepriseCreationEntreprise = dateRepriseCreationEntreprise;
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

    public Logement getLogement() {
	return logement;
    }

    public void setLogement(Logement logement) {
	this.logement = logement;
    }

    @Override
    public String toString() {
	return "InformationsPersonnelles [dateNaissance=" + dateNaissance + ", email=" + email + ", hasPensionRetraite=" + hasPensionRetraite + ", hasRevenusImmobilier="
		+ hasRevenusImmobilier + ", travailleurIndependant=" + travailleurIndependant + ", microEntrepreneur=" + microEntrepreneur + ", isSalarie=" + isSalarie
		+ ", isSansRessource=" + isSansRessource + ", isBeneficiaireACRE=" + isBeneficiaireACRE + ", dateRepriseCreationEntreprise=" + dateRepriseCreationEntreprise
		+ ", nationalite=" + nationalite + ", nom=" + nom + ", prenom=" + prenom + ", titreSejourEnFranceValide=" + titreSejourEnFranceValide + ", logement=" + logement
		+ "]";
    }
}
