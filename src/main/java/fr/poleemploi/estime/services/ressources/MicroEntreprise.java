package fr.poleemploi.estime.services.ressources;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;

public class MicroEntreprise {

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate dateRepriseCreationEntreprise;
    private String typeBenefices;
    private Float chiffreAffairesN;
    private Float chiffreAffairesNMoins1;
    private Float chiffreAffairesNMoins2;
    private Float deficitNMoins2;

    public LocalDate getDateRepriseCreationEntreprise() {
	return dateRepriseCreationEntreprise;
    }

    public void setDateRepriseCreationEntreprise(LocalDate dateRepriseCreationEntreprise) {
	this.dateRepriseCreationEntreprise = dateRepriseCreationEntreprise;
    }

    public String getTypeBenefices() {
	return typeBenefices;
    }

    public void setTypeBenefices(String typeBenefices) {
	this.typeBenefices = typeBenefices;
    }

    public Float getChiffreAffairesN() {
	return chiffreAffairesN;
    }

    public void setChiffreAffairesN(Float chiffreAffairesN) {
	this.chiffreAffairesN = chiffreAffairesN;
    }

    public Float getChiffreAffairesNMoins1() {
	return chiffreAffairesNMoins1;
    }

    public void setChiffreAffairesNMoins1(Float chiffreAffairesNMoins1) {
	this.chiffreAffairesNMoins1 = chiffreAffairesNMoins1;
    }

    public Float getChiffreAffairesNMoins2() {
	return chiffreAffairesNMoins2;
    }

    public void setChiffreAffairesNMoins2(Float chiffreAffairesNMoins2) {
	this.chiffreAffairesNMoins2 = chiffreAffairesNMoins2;
    }

    public Float getDeficitNMoins2() {
	return deficitNMoins2;
    }

    public void setDeficitNMoins2(Float deficitNMoins2) {
	this.deficitNMoins2 = deficitNMoins2;
    }

    @Override
    public String toString() {
	return "MicroEntreprise [dateRepriseCreationEntreprise=" + dateRepriseCreationEntreprise + ", typeBenefices=" + typeBenefices + ", chiffreAffairesN=" + chiffreAffairesN
		+ ", chiffreAffairesNMoins1=" + chiffreAffairesNMoins1 + ", chiffreAffairesNMoins2=" + chiffreAffairesNMoins2 + ", deficitNMoins2=" + deficitNMoins2 + "]";
    }

}
