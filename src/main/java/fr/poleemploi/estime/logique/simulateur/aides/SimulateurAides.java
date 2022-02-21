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
import fr.poleemploi.estime.commun.utile.demandeuremploi.RessourcesFinancieresUtile;
import fr.poleemploi.estime.logique.simulateur.aides.caf.SimulateurAidesCAF;
import fr.poleemploi.estime.logique.simulateur.aides.poleemploi.SimulateurAidesPoleEmploi;
import fr.poleemploi.estime.logique.simulateur.aides.utile.AideUtile;
import fr.poleemploi.estime.logique.simulateur.aides.utile.SimulateurAidesUtile;
import fr.poleemploi.estime.services.ressources.Aide;
import fr.poleemploi.estime.services.ressources.DemandeurEmploi;
import fr.poleemploi.estime.services.ressources.SimulationAides;
import fr.poleemploi.estime.services.ressources.SimulationMensuelle;

@Component
public class SimulateurAides {

    @Autowired
    private AideUtile aideUtile;

    @Autowired
    private DateUtile dateUtile;

    @Autowired
    private SimulateurAidesUtile simulateurAidesUtile;

    @Autowired
    private RessourcesFinancieresUtile ressourcesFinancieresUtile;

    @Autowired
    private SimulateurAidesCAF simulateurAidesCAF;

    @Autowired
    private SimulateurAidesPoleEmploi simulateurAidesPoleEmploi;

    public SimulationAides simuler(DemandeurEmploi demandeurEmploi) {
	SimulationAides simulationAides = new SimulationAides();
	simulationAides.setSimulationsMensuelles(new ArrayList<>());

	LocalDate dateDemandeSimulation = dateUtile.getDateJour();
	LocalDate dateDebutSimulation = simulateurAidesUtile.getDateDebutSimulation(dateDemandeSimulation);

	simulationAides.setMontantRessourcesFinancieresMoisAvantSimulation(ressourcesFinancieresUtile.calculerMontantRessourcesFinancieresMoisAvantSimulation(demandeurEmploi));

	int nombreMoisASimuler = simulateurAidesUtile.getNombreMoisASimuler(demandeurEmploi);

	for (int numeroMoisSimule = 1; numeroMoisSimule <= nombreMoisASimuler; numeroMoisSimule++) {
	    simulerAidesPourCeMois(simulationAides, dateDebutSimulation, numeroMoisSimule, demandeurEmploi);
	}
	return simulationAides;
    }

    private void simulerAidesPourCeMois(SimulationAides simulationAides, LocalDate dateDebutSimulation, int numeroMoisSimule, DemandeurEmploi demandeurEmploi) {
	LocalDate dateMoisASimuler = getDateMoisASimuler(dateDebutSimulation, numeroMoisSimule);

	SimulationMensuelle simulationMensuelle = new SimulationMensuelle();
	simulationMensuelle.setDatePremierJourMoisSimule(dateMoisASimuler);
	simulationAides.getSimulationsMensuelles().add(simulationMensuelle);

	HashMap<String, Aide> aidesPourCeMois = new HashMap<>();
	simulationMensuelle.setMesAides(aidesPourCeMois);

	ajouterAidesSansCalcul(aidesPourCeMois, dateDebutSimulation, numeroMoisSimule, demandeurEmploi);
	simulateurAidesCAF.simuler(simulationAides, aidesPourCeMois, dateDebutSimulation, numeroMoisSimule, demandeurEmploi);
	simulateurAidesPoleEmploi.simuler(aidesPourCeMois, numeroMoisSimule, dateMoisASimuler, demandeurEmploi, dateDebutSimulation);
    }

    private LocalDate getDateMoisASimuler(LocalDate dateDebutSimulation, int numeroMoisSimule) {
	int nombreMoisToAdd = numeroMoisSimule - 1;
	return dateUtile.ajouterMoisALocalDate(dateDebutSimulation, nombreMoisToAdd);
    }

    private void ajouterAidesSansCalcul(Map<String, Aide> aidesPourCeMois, LocalDate dateDebutSimulation, int numeroMoisSimule, DemandeurEmploi demandeurEmploi) {
	if (ressourcesFinancieresUtile.hasPensionInvalidite(demandeurEmploi)) {
	    float montantPensionInvalidite = ressourcesFinancieresUtile.getPensionInvalidite(demandeurEmploi);
	    aidesPourCeMois.put(AideEnum.PENSION_INVALIDITE.getCode(), creerAideSansCalcul(AideEnum.PENSION_INVALIDITE, Optional.of(OrganismeEnum.CPAM), montantPensionInvalidite));
	}
	if (ressourcesFinancieresUtile.hasAllocationSupplementaireInvalidite(demandeurEmploi)) {
	    float montantAllocationSupplementaireInvalidite = ressourcesFinancieresUtile.getAllocationSupplementaireInvalidite(demandeurEmploi);
	    aidesPourCeMois.put(AideEnum.ALLOCATION_SUPPLEMENTAIRE_INVALIDITE.getCode(),
		    creerAideSansCalcul(AideEnum.ALLOCATION_SUPPLEMENTAIRE_INVALIDITE, Optional.of(OrganismeEnum.CPAM), montantAllocationSupplementaireInvalidite));
	}
	if (ressourcesFinancieresUtile.hasRevenusTravailleurIndependant(demandeurEmploi)) {
	    float montantRevenusTravailleurIndependantSur1Mois = ressourcesFinancieresUtile.getRevenusTravailleurIndependantSur1Mois(demandeurEmploi);
	    aidesPourCeMois.put(AideEnum.TRAVAILLEUR_INDEPENDANT.getCode(),
		    creerAideSansCalcul(AideEnum.TRAVAILLEUR_INDEPENDANT, Optional.empty(), montantRevenusTravailleurIndependantSur1Mois));
	}
	if (ressourcesFinancieresUtile.hasRevenusMicroEntreprise(demandeurEmploi)) {
	    float montantBeneficesMicroEntrepriseSur1Mois = ressourcesFinancieresUtile.getRevenusMicroEntrepriseSur1Mois(demandeurEmploi);
	    aidesPourCeMois.put(AideEnum.MICRO_ENTREPRENEUR.getCode(), creerAideSansCalcul(AideEnum.MICRO_ENTREPRENEUR, Optional.empty(), montantBeneficesMicroEntrepriseSur1Mois));
	}
	if (ressourcesFinancieresUtile.hasRevenusImmobilier(demandeurEmploi)) {
	    float montantRevenusImmobilier = ressourcesFinancieresUtile.getRevenusImmobilierSur1Mois(demandeurEmploi);
	    aidesPourCeMois.put(AideEnum.IMMOBILIER.getCode(), creerAideSansCalcul(AideEnum.IMMOBILIER, Optional.empty(), montantRevenusImmobilier));
	}
	if (ressourcesFinancieresUtile.hasFuturSalaire(demandeurEmploi)) {
	    float montantSalaire = ressourcesFinancieresUtile.getFuturSalaire(demandeurEmploi);
	    aidesPourCeMois.put(AideEnum.SALAIRE.getCode(), creerAideSansCalcul(AideEnum.SALAIRE, Optional.empty(), montantSalaire));
	}
    }

    private Aide creerAideSansCalcul(AideEnum aideEnum, Optional<OrganismeEnum> organismeEnumOptional, float montant) {
	return aideUtile.creerAide(aideEnum, organismeEnumOptional, Optional.empty(), false, montant);
    }

}
