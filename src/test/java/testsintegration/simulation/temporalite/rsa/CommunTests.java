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

import fr.poleemploi.estime.clientsexternes.emploistoredev.EmploiStoreDevClient;
import fr.poleemploi.estime.clientsexternes.emploistoredev.ressources.DetailIndemnisationESD;
import fr.poleemploi.estime.commun.enumerations.TypePopulation;
import fr.poleemploi.estime.commun.utile.DateUtile;
import utile.tests.UtileDemandeurTests;
import utile.tests.UtileTests;

public class CommunTests {
    
    @Autowired
    protected UtileTests utileTests;

    @SpyBean
    private EmploiStoreDevClient detailIndemnisationPoleEmploiClient;

    @SpyBean
    protected DateUtile dateUtile;
    
    @Autowired
    protected UtileDemandeurTests demandeurBaseTests;
    
    
    protected void initMocks(String dateSimulation) throws ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException, JSONException {
        //mock création date de demande de simulation
        doReturn(utileTests.getDate(dateSimulation)).when(dateUtile).getDateJour();

        //mock retour appel détail indemnisation de l'ESD 
        DetailIndemnisationESD detailIndemnisationESD = demandeurBaseTests.creerDetailIndemnisationESD(TypePopulation.RSA.getLibelle());        
        doReturn(detailIndemnisationESD).when(detailIndemnisationPoleEmploiClient).callDetailIndemnisationEndPoint(Mockito.any(String.class)); 
    }
}
