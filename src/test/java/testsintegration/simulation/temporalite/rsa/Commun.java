package testsintegration.simulation.temporalite.rsa;

import static org.mockito.Mockito.doReturn;

import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.text.ParseException;

import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.test.mock.mockito.SpyBean;

import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import fr.poleemploi.estime.clientsexternes.poleemploiio.PoleEmploiIOClient;
import fr.poleemploi.estime.clientsexternes.poleemploiio.ressources.DetailIndemnisationPEIO;
import fr.poleemploi.estime.commun.enumerations.TypePopulation;
import fr.poleemploi.estime.commun.utile.DateUtile;
import fr.poleemploi.estime.services.ressources.Logement;
import fr.poleemploi.estime.services.ressources.StatutOccupationLogement;
import utile.tests.Utile;

public class Commun {
    
    @Autowired
    protected Utile utileTests;

    @SpyBean
    private PoleEmploiIOClient poleEmploiIOClient;

    @SpyBean
    protected DateUtile dateUtile;    
    
    
    protected void initMocks(String dateSimulation) throws ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException, JSONException {
        //mock création date de demande de simulation
        doReturn(utileTests.getDate(dateSimulation)).when(dateUtile).getDateJour();

        //mock retour appel détail indemnisation de l'ESD 
        DetailIndemnisationPEIO detailIndemnisationESD = utileTests.creerDetailIndemnisationESD(TypePopulation.RSA.getLibelle());        
        doReturn(detailIndemnisationESD).when(poleEmploiIOClient).callDetailIndemnisationEndPoint(Mockito.any(String.class)); 
    }

    protected Logement initLogement() {
        Logement logement = new Logement();
        StatutOccupationLogement statutOccupationLogement = new StatutOccupationLogement();
        statutOccupationLogement.setLocataireNonMeuble(true);
        logement.setStatutOccupationLogement(statutOccupationLogement);
        logement.setMontantCharges(50f);
        logement.setMontantLoyer(500f);
        logement.setCodeInsee("44109");
        logement.setDeMayotte(false);
        return logement;
    }
}
