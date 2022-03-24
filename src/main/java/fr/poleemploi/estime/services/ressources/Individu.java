package fr.poleemploi.estime.services.ressources;

public class Individu {

    private String idPoleEmploi;
    private BeneficiaireAides beneficiaireAides;
    private boolean isPopulationAutorisee;
    private PeConnectAuthorization peConnectAuthorization;
    private RessourcesFinancieresAvantSimulation ressourcesFinancieres;
    private InformationsPersonnelles informationsPersonnelles;

    public String getIdPoleEmploi() {
	return idPoleEmploi;
    }

    public void setIdPoleEmploi(String idPoleEmploi) {
	this.idPoleEmploi = idPoleEmploi;
    }

    public PeConnectAuthorization getPeConnectAuthorization() {
	return peConnectAuthorization;
    }

    public void setPeConnectAuthorization(PeConnectAuthorization peConnectAuthorization) {
	this.peConnectAuthorization = peConnectAuthorization;
    }

    public boolean isPopulationAutorisee() {
	return isPopulationAutorisee;
    }

    public void setPopulationAutorisee(boolean isPopulationAutorisee) {
	this.isPopulationAutorisee = isPopulationAutorisee;
    }

    public BeneficiaireAides getBeneficiaireAides() {
	return beneficiaireAides;
    }

    public void setBeneficiaireAides(BeneficiaireAides beneficiaireAides) {
	this.beneficiaireAides = beneficiaireAides;
    }

    public RessourcesFinancieresAvantSimulation getRessourcesFinancieres() {
	return ressourcesFinancieres;
    }

    public void setRessourcesFinancieresAvantSimulation(RessourcesFinancieresAvantSimulation ressourcesFinancieres) {
	this.ressourcesFinancieres = ressourcesFinancieres;
    }

    public InformationsPersonnelles getInformationsPersonnelles() {
	return informationsPersonnelles;
    }

    public void setInformationsPersonnelles(InformationsPersonnelles informationsPersonnelles) {
	this.informationsPersonnelles = informationsPersonnelles;
    }

    public void setRessourcesFinancieres(RessourcesFinancieresAvantSimulation ressourcesFinancieres) {
	this.ressourcesFinancieres = ressourcesFinancieres;
    }

    @Override
    public String toString() {
	return "Individu [idPoleEmploi=" + idPoleEmploi + ", beneficiaireAides=" + beneficiaireAides + ", isPopulationAutorisee=" + isPopulationAutorisee
		+ ", peConnectAuthorization=" + peConnectAuthorization + ", ressourcesFinancieres=" + ressourcesFinancieres + ", informationsPersonnelles="
		+ informationsPersonnelles + "]";
    }
}
