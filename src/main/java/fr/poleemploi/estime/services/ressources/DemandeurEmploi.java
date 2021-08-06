package fr.poleemploi.estime.services.ressources;

public class DemandeurEmploi {

    private String idPoleEmploi;
    private BeneficiairePrestationsSociales beneficiairePrestationsSociales;
    private FuturTravail futurTravail;
    private InformationsPersonnelles informationsPersonnelles;
    private RessourcesFinancieres ressourcesFinancieres;
    private SituationFamiliale situationFamiliale;

    public String getIdPoleEmploi() {
        return idPoleEmploi;
    }
    public void setIdPoleEmploi(String idPoleEmploi) {
        this.idPoleEmploi = idPoleEmploi;
    }
    public void setRessourcesFinancieres(RessourcesFinancieres ressourcesFinancieres) {
        this.ressourcesFinancieres = ressourcesFinancieres;
    }
    public BeneficiairePrestationsSociales getBeneficiairePrestationsSociales() {
        return beneficiairePrestationsSociales;
    }
    public void setBeneficiairePrestationsSociales(BeneficiairePrestationsSociales beneficiairePrestationsSociales) {
        this.beneficiairePrestationsSociales = beneficiairePrestationsSociales;
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
        return "DemandeurEmploi [idPoleEmploi=" + idPoleEmploi + ", beneficiairePrestationsSociales="
                + beneficiairePrestationsSociales + ", futurTravail=" + futurTravail + ", informationsPersonnelles="
                + informationsPersonnelles + ", ressourcesFinancieres=" + ressourcesFinancieres
                + ", situationFamiliale=" + situationFamiliale + "]";
    }
}
