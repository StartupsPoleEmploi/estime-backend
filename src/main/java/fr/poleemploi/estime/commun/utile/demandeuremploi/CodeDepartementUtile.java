package fr.poleemploi.estime.commun.utile.demandeuremploi;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fr.poleemploi.estime.commun.utile.StringUtile;

@Component
public class CodeDepartementUtile {

    public static final String CODE_DEPARTEMENT_MAYOTTE = "976";
    public static final int CODE_DEPARTEMENT_VAL_OISE = 95;

    @Autowired
    private StringUtile stringUtile;

    public String getCodeDepartement(String codePostal) {
	String deuxPremiersCaracteresCodePostal = stringUtile.getPremiersCaracteres(codePostal, 2);
	if (stringUtile.isNumeric(deuxPremiersCaracteresCodePostal) && Integer.valueOf(deuxPremiersCaracteresCodePostal) > CODE_DEPARTEMENT_VAL_OISE) {
	    return stringUtile.getPremiersCaracteres(codePostal, 3);
	}
	return deuxPremiersCaracteresCodePostal;
    }

    public boolean isDesDOM(String codePostal) {
	return getCodeDepartement(codePostal).length() == 3;
    }

    public boolean isDeMayotte(String codePostal) {
	return getCodeDepartement(codePostal).equals(CODE_DEPARTEMENT_MAYOTTE);
    }
}
