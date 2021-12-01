package fr.poleemploi.estime.clientsexternes.poleemploiio.ressources;

public class ArePEIOOut {
    private float allocationMensuelle;
    private Float soldePrevisionnelReliquat;
    private float salaireRetenuActiviteReprise;
    private float montantCRC;
    private float montantCSG;
    private float montantCRDS;

    public float getAllocationMensuelle() {
	return allocationMensuelle;
    }

    public void setAllocationMensuelle(float allocationMensuelle) {
	this.allocationMensuelle = allocationMensuelle;
    }

    public Float getSoldePrevisionnelReliquat() {
	return soldePrevisionnelReliquat;
    }

    public void setSoldePrevisionnelReliquat(Float soldePrevisionnelReliquat) {
	this.soldePrevisionnelReliquat = soldePrevisionnelReliquat;
    }

    public float getSalaireRetenuActiviteReprise() {
	return salaireRetenuActiviteReprise;
    }

    public void setSalaireRetenuActiviteReprise(float salaireRetenuActiviteReprise) {
	this.salaireRetenuActiviteReprise = salaireRetenuActiviteReprise;
    }

    public float getMontantCRC() {
	return montantCRC;
    }

    public void setMontantCRC(float montantCRC) {
	this.montantCRC = montantCRC;
    }

    public float getMontantCSG() {
	return montantCSG;
    }

    public void setMontantCSG(float montantCSG) {
	this.montantCSG = montantCSG;
    }

    public float getMontantCRDS() {
	return montantCRDS;
    }

    public void setMontantCRDS(float montantCRDS) {
	this.montantCRDS = montantCRDS;
    }

    @Override
    public String toString() {
	return "ArePEIOOut [allocationMensuelle=" + allocationMensuelle + ", soldePrevisionnelReliquat=" + soldePrevisionnelReliquat + ", salaireRetenuActiviteReprise="
		+ salaireRetenuActiviteReprise + ", montantCRC=" + montantCRC + ", montantCSG=" + montantCSG + ", montantCRDS=" + montantCRDS + "]";
    }

}
