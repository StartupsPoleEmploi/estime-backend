package fr.poleemploi.estime.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import fr.poleemploi.estime.logique.IndividuLogique;
import fr.poleemploi.estime.services.controleurs.IndividuServiceControleur;
import fr.poleemploi.estime.services.ressources.Individu;
import fr.poleemploi.estime.services.ressources.PeConnectPayload;

@RestController
@RequestMapping("/individus")
public class IndividuService {

    @Autowired
    private IndividuLogique individuLogique;

    @Autowired
    private IndividuServiceControleur individuServiceControleur;

    @PostMapping(value = "/authentifier", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Individu authentifier(@RequestBody(required = false) PeConnectPayload peConnectPayload, @RequestParam("trafficSource") final String trafficSource) {
	individuServiceControleur.controlerDonneesEntreeServiceAuthentifier(trafficSource);
	return individuLogique.authentifier(peConnectPayload, trafficSource);
    }

}
