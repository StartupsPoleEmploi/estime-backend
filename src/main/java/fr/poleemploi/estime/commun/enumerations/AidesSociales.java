package fr.poleemploi.estime.commun.enumerations;

public enum AidesSociales {

    AGEPI("AGEPI", "Aide à la Garde d'Enfants pour Parent Isolé", "agepi.html"),
    AIDE_MOBILITE("AM", "Aide à la mobilité", "aide-mobilite.html"),
    ALLOCATION_ADULTES_HANDICAPES("AAH", "Allocation aux Adultes Handicapés", "aah.html"),
    ALLOCATION_SOLIDARITE_SPECIFIQUE("ASS", "Allocation de Solidarité Spécifique", "ass.html"),
    PRIME_ACTIVITE("PA", "Prime d'Activité", "prime-activite.html"),
    PENSION_INVALIDITE("PI", "Pension d'invalidité",""),
    RSA("RSA", "Revenu de Solidarité Active","");
    
    private String code;
    private String nom;
    private String nomFichierDetail;
    
    AidesSociales(String code, String nom, String nomFichierDetail) {
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
