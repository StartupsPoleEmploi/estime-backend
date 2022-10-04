package fr.poleemploi.estime.commun.utile;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fr.poleemploi.estime.clientsexternes.poleemploiio.ressources.DetailIndemnisationPEIOOut;
import fr.poleemploi.estime.clientsexternes.poleemploiio.ressources.UserInfoPEIOOut;
import fr.poleemploi.estime.commun.enumerations.ParcoursUtilisateurEnum;
import fr.poleemploi.estime.commun.enumerations.TypePopulationEnum;
import fr.poleemploi.estime.donnees.entities.SuiviParcoursUtilisateurEntity;
import fr.poleemploi.estime.donnees.managers.SuiviParcoursUtilisateurManager;
import fr.poleemploi.estime.services.ressources.BeneficiaireAides;
import fr.poleemploi.estime.services.ressources.Individu;
import fr.poleemploi.estime.services.ressources.InformationsPersonnelles;

/**
 * Classe offrant de méthodes pour tracer le parcours de l'utilisateur et povoir le recontacter par la suite. 
 * Traitement à supprimer après la phase de croissance
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

    public void tracerParcoursUtilisateurAuthentification(UserInfoPEIOOut userInfoESD, String parcours, BeneficiaireAides beneficiaireAides, DetailIndemnisationPEIOOut detailIndemnisationESD, String trafficSource) {
	SuiviParcoursUtilisateurEntity suiviParcoursUtilisateurEntity = creerSuiviParcoursUtilisateurEntityAuthentification(userInfoESD, parcours,
		getTypePopulation(beneficiaireAides), detailIndemnisationESD, trafficSource);

	suiviParcoursUtilisateurManager.creerSuiviParcoursUtilisateur(suiviParcoursUtilisateurEntity);
    }

    public void tracerParcoursUtilisateurCreationSimulation(String idPoleEmploi, String idEstime, String parcours, BeneficiaireAides beneficiaireAides, InformationsPersonnelles informationsPersonnelles) {
	SuiviParcoursUtilisateurEntity suiviParcoursUtilisateurEntity = creerSuiviParcoursUtilisateurEntityCreationSimulation(idPoleEmploi, idEstime, parcours,
		getTypePopulation(beneficiaireAides), getCodePostal(informationsPersonnelles), getEmail(informationsPersonnelles));

	suiviParcoursUtilisateurManager.creerSuiviParcoursUtilisateur(suiviParcoursUtilisateurEntity);
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

    private SuiviParcoursUtilisateurEntity creerSuiviParcoursUtilisateurEntityAuthentification(UserInfoPEIOOut userInfoESD, String parcours, String typePopulation, DetailIndemnisationPEIOOut detailIndemnisationESD, String trafficSource) {
	SuiviParcoursUtilisateurEntity suiviParcoursUtilisateurEntity = new SuiviParcoursUtilisateurEntity();
	suiviParcoursUtilisateurEntity.setDateCreation(dateUtile.getDateTimeJour());
	suiviParcoursUtilisateurEntity.setIdPoleEmploi(userInfoESD.getSub());
	suiviParcoursUtilisateurEntity.setSuiviParcours(parcours);
	suiviParcoursUtilisateurEntity.setTypePopulation(typePopulation);
	suiviParcoursUtilisateurEntity.setEmail(userInfoESD.getEmail());
	suiviParcoursUtilisateurEntity.setNom(userInfoESD.getFamilyName());
	suiviParcoursUtilisateurEntity.setPrenom(userInfoESD.getGivenName());
	suiviParcoursUtilisateurEntity.setTrafficSource(trafficSource);

	if (detailIndemnisationESD.getCodeIndemnisation() != null) {
	    suiviParcoursUtilisateurEntity.setEsdCodeIndemnisation(detailIndemnisationESD.getCodeIndemnisation());
	}
	suiviParcoursUtilisateurEntity.setEsdBeneficiaireAssuranceChomage(detailIndemnisationESD.isBeneficiaireAssuranceChomage());
	return suiviParcoursUtilisateurEntity;
    }

    private SuiviParcoursUtilisateurEntity creerSuiviParcoursUtilisateurEntityCreationSimulation(String idPoleEmploi, String idEstime, String parcours, String typePopulation, String codePostal, String email) {
	SuiviParcoursUtilisateurEntity suiviParcoursUtilisateurEntity = new SuiviParcoursUtilisateurEntity();
	suiviParcoursUtilisateurEntity.setDateCreation(dateUtile.getDateTimeJour());
	suiviParcoursUtilisateurEntity.setIdPoleEmploi(idPoleEmploi);
	suiviParcoursUtilisateurEntity.setIdEstime(idEstime);
	suiviParcoursUtilisateurEntity.setSuiviParcours(parcours);
	suiviParcoursUtilisateurEntity.setTypePopulation(typePopulation);
	suiviParcoursUtilisateurEntity.setCodePostal(codePostal);
	suiviParcoursUtilisateurEntity.setEmail(email);
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

    private String getCodePostal(InformationsPersonnelles informationsPersonnelles) {
	if (informationsPersonnelles != null && informationsPersonnelles.getLogement() != null && informationsPersonnelles.getLogement().getCoordonnees() != null) {
	    return informationsPersonnelles.getLogement().getCoordonnees().getCodePostal();
	} else {
	    return StringUtile.EMPTY;
	}
    }

    private String getEmail(InformationsPersonnelles informationsPersonnelles) {
	if (informationsPersonnelles != null && informationsPersonnelles.getEmail() != null) {
	    return informationsPersonnelles.getEmail();
	} else {
	    return StringUtile.EMPTY;
	}
    }
}
