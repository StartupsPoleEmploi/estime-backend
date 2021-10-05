package fr.poleemploi.estime.services.ressources;

public class AidesCAF {
    
    private Float allocationAAH;
    private Float allocationRSA; 
    private AidesLogement aidesLogement;   
    private AidesFamiliales aidesFamiliales;
    private Integer prochaineDeclarationTrimestrielle;
    
    
    public AidesFamiliales getAidesFamiliales() {
        return aidesFamiliales;
    }
    public void setAidesFamiliales(AidesFamiliales aidesFamiliales) {
        this.aidesFamiliales = aidesFamiliales;
    }
    public Integer getProchaineDeclarationTrimestrielle() {
        return prochaineDeclarationTrimestrielle;
    }
    public void setProchaineDeclarationTrimestrielle(Integer prochaineDeclarationTrimestrielle) {
        this.prochaineDeclarationTrimestrielle = prochaineDeclarationTrimestrielle;
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
    public AidesLogement getAidesLogement() {
        return aidesLogement;
    }
    public void setAidesLogement(AidesLogement aidesLogement) {
        this.aidesLogement = aidesLogement;
    }
    
    @Override
    public String toString() {
        return "AidesCAF [allocationAAH=" + allocationAAH + ", allocationRSA=" + allocationRSA + ", aidesLogement=" + aidesLogement + ", aidesFamiliales=" + aidesFamiliales
                + ", prochaineDeclarationTrimestrielle=" + prochaineDeclarationTrimestrielle + "]";
    }
}
