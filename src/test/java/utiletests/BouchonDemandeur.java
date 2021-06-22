package utiletests;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;

import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.stereotype.Component;

import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import fr.poleemploi.estime.clientsexternes.emploistoredev.EmploiStoreDevClient;
import fr.poleemploi.estime.clientsexternes.emploistoredev.ressources.DetailIndemnisationESD;
import fr.poleemploi.estime.commun.enumerations.Nationalites;
import fr.poleemploi.estime.commun.enumerations.ParcoursUtilisateur;
import fr.poleemploi.estime.commun.enumerations.TypesContratTravail;
import fr.poleemploi.estime.commun.utile.DateUtile;
import fr.poleemploi.estime.commun.utile.SuiviUtilisateurUtile;
import fr.poleemploi.estime.services.ressources.AllocationsCAF;
import fr.poleemploi.estime.services.ressources.BeneficiaireAidesSociales;
import fr.poleemploi.estime.services.ressources.DemandeurEmploi;
import fr.poleemploi.estime.services.ressources.FuturTravail;
import fr.poleemploi.estime.services.ressources.InformationsPersonnelles;
import fr.poleemploi.estime.services.ressources.Personne;
import fr.poleemploi.estime.services.ressources.RessourcesFinancieres;
import fr.poleemploi.estime.services.ressources.Salaire;
import fr.poleemploi.estime.services.ressources.SituationFamiliale;

@Component
public class BouchonDemandeur {
    
    @SpyBean
    private SuiviUtilisateurUtile suiviUtilisateurUtile;
    
    @Autowired
    private TestUtile testUtile;
    
    
    
    @SpyBean
    private DateUtile dateUtile;   

    public SituationFamiliale creerSituationFamiliale() {
        SituationFamiliale situationFamiliale = new SituationFamiliale();
        situationFamiliale.setIsEnCouple(true);
        Personne personne = new Personne();
        InformationsPersonnelles informationsPersonnelles = new InformationsPersonnelles();
        informationsPersonnelles.setDateNaissance(LocalDate.now());
        personne.setInformationsPersonnelles(informationsPersonnelles);
        situationFamiliale.setConjoint(personne);
        return situationFamiliale;
    }
    
    public InformationsPersonnelles creerInformationsPersonnelles() {
        InformationsPersonnelles informationsPersonnelles = new InformationsPersonnelles();
        informationsPersonnelles.setDateNaissance(LocalDate.now());
        informationsPersonnelles.setNationalite(Nationalites.FRANCAISE.getValeur());
        informationsPersonnelles.setCodePostal("44200");
        return informationsPersonnelles;
    }
    
    public FuturTravail creerFuturTravail() {
        FuturTravail futurTravail = new FuturTravail();
        futurTravail.setNombreMoisContratCDD(6);
        futurTravail.setDistanceKmDomicileTravail(50);
        futurTravail.setNombreHeuresTravailleesSemaine(15);
        Salaire salaire = new Salaire();
        salaire.setMontantNet(900);
        salaire.setMontantBrut(1200);
        futurTravail.setSalaire(salaire);        
        futurTravail.setTypeContrat(TypesContratTravail.CDD.name());
        return futurTravail;
    }
}
