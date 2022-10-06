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
import fr.poleemploi.estime.commun.utile.demandeuremploi.InformationsPersonnellesUtile;
import fr.poleemploi.estime.commun.utile.demandeuremploi.PeriodeTravailleeAvantSimulationUtile;
import fr.poleemploi.estime.commun.utile.demandeuremploi.RessourcesFinancieresAvantSimulationUtile;
import fr.poleemploi.estime.logique.simulateur.aides.utile.AideUtile;
import fr.poleemploi.estime.services.ressources.Aide;
import fr.poleemploi.estime.services.ressources.DemandeurEmploi;
import fr.poleemploi.estime.services.ressources.MoisTravailleAvantSimulation;

@Component
public class AllocationSolidariteSpecifiqueUtile {

    private static final int NOMBRE_MOIS_MAX_ASS_ELIGIBLE = 4;
    private static final int NOMBRE_MOIS_MAX_ASS_BENEFICIAIRE_ACRE_ELIGIBLE = 6;

    @Autowired
    private AideUtile aideUtile;

    @Autowired
    private DateUtile dateUtile;

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
	if (ressourcesFinancieresAvantSimulationUtile.hasAllocationSolidariteSpecifique(demandeurEmploi)) {
	    LocalDate dateOuvertureDroitASS = demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesPoleEmploi().getAllocationASS().getDateDerniereOuvertureDroit();
	    if (!dateUtile.isDateAvant(mois, dateOuvertureDroitASS)) {
		int nombreJoursDansLeMois = dateUtile.getNombreJoursDansLeMois(mois);
		float montantJournalierNetSolidariteSpecifique = ressourcesFinancieresAvantSimulationUtile.getAllocationSolidariteSpecifiqueJournaliere(demandeurEmploi);
		return BigDecimal.valueOf(nombreJoursDansLeMois).multiply(BigDecimal.valueOf(montantJournalierNetSolidariteSpecifique)).setScale(0, RoundingMode.DOWN).floatValue();
	    }
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
	if (informationsPersonnellesUtile.isBeneficiaireACRE(demandeurEmploi)
		&& informationsPersonnellesUtile.getNombreMoisDepuisCreationEntreprise(demandeurEmploi, dateDebutSimulation) < 12) {
	    return getNombreMoisEligiblesBeneficiaireACRE(informationsPersonnellesUtile.getNombreMoisDepuisCreationEntreprise(demandeurEmploi, dateDebutSimulation));
	} else if (!informationsPersonnellesUtile.isBeneficiaireACRE(demandeurEmploi)) {
	    return getNombreMoisEligiblesCumulRestants(demandeurEmploi, dateDebutSimulation);
	}
	return 0;
    }

    public int getNombreMoisEligiblesCumulRestants(DemandeurEmploi demandeurEmploi, LocalDate dateDebutSimulation) {
	int nombreMoisCumul = 0;
	int nombreMoisDePause = 0;
	int nombreMoisDepuisOuvertureASS = getNombreMoisDepuisOuvertureASS(demandeurEmploi, dateDebutSimulation);
	if (periodeTravailleeAvantSimulationUtile.hasSalairesAvantPeriodeSimulation(demandeurEmploi)) {
	    MoisTravailleAvantSimulation[] mois = demandeurEmploi.getRessourcesFinancieresAvantSimulation().getPeriodeTravailleeAvantSimulation().getMois();
	    for (int index = mois.length - 1; index >= 0; index--) {
		if (periodeTravailleeAvantSimulationUtile.isMoisTravaille(mois[index])) {
		    nombreMoisDePause = 0;
		    nombreMoisCumul++;
		} else {
		    nombreMoisDePause++;
		    if (nombreMoisDePause >= 3) {
			nombreMoisCumul = 0;
		    }
		}
		if (index == nombreMoisDepuisOuvertureASS) {
		    nombreMoisDePause = 0;
		    nombreMoisCumul = 0;
		}
	    }
	}
	return Math.max(0, NOMBRE_MOIS_MAX_ASS_ELIGIBLE - nombreMoisCumul);

    }

    private int getNombreMoisDepuisOuvertureASS(DemandeurEmploi demandeurEmploi, LocalDate dateDebutSimulation) {
	LocalDate dateOuvertureDroitASS = demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesPoleEmploi().getAllocationASS().getDateDerniereOuvertureDroit();
	return dateUtile.getNbrMoisEntreDeuxLocalDates(dateOuvertureDroitASS, dateDebutSimulation);
    }

    private Aide creerAide(float montantAide) {
	return aideUtile.creerAide(AideEnum.ALLOCATION_SOLIDARITE_SPECIFIQUE, Optional.of(OrganismeEnum.PE), Optional.empty(), false, montantAide);
    }

    private int getNombreMoisEligiblesBeneficiaireACRE(int nombreMoisDepuisCreationEntreprise) {
	if (nombreMoisDepuisCreationEntreprise > 6) {
	    int nombreDeMoisApres6PremiersMois = nombreMoisDepuisCreationEntreprise - 6;
	    return NOMBRE_MOIS_MAX_ASS_BENEFICIAIRE_ACRE_ELIGIBLE - nombreDeMoisApres6PremiersMois;
	}
	return NOMBRE_MOIS_MAX_ASS_BENEFICIAIRE_ACRE_ELIGIBLE;
    }
}
