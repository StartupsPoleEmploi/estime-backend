package fr.poleemploi.estime.commun.utile;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fr.poleemploi.estime.clientsexternes.poleemploiio.ressources.DetailIndemnisationPEIO;
import fr.poleemploi.estime.clientsexternes.poleemploiio.ressources.UserInfoPEIO;
import fr.poleemploi.estime.commun.enumerations.ParcoursUtilisateurEnum;
import fr.poleemploi.estime.commun.enumerations.TypePopulationEnum;
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

    @Autowired
    private StagingEnvironnementUtile stagingEnvironnementUtile;

    public void tracerParcoursUtilisateurAuthentification(UserInfoPEIO userInfoPEIO, String parcours, BeneficiaireAides beneficiaireAides, DetailIndemnisationPEIO detailIndemnisationPEIO) {
	// On ne suit pas les utilisateurs dans les environnements de développement
	if (!stagingEnvironnementUtile.isStagingEnvironnement()) {
	    SuiviParcoursUtilisateurEntity suiviParcoursUtilisateurEntity = creerSuiviParcoursUtilisateurEntityAuthentification(userInfoPEIO, parcours,
		    getTypePopulation(beneficiaireAides), detailIndemnisationPEIO);

	    suiviParcoursUtilisateurManager.creerSuiviParcoursUtilisateur(suiviParcoursUtilisateurEntity);
	}
    }

    public void tracerParcoursUtilisateurCreationSimulation(String idPoleEmploi, String parcours, BeneficiaireAides beneficiaireAides, InformationsPersonnelles informationsPersonnelles) {
	if (!stagingEnvironnementUtile.isStagingEnvironnement()) {
	    SuiviParcoursUtilisateurEntity suiviParcoursUtilisateurEntity = creerSuiviParcoursUtilisateurEntityCreationSimulation(idPoleEmploi, parcours,
		    getTypePopulation(beneficiaireAides), getCodePostal(informationsPersonnelles));

	    suiviParcoursUtilisateurManager.creerSuiviParcoursUtilisateur(suiviParcoursUtilisateurEntity);
	}
    }

    public void supprimerTracesParcoursUtilisateur(String idPoleEmploi) {
	suiviParcoursUtilisateurManager.supprimerSuiviParcoursUtilisateurParIdPoleEmploi(idPoleEmploi);
    }

    public String getParcoursAccesService(Individu individu) {
	if (individu.isPopulationAutorisee()) {
	    return ParcoursUtilisateurEnum.CONNEXION_REUSSIE.getParcours();
	}
	return ParcoursUtilisateurEnum.CONNEXION_REFUSEE.getParcours();
    }

    private String getTypePopulation(BeneficiaireAides beneficiaireAides) {
	return getTypePopulation(beneficiaireAides.isBeneficiaireARE(), beneficiaireAides.isBeneficiaireASS(), beneficiaireAides.isBeneficiaireRSA(),
		beneficiaireAides.isBeneficiaireAAH());
    }

    private String getCodePostal(InformationsPersonnelles informationsPersonnelles) {
	return informationsPersonnelles.getCodePostal();
    }

    private SuiviParcoursUtilisateurEntity creerSuiviParcoursUtilisateurEntityAuthentification(UserInfoPEIO userInfoPEIO, String parcours, String typePopulation, DetailIndemnisationPEIO detailIndemnisationPEIO) {

	SuiviParcoursUtilisateurEntity suiviParcoursUtilisateurEntity = new SuiviParcoursUtilisateurEntity();
	suiviParcoursUtilisateurEntity.setDateCreation(dateUtile.getDateTimeJour());
	suiviParcoursUtilisateurEntity.setIdPoleEmploi(userInfoPEIO.getSub());
	suiviParcoursUtilisateurEntity.setSuiviParcours(parcours);
	suiviParcoursUtilisateurEntity.setTypePopulation(typePopulation);
	suiviParcoursUtilisateurEntity.setEmail(userInfoPEIO.getEmail());
	suiviParcoursUtilisateurEntity.setNom(userInfoPEIO.getFamilyName());
	suiviParcoursUtilisateurEntity.setPrenom(userInfoPEIO.getGivenName());

	if (detailIndemnisationPEIO.getCodeIndemnisation() != null) {
	    suiviParcoursUtilisateurEntity.setEsdCodeIndemnisation(detailIndemnisationPEIO.getCodeIndemnisation());
	}
	suiviParcoursUtilisateurEntity.setEsdBeneficiaireAssuranceChomage(detailIndemnisationPEIO.isBeneficiaireAssuranceChomage());

	return suiviParcoursUtilisateurEntity;
    }

    private SuiviParcoursUtilisateurEntity creerSuiviParcoursUtilisateurEntityCreationSimulation(String idPoleEmploi, String parcours, String typePopulation, String codePostal) {

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
	    typePopulationBuilder.append(TypePopulationEnum.NON_BENEFICIAIRE.getLibelle());
	} else {
	    StringBuilder beneficiaireBuilder = new StringBuilder("");
	    appendTypePopulation(beneficiaireBuilder, isBeneficiaireARE, TypePopulationEnum.ARE.getLibelle());
	    appendTypePopulation(beneficiaireBuilder, isBeneficiaireASS, TypePopulationEnum.ASS.getLibelle());
	    appendTypePopulation(beneficiaireBuilder, isBeneficiaireRSA, TypePopulationEnum.RSA.getLibelle());
	    appendTypePopulation(beneficiaireBuilder, isBeneficiaireAAH, TypePopulationEnum.AAH.getLibelle());
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
