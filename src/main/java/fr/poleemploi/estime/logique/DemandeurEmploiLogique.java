package fr.poleemploi.estime.logique;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fr.poleemploi.estime.commun.enumerations.ParcourUtilisateurEnum;
import fr.poleemploi.estime.commun.utile.ContactSendinblueUtile;
import fr.poleemploi.estime.commun.utile.StagingEnvironnementUtile;
import fr.poleemploi.estime.commun.utile.SuiviUtilisateurUtile;
import fr.poleemploi.estime.commun.utile.demandeuremploi.DemandeurEmploiUtile;
import fr.poleemploi.estime.logique.simulateur.aides.SimulateurAides;
import fr.poleemploi.estime.services.ressources.DemandeurEmploi;
import fr.poleemploi.estime.services.ressources.Individu;
import fr.poleemploi.estime.services.ressources.Simulation;

@Component
public class DemandeurEmploiLogique {

    @Autowired
    private ContactSendinblueUtile contactSendinblueUtile;

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

	contactSendinblueUtile.miseAJourContactSendinblue(demandeurEmploi, ParcourUtilisateurEnum.SIMULATION_COMMENCEE.getParcours());

	return demandeurEmploi;
    }

    public Simulation simulerMesAides(DemandeurEmploi demandeurEmploi) {
	demandeurEmploiUtile.miseAJourCoordonnees(demandeurEmploi);
	Simulation simulation = simulateurAides.simuler(demandeurEmploi);

	if (stagingEnvironnementUtile.isNotLocalhostEnvironnement()) {
	    suiviUtilisateurUtile.tracerParcoursUtilisateurCreationSimulation(demandeurEmploi.getIdPoleEmploi(), ParcourUtilisateurEnum.SIMULATION_EFFECTUEE.getParcours(),
		    demandeurEmploi.getBeneficiaireAides(), demandeurEmploi.getInformationsPersonnelles());
	}

	contactSendinblueUtile.miseAJourContactSendinblue(demandeurEmploi, ParcourUtilisateurEnum.SIMULATION_EFFECTUEE.getParcours());

	return simulation;
    }

    public void supprimerSuiviParcoursUtilisateur(String idPoleEmploi) {
	suiviUtilisateurUtile.supprimerTracesParcoursUtilisateur(idPoleEmploi);
    }
}
