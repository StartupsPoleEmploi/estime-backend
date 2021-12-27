package testsunitaires.services.controleurs.demandeuremploi;

import java.time.LocalDate;

import fr.poleemploi.estime.commun.enumerations.NationaliteEnum;
import fr.poleemploi.estime.commun.enumerations.TypeContratTravailEnum;
import fr.poleemploi.estime.services.ressources.FuturTravail;
import fr.poleemploi.estime.services.ressources.InformationsPersonnelles;
import fr.poleemploi.estime.services.ressources.Personne;
import fr.poleemploi.estime.services.ressources.Salaire;
import fr.poleemploi.estime.services.ressources.SituationFamiliale;

public class Commun {
    
    protected SituationFamiliale creerSituationFamiliale() {
        SituationFamiliale situationFamiliale = new SituationFamiliale();
        situationFamiliale.setIsEnCouple(true);
        Personne personne = new Personne();
        InformationsPersonnelles informationsPersonnelles = new InformationsPersonnelles();
        informationsPersonnelles.setDateNaissance(LocalDate.now());
        personne.setInformationsPersonnelles(informationsPersonnelles);
        situationFamiliale.setConjoint(personne);
        return situationFamiliale;
    }
    
    protected InformationsPersonnelles creerInformationsPersonnelles() {
        InformationsPersonnelles informationsPersonnelles = new InformationsPersonnelles();
        informationsPersonnelles.setDateNaissance(LocalDate.now());
        informationsPersonnelles.setNationalite(NationaliteEnum.FRANCAISE.getValeur());
        informationsPersonnelles.setCodePostal("44200");
        return informationsPersonnelles;
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
