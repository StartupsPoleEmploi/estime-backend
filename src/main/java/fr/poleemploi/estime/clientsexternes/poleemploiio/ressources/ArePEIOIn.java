package fr.poleemploi.estime.clientsexternes.poleemploiio.ressources;

import java.math.BigDecimal;

public class ArePEIOIn {
	private BigDecimal salaireBrutJournalier;
	private BigDecimal allocationBruteJournaliere;
	private BigDecimal gainBrut;
	
	public BigDecimal getSalaireBrutJournalier() {
		return salaireBrutJournalier;
	}
	public void setSalaireBrutJournalier(BigDecimal salaireBrutJournalier) {
		this.salaireBrutJournalier = salaireBrutJournalier;
	}
	public BigDecimal getAllocationBruteJournaliere() {
		return allocationBruteJournaliere;
	}
	public void setAllocationBruteJournaliere(BigDecimal allocationBruteJournaliere) {
		this.allocationBruteJournaliere = allocationBruteJournaliere;
	}
	public BigDecimal getGainBrut() {
		return gainBrut;
	}
	public void setGainBrut(BigDecimal gainBrut) {
		this.gainBrut = gainBrut;
	}
}
