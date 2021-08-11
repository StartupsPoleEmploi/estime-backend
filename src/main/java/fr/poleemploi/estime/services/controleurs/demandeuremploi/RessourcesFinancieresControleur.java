package fr.poleemploi.estime.services.controleurs.demandeuremploi;

import org.springframework.stereotype.Component;

import fr.poleemploi.estime.commun.enumerations.exceptions.BadRequestMessages;
import fr.poleemploi.estime.services.exceptions.BadRequestException;
import fr.poleemploi.estime.services.ressources.AidesCAF;
import fr.poleemploi.estime.services.ressources.AidesPoleEmploi;
import fr.poleemploi.estime.services.ressources.DemandeurEmploi;
import fr.poleemploi.estime.services.ressources.RessourcesFinancieres;

@Component
public class RessourcesFinancieresControleur {
    
    private static final String MESSAGE_RESSOURCES_FINANCIERE_OBLIGATOIRE =  "ressourcesFinancieres dans DemandeurEmploi";


    public void controlerDemandeurEmploiAllocationsPoleEmploiASS(RessourcesFinancieres ressourcesFinancieres) {
        if(ressourcesFinancieres == null) {
            throw new BadRequestException(String.format(BadRequestMessages.CHAMP_OBLIGATOIRE.getMessage(), MESSAGE_RESSOURCES_FINANCIERE_OBLIGATOIRE));
        }
        AidesPoleEmploi aidesPoleEmploi = ressourcesFinancieres.getAidesPoleEmploi();
        if(aidesPoleEmploi == null) {
            throw new BadRequestException(String.format(BadRequestMessages.CHAMP_OBLIGATOIRE.getMessage(), "aidesPoleEmploi dans RessourcesFinancieres de DemandeurEmploi"));
        }
        if(aidesPoleEmploi.getAllocationASS() != null) {
            if(aidesPoleEmploi.getAllocationASS().getAllocationJournaliereNet() <= 0) {
                throw new BadRequestException(String.format(BadRequestMessages.MONTANT_INCORRECT_INFERIEUR_EGAL_ZERO.getMessage(), "allocationJournaliereNetASS"));
            }
            if(aidesPoleEmploi.getAllocationASS().getDateDerniereOuvertureDroit() == null) {
                throw new BadRequestException(String.format(BadRequestMessages.CHAMP_OBLIGATOIRE.getMessage(), "dateDerniereOuvertureDroitASS dans AllocationsPoleEmploi dans RessourcesFinancieres de DemandeurEmploi"));
            }
        }
        if(ressourcesFinancieres.getHasTravailleAuCoursDerniersMois() == null) {
            throw new BadRequestException(String.format(BadRequestMessages.CHAMP_OBLIGATOIRE.getMessage(), "hasTravailleAuCoursDerniersMois dans RessourcesFinancieres"));
        }
        if(ressourcesFinancieres.getHasTravailleAuCoursDerniersMois() != null && ressourcesFinancieres.getHasTravailleAuCoursDerniersMois().booleanValue()) {
            controlerNombreMoisCumulesAssEtSalaire(ressourcesFinancieres);            
        }
    }

    public void controlerDemandeurEmploiAllocationsCafAAH(RessourcesFinancieres ressourcesFinancieres) {
        if(ressourcesFinancieres == null) {
            throw new BadRequestException(String.format(BadRequestMessages.CHAMP_OBLIGATOIRE.getMessage(), MESSAGE_RESSOURCES_FINANCIERE_OBLIGATOIRE));
        }
        AidesCAF aidesCAF = ressourcesFinancieres.getAidesCAF();
        if(aidesCAF == null) {
            throw new BadRequestException(String.format(BadRequestMessages.CHAMP_OBLIGATOIRE.getMessage(), "allocationsCAF dans RessourcesFinancieres de DemandeurEmploi"));
        }
        if(aidesCAF.getAllocationAAH() != null && aidesCAF.getAllocationAAH() <= 0) {
            throw new BadRequestException(String.format(BadRequestMessages.MONTANT_INCORRECT_INFERIEUR_EGAL_ZERO.getMessage(), "allocationMensuelleNetAAH"));
        }        
        if(ressourcesFinancieres.getNombreMoisTravaillesDerniersMois() == null) { 
            throw new BadRequestException(String.format(BadRequestMessages.CHAMP_OBLIGATOIRE.getMessage(), "nombreMoisTravaillesDerniersMois dans RessourcesFinancieres"));
        }
        if(ressourcesFinancieres.getNombreMoisTravaillesDerniersMois() > 0 && ressourcesFinancieres.getSalairesAvantPeriodeSimulation() == null) {
            throw new BadRequestException(String.format(BadRequestMessages.CHAMP_OBLIGATOIRE.getMessage(), "salairesAvantPeriodeSimulation dans RessourcesFinancieres de DemandeurEmploi"));    
        }
    }
    
    public void controlerDemandeurEmploiAllocationsCafRSA(DemandeurEmploi demandeurEmploi) {
        RessourcesFinancieres ressourcesFinancieres = demandeurEmploi.getRessourcesFinancieres();
        if(ressourcesFinancieres == null) {
            throw new BadRequestException(String.format(BadRequestMessages.CHAMP_OBLIGATOIRE.getMessage(), MESSAGE_RESSOURCES_FINANCIERE_OBLIGATOIRE));
        }
        AidesCAF aidesCAF = ressourcesFinancieres.getAidesCAF();
        if(aidesCAF == null) {
            throw new BadRequestException(String.format(BadRequestMessages.CHAMP_OBLIGATOIRE.getMessage(), "allocationsCAF dans RessourcesFinancieres de DemandeurEmploi"));
        }
        if(aidesCAF.getAllocationRSA() <= 0) {
            throw new BadRequestException(String.format(BadRequestMessages.MONTANT_INCORRECT_INFERIEUR_EGAL_ZERO.getMessage(), "allocationMensuelleNetRSA"));
        }
        if(aidesCAF.getProchaineDeclarationRSA() == null) {
            throw new BadRequestException(String.format(BadRequestMessages.CHAMP_OBLIGATOIRE.getMessage(), "prochaineDeclarationRSA dans allocationsCAF de RessourcesFinancieres de DemandeurEmploi"));
        }
        if(aidesCAF.getProchaineDeclarationRSA() < 0 || aidesCAF.getProchaineDeclarationRSA() >= 4) { 
            throw new BadRequestException(String.format(BadRequestMessages.VALEUR_INCORRECT_PROCHAINE_DECLARATION_RSA.getMessage(), aidesCAF.getProchaineDeclarationRSA()));
        }
    }

    private void controlerNombreMoisCumulesAssEtSalaire(RessourcesFinancieres ressourcesFinancieres) {
        if(ressourcesFinancieres.getNombreMoisTravaillesDerniersMois() == null) { 
            throw new BadRequestException(String.format(BadRequestMessages.CHAMP_OBLIGATOIRE.getMessage(), "nombreMoisTravaillesDerniersMois dans RessourcesFinancieres de DemandeurEmploi"));
        }
        if(ressourcesFinancieres.getNombreMoisTravaillesDerniersMois() == 2 || ressourcesFinancieres.getNombreMoisTravaillesDerniersMois() == 3) {
            controlerSalairesAvantPeriodeSimulationPopulationASS(ressourcesFinancieres);
        }
    }
    
    private void controlerSalairesAvantPeriodeSimulationPopulationASS(RessourcesFinancieres ressourcesFinancieres) {
        if(ressourcesFinancieres.getSalairesAvantPeriodeSimulation() == null) {
            throw new BadRequestException(String.format(BadRequestMessages.CHAMP_OBLIGATOIRE.getMessage(), "salairesAvantPeriodeSimulation dans RessourcesFinancieres de DemandeurEmploi"));
        }
    }
}
