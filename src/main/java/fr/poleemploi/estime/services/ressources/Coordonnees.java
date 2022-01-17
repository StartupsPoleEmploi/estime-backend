package fr.poleemploi.estime.services.ressources;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Coordonnees {

    private String codeInsee;
    private String codePostal;
    @JsonProperty("isDeMayotte")
    private boolean isDeMayotte;
    @JsonProperty("isDesDOM")
    private boolean isDesDOM;

    public String getCodeInsee() {
	return codeInsee;
    }

    public void setCodeInsee(String codeInsee) {
	this.codeInsee = codeInsee;
    }

    public String getCodePostal() {
	return codePostal;
    }

    public void setCodePostal(String codePostal) {
	this.codePostal = codePostal;
    }

    public boolean isDeMayotte() {
	return isDeMayotte;
    }

    @JsonProperty("isDeMayotte")
    public void setDeMayotte(boolean isDeMayotte) {
	this.isDeMayotte = isDeMayotte;
    }

    public boolean isDesDOM() {
	return isDesDOM;
    }

    @JsonProperty("isDesDOM")
    public void setDesDOM(boolean isDesDOM) {
	this.isDesDOM = isDesDOM;
    }

    @Override
    public String toString() {
	return "Coordonnees [codeInsee=" + codeInsee + ", codePostal=" + codePostal + ", isDeMayotte=" + isDeMayotte + ", isDesDOM=" + isDesDOM + "]";
    }
}
