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
import fr.poleemploi.estime.clientsexternes.poleemploiio.ressources.DetailIndemnisationPEIOOut;
import fr.poleemploi.estime.commun.enumerations.StatutOccupationLogementEnum;
import fr.poleemploi.estime.commun.enumerations.TypePopulationEnum;
import fr.poleemploi.estime.commun.utile.DateUtile;
import fr.poleemploi.estime.services.ressources.Coordonnees;
import fr.poleemploi.estime.services.ressources.Logement;
import utile.tests.Utile;

public class Commun {

    @Autowired
    protected Utile utileTests;

    @SpyBean
    private PoleEmploiIOClient poleEmploiIOClient;

    @SpyBean
    protected DateUtile dateUtile;

    protected void initMocks() throws ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException, JSONException {
	//mock création date de demande de simulation
	doReturn(utileTests.getDate("01-01-2022")).when(dateUtile).getDateJour();

	//mock retour appel détail indemnisation de l'ESD 
	DetailIndemnisationPEIOOut detailIndemnisationESD = utileTests.creerDetailIndemnisationPEIO(TypePopulationEnum.RSA);
	doReturn(detailIndemnisationESD).when(poleEmploiIOClient).getDetailIndemnisation(Mockito.any(String.class));
    }

    protected Logement initLogement() {
	Logement logement = new Logement();
	logement.setStatutOccupationLogement(StatutOccupationLogementEnum.LOCATAIRE_NON_MEUBLE.getLibelle());
	logement.setMontantLoyer(500f);
	logement.setCoordonnees(createCoordonnees());
	return logement;
    }

    protected Coordonnees createCoordonnees() {
	Coordonnees coordonnees = new Coordonnees();
	coordonnees.setCodePostal("44200");
	coordonnees.setCodeInsee("44109");
	coordonnees.setDeMayotte(false);
	return coordonnees;
    }
}
