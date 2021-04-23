package fr.poleemploi.estime.commun.enumerations;

public enum MontantsParPalierAgepi {

    NBR_ENFANT_PALIER_MIN_NBR_HEURE_PALIER_INTERMEDIAIRE(170),
    NBR_ENFANT_PALIER_MIN_NBR_HEURE_PALIER_MAX(400),
    NBR_ENFANT_PALIER_INTERMEDIAIRE_NBR_HEURE_PALIER_INTERMEDIAIRE(195),
    NBR_ENFANT_PALIER_INTERMEDIAIRE_NBR_HEURE_PALIER_MAX(460),
    NBR_ENFANT_PALIER_MAX_NBR_HEURE_PALIER_INTERMEDIAIRE(220),
    NBR_ENFANT_PALIER_MAX_NBR_HEURE_PALIER_MAX(520);

    private int montantAgepi;

    MontantsParPalierAgepi(int montantAgepi) {
        this.montantAgepi = montantAgepi;
    }

    public int getMontant() {
        return montantAgepi;
    }

}
