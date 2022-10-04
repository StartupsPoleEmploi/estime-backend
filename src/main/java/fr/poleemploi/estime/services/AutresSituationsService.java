package fr.poleemploi.estime.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import fr.poleemploi.estime.logique.AutresSituationsLogique;
import fr.poleemploi.estime.services.controleurs.AutresSituationsServiceControleur;
import fr.poleemploi.estime.services.ressources.AutresSituations;

@RestController
@RequestMapping("/autres_situations")
public class AutresSituationsService {

    @Autowired
    private AutresSituationsLogique autresSituationsLogique;

    @Autowired
    private AutresSituationsServiceControleur autresSituationsServiceControleur;

    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public void createAutresSituations(@RequestParam("idEstime") final String idEstime, @RequestBody AutresSituations autresSituations) {
	autresSituationsServiceControleur.controlerDonneesEntreeServiceAutresSituations(idEstime, autresSituations);
	autresSituationsLogique.creerAutresSituations(idEstime, autresSituations);
    }
}