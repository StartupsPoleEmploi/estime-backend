package fr.poleemploi.estime.commun.utile;

import java.time.Period;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fr.poleemploi.estime.services.ressources.Personne;

@Component
public class AgeUtile {

    @Autowired
    private DateUtile dateUtile;

    public int calculerAge(Personne personne) {
	return Period.between(personne.getInformationsPersonnelles().getDateNaissance(), dateUtile.getDateJour()).getYears();
    }

    public int calculerAgeMois(Personne personne) {
	return dateUtile.getNbrMoisEntreDeuxLocalDates(personne.getInformationsPersonnelles().getDateNaissance(), dateUtile.getDateJour());
    }

    public int calculerAgeMoisMoisSimule(Personne personne, int numeroMoisSimule) {
	return dateUtile.getNbrMoisEntreDeuxLocalDates(personne.getInformationsPersonnelles().getDateNaissance(), dateUtile.getDateJour().plusMonths(numeroMoisSimule));
    }
}