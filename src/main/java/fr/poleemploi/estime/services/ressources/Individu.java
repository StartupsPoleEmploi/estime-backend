package fr.poleemploi.estime.services.ressources;

public class Individu {

    private String idPoleEmploi;
    private BeneficiairePrestationsSociales beneficiairePrestationsSociales;
    private boolean isPopulationAutorisee;
    private PeConnectAuthorization peConnectAuthorization;
    private RessourcesFinancieres ressourcesFinancieres;
    
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
    public BeneficiairePrestationsSociales getBeneficiairePrestationsSociales() {
        return beneficiairePrestationsSociales;
    }
    public void setBeneficiairePrestationsSociales(BeneficiairePrestationsSociales beneficiairePrestationsSociales) {
        this.beneficiairePrestationsSociales = beneficiairePrestationsSociales;
    }
    public RessourcesFinancieres getRessourcesFinancieres() {
        return ressourcesFinancieres;
    }
    public void setRessourcesFinancieres(RessourcesFinancieres ressourcesFinancieres) {
        this.ressourcesFinancieres = ressourcesFinancieres;
    }
    @Override
    public String toString() {
        return "Individu [idPoleEmploi=" + idPoleEmploi + ", beneficiairePrestationsSociales=" + beneficiairePrestationsSociales
                + ", isPopulationAutorisee=" + isPopulationAutorisee + ", peConnectAuthorization="
                + peConnectAuthorization + ", ressourcesFinancieres=" + ressourcesFinancieres + "]";
    } 
}
