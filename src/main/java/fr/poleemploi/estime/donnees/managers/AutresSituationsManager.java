package fr.poleemploi.estime.donnees.managers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fr.poleemploi.estime.commun.enumerations.exceptions.LoggerMessages;
import fr.poleemploi.estime.donnees.entities.AutresSituationsEntity;
import fr.poleemploi.estime.donnees.repositories.jpa.AutresSituationsRepository;

@Component
public class AutresSituationsManager {

    @Autowired
    private AutresSituationsRepository autresSituationsRepository;

    private static final Logger LOGGER = LoggerFactory.getLogger(AutresSituationsManager.class);

    public void creerAutresSituations(AutresSituationsEntity autresSituationsEntity) {
	try {
	    autresSituationsRepository.save(autresSituationsEntity);
	} catch (Exception e) {
	    // L'échec d'un enregistrement du suivi du parcours utilisateur ne doit pas avoir d'impact sur le fonctionnement de l'application
	    String messageError = String.format(LoggerMessages.AUTRES_SITUATIONS_ENREGISTREMENT_KO.getMessage(), e.getMessage());
	    LOGGER.error(messageError);
	}
    }
}
