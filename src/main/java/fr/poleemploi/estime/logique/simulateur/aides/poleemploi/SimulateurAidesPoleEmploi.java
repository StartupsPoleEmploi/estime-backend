package fr.poleemploi.estime.logique.simulateur.aides.poleemploi;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fr.poleemploi.estime.clientsexternes.openfisca.OpenFiscaClient;
import fr.poleemploi.estime.clientsexternes.openfisca.OpenFiscaRetourSimulation;
import fr.poleemploi.estime.commun.enumerations.AideEnum;
import fr.poleemploi.estime.commun.enumerations.MessageInformatifEnum;
import fr.poleemploi.estime.commun.enumerations.OrganismeEnum;
import fr.poleemploi.estime.commun.enumerations.SituationAppelOpenFiscaEnum;
import fr.poleemploi.estime.commun.utile.StagingEnvironnementUtile;
import fr.poleemploi.estime.commun.utile.demandeuremploi.BeneficiaireAidesUtile;
import fr.poleemploi.estime.logique.simulateur.aides.poleemploi.utile.AllocationSolidariteSpecifiqueUtile;
import fr.poleemploi.estime.logique.simulateur.aides.poleemploi.utile.AreUtile;
import fr.poleemploi.estime.logique.simulateur.aides.utile.AideUtile;
import fr.poleemploi.estime.logique.simulateur.aides.utile.SimulateurAidesUtile;
import fr.poleemploi.estime.services.ressources.Aide;
import fr.poleemploi.estime.services.ressources.DemandeurEmploi;
import fr.poleemploi.estime.services.ressources.Simulation;

@Component
public class SimulateurAidesPoleEmploi {

    @Autowired
    private AreUtile areUtile;

    @Autowired
    private AllocationSolidariteSpecifiqueUtile allocationSolidariteSpecifiqueUtile;

    @Autowired
    private BeneficiaireAidesUtile beneficiaireAidesUtile;

    @Autowired
    private StagingEnvironnementUtile stagingEnvironnementUtile;

    @Autowired
    private OpenFiscaClient openFiscaClient;

    @Autowired
    private SimulateurAidesUtile simulateurAidesUtile;

    @Autowired
    private AideUtile aideUtile;

    public void simuler(Simulation simulation, Map<String, Aide> aidesPourCeMois, int numeroMoisSimule, LocalDate moisSimule, DemandeurEmploi demandeurEmploi, LocalDate dateDebutSimulation) {

	//si l'utilisateur est un demandeur fictif, on ne peut pas simuler les aides necessitant un appel à une API peio sécurisée individu
	if (stagingEnvironnementUtile.isNotDemandeurFictif(demandeurEmploi)) {
	    simulerAidesByCallingApiPEIO(aidesPourCeMois, numeroMoisSimule, demandeurEmploi, dateDebutSimulation);
	}

	if (beneficiaireAidesUtile.isBeneficiaireASS(demandeurEmploi) && allocationSolidariteSpecifiqueUtile.isEligible(numeroMoisSimule, demandeurEmploi, dateDebutSimulation)) {
	    Optional<Aide> aideOptional = allocationSolidariteSpecifiqueUtile.simulerAide(demandeurEmploi, moisSimule, dateDebutSimulation);
	    if (aideOptional.isPresent()) {
		aidesPourCeMois.put(AideEnum.ALLOCATION_SOLIDARITE_SPECIFIQUE.getCode(), aideOptional.get());
	    }
	}

	simulerAidesByCallingOpenFisca(simulation, aidesPourCeMois, dateDebutSimulation, numeroMoisSimule, demandeurEmploi);
    }

    private void simulerAidesByCallingApiPEIO(Map<String, Aide> aidesPourCeMois, int numeroMoisSimule, DemandeurEmploi demandeurEmploi, LocalDate dateDebutSimulation) {
	if (beneficiaireAidesUtile.isBeneficiaireARE(demandeurEmploi)) {
	    areUtile.simuler(aidesPourCeMois, demandeurEmploi, numeroMoisSimule, dateDebutSimulation);
	}
    }

    private void simulerAidesByCallingOpenFisca(Simulation simulation, Map<String, Aide> aidesPourCeMois, LocalDate dateDebutSimulation, int numeroMoisSimule, DemandeurEmploi demandeurEmploi) {
	if (isAgepiEtAideMobiliteAVerser(numeroMoisSimule)) {
	    OpenFiscaRetourSimulation openFiscaRetourSimulation = openFiscaClient.calculerAidesSelonSituation(simulation, demandeurEmploi, dateDebutSimulation, numeroMoisSimule,
		    SituationAppelOpenFiscaEnum.AGEPI_AM);
	    verserAide(aidesPourCeMois, openFiscaRetourSimulation);
	}
    }

    private void verserAide(Map<String, Aide> aidesPourCeMois, OpenFiscaRetourSimulation openFiscaRetourSimulation) {
	if (openFiscaRetourSimulation.getMontantAgepi() > 0) {
	    ArrayList<String> messagesAlerte = new ArrayList<>();
	    messagesAlerte.add(MessageInformatifEnum.AGEPI_IDF.getMessage());
	    Aide agepi = aideUtile.creerAide(AideEnum.AGEPI, Optional.of(OrganismeEnum.PE), Optional.of(messagesAlerte), false, openFiscaRetourSimulation.getMontantAgepi());
	    aidesPourCeMois.put(agepi.getCode(), agepi);
	}
	if (openFiscaRetourSimulation.getMontantAideMobilite() > 0) {
	    Aide aideMobilite = aideUtile.creerAide(AideEnum.AIDE_MOBILITE, Optional.of(OrganismeEnum.PE), Optional.empty(), false,
		    openFiscaRetourSimulation.getMontantAideMobilite());
	    aidesPourCeMois.put(aideMobilite.getCode(), aideMobilite);
	}
    }

    private boolean isAgepiEtAideMobiliteAVerser(int numeroMoisSimule) {
	return simulateurAidesUtile.isPremierMois(numeroMoisSimule);
    }
}
