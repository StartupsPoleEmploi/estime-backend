package fr.poleemploi.estime.services.ressources;

public class AllocationsLogement {

    private float moisN;
    private float moisNMoins1;
    private float moisNMoins2;
    private float moisNMoins3;

    public float getMoisN() {
        return moisN;
    }
    public void setMoisN(float moisN) {
        this.moisN = moisN;
    }
    public float getMoisNMoins1() {
        return moisNMoins1;
    }
    public void setMoisNMoins1(float moisNMoins1) {
        this.moisNMoins1 = moisNMoins1;
    }
    public float getMoisNMoins2() {
        return moisNMoins2;
    }
    public void setMoisNMoins2(float moisNMoins2) {
        this.moisNMoins2 = moisNMoins2;
    }
    public float getMoisNMoins3() {
        return moisNMoins3;
    }
    public void setMoisNMoins3(float moisNMoins3) {
        this.moisNMoins3 = moisNMoins3;
    }
    @Override
    public String toString() {
        return "allocationsLogement [moisN=" + moisN + ", moisNMoins1=" + moisNMoins1
                + ", moisNMoins2=" + moisNMoins2 + ", moisNMoins3=" + moisNMoins3 + "]";
    }
}
