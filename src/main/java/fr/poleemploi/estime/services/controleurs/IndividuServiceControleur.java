package fr.poleemploi.estime.services.controleurs;

import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import fr.poleemploi.estime.commun.enumerations.exceptions.BadRequestMessages;
import fr.poleemploi.estime.services.exceptions.BadRequestException;
import fr.poleemploi.estime.services.ressources.PeConnectPayload;

@Component
public class IndividuServiceControleur {   
    
    public void controlerDonneesEntreeServiceAuthentifier(PeConnectPayload peConnectPayload) {
        if (peConnectPayload == null) {
            throw new BadRequestException(BadRequestMessages.INDIVIDU_OBLIGATOIRE.getMessage());
        } else {
            if (ObjectUtils.isEmpty(peConnectPayload.getCode())) {
                throw new BadRequestException(String.format(BadRequestMessages.CHAMP_OBLIGATOIRE.getMessage(), "code de peConnectPayload"));
            }
            if (ObjectUtils.isEmpty(peConnectPayload.getRedirectURI())) {
                throw new BadRequestException(String.format(BadRequestMessages.CHAMP_OBLIGATOIRE.getMessage(), "redirectURI de peConnectPayload"));
            }
            if (ObjectUtils.isEmpty(peConnectPayload.getNonce())) {
                throw new BadRequestException(String.format(BadRequestMessages.CHAMP_OBLIGATOIRE.getMessage(), "nonce de peConnectPayload"));
            } 
        }   
    }
}
