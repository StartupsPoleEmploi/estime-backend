package fr.poleemploi.estime.services.ressources;

import com.fasterxml.jackson.annotation.JsonProperty;

public class StatutOccupationLogement {

    private boolean isLocataireHLM;
    private boolean isLocataireMeuble;
    private boolean isLocataireNonMeuble;
    private boolean isLogeGratuitement;
    private boolean isProprietaire;
    private boolean isProprietaireAvecEmprunt;

    public boolean isLocataireHLM() {
	return isLocataireHLM;
    }

    @JsonProperty("isLocataireHLM")
    public void setLocataireHLM(boolean isLocataireHLM) {
	this.isLocataireHLM = isLocataireHLM;
    }

    public boolean isLocataireMeuble() {
	return isLocataireMeuble;
    }

    @JsonProperty("isLocataireMeuble")
    public void setLocataireMeuble(boolean isLocataireMeuble) {
	this.isLocataireMeuble = isLocataireMeuble;
    }

    public boolean isLocataireNonMeuble() {
	return isLocataireNonMeuble;
    }

    @JsonProperty("isLocataireNonMeuble")
    public void setLocataireNonMeuble(boolean isLocataireNonMeuble) {
	this.isLocataireNonMeuble = isLocataireNonMeuble;
    }

    public boolean isLogeGratuitement() {
	return isLogeGratuitement;
    }

    @JsonProperty("isLogeGratuitement")
    public void setLogeGratuitement(boolean isLogeGratuitement) {
	this.isLogeGratuitement = isLogeGratuitement;
    }

    public boolean isProprietaire() {
	return isProprietaire;
    }

    @JsonProperty("isProprietaire")
    public void setProprietaire(boolean isProprietaire) {
	this.isProprietaire = isProprietaire;
    }

    public boolean isProprietaireAvecEmprunt() {
	return isProprietaireAvecEmprunt;
    }

    @JsonProperty("isProprietaireAvecEmprunt")
    public void setProprietaireAvecEmprunt(boolean isProprietaireAvecEmprunt) {
	this.isProprietaireAvecEmprunt = isProprietaireAvecEmprunt;
    }

    @Override
    public String toString() {
	return "StatutOccupationLogement [isLocataireHLM=" + isLocataireHLM + ", isLocataireMeuble=" + isLocataireMeuble + ", isLocataireNonMeuble=" + isLocataireNonMeuble + ", isLogeGratuitement="
		+ isLogeGratuitement + ", isProprietaire=" + isProprietaire + ", isProprietaireAvecEmprunt=" + isProprietaireAvecEmprunt + "]";
    }

}
