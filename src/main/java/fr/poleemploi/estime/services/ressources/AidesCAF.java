package fr.poleemploi.estime.services.ressources;

public class AidesCAF {
    
    private Float allocationAAH;
    private Float allocationRSA; 
    private AllocationsLogement allocationsLogement;   
    private AidesFamiliales aidesFamiliales;
    private Integer prochaineDeclarationRSA;
    
    
    public AidesFamiliales getAidesFamiliales() {
        return aidesFamiliales;
    }
    public void setAidesFamiliales(AidesFamiliales aidesFamiliales) {
        this.aidesFamiliales = aidesFamiliales;
    }
    public Integer getProchaineDeclarationRSA() {
        return prochaineDeclarationRSA;
    }
    public void setProchaineDeclarationRSA(Integer prochaineDeclarationRSA) {
        this.prochaineDeclarationRSA = prochaineDeclarationRSA;
    }
    public Float getAllocationAAH() {
        return allocationAAH;
    }
    public void setAllocationAAH(Float allocationAAH) {
        this.allocationAAH = allocationAAH;
    }
    public Float getAllocationRSA() {
        return allocationRSA;
    }
    public void setAllocationRSA(Float allocationRSA) {
        this.allocationRSA = allocationRSA;
    }
    public AllocationsLogement getAllocationsLogement() {
        return allocationsLogement;
    }
    public void setAllocationsLogement(AllocationsLogement allocationsLogement) {
        this.allocationsLogement = allocationsLogement;
    }
    
    @Override
    public String toString() {
        return "AidesCAF [allocationAAH=" + allocationAAH + ", allocationRSA=" + allocationRSA
                + ", allocationsLogement=" + allocationsLogement + ", aidesFamiliales=" + aidesFamiliales
                + ", prochaineDeclarationRSA=" + prochaineDeclarationRSA + "]";
    }
}
