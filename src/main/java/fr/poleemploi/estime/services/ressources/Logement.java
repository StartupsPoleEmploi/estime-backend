package fr.poleemploi.estime.services.ressources;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Logement {

    private String codeInsee;
    private StatutOccupationLogement statutOccupationLogement;
    @JsonProperty("isDeMayotte")
    private boolean isDeMayotte;
    @JsonProperty("isCrous")
    private boolean isCrous;
    @JsonProperty("isColoc")
    private boolean isColloc;
    @JsonProperty("isConventionne")
    private boolean isConventionne;
    @JsonProperty("isChambre")
    private boolean isChambre;
    private float montantLoyer;
    private float montantCharges;

    public String getCodeInsee() {
        return codeInsee;
    }

    public void setCodeInsee(String codeInsee) {
        this.codeInsee = codeInsee;
    }

    public StatutOccupationLogement getStatutOccupationLogement() {
        return statutOccupationLogement;
    }

    public void setStatutOccupationLogement(StatutOccupationLogement statutOccupationLogement) {
        this.statutOccupationLogement = statutOccupationLogement;
    }

    public boolean isDeMayotte() {
        return isDeMayotte;
    }

    @JsonProperty("isDeMayotte")
    public void setDeMayotte(boolean isDeMayotte) {
        this.isDeMayotte = isDeMayotte;
    }

    public boolean isCrous() {
        return isCrous;
    }

    @JsonProperty("isCrous")
    public void setCrous(boolean isCrous) {
        this.isCrous = isCrous;
    }

    public boolean isColloc() {
        return isColloc;
    }

    @JsonProperty("isColloc")
    public void setColloc(boolean isColloc) {
        this.isColloc = isColloc;
    }

    public boolean isConventionne() {
        return isConventionne;
    }

    @JsonProperty("isConventionne")
    public void setConventionne(boolean isConventionne) {
        this.isConventionne = isConventionne;
    }

    public boolean isChambre() {
        return isChambre;
    }

    @JsonProperty("isChambre")
    public void setChambre(boolean isChambre) {
        this.isChambre = isChambre;
    }

    public float getMontantLoyer() {
        return montantLoyer;
    }

    public void setMontantLoyer(float montantLoyer) {
        this.montantLoyer = montantLoyer;
    }

    public float getMontantCharges() {
        return montantCharges;
    }

    public void setMontantCharges(float montantCharges) {
        this.montantCharges = montantCharges;
    }

    @Override
    public String toString() {
        return "Logement [codeInsee=" + codeInsee + ", statutOccupationLogement=" + statutOccupationLogement + ", isDeMayotte=" + isDeMayotte + ", isCrous=" + isCrous + ", isColoc=" + isColloc
                + ", isConventionne=" + isConventionne + ", isChambre=" + isChambre + ", montantLoyer=" + montantLoyer + ", montantCharges=" + montantCharges + "]";
    }

}
