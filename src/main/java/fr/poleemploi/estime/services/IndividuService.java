package fr.poleemploi.estime.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import fr.poleemploi.estime.logique.IndividuLogique;
import fr.poleemploi.estime.services.controleurs.IndividuServiceControleur;
import fr.poleemploi.estime.services.ressources.DemandeurEmploi;
import fr.poleemploi.estime.services.ressources.Individu;
import fr.poleemploi.estime.services.ressources.PeConnectPayload;
import fr.poleemploi.estime.services.ressources.SimulationAides;

@RestController
@RequestMapping("/individus")
public class IndividuService {
    
    @Autowired
    private IndividuLogique individuLogique;
    
    @Autowired
    private IndividuServiceControleur individuServiceControleur;
    
    @PostMapping(value = "/authentifier", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Individu authentifier(@RequestBody PeConnectPayload peConnectPayload) {
        individuServiceControleur.controlerDonneesEntreeServiceAuthentifier(peConnectPayload);
        return individuLogique.authentifier(
                peConnectPayload.getCode(), 
                peConnectPayload.getRedirectURI(), 
                peConnectPayload.getNonce());
    }

    @PutMapping(value ="/demandeur_emploi", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public DemandeurEmploi creerDemandeurEmploi(@RequestBody Individu individu) {
        individuServiceControleur.controlerDonneesEntreeServiceCreerDemandeurEmploi(individu);
        return individuLogique.creerDemandeurEmploi(individu);
    }

    @PostMapping(value = "/demandeur_emploi/simulation_aides", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public SimulationAides simulerAides(@RequestBody DemandeurEmploi demandeurEmploi) {
        individuServiceControleur.controlerDonneesEntreeServiceSimulerMesAides(demandeurEmploi);
        return individuLogique.simulerMesAides(demandeurEmploi);
    }
    
    @DeleteMapping(value = "/demandeur_emploi/suivi_parcours", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    public void supprimerSuiviParcoursUtilisateur(@RequestParam("idPoleEmploi") final String idPoleEmploi) {
        individuLogique.supprimerSuiviParcoursUtilisateur(idPoleEmploi);
    }
}
