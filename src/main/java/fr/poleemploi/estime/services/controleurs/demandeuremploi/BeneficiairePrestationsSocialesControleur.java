package fr.poleemploi.estime.services.controleurs.demandeuremploi;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fr.poleemploi.estime.commun.enumerations.exceptions.BadRequestMessages;
import fr.poleemploi.estime.services.exceptions.BadRequestException;
import fr.poleemploi.estime.services.ressources.BeneficiairePrestationsSociales;
import fr.poleemploi.estime.services.ressources.DemandeurEmploi;

@Component
public class BeneficiairePrestationsSocialesControleur {

    @Autowired
    private RessourcesFinancieresControleur ressourcesFinancieresControleur;
    
    public void controlerDonnees(DemandeurEmploi demandeurEmploi) {
        BeneficiairePrestationsSociales beneficiairePrestationsSociales = demandeurEmploi.getBeneficiairePrestationsSociales();
        if(beneficiairePrestationsSociales == null) {
            throw new BadRequestException(String.format(BadRequestMessages.CHAMP_OBLIGATOIRE.getMessage(), "beneficiairePrestationsSociales"));
        }
        if(beneficiairePrestationsSociales.isBeneficiaireAAH()) {
            ressourcesFinancieresControleur.controlerDemandeurEmploiAllocationsCafAAH(demandeurEmploi.getRessourcesFinancieres());
        }
        if(beneficiairePrestationsSociales.isBeneficiaireRSA()) {
            ressourcesFinancieresControleur.controlerDemandeurEmploiAllocationsCafRSA(demandeurEmploi);
        }
        if(beneficiairePrestationsSociales.isBeneficiaireASS()) {
            ressourcesFinancieresControleur.controlerDemandeurEmploiAllocationsPoleEmploiASS(demandeurEmploi.getRessourcesFinancieres());
        }
    }
}
