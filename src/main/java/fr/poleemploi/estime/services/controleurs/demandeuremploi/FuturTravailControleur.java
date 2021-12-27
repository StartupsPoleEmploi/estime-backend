package fr.poleemploi.estime.services.controleurs.demandeuremploi;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import fr.poleemploi.estime.commun.enumerations.TypeContratTravailEnum;
import fr.poleemploi.estime.commun.enumerations.exceptions.BadRequestMessages;
import fr.poleemploi.estime.commun.utile.demandeuremploi.FuturTravailUtile;
import fr.poleemploi.estime.commun.utile.demandeuremploi.TypeContratUtile;
import fr.poleemploi.estime.services.exceptions.BadRequestException;
import fr.poleemploi.estime.services.ressources.FuturTravail;

@Component
public class FuturTravailControleur {
    
    @Autowired
    private FuturTravailUtile futurTravailUtile;
    
    @Autowired
    private TypeContratUtile typeContratUtile;
    
    public void controlerDonnees(FuturTravail futurTravail) {
        if(futurTravail == null) {
            throw new BadRequestException(String.format(BadRequestMessages.CHAMP_OBLIGATOIRE.getMessage(), "futurTravail"));
        } else {
            controlerTypeDeContrat(futurTravail);
            
            if(futurTravailUtile.hasContratCDD(futurTravail)) {
                controlerFuturTravailCDD(futurTravail);
            }
            
            if(futurTravail.getNombreHeuresTravailleesSemaine() == 0) {
                throw new BadRequestException(BadRequestMessages.NOMBRE_HEURE_TRAVAILLEES_ZERO.getMessage());
            }
            
            if(futurTravail.getSalaire() == null) {
                throw new BadRequestException(String.format(BadRequestMessages.CHAMP_OBLIGATOIRE.getMessage(), "salaire de futurTravail"));
            }
            
            if(futurTravail.getSalaire().getMontantNet() == 0) {
                throw new BadRequestException(BadRequestMessages.SALAIRE_MENSUEL_NET_ZERO.getMessage());
            }
            
            if(futurTravail.getSalaire().getMontantBrut()  == 0) {
                throw new BadRequestException(BadRequestMessages.SALAIRE_MENSUEL_BRUT_ZERO.getMessage());
            }
        }
    }
    
    private void controlerTypeDeContrat(FuturTravail futurTravail) {
        if(ObjectUtils.isEmpty(futurTravail.getTypeContrat())) {
            throw new BadRequestException(String.format(BadRequestMessages.CHAMP_OBLIGATOIRE.getMessage(), "typeContrat de futurTravail"));
        }
        isTypeContratCorrect(futurTravail.getTypeContrat());
    }

    private void controlerFuturTravailCDD(FuturTravail futurTravail) {
        if(futurTravail.getNombreMoisContratCDD()== null) {
            throw new BadRequestException(String.format(BadRequestMessages.CHAMP_OBLIGATOIRE.getMessage(), "nombreMoisContratCDD de futurTravail"));
        }
    }

    private void isTypeContratCorrect(String typeContratToCheck) {
        boolean typeContratCorrect = Arrays.asList(TypeContratTravailEnum.values()).stream().anyMatch(typeContrat -> typeContrat.name().equals(typeContratToCheck));
        if(!typeContratCorrect) {
            throw new BadRequestException(String.format(BadRequestMessages.TYPE_CONTRAT_INCORRECT.getMessage(), typeContratUtile.getListeFormateeTypesContratPossibles()));            
        }
    }
}
