package fr.poleemploi.estime.services.ressources;

public class AidesLogement {

    private AllocationsLogement aidePersonnaliseeLogement;
    private AllocationsLogement allocationLogementFamiliale;
    private AllocationsLogement allocationLogementSociale;
    
    public AllocationsLogement getAidePersonnaliseeLogement() {
        return aidePersonnaliseeLogement;
    }
    public void setAidePersonnaliseeLogement(AllocationsLogement aidePersonnaliseeLogement) {
        this.aidePersonnaliseeLogement = aidePersonnaliseeLogement;
    }
    public AllocationsLogement getAllocationLogementFamiliale() {
        return allocationLogementFamiliale;
    }
    public void setAllocationLogementFamiliale(AllocationsLogement allocationLogementFamiliale) {
        this.allocationLogementFamiliale = allocationLogementFamiliale;
    }
    public AllocationsLogement getAllocationLogementSociale() {
        return allocationLogementSociale;
    }
    public void setAllocationLogementSociale(AllocationsLogement allocationLogementSociale) {
        this.allocationLogementSociale = allocationLogementSociale;
    }
    
    @Override
    public String toString() {
        return "AidesLogement [aidePersonnaliseeLogement=" + aidePersonnaliseeLogement + ", allocationLogementFamiliale=" + allocationLogementFamiliale + ", allocationLogementSociale="
                + allocationLogementSociale + "]";
    }
}
