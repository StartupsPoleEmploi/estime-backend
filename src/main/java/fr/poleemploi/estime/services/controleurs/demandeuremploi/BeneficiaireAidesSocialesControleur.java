package fr.poleemploi.estime.services.controleurs.demandeuremploi;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fr.poleemploi.estime.commun.enumerations.exceptions.BadRequestMessages;
import fr.poleemploi.estime.services.exceptions.BadRequestException;
import fr.poleemploi.estime.services.ressources.BeneficiaireAidesSociales;
import fr.poleemploi.estime.services.ressources.DemandeurEmploi;

@Component
public class BeneficiaireAidesSocialesControleur {

    @Autowired
    private RessourcesFinancieresControleur ressourcesFinancieresControleur;
    
    public void controlerDonnees(DemandeurEmploi demandeurEmploi) {
        BeneficiaireAidesSociales beneficiaireAidesSociales = demandeurEmploi.getBeneficiaireAidesSociales();
        if(beneficiaireAidesSociales == null) {
            throw new BadRequestException(String.format(BadRequestMessages.CHAMP_OBLIGATOIRE.getMessage(), "beneficiaireAidesSociales"));
        }
        if(beneficiaireAidesSociales.isBeneficiaireAAH()) {
            ressourcesFinancieresControleur.controlerDemandeurEmploiAllocationsCafAAH(demandeurEmploi.getRessourcesFinancieres());
        }
        if(beneficiaireAidesSociales.isBeneficiaireRSA()) {
            ressourcesFinancieresControleur.controlerDemandeurEmploiAllocationsCafRSA(demandeurEmploi);
        }
        if(beneficiaireAidesSociales.isBeneficiaireASS()) {
            ressourcesFinancieresControleur.controlerDemandeurEmploiAllocationsPoleEmploiASS(demandeurEmploi.getRessourcesFinancieres());
        }
    }
}
