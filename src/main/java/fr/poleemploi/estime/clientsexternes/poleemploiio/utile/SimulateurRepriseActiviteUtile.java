package fr.poleemploi.estime.clientsexternes.poleemploiio.utile;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import fr.poleemploi.estime.clientsexternes.poleemploiio.ressources.ArePEIOIn;
import fr.poleemploi.estime.services.ressources.DemandeurEmploi;

@Component
public class SimulateurRepriseActiviteUtile {

    public HttpEntity<ArePEIOIn> createHttpEntityAre(DemandeurEmploi demandeurEmploi) {
	HttpHeaders headers = new HttpHeaders();
	headers.add("Authorization", demandeurEmploi.getPeConnectAuthorization().getBearerToken());
	headers.setContentType(MediaType.APPLICATION_JSON);
	return new HttpEntity<>(createArePEIOIn(demandeurEmploi), headers);
    }

    private ArePEIOIn createArePEIOIn(DemandeurEmploi demandeurEmploi) {
	ArePEIOIn areIn = new ArePEIOIn();
	//TODO JLA contrôle à déporter dans controleur couche service
	if (demandeurEmploi.getRessourcesFinancieresAvantSimulation() != null && demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesPoleEmploi() != null
		&& demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesPoleEmploi().getAllocationARE() != null) {
	    areIn.setAllocationBruteJournaliere(demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesPoleEmploi().getAllocationARE().getAllocationJournaliereBrute());
	    areIn.setSalaireBrutJournalier(demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesPoleEmploi().getAllocationARE().getSalaireJournalierReferenceBrut());
	    areIn.setGainBrut(demandeurEmploi.getFuturTravail().getSalaire().getMontantBrut());
	}
	return areIn;
    }
}
