package fr.poleemploi.estime.clientsexternes.openfisca;

public class OpenFiscaRetourSimulation {

    private float montantRSA;
    private float montantPrimeActivite;
    
    public float getMontantRSA() {
        return montantRSA;
    }
    public void setMontantRSA(float montantRSA) {
        this.montantRSA = montantRSA;
    }
    public float getMontantPrimeActivite() {
        return montantPrimeActivite;
    }
    public void setMontantPrimeActivite(float montantPrimeActivite) {
        this.montantPrimeActivite = montantPrimeActivite;
    }
    
    @Override
    public String toString() {
        return "OpenFiscaRetourSimulation [montantRSA=" + montantRSA + ", montantPrimeActivite=" + montantPrimeActivite
                + "]";
    }
}
