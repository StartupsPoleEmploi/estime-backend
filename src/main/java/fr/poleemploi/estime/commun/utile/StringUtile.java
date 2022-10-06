package fr.poleemploi.estime.commun.utile;

import java.util.UUID;
import java.util.regex.Pattern;

import org.springframework.stereotype.Component;

@Component
public class StringUtile {

    public static final String EMPTY = "";
    private Pattern patternCheckIsNumeric = Pattern.compile("-?\\d+(\\.\\d+)?");

    public boolean isNumeric(String stringToCheck) {
	if (stringToCheck == null) {
	    return false;
	}
	return patternCheckIsNumeric.matcher(stringToCheck).matches();
    }

    public String getPremiersCaracteres(String stringValue, int nombreCaracteres) {
	return stringValue.subSequence(0, nombreCaracteres).toString();
    }

    public String generateRandomString() {
	UUID randomUUID = UUID.randomUUID();
	return randomUUID.toString().replace("_", "").substring(0, 15);
    }
}
