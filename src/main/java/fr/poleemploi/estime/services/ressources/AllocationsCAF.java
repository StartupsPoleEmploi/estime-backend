package fr.poleemploi.estime.services.ressources;

public class AllocationsCAF {
    
    private Float allocationMensuelleNetAAH;
    private Float allocationMensuelleNetRSA;
    private float allocationsFamilialesMensuellesNetFoyer;
    private AllocationsLogementMensuellesNetFoyer allocationsLogementMensuellesNetFoyer;
    private float pensionsAlimentairesFoyer;
    private Integer prochaineDeclarationRSA;
    
    public float getAllocationsFamilialesMensuellesNetFoyer() {
        return allocationsFamilialesMensuellesNetFoyer;
    }
    public void setAllocationsFamilialesMensuellesNetFoyer(float allocationsFamilialesMensuellesNetFoyer) {
        this.allocationsFamilialesMensuellesNetFoyer = allocationsFamilialesMensuellesNetFoyer;
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
    public float getPensionsAlimentairesFoyer() {
        return pensionsAlimentairesFoyer;
    }
    public void setPensionsAlimentairesFoyer(float pensionsAlimentairesFoyer) {
        this.pensionsAlimentairesFoyer = pensionsAlimentairesFoyer;
    }
    @Override
    public String toString() {
        return "AllocationsCAF [allocationMensuelleNetAAH=" + allocationMensuelleNetAAH + ", allocationMensuelleNetRSA="
                + allocationMensuelleNetRSA + ", prochaineDeclarationRSA=" + prochaineDeclarationRSA
                + ", allocationsFamilialesMensuellesNetFoyer=" + allocationsFamilialesMensuellesNetFoyer
                + ", allocationsLogementMensuellesNetFoyer=" + allocationsLogementMensuellesNetFoyer
                + ", pensionsAlimentairesFoyer=" + pensionsAlimentairesFoyer + "]";
    }
}
