package fr.poleemploi.estime.commun.utile.demandeuremploi;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fr.poleemploi.estime.commun.enumerations.NationaliteEnum;
import fr.poleemploi.estime.commun.enumerations.StatutOccupationLogementEnum;
import fr.poleemploi.estime.services.ressources.DemandeurEmploi;
import fr.poleemploi.estime.services.ressources.InformationsPersonnelles;
import fr.poleemploi.estime.services.ressources.Logement;
import fr.poleemploi.estime.services.ressources.StatutOccupationLogement;

@Component
public class InformationsPersonnellesUtile {

    @Autowired
    private CodeDepartementUtile codeDepartementUtile;

    public boolean isFrancais(InformationsPersonnelles informationsPersonnelles) {
	return NationaliteEnum.FRANCAISE.getValeur().equalsIgnoreCase(informationsPersonnelles.getNationalite());
    }

    public boolean isEuropeenOuSuisse(InformationsPersonnelles informationsPersonnelles) {
	return NationaliteEnum.EUROPEEN_OU_SUISSE.getValeur().equalsIgnoreCase(informationsPersonnelles.getNationalite());
    }

    public boolean isNotFrancaisOuEuropeenOuSuisse(InformationsPersonnelles informationsPersonnelles) {
	return NationaliteEnum.AUTRE.getValeur().equalsIgnoreCase(informationsPersonnelles.getNationalite());
    }

    public boolean isTitreSejourEnFranceValide(InformationsPersonnelles informationsPersonnelles) {
	return informationsPersonnelles.getTitreSejourEnFranceValide().booleanValue();
    }

    public boolean isDeFranceMetropolitaine(DemandeurEmploi demandeurEmploi) {
	String codeDepartement = codeDepartementUtile.getCodeDepartement(demandeurEmploi.getInformationsPersonnelles().getLogement().getCoordonnees().getCodePostal());
	return codeDepartement.length() == 2;
    }

    public boolean isDesDOM(DemandeurEmploi demandeurEmploi) {
	String codeDepartement = codeDepartementUtile.getCodeDepartement(demandeurEmploi.getInformationsPersonnelles().getLogement().getCoordonnees().getCodePostal());
	return codeDepartement.length() == 3;
    }

    public boolean isDeMayotte(DemandeurEmploi demandeurEmploi) {
	if (hasCodePostal(demandeurEmploi)) {
	    String codeDepartement = codeDepartementUtile.getCodeDepartement(demandeurEmploi.getInformationsPersonnelles().getLogement().getCoordonnees().getCodePostal());
	    return CodeDepartementUtile.CODE_DEPARTEMENT_MAYOTTE.equals(codeDepartement);
	}
	return false;
    }

    public boolean hasCodePostal(DemandeurEmploi demandeurEmploi) {
	return demandeurEmploi.getInformationsPersonnelles() != null && demandeurEmploi.getInformationsPersonnelles().getLogement() != null
		&& demandeurEmploi.getInformationsPersonnelles().getLogement().getCoordonnees() != null
		&& demandeurEmploi.getInformationsPersonnelles().getLogement().getCoordonnees().getCodePostal() != null;
    }

    public String getStatutOccupationLogement(Logement logement) {
	if (logement != null) {
	    StatutOccupationLogement statutOccupationLogement = logement.getStatutOccupationLogement();
	    if (statutOccupationLogement.isLogeGratuitement())
		return StatutOccupationLogementEnum.LOGE_GRATUITEMENT.getLibelle();
	    else if (statutOccupationLogement.isLocataireHLM())
		return StatutOccupationLogementEnum.LOCATAIRE_HLM.getLibelle();
	    else if (statutOccupationLogement.isLocataireMeuble())
		return StatutOccupationLogementEnum.LOCATAIRE_MEUBLE.getLibelle();
	    else if (statutOccupationLogement.isLocataireNonMeuble())
		return StatutOccupationLogementEnum.LOCATAIRE_NON_MEUBLE.getLibelle();
	    else if (statutOccupationLogement.isProprietaire())
		return StatutOccupationLogementEnum.PROPRIETAIRE.getLibelle();
	    else if (statutOccupationLogement.isProprietaireAvecEmprunt())
		return StatutOccupationLogementEnum.PROPRIETAIRE_AVEC_EMPRUNT.getLibelle();
	}
	return StatutOccupationLogementEnum.NON_RENSEIGNE.getLibelle();
    }
}
