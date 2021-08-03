package fr.poleemploi.estime.services.ressources;

public class PrestationsCAF {
    
    private Float allocationMensuelleNetAAH;
    private Float allocationMensuelleNetRSA; 
    private AllocationsLogementMensuellesNetFoyer allocationsLogementMensuellesNetFoyer;   
    private PrestationsFamiliales prestationsFamiliales;
    private Integer prochaineDeclarationRSA;
    
    public PrestationsFamiliales getPrestationsFamiliales() {
        return prestationsFamiliales;
    }
    public void setPrestationsFamiliales(PrestationsFamiliales prestationsFamiliales) {
        this.prestationsFamiliales = prestationsFamiliales;
    }
    public AllocationsLogementMensuellesNetFoyer getAllocationsLogementMensuellesNetFoyer() {
        return allocationsLogementMensuellesNetFoyer;
    }
    public void setAllocationsLogementMensuellesNetFoyer(AllocationsLogementMensuellesNetFoyer allocationsLogementMensuellesNetFoyer) {
        this.allocationsLogementMensuellesNetFoyer = allocationsLogementMensuellesNetFoyer;
    }
    public Float getAllocationMensuelleNetAAH() {
        return allocationMensuelleNetAAH;
    }
    public void setAllocationMensuelleNetAAH(Float allocationMensuelleNetAAH) {
        this.allocationMensuelleNetAAH = allocationMensuelleNetAAH;
    }
    public Float getAllocationMensuelleNetRSA() {
        return allocationMensuelleNetRSA;
    }
    public void setAllocationMensuelleNetRSA(Float allocationMensuelleNetRSA) {
        this.allocationMensuelleNetRSA = allocationMensuelleNetRSA;
    }
    public Integer getProchaineDeclarationRSA() {
        return prochaineDeclarationRSA;
    }
    public void setProchaineDeclarationRSA(Integer prochaineDeclarationRSA) {
        this.prochaineDeclarationRSA = prochaineDeclarationRSA;
    }
    
    @Override
    public String toString() {
        return "PrestationsCAF [allocationMensuelleNetAAH=" + allocationMensuelleNetAAH + ", allocationMensuelleNetRSA="
                + allocationMensuelleNetRSA + ", allocationsLogementMensuellesNetFoyer="
                + allocationsLogementMensuellesNetFoyer + ", prestationsFamiliales=" + prestationsFamiliales
                + ", prochaineDeclarationRSA=" + prochaineDeclarationRSA + "]";
    }
}
