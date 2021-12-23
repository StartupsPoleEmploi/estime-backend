package fr.poleemploi.estime.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fr.poleemploi.estime.logique.AideLogique;
import fr.poleemploi.estime.services.controleurs.AideServiceControleur;
import fr.poleemploi.estime.services.ressources.Aide;

@RestController
@RequestMapping("/aides")
public class AidesService {
    
    @Autowired
    private AideLogique aideLogique;
    
    @Autowired
    private AideServiceControleur aideServiceControleur;
    
   
    @GetMapping(value = "/{codeAide}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Aide getAideByCode(@PathVariable String codeAide) {
        aideServiceControleur.controlerDonneesEntreeServiceAuthentifier(codeAide);
        return aideLogique.getAideByCode(codeAide);
    }
}