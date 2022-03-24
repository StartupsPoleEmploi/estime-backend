package fr.poleemploi.estime.commun.utile;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import fr.poleemploi.estime.clientsexternes.sendinblue.SendinblueClient;
import fr.poleemploi.estime.services.ressources.DemandeurEmploi;
import fr.poleemploi.estime.services.ressources.Individu;

/**
 * Classe offrant de méthodes pour tracer le parcours de l'utilisateur et povoir le recontacter par la suite. TODO : traitement à supprimer après la
 * phase de croissance
 * 
 * @author ijla5100
 *
 */
@Component
public class ContactSendinblueUtile {

    @Autowired
    private SendinblueClient sendinblueClient;

    @Async
    public void ajouterContactSendinblue(Individu individu) {
	if (individu.getInformationsPersonnelles() != null && individu.getInformationsPersonnelles().getEmail() != null) {
	    sendinblueClient.addContactSendinblue(individu.getInformationsPersonnelles().getEmail());
	}
    }

    public void miseAJourContactSendinblue(DemandeurEmploi demandeurEmploi, String suiviParcours) {
	if (demandeurEmploi.getInformationsPersonnelles() != null && demandeurEmploi.getInformationsPersonnelles().getEmail() != null) {
	    sendinblueClient.updateContactSendinblue(demandeurEmploi.getInformationsPersonnelles().getEmail(), suiviParcours);
	}
    }
}
