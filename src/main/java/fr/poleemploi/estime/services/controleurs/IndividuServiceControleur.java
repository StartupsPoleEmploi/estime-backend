package fr.poleemploi.estime.services.controleurs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import fr.poleemploi.estime.commun.enumerations.exceptions.BadRequestMessages;
import fr.poleemploi.estime.services.controleurs.demandeuremploi.BeneficiaireAidesSocialesControleur;
import fr.poleemploi.estime.services.controleurs.demandeuremploi.FuturTravailControleur;
import fr.poleemploi.estime.services.controleurs.demandeuremploi.InformationsPersonnellesControleur;
import fr.poleemploi.estime.services.controleurs.demandeuremploi.SituationFamilialeControleur;
import fr.poleemploi.estime.services.exceptions.BadRequestException;
import fr.poleemploi.estime.services.ressources.DemandeurEmploi;
import fr.poleemploi.estime.services.ressources.Individu;
import fr.poleemploi.estime.services.ressources.PeConnectPayload;

@Component
public class IndividuServiceControleur {

    @Autowired
    private FuturTravailControleur futurTravailControleur;
    
    @Autowired
    private BeneficiaireAidesSocialesControleur beneficiaireAidesSocialesControleur;
    
    @Autowired
    private InformationsPersonnellesControleur informationsPersonnellesControleur;
    
    @Autowired
    private SituationFamilialeControleur situationFamilialeControleur;
    
    
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
    
    public void controlerDonneesEntreeServiceCreerDemandeurEmploi(Individu individu) {
        if (individu == null) {
            throw new BadRequestException(BadRequestMessages.INDIVIDU_OBLIGATOIRE.getMessage());
        } else {
            if(individu.getPeConnectAuthorization() == null 
             || (individu.getPeConnectAuthorization() != null && ObjectUtils.isEmpty(individu.getPeConnectAuthorization().getAccessToken().isEmpty()))) {
                throw new BadRequestException(String.format(BadRequestMessages.CHAMP_OBLIGATOIRE.getMessage(), "accessToken de peConnectAuthorization"));
            }
        }   
    }
    
    public void controlerDonneesEntreeServiceSimulerMesAidesSociales(DemandeurEmploi demandeurEmploi) {
        if (demandeurEmploi == null) {
            throw new BadRequestException(BadRequestMessages.DEMANDEUR_EMPLOI_OBLIGATOIRE.getMessage());
        } else {
            futurTravailControleur.controlerDonnees(demandeurEmploi.getFuturTravail());
            beneficiaireAidesSocialesControleur.controlerDonnees(demandeurEmploi);
            informationsPersonnellesControleur.controlerDonnees(demandeurEmploi.getInformationsPersonnelles(), demandeurEmploi.getBeneficiaireAidesSociales());
            situationFamilialeControleur.controlerDonnees(demandeurEmploi.getSituationFamiliale(), demandeurEmploi.getBeneficiaireAidesSociales());            
        }
    }

  
}
