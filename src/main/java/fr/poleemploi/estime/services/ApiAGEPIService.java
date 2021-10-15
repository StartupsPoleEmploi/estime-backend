package fr.poleemploi.estime.services;

import java.util.Arrays;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import fr.poleemploi.estime.clientsexternes.poleemploiio.PoleEmploiIOClient;
import fr.poleemploi.estime.clientsexternes.poleemploiio.ressources.AgepiPEIOIn;
import fr.poleemploi.estime.clientsexternes.poleemploiio.ressources.AgepiPEIOOut;

@RestController
@RequestMapping("/apiAgepi")
public class ApiAGEPIService {
	
	@Autowired
	private RestTemplate restTemplate;

	private String uri = "https://api.emploi-store.fr/partenaire/peconnect-simulateurs-aides/v1/demande-agepi/simuler";

	@Autowired
    private PoleEmploiIOClient emploiStoreDevClient;
	
	@PostMapping(value = "/montantAGEPI", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public float getMontantAgepi(@RequestBody String valeursAgepi) {
		AgepiPEIOIn agepiIn = new AgepiPEIOIn();
		//TODO creer agepiIn a pertir de valeursAgepi a partir d'un parseur?
		AgepiPEIOOut agepiOut = emploiStoreDevClient.callAgepiEndPoint(agepiIn);
		
		return agepiOut.getMontant();
		
		
//		HttpHeaders httpHeaders= new HttpHeaders();
//		httpHeaders.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
//		HttpEntity<String> origineEntity = new HttpEntity<String>(origine, httpHeaders);
//		
//		return restTemplate.exchange(this.uri+"/origine", HttpMethod.POST, origineEntity, String.class).getBody();
	}
}
