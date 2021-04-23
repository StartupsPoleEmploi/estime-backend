package fr.poleemploi.estime.commun.utile.demandeuremploi;

import java.time.LocalDate;
import java.time.Period;

import org.springframework.stereotype.Component;

import fr.poleemploi.estime.services.ressources.Personne;

@Component
public class PersonneUtile {

    public int calculerAge(Personne personne) {
        return Period.between(personne.getInformationsPersonnelles().getDateNaissance(), LocalDate.now()).getYears();
    }

    public boolean hasAllocationRSA(Personne personne) {
        return personne.getRessourcesFinancieres() != null 
                && personne.getRessourcesFinancieres().getAllocationsCAF() != null 
                && personne.getRessourcesFinancieres().getAllocationsCAF().getAllocationMensuelleNetRSA() != null
                && personne.getRessourcesFinancieres().getAllocationsCAF().getAllocationMensuelleNetRSA() > 0;
    }

}
