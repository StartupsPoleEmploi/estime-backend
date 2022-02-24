package fr.poleemploi.estime.services.ressources;

public class Personne {

    private BeneficiaireAides beneficiaireAides;
    private InformationsPersonnelles informationsPersonnelles;
    private RessourcesFinancieresAvantSimulation ressourcesFinancieres;

    public BeneficiaireAides getBeneficiaireAides() {
	return beneficiaireAides;
    }
    public void setBeneficiaireAides(BeneficiaireAides beneficiaireAides) {
	this.beneficiaireAides = beneficiaireAides;
    }
    public InformationsPersonnelles getInformationsPersonnelles() {
	return informationsPersonnelles;
    }
    public void setInformationsPersonnelles(InformationsPersonnelles informationsPersonnelles) {
	this.informationsPersonnelles = informationsPersonnelles;
    }
    public RessourcesFinancieresAvantSimulation getRessourcesFinancieres() {
	return ressourcesFinancieres;
    }
    public void setRessourcesFinancieresAvantSimulation(RessourcesFinancieresAvantSimulation ressourcesFinancieres) {
	this.ressourcesFinancieres = ressourcesFinancieres;
    }
    @Override
    public String toString() {
	return "Personne [beneficiaireAides=" + beneficiaireAides + ", informationsPersonnelles="
		+ informationsPersonnelles + ", ressourcesFinancieres=" + ressourcesFinancieres + "]";
    }
}
