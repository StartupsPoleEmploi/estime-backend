package fr.poleemploi.estime.clientsexternes.openfisca.mappeur;

import static fr.poleemploi.estime.clientsexternes.openfisca.mappeur.ParametresOpenFisca.DEMANDEUR;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fr.poleemploi.estime.clientsexternes.openfisca.ressources.OpenFiscaIndividu;
import fr.poleemploi.estime.clientsexternes.openfisca.ressources.OpenFiscaPeriodes;
import fr.poleemploi.estime.clientsexternes.openfisca.ressources.OpenFiscaRoot;
import fr.poleemploi.estime.commun.enumerations.exceptions.InternalServerMessages;
import fr.poleemploi.estime.commun.enumerations.exceptions.LoggerMessages;
import fr.poleemploi.estime.commun.utile.DateUtile;
import fr.poleemploi.estime.commun.utile.demandeuremploi.FuturTravailUtile;
import fr.poleemploi.estime.services.exceptions.InternalServerException;
import fr.poleemploi.estime.services.ressources.DemandeurEmploi;

@Component
public class OpenFiscaMappeurAgepi {

    @Autowired
    private OpenFiscaMappeurPeriode openFiscaPeriodeMappeur;

    @Autowired
    private DateUtile dateUtile;

    @Autowired
    private FuturTravailUtile futurTravailUtile;

    private static final String INTENSITE_ACTIVITE = "hebdomadaire";
    private static final String CATEGORIE_DEMANDEUR_EMPLOI = "categorie_1";
    private static final String LIEU_EMPLOI_OU_FORMATION = "metropole_hors_corse";

    private static final Logger LOGGER = LoggerFactory.getLogger(OpenFiscaMappeurAgepi.class);

    public float getMontantAgepi(OpenFiscaRoot openFiscaRoot, LocalDate dateDebutSimulation, int numeroMoisSimule) {
	try {
	    Map<String, OpenFiscaIndividu> openFiscaIndividus = openFiscaRoot.getIndividus();
	    OpenFiscaIndividu openFiscaIndividu = openFiscaIndividus.get(DEMANDEUR);
	    OpenFiscaPeriodes openFiscaAgepi = openFiscaIndividu.getAgepi();
	    String periodeFormateeAgepi = openFiscaPeriodeMappeur.getPeriodeOpenfiscaCalculAide(dateDebutSimulation, numeroMoisSimule);
	    Double montantAgepi = (Double) openFiscaAgepi.get(periodeFormateeAgepi);

	    return BigDecimal.valueOf(montantAgepi).setScale(0, RoundingMode.HALF_UP).floatValue();

	} catch (NullPointerException e) {
	    LOGGER.error(String.format(LoggerMessages.SIMULATION_IMPOSSIBLE_PROBLEME_TECHNIQUE.getMessage(), e.getMessage()));
	    throw new InternalServerException(InternalServerMessages.SIMULATION_IMPOSSIBLE.getMessage());
	}
    }

    public OpenFiscaIndividu addAgepiOpenFiscaIndividu(OpenFiscaIndividu openFiscaIndividu, DemandeurEmploi demandeurEmploi, LocalDate dateDebutSimulation, int numeroMoisSimule) {
	openFiscaIndividu.setTypeContratTravail(
		openFiscaPeriodeMappeur.creerPeriodesOpenFiscaAgepi(futurTravailUtile.getTypeContratOpenFisca(demandeurEmploi.getFuturTravail()), dateDebutSimulation));
	openFiscaIndividu.setIntensiteActivite(openFiscaPeriodeMappeur.creerPeriodesOpenFiscaAgepi(INTENSITE_ACTIVITE, dateDebutSimulation));
	openFiscaIndividu
		.setTempsDeTravail(openFiscaPeriodeMappeur.creerPeriodesOpenFiscaAgepi(demandeurEmploi.getFuturTravail().getNombreHeuresTravailleesSemaine(), dateDebutSimulation));
	openFiscaIndividu.setCategorieDemandeurEmploi(openFiscaPeriodeMappeur.creerPeriodesOpenFiscaAgepi(CATEGORIE_DEMANDEUR_EMPLOI, dateDebutSimulation));
	openFiscaIndividu.setLieuEmploiOuFormation(openFiscaPeriodeMappeur.creerPeriodesOpenFiscaAgepi(LIEU_EMPLOI_OU_FORMATION, dateDebutSimulation));
	openFiscaIndividu.setAgepiDateDemande(openFiscaPeriodeMappeur
		.creerPeriodesOpenFiscaAgepi(dateUtile.convertDateToStringOpenFisca(dateDebutSimulation.plusMonths((long) numeroMoisSimule - 1)), dateDebutSimulation));
	openFiscaIndividu
		.setDebutContratTravail(openFiscaPeriodeMappeur.creerPeriodesOpenFiscaAgepi(dateUtile.convertDateToStringOpenFisca(dateDebutSimulation), dateDebutSimulation));
	if (demandeurEmploi.getFuturTravail().getNombreMoisContratCDD() != null) {

	    openFiscaIndividu
		    .setDureeContratTravail(openFiscaPeriodeMappeur.creerPeriodesOpenFiscaAgepi(demandeurEmploi.getFuturTravail().getNombreMoisContratCDD(), dateDebutSimulation));
	}

	openFiscaIndividu.setAgepi(openFiscaPeriodeMappeur.getPeriodeOpenfiscaCalculAgepi(dateDebutSimulation));

	return openFiscaIndividu;
    }
}
