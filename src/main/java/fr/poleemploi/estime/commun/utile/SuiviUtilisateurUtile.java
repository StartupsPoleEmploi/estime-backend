package fr.poleemploi.estime.commun.utile;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fr.poleemploi.estime.clientsexternes.emploistoredev.ressources.DetailIndemnisationESD;
import fr.poleemploi.estime.clientsexternes.emploistoredev.ressources.UserInfoESD;
import fr.poleemploi.estime.commun.enumerations.ParcoursUtilisateur;
import fr.poleemploi.estime.commun.enumerations.TypePopulation;
import fr.poleemploi.estime.donnees.entities.SuiviParcoursUtilisateurEntity;
import fr.poleemploi.estime.donnees.managers.SuiviParcoursUtilisateurManager;
import fr.poleemploi.estime.services.ressources.BeneficiaireAidesSociales;
import fr.poleemploi.estime.services.ressources.Individu;

/**
 * Classe offrant de méthodes pour tracer le parcours de l'utilisateur et povoir le recontacter par la suite.
 * TODO : traitement à supprimer après la phase de croissance
 * @author ijla5100
 *
 */
@Component 
public class SuiviUtilisateurUtile {

    @Autowired
    private DateUtile dateUtile;

    @Autowired
    private SuiviParcoursUtilisateurManager suiviParcoursUtilisateurManager;
    
    public void tracerParcoursUtilisateur(UserInfoESD userInfoESD, String parcours, BeneficiaireAidesSociales beneficiaireAidesSociales, DetailIndemnisationESD detailIndemnisationESD) {

        SuiviParcoursUtilisateurEntity suiviParcoursUtilisateurEntity = creerSuiviParcoursUtilisateurEntity(
                userInfoESD, 
                parcours, 
                getTypePopulation(beneficiaireAidesSociales), 
                detailIndemnisationESD);

        suiviParcoursUtilisateurManager.creerSuiviParcoursUtilisateur(suiviParcoursUtilisateurEntity); 
    }


    public void tracerParcoursUtilisateur(String idPoleEmploi, String parcours, BeneficiaireAidesSociales beneficiaireAidesSociales) {

        SuiviParcoursUtilisateurEntity suiviParcoursUtilisateurEntity = creerSuiviParcoursUtilisateurEntity(
                idPoleEmploi,
                parcours, 
                getTypePopulation(beneficiaireAidesSociales));

        suiviParcoursUtilisateurManager.creerSuiviParcoursUtilisateur(suiviParcoursUtilisateurEntity); 
    }

    

    public void supprimerTracesParcoursUtilisateur(String idPoleEmploi) {        
        suiviParcoursUtilisateurManager.supprimerSuiviParcoursUtilisateurParIdPoleEmploi(idPoleEmploi);
    }

    public String getParcoursAccesService(Individu individu) {
        if(individu.isPopulationAutorisee()) {
            return ParcoursUtilisateur.CONNEXION_REUSSIE.getParcours();
        }
        return ParcoursUtilisateur.CONNEXION_REFUSEE.getParcours();        
    }

    private String getTypePopulation(BeneficiaireAidesSociales beneficiaireAidesSociales) {
        return getTypePopulation(
                beneficiaireAidesSociales.isBeneficiaireARE(),
                beneficiaireAidesSociales.isBeneficiaireASS(),
                beneficiaireAidesSociales.isBeneficiaireRSA(),
                beneficiaireAidesSociales.isBeneficiaireAAH());  
    }    

    private SuiviParcoursUtilisateurEntity creerSuiviParcoursUtilisateurEntity(UserInfoESD userInfoESD, String parcours, String typePopulation, DetailIndemnisationESD detailIndemnisationESD) {
        
        SuiviParcoursUtilisateurEntity suiviParcoursUtilisateurEntity = new SuiviParcoursUtilisateurEntity();
        suiviParcoursUtilisateurEntity.setDateCreation(dateUtile.getDateTimeJour());
        suiviParcoursUtilisateurEntity.setIdPoleEmploi(userInfoESD.getSub());
        suiviParcoursUtilisateurEntity.setSuiviParcours(parcours);
        suiviParcoursUtilisateurEntity.setTypePopulation(typePopulation);
        suiviParcoursUtilisateurEntity.setEmail(userInfoESD.getEmail());
        suiviParcoursUtilisateurEntity.setNom(userInfoESD.getFamilyName());
        suiviParcoursUtilisateurEntity.setPrenom(userInfoESD.getGivenName());
        
        if(detailIndemnisationESD.getCodeIndemnisation() != null) {
            suiviParcoursUtilisateurEntity.setEsdCodeIndemnisation(detailIndemnisationESD.getCodeIndemnisation());            
        }
        suiviParcoursUtilisateurEntity.setEsdBeneficiaireAssuranceChomage(detailIndemnisationESD.isBeneficiaireAssuranceChomage());
        return suiviParcoursUtilisateurEntity;       
    }
    
    private SuiviParcoursUtilisateurEntity creerSuiviParcoursUtilisateurEntity(String idPoleEmploi, String parcours, String typePopulation) {
        
        SuiviParcoursUtilisateurEntity suiviParcoursUtilisateurEntity = new SuiviParcoursUtilisateurEntity();
        suiviParcoursUtilisateurEntity.setDateCreation(dateUtile.getDateTimeJour());
        suiviParcoursUtilisateurEntity.setIdPoleEmploi(idPoleEmploi);
        suiviParcoursUtilisateurEntity.setSuiviParcours(parcours);
        suiviParcoursUtilisateurEntity.setTypePopulation(typePopulation);   
        return suiviParcoursUtilisateurEntity;       
    }


    private String getTypePopulation(boolean isBeneficiaireARE, boolean isBeneficiaireASS, boolean isBeneficiaireRSA, boolean isBeneficiaireAAH) {
        StringBuilder typePopulationBuilder = new StringBuilder("");

        if(isNonBeneficaireAllocations(isBeneficiaireARE, isBeneficiaireASS, isBeneficiaireRSA, isBeneficiaireAAH)) {
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
        if(isBeneficiaire) {
            if(!beneficiaireBuilder.toString().isEmpty()) {
                beneficiaireBuilder.append(", ");
            } 
            beneficiaireBuilder.append(libelleBeneficaire);
        }
    }

    private boolean isNonBeneficaireAllocations(boolean isBeneficiaireARE, boolean isBeneficiaireASS, boolean isBeneficiaireRSA, boolean isBeneficiaireAAH) {
        return !isBeneficiaireARE && !isBeneficiaireASS && !isBeneficiaireRSA && !isBeneficiaireAAH;
    }

}
