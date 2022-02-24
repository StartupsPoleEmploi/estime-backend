package fr.poleemploi.estime.services.ressources;

public class RessourceFinanciere {

    private String code;
    private String nom;
    private Float montant;

    public String getCode() {
	return code;
    }

    public void setCode(String code) {
	this.code = code;
    }

    public String getNom() {
	return nom;
    }

    public void setNom(String nom) {
	this.nom = nom;
    }

    public Float getMontant() {
	return montant;
    }

    public void setMontant(Float montant) {
	this.montant = montant;
    }

    @Override
    public String toString() {
	return "RessourceFinanciere [code=" + code + ", nom=" + nom + ", montant=" + montant + "]";
    }
}
