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

import fr.poleemploi.estime.logique.DemandeurEmploiLogique;
import fr.poleemploi.estime.services.controleurs.demandeuremploi.DemandeurEmploiServiceControleur;
import fr.poleemploi.estime.services.ressources.DemandeurEmploi;
import fr.poleemploi.estime.services.ressources.Individu;
import fr.poleemploi.estime.services.ressources.Simulation;

@RestController
@RequestMapping("/demandeurs_emploi")
public class DemandeurEmploiService {

    @Autowired
    private DemandeurEmploiServiceControleur demandeurEmploiServiceControleur;

    @Autowired
    private DemandeurEmploiLogique demandeurEmploiLogique;

    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public DemandeurEmploi creerDemandeurEmploi(@RequestBody Individu individu) {
	demandeurEmploiServiceControleur.controlerDonneesEntreeServiceCreerDemandeurEmploi(individu);
	return demandeurEmploiLogique.creerDemandeurEmploi(individu);
    }

    @PostMapping(value = "/simulation_aides", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Simulation simulerAides(@RequestBody DemandeurEmploi demandeurEmploi) {
	demandeurEmploiServiceControleur.controlerDonneesEntreeServiceSimulerMesAides(demandeurEmploi);
	return demandeurEmploiLogique.simulerMesAides(demandeurEmploi);
    }

    @DeleteMapping(value = "suivi_parcours", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    public void supprimerSuiviParcoursUtilisateur(@RequestParam("idPoleEmploi") final String idPoleEmploi) {
	demandeurEmploiLogique.supprimerSuiviParcoursUtilisateur(idPoleEmploi);
    }
}
