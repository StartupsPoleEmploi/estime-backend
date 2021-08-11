package fr.poleemploi.estime.commun.utile.demandeuremploi;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fr.poleemploi.estime.services.ressources.DemandeurEmploi;
import fr.poleemploi.estime.services.ressources.Personne;

@Component
public class BeneficiaireAidesUtile {
    
    public static final float ALLOCATION_CHOMAGE_MAX_ELIGIBILITE_AIDE = 29.38f;
    public static final float ALLOCATION_CHOMAGE_MAX_ELIGIBILITE_AIDE_MAYOTTE = 14.68f;
    
    @Autowired
    private InformationsPersonnellesUtile informationsPersonnellesUtile;
    
    public boolean isBeneficiaireAides(DemandeurEmploi demandeurEmploi) {
        return demandeurEmploi.getBeneficiaireAides() != null;
    }
    
    public boolean isBeneficiaireAidePEouCAF(DemandeurEmploi demandeurEmploi) {
        return isBeneficiaireAideMinimaSocial(demandeurEmploi) || 
               isBeneficiaireAREAvecMontantAREInferieurEgaleSeuilMaxEligibilite(demandeurEmploi);
    }
    
    public boolean isBeneficiaireARE(Personne personne) {
        return personne.getBeneficiaireAides() != null  && personne.getBeneficiaireAides().isBeneficiaireARE();
    }
    
    public boolean isBeneficiaireASS(DemandeurEmploi demandeurEmploi) {
        return isBeneficiaireAidePEouCAF(demandeurEmploi) && demandeurEmploi.getBeneficiaireAides().isBeneficiaireASS();
    }
    
    public boolean isBeneficiaireASS(Personne personne) {
        return personne.getBeneficiaireAides() != null  && personne.getBeneficiaireAides().isBeneficiaireASS();
    }
      
    public boolean isBeneficiaireAAH(DemandeurEmploi demandeurEmploi) {
        return isBeneficiaireAidePEouCAF(demandeurEmploi) && demandeurEmploi.getBeneficiaireAides().isBeneficiaireAAH();
    }
    
    public boolean isBeneficiairePensionInvalidite(DemandeurEmploi demandeurEmploi) {
        return demandeurEmploi.getBeneficiaireAides().isBeneficiairePensionInvalidite();
    }

    public boolean isBeneficiaireAideMinimaSocial(DemandeurEmploi demandeurEmploi) {
        return isBeneficiaireAides(demandeurEmploi) && (
               demandeurEmploi.getBeneficiaireAides().isBeneficiaireAAH() ||
               demandeurEmploi.getBeneficiaireAides().isBeneficiaireASS() ||
               demandeurEmploi.getBeneficiaireAides().isBeneficiaireRSA());
    }
    
    private boolean isBeneficiaireAREAvecMontantAREInferieurEgaleSeuilMaxEligibilite(DemandeurEmploi demandeurEmploi) {
        
        if(isBeneficiaireAides(demandeurEmploi) 
           && demandeurEmploi.getRessourcesFinancieres() != null 
           && demandeurEmploi.getRessourcesFinancieres().getAidesPoleEmploi() != null
           && demandeurEmploi.getRessourcesFinancieres().getAidesPoleEmploi().getAllocationARE() != null) {
            
            float indemnisationJournaliereNet = demandeurEmploi.getRessourcesFinancieres().getAidesPoleEmploi().getAllocationARE().getAllocationJournaliereNet();
            return (!informationsPersonnellesUtile.isDeMayotte(demandeurEmploi) &&  indemnisationJournaliereNet <= ALLOCATION_CHOMAGE_MAX_ELIGIBILITE_AIDE) 
                    || (informationsPersonnellesUtile.isDeMayotte(demandeurEmploi) && indemnisationJournaliereNet <= ALLOCATION_CHOMAGE_MAX_ELIGIBILITE_AIDE_MAYOTTE);
        }
        return false;   
    }    
    
    public boolean isBeneficiaireRSA(DemandeurEmploi demandeurEmploi) {
        return isBeneficiaireAidePEouCAF(demandeurEmploi) && demandeurEmploi.getBeneficiaireAides().isBeneficiaireRSA();
    }
}
