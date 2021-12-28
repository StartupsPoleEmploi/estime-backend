package fr.poleemploi.estime.services.ressources;

public class AllocationARE {

    private Float allocationJournaliereNet;
    private Float montantJournalierBrut;
    private Float allocationMensuelleNet;
    private Float salaireJournalierReferenceBrut;
    private Float nombreJoursRestants;
    private boolean isConcerneDegressivite;

    
    public Float getAllocationJournaliereNet() {
        return allocationJournaliereNet;
    }

    public void setAllocationJournaliereNet(Float allocationJournaliereNet) {
        this.allocationJournaliereNet = allocationJournaliereNet;
    }

    public Float getAllocationMensuelleNet() {
        return allocationMensuelleNet;
    }

    public void setAllocationMensuelleNet(Float allocationMensuelleNet) {
        this.allocationMensuelleNet = allocationMensuelleNet;
    }

    public Float getMontantJournalierBrut() {
        return montantJournalierBrut;
    }

    public void setMontantJournalierBrut(Float montantJournalierBrut) {
        this.montantJournalierBrut = montantJournalierBrut;
    }

    public Float getSalaireJournalierReferenceBrut() {
        return salaireJournalierReferenceBrut;
    }

    public void setSalaireJournalierReferenceBrut(Float salaireJournalierReferenceBrut) {
        this.salaireJournalierReferenceBrut = salaireJournalierReferenceBrut;
    }

    public Float getNombreJoursRestants() {
        return nombreJoursRestants;
    }

    public void setNombreJoursRestants(Float nombreJoursRestants) {
        this.nombreJoursRestants = nombreJoursRestants;
    }

    public boolean isConcerneDegressivite() {
        return isConcerneDegressivite;
    }

    public void setConcerneDegressivite(boolean isConcerneDegressivite) {
        this.isConcerneDegressivite = isConcerneDegressivite;
    }

    @Override
    public String toString() {
        return "AllocationARE [allocationJournaliereNet=" + allocationJournaliereNet + ", montantJournalierBrut="
                + montantJournalierBrut + ", allocationMensuelleNet=" + allocationMensuelleNet
                + ", salaireJournalierReferenceBrut=" + salaireJournalierReferenceBrut + ", nombreJoursRestants="
                + nombreJoursRestants + ", isConcerneDegressivite=" + isConcerneDegressivite + "]";
    }

	public Float getMontantJournalierBrut() {
		return montantJournalierBrut;
	}

	public void setMontantJournalierBrut(Float montantJournalierBrut) {
		this.montantJournalierBrut = montantJournalierBrut;
	}

	public Float getSalaireJournalierReferenceBrut() {
		return salaireJournalierReferenceBrut;
	}

	public void setSalaireJournalierReferenceBrut(Float salaireJournalierReferenceBrut) {
		this.salaireJournalierReferenceBrut = salaireJournalierReferenceBrut;
	}

	public Float getNombreJoursRestants() {
		return nombreJoursRestants;
	}

	public void setNombreJoursRestants(Float nombreJoursRestants) {
		this.nombreJoursRestants = nombreJoursRestants;
	}

	public boolean isConcerneDegressivite() {
		return isConcerneDegressivite;
	}

	public void setConcerneDegressivite(boolean isConcerneDegressivite) {
		this.isConcerneDegressivite = isConcerneDegressivite;
	}
}
