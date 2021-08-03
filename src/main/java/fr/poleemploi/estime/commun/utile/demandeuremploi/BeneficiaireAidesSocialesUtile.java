package fr.poleemploi.estime.commun.utile.demandeuremploi;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fr.poleemploi.estime.services.ressources.DemandeurEmploi;
import fr.poleemploi.estime.services.ressources.Personne;

@Component
public class BeneficiaireAidesSocialesUtile {
    
    public static final float ALLOCATION_CHOMAGE_MAX_ELIGIBILITE_AIDE = 29.38f;
    public static final float ALLOCATION_CHOMAGE_MAX_ELIGIBILITE_AIDE_MAYOTTE = 14.68f;
    
    @Autowired
    private InformationsPersonnellesUtile informationsPersonnellesUtile;
    
    public boolean isBeneficiaireAidesSociales(DemandeurEmploi demandeurEmploi) {
        return demandeurEmploi.getBeneficiaireAidesSociales() != null;
    }
    
    public boolean isBeneficiaireAidePEouCAF(DemandeurEmploi demandeurEmploi) {
        return isBeneficiaireAideMinimaSocial(demandeurEmploi) || 
               isBeneficiaireAREAvecMontantAREInferieurEgaleSeuilMaxEligibilite(demandeurEmploi);
    }
    
    public boolean isBeneficiaireARE(Personne personne) {
        return personne.getBeneficiaireAidesSociales() != null  && personne.getBeneficiaireAidesSociales().isBeneficiaireARE();
    }
    
    public boolean isBeneficiaireASS(DemandeurEmploi demandeurEmploi) {
        return isBeneficiaireAidePEouCAF(demandeurEmploi) && demandeurEmploi.getBeneficiaireAidesSociales().isBeneficiaireASS();
    }
    
    public boolean isBeneficiaireASS(Personne personne) {
        return personne.getBeneficiaireAidesSociales() != null  && personne.getBeneficiaireAidesSociales().isBeneficiaireASS();
    }
      
    public boolean isBeneficiaireAAH(DemandeurEmploi demandeurEmploi) {
        return isBeneficiaireAidePEouCAF(demandeurEmploi) && demandeurEmploi.getBeneficiaireAidesSociales().isBeneficiaireAAH();
    }
    
    public boolean isBeneficiairePensionInvalidite(DemandeurEmploi demandeurEmploi) {
        return demandeurEmploi.getBeneficiaireAidesSociales().isBeneficiairePensionInvalidite();
    }

    public boolean isBeneficiaireAideMinimaSocial(DemandeurEmploi demandeurEmploi) {
        return isBeneficiaireAidesSociales(demandeurEmploi) && (
               demandeurEmploi.getBeneficiaireAidesSociales().isBeneficiaireAAH() ||
               demandeurEmploi.getBeneficiaireAidesSociales().isBeneficiaireASS() ||
               demandeurEmploi.getBeneficiaireAidesSociales().isBeneficiaireRSA());
    }
    
    private boolean isBeneficiaireAREAvecMontantAREInferieurEgaleSeuilMaxEligibilite(DemandeurEmploi demandeurEmploi) {
        
        if(isBeneficiaireAidesSociales(demandeurEmploi) 
           && demandeurEmploi.getRessourcesFinancieres() != null 
           && demandeurEmploi.getRessourcesFinancieres().getPrestationsPoleEmploi() != null
           && demandeurEmploi.getRessourcesFinancieres().getPrestationsPoleEmploi().getAllocationARE() != null) {
            
            float indemnisationJournaliereNet = demandeurEmploi.getRessourcesFinancieres().getPrestationsPoleEmploi().getAllocationARE().getAllocationJournaliereNet();
            return (!informationsPersonnellesUtile.isDeMayotte(demandeurEmploi) &&  indemnisationJournaliereNet <= ALLOCATION_CHOMAGE_MAX_ELIGIBILITE_AIDE) 
                    || (informationsPersonnellesUtile.isDeMayotte(demandeurEmploi) && indemnisationJournaliereNet <= ALLOCATION_CHOMAGE_MAX_ELIGIBILITE_AIDE_MAYOTTE);
        }
        return false;   
    }    
    
    public boolean isBeneficiaireRSA(DemandeurEmploi demandeurEmploi) {
        return isBeneficiaireAidePEouCAF(demandeurEmploi) && demandeurEmploi.getBeneficiaireAidesSociales().isBeneficiaireRSA();
    }
}
