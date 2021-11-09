package fr.poleemploi.estime.donnees.managers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fr.poleemploi.estime.commun.enumerations.exceptions.LoggerMessages;
import fr.poleemploi.estime.donnees.entities.SuiviParcoursUtilisateurEntity;
import fr.poleemploi.estime.donnees.repositories.jpa.SuiviParcoursUtilisateurRepository;

@Component
public class SuiviParcoursUtilisateurManager {

    @Autowired
    private SuiviParcoursUtilisateurRepository suiviParcoursUtilisateurRepository;

    private static final Logger LOGGER = LoggerFactory.getLogger(SuiviParcoursUtilisateurManager.class);

    public void creerSuiviParcoursUtilisateur(SuiviParcoursUtilisateurEntity suiviParcoursUtilisateurEntity) {
	try {
	    suiviParcoursUtilisateurRepository.save(suiviParcoursUtilisateurEntity);
	} catch (Exception e) {
	    // L'échec d'un enregistrement du suivi du parcours utilisateur ne doit pas avoir d'impact sur le fonctionnement de l'application
	    String messageError = String.format(LoggerMessages.SUIVI_UTILISATEUR_ENREGISTREMENT_KO.getMessage(), e.getMessage());
	    LOGGER.error(messageError);
	}
    }

    public void supprimerSuiviParcoursUtilisateurParIdPoleEmploi(String email) {
	suiviParcoursUtilisateurRepository.deleteByIdPoleEmploi(email);
    }
}
