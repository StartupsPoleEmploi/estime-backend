package fr.poleemploi.estime.logique.simulateur.aides;

import java.math.BigDecimal;
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
import fr.poleemploi.estime.commun.utile.demandeuremploi.InformationsPersonnellesUtile;
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
    private RessourcesFinancieresAvantSimulationUtile ressourcesFinancieresAvantSimulationUtile;

    @Autowired
    private InformationsPersonnellesUtile informationsPersonnellesUtile;

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

	int nombreMoisASimuler = simulateurAidesUtile.getNombreMoisASimuler(demandeurEmploi);
	OpenFiscaRoot openFiscaRoot = openFiscaClient.callApiCalculate(demandeurEmploi, dateDebutSimulation, false);

	for (int numeroMoisSimule = 1; numeroMoisSimule <= nombreMoisASimuler; numeroMoisSimule++) {
	    simulerAidesPourCeMois(openFiscaRoot, simulation, dateDebutSimulation, numeroMoisSimule, demandeurEmploi);
	}
	simulation.setMontantRessourcesFinancieresMoisAvantSimulation(getMontantRessourcesFinancieresMoisAvantSimulation(openFiscaRoot, demandeurEmploi, dateDebutSimulation));
	return simulation;
    }

    private float getMontantRessourcesFinancieresMoisAvantSimulation(OpenFiscaRoot openFiscaRoot, DemandeurEmploi demandeurEmploi, LocalDate dateDebutSimulation) {
	BigDecimal montant = BigDecimal.valueOf(ressourcesFinancieresAvantSimulationUtile.calculerMontantRessourcesFinancieresMoisAvantSimulation(demandeurEmploi));
	if (informationsPersonnellesUtile.hasMicroEntreprise(demandeurEmploi)) {
	    montant = montant.add(BigDecimal.valueOf(openFiscaClient.calculerMontantBeneficesMicroEntreprise(openFiscaRoot, dateDebutSimulation)));
	}
	return montant.floatValue();
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

	ajouterRessourcesFinancieres(openFiscaRoot, ressourcesFinancieresPourCeMois, demandeurEmploi, dateDebutSimulation);
    }

    private void ajouterAidesSansCalcul(Map<String, Aide> aidesPourCeMois, DemandeurEmploi demandeurEmploi) {
	if (ressourcesFinancieresAvantSimulationUtile.hasPensionInvalidite(demandeurEmploi)) {
	    float montantPensionInvalidite = ressourcesFinancieresAvantSimulationUtile.getPensionInvalidite(demandeurEmploi);
	    aidesPourCeMois.put(AideEnum.PENSION_INVALIDITE.getCode(), creerAideSansCalcul(AideEnum.PENSION_INVALIDITE, Optional.of(OrganismeEnum.CPAM), montantPensionInvalidite));
	}
    }

    private void ajouterRessourcesFinancieres(OpenFiscaRoot openFiscaRoot, Map<String, RessourceFinanciere> ressourcesFinancieresPourCeMois, DemandeurEmploi demandeurEmploi, LocalDate dateDebutSimulation) {
	if (informationsPersonnellesUtile.hasMicroEntreprise(demandeurEmploi)) {
	    ArrayList<String> messagesAlerte = new ArrayList<>();
	    messagesAlerte.add(MessageInformatifEnum.MOYENNE_REVENUS_ENTREPRENEURS.getMessage());
	    float montantBeneficesMicroEntrepriseSur1Mois = openFiscaClient.calculerMontantBeneficesMicroEntreprise(openFiscaRoot, dateDebutSimulation);
	    ressourcesFinancieresPourCeMois.put(AideEnum.MICRO_ENTREPRENEUR.getCode(),
		    creerRessourceFinanciere(AideEnum.MICRO_ENTREPRENEUR, Optional.of(messagesAlerte), montantBeneficesMicroEntrepriseSur1Mois));
	}
	if (ressourcesFinancieresAvantSimulationUtile.hasRevenusImmobilier(demandeurEmploi)) {
	    float montantRevenusImmobilier = ressourcesFinancieresAvantSimulationUtile.getRevenusImmobilierSur1Mois(demandeurEmploi);
	    ressourcesFinancieresPourCeMois.put(AideEnum.IMMOBILIER.getCode(), creerRessourceFinanciere(AideEnum.IMMOBILIER, Optional.empty(), montantRevenusImmobilier));
	}
	if (ressourcesFinancieresAvantSimulationUtile.hasFuturSalaire(demandeurEmploi)) {
	    float montantSalaire = ressourcesFinancieresAvantSimulationUtile.getFuturSalaire(demandeurEmploi);
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

	ajouterRessourcesFinancieres(openFiscaRoot, ressourcesFinancieresPourCeMois, demandeurEmploi, dateDebutSimulation);
    }
}
