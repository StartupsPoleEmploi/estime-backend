package fr.poleemploi.estime.services.controleurs.demandeuremploi;

import java.util.List;

import org.springframework.stereotype.Component;

import fr.poleemploi.estime.commun.enumerations.exceptions.BadRequestMessages;
import fr.poleemploi.estime.services.exceptions.BadRequestException;
import fr.poleemploi.estime.services.ressources.InformationsPersonnelles;
import fr.poleemploi.estime.services.ressources.Personne;
import fr.poleemploi.estime.services.ressources.SituationFamiliale;

@Component
public class SituationFamilialeControleur {

    public void controlerDonnees(SituationFamiliale situationFamiliale) {
        if(situationFamiliale == null) {
            throw new BadRequestException(String.format(BadRequestMessages.CHAMP_OBLIGATOIRE.getMessage(), "situationFamiliale"));
        } else {
            controlerEnCouple(situationFamiliale);
            controlerPersonnesACharges(situationFamiliale.getPersonnesACharge());            
        }
    }

    private void controlerEnCouple(SituationFamiliale situationFamiliale) {
        if(situationFamiliale.getIsEnCouple() == null) {
            throw new BadRequestException(String.format(BadRequestMessages.CHAMP_OBLIGATOIRE.getMessage(), "enCouple de situationFamiliale"));
        }
        if(situationFamiliale.getIsEnCouple().booleanValue() && situationFamiliale.getConjoint() == null) {
             throw new BadRequestException(String.format(BadRequestMessages.CHAMP_OBLIGATOIRE.getMessage(), "conjoint de situationFamiliale"));
        }
    }

    private void controlerPersonnesACharges(List<Personne> personnesACharge) {
        if(personnesACharge != null) {
            for (Personne personne : personnesACharge) {
                controlerInformationsPersonnelles(personne.getInformationsPersonnelles());
            }            
        }
    }

    private void controlerInformationsPersonnelles(InformationsPersonnelles informationsPersonnelles) {
        if(informationsPersonnelles == null) {
            throw new BadRequestException(String.format(BadRequestMessages.CHAMP_OBLIGATOIRE.getMessage(), "informationsPersonnelles"));
        } else {
            if(informationsPersonnelles.getDateNaissance() == null) {
                throw new BadRequestException(String.format(BadRequestMessages.CHAMP_OBLIGATOIRE.getMessage(), "dateNaissance de informationsPersonnelles"));
            }            
        }
    }
}
