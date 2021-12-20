package fr.poleemploi.estime.logique.simulateur.aides.poleemploi.utile;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fr.poleemploi.estime.commun.enumerations.Aides;
import fr.poleemploi.estime.commun.enumerations.MessagesInformatifs;
import fr.poleemploi.estime.commun.enumerations.Organismes;
import fr.poleemploi.estime.commun.utile.DateUtile;
import fr.poleemploi.estime.commun.utile.demandeuremploi.FuturTravailUtile;
import fr.poleemploi.estime.commun.utile.demandeuremploi.PeriodeTravailleeAvantSimulationUtile;
import fr.poleemploi.estime.logique.simulateur.aides.utile.AideUtile;
import fr.poleemploi.estime.services.ressources.Aide;
import fr.poleemploi.estime.services.ressources.DemandeurEmploi;

@Component
public class AllocationSolidariteSpecifiqueUtile {

    private static final int NOMBRE_MOIS_MAX_ASS_ELIGIBLE = 3;

    @Autowired
    private AideUtile aideeUtile;

    @Autowired
    private DateUtile dateUtile;

    @Autowired
    private FuturTravailUtile futurTravailUtile;

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
	if (demandeurEmploi.getRessourcesFinancieres() != null && demandeurEmploi.getRessourcesFinancieres().getAidesPoleEmploi() != null
		&& demandeurEmploi.getRessourcesFinancieres().getAidesPoleEmploi().getAllocationASS() != null
		&& demandeurEmploi.getRessourcesFinancieres().getAidesPoleEmploi().getAllocationASS().getAllocationJournaliereNet() != null) {
	    float montantJournalierNetSolidariteSpecifique = demandeurEmploi.getRessourcesFinancieres().getAidesPoleEmploi().getAllocationASS().getAllocationJournaliereNet();
	    return BigDecimal.valueOf(nombreJoursDansLeMois).multiply(BigDecimal.valueOf(montantJournalierNetSolidariteSpecifique)).setScale(0, RoundingMode.DOWN).floatValue();
	}
	return 0;
    }

    public boolean isEligible(int numeroMoisSimule, DemandeurEmploi demandeurEmploi) {
	return numeroMoisSimule <= getNombreMoisEligibles(demandeurEmploi);
    }

    public int getNombreMoisEligibles(DemandeurEmploi demandeurEmploi) {
	int nombreMoisCumulesASSPercueEtSalaire = periodeTravailleeAvantSimulationUtile.getNombreMoisTravaillesAuCoursDes3DerniersMoisAvantSimulation(demandeurEmploi);
	if (futurTravailUtile.hasContratCDI(demandeurEmploi.getFuturTravail())) {
	    return getNombreMoisEligiblesCDI(nombreMoisCumulesASSPercueEtSalaire);
	} else if (futurTravailUtile.hasContratCDD(demandeurEmploi.getFuturTravail())) {
	    return getNombreMoisEligiblesCDD(demandeurEmploi, nombreMoisCumulesASSPercueEtSalaire);
	}
	return 0;
    }

    private Aide creerAide(DemandeurEmploi demandeurEmploi, LocalDate dateDebutSimulation, float montantAide) {
	Aide ass = new Aide();
	ass.setCode(Aides.ALLOCATION_SOLIDARITE_SPECIFIQUE.getCode());
	Optional<String> detailAideOptional = aideeUtile.getDescription(Aides.ALLOCATION_SOLIDARITE_SPECIFIQUE.getNomFichierDetail());
	if (detailAideOptional.isPresent()) {
	    ass.setDetail(detailAideOptional.get());
	}
	Optional<String> messageAlerteOptional = getMessageAlerte(demandeurEmploi, dateDebutSimulation);
	if (messageAlerteOptional.isPresent()) {
	    ass.setMessageAlerte(messageAlerteOptional.get());
	}
	ass.setMontant(montantAide);
	ass.setNom(Aides.ALLOCATION_SOLIDARITE_SPECIFIQUE.getNom());
	ass.setOrganisme(Organismes.PE.getNom());
	ass.setReportee(false);
	return ass;
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
    private Optional<String> getMessageAlerte(DemandeurEmploi demandeurEmploi, LocalDate dateDebutSimulation) {
	LocalDate dateDerniereOuvertureDroitASS = demandeurEmploi.getRessourcesFinancieres().getAidesPoleEmploi().getAllocationASS().getDateDerniereOuvertureDroit();
	LocalDate dateFinDroitASS = dateUtile.ajouterMoisALocalDate(dateDerniereOuvertureDroitASS, 6);
	LocalDate date3emeMoisSimulation = dateUtile.ajouterMoisALocalDate(dateDebutSimulation, 3);
	if (dateUtile.isDateAvant(dateFinDroitASS, date3emeMoisSimulation)) {
	    return Optional.of(MessagesInformatifs.ASS_DEMANDE_RENOUVELLEMENT.getMessage());
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
}
