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
    //
    //    @Autowired
    //    private DateUtile dateUtile;

    public static final float SOLDE_PREVISIONNEL_RELIQUAT = 10;

    private float joursIndemnisables;
    private float nombreJoursRestants;
    private float montantComplementARE;

    private static final Logger LOGGER = LoggerFactory.getLogger(AreUtile.class);

    public void simulerAideEtGetReliquat(Map<String, Aide> aidesPourCeMois, DemandeurEmploi demandeurEmploi, int numeroMoisSimule, LocalDate moisSimule) {

	if (isMoisCalculARE(numeroMoisSimule)) {
	    calculerMontantComplementARE(aidesPourCeMois, demandeurEmploi);
	} else {
	    verserReliquatComplementARE(aidesPourCeMois);
	}
    }

    private boolean isMoisCalculARE(int numeroMoisSimule) {
	return numeroMoisSimule == 1;
    }

    private void calculerMontantComplementARE(Map<String, Aide> aidesPourCeMois, DemandeurEmploi demandeurEmploi) {
	String bearerToken = accesTokenUtile.getBearerToken(demandeurEmploi.getPeConnectAuthorization().getAccessToken());
	ArePEIOIn areIn = remplirAreIn(demandeurEmploi);
	Optional<ArePEIOOut> optionalAreOut = poleEmploiIOClient.callAreEndPoint(areIn, bearerToken);
	if (optionalAreOut.isPresent() && optionalAreOut.get().getAllocationMensuelle() > 0) {
	    ArePEIOOut areOut = optionalAreOut.get();
	    this.montantComplementARE = (float) Math.floor(areOut.getAllocationMensuelle() - (areOut.getMontantCRC() + areOut.getMontantCRDS() + areOut.getMontantCSG()));
	    this.nombreJoursRestants = demandeurEmploi.getRessourcesFinancieres().getAidesPoleEmploi().getAllocationARE().getNombreJoursRestants();
	    float soldePrevisionnelReliquat = areOut.getSoldePrevisionnelReliquat();
	    this.joursIndemnisables = this.nombreJoursRestants - soldePrevisionnelReliquat;
	    this.nombreJoursRestants -= this.joursIndemnisables;
	    Aide complementARE = creerComplementARE(montantComplementARE);
	    aidesPourCeMois.put(Aides.ALLOCATION_RETOUR_EMPLOI.getCode(), complementARE);
	} else {
	    LOGGER.error(LoggerMessages.USER_INFO_KO.getMessage());
	}
    }

    private void verserReliquatComplementARE(Map<String, Aide> aidesPourCeMois) {
	this.nombreJoursRestants = getNombreJoursRestantsReliquat();
	if (nombreJoursRestants > 0) {
	    Aide complementARE = creerComplementARE(this.montantComplementARE);
	    aidesPourCeMois.put(Aides.ALLOCATION_RETOUR_EMPLOI.getCode(), complementARE);
	} else {
	    float nombreJoursRestantsAvantCeMois = getNombreJoursRestantsReliquatAvantCeMois();
	    if (nombreJoursRestantsAvantCeMois > 0) {
		float montantReliquatComplementARE = (float) Math.floor((this.montantComplementARE / this.joursIndemnisables) * nombreJoursRestantsAvantCeMois);
		Aide complementARE = creerComplementARE(montantReliquatComplementARE);
		aidesPourCeMois.put(Aides.ALLOCATION_RETOUR_EMPLOI.getCode(), complementARE);
	    }
	}
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

    private float getNombreJoursRestantsReliquat() {
	return this.nombreJoursRestants - this.joursIndemnisables;
    }

    private float getNombreJoursRestantsReliquatAvantCeMois() {
	return this.nombreJoursRestants + this.joursIndemnisables;
    }

    private ArePEIOIn remplirAreIn(DemandeurEmploi demandeurEmploi) {
	ArePEIOIn areIn = new ArePEIOIn();
	if (demandeurEmploi.getRessourcesFinancieres() != null && demandeurEmploi.getRessourcesFinancieres().getAidesPoleEmploi() != null
		&& demandeurEmploi.getRessourcesFinancieres().getAidesPoleEmploi().getAllocationARE() != null) {
	    areIn.setAllocationBruteJournaliere(demandeurEmploi.getRessourcesFinancieres().getAidesPoleEmploi().getAllocationARE().getMontantJournalierBrut());
	    areIn.setSalaireBrutJournalier(demandeurEmploi.getRessourcesFinancieres().getAidesPoleEmploi().getAllocationARE().getSalaireJournalierReferenceBrut());
	    areIn.setGainBrut(demandeurEmploi.getFuturTravail().getSalaire().getMontantBrut());
	} else {
	    LOGGER.error(LoggerMessages.USER_INFO_KO.getMessage());
	}
	return areIn;
    }
}
