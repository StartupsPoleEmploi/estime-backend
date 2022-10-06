package testsintegration.simulation.montants.openfisca.primeactivite;

import java.text.ParseException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import fr.poleemploi.estime.commun.enumerations.AideEnum;
import fr.poleemploi.estime.commun.enumerations.NationaliteEnum;
import fr.poleemploi.estime.commun.enumerations.OrganismeEnum;
import fr.poleemploi.estime.commun.enumerations.TypeContratTravailEnum;
import fr.poleemploi.estime.commun.enumerations.TypePopulationEnum;
import fr.poleemploi.estime.commun.utile.DateUtile;
import fr.poleemploi.estime.services.ressources.Aide;
import fr.poleemploi.estime.services.ressources.AidesCPAM;
import fr.poleemploi.estime.services.ressources.Coordonnees;
import fr.poleemploi.estime.services.ressources.DemandeurEmploi;
import fr.poleemploi.estime.services.ressources.Logement;
import fr.poleemploi.estime.services.ressources.Salaire;
import fr.poleemploi.estime.services.ressources.Simulation;
import fr.poleemploi.estime.services.ressources.SimulationMensuelle;
import fr.poleemploi.estime.services.ressources.StatutOccupationLogement;
import utile.tests.Utile;

public class Commun {

    @Autowired
    protected Utile utileTests;

    @Autowired
    protected DateUtile dateUtile;

    public SimulationMensuelle createSimulationMensuelleASS(float montantASS) {
	SimulationMensuelle simulationMensuelle = new SimulationMensuelle();
	HashMap<String, Aide> prestations = new HashMap<>();
	prestations.put(AideEnum.ALLOCATION_SOLIDARITE_SPECIFIQUE.getCode(), createSimulationMensuelAideASS(montantASS));
	simulationMensuelle.setAides(prestations);
	return simulationMensuelle;
    }

    private Aide createSimulationMensuelAideASS(float montantASS) {
	Aide ass = new Aide();
	ass.setCode(AideEnum.ALLOCATION_SOLIDARITE_SPECIFIQUE.getCode());
	ass.setOrganisme(OrganismeEnum.PE.getNomCourt());
	ass.setMontant(montantASS);
	return ass;
    }

    protected DemandeurEmploi createDemandeurEmploi(boolean isEnCouple, int nbEnfant) throws ParseException {
	DemandeurEmploi demandeurEmploi = utileTests.creerBaseDemandeurEmploi(TypePopulationEnum.ASS, isEnCouple, nbEnfant);
	demandeurEmploi.getInformationsPersonnelles().setDateNaissance(utileTests.getDate("05-07-1986"));
	demandeurEmploi.getInformationsPersonnelles().setNationalite(NationaliteEnum.FRANCAISE.getValeur());
	demandeurEmploi.getFuturTravail().setTypeContrat(TypeContratTravailEnum.CDI.name());
	demandeurEmploi.getFuturTravail().setNombreHeuresTravailleesSemaine(35);
	demandeurEmploi.getFuturTravail().getSalaire().setMontantMensuelNet(900);
	demandeurEmploi.getFuturTravail().getSalaire().setMontantMensuelBrut(1165);
	demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesPoleEmploi().getAllocationASS().setAllocationJournaliereNet(16.89f);
	LocalDate dateDerniereOuvertureDroitASS = dateUtile.enleverMoisALocalDate(utileTests.getDate("01-01-2022"), 6);
	demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesPoleEmploi().getAllocationASS().setDateDerniereOuvertureDroit(dateDerniereOuvertureDroitASS);
	demandeurEmploi.getRessourcesFinancieresAvantSimulation().setHasTravailleAuCoursDerniersMois(true);
	demandeurEmploi.getRessourcesFinancieresAvantSimulation().setPeriodeTravailleeAvantSimulation(utileTests.creerPeriodeTravailleeAvantSimulation(1101, 850, 1));

	return demandeurEmploi;
    }

    protected Simulation createSimulation() {
	Simulation simulation = new Simulation();
	List<SimulationMensuelle> simulationsMensuelles = new ArrayList<>();
	simulationsMensuelles.add(createSimulationMensuelleASS(506.7f));
	simulationsMensuelles.add(createSimulationMensuelleASS(523.6f));
	simulationsMensuelles.add(createSimulationMensuelleASS(523.6f));
	simulationsMensuelles.add(createSimulationMensuelleASS(0));
	simulation.setSimulationsMensuelles(simulationsMensuelles);
	return simulation;
    }

    protected Salaire createSalaireConjoint() {
	Salaire salaireConjoint = new Salaire();
	salaireConjoint.setMontantMensuelNet(1200);
	salaireConjoint.setMontantMensuelBrut(1544);
	return salaireConjoint;
    }

    protected AidesCPAM createAidesCPAMConjoint() {
	AidesCPAM aidesCPAMConjoint = new AidesCPAM();
	aidesCPAMConjoint.setPensionInvalidite(200f);
	return aidesCPAMConjoint;
    }

    protected Logement createLogement() {
	Logement logement = new Logement();
	StatutOccupationLogement statutOccupationLogement = new StatutOccupationLogement();
	statutOccupationLogement.setLocataireNonMeuble(true);
	logement.setStatutOccupationLogement(statutOccupationLogement);
	logement.setMontantLoyer(500f);
	logement.setCoordonnees(createCoordonnees());
	return logement;
    }

    protected Coordonnees createCoordonnees() {
	Coordonnees coordonnees = new Coordonnees();
	coordonnees.setCodePostal("44200");
	coordonnees.setCodeInsee("44109");
	return coordonnees;
    }

}
