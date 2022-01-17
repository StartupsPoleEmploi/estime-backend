package fr.poleemploi.estime.services.controleurs.demandeuremploi;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import fr.poleemploi.estime.commun.enumerations.NationaliteEnum;
import fr.poleemploi.estime.commun.enumerations.exceptions.BadRequestMessages;
import fr.poleemploi.estime.commun.utile.demandeuremploi.NationalitesUtile;
import fr.poleemploi.estime.services.exceptions.BadRequestException;
import fr.poleemploi.estime.services.ressources.InformationsPersonnelles;

@Component
public class InformationsPersonnellesControleur {

    @Autowired
    private NationalitesUtile nationalitesUtile;

    public void controlerDonnees(InformationsPersonnelles informationsPersonnelles) {
	if (informationsPersonnelles == null) {
	    throw new BadRequestException(String.format(BadRequestMessages.CHAMP_OBLIGATOIRE.getMessage(), "informationsPersonnelles"));
	} else {
	    if (ObjectUtils.isEmpty(informationsPersonnelles.getLogement())) {
		throw new BadRequestException(String.format(BadRequestMessages.CHAMP_OBLIGATOIRE.getMessage(), "logement de informationsPersonnelles"));
	    }
	    if (ObjectUtils.isEmpty(informationsPersonnelles.getLogement().getCoordonnees())) {
		throw new BadRequestException(String.format(BadRequestMessages.CHAMP_OBLIGATOIRE.getMessage(), "coordonnees de logement"));
	    }
	    if (ObjectUtils.isEmpty(informationsPersonnelles.getLogement().getCoordonnees().getCodePostal())) {
		throw new BadRequestException(String.format(BadRequestMessages.CHAMP_OBLIGATOIRE.getMessage(), "codePostal de coordonnees"));
	    }
	    if (informationsPersonnelles.getDateNaissance() == null) {
		throw new BadRequestException(String.format(BadRequestMessages.CHAMP_OBLIGATOIRE.getMessage(), "dateNaissance de informationsPersonnelles"));
	    }
	    if (ObjectUtils.isEmpty(informationsPersonnelles.getNationalite())) {
		throw new BadRequestException(String.format(BadRequestMessages.CHAMP_OBLIGATOIRE.getMessage(), "nationalite de informationsPersonnelles"));
	    } else {
		controleNationalite(informationsPersonnelles);
	    }
	}
    }

    private void controleNationalite(InformationsPersonnelles informationsPersonnelles) {
	isValeurNationnaliteCorrecte(informationsPersonnelles.getNationalite());
	if (NationaliteEnum.AUTRE.getValeur().equalsIgnoreCase(informationsPersonnelles.getNationalite()) && informationsPersonnelles.getTitreSejourEnFranceValide() == null) {
	    throw new BadRequestException(String.format(BadRequestMessages.CHAMP_OBLIGATOIRE.getMessage(), "titreSejourEnFranceValide de informationsPersonnelles"));
	}
    }

    private void isValeurNationnaliteCorrecte(String nationalite) {
	boolean nationaliteCorrecte = Arrays.asList(NationaliteEnum.values()).stream().anyMatch(nationaliteEnum -> nationaliteEnum.getValeur().equalsIgnoreCase(nationalite));
	if (!nationaliteCorrecte) {
	    throw new BadRequestException(String.format(BadRequestMessages.NATIONALITE_INCORRECTE.getMessage(), nationalitesUtile.getListeFormateeNationalitesPossibles()));
	}
    }
}
