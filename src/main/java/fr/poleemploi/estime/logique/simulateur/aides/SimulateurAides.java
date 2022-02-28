package fr.poleemploi.estime.logique.simulateur.aides;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fr.poleemploi.estime.commun.enumerations.AideEnum;
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

    public Simulation simuler(DemandeurEmploi demandeurEmploi) {
	Simulation simulation = new Simulation();
	simulation.setSimulationsMensuelles(new ArrayList<>());

	LocalDate dateDemandeSimulation = dateUtile.getDateJour();
	LocalDate dateDebutSimulation = simulateurAidesUtile.getDateDebutSimulation(dateDemandeSimulation);

	simulation.setMontantRessourcesFinancieresMoisAvantSimulation(ressourcesFinancieresUtile.calculerMontantRessourcesFinancieresMoisAvantSimulation(demandeurEmploi));

	int nombreMoisASimuler = simulateurAidesUtile.getNombreMoisASimuler(demandeurEmploi);

	for (int numeroMoisSimule = 1; numeroMoisSimule <= nombreMoisASimuler; numeroMoisSimule++) {
	    simulerAidesPourCeMois(simulation, dateDebutSimulation, numeroMoisSimule, demandeurEmploi);
	}
	return simulation;
    }

    private void simulerAidesPourCeMois(Simulation simulation, LocalDate dateDebutSimulation, int numeroMoisSimule, DemandeurEmploi demandeurEmploi) {
	LocalDate dateMoisASimuler = getDateMoisASimuler(dateDebutSimulation, numeroMoisSimule);

	SimulationMensuelle simulationMensuelle = new SimulationMensuelle();
	simulationMensuelle.setDatePremierJourMoisSimule(dateMoisASimuler);
	simulation.getSimulationsMensuelles().add(simulationMensuelle);

	HashMap<String, Aide> aidesPourCeMois = new HashMap<>();
	simulationMensuelle.setAides(aidesPourCeMois);

	ajouterAidesSansCalcul(aidesPourCeMois, demandeurEmploi);
	simulateurAidesCAF.simuler(simulation, aidesPourCeMois, dateDebutSimulation, numeroMoisSimule, demandeurEmploi);
	simulateurAidesPoleEmploi.simuler(aidesPourCeMois, numeroMoisSimule, dateMoisASimuler, demandeurEmploi, dateDebutSimulation);

	HashMap<String, RessourceFinanciere> ressourcesFinancieresPourCeMois = new HashMap<>();
	simulationMensuelle.setRessourcesFinancieres(ressourcesFinancieresPourCeMois);

	ajouterRessourcesFinancieres(ressourcesFinancieresPourCeMois, demandeurEmploi);

    }

    private LocalDate getDateMoisASimuler(LocalDate dateDebutSimulation, int numeroMoisSimule) {
	int nombreMoisToAdd = numeroMoisSimule - 1;
	return dateUtile.ajouterMoisALocalDate(dateDebutSimulation, nombreMoisToAdd);
    }

    private void ajouterAidesSansCalcul(Map<String, Aide> aidesPourCeMois, DemandeurEmploi demandeurEmploi) {
	if (ressourcesFinancieresUtile.hasPensionInvalidite(demandeurEmploi)) {
	    float montantPensionInvalidite = ressourcesFinancieresUtile.getPensionInvalidite(demandeurEmploi);
	    aidesPourCeMois.put(AideEnum.PENSION_INVALIDITE.getCode(), creerAideSansCalcul(AideEnum.PENSION_INVALIDITE, Optional.of(OrganismeEnum.CPAM), montantPensionInvalidite));
	}
	if (ressourcesFinancieresUtile.hasAllocationSupplementaireInvalidite(demandeurEmploi)) {
	    float montantAllocationSupplementaireInvalidite = ressourcesFinancieresUtile.getAllocationSupplementaireInvalidite(demandeurEmploi);
	    aidesPourCeMois.put(AideEnum.ALLOCATION_SUPPLEMENTAIRE_INVALIDITE.getCode(),
		    creerAideSansCalcul(AideEnum.ALLOCATION_SUPPLEMENTAIRE_INVALIDITE, Optional.of(OrganismeEnum.CPAM), montantAllocationSupplementaireInvalidite));
	}
    }

    private void ajouterRessourcesFinancieres(Map<String, RessourceFinanciere> ressourcesFinancieresPourCeMois, DemandeurEmploi demandeurEmploi) {
	if (ressourcesFinancieresUtile.hasRevenusTravailleurIndependant(demandeurEmploi)) {
	    float montantRevenusTravailleurIndependantSur1Mois = ressourcesFinancieresUtile.getRevenusTravailleurIndependantSur1Mois(demandeurEmploi);
	    ressourcesFinancieresPourCeMois.put(AideEnum.TRAVAILLEUR_INDEPENDANT.getCode(),
		    creerRessourceFinanciere(AideEnum.TRAVAILLEUR_INDEPENDANT, montantRevenusTravailleurIndependantSur1Mois));
	}
	if (ressourcesFinancieresUtile.hasRevenusMicroEntreprise(demandeurEmploi)) {
	    float montantBeneficesMicroEntrepriseSur1Mois = ressourcesFinancieresUtile.getRevenusMicroEntrepriseSur1Mois(demandeurEmploi);
	    ressourcesFinancieresPourCeMois.put(AideEnum.MICRO_ENTREPRENEUR.getCode(),
		    creerRessourceFinanciere(AideEnum.MICRO_ENTREPRENEUR, montantBeneficesMicroEntrepriseSur1Mois));
	}
	if (ressourcesFinancieresUtile.hasRevenusImmobilier(demandeurEmploi)) {
	    float montantRevenusImmobilier = ressourcesFinancieresUtile.getRevenusImmobilierSur1Mois(demandeurEmploi);
	    ressourcesFinancieresPourCeMois.put(AideEnum.IMMOBILIER.getCode(), creerRessourceFinanciere(AideEnum.IMMOBILIER, montantRevenusImmobilier));
	}
	if (ressourcesFinancieresUtile.hasFuturSalaire(demandeurEmploi)) {
	    float montantSalaire = ressourcesFinancieresUtile.getFuturSalaire(demandeurEmploi);
	    ressourcesFinancieresPourCeMois.put(AideEnum.SALAIRE.getCode(), creerRessourceFinanciere(AideEnum.SALAIRE, montantSalaire));
	}
    }

    private Aide creerAideSansCalcul(AideEnum aideEnum, Optional<OrganismeEnum> organismeEnumOptional, float montant) {
	return aideUtile.creerAide(aideEnum, organismeEnumOptional, Optional.empty(), false, montant);
    }

    private RessourceFinanciere creerRessourceFinanciere(AideEnum aideEnum, float montant) {
	return ressourceFinanciereUtile.creerRessourceFinanciere(aideEnum, montant);
    }

}
