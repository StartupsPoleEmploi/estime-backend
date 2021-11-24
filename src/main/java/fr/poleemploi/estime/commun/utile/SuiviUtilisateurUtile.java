package fr.poleemploi.estime.commun.utile;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fr.poleemploi.estime.clientsexternes.poleemploiio.ressources.DetailIndemnisationPEIO;
import fr.poleemploi.estime.clientsexternes.poleemploiio.ressources.UserInfoPEIO;
import fr.poleemploi.estime.commun.enumerations.ParcoursUtilisateur;
import fr.poleemploi.estime.commun.enumerations.TypePopulation;
import fr.poleemploi.estime.donnees.entities.SuiviParcoursUtilisateurEntity;
import fr.poleemploi.estime.donnees.managers.SuiviParcoursUtilisateurManager;
import fr.poleemploi.estime.services.ressources.BeneficiaireAides;
import fr.poleemploi.estime.services.ressources.Individu;
import fr.poleemploi.estime.services.ressources.InformationsPersonnelles;

/**
 * Classe offrant de méthodes pour tracer le parcours de l'utilisateur et povoir le recontacter par la suite. TODO : traitement à supprimer après la
 * phase de croissance
 * 
 * @author ijla5100
 *
 */
@Component
public class SuiviUtilisateurUtile {

    @Autowired
    private DateUtile dateUtile;

    @Autowired
    private SuiviParcoursUtilisateurManager suiviParcoursUtilisateurManager;

    public void tracerParcoursUtilisateur(UserInfoPEIO userInfoPEIO, String parcours, BeneficiaireAides beneficiaireAides, InformationsPersonnelles informationsPersonnelles, DetailIndemnisationPEIO detailIndemnisationPEIO) {

	SuiviParcoursUtilisateurEntity suiviParcoursUtilisateurEntity = creerSuiviParcoursUtilisateurEntity(userInfoPEIO, parcours, getTypePopulation(beneficiaireAides),
		getCodePostal(informationsPersonnelles), detailIndemnisationPEIO);

	suiviParcoursUtilisateurManager.creerSuiviParcoursUtilisateur(suiviParcoursUtilisateurEntity);
    }

    public void tracerParcoursUtilisateur(String idPoleEmploi, String parcours, BeneficiaireAides beneficiaireAides, InformationsPersonnelles informationsPersonnelles) {

	SuiviParcoursUtilisateurEntity suiviParcoursUtilisateurEntity = creerSuiviParcoursUtilisateurEntity(idPoleEmploi, parcours, getTypePopulation(beneficiaireAides),
		getCodePostal(informationsPersonnelles));

	suiviParcoursUtilisateurManager.creerSuiviParcoursUtilisateur(suiviParcoursUtilisateurEntity);
    }

    public void supprimerTracesParcoursUtilisateur(String idPoleEmploi) {
	suiviParcoursUtilisateurManager.supprimerSuiviParcoursUtilisateurParIdPoleEmploi(idPoleEmploi);
    }

    public String getParcoursAccesService(Individu individu) {
	if (individu.isPopulationAutorisee()) {
	    return ParcoursUtilisateur.CONNEXION_REUSSIE.getParcours();
	}
	return ParcoursUtilisateur.CONNEXION_REFUSEE.getParcours();
    }

    private String getTypePopulation(BeneficiaireAides beneficiaireAides) {
	return getTypePopulation(beneficiaireAides.isBeneficiaireARE(), beneficiaireAides.isBeneficiaireASS(), beneficiaireAides.isBeneficiaireRSA(),
		beneficiaireAides.isBeneficiaireAAH());
    }

    private String getCodePostal(InformationsPersonnelles informationsPersonnelles) {
	return informationsPersonnelles.getCodePostal();
    }

    private SuiviParcoursUtilisateurEntity creerSuiviParcoursUtilisateurEntity(UserInfoPEIO userInfoPEIO, String parcours, String typePopulation, String codePostal, DetailIndemnisationPEIO detailIndemnisationPEIO) {

	SuiviParcoursUtilisateurEntity suiviParcoursUtilisateurEntity = new SuiviParcoursUtilisateurEntity();
	suiviParcoursUtilisateurEntity.setDateCreation(dateUtile.getDateTimeJour());
	suiviParcoursUtilisateurEntity.setIdPoleEmploi(userInfoPEIO.getSub());
	suiviParcoursUtilisateurEntity.setSuiviParcours(parcours);
	suiviParcoursUtilisateurEntity.setTypePopulation(typePopulation);
	suiviParcoursUtilisateurEntity.setEmail(userInfoPEIO.getEmail());
	suiviParcoursUtilisateurEntity.setNom(userInfoPEIO.getFamilyName());
	suiviParcoursUtilisateurEntity.setPrenom(userInfoPEIO.getGivenName());
	suiviParcoursUtilisateurEntity.setCodePostal(codePostal);

	if (detailIndemnisationPEIO.getCodeIndemnisation() != null) {
	    suiviParcoursUtilisateurEntity.setEsdCodeIndemnisation(detailIndemnisationPEIO.getCodeIndemnisation());
	}
	suiviParcoursUtilisateurEntity.setEsdBeneficiaireAssuranceChomage(detailIndemnisationPEIO.isBeneficiaireAssuranceChomage());

	return suiviParcoursUtilisateurEntity;
    }

    private SuiviParcoursUtilisateurEntity creerSuiviParcoursUtilisateurEntity(String idPoleEmploi, String parcours, String typePopulation, String codePostal) {

	SuiviParcoursUtilisateurEntity suiviParcoursUtilisateurEntity = new SuiviParcoursUtilisateurEntity();
	suiviParcoursUtilisateurEntity.setDateCreation(dateUtile.getDateTimeJour());
	suiviParcoursUtilisateurEntity.setIdPoleEmploi(idPoleEmploi);
	suiviParcoursUtilisateurEntity.setSuiviParcours(parcours);
	suiviParcoursUtilisateurEntity.setTypePopulation(typePopulation);
	suiviParcoursUtilisateurEntity.setCodePostal(codePostal);
	return suiviParcoursUtilisateurEntity;
    }

    private String getTypePopulation(boolean isBeneficiaireARE, boolean isBeneficiaireASS, boolean isBeneficiaireRSA, boolean isBeneficiaireAAH) {
	StringBuilder typePopulationBuilder = new StringBuilder("");

	if (isNonBeneficaireAllocations(isBeneficiaireARE, isBeneficiaireASS, isBeneficiaireRSA, isBeneficiaireAAH)) {
	    typePopulationBuilder.append(TypePopulation.NON_BENEFICIAIRE.getLibelle());
	} else {
	    StringBuilder beneficiaireBuilder = new StringBuilder("");
	    appendTypePopulation(beneficiaireBuilder, isBeneficiaireARE, TypePopulation.ARE.getLibelle());
	    appendTypePopulation(beneficiaireBuilder, isBeneficiaireASS, TypePopulation.ASS.getLibelle());
	    appendTypePopulation(beneficiaireBuilder, isBeneficiaireRSA, TypePopulation.RSA.getLibelle());
	    appendTypePopulation(beneficiaireBuilder, isBeneficiaireAAH, TypePopulation.AAH.getLibelle());
	    typePopulationBuilder.append(beneficiaireBuilder.toString());
	}
	return typePopulationBuilder.toString();
    }

    private void appendTypePopulation(StringBuilder beneficiaireBuilder, boolean isBeneficiaire, String libelleBeneficaire) {
	if (isBeneficiaire) {
	    if (!beneficiaireBuilder.toString().isEmpty()) {
		beneficiaireBuilder.append(", ");
	    }
	    beneficiaireBuilder.append(libelleBeneficaire);
	}
    }

    private boolean isNonBeneficaireAllocations(boolean isBeneficiaireARE, boolean isBeneficiaireASS, boolean isBeneficiaireRSA, boolean isBeneficiaireAAH) {
	return !isBeneficiaireARE && !isBeneficiaireASS && !isBeneficiaireRSA && !isBeneficiaireAAH;
    }

}
