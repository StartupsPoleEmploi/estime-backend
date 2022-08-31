package fr.poleemploi.estime.services.ressources;

public class Salaire {

    private float montantMensuelBrut;
    private float montantMensuelNet;

    public float getMontantMensuelBrut() {
	return montantMensuelBrut;
    }

    public void setMontantMensuelBrut(float montantMensuelBrut) {
	this.montantMensuelBrut = montantMensuelBrut;
    }

    public float getMontantMensuelNet() {
	return montantMensuelNet;
    }

    public void setMontantMensuelNet(float montantMensuelNet) {
	this.montantMensuelNet = montantMensuelNet;
    }

    @Override
    public String toString() {
	return "Salaire [montantMensuelBrut=" + montantMensuelBrut + ", montantMensuelNet=" + montantMensuelNet + "]";
    }
}
