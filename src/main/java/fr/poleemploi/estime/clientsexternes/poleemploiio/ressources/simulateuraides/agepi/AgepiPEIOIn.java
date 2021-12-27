package fr.poleemploi.estime.clientsexternes.poleemploiio.ressources.simulateuraides.agepi;

public class AgepiPEIOIn {
    
    private String codeTerritoire;
    private String origine;
    private String dateDepot;
    private String dateActionReclassement;
    private String contexte;
    private String natureContratTravail;
    private String lieuFormationOuEmploi;
    private String typeIntensite;
    private int intensite;
    private int dureePeriodeEmploiOuFormation;
    private int nombreEnfants;
    private int nombreEnfantsMoins10Ans;
    private boolean eleveSeulEnfants;

    public String getCodeTerritoire() {
	return codeTerritoire;
    }

    public void setCodeTerritoire(String codeTerritoire) {
	this.codeTerritoire = codeTerritoire;
    }

    public String getOrigine() {
	return origine;
    }

    public void setOrigine(String origine) {
	this.origine = origine;
    }

    public String getDateDepot() {
	return dateDepot;
    }

    public void setDateDepot(String dateDepot) {
	this.dateDepot = dateDepot;
    }

    public String getDateActionReclassement() {
	return dateActionReclassement;
    }

    public void setDateActionReclassement(String dateActionReclassement) {
	this.dateActionReclassement = dateActionReclassement;
    }

    public String getContexte() {
	return contexte;
    }

    public void setContexte(String contexte) {
	this.contexte = contexte;
    }

    public String getNatureContratTravail() {
	return natureContratTravail;
    }

    public void setNatureContratTravail(String natureContratTravail) {
	this.natureContratTravail = natureContratTravail;
    }

    public String getLieuFormationOuEmploi() {
	return lieuFormationOuEmploi;
    }

    public void setLieuFormationOuEmploi(String lieuFormationOuEmploi) {
	this.lieuFormationOuEmploi = lieuFormationOuEmploi;
    }

    public String getTypeIntensite() {
	return typeIntensite;
    }

    public void setTypeIntensite(String typeIntensite) {
	this.typeIntensite = typeIntensite;
    }

    public int getIntensite() {
	return intensite;
    }

    public void setIntensite(int intensite) {
	this.intensite = intensite;
    }

    public int getDureePeriodeEmploiOuFormation() {
	return dureePeriodeEmploiOuFormation;
    }

    public void setDureePeriodeEmploiOuFormation(int dureePeriodeEmploiOuFormation) {
	this.dureePeriodeEmploiOuFormation = dureePeriodeEmploiOuFormation;
    }

    public int getNombreEnfants() {
	return nombreEnfants;
    }

    public void setNombreEnfants(int nombreEnfants) {
	this.nombreEnfants = nombreEnfants;
    }

    public int getNombreEnfantsMoins10Ans() {
	return nombreEnfantsMoins10Ans;
    }

    public void setNombreEnfantsMoins10Ans(int nombreEnfantsMoins10Ans) {
	this.nombreEnfantsMoins10Ans = nombreEnfantsMoins10Ans;
    }

    public boolean isEleveSeulEnfants() {
	return eleveSeulEnfants;
    }

    public void setEleveSeulEnfants(boolean eleveSeulEnfants) {
	this.eleveSeulEnfants = eleveSeulEnfants;
    }

    @Override
    public String toString() {
        return "AgepiPEIOIn [codeTerritoire=" + codeTerritoire + ", origine=" + origine + ", dateDepot=" + dateDepot
                + ", dateActionReclassement=" + dateActionReclassement + ", contexte=" + contexte
                + ", natureContratTravail=" + natureContratTravail + ", lieuFormationOuEmploi=" + lieuFormationOuEmploi
                + ", typeIntensite=" + typeIntensite + ", intensite=" + intensite + ", dureePeriodeEmploiOuFormation="
                + dureePeriodeEmploiOuFormation + ", nombreEnfants=" + nombreEnfants + ", nombreEnfantsMoins10Ans="
                + nombreEnfantsMoins10Ans + ", eleveSeulEnfants=" + eleveSeulEnfants + "]";
    }
}
