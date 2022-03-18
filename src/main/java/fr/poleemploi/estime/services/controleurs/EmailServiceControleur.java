package fr.poleemploi.estime.services.controleurs;

import java.util.regex.Pattern;

import org.springframework.stereotype.Component;

import fr.poleemploi.estime.commun.enumerations.exceptions.BadRequestMessages;
import fr.poleemploi.estime.services.exceptions.BadRequestException;

@Component
public class EmailServiceControleur {

    public void controlerDonneesEntreeServiceCreerEmail(String email) {
	String emailRegex = "^[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,3}$";
	Pattern emailPattern = Pattern.compile(emailRegex);
	if (email == null || email.isEmpty() || email.equals("")) {
	    throw new BadRequestException(String.format(BadRequestMessages.EMAIL_OBLIGATOIRE.getMessage()));
	}
	if (!emailPattern.matcher(email).matches()) {
	    throw new BadRequestException(String.format(BadRequestMessages.EMAIL_INVALIDE.getMessage(), email));
	}
    }
}
