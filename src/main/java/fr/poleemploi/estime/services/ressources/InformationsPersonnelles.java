package fr.poleemploi.estime.services.ressources;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

public class InformationsPersonnelles {

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate dateNaissance;
    private String email;
    private String nationalite;
    private String nom;
    private String prenom;
    private Logement logement;
    private MicroEntreprise microEntreprise;
    @JsonProperty("hasTitreSejourEnFranceValide")
    private boolean hasTitreSejourEnFranceValide;
    @JsonProperty("hasPensionRetraite")
    private boolean hasPensionRetraite;
    @JsonProperty("hasRevenusImmobilier")
    private boolean hasRevenusImmobilier;
    @JsonProperty("isTravailleurIndependant")
    private boolean isTravailleurIndependant;
    @JsonProperty("isMicroEntrepreneur")
    private boolean isMicroEntrepreneur;
    @JsonProperty("isSalarie")
    private boolean isSalarie;
    @JsonProperty("isSansRessource")
    private boolean isSansRessource;
    @JsonProperty("isBeneficiaireACRE")
    private boolean isBeneficiaireACRE;

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

    public Logement getLogement() {
	return logement;
    }

    public void setLogement(Logement logement) {
	this.logement = logement;
    }

    public MicroEntreprise getMicroEntreprise() {
	return microEntreprise;
    }

    public void setMicroEntreprise(MicroEntreprise microEntreprise) {
	this.microEntreprise = microEntreprise;
    }

    @JsonProperty("hasTitreSejourEnFranceValide")
    public boolean hasTitreSejourEnFranceValide() {
	return hasTitreSejourEnFranceValide;
    }

    public void setTitreSejourEnFranceValide(boolean hasTitreSejourEnFranceValide) {
	this.hasTitreSejourEnFranceValide = hasTitreSejourEnFranceValide;
    }

    @JsonProperty("hasPensionRetraite")
    public boolean hasPensionRetraite() {
	return hasPensionRetraite;
    }

    public void setHasPensionRetraite(boolean hasPensionRetraite) {
	this.hasPensionRetraite = hasPensionRetraite;
    }

    @JsonProperty("hasRevenusImmobilier")
    public boolean hasRevenusImmobilier() {
	return hasRevenusImmobilier;
    }

    public void setHasRevenusImmobilier(boolean hasRevenusImmobilier) {
	this.hasRevenusImmobilier = hasRevenusImmobilier;
    }

    @JsonProperty("isTravailleurIndependant")
    public boolean isTravailleurIndependant() {
	return isTravailleurIndependant;
    }

    public void setTravailleurIndependant(boolean isTravailleurIndependant) {
	this.isTravailleurIndependant = isTravailleurIndependant;
    }

    @JsonProperty("isMicroEntrepreneur")
    public boolean isMicroEntrepreneur() {
	return isMicroEntrepreneur;
    }

    public void setMicroEntrepreneur(boolean isMicroEntrepreneur) {
	this.isMicroEntrepreneur = isMicroEntrepreneur;
    }

    @JsonProperty("isSalarie")
    public boolean isSalarie() {
	return isSalarie;
    }

    public void setSalarie(boolean isSalarie) {
	this.isSalarie = isSalarie;
    }

    @JsonProperty("isSansRessource")
    public boolean isSansRessource() {
	return isSansRessource;
    }

    public void setSansRessource(boolean isSansRessource) {
	this.isSansRessource = isSansRessource;
    }

    @JsonProperty("isBeneficiaireACRE")
    public boolean isBeneficiaireACRE() {
	return isBeneficiaireACRE;
    }

    public void setBeneficiaireACRE(boolean isBeneficiaireACRE) {
	this.isBeneficiaireACRE = isBeneficiaireACRE;
    }

    @Override
    public String toString() {
	return "InformationsPersonnelles [dateNaissance=" + dateNaissance + ", email=" + email + ", nationalite=" + nationalite + ", nom=" + nom + ", prenom=" + prenom
		+ ", logement=" + logement + ", microEntreprise=" + microEntreprise + ", hasTitreSejourEnFranceValide=" + hasTitreSejourEnFranceValide + ", hasPensionRetraite="
		+ hasPensionRetraite + ", hasRevenusImmobilier=" + hasRevenusImmobilier + ", isTravailleurIndependant=" + isTravailleurIndependant + ", isMicroEntrepreneur="
		+ isMicroEntrepreneur + ", isSalarie=" + isSalarie + ", isSansRessource=" + isSansRessource + ", isBeneficiaireACRE=" + isBeneficiaireACRE + "]";
    }

}
