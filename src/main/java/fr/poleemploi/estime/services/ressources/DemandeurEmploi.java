package fr.poleemploi.estime.services.ressources;

public class DemandeurEmploi {

    private String idPoleEmploi;
    private BeneficiaireAides beneficiaireAides;
    private FuturTravail futurTravail;
    private InformationsPersonnelles informationsPersonnelles;
    private RessourcesFinancieres ressourcesFinancieres;
    private SituationFamiliale situationFamiliale;
    private PeConnectAuthorization peConnectAuthorization;

    public String getIdPoleEmploi() {
        return idPoleEmploi;
    }
    public void setIdPoleEmploi(String idPoleEmploi) {
        this.idPoleEmploi = idPoleEmploi;
    }
    public void setRessourcesFinancieres(RessourcesFinancieres ressourcesFinancieres) {
        this.ressourcesFinancieres = ressourcesFinancieres;
    }
    public BeneficiaireAides getBeneficiaireAides() {
        return beneficiaireAides;
    }
    public void setBeneficiaireAides(BeneficiaireAides beneficiaireAides) {
        this.beneficiaireAides = beneficiaireAides;
    }
    public SituationFamiliale getSituationFamiliale() {
        return situationFamiliale;
    }
    public void setSituationFamiliale(SituationFamiliale situationFamiliale) {
        this.situationFamiliale = situationFamiliale;
    }
    public FuturTravail getFuturTravail() {
        return futurTravail;
    }
    public void setFuturTravail(FuturTravail futurTravail) {
        this.futurTravail = futurTravail;
    }
    public InformationsPersonnelles getInformationsPersonnelles() {
        return informationsPersonnelles;
    }
    public void setInformationsPersonnelles(InformationsPersonnelles informationsPersonnelles) {
        this.informationsPersonnelles = informationsPersonnelles;
    }
    public RessourcesFinancieres getRessourcesFinancieres() {
        return ressourcesFinancieres;
    }
    @Override
    public String toString() {
        return "DemandeurEmploi [idPoleEmploi=" + idPoleEmploi + ", beneficiaireAides="
                + beneficiaireAides + ", futurTravail=" + futurTravail + ", informationsPersonnelles="
                + informationsPersonnelles + ", ressourcesFinancieres=" + ressourcesFinancieres
                + ", situationFamiliale=" + situationFamiliale + "]";
    }
	public PeConnectAuthorization getPeConnectAuthorization() {
		return peConnectAuthorization;
	}
	public void setPeConnectAuthorization(PeConnectAuthorization peConnectAuthorization) {
		this.peConnectAuthorization = peConnectAuthorization;
	}
}
