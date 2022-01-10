package fr.poleemploi.estime.clientsexternes.poleemploiio.utile;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import fr.poleemploi.estime.clientsexternes.poleemploiio.ressources.simulateuraides.agepi.AgepiPEIOIn;
import fr.poleemploi.estime.commun.utile.DateUtile;
import fr.poleemploi.estime.commun.utile.demandeuremploi.SituationFamilialeUtile;
import fr.poleemploi.estime.services.ressources.DemandeurEmploi;

@Component
public class SimulateurAidesAgepiUtile {

    @Autowired
    private DateUtile dateUtile;

    @Autowired
    private SituationFamilialeUtile situationFamilialeUtile;

    private static final String CONTEXTE_REPRISE_EMPLOI = "reprise";

    public HttpEntity<AgepiPEIOIn> createHttpEntityAgepiSimulateurAides(DemandeurEmploi demandeurEmploi) {
	HttpHeaders headers = new HttpHeaders();
	headers.setContentType(MediaType.APPLICATION_JSON);
	headers.add("Authorization", demandeurEmploi.getPeConnectAuthorization().getBearerToken());
	AgepiPEIOIn agepiPEIOIn = createAgepiPEIOIn(demandeurEmploi);
	return new HttpEntity<AgepiPEIOIn>(agepiPEIOIn, headers);
    }

    private AgepiPEIOIn createAgepiPEIOIn(DemandeurEmploi demandeurEmploi) {
	AgepiPEIOIn agepiPEIOIn = new AgepiPEIOIn();
	agepiPEIOIn.setContexte(CONTEXTE_REPRISE_EMPLOI);
	agepiPEIOIn.setOrigine("W");
	agepiPEIOIn.setCodeTerritoire("001");
	agepiPEIOIn.setDateActionReclassement(dateUtile.getDateJour().toString());
	agepiPEIOIn.setDateDepot(dateUtile.getDateJour().toString());
	agepiPEIOIn.setNatureContratTravail(demandeurEmploi.getFuturTravail().getTypeContrat());
	agepiPEIOIn.setIntensite(Math.round(demandeurEmploi.getFuturTravail().getNombreHeuresTravailleesSemaine()));
	agepiPEIOIn.setTypeIntensite("Hebdomadaire");
	agepiPEIOIn.setDureePeriodeEmploiOuFormation(demandeurEmploi.getRessourcesFinancieres().getNombreMoisTravaillesDerniersMois());
	agepiPEIOIn.setLieuFormationOuEmploi("France");
	agepiPEIOIn.setEleveSeulEnfants(!situationFamilialeUtile.isEnCouple(demandeurEmploi));
	agepiPEIOIn.setNombreEnfants(situationFamilialeUtile.getNombrePersonnesAChargeAgeInferieureAgeLimite(demandeurEmploi, 21));
	agepiPEIOIn.setNombreEnfantsMoins10Ans(situationFamilialeUtile.getNombrePersonnesAChargeAgeInferieureAgeLimite(demandeurEmploi, 10));

	return agepiPEIOIn;
    }
}
