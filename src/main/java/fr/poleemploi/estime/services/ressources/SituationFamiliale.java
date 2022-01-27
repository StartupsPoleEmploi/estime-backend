package fr.poleemploi.estime.services.ressources;

import java.util.List;

public class SituationFamiliale {

    private Personne conjoint;
    private Boolean isEnCouple;
    private Boolean isSeulPlusDe18Mois;
    private List<Personne> personnesACharge;

    public Boolean getIsSeulPlusDe18Mois() {
	return isSeulPlusDe18Mois;
    }
    public void setIsSeulPlusDe18Mois(Boolean isSeulPlusDe18Mois) {
	this.isSeulPlusDe18Mois = isSeulPlusDe18Mois;
    }
    public Boolean getIsEnCouple() {
	return isEnCouple;
    }
    public void setIsEnCouple(Boolean isEnCouple) {
	this.isEnCouple = isEnCouple;
    }
    public List<Personne> getPersonnesACharge() {
	return personnesACharge;
    }
    public void setPersonnesACharge(List<Personne> personnesACharge) {
	this.personnesACharge = personnesACharge;
    }
    public Personne getConjoint() {
	return conjoint;
    }
    public void setConjoint(Personne conjoint) {
	this.conjoint = conjoint;
    }
    @Override
    public String toString() {
	return "SituationFamiliale [conjoint=" + conjoint + ", isEnCouple=" + isEnCouple + ", isSeulPlusDe18Mois="
		+ isSeulPlusDe18Mois + ", personnesACharge=" + personnesACharge + "]";
    }
}
