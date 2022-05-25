package fr.poleemploi.estime.clientsexternes.openfisca.ressources;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

public class OpenFiscaRoot {

    @JsonProperty("individus")
    private Map<String, OpenFiscaIndividu> individus;
    @JsonProperty("familles")
    private Map<String, OpenFiscaFamille> familles;
    @JsonProperty("menages")
    private Map<String, OpenFiscaMenage> menages;

    @JsonProperty("individus")
    public Map<String, OpenFiscaIndividu> getIndividus() {
	return individus;
    }

    public void setIndividus(Map<String, OpenFiscaIndividu> individus) {
	this.individus = individus;
    }

    @JsonProperty("familles")
    public Map<String, OpenFiscaFamille> getFamilles() {
	return familles;
    }

    public void setFamilles(Map<String, OpenFiscaFamille> familles) {
	this.familles = familles;
    }

    @JsonProperty("menages")
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
