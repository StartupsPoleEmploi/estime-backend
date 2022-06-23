package fr.poleemploi.estime.logique.simulateur.aides.poleemploi.utile;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
    private DateUtile dateUtile;

    @Autowired
    private AideUtile aideUtile;

    private static final float ARE_MINI = 29.56f;
    private static final float SEUIL_CSG_CRDS = 53f;
    private static final float TAUX_DEDUCTION_CRC = 0.03f;
    private static final float TAUX_DEDUCTION_CSG = 0.9825f * 0.062f;
    private static final float TAUX_DEDUCTION_CRDS = 0.9825f * 0.005f;

    public void reporterARE(Map<String, Aide> aidesPourCeMois, DemandeurEmploi demandeurEmploi, int numeroMoisSimule, LocalDate dateDebutSimulation) {
	float montantMensuelARE = calculerMontantMensuelARE(demandeurEmploi, numeroMoisSimule, dateDebutSimulation);
	Aide are = creerARE(montantMensuelARE);
	aidesPourCeMois.put(AideEnum.AIDE_RETOUR_EMPLOI.getCode(), are);
    }

    public float calculerMontantMensuelARE(DemandeurEmploi demandeurEmploi, int numeroMoisSimule, LocalDate dateDebutSimulation) {
	float allocationJournaliereBrute = demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesPoleEmploi().getAllocationARE().getAllocationJournaliereBrute();
	float sjr = demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesPoleEmploi().getAllocationARE().getSalaireJournalierReferenceBrut();
	int nombreJoursDansLeMois = dateUtile.getNombreJoursDansLeMois(dateUtile.ajouterMoisALocalDate(dateDebutSimulation, numeroMoisSimule - 1));
	float deductions = 0.0f;
	if (allocationJournaliereBrute > ARE_MINI) {
	    deductions = sjr * TAUX_DEDUCTION_CRC;
	    if ((allocationJournaliereBrute - deductions) > SEUIL_CSG_CRDS) {
		deductions += allocationJournaliereBrute * TAUX_DEDUCTION_CSG;
		if ((allocationJournaliereBrute - deductions) > SEUIL_CSG_CRDS) {
		    deductions += allocationJournaliereBrute * TAUX_DEDUCTION_CRDS;
		}
	    }
	}
	return (float) Math.floor((allocationJournaliereBrute - deductions) * nombreJoursDansLeMois);
    }

    public Aide creerARE(float montantAide) {
	ArrayList<String> messagesAlerte = new ArrayList<>();
	messagesAlerte.add(MessageInformatifEnum.MONTANT_ARE_AVANT_PAS.getMessage());
	return aideUtile.creerAide(AideEnum.AIDE_RETOUR_EMPLOI, Optional.of(OrganismeEnum.PE), Optional.of(messagesAlerte), false, montantAide);
    }

    public Aide creerComplementARE(float montantAide, boolean isDernierMoisComplementARE) {
	ArrayList<String> messagesAlerte = new ArrayList<>();
	messagesAlerte.add(MessageInformatifEnum.ACTUALISATION_ARE.getMessage());
	if (isDernierMoisComplementARE) {
	    messagesAlerte.add(MessageInformatifEnum.FIN_DE_DROIT_ARE.getMessage());
	}
	return aideUtile.creerAide(AideEnum.COMPLEMENT_AIDE_RETOUR_EMPLOI, Optional.of(OrganismeEnum.PE), Optional.of(messagesAlerte), false, montantAide);
    }

    private float getNombreJoursRestantsRenseigne(DemandeurEmploi demandeurEmploi) {
	float nombreJoursRestantsRenseigne = 0;
	if (demandeurEmploi != null && demandeurEmploi.getRessourcesFinancieresAvantSimulation() != null
		&& demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesPoleEmploi() != null
		&& demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesPoleEmploi().getAllocationARE() != null)
	    nombreJoursRestantsRenseigne = demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesPoleEmploi().getAllocationARE().getNombreJoursRestants();
	return nombreJoursRestantsRenseigne;
    }

    public float getNombreJoursRestantsApresPremierMois(DemandeurEmploi demandeurEmploi, LocalDate dateDebutSimulation) {
	int nombreJoursDansLeMois = dateUtile.getNombreJoursDansLeMois(dateDebutSimulation);
	float nombreJoursRestants = getNombreJoursRestantsRenseigne(demandeurEmploi);

	return nombreJoursRestants - nombreJoursDansLeMois;
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
}
