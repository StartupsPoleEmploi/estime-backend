package fr.poleemploi.estime.logique.simulateur.aides;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fr.poleemploi.estime.clientsexternes.openfisca.OpenFiscaClient;
import fr.poleemploi.estime.clientsexternes.openfisca.ressources.OpenFiscaRoot;
import fr.poleemploi.estime.commun.enumerations.AideEnum;
import fr.poleemploi.estime.commun.enumerations.MessageInformatifEnum;
import fr.poleemploi.estime.commun.enumerations.OrganismeEnum;
import fr.poleemploi.estime.commun.utile.DateUtile;
import fr.poleemploi.estime.commun.utile.demandeuremploi.RessourcesFinancieresAvantSimulationUtile;
import fr.poleemploi.estime.logique.simulateur.aides.caf.SimulateurAidesCAF;
import fr.poleemploi.estime.logique.simulateur.aides.poleemploi.SimulateurAidesPoleEmploi;
import fr.poleemploi.estime.logique.simulateur.aides.utile.AideUtile;
import fr.poleemploi.estime.logique.simulateur.aides.utile.RessourceFinanciereUtile;
import fr.poleemploi.estime.logique.simulateur.aides.utile.SimulateurAidesUtile;
import fr.poleemploi.estime.services.ressources.Aide;
import fr.poleemploi.estime.services.ressources.DemandeurEmploi;
import fr.poleemploi.estime.services.ressources.RessourceFinanciere;
import fr.poleemploi.estime.services.ressources.Simulation;
import fr.poleemploi.estime.services.ressources.SimulationMensuelle;

@Component
public class SimulateurAides {

    @Autowired
    private AideUtile aideUtile;

    @Autowired
    private RessourceFinanciereUtile ressourceFinanciereUtile;

    @Autowired
    private DateUtile dateUtile;

    @Autowired
    private SimulateurAidesUtile simulateurAidesUtile;

    @Autowired
    private RessourcesFinancieresAvantSimulationUtile ressourcesFinancieresUtile;

    @Autowired
    private SimulateurAidesCAF simulateurAidesCAF;

    @Autowired
    private SimulateurAidesPoleEmploi simulateurAidesPoleEmploi;

    @Autowired
    private TemporaliteOpenFiscaUtile temporaliteOpenFiscaUtile;

    @Autowired
    private OpenFiscaClient openFiscaClient;

    public Simulation simuler(DemandeurEmploi demandeurEmploi) {
	Simulation simulation = new Simulation();
	simulation.setSimulationsMensuelles(new ArrayList<>());

	LocalDate dateDemandeSimulation = dateUtile.getDateJour();
	LocalDate dateDebutSimulation = simulateurAidesUtile.getDateDebutSimulation(dateDemandeSimulation);

	simulation.setMontantRessourcesFinancieresMoisAvantSimulation(ressourcesFinancieresUtile.calculerMontantRessourcesFinancieresMoisAvantSimulation(demandeurEmploi));

	int nombreMoisASimuler = simulateurAidesUtile.getNombreMoisASimuler(demandeurEmploi);
	OpenFiscaRoot openFiscaRoot = openFiscaClient.callApiCalculate(demandeurEmploi, dateDebutSimulation, false);

	for (int numeroMoisSimule = 1; numeroMoisSimule <= nombreMoisASimuler; numeroMoisSimule++) {
	    simulerAidesPourCeMois(openFiscaRoot, simulation, dateDebutSimulation, numeroMoisSimule, demandeurEmploi);
	}
	return simulation;
    }

    private void simulerAidesPourCeMois(OpenFiscaRoot openFiscaRoot, Simulation simulation, LocalDate dateDebutSimulation, int numeroMoisSimule, DemandeurEmploi demandeurEmploi) {
	LocalDate dateMoisASimuler = dateUtile.getDateMoisASimuler(dateDebutSimulation, numeroMoisSimule);

	SimulationMensuelle simulationMensuelle = new SimulationMensuelle();
	simulationMensuelle.setDatePremierJourMoisSimule(dateMoisASimuler);
	simulation.getSimulationsMensuelles().add(simulationMensuelle);

	HashMap<String, Aide> aidesPourCeMois = new HashMap<>();
	simulationMensuelle.setAides(aidesPourCeMois);

	ajouterAidesSansCalcul(aidesPourCeMois, demandeurEmploi);
	temporaliteOpenFiscaUtile.simulerTemporaliteAppelOpenfisca(openFiscaRoot, simulation, aidesPourCeMois, dateDebutSimulation, numeroMoisSimule, demandeurEmploi);
	simulateurAidesCAF.simuler(aidesPourCeMois, numeroMoisSimule, demandeurEmploi);
	simulateurAidesPoleEmploi.simuler(aidesPourCeMois, numeroMoisSimule, demandeurEmploi, dateDebutSimulation);

	HashMap<String, RessourceFinanciere> ressourcesFinancieresPourCeMois = new HashMap<>();
	simulationMensuelle.setRessourcesFinancieres(ressourcesFinancieresPourCeMois);

	ajouterRessourcesFinancieres(ressourcesFinancieresPourCeMois, demandeurEmploi);
    }

    private void ajouterAidesSansCalcul(Map<String, Aide> aidesPourCeMois, DemandeurEmploi demandeurEmploi) {
	if (ressourcesFinancieresUtile.hasPensionInvalidite(demandeurEmploi)) {
	    float montantPensionInvalidite = ressourcesFinancieresUtile.getPensionInvalidite(demandeurEmploi);
	    aidesPourCeMois.put(AideEnum.PENSION_INVALIDITE.getCode(), creerAideSansCalcul(AideEnum.PENSION_INVALIDITE, Optional.of(OrganismeEnum.CPAM), montantPensionInvalidite));
	}
    }

    private void ajouterRessourcesFinancieres(Map<String, RessourceFinanciere> ressourcesFinancieresPourCeMois, DemandeurEmploi demandeurEmploi) {
	if (ressourcesFinancieresUtile.hasRevenusTravailleurIndependant(demandeurEmploi)) {
	    ArrayList<String> messagesAlerte = new ArrayList<>();
	    messagesAlerte.add(MessageInformatifEnum.MOYENNE_REVENUS_ENTREPRENEURS.getMessage());
	    float montantRevenusTravailleurIndependantSur1Mois = ressourcesFinancieresUtile.getRevenusTravailleurIndependantSur1Mois(demandeurEmploi);
	    ressourcesFinancieresPourCeMois.put(AideEnum.TRAVAILLEUR_INDEPENDANT.getCode(),
		    creerRessourceFinanciere(AideEnum.TRAVAILLEUR_INDEPENDANT, Optional.of(messagesAlerte), montantRevenusTravailleurIndependantSur1Mois));
	}
	if (ressourcesFinancieresUtile.hasRevenusMicroEntreprise(demandeurEmploi)) {
	    ArrayList<String> messagesAlerte = new ArrayList<>();
	    messagesAlerte.add(MessageInformatifEnum.MOYENNE_REVENUS_ENTREPRENEURS.getMessage());
	    float montantBeneficesMicroEntrepriseSur1Mois = ressourcesFinancieresUtile.getRevenusMicroEntrepriseSur1Mois(demandeurEmploi);
	    ressourcesFinancieresPourCeMois.put(AideEnum.MICRO_ENTREPRENEUR.getCode(),
		    creerRessourceFinanciere(AideEnum.MICRO_ENTREPRENEUR, Optional.of(messagesAlerte), montantBeneficesMicroEntrepriseSur1Mois));
	}
	if (ressourcesFinancieresUtile.hasRevenusImmobilier(demandeurEmploi)) {
	    float montantRevenusImmobilier = ressourcesFinancieresUtile.getRevenusImmobilierSur1Mois(demandeurEmploi);
	    ressourcesFinancieresPourCeMois.put(AideEnum.IMMOBILIER.getCode(), creerRessourceFinanciere(AideEnum.IMMOBILIER, Optional.empty(), montantRevenusImmobilier));
	}
	if (ressourcesFinancieresUtile.hasFuturSalaire(demandeurEmploi)) {
	    float montantSalaire = ressourcesFinancieresUtile.getFuturSalaire(demandeurEmploi);
	    ressourcesFinancieresPourCeMois.put(AideEnum.SALAIRE.getCode(), creerRessourceFinanciere(AideEnum.SALAIRE, Optional.empty(), montantSalaire));
	}
    }

    private Aide creerAideSansCalcul(AideEnum aideEnum, Optional<OrganismeEnum> organismeEnumOptional, float montant) {
	return aideUtile.creerAide(aideEnum, organismeEnumOptional, Optional.empty(), false, montant);
    }

    private RessourceFinanciere creerRessourceFinanciere(AideEnum aideEnum, Optional<List<String>> messageAlerteOptional, float montant) {
	return ressourceFinanciereUtile.creerRessourceFinanciere(aideEnum, messageAlerteOptional, montant);
    }

    public Simulation simulerComplementARE(DemandeurEmploi demandeurEmploi) {
	Simulation simulation = new Simulation();
	simulation.setSimulationsMensuelles(new ArrayList<>());

	LocalDate dateDemandeSimulation = dateUtile.getDateJour();
	LocalDate dateDebutSimulation = simulateurAidesUtile.getDateDebutSimulationParcoursComplementARE(dateDemandeSimulation);

	OpenFiscaRoot openFiscaRoot = openFiscaClient.callApiCalculate(demandeurEmploi, dateDebutSimulation, true);
	simulerComplementAREPourCeMois(openFiscaRoot, simulation, dateDebutSimulation, demandeurEmploi);
	return simulation;
    }

    private void simulerComplementAREPourCeMois(OpenFiscaRoot openFiscaRoot, Simulation simulation, LocalDate dateDebutSimulation, DemandeurEmploi demandeurEmploi) {
	SimulationMensuelle simulationMensuelle = new SimulationMensuelle();
	simulationMensuelle.setDatePremierJourMoisSimule(dateDebutSimulation);
	simulation.getSimulationsMensuelles().add(simulationMensuelle);

	HashMap<String, Aide> aidesPourCeMois = new HashMap<>();
	simulationMensuelle.setAides(aidesPourCeMois);
	simulateurAidesPoleEmploi.simulerComplementARE(openFiscaRoot, aidesPourCeMois, dateDebutSimulation);

	HashMap<String, RessourceFinanciere> ressourcesFinancieresPourCeMois = new HashMap<>();
	simulationMensuelle.setRessourcesFinancieres(ressourcesFinancieresPourCeMois);

	ajouterRessourcesFinancieres(ressourcesFinancieresPourCeMois, demandeurEmploi);
    }
}
