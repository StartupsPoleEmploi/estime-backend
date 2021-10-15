package fr.poleemploi.estime.clientsexternes.poleemploiio.ressources;

public class AgepiPEIOOut {
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
	private String codeTerritoire;
	private String categorie;
	private int tauxNetDroit;
	private boolean allocationEnCours;
	private int droitAREMinimal;
	private String dateFaitGenerateurDerniereDemandeAGEPI;
	private ConditionInscription conditionInscription;
	private ConditionGardeEnfant conditionGardeEnfant;
	private ConditionChampTerritorial conditionChampTerritorial;
	private ConditionDuree conditionDuree;
	private ConditionRessource conditionRessource;
	private ConditionDateDepot conditionDateDepot;
	private ConditionDateFaitGenerateur condtionDateFaitGenerateur;
	private String nature;
	private float montant;
	private String libelleMotifRejet;
	
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
		eleveSeulEnfants = eleveSeulEnfants;
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
	public String getDateFaitGenerateurDerniereDemandeAGEPI() {
		return dateFaitGenerateurDerniereDemandeAGEPI;
	}
	public void setDateFaitGenerateurDerniereDemandeAGEPI(String dateFaitGenerateurDerniereDemandeAGEPI) {
		this.dateFaitGenerateurDerniereDemandeAGEPI = dateFaitGenerateurDerniereDemandeAGEPI;
	}
	public ConditionInscription getConditionInscription() {
		return conditionInscription;
	}
	public void setConditionInscription(ConditionInscription conditionInscription) {
		this.conditionInscription = conditionInscription;
	}
	
	public boolean isConditionInscriptionRemplie() {
		return this.conditionInscription.isRemplie();
	}
	
	public String getConditionInscriptionJustification() {
		return this.conditionInscription.getJustification();
	}
	
	public ConditionGardeEnfant getConditionGardeEnfant() {
		return conditionGardeEnfant;
	}
	public void setConditionGardeEnfant(ConditionGardeEnfant conditionGardeEnfant) {
		this.conditionGardeEnfant = conditionGardeEnfant;
	}
	
	public boolean isConditionGardeEnfantRemplie() {
		return this.conditionGardeEnfant.isRemplie();
	}
	
	public String getConditionGardeEnfantJustification() {
		return this.conditionGardeEnfant.getJustification();
	}
	
	public ConditionChampTerritorial getConditionChampTerritorial() {
		return conditionChampTerritorial;
	}
	public void setConditionChampTerritorial(ConditionChampTerritorial conditionChampTerritorial) {
		this.conditionChampTerritorial = conditionChampTerritorial;
	}
	public boolean isConditionChampTerritorialRemplie() {
		return this.conditionChampTerritorial.isRemplie();
	}
	
	public String getConditionChampTerritorialJustification() {
		return this.conditionChampTerritorial.getJustification();
	}
	public ConditionDuree getConditionDuree() {
		return conditionDuree;
	}
	public void setConditionDuree(ConditionDuree conditionDuree) {
		this.conditionDuree = conditionDuree;
	}
	public boolean isConditionDureeRemplie() {
		return this.conditionDuree.isRemplie();
	}
	
	public String getConditionDureeJustification() {
		return this.conditionDuree.getJustification();
	}
	
	public ConditionRessource getConditionRessource() {
		return conditionRessource;
	}
	public void setConditionRessource(ConditionRessource conditionRessource) {
		this.conditionRessource = conditionRessource;
	}
	
	public boolean isConditionRessourceRemplie() {
		return this.conditionRessource.isRemplie();
	}
	
	public String getConditionRessourceJustification() {
		return this.conditionRessource.getJustification();
	}
	
	public ConditionDateDepot getConditionDateDepot() {
		return conditionDateDepot;
	}
	public void setConditionDateDepot(ConditionDateDepot conditionDateDepot) {
		this.conditionDateDepot = conditionDateDepot;
	}
	public boolean isConditionDateDepotRemplie() {
		return this.conditionDateDepot.isRemplie();
	}
	
	public String getConditionDateDepotJustification() {
		return this.conditionDateDepot.getJustification();
	}
	
	public ConditionDateFaitGenerateur getCondtionDateFaitGenerateur() {
		return condtionDateFaitGenerateur;
	}
	public void setCondtionDateFaitGenerateur(ConditionDateFaitGenerateur condtionDateFaitGenerateur) {
		this.condtionDateFaitGenerateur = condtionDateFaitGenerateur;
	}
	public boolean isCondtionDateFaitGenerateurRemplie() {
		return this.condtionDateFaitGenerateur.isRemplie();
	}
	
	public String getCondtionDateFaitGenerateurJustification() {
		return this.condtionDateFaitGenerateur.getJustification();
	}
	public String getNature() {
		return nature;
	}
	public void setNature(String nature) {
		this.nature = nature;
	}
	public float getMontant() {
		return montant;
	}
	public void setMontant(float montant) {
		this.montant = montant;
	}
	public String getLibelleMotifRejet() {
		return libelleMotifRejet;
	}
	public void setLibelleMotifRejet(String libelleMotifRejet) {
		this.libelleMotifRejet = libelleMotifRejet;
	}
}
