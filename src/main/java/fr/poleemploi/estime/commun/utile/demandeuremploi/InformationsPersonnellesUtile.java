package fr.poleemploi.estime.commun.utile.demandeuremploi;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fr.poleemploi.estime.commun.enumerations.NationaliteEnum;
import fr.poleemploi.estime.commun.enumerations.StatutOccupationLogementEnum;
import fr.poleemploi.estime.commun.utile.DateUtile;
import fr.poleemploi.estime.services.ressources.DemandeurEmploi;
import fr.poleemploi.estime.services.ressources.InformationsPersonnelles;
import fr.poleemploi.estime.services.ressources.Logement;
import fr.poleemploi.estime.services.ressources.StatutOccupationLogement;

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

    public boolean isTitreSejourEnFranceValide(InformationsPersonnelles informationsPersonnelles) {
	return informationsPersonnelles.getTitreSejourEnFranceValide().booleanValue();
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
	return (demandeurEmploi != null && demandeurEmploi.getInformationsPersonnelles() != null && demandeurEmploi.getInformationsPersonnelles().isBeneficiaireACRE() != null
		&& demandeurEmploi.getInformationsPersonnelles().isBeneficiaireACRE().booleanValue()
		&& demandeurEmploi.getInformationsPersonnelles().getDateRepriseCreationEntreprise() != null);
    }

    public int getNombreMoisDepuisCreationEntreprise(DemandeurEmploi demandeurEmploi, LocalDate dateDebutSimulation) {
	if (isBeneficiaireACRE(demandeurEmploi)) {
	    return dateUtile.getNbrMoisEntreDeuxLocalDates(demandeurEmploi.getInformationsPersonnelles().getDateRepriseCreationEntreprise(), dateDebutSimulation);
	}
	return 0;
    }

    public boolean hasCodePostal(DemandeurEmploi demandeurEmploi) {
	return demandeurEmploi.getInformationsPersonnelles() != null && demandeurEmploi.getInformationsPersonnelles().getLogement() != null
		&& demandeurEmploi.getInformationsPersonnelles().getLogement().getCoordonnees() != null
		&& demandeurEmploi.getInformationsPersonnelles().getLogement().getCoordonnees().getCodePostal() != null;
    }

    public String getStatutOccupationLogement(Logement logement) {
	if (logement != null) {
	    StatutOccupationLogement statutOccupationLogement = logement.getStatutOccupationLogement();
	    if (statutOccupationLogement.isLogeGratuitement()) {
		return StatutOccupationLogementEnum.LOGE_GRATUITEMENT.getLibelle();
	    }
	    if (statutOccupationLogement.isLocataireHLM()) {
		return StatutOccupationLogementEnum.LOCATAIRE_HLM.getLibelle();
	    }
	    if (statutOccupationLogement.isLocataireMeuble()) {
		return StatutOccupationLogementEnum.LOCATAIRE_MEUBLE.getLibelle();
	    }
	    if (statutOccupationLogement.isLocataireNonMeuble()) {
		return StatutOccupationLogementEnum.LOCATAIRE_NON_MEUBLE.getLibelle();
	    }
	    if (statutOccupationLogement.isProprietaire()) {
		return StatutOccupationLogementEnum.PROPRIETAIRE.getLibelle();
	    }
	    if (statutOccupationLogement.isProprietaireAvecEmprunt()) {
		return StatutOccupationLogementEnum.PROPRIETAIRE_AVEC_EMPRUNT.getLibelle();
	    }
	}
	return StatutOccupationLogementEnum.NON_RENSEIGNE.getLibelle();
    }
}
