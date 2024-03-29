package fr.poleemploi.estime.commun.utile.demandeuremploi;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fr.poleemploi.estime.commun.enumerations.NationaliteEnum;
import fr.poleemploi.estime.commun.utile.DateUtile;
import fr.poleemploi.estime.services.ressources.DemandeurEmploi;
import fr.poleemploi.estime.services.ressources.InformationsPersonnelles;

@Component
public class InformationsPersonnellesUtile {

    @Autowired
    private CodeDepartementUtile codeDepartementUtile;

    @Autowired
    private DateUtile dateUtile;

    public boolean isFrancais(InformationsPersonnelles informationsPersonnelles) {
	return NationaliteEnum.FRANCAISE.getValeur().equalsIgnoreCase(informationsPersonnelles.getNationalite());
    }

    public boolean isEuropeenOuSuisse(InformationsPersonnelles informationsPersonnelles) {
	return NationaliteEnum.RESSORTISSANT_UNION_EUROPEENNE.getValeur().equalsIgnoreCase(informationsPersonnelles.getNationalite())
		|| NationaliteEnum.RESSORTISSANT_ESPACE_ECONOMIQUE_EUROPEEN.getValeur().equalsIgnoreCase(informationsPersonnelles.getNationalite())
		|| NationaliteEnum.SUISSE.getValeur().equalsIgnoreCase(informationsPersonnelles.getNationalite());
    }

    public boolean isNotFrancaisOuEuropeenOuSuisse(InformationsPersonnelles informationsPersonnelles) {
	return NationaliteEnum.AUTRE.getValeur().equalsIgnoreCase(informationsPersonnelles.getNationalite());
    }

    public boolean hasTitreSejourEnFranceValide(InformationsPersonnelles informationsPersonnelles) {
	return informationsPersonnelles.hasTitreSejourEnFranceValide();
    }

    public boolean isDeFranceMetropolitaine(DemandeurEmploi demandeurEmploi) {
	return !codeDepartementUtile.isDesDOM(demandeurEmploi.getInformationsPersonnelles().getLogement().getCoordonnees().getCodePostal());
    }

    public boolean isDesDOM(DemandeurEmploi demandeurEmploi) {
	return codeDepartementUtile.isDesDOM(demandeurEmploi.getInformationsPersonnelles().getLogement().getCoordonnees().getCodePostal());
    }

    public boolean isDeMayotte(DemandeurEmploi demandeurEmploi) {
	if (hasCodePostal(demandeurEmploi)) {
	    return codeDepartementUtile.isDeMayotte(demandeurEmploi.getInformationsPersonnelles().getLogement().getCoordonnees().getCodePostal());
	}
	return false;
    }

    public boolean isBeneficiaireACRE(DemandeurEmploi demandeurEmploi) {
	return (demandeurEmploi.getInformationsPersonnelles().isBeneficiaireACRE() && hasMicroEntreprise(demandeurEmploi));
    }

    public int getNombreMoisDepuisCreationEntreprise(DemandeurEmploi demandeurEmploi, LocalDate dateDebutSimulation) {
	if (hasMicroEntreprise(demandeurEmploi)) {
	    return dateUtile.getNbrMoisEntreDeuxLocalDates(
		    dateUtile.getDateDernierJourDuMois(demandeurEmploi.getInformationsPersonnelles().getMicroEntreprise().getDateRepriseCreationEntreprise()), dateDebutSimulation);
	}
	return 0;
    }

    public boolean hasCodePostal(DemandeurEmploi demandeurEmploi) {
	return demandeurEmploi.getInformationsPersonnelles() != null && demandeurEmploi.getInformationsPersonnelles().getLogement() != null
		&& demandeurEmploi.getInformationsPersonnelles().getLogement().getCoordonnees() != null
		&& demandeurEmploi.getInformationsPersonnelles().getLogement().getCoordonnees().getCodePostal() != null;
    }

    public boolean hasMicroEntreprise(DemandeurEmploi demandeurEmploi) {
	return (demandeurEmploi != null && demandeurEmploi.getInformationsPersonnelles() != null && demandeurEmploi.getInformationsPersonnelles().isMicroEntrepreneur()
		&& demandeurEmploi.getInformationsPersonnelles().getMicroEntreprise() != null);
    }

    public float getRevenusActuelsSur1MoisMicroEntreprise(DemandeurEmploi demandeurEmploi) {
	if (hasMicroEntreprise(demandeurEmploi)) {
	    return BigDecimal.valueOf(demandeurEmploi.getInformationsPersonnelles().getMicroEntreprise().getChiffreAffairesN())
		    .divide(BigDecimal.valueOf(12), 0, RoundingMode.HALF_UP).floatValue();
	}
	return 0;
    }

    public boolean isSalarie(DemandeurEmploi demandeurEmploi) {
	return (demandeurEmploi.getInformationsPersonnelles() != null && demandeurEmploi.getInformationsPersonnelles().isSalarie());
    }

    public boolean hasCumulAncienEtNouveauSalaire(DemandeurEmploi demandeurEmploi) {
	return (isSalarie(demandeurEmploi) && demandeurEmploi.getInformationsPersonnelles().hasCumulAncienEtNouveauSalaire());
    }
}
