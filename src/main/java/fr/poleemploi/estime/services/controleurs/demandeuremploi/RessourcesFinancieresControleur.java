package fr.poleemploi.estime.services.controleurs.demandeuremploi;

import org.springframework.stereotype.Component;

import fr.poleemploi.estime.commun.enumerations.exceptions.BadRequestMessages;
import fr.poleemploi.estime.services.exceptions.BadRequestException;
import fr.poleemploi.estime.services.ressources.DemandeurEmploi;
import fr.poleemploi.estime.services.ressources.PrestationsCAF;
import fr.poleemploi.estime.services.ressources.PrestationsPoleEmploi;
import fr.poleemploi.estime.services.ressources.RessourcesFinancieres;

@Component
public class RessourcesFinancieresControleur {
    
    private static final String MESSAGE_RESSOURCES_FINANCIERE_OBLIGATOIRE =  "ressourcesFinancieres dans DemandeurEmploi";


    public void controlerDemandeurEmploiAllocationsPoleEmploiASS(RessourcesFinancieres ressourcesFinancieres) {
        if(ressourcesFinancieres == null) {
            throw new BadRequestException(String.format(BadRequestMessages.CHAMP_OBLIGATOIRE.getMessage(), MESSAGE_RESSOURCES_FINANCIERE_OBLIGATOIRE));
        }
        PrestationsPoleEmploi prestationsPoleEmploi = ressourcesFinancieres.getPrestationsPoleEmploi();
        if(prestationsPoleEmploi == null) {
            throw new BadRequestException(String.format(BadRequestMessages.CHAMP_OBLIGATOIRE.getMessage(), "prestationsPoleEmploi dans RessourcesFinancieres de DemandeurEmploi"));
        }
        if(prestationsPoleEmploi.getAllocationASS() != null) {
            if(prestationsPoleEmploi.getAllocationASS().getAllocationJournaliereNet() <= 0) {
                throw new BadRequestException(String.format(BadRequestMessages.MONTANT_INCORRECT_INFERIEUR_EGAL_ZERO.getMessage(), "allocationJournaliereNetASS"));
            }
            if(prestationsPoleEmploi.getAllocationASS().getDateDerniereOuvertureDroitASS() == null) {
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
        PrestationsCAF prestationsCAF = ressourcesFinancieres.getPrestationsCAF();
        if(prestationsCAF == null) {
            throw new BadRequestException(String.format(BadRequestMessages.CHAMP_OBLIGATOIRE.getMessage(), "allocationsCAF dans RessourcesFinancieres de DemandeurEmploi"));
        }
        if(prestationsCAF.getAllocationMensuelleNetAAH() != null && prestationsCAF.getAllocationMensuelleNetAAH() <= 0) {
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
        PrestationsCAF prestationsCAF = ressourcesFinancieres.getPrestationsCAF();
        if(prestationsCAF == null) {
            throw new BadRequestException(String.format(BadRequestMessages.CHAMP_OBLIGATOIRE.getMessage(), "allocationsCAF dans RessourcesFinancieres de DemandeurEmploi"));
        }
        if(prestationsCAF.getAllocationMensuelleNetRSA() <= 0) {
            throw new BadRequestException(String.format(BadRequestMessages.MONTANT_INCORRECT_INFERIEUR_EGAL_ZERO.getMessage(), "allocationMensuelleNetRSA"));
        }
        if(prestationsCAF.getProchaineDeclarationRSA() == null) {
            throw new BadRequestException(String.format(BadRequestMessages.CHAMP_OBLIGATOIRE.getMessage(), "prochaineDeclarationRSA dans allocationsCAF de RessourcesFinancieres de DemandeurEmploi"));
        }
        if(prestationsCAF.getProchaineDeclarationRSA() < 0 || prestationsCAF.getProchaineDeclarationRSA() >= 4) { 
            throw new BadRequestException(String.format(BadRequestMessages.VALEUR_INCORRECT_PROCHAINE_DECLARATION_RSA.getMessage(), prestationsCAF.getProchaineDeclarationRSA()));
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
