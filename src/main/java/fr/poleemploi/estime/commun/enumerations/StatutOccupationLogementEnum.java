package fr.poleemploi.estime.commun.enumerations;

public enum StatutOccupationLogementEnum {
    LOCATAIRE_HLM("locataire_hlm"),
    LOCATAIRE_MEUBLE("locataire_meuble"),
    LOCATAIRE_NON_MEUBLE("locataire_vide"),
    LOGE_GRATUITEMENT("loge_gratuitement"),
    PROPRIETAIRE("proprietaire"),
    PROPRIETAIRE_AVEC_EMPRUNT("proprietaire"),
    NON_RENSEIGNE("non_renseigne");

    private String libelle;

    StatutOccupationLogementEnum(String libelle) {
	this.libelle = libelle;
    }

    public String getLibelle() {
	return libelle;
    }
}
