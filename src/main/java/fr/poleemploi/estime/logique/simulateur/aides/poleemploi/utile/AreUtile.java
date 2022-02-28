package fr.poleemploi.estime.logique.simulateur.aides.poleemploi.utile;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fr.poleemploi.estime.clientsexternes.poleemploiio.PoleEmploiIOClient;
import fr.poleemploi.estime.clientsexternes.poleemploiio.ressources.ArePEIOOut;
import fr.poleemploi.estime.commun.enumerations.AideEnum;
import fr.poleemploi.estime.commun.enumerations.MessageInformatifEnum;
import fr.poleemploi.estime.commun.enumerations.OrganismeEnum;
import fr.poleemploi.estime.commun.utile.DateUtile;
import fr.poleemploi.estime.logique.simulateur.aides.utile.AideUtile;
import fr.poleemploi.estime.services.ressources.Aide;
import fr.poleemploi.estime.services.ressources.DemandeurEmploi;

@Component
public class AreUtile {

    @Autowired
    private PoleEmploiIOClient poleEmploiIOClient;

    @Autowired
    private DateUtile dateUtile;

    @Autowired
    private AideUtile aideUtile;

    private float joursIndemnisables;
    private float nombreJoursRestants;
    private float montantComplementARE;

    public void simuler(Map<String, Aide> aidesPourCeMois, DemandeurEmploi demandeurEmploi, int numeroMoisSimule) {
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
	Optional<ArePEIOOut> optionalAreOut = poleEmploiIOClient.getAreSimulateurRepriseActivite(demandeurEmploi);
	if (optionalAreOut.isPresent() && optionalAreOut.get().getAllocationMensuelle() > 0) {
	    ArePEIOOut areOut = optionalAreOut.get();
	    this.montantComplementARE = (float) Math.floor(areOut.getAllocationMensuelle() - (areOut.getMontantCRC() + areOut.getMontantCRDS() + areOut.getMontantCSG()));
	    this.nombreJoursRestants = getNombreJoursRestantsRenseigne(demandeurEmploi);
	    this.joursIndemnisables = getNombreJoursIndemnisables(areOut, demandeurEmploi);
	    this.nombreJoursRestants -= this.joursIndemnisables;
	    Aide complementARE = creerComplementARE(montantComplementARE, false);
	    aidesPourCeMois.put(AideEnum.AIDE_RETOUR_EMPLOI.getCode(), complementARE);
	} else {
	    unsetComplementARE();
	}
    }

    private void verserReliquatComplementARE(Map<String, Aide> aidesPourCeMois) {
	this.nombreJoursRestants = getNombreJoursRestantsReliquat();
	if (nombreJoursRestants > 0) {
	    Aide complementARE = creerComplementARE(this.montantComplementARE, false);
	    aidesPourCeMois.put(AideEnum.AIDE_RETOUR_EMPLOI.getCode(), complementARE);
	} else {
	    float nombreJoursRestantsAvantCeMois = getNombreJoursRestantsReliquatAvantCeMois();
	    if (nombreJoursRestantsAvantCeMois > 0) {
		float montantReliquatComplementARE = (float) Math.floor((this.montantComplementARE / this.joursIndemnisables) * nombreJoursRestantsAvantCeMois);
		Aide complementARE = creerComplementARE(montantReliquatComplementARE, true);
		aidesPourCeMois.put(AideEnum.AIDE_RETOUR_EMPLOI.getCode(), complementARE);
	    }
	}
    }

    private Aide creerComplementARE(float montantAide, boolean isDernierMoisComplementARE) {
	if (isDernierMoisComplementARE) {
	    return aideUtile.creerAide(AideEnum.AIDE_RETOUR_EMPLOI, Optional.of(OrganismeEnum.PE), Optional.of(MessageInformatifEnum.FIN_DE_DROIT_ARE.getMessage()), false,
		    montantAide);
	} else {
	    return aideUtile.creerAide(AideEnum.AIDE_RETOUR_EMPLOI, Optional.of(OrganismeEnum.PE), Optional.empty(), false, montantAide);
	}
    }

    private float getNombreJoursIndemnisables(ArePEIOOut areOut, DemandeurEmploi demandeurEmploi) {
	float joursIndemnisablesMois = 0;
	if (areOut.getSoldePrevisionnelReliquat() != null) {
	    joursIndemnisablesMois = areOut.getSoldePrevisionnelReliquat().floatValue();
	} else {
	    float allocationJournaliereBrute = demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesPoleEmploi().getAllocationARE().getAllocationJournaliereBrute();
	    joursIndemnisablesMois = getNombreJoursIndemnisablesCalcule(getMontantAllocationARE(areOut), allocationJournaliereBrute);
	}
	return joursIndemnisablesMois;
    }

    private float getNombreJoursRestantsRenseigne(DemandeurEmploi demandeurEmploi) {
	float nombreJoursRestantsRenseigne = 0;
	if (demandeurEmploi != null && demandeurEmploi.getRessourcesFinancieresAvantSimulation() != null
		&& demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesPoleEmploi() != null
		&& demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesPoleEmploi().getAllocationARE() != null)
	    nombreJoursRestantsRenseigne = demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesPoleEmploi().getAllocationARE().getNombreJoursRestants();
	return nombreJoursRestantsRenseigne;
    }

    private float getNombreJoursRestantsReliquat() {
	return this.nombreJoursRestants - this.joursIndemnisables;
    }

    private float getNombreJoursRestantsReliquatAvantCeMois() {
	return this.nombreJoursRestants + this.joursIndemnisables;
    }

    private int getNombreJoursIndemnisablesCalcule(float montantAllocationARE, float allocationJournaliereBrute) {
	int nombreJoursIndemnisables = 0;
	if (montantAllocationARE > 0 && allocationJournaliereBrute > 0) {
	    nombreJoursIndemnisables = Math.round(montantAllocationARE / allocationJournaliereBrute);
	}
	return nombreJoursIndemnisables;
    }

    private float getMontantAllocationARE(ArePEIOOut areOut) {
	return (float) Math.floor(areOut.getAllocationMensuelle() - (areOut.getMontantCRC() + areOut.getMontantCRDS() + areOut.getMontantCSG()));
    }

    public float calculerMontantAreAvantSimulation(DemandeurEmploi demandeurEmploi, LocalDate mois) {
	int nombreJoursDansLeMois = dateUtile.getNombreJoursDansLeMois(mois);
	if (demandeurEmploi.getRessourcesFinancieresAvantSimulation() != null && demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesPoleEmploi() != null
		&& demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesPoleEmploi().getAllocationARE() != null
		&& demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesPoleEmploi().getAllocationARE().getAllocationJournaliereBrute() != null) {
	    float montantJournalierBrutAre = demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesPoleEmploi().getAllocationARE().getAllocationJournaliereBrute();
	    return BigDecimal.valueOf(nombreJoursDansLeMois).multiply(BigDecimal.valueOf(montantJournalierBrutAre)).setScale(0, RoundingMode.DOWN).floatValue();
	}
	return 0;
    }

    private void unsetComplementARE() {
	this.montantComplementARE = 0;
	this.nombreJoursRestants = 0;
	this.joursIndemnisables = 0;
    }
}
