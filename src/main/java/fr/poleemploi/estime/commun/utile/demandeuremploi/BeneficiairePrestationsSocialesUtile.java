package fr.poleemploi.estime.commun.utile.demandeuremploi;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fr.poleemploi.estime.services.ressources.DemandeurEmploi;
import fr.poleemploi.estime.services.ressources.Personne;

@Component
public class BeneficiairePrestationsSocialesUtile {
    
    public static final float ALLOCATION_CHOMAGE_MAX_ELIGIBILITE_AIDE = 29.38f;
    public static final float ALLOCATION_CHOMAGE_MAX_ELIGIBILITE_AIDE_MAYOTTE = 14.68f;
    
    @Autowired
    private InformationsPersonnellesUtile informationsPersonnellesUtile;
    
    public boolean isBeneficiairePrestationsSociales(DemandeurEmploi demandeurEmploi) {
        return demandeurEmploi.getBeneficiairePrestationsSociales() != null;
    }
    
    public boolean isBeneficiaireAidePEouCAF(DemandeurEmploi demandeurEmploi) {
        return isBeneficiaireAideMinimaSocial(demandeurEmploi) || 
               isBeneficiaireAREAvecMontantAREInferieurEgaleSeuilMaxEligibilite(demandeurEmploi);
    }
    
    public boolean isBeneficiaireARE(Personne personne) {
        return personne.getBeneficiairePrestationsSociales() != null  && personne.getBeneficiairePrestationsSociales().isBeneficiaireARE();
    }
    
    public boolean isBeneficiaireASS(DemandeurEmploi demandeurEmploi) {
        return isBeneficiaireAidePEouCAF(demandeurEmploi) && demandeurEmploi.getBeneficiairePrestationsSociales().isBeneficiaireASS();
    }
    
    public boolean isBeneficiaireASS(Personne personne) {
        return personne.getBeneficiairePrestationsSociales() != null  && personne.getBeneficiairePrestationsSociales().isBeneficiaireASS();
    }
      
    public boolean isBeneficiaireAAH(DemandeurEmploi demandeurEmploi) {
        return isBeneficiaireAidePEouCAF(demandeurEmploi) && demandeurEmploi.getBeneficiairePrestationsSociales().isBeneficiaireAAH();
    }
    
    public boolean isBeneficiairePensionInvalidite(DemandeurEmploi demandeurEmploi) {
        return demandeurEmploi.getBeneficiairePrestationsSociales().isBeneficiairePensionInvalidite();
    }

    public boolean isBeneficiaireAideMinimaSocial(DemandeurEmploi demandeurEmploi) {
        return isBeneficiairePrestationsSociales(demandeurEmploi) && (
               demandeurEmploi.getBeneficiairePrestationsSociales().isBeneficiaireAAH() ||
               demandeurEmploi.getBeneficiairePrestationsSociales().isBeneficiaireASS() ||
               demandeurEmploi.getBeneficiairePrestationsSociales().isBeneficiaireRSA());
    }
    
    private boolean isBeneficiaireAREAvecMontantAREInferieurEgaleSeuilMaxEligibilite(DemandeurEmploi demandeurEmploi) {
        
        if(isBeneficiairePrestationsSociales(demandeurEmploi) 
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
        return isBeneficiaireAidePEouCAF(demandeurEmploi) && demandeurEmploi.getBeneficiairePrestationsSociales().isBeneficiaireRSA();
    }
}
