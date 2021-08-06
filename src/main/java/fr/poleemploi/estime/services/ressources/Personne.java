package fr.poleemploi.estime.services.ressources;

public class Personne {

    private BeneficiairePrestationsSociales beneficiairePrestationsSociales;
    private InformationsPersonnelles informationsPersonnelles;
    private RessourcesFinancieres ressourcesFinancieres;

    public BeneficiairePrestationsSociales getBeneficiairePrestationsSociales() {
        return beneficiairePrestationsSociales;
    }
    public void setBeneficiairePrestationsSociales(BeneficiairePrestationsSociales beneficiairePrestationsSociales) {
        this.beneficiairePrestationsSociales = beneficiairePrestationsSociales;
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
    public void setRessourcesFinancieres(RessourcesFinancieres ressourcesFinancieres) {
        this.ressourcesFinancieres = ressourcesFinancieres;
    }
    @Override
    public String toString() {
        return "Personne [beneficiairePrestationsSociales=" + beneficiairePrestationsSociales + ", informationsPersonnelles="
                + informationsPersonnelles + ", ressourcesFinancieres=" + ressourcesFinancieres + "]";
    }
}
