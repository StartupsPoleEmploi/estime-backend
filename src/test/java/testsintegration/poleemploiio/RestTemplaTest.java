package testsintegration.poleemploiio;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.response.MockRestResponseCreators;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import fr.poleemploi.estime.clientsexternes.poleemploiio.ressources.EtatCivilPEIO;
import fr.poleemploi.estime.commun.utile.demandeuremploi.DemandeurEmploiUtile;
import fr.poleemploi.estime.services.ressources.DemandeurEmploi;

@ContextConfiguration
@SpringBootTest

public class RestTemplaTest {

	
	
	@Autowired
	private DemandeurEmploiUtile demandeurEmploiUtile;

	@Autowired
	private RestTemplate restTemplate;

	private MockRestServiceServer mockServer;
	private ObjectMapper mapper = new ObjectMapper();

	@Configuration
	@ComponentScan({ "utile.tests", "fr.poleemploi.estime" })
	public static class SpringConfig {
	}

	@BeforeEach
	public void init() {
		mockServer = MockRestServiceServer.createServer(restTemplate);
	}

	@Test
	void testCallDateNaissance() throws URISyntaxException, JsonProcessingException {
		EtatCivilPEIO datenaissanceESD = new EtatCivilPEIO();
		datenaissanceESD.setDateDeNaissance(new Date());


		mockServer.expect(ExpectedCount.manyTimes(), 
				requestTo(new URI("https://api.emploi-store.fr/partenaire/peconnect-datenaissance/v1/etat-civil")))
		.andRespond(MockRestResponseCreators.withStatus(HttpStatus.INTERNAL_SERVER_ERROR));
		
		DemandeurEmploi demandeurEmploi = new DemandeurEmploi();
		demandeurEmploiUtile.addDateNaissance(demandeurEmploi, "Bearer lAZED4_IbHupj-aqbnZrD-nBIfw");
	}
	
	

}
