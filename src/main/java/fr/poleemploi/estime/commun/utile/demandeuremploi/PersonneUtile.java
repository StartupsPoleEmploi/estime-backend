package fr.poleemploi.estime.commun.utile.demandeuremploi;

import java.time.LocalDate;
import java.time.Period;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fr.poleemploi.estime.services.ressources.Personne;

@Component
public class PersonneUtile {

    @Autowired
    private BeneficiairePrestationsSocialesUtile beneficiairePrestationsSocialesUtile;
    
    public int calculerAge(Personne personne) {
        return Period.between(personne.getInformationsPersonnelles().getDateNaissance(), LocalDate.now()).getYears();
    }

    public boolean hasAllocationRSA(Personne personne) {
        return personne.getRessourcesFinancieres() != null 
                && personne.getRessourcesFinancieres().getPrestationsCAF() != null 
                && personne.getRessourcesFinancieres().getPrestationsCAF().getAllocationMensuelleNetRSA() != null
                && personne.getRessourcesFinancieres().getPrestationsCAF().getAllocationMensuelleNetRSA() > 0;
    }
    
    public boolean hasAllocationARE(Personne personne) {
        return beneficiairePrestationsSocialesUtile.isBeneficiaireARE(personne)
               && personne.getRessourcesFinancieres() != null 
               && personne.getRessourcesFinancieres().getPrestationsPoleEmploi()!= null 
               && personne.getRessourcesFinancieres().getPrestationsPoleEmploi().getAllocationARE() != null
               && personne.getRessourcesFinancieres().getPrestationsPoleEmploi().getAllocationARE().getAllocationMensuelleNet() != null 
               && personne.getRessourcesFinancieres().getPrestationsPoleEmploi().getAllocationARE().getAllocationMensuelleNet() > 0;
    }
    
    public boolean hasAllocationASS(Personne personne) {
        return beneficiairePrestationsSocialesUtile.isBeneficiaireASS(personne)
               && personne.getRessourcesFinancieres() != null 
               && personne.getRessourcesFinancieres().getPrestationsPoleEmploi() != null 
               && personne.getRessourcesFinancieres().getPrestationsPoleEmploi().getAllocationASS() != null
               && personne.getRessourcesFinancieres().getPrestationsPoleEmploi().getAllocationASS().getAllocationMensuelleNet() != null 
               && personne.getRessourcesFinancieres().getPrestationsPoleEmploi().getAllocationASS().getAllocationMensuelleNet() > 0;
    }
}
