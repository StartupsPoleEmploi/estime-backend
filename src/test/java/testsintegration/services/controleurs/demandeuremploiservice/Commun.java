package testsintegration.services.controleurs.demandeuremploiservice;

import org.springframework.boot.test.mock.mockito.SpyBean;

import fr.poleemploi.estime.commun.enumerations.NationaliteEnum;
import fr.poleemploi.estime.commun.enumerations.TypeContratTravailEnum;
import fr.poleemploi.estime.commun.utile.DateUtile;
import fr.poleemploi.estime.services.ressources.Coordonnees;
import fr.poleemploi.estime.services.ressources.FuturTravail;
import fr.poleemploi.estime.services.ressources.InformationsPersonnelles;
import fr.poleemploi.estime.services.ressources.Logement;
import fr.poleemploi.estime.services.ressources.Personne;
import fr.poleemploi.estime.services.ressources.Salaire;
import fr.poleemploi.estime.services.ressources.SituationFamiliale;
import fr.poleemploi.estime.services.ressources.StatutOccupationLogement;

public class Commun {

    @SpyBean
    protected DateUtile dateUtile;

    protected SituationFamiliale creerSituationFamiliale() {
	SituationFamiliale situationFamiliale = new SituationFamiliale();
	situationFamiliale.setIsEnCouple(true);
	Personne personne = new Personne();
	InformationsPersonnelles informationsPersonnelles = new InformationsPersonnelles();
	informationsPersonnelles.setDateNaissance(dateUtile.getDateJour());
	personne.setInformationsPersonnelles(informationsPersonnelles);
	situationFamiliale.setConjoint(personne);
	return situationFamiliale;
    }

    protected InformationsPersonnelles creerInformationsPersonnelles() {
	InformationsPersonnelles informationsPersonnelles = new InformationsPersonnelles();
	informationsPersonnelles.setDateNaissance(dateUtile.getDateJour());
	informationsPersonnelles.setNationalite(NationaliteEnum.FRANCAISE.getValeur());
	informationsPersonnelles.setLogement(creerLogement());
	return informationsPersonnelles;
    }

    protected Logement creerLogement() {
	Logement logement = new Logement();
	logement.setCoordonnees(creerCoordonnees());
	logement.setStatutOccupationLogement(new StatutOccupationLogement());
	return logement;
    }

    protected Coordonnees creerCoordonnees() {
	Coordonnees coordonnees = new Coordonnees();
	coordonnees.setCodePostal("44200");
	return coordonnees;
    }

    protected FuturTravail creerFuturTravail() {
	FuturTravail futurTravail = new FuturTravail();
	futurTravail.setNombreMoisContratCDD(6);
	futurTravail.setDistanceKmDomicileTravail(50);
	futurTravail.setNombreHeuresTravailleesSemaine(15);
	Salaire salaire = new Salaire();
	salaire.setMontantNet(900);
	salaire.setMontantBrut(1200);
	futurTravail.setSalaire(salaire);
	futurTravail.setTypeContrat(TypeContratTravailEnum.CDD.name());
	return futurTravail;
    }
}
