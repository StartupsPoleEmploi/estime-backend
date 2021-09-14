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

@RestController
@RequestMapping("/apiam")
public class ApiAGEPIService {
	
	@Autowired
	private RestTemplate restTemplate;

	private String uri = "http://ex054-vipa-a1rmex.sii24.pole-emploi.intra/exp-aides/demande-agepi/simuler";

//	@RequestMapping("/origine")
//	public String getOrigine()
//	{
//	    RestTemplate restTemplate = new RestTemplate();
//	    String result = restTemplate.getForObject(this.uri, String.class);	    
//	    return result;
//	}
	
	@PostMapping(value = "/origine", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public String postOrigine(@RequestBody String origine) {
		HttpHeaders httpHeaders= new HttpHeaders();
		httpHeaders.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		HttpEntity<String> origineEntity = new HttpEntity<String>(origine, httpHeaders);
		
		return restTemplate.exchange(this.uri+"/origine", HttpMethod.POST, origineEntity, String.class).getBody();
	}
}
