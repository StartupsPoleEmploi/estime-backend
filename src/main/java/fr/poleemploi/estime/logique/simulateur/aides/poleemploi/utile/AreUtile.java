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
import fr.poleemploi.estime.commun.utile.demandeuremploi.BeneficiaireAidesUtile;
import fr.poleemploi.estime.commun.utile.demandeuremploi.PeriodeTravailleeAvantSimulationUtile;
import fr.poleemploi.estime.logique.simulateur.aides.utile.AideUtile;
import fr.poleemploi.estime.logique.simulateur.aides.utile.SimulateurAidesUtile;
import fr.poleemploi.estime.services.ressources.Aide;
import fr.poleemploi.estime.services.ressources.DemandeurEmploi;

@Component
public class AreUtile {

    @Autowired
    private DateUtile dateUtile;

    @Autowired
    private AideUtile aideUtile;

    @Autowired
    private PeriodeTravailleeAvantSimulationUtile periodeTravailleeAvantSimulationUtile;

    @Autowired
    private SimulateurAidesUtile simulateurAidesUtile;

    @Autowired
    private BeneficiaireAidesUtile beneficiaireAidesUtile;

    private static final float ARE_MINI = 29.56f;
    private static final float SEUIL_CSG_CRDS = 53f;
    private static final float TAUX_DEDUCTION_CRC = 0.03f;
    private static final float TAUX_DEDUCTION_CSG = 0.9825f * 0.062f;
    private static final float TAUX_DEDUCTION_CRDS = 0.9825f * 0.005f;

    public void reporterARE(Map<String, Aide> aidesPourCeMois, DemandeurEmploi demandeurEmploi, int numeroMoisSimule, LocalDate dateDebutSimulation) {
	float montantMensuelARE = calculerMontantMensuelARE(demandeurEmploi, numeroMoisSimule, dateDebutSimulation);
	float nombreJoursRestantsApresPremierMois = getNombreJoursRestantsApresPremierMois(demandeurEmploi, dateDebutSimulation);
	Aide are = creerARE(montantMensuelARE, nombreJoursRestantsApresPremierMois);
	aidesPourCeMois.put(AideEnum.AIDE_RETOUR_EMPLOI.getCode(), are);
    }

    public float calculerMontantMensuelARE(DemandeurEmploi demandeurEmploi, int numeroMoisSimule, LocalDate dateDebutSimulation) {
	float allocationJournaliereBrute = demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesPoleEmploi().getAllocationARE().getAllocationJournaliereBrute();
	float sjr = demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesPoleEmploi().getAllocationARE().getSalaireJournalierReferenceBrut();
	int nombreJoursRestants = getNombreJoursRestantsARE(demandeurEmploi, numeroMoisSimule, dateDebutSimulation);
	float allocationJournaliereNette = calculerAllocationJournaliereNetteARE(allocationJournaliereBrute, sjr);

	return (float) Math.floor(allocationJournaliereNette * nombreJoursRestants);
    }

    private float calculerAllocationJournaliereNetteARE(float allocationJournaliereBrute, float sjr) {
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
	return (allocationJournaliereBrute - deductions);
    }

    private int getNombreJoursRestantsARE(DemandeurEmploi demandeurEmploi, int numeroMoisSimule, LocalDate dateDebutSimulation) {
	int nombreJoursDansLeMois = dateUtile.getNombreJoursDansLeMois(dateUtile.ajouterMoisALocalDate(dateDebutSimulation, numeroMoisSimule - 1));
	int nombreJoursRestantsRenseigne = getNombreJoursRestantsRenseigne(demandeurEmploi);

	return nombreJoursRestantsRenseigne < nombreJoursDansLeMois ? nombreJoursRestantsRenseigne : nombreJoursDansLeMois;
    }

    public Aide creerARE(float montantAide, float nombreJoursRestantsARE) {
	ArrayList<String> messagesAlerte = new ArrayList<>();
	messagesAlerte.add(MessageInformatifEnum.MONTANT_ARE_AVANT_PAS.getMessage());
	if (nombreJoursRestantsARE <= 0) {
	    messagesAlerte.add(MessageInformatifEnum.FIN_DE_DROIT_ARE.getMessage());
	}
	return aideUtile.creerAide(AideEnum.AIDE_RETOUR_EMPLOI, Optional.of(OrganismeEnum.PE), Optional.of(messagesAlerte), false, montantAide);
    }

    public Aide creerComplementARE(float montantAide, float nombreJoursRestantsARE) {
	ArrayList<String> messagesAlerte = new ArrayList<>();
	messagesAlerte.add(MessageInformatifEnum.ACTUALISATION_ARE.getMessage());
	if (nombreJoursRestantsARE <= 0) {
	    messagesAlerte.add(MessageInformatifEnum.FIN_DE_DROIT_ARE.getMessage());
	}
	return aideUtile.creerAide(AideEnum.COMPLEMENT_AIDE_RETOUR_EMPLOI, Optional.of(OrganismeEnum.PE), Optional.of(messagesAlerte), false, montantAide);
    }

    private int getNombreJoursRestantsRenseigne(DemandeurEmploi demandeurEmploi) {
	int nombreJoursRestantsRenseigne = 0;
	if (demandeurEmploi != null && demandeurEmploi.getRessourcesFinancieresAvantSimulation() != null
		&& demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesPoleEmploi() != null
		&& demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesPoleEmploi().getAllocationARE() != null)
	    nombreJoursRestantsRenseigne = demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesPoleEmploi().getAllocationARE().getNombreJoursRestants();
	return nombreJoursRestantsRenseigne;
    }

    public float getNombreJoursRestantsApresPremierMois(DemandeurEmploi demandeurEmploi, LocalDate dateDebutSimulation) {
	int nombreJoursDansLeMois = 0;
	if (!periodeTravailleeAvantSimulationUtile.hasSalairesAvantPeriodeSimulation(demandeurEmploi, 0)) {
	    nombreJoursDansLeMois = dateUtile.getNombreJoursDansLeMois(dateDebutSimulation);
	}
	float nombreJoursRestants = getNombreJoursRestantsRenseigne(demandeurEmploi);

	return Math.max(0f, nombreJoursRestants - nombreJoursDansLeMois);
    }

    public float calculerMontantAreAvantSimulation(DemandeurEmploi demandeurEmploi, LocalDate mois) {

	int nombreJoursDansLeMois = dateUtile.getNombreJoursDansLeMois(mois);
	float allocationJournaliereBrute = demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesPoleEmploi().getAllocationARE().getAllocationJournaliereBrute();
	float sjr = demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesPoleEmploi().getAllocationARE().getSalaireJournalierReferenceBrut();

	float allocationJournaliereNette = calculerAllocationJournaliereNetteARE(allocationJournaliereBrute, sjr);
	return BigDecimal.valueOf(nombreJoursDansLeMois).multiply(BigDecimal.valueOf(allocationJournaliereNette)).setScale(0, RoundingMode.DOWN).floatValue();
    }

    public int numeroMoisPassageAuComplementARE(DemandeurEmploi demandeurEmploi) {
	return periodeTravailleeAvantSimulationUtile.hasSalairesAvantPeriodeSimulation(demandeurEmploi, 0) ? 0 : 1;
    }

    public boolean isAreAReporter(DemandeurEmploi demandeurEmploi, int numeroMoisSimule) {
	if (!periodeTravailleeAvantSimulationUtile.hasSalairesAvantPeriodeSimulation(demandeurEmploi, 0)) {
	    return simulateurAidesUtile.isPremierMois(numeroMoisSimule) && beneficiaireAidesUtile.isBeneficiaireARE(demandeurEmploi);
	}
	return false;
    }

    public boolean isComplementAREAVerser(DemandeurEmploi demandeurEmploi, int numeroMoisSimule) {
	return !isAreAReporter(demandeurEmploi, numeroMoisSimule) && beneficiaireAidesUtile.isBeneficiaireARE(demandeurEmploi);
    }
}
