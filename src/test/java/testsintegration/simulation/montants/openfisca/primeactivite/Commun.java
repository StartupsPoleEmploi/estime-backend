package testsintegration.simulation.montants.openfisca.primeactivite;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import fr.poleemploi.estime.commun.enumerations.AideEnum;
import fr.poleemploi.estime.commun.enumerations.NationaliteEnum;
import fr.poleemploi.estime.commun.enumerations.OrganismeEnum;
import fr.poleemploi.estime.commun.enumerations.TypePopulationEnum;
import fr.poleemploi.estime.commun.enumerations.TypesContratTravailEnum;
import fr.poleemploi.estime.services.ressources.Aide;
import fr.poleemploi.estime.services.ressources.AidesCPAM;
import fr.poleemploi.estime.services.ressources.DemandeurEmploi;
import fr.poleemploi.estime.services.ressources.Logement;
import fr.poleemploi.estime.services.ressources.Salaire;
import fr.poleemploi.estime.services.ressources.SimulationAides;
import fr.poleemploi.estime.services.ressources.SimulationMensuelle;
import fr.poleemploi.estime.services.ressources.StatutOccupationLogement;
import utile.tests.Utile;

public class Commun {

    @Autowired
    protected Utile utileTests;

    public SimulationMensuelle createSimulationMensuelleASS(float montantASS) {
        SimulationMensuelle simulationMensuelle = new SimulationMensuelle();
        HashMap<String, Aide> prestations = new HashMap<>();
        prestations.put(AideEnum.ALLOCATION_SOLIDARITE_SPECIFIQUE.getCode(), createSimulationMensuelAideASS(montantASS));
        simulationMensuelle.setMesAides(prestations);
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
        DemandeurEmploi demandeurEmploi = utileTests.creerBaseDemandeurEmploi(TypePopulationEnum.ASS.getLibelle(), isEnCouple, nbEnfant);
        demandeurEmploi.getInformationsPersonnelles().setDateNaissance(utileTests.getDate("05-07-1986"));
        demandeurEmploi.getInformationsPersonnelles().setNationalite(NationaliteEnum.FRANCAISE.getValeur());
        demandeurEmploi.getInformationsPersonnelles().setCodePostal("44200");
        demandeurEmploi.getFuturTravail().setTypeContrat(TypesContratTravailEnum.CDI.name());
        demandeurEmploi.getFuturTravail().setNombreHeuresTravailleesSemaine(35);
        demandeurEmploi.getFuturTravail().getSalaire().setMontantNet(900);
        demandeurEmploi.getFuturTravail().getSalaire().setMontantBrut(1165);
        demandeurEmploi.getRessourcesFinancieres().getAidesPoleEmploi().getAllocationASS().setAllocationJournaliereNet(16.89f);
        demandeurEmploi.getInformationsPersonnelles().setDateNaissance(utileTests.getDate("05-07-1986"));

        return demandeurEmploi;
    }

    protected SimulationAides createSimulationAides() {
        SimulationAides simulationAides = new SimulationAides();
        List<SimulationMensuelle> simulationsMensuelles = new ArrayList<>();
        simulationsMensuelles.add(createSimulationMensuelleASS(506.7f));
        simulationsMensuelles.add(createSimulationMensuelleASS(523.6f));
        simulationsMensuelles.add(createSimulationMensuelleASS(523.6f));
        simulationsMensuelles.add(createSimulationMensuelleASS(0));
        simulationAides.setSimulationsMensuelles(simulationsMensuelles);
        return simulationAides;
    }

    protected Salaire createSalaireConjoint() {
        Salaire salaireConjoint = new Salaire();
        salaireConjoint.setMontantNet(1200);
        salaireConjoint.setMontantBrut(1544);
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
        logement.setMontantCharges(50f);
        logement.setCodeInsee("44109");
        return logement;
    }
}
