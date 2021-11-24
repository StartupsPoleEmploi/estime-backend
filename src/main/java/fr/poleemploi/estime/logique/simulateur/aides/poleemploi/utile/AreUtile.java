package fr.poleemploi.estime.logique.simulateur.aides.poleemploi.utile;

import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fr.poleemploi.estime.clientsexternes.poleemploiio.PoleEmploiIOClient;
import fr.poleemploi.estime.clientsexternes.poleemploiio.ressources.ArePEIOIn;
import fr.poleemploi.estime.clientsexternes.poleemploiio.ressources.ArePEIOOut;
import fr.poleemploi.estime.commun.enumerations.Aides;
import fr.poleemploi.estime.commun.enumerations.Organismes;
import fr.poleemploi.estime.commun.enumerations.exceptions.LoggerMessages;
import fr.poleemploi.estime.commun.utile.AccesTokenUtile;
import fr.poleemploi.estime.commun.utile.DateUtile;
import fr.poleemploi.estime.logique.simulateur.aides.utile.AideUtile;
import fr.poleemploi.estime.services.ressources.Aide;
import fr.poleemploi.estime.services.ressources.DemandeurEmploi;

@Component
public class AreUtile {
    @Autowired
    private AccesTokenUtile accesTokenUtile;

    @Autowired
    private AideUtile aideUtile;

    @Autowired
    private PoleEmploiIOClient poleEmploiIOClient;

    @Autowired
    private DateUtile dateUtile;

    private float nouveauReliquat;

    private static final Logger LOGGER = LoggerFactory.getLogger(AreUtile.class);

    public void simulerAideEtGetReliquat(Map<String, Aide> aidesPourCeMois, DemandeurEmploi demandeurEmploi, int numeroMoisSimule, LocalDate moisSimule) {

	if (isMoisCalculARE(numeroMoisSimule)) {
	    this.nouveauReliquat = calculerMontantComplementAREEtGetReliquat(aidesPourCeMois, demandeurEmploi);
	} else {
	    this.nouveauReliquat = verserReliquatComplementAREEtGetReliquat(aidesPourCeMois, demandeurEmploi, moisSimule, this.nouveauReliquat);
	}
	System.out.println(this.nouveauReliquat);
    }

    private boolean isMoisCalculARE(int numeroMoisSimule) {
	return numeroMoisSimule == 1;
    }

    private float calculerMontantComplementAREEtGetReliquat(Map<String, Aide> aidesPourCeMois, DemandeurEmploi demandeurEmploi) {
	String bearerToken = accesTokenUtile.getBearerToken(demandeurEmploi.getPeConnectAuthorization().getAccessToken());
	ArePEIOIn areIn = remplirAreIn(demandeurEmploi);
	Optional<ArePEIOOut> optionalAreOut = poleEmploiIOClient.callAreEndPoint(areIn, bearerToken);

	if (optionalAreOut.isPresent() && optionalAreOut.get().getAllocationMensuelle() > 0) {
	    System.out.println(optionalAreOut.get().toString());
	    Aide complementARE = creerComplementARE(optionalAreOut.get().getAllocationMensuelle());
	    aidesPourCeMois.put(Aides.ALLOCATION_RETOUR_EMPLOI.getCode(), complementARE);
	    return 100;
	} else {
	    LOGGER.error(LoggerMessages.USER_INFO_KO.getMessage());
	}
	return 0;
    }

    private float verserReliquatComplementAREEtGetReliquat(Map<String, Aide> aidesPourCeMois, DemandeurEmploi demandeurEmploi, LocalDate moisSimule, float reliquatMoisPrecedent) {
	int nombreJoursDansLeMois = dateUtile.getNombreJoursDansLeMois(moisSimule);

	return reliquatMoisPrecedent - nombreJoursDansLeMois;
    }

    private Aide creerComplementARE(float montantAide) {
	Aide are = new Aide();
	are.setCode(Aides.ALLOCATION_RETOUR_EMPLOI.getCode());
	Optional<String> detailAideOptional = aideUtile.getDescription(Aides.ALLOCATION_RETOUR_EMPLOI.getNomFichierDetail());
	if (detailAideOptional.isPresent()) {
	    are.setDetail(detailAideOptional.get());
	}
	are.setNom(Aides.ALLOCATION_RETOUR_EMPLOI.getNom());
	are.setOrganisme(Organismes.PE.getNom());
	are.setMontant(montantAide);
	are.setReportee(false);
	return are;
    }

    private ArePEIOIn remplirAreIn(DemandeurEmploi demandeurEmploi) {
	ArePEIOIn areIn = new ArePEIOIn();
	areIn.setAllocationBruteJournaliere(demandeurEmploi.getRessourcesFinancieres().getAidesPoleEmploi().getAllocationARE().getMontantJournalierBrut());
	areIn.setSalaireBrutJournalier(demandeurEmploi.getRessourcesFinancieres().getAidesPoleEmploi().getAllocationARE().getSalaireJournalierReferenceBrut());
	areIn.setGainBrut(demandeurEmploi.getFuturTravail().getSalaire().getMontantBrut());
	return areIn;
    }
}
