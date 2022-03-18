package fr.poleemploi.estime.logique;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.github.tsohr.JSONObject;

import fr.poleemploi.estime.clientsexternes.mailjet.MailjetClient;

@Component
public class EmailLogique {

    @Autowired
    private MailjetClient mailjetClient;

    public JSONObject createContact(String email) {
	return mailjetClient.addContactMailjet(email);
    }

}
