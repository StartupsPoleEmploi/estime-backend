package fr.poleemploi.estime.services.ressources;

public class Personne {

    private BeneficiaireAidesSociales beneficiaireAidesSociales;
    private InformationsPersonnelles informationsPersonnelles;
    private RessourcesFinancieres ressourcesFinancieres;
    
   
    public BeneficiaireAidesSociales getBeneficiaireAidesSociales() {
        return beneficiaireAidesSociales;
    }
    public void setBeneficiaireAidesSociales(BeneficiaireAidesSociales beneficiaireAidesSociales) {
        this.beneficiaireAidesSociales = beneficiaireAidesSociales;
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
        return "Personne [beneficiaireAidesSociales=" + beneficiaireAidesSociales + ", informationsPersonnelles="
                + informationsPersonnelles + ", ressourcesFinancieres=" + ressourcesFinancieres + "]";
    }
}
