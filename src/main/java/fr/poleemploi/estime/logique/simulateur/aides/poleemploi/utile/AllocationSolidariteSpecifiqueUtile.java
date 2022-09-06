package fr.poleemploi.estime.logique.simulateur.aides.poleemploi.utile;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fr.poleemploi.estime.commun.enumerations.AideEnum;
import fr.poleemploi.estime.commun.enumerations.OrganismeEnum;
import fr.poleemploi.estime.commun.utile.DateUtile;
import fr.poleemploi.estime.commun.utile.demandeuremploi.FuturTravailUtile;
import fr.poleemploi.estime.commun.utile.demandeuremploi.InformationsPersonnellesUtile;
import fr.poleemploi.estime.commun.utile.demandeuremploi.PeriodeTravailleeAvantSimulationUtile;
import fr.poleemploi.estime.commun.utile.demandeuremploi.RessourcesFinancieresAvantSimulationUtile;
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

    @Autowired
    private RessourcesFinancieresAvantSimulationUtile ressourcesFinancieresAvantSimulationUtile;

    public Optional<Aide> simulerAide(DemandeurEmploi demandeurEmploi, int numeroMoisSimule, LocalDate dateDebutSimulation) {
	LocalDate dateMoisASimuler = dateUtile.getDateMoisASimuler(dateDebutSimulation, numeroMoisSimule);

	float montantASS = calculerMontant(demandeurEmploi, dateMoisASimuler);
	if (montantASS > 0) {
	    Aide aideAllocationSolidariteSpecifique = creerAide(montantASS);
	    return Optional.of(aideAllocationSolidariteSpecifique);
	}
	return Optional.empty();
    }

    public float calculerMontant(DemandeurEmploi demandeurEmploi, LocalDate mois) {
	int nombreJoursDansLeMois = dateUtile.getNombreJoursDansLeMois(mois);
	if (ressourcesFinancieresAvantSimulationUtile.hasAllocationSolidariteSpecifique(demandeurEmploi)) {
	    float montantJournalierNetSolidariteSpecifique = ressourcesFinancieresAvantSimulationUtile.getAllocationSolidariteSpecifiqueJournaliere(demandeurEmploi);
	    return BigDecimal.valueOf(nombreJoursDansLeMois).multiply(BigDecimal.valueOf(montantJournalierNetSolidariteSpecifique)).setScale(0, RoundingMode.DOWN).floatValue();
	}
	return 0;
    }

    public float calculerMontantSiEligible(DemandeurEmploi demandeurEmploi, int numeroMoisPeriode, LocalDate dateDebutSimulation) {
	int nombreJoursDansLeMois = dateUtile.getNombreJoursDansLeMois(dateUtile.ajouterMoisALocalDate(dateDebutSimulation, numeroMoisPeriode));
	if (isEligible(numeroMoisPeriode + 1, demandeurEmploi, dateDebutSimulation)
		&& ressourcesFinancieresAvantSimulationUtile.hasAllocationSolidariteSpecifique(demandeurEmploi)) {
	    float montantJournalierNetSolidariteSpecifique = ressourcesFinancieresAvantSimulationUtile.getAllocationSolidariteSpecifiqueJournaliere(demandeurEmploi);
	    return BigDecimal.valueOf(nombreJoursDansLeMois).multiply(BigDecimal.valueOf(montantJournalierNetSolidariteSpecifique)).setScale(0, RoundingMode.DOWN).floatValue();
	}
	return 0;
    }

    public boolean isEligible(int numeroMoisSimule, DemandeurEmploi demandeurEmploi, LocalDate dateDebutSimulation) {
	return numeroMoisSimule - 1 < getNombreMoisEligibles(demandeurEmploi, dateDebutSimulation);
    }

    public int getNombreMoisEligibles(DemandeurEmploi demandeurEmploi, LocalDate dateDebutSimulation) {
	int nombreMoisCumulesASSPercueEtSalaire = periodeTravailleeAvantSimulationUtile.getNombreMoisTravaillesAuCoursDes3DerniersMoisAvantSimulation(demandeurEmploi);
	if (informationsPersonnellesUtile.isBeneficiaireACRE(demandeurEmploi)
		&& informationsPersonnellesUtile.getNombreMoisDepuisCreationEntreprise(demandeurEmploi, dateDebutSimulation) < 12) {

	    return getNombreMoisEligiblesBeneficiaireACRE(informationsPersonnellesUtile.getNombreMoisDepuisCreationEntreprise(demandeurEmploi, dateDebutSimulation));
	} else if (!informationsPersonnellesUtile.isBeneficiaireACRE(demandeurEmploi)) {
	    if (futurTravailUtile.hasContratCDI(demandeurEmploi.getFuturTravail())) {
		return getNombreMoisEligiblesCDI(nombreMoisCumulesASSPercueEtSalaire);
	    } else if (futurTravailUtile.hasContratDureeDeterminee(demandeurEmploi.getFuturTravail())) {
		return getNombreMoisEligiblesCDD(demandeurEmploi, nombreMoisCumulesASSPercueEtSalaire);
	    }
	}
	return 0;
    }

    private Aide creerAide(float montantAide) {
	return aideUtile.creerAide(AideEnum.ALLOCATION_SOLIDARITE_SPECIFIQUE, Optional.of(OrganismeEnum.PE), Optional.empty(), false, montantAide);
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
