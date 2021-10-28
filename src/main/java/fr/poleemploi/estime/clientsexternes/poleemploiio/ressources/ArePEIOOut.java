package fr.poleemploi.estime.clientsexternes.poleemploiio.ressources;

import java.math.BigDecimal;

public class ArePEIOOut {
	private BigDecimal allocationMensuelle;
	private BigDecimal soldePrevisionnelReliquat;
	private BigDecimal salaireRetenuActiviteReprise;
	private BigDecimal montantCRC;
	private BigDecimal montantCSG;
	private BigDecimal montantCRDS;
	
	public BigDecimal getAllocationMensuelle() {
		return allocationMensuelle;
	}
	public void setAllocationMensuelle(BigDecimal allocationMensuelle) {
		this.allocationMensuelle = allocationMensuelle;
	}
	public BigDecimal getSoldePrevisionnelReliquat() {
		return soldePrevisionnelReliquat;
	}
	public void setSoldePrevisionnelReliquat(BigDecimal soldePrevisionnelReliquat) {
		this.soldePrevisionnelReliquat = soldePrevisionnelReliquat;
	}
	public BigDecimal getSalaireRetenuActiviteReprise() {
		return salaireRetenuActiviteReprise;
	}
	public void setSalaireRetenuActiviteReprise(BigDecimal salaireRetenuActiviteReprise) {
		this.salaireRetenuActiviteReprise = salaireRetenuActiviteReprise;
	}
	public BigDecimal getMontantCRC() {
		return montantCRC;
	}
	public void setMontantCRC(BigDecimal montantCRC) {
		this.montantCRC = montantCRC;
	}
	public BigDecimal getMontantCSG() {
		return montantCSG;
	}
	public void setMontantCSG(BigDecimal montantCSG) {
		this.montantCSG = montantCSG;
	}
	public BigDecimal getMontantCRDS() {
		return montantCRDS;
	}
	public void setMontantCRDS(BigDecimal montantCRDS) {
		this.montantCRDS = montantCRDS;
	}
}
