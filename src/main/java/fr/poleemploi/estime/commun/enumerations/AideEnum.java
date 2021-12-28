package fr.poleemploi.estime.commun.enumerations;

public enum AideEnum {

    AGEPI("AGEPI", "Aide à la Garde d'Enfants pour Parent Isolé", "agepi.html"),
    AIDE_MOBILITE("AM", "Aide à la mobilité", "aide-mobilite.html"),
    AIDE_PERSONNALISEE_LOGEMENT("APL", "Aide Personnalisée au Logement", ""),
    ALLOCATION_ADULTES_HANDICAPES("AAH", "Allocation aux Adultes Handicapés", "aah.html"),
    ALLOCATION_LOGEMENT_FAMILIALE("ALF", "Allocation de Logement Familiale", ""),
    ALLOCATION_LOGEMENT_SOCIALE("ALS", "Allocation de Logement Sociale", ""),
    ALLOCATION_RETOUR_EMPLOI("ARE", "Allocation de Retour à l'Emploi", "are.html"),
    ALLOCATION_SOLIDARITE_SPECIFIQUE("ASS", "Allocation de Solidarité Spécifique", "ass.html"),
    ALLOCATIONS_FAMILIALES("AF", "Allocations Familiales", ""),
    ALLOCATION_SOUTIEN_FAMILIAL("ASF", "Allocation de Soutien Familial", ""),
    COMPLEMENT_FAMILIAL("CF", "Complément Familial", ""),
    PRIME_ACTIVITE("PA", "Prime d'Activité", "prime-activite.html"),
    PENSION_INVALIDITE("PI", "Pension d'Invalidité",""),
    PENSIONS_ALIMENTAIRES("PAF", "Pensions Alimentaires du Foyer",""),
    PRESTATION_ACCUEIL_JEUNE_ENFANT("PAJE", "Prestation d'Accueil du Jeune Enfant",""),
    RSA("RSA", "Revenu de Solidarité Active","");
    
    private String code;
    private String nom;
    private String nomFichierDetail;
    
    AideEnum(String code, String nom, String nomFichierDetail) {
        this.code = code;
        this.nom = nom;
        this.nomFichierDetail = nomFichierDetail;
    }

    public String getCode() {
        return code;
    }
    
    public String getNomFichierDetail() {
        return nomFichierDetail;
    }

    public String getNom() {
        return nom;
    }
}