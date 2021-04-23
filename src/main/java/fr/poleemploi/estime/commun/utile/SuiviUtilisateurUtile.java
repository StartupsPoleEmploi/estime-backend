package fr.poleemploi.estime.commun.utile;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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

    public void tracerParcoursUtilisateur(String idPoleEmploi, String nom, String prenom, String email, String parcours, BeneficiaireAidesSociales beneficiaireAidesSociales, boolean isValoriserInfosPerso) {

        SuiviParcoursUtilisateurEntity suiviParcoursUtilisateurEntity = creerSuiviParcoursUtilisateurEntity(
                idPoleEmploi, 
                nom, 
                prenom, 
                email, 
                parcours, 
                getTypePopulation(beneficiaireAidesSociales), 
                isValoriserInfosPerso);

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

    private SuiviParcoursUtilisateurEntity creerSuiviParcoursUtilisateurEntity(String idPoleEmploi, String nom, String prenom, String email, String parcours, String typePopulation, boolean isValoriserInfosPerso) {
        SuiviParcoursUtilisateurEntity suiviParcoursUtilisateurEntity = new SuiviParcoursUtilisateurEntity();
        suiviParcoursUtilisateurEntity.setDateCreation(dateUtile.getDateTimeJour());
        suiviParcoursUtilisateurEntity.setIdPoleEmploi(idPoleEmploi);
        suiviParcoursUtilisateurEntity.setSuiviParcours(parcours);
        suiviParcoursUtilisateurEntity.setTypePopulation(typePopulation);
        if(isValoriserInfosPerso) {
            suiviParcoursUtilisateurEntity.setEmail(email);
            suiviParcoursUtilisateurEntity.setNom(nom);
            suiviParcoursUtilisateurEntity.setPrenom(prenom);
        }
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
