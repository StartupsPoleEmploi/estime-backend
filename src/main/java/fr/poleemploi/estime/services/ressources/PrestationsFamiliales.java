package fr.poleemploi.estime.services.ressources;

/**
 * Ces prestations correspondent aux prestations pour les personnes ayant au moins un enfant.
 * @author ijla5100
 *
 */
public class PrestationsFamiliales {
    
    private float allocationsFamiliales;
    private float allocationSoutienFamilial;
    private float complementFamilial;
    private float prestationAccueilJeuneEnfant;
    private float pensionsAlimentairesFoyer;
    
    public float getPensionsAlimentairesFoyer() {
        return pensionsAlimentairesFoyer;
    }
    public void setPensionsAlimentairesFoyer(float pensionsAlimentairesFoyer) {
        this.pensionsAlimentairesFoyer = pensionsAlimentairesFoyer;
    }
    public float getAllocationsFamiliales() {
        return allocationsFamiliales;
    }
    public void setAllocationsFamiliales(float allocationsFamiliales) {
        this.allocationsFamiliales = allocationsFamiliales;
    }
    public float getAllocationSoutienFamilial() {
        return allocationSoutienFamilial;
    }
    public void setAllocationSoutienFamilial(float allocationSoutienFamilial) {
        this.allocationSoutienFamilial = allocationSoutienFamilial;
    }
    public float getComplementFamilial() {
        return complementFamilial;
    }
    public void setComplementFamilial(float complementFamilial) {
        this.complementFamilial = complementFamilial;
    }
    public float getPrestationAccueilJeuneEnfant() {
        return prestationAccueilJeuneEnfant;
    }
    public void setPrestationAccueilJeuneEnfant(float prestationAccueilJeuneEnfant) {
        this.prestationAccueilJeuneEnfant = prestationAccueilJeuneEnfant;
    }
    
    @Override
    public String toString() {
        return "PrestationsFamiliales [allocationsFamiliales=" + allocationsFamiliales + ", allocationSoutienFamilial="
                + allocationSoutienFamilial + ", complementFamilial=" + complementFamilial
                + ", prestationAccueilJeuneEnfant=" + prestationAccueilJeuneEnfant + ", pensionsAlimentairesFoyer="
                + pensionsAlimentairesFoyer + "]";
    }
}
