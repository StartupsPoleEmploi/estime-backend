package fr.poleemploi.estime.logique;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fr.poleemploi.estime.clientsexternes.mailjet.MailjetClient;

@Component
public class EmailLogique {

    @Autowired
    private MailjetClient mailjetClient;

    public void createContact(String email) {
	mailjetClient.addContactMailjet(email);
    }

}
