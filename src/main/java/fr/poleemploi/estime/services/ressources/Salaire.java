package fr.poleemploi.estime.services.ressources;

public class Salaire {
    
    private float montantBrut;
    private float montantNet;
    
    public float getMontantBrut() {
        return montantBrut;
    }
    public void setMontantBrut(float montantBrut) {
        this.montantBrut = montantBrut;
    }
    public float getMontantNet() {
        return montantNet;
    }
    public void setMontantNet(float montantNet) {
        this.montantNet = montantNet;
    }
    
    @Override
    public String toString() {
        return "Salaire [montantBrut=" + montantBrut + ", montantNet=" + montantNet + "]";
    }
}
