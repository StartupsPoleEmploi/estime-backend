package fr.poleemploi.estime.clientsexternes.openfisca.ressources;

import static fr.poleemploi.estime.clientsexternes.openfisca.mappeur.ParametresOpenFisca.FAMILLES;
import static fr.poleemploi.estime.clientsexternes.openfisca.mappeur.ParametresOpenFisca.INDIVIDUS;
import static fr.poleemploi.estime.clientsexternes.openfisca.mappeur.ParametresOpenFisca.MENAGES;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

public class OpenFiscaRoot {

    @JsonProperty(INDIVIDUS)
    private Map<String, OpenFiscaIndividu> individus;
    @JsonProperty(FAMILLES)
    private Map<String, OpenFiscaFamille> familles;
    @JsonProperty(MENAGES)
    private Map<String, OpenFiscaMenage> menages;

    @JsonProperty(INDIVIDUS)
    public Map<String, OpenFiscaIndividu> getIndividus() {
	return individus;
    }

    public void setIndividus(Map<String, OpenFiscaIndividu> individus) {
	this.individus = individus;
    }

    @JsonProperty(FAMILLES)
    public Map<String, OpenFiscaFamille> getFamilles() {
	return familles;
    }

    public void setFamilles(Map<String, OpenFiscaFamille> familles) {
	this.familles = familles;
    }

    @JsonProperty(MENAGES)
    public Map<String, OpenFiscaMenage> getMenages() {
	return menages;
    }

    public void setMenages(Map<String, OpenFiscaMenage> menages) {
	this.menages = menages;
    }

    @Override
    public String toString() {
	return OpenFiscaObjectMapperService.getJsonStringFromObject(this);
    }

}
