package fr.poleemploi.estime.clientsexternes.poleemploiio.utile;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import fr.poleemploi.estime.clientsexternes.poleemploiio.ressources.simulateuraides.aidemobilite.AideMobilitePEIOIn;
import fr.poleemploi.estime.commun.enumerations.NombreJourIndemniseEnum;
import fr.poleemploi.estime.commun.utile.DateUtile;
import fr.poleemploi.estime.commun.utile.demandeuremploi.SituationFamilialeUtile;
import fr.poleemploi.estime.services.ressources.DemandeurEmploi;

@Component
public class SimulateurAidesAideMobiliteUtile {

    @Autowired
    private DateUtile dateUtile;

    @Autowired
    private SituationFamilialeUtile situationFamilialeUtile;

    private static final int NOMBRE_MAX_JOURS_INDEMNISES = 20;
    private static final String CONTEXTE_REPRISE_EMPLOI = "reprise";

    public HttpEntity<AideMobilitePEIOIn> createHttpEntityAgepiSimulateurAides(DemandeurEmploi demandeurEmploi) {
	HttpHeaders headers = new HttpHeaders();
	headers.setContentType(MediaType.APPLICATION_JSON);
	headers.add("Authorization", demandeurEmploi.getPeConnectAuthorization().getBearerToken());
	return new HttpEntity<>(createAideMobilitePEIOIn(demandeurEmploi), headers);
    }

    private AideMobilitePEIOIn createAideMobilitePEIOIn(DemandeurEmploi demandeurEmploi) {
	AideMobilitePEIOIn aideMobilitePEIOIn = new AideMobilitePEIOIn();
	aideMobilitePEIOIn.setContexte(CONTEXTE_REPRISE_EMPLOI);
	aideMobilitePEIOIn.setOrigine("W");
	aideMobilitePEIOIn.setCodeTerritoire("001");
	aideMobilitePEIOIn.setDateActionReclassement(dateUtile.getDateJour().toString());
	aideMobilitePEIOIn.setDateDepot(dateUtile.getDateJour().toString());
	aideMobilitePEIOIn.setNatureContratTravail(demandeurEmploi.getFuturTravail().getTypeContrat());
	aideMobilitePEIOIn.setIntensite(Math.round(demandeurEmploi.getFuturTravail().getNombreHeuresTravailleesSemaine()));
	aideMobilitePEIOIn.setTypeIntensite("Hebdomadaire");
	aideMobilitePEIOIn.setDureePeriodeEmploiOuFormation(demandeurEmploi.getRessourcesFinancieres().getNombreMoisTravaillesDerniersMois());
	aideMobilitePEIOIn.setLieuFormationOuEmploi("France");
	aideMobilitePEIOIn.setDistanceDomicileActionReclassement(Math.round(demandeurEmploi.getFuturTravail().getDistanceKmDomicileTravail() * 1000));
	aideMobilitePEIOIn.setEleveSeulEnfants(!situationFamilialeUtile.isEnCouple(demandeurEmploi) && situationFamilialeUtile.hasPersonnesACharge(demandeurEmploi));
	aideMobilitePEIOIn.setNombreEnfants(situationFamilialeUtile.getNombrePersonnesAChargeAgeInferieureAgeLimite(demandeurEmploi, 21));
	aideMobilitePEIOIn.setNombreEnfantsMoins10Ans(situationFamilialeUtile.getNombrePersonnesAChargeAgeInferieureAgeLimite(demandeurEmploi, 10));
	aideMobilitePEIOIn.setNombreAllersRetours(demandeurEmploi.getFuturTravail().getNombreTrajetsDomicileTravail());
	aideMobilitePEIOIn.setNombreRepas(getNombreRepas(demandeurEmploi));
	aideMobilitePEIOIn.setNombreNuitees(getNombreNuitees(demandeurEmploi));

	return aideMobilitePEIOIn;
    }

    //TODO JLA ??
    private int getNombreNuitees(DemandeurEmploi demandeurEmploi) {
	int nombreJoursIndemnises = getNombrejoursIndemnises(demandeurEmploi.getFuturTravail().getNombreHeuresTravailleesSemaine());
	if (nombreJoursIndemnises - 1 > 0) {
	    return nombreJoursIndemnises - 1;
	} else {
	    return 0;
	}
    }

    //TODO JLA ??
    private int getNombreRepas(DemandeurEmploi demandeurEmploi) {
	float nombreHeuresHebdoTravaillees = demandeurEmploi.getFuturTravail().getNombreHeuresTravailleesSemaine();
	int nombreRepas = 0;
	for (NombreJourIndemniseEnum nombresJoursTravailles : NombreJourIndemniseEnum.values()) {
	    if (nombreHeuresHebdoTravaillees >= nombresJoursTravailles.getNombreHeuresMinHebdo()
		    && nombreHeuresHebdoTravaillees <= nombresJoursTravailles.getNombreHeuresMaxHebdo()) {
		nombreRepas = nombresJoursTravailles.getNombreRepasIndemnises();
	    }
	}
	return nombreRepas;
    }

    private int getNombrejoursIndemnises(float nombreHeuresHebdoTravaillees) {
	for (NombreJourIndemniseEnum nombresJoursTravailles : NombreJourIndemniseEnum.values()) {
	    if (nombreHeuresHebdoTravaillees >= nombresJoursTravailles.getNombreHeuresMinHebdo()
		    && nombreHeuresHebdoTravaillees <= nombresJoursTravailles.getNombreHeuresMaxHebdo()) {
		return nombresJoursTravailles.getNombreRepasIndemnises();
	    }
	}
	return NOMBRE_MAX_JOURS_INDEMNISES;
    }
}
