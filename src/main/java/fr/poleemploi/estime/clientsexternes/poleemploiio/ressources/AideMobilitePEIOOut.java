package fr.poleemploi.estime.clientsexternes.poleemploiio.ressources;

public class AideMobilitePEIOOut {
    private String origine;
    private String dateDepot;
    private String contexte;
    private String dateActionReclassement;
    private String lieuFormationOuEmploi;
    private ConditionsAideMobiliteAPI conditionsAideMobiliteAPI;
    private int distanceDomicileActionReclassement;
    private int dureePeriodeEmploiOuFormation;
    private String natureContratTravail;
    private DecisionAideMobiliteAPI decisionAideMobiliteAPI;
    private boolean fraisPrisEnChargeParTiers;
    private int totalMontantsAidesMobilitePrecedentes;
    private int montantMaximalAttribuable;
    private int nombreAllersRetours;
    private int nombreRepas;
    private int nombreNuitees;
    private String codeTerritoire;
    private String categorie;
    private int tauxNetDroit;
    private String dateDernierJourIndemnise;
    private boolean allocationEnCours;
    private int droitAREMinimal;
    private String libelleDroit;
    private String lieuResidenceDE;

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

    public String getContexte() {
	return contexte;
    }

    public void setContexte(String contexte) {
	this.contexte = contexte;
    }

    public String getDateActionReclassement() {
	return dateActionReclassement;
    }

    public void setDateActionReclassement(String dateActionReclassement) {
	this.dateActionReclassement = dateActionReclassement;
    }

    public int getDistanceDomicileActionReclassement() {
	return distanceDomicileActionReclassement;
    }

    public void setDistanceDomicileActionReclassement(int distanceDomicileActionReclassement) {
	this.distanceDomicileActionReclassement = distanceDomicileActionReclassement;
    }

    public int getDureePeriodeEmploiOuFormation() {
	return dureePeriodeEmploiOuFormation;
    }

    public void setDureePeriodeEmploiOuFormation(int dureePeriodeEmploiOuFormation) {
	this.dureePeriodeEmploiOuFormation = dureePeriodeEmploiOuFormation;
    }

    public String getNatureContratTravail() {
	return natureContratTravail;
    }

    public void setNatureContratTravail(String natureContratTravail) {
	this.natureContratTravail = natureContratTravail;
    }

    public boolean isFraisPrisEnChargeParTiers() {
	return fraisPrisEnChargeParTiers;
    }

    public void setFraisPrisEnChargeParTiers(boolean fraisPrisEnChargeParTiers) {
	this.fraisPrisEnChargeParTiers = fraisPrisEnChargeParTiers;
    }

    public int getNombreAllersRetours() {
	return nombreAllersRetours;
    }

    public void setNombreAllersRetours(int nombreAllersRetours) {
	this.nombreAllersRetours = nombreAllersRetours;
    }

    public int getNombreRepas() {
	return nombreRepas;
    }

    public void setNombreRepas(int nombreRepas) {
	this.nombreRepas = nombreRepas;
    }

    public int getNombreNuitees() {
	return nombreNuitees;
    }

    public void setNombreNuitees(int nombreNuitees) {
	this.nombreNuitees = nombreNuitees;
    }

    public String getCodeTerritoire() {
	return codeTerritoire;
    }

    public void setCodeTerritoire(String codeTerritoire) {
	this.codeTerritoire = codeTerritoire;
    }

    public String getCategorie() {
	return categorie;
    }

    public void setCategorie(String categorie) {
	this.categorie = categorie;
    }

    public int getTauxNetDroit() {
	return tauxNetDroit;
    }

    public void setTauxNetDroit(int tauxNetDroit) {
	this.tauxNetDroit = tauxNetDroit;
    }

    public boolean isAllocationEnCours() {
	return allocationEnCours;
    }

    public void setAllocationEnCours(boolean allocationEnCours) {
	this.allocationEnCours = allocationEnCours;
    }

    public int getDroitAREMinimal() {
	return droitAREMinimal;
    }

    public void setDroitAREMinimal(int droitAREMinimal) {
	this.droitAREMinimal = droitAREMinimal;
    }

    public String getLieuResidenceDE() {
	return lieuResidenceDE;
    }

    public void setLieuResidenceDE(String lieuResidenceDE) {
	this.lieuResidenceDE = lieuResidenceDE;
    }

    public String getDateDernierJourIndemnise() {
	return dateDernierJourIndemnise;
    }

    public void setDateDernierJourIndemnise(String dateDernierJourIndemnise) {
	this.dateDernierJourIndemnise = dateDernierJourIndemnise;
    }

    public String getLibelleDroit() {
	return libelleDroit;
    }

    public void setLibelleDroit(String libelleDroit) {
	this.libelleDroit = libelleDroit;
    }

    public String getLieuFormationOuEmploi() {
	return lieuFormationOuEmploi;
    }

    public void setLieuFormationOuEmploi(String lieuFormationOuEmploi) {
	this.lieuFormationOuEmploi = lieuFormationOuEmploi;
    }

    public int getTotalMontantsAidesMobilitePrecedentes() {
	return totalMontantsAidesMobilitePrecedentes;
    }

    public void setTotalMontantsAidesMobilitePrecedentes(int totalMontantsAidesMobilitePrecedentes) {
	this.totalMontantsAidesMobilitePrecedentes = totalMontantsAidesMobilitePrecedentes;
    }

    public int getMontantMaximalAttribuable() {
	return montantMaximalAttribuable;
    }

    public void setMontantMaximalAttribuable(int montantMaximalAttribuable) {
	this.montantMaximalAttribuable = montantMaximalAttribuable;
    }

    public ConditionsAideMobiliteAPI getConditionsAideMobiliteAPI() {
	return conditionsAideMobiliteAPI;
    }

    public void setConditionsAideMobiliteAPI(ConditionsAideMobiliteAPI conditionsAideMobiliteAPI) {
	this.conditionsAideMobiliteAPI = conditionsAideMobiliteAPI;
    }

    public DecisionAideMobiliteAPI getDecisionAideMobiliteAPI() {
	return decisionAideMobiliteAPI;
    }

    public void setDecisionAideMobiliteAPI(DecisionAideMobiliteAPI decisionAideMobiliteAPI) {
	this.decisionAideMobiliteAPI = decisionAideMobiliteAPI;
    }

    @Override
    public String toString() {
	return "AideMobilitePEIOOut [origine=" + origine + ", dateDepot=" + dateDepot + ", contexte=" + contexte + ", dateActionReclassement=" + dateActionReclassement
		+ ", lieuFormationOuEmploi=" + lieuFormationOuEmploi + ", conditionsAideMobiliteAPI=" + conditionsAideMobiliteAPI + ", distanceDomicileActionReclassement="
		+ distanceDomicileActionReclassement + ", dureePeriodeEmploiOuFormation=" + dureePeriodeEmploiOuFormation + ", natureContratTravail=" + natureContratTravail
		+ ", decisionAideMobiliteAPI=" + decisionAideMobiliteAPI + ", fraisPrisEnChargeParTiers=" + fraisPrisEnChargeParTiers + ", totalMontantsAidesMobilitePrecedentes="
		+ totalMontantsAidesMobilitePrecedentes + ", montantMaximalAttribuable=" + montantMaximalAttribuable + ", nombreAllersRetours=" + nombreAllersRetours
		+ ", nombreRepas=" + nombreRepas + ", nombreNuitees=" + nombreNuitees + ", codeTerritoire=" + codeTerritoire + ", categorie=" + categorie + ", tauxNetDroit="
		+ tauxNetDroit + ", dateDernierJourIndemnise=" + dateDernierJourIndemnise + ", allocationEnCours=" + allocationEnCours + ", droitAREMinimal=" + droitAREMinimal
		+ ", libelleDroit=" + libelleDroit + ", lieuResidenceDE=" + lieuResidenceDE + "]";
    }

}
