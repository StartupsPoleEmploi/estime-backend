package fr.poleemploi.estime.logique.simulateur.aides.poleemploi.utile;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fr.poleemploi.estime.commun.enumerations.AideEnum;
import fr.poleemploi.estime.commun.enumerations.MessageInformatifEnum;
import fr.poleemploi.estime.commun.enumerations.OrganismeEnum;
import fr.poleemploi.estime.commun.utile.DateUtile;
import fr.poleemploi.estime.commun.utile.demandeuremploi.FuturTravailUtile;
import fr.poleemploi.estime.commun.utile.demandeuremploi.InformationsPersonnellesUtile;
import fr.poleemploi.estime.commun.utile.demandeuremploi.PeriodeTravailleeAvantSimulationUtile;
import fr.poleemploi.estime.logique.simulateur.aides.utile.AideUtile;
import fr.poleemploi.estime.services.ressources.Aide;
import fr.poleemploi.estime.services.ressources.DemandeurEmploi;

@Component
public class AllocationSolidariteSpecifiqueUtile {

    private static final int NOMBRE_MOIS_MAX_ASS_ELIGIBLE = 4;
    private static final int NOMBRE_MOIS_MAX_ASS_BENEFICIAIRE_ACRE_ELIGIBLE = 6;

    @Autowired
    private AideUtile aideUtile;

    @Autowired
    private DateUtile dateUtile;

    @Autowired
    private FuturTravailUtile futurTravailUtile;

    @Autowired
    private InformationsPersonnellesUtile informationsPersonnellesUtile;

    @Autowired
    private PeriodeTravailleeAvantSimulationUtile periodeTravailleeAvantSimulationUtile;

    public Optional<Aide> simulerAide(DemandeurEmploi demandeurEmploi, LocalDate moisSimule, LocalDate dateDebutSimulation) {
	float montantASS = calculerMontant(demandeurEmploi, moisSimule);
	if (montantASS > 0) {
	    Aide aideAllocationSolidariteSpecifique = creerAide(demandeurEmploi, dateDebutSimulation, montantASS);
	    return Optional.of(aideAllocationSolidariteSpecifique);
	}
	return Optional.empty();
    }

    public float calculerMontant(DemandeurEmploi demandeurEmploi, LocalDate mois) {
	int nombreJoursDansLeMois = dateUtile.getNombreJoursDansLeMois(mois);
	if (demandeurEmploi.getRessourcesFinancieresAvantSimulation() != null && demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesPoleEmploi() != null
		&& demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesPoleEmploi().getAllocationASS() != null
		&& demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesPoleEmploi().getAllocationASS().getAllocationJournaliereNet() != null) {
	    float montantJournalierNetSolidariteSpecifique = demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesPoleEmploi().getAllocationASS()
		    .getAllocationJournaliereNet();
	    return BigDecimal.valueOf(nombreJoursDansLeMois).multiply(BigDecimal.valueOf(montantJournalierNetSolidariteSpecifique)).setScale(0, RoundingMode.DOWN).floatValue();
	}
	return 0;
    }

    public boolean isEligible(int numeroMoisSimule, DemandeurEmploi demandeurEmploi, LocalDate dateDebutSimulation) {
	return numeroMoisSimule <= getNombreMoisEligibles(demandeurEmploi, dateDebutSimulation);
    }

    public int getNombreMoisEligibles(DemandeurEmploi demandeurEmploi, LocalDate dateDebutSimulation) {
	int nombreMoisCumulesASSPercueEtSalaire = periodeTravailleeAvantSimulationUtile.getNombreMoisTravaillesAuCoursDes3DerniersMoisAvantSimulation(demandeurEmploi);
	if (informationsPersonnellesUtile.isBeneficiaireACRE(demandeurEmploi)
		&& informationsPersonnellesUtile.getNombreMoisDepuisCreationEntreprise(demandeurEmploi, dateDebutSimulation) <= 12) {
	    return getNombreMoisEligiblesBeneficiaireACRE(informationsPersonnellesUtile.getNombreMoisDepuisCreationEntreprise(demandeurEmploi, dateDebutSimulation));
	} else {
	    if (futurTravailUtile.hasContratCDI(demandeurEmploi.getFuturTravail())) {
		return getNombreMoisEligiblesCDI(nombreMoisCumulesASSPercueEtSalaire);
	    } else if (futurTravailUtile.hasContratDureeDeterminee(demandeurEmploi.getFuturTravail())) {
		return getNombreMoisEligiblesCDD(demandeurEmploi, nombreMoisCumulesASSPercueEtSalaire);
	    }
	}
	return 0;
    }

    private Aide creerAide(DemandeurEmploi demandeurEmploi, LocalDate dateDebutSimulation, float montantAide) {
	return aideUtile.creerAide(AideEnum.ALLOCATION_SOLIDARITE_SPECIFIQUE, Optional.of(OrganismeEnum.PE), getMessageAlerte(demandeurEmploi, dateDebutSimulation), false,
		montantAide);
    }

    /**
     * Méthode permettant de récupérer un message d'alerte sur l'aide ASS.
     * Date de la dernière ouverture de droit à l'ASS  + 6 mois = date de fin  de droit
     * Si date de fin  de droit avant M3 de la synthèse de résultat il faut créer un message d'alerte.
     * 
     * @param demandeurEmploi
     * @param dateDebutSimulation
     * @return message d'alerte sinon vide
     */
    private Optional<List<String>> getMessageAlerte(DemandeurEmploi demandeurEmploi, LocalDate dateDebutSimulation) {
	LocalDate dateDerniereOuvertureDroitASS = demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesPoleEmploi().getAllocationASS().getDateDerniereOuvertureDroit();
	LocalDate dateFinDroitASS = dateUtile.ajouterMoisALocalDate(dateDerniereOuvertureDroitASS, 6);
	LocalDate date3emeMoisSimulation = dateUtile.ajouterMoisALocalDate(dateDebutSimulation, 3);
	if (dateUtile.isDateAvant(dateFinDroitASS, date3emeMoisSimulation)) {
	    ArrayList<String> messagesAlerte = new ArrayList<>();
	    messagesAlerte.add(MessageInformatifEnum.ASS_DEMANDE_RENOUVELLEMENT.getMessage());
	    return Optional.of(messagesAlerte);
	}
	return Optional.empty();
    }

    private int getNombreMoisEligiblesCDI(int nombreMoisCumulesASSPercueEtSalaire) {
	return NOMBRE_MOIS_MAX_ASS_ELIGIBLE - nombreMoisCumulesASSPercueEtSalaire;
    }

    private int getNombreMoisEligiblesCDD(DemandeurEmploi demandeurEmploi, int nombreMoisCumulesAssEtSalaire) {
	int dureeContratCDDEnMois = demandeurEmploi.getFuturTravail().getNombreMoisContratCDD();
	if (dureeContratCDDEnMois >= NOMBRE_MOIS_MAX_ASS_ELIGIBLE) {
	    return NOMBRE_MOIS_MAX_ASS_ELIGIBLE - nombreMoisCumulesAssEtSalaire;
	} else {
	    int nombreMoisEligibles = dureeContratCDDEnMois - nombreMoisCumulesAssEtSalaire;
	    if (nombreMoisEligibles > 0) {
		return nombreMoisEligibles;
	    }
	}
	return 0;
    }

    private int getNombreMoisEligiblesBeneficiaireACRE(int nombreMoisDepuisCreationEntreprise) {
	if (nombreMoisDepuisCreationEntreprise > 6) {
	    int nombreDeMoisApres6PremiersMois = nombreMoisDepuisCreationEntreprise - 6;
	    return NOMBRE_MOIS_MAX_ASS_BENEFICIAIRE_ACRE_ELIGIBLE - nombreDeMoisApres6PremiersMois;
	}
	return NOMBRE_MOIS_MAX_ASS_BENEFICIAIRE_ACRE_ELIGIBLE;
    }
}
