package fr.poleemploi.estime.services.ressources;

public class Individu {

    private String idPoleEmploi;
    private BeneficiaireAidesSociales beneficiaireAidesSociales;
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
    public BeneficiaireAidesSociales getBeneficiaireAidesSociales() {
        return beneficiaireAidesSociales;
    }
    public void setBeneficiaireAidesSociales(BeneficiaireAidesSociales beneficiaireAidesSociales) {
        this.beneficiaireAidesSociales = beneficiaireAidesSociales;
    }
    public RessourcesFinancieres getRessourcesFinancieres() {
        return ressourcesFinancieres;
    }
    public void setRessourcesFinancieres(RessourcesFinancieres ressourcesFinancieres) {
        this.ressourcesFinancieres = ressourcesFinancieres;
    }
    @Override
    public String toString() {
        return "Individu [idPoleEmploi=" + idPoleEmploi + ", beneficiaireAidesSociales=" + beneficiaireAidesSociales
                + ", isPopulationAutorisee=" + isPopulationAutorisee + ", peConnectAuthorization="
                + peConnectAuthorization + ", ressourcesFinancieres=" + ressourcesFinancieres + "]";
    } 
}
