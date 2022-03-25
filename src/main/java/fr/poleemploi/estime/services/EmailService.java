package fr.poleemploi.estime.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fr.poleemploi.estime.logique.EmailLogique;
import fr.poleemploi.estime.services.controleurs.EmailServiceControleur;

@RestController
@RequestMapping("/emails")
public class EmailService {

    @Autowired
    private EmailLogique emailLogique;

    @Autowired
    private EmailServiceControleur emailServiceControleur;

    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public void creerEmail(@RequestBody String email) {
	emailServiceControleur.controlerDonneesEntreeServiceCreerEmail(email);
	emailLogique.createContact(email);
    }

}
