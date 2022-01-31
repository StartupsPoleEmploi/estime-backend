package fr.poleemploi.estime.logique;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fr.poleemploi.estime.commun.enumerations.ParcourUtilisateurEnum;
import fr.poleemploi.estime.commun.utile.StagingEnvironnementUtile;
import fr.poleemploi.estime.commun.utile.SuiviUtilisateurUtile;
import fr.poleemploi.estime.commun.utile.demandeuremploi.DemandeurEmploiUtile;
import fr.poleemploi.estime.logique.simulateur.aides.SimulateurAides;
import fr.poleemploi.estime.services.ressources.DemandeurEmploi;
import fr.poleemploi.estime.services.ressources.Individu;
import fr.poleemploi.estime.services.ressources.SimulationAides;

@Component
public class DemandeurEmploiLogique {

    @Autowired
    private DemandeurEmploiUtile demandeurEmploiUtile;

    @Autowired
    private SimulateurAides simulateurAides;

    @Autowired
    private StagingEnvironnementUtile stagingEnvironnementUtile;

    @Autowired
    private SuiviUtilisateurUtile suiviUtilisateurUtile;

    public DemandeurEmploi creerDemandeurEmploi(Individu individu) {

	DemandeurEmploi demandeurEmploi = new DemandeurEmploi();
	demandeurEmploi.setIdPoleEmploi(individu.getIdPoleEmploi());
	demandeurEmploiUtile.addInformationsPersonnelles(demandeurEmploi, individu);
	demandeurEmploi.setBeneficiaireAides(individu.getBeneficiaireAides());
	demandeurEmploiUtile.addRessourcesFinancieres(demandeurEmploi, individu);

	if (stagingEnvironnementUtile.isNotLocalhostEnvironnement()) {
	    suiviUtilisateurUtile.tracerParcoursUtilisateurCreationSimulation(demandeurEmploi.getIdPoleEmploi(), ParcourUtilisateurEnum.SIMULATION_COMMENCEE.getParcours(),
		    individu.getBeneficiaireAides(), demandeurEmploi.getInformationsPersonnelles());
	}

	return demandeurEmploi;
    }

    public SimulationAides simulerMesAides(DemandeurEmploi demandeurEmploi) {
	demandeurEmploiUtile.miseAJourCoordonnees(demandeurEmploi);
	SimulationAides simulationAides = simulateurAides.simuler(demandeurEmploi);

	if (stagingEnvironnementUtile.isNotLocalhostEnvironnement()) {
	    suiviUtilisateurUtile.tracerParcoursUtilisateurCreationSimulation(demandeurEmploi.getIdPoleEmploi(), ParcourUtilisateurEnum.SIMULATION_EFFECTUEE.getParcours(),
		    demandeurEmploi.getBeneficiaireAides(), demandeurEmploi.getInformationsPersonnelles());
	}

	return simulationAides;
    }

    public void supprimerSuiviParcoursUtilisateur(String idPoleEmploi) {
	suiviUtilisateurUtile.supprimerTracesParcoursUtilisateur(idPoleEmploi);
    }
}
