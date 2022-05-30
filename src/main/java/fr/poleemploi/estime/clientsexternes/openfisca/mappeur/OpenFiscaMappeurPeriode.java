package fr.poleemploi.estime.clientsexternes.openfisca.mappeur;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import fr.poleemploi.estime.clientsexternes.openfisca.ressources.OpenFiscaIndividu;
import fr.poleemploi.estime.clientsexternes.openfisca.ressources.OpenFiscaPeriodes;
import fr.poleemploi.estime.commun.utile.DateUtile;
import fr.poleemploi.estime.commun.utile.demandeuremploi.PeriodeTravailleeAvantSimulationUtile;
import fr.poleemploi.estime.logique.simulateur.aides.utile.AideUtile;
import fr.poleemploi.estime.services.ressources.AllocationsLogement;
import fr.poleemploi.estime.services.ressources.DemandeurEmploi;
import fr.poleemploi.estime.services.ressources.Personne;
import fr.poleemploi.estime.services.ressources.Salaire;
import fr.poleemploi.estime.services.ressources.Simulation;

@Component
public class OpenFiscaMappeurPeriode {

    public static final int NUMERO_MOIS_PERIODE = 0;
    public static final int NOMBRE_MOIS_PERIODE_OPENFISCA = 4;
    public static final int NOMBRE_MOIS_PERIODE_OPENFISCA_TNS_AUTO_ENTREPRENEUR_CHIFFRE_AFFAIRES = 3;
    public static final int NOMBRE_MOIS_PERIODE_OPENFISCA_SALAIRES = 14;
    public static final int NOMBRE_MOIS_PERIODE_36 = 36;

    @Autowired
    private DateUtile dateUtile;

    @Autowired
    private AideUtile aideUtile;

    @Autowired
    private PeriodeTravailleeAvantSimulationUtile periodeTravailleeAvantSimulationUtile;

    public OpenFiscaPeriodes creerPeriodesOpenFisca(Object valeur, LocalDate dateDebutSimulation, int numeroMoisSimule, int nombrePeriodes) {
	OpenFiscaPeriodes periodesOpenFisca = new OpenFiscaPeriodes();
	for (int numeroMoisPeriode = NUMERO_MOIS_PERIODE; numeroMoisPeriode < nombrePeriodes; numeroMoisPeriode++) {
	    periodesOpenFisca.put(getPeriodeFormateeRessourceFinanciere(dateDebutSimulation, numeroMoisSimule, numeroMoisPeriode), valeur);
	}
	return periodesOpenFisca;
    }

    public OpenFiscaPeriodes creerPeriodesOpenFiscaAgepi(Object valeur, LocalDate dateDebutSimulation) {
	OpenFiscaPeriodes periodesOpenFisca = new OpenFiscaPeriodes();
	periodesOpenFisca.put(getPeriodeFormatee(dateDebutSimulation), valeur);
	return periodesOpenFisca;
    }

    public OpenFiscaPeriodes creerPeriodesValeurNulleEgaleZero(Object valeur, LocalDate dateDebutSimulation, int numeroMoisSimule, int nombrePeriode) {
	OpenFiscaPeriodes periodesOpenFisca = new OpenFiscaPeriodes();
	for (int numeroMoisPeriode = NUMERO_MOIS_PERIODE; numeroMoisPeriode < nombrePeriode; numeroMoisPeriode++) {
	    if (valeur != null) {
		periodesOpenFisca.put(getPeriodeFormateeRessourceFinanciere(dateDebutSimulation, numeroMoisSimule, numeroMoisPeriode), valeur);
	    } else {
		periodesOpenFisca.put(getPeriodeFormateeRessourceFinanciere(dateDebutSimulation, numeroMoisSimule, numeroMoisPeriode), 0.0);
	    }
	}
	return periodesOpenFisca;
    }

    public OpenFiscaPeriodes creerPeriodesRevenusImmobilier(Object valeur, LocalDate dateDebutSimulation, int numeroMoisSimule) {
	OpenFiscaPeriodes periodesOpenFisca = new OpenFiscaPeriodes();
	LocalDate datePlusNbrPeriodeMois = dateUtile.ajouterMoisALocalDate(dateDebutSimulation, numeroMoisSimule);
	for (int nombreMoisAEnlever = NOMBRE_MOIS_PERIODE_36; nombreMoisAEnlever >= 1; nombreMoisAEnlever--) {
	    LocalDate datePeriode = dateUtile.enleverMoisALocalDate(datePlusNbrPeriodeMois, nombreMoisAEnlever);
	    periodesOpenFisca.put(getPeriodeFormatee(datePeriode), valeur);
	}
	return periodesOpenFisca;
    }

    public OpenFiscaPeriodes creerPeriodesRSA(float montantRsaDemandeur, float prochaineDeclarationRsa, LocalDate dateDebutSimulation, int numeroMoisSimule) {
	OpenFiscaPeriodes periodesOpenFisca = new OpenFiscaPeriodes();
	ObjectMapper mapper = new ObjectMapper();
	for (int numeroMoisPeriode = NUMERO_MOIS_PERIODE; numeroMoisPeriode < OpenFiscaMappeurPeriode.NOMBRE_MOIS_PERIODE_OPENFISCA; numeroMoisPeriode++) {
	    if (numeroMoisPeriode <= prochaineDeclarationRsa) {
		periodesOpenFisca.put(getPeriodeFormateeRessourceFinanciere(dateDebutSimulation, numeroMoisSimule, numeroMoisPeriode), montantRsaDemandeur);
	    } else {
		periodesOpenFisca.put(getPeriodeFormateeRessourceFinanciere(dateDebutSimulation, numeroMoisSimule, numeroMoisPeriode), mapper.nullNode());
	    }
	}
	return periodesOpenFisca;
    }

    public OpenFiscaPeriodes creerPeriodesAllocationsLogement(AllocationsLogement allocationsLogement, LocalDate dateDebutSimulation, int numeroMoisSimule) {
	OpenFiscaPeriodes periodesOpenFisca = new OpenFiscaPeriodes();
	ObjectMapper mapper = new ObjectMapper();
	float moisNMoins3 = 0;
	float moisNMoins2 = 0;
	float moisNMoins1 = 0;
	if (allocationsLogement != null) {
	    moisNMoins3 = allocationsLogement.getMoisNMoins3();
	    moisNMoins2 = allocationsLogement.getMoisNMoins2();
	    moisNMoins1 = allocationsLogement.getMoisNMoins1();
	}
	if (numeroMoisSimule == 1) {
	    periodesOpenFisca.put(getPeriodeFormateeRessourceFinanciere(dateDebutSimulation, numeroMoisSimule, 0), moisNMoins3);
	    periodesOpenFisca.put(getPeriodeFormateeRessourceFinanciere(dateDebutSimulation, numeroMoisSimule, 1), moisNMoins2);
	    periodesOpenFisca.put(getPeriodeFormateeRessourceFinanciere(dateDebutSimulation, numeroMoisSimule, 2), moisNMoins1);
	    periodesOpenFisca.put(getPeriodeFormateeRessourceFinanciere(dateDebutSimulation, numeroMoisSimule, 3), mapper.nullNode());
	} else if (numeroMoisSimule == 2) {
	    periodesOpenFisca.put(getPeriodeFormateeRessourceFinanciere(dateDebutSimulation, numeroMoisSimule, 0), moisNMoins2);
	    periodesOpenFisca.put(getPeriodeFormateeRessourceFinanciere(dateDebutSimulation, numeroMoisSimule, 1), moisNMoins1);
	    periodesOpenFisca.put(getPeriodeFormateeRessourceFinanciere(dateDebutSimulation, numeroMoisSimule, 2), mapper.nullNode());
	    periodesOpenFisca.put(getPeriodeFormateeRessourceFinanciere(dateDebutSimulation, numeroMoisSimule, 3), mapper.nullNode());
	} else if (numeroMoisSimule == 3) {
	    periodesOpenFisca.put(getPeriodeFormateeRessourceFinanciere(dateDebutSimulation, numeroMoisSimule, 0), moisNMoins1);
	    periodesOpenFisca.put(getPeriodeFormateeRessourceFinanciere(dateDebutSimulation, numeroMoisSimule, 1), mapper.nullNode());
	    periodesOpenFisca.put(getPeriodeFormateeRessourceFinanciere(dateDebutSimulation, numeroMoisSimule, 2), mapper.nullNode());
	    periodesOpenFisca.put(getPeriodeFormateeRessourceFinanciere(dateDebutSimulation, numeroMoisSimule, 3), mapper.nullNode());
	} else {
	    periodesOpenFisca.put(getPeriodeFormateeRessourceFinanciere(dateDebutSimulation, numeroMoisSimule, 0), mapper.nullNode());
	    periodesOpenFisca.put(getPeriodeFormateeRessourceFinanciere(dateDebutSimulation, numeroMoisSimule, 1), mapper.nullNode());
	    periodesOpenFisca.put(getPeriodeFormateeRessourceFinanciere(dateDebutSimulation, numeroMoisSimule, 2), mapper.nullNode());
	    periodesOpenFisca.put(getPeriodeFormateeRessourceFinanciere(dateDebutSimulation, numeroMoisSimule, 3), mapper.nullNode());
	}
	return periodesOpenFisca;
    }

    public void creerPeriodesSalaireDemandeur(OpenFiscaIndividu demandeurOpenFisca, DemandeurEmploi demandeurEmploi, LocalDate dateDebutSimulation, int numeroMoisSimule) {
	OpenFiscaPeriodes periodesOpenFiscaSalaireDeBase = new OpenFiscaPeriodes();
	OpenFiscaPeriodes periodesOpenFiscaSalaireImposable = new OpenFiscaPeriodes();

	for (int numeroMoisPeriodeOpenfisca = 0; numeroMoisPeriodeOpenfisca < NOMBRE_MOIS_PERIODE_OPENFISCA_SALAIRES; numeroMoisPeriodeOpenfisca++) {
	    Salaire salairePourSimulation = periodeTravailleeAvantSimulationUtile.getSalaireAvantPeriodeSimulation(demandeurEmploi, numeroMoisSimule, numeroMoisPeriodeOpenfisca);
	    periodesOpenFiscaSalaireDeBase.put(getPeriodeFormateeSalaires(dateDebutSimulation, numeroMoisSimule, numeroMoisPeriodeOpenfisca),
		    salairePourSimulation.getMontantBrut());
	    periodesOpenFiscaSalaireImposable.put(getPeriodeFormateeSalaires(dateDebutSimulation, numeroMoisSimule, numeroMoisPeriodeOpenfisca),
		    salairePourSimulation.getMontantNet());
	}

	demandeurOpenFisca.setSalaireDeBase(periodesOpenFiscaSalaireDeBase);
	demandeurOpenFisca.setSalaireImposable(periodesOpenFiscaSalaireImposable);
    }

    public void creerPeriodesSalairePersonne(OpenFiscaIndividu personneOpenFisca, Personne personne, LocalDate dateDebutSimulation, int numeroMoisSimule) {
	OpenFiscaPeriodes periodesOpenFiscaSalaireDeBase = new OpenFiscaPeriodes();
	OpenFiscaPeriodes periodesOpenFiscaSalaireImposable = new OpenFiscaPeriodes();

	for (int numeroMoisPeriodeOpenfisca = 0; numeroMoisPeriodeOpenfisca < NOMBRE_MOIS_PERIODE_OPENFISCA_SALAIRES; numeroMoisPeriodeOpenfisca++) {
	    Salaire salairePourSimulation = periodeTravailleeAvantSimulationUtile.getSalaireAvantPeriodeSimulationPersonne(personne, numeroMoisSimule, numeroMoisPeriodeOpenfisca);
	    periodesOpenFiscaSalaireDeBase.put(getPeriodeFormateeSalaires(dateDebutSimulation, numeroMoisSimule, numeroMoisPeriodeOpenfisca),
		    salairePourSimulation.getMontantBrut());
	    periodesOpenFiscaSalaireImposable.put(getPeriodeFormateeSalaires(dateDebutSimulation, numeroMoisSimule, numeroMoisPeriodeOpenfisca),
		    salairePourSimulation.getMontantNet());
	}

	personneOpenFisca.setSalaireDeBase(periodesOpenFiscaSalaireDeBase);
	personneOpenFisca.setSalaireImposable(periodesOpenFiscaSalaireImposable);
    }

    public OpenFiscaPeriodes creerPeriodesOpenFiscaAide(DemandeurEmploi demandeurEmploi, Simulation simulation, String codeAide, LocalDate dateDebutSimulation, int numeroMoisSimule) {
	OpenFiscaPeriodes periodesOpenFisca = new OpenFiscaPeriodes();
	int numeroMoisMontantARecuperer = numeroMoisSimule - (OpenFiscaMappeurPeriode.NOMBRE_MOIS_PERIODE_OPENFISCA - 1);
	for (int numeroMoisPeriode = NUMERO_MOIS_PERIODE; numeroMoisPeriode < OpenFiscaMappeurPeriode.NOMBRE_MOIS_PERIODE_OPENFISCA; numeroMoisPeriode++) {
	    float montantAide = 0;
	    if (isNumeroMoisMontantARecupererDansPeriodeSimulation(numeroMoisMontantARecuperer)) {
		montantAide = aideUtile.getMontantAidePourCeMoisSimule(simulation, codeAide, numeroMoisMontantARecuperer);
	    } else {
		montantAide = aideUtile.getMontantAideAvantSimulation(numeroMoisMontantARecuperer, demandeurEmploi, codeAide, dateDebutSimulation);
	    }
	    periodesOpenFisca.put(getPeriodeFormateeRessourceFinanciere(dateDebutSimulation, numeroMoisPeriode, numeroMoisSimule), montantAide);
	    numeroMoisMontantARecuperer++;
	}
	return periodesOpenFisca;
    }

    public OpenFiscaPeriodes creerPeriodesAnnees(Object valeur, LocalDate dateDebutSimulation, int numeroMoisSimule) {
	OpenFiscaPeriodes periodesOpenFisca = new OpenFiscaPeriodes();
	int anneeDateSimulation = dateUtile.ajouterMoisALocalDate(dateDebutSimulation, numeroMoisSimule).getYear();
	String anneeMoinsUnAnneeSimulation = String.valueOf(anneeDateSimulation - 1);
	String anneeMoinsDeuxAnneeSimulation = String.valueOf(anneeDateSimulation - 2);
	periodesOpenFisca.put(anneeMoinsUnAnneeSimulation, valeur);
	periodesOpenFisca.put(anneeMoinsDeuxAnneeSimulation, valeur);
	return periodesOpenFisca;
    }

    public OpenFiscaPeriodes creerPeriodesEnfantACharge(LocalDate dateDebutSimulation) {
	OpenFiscaPeriodes periodesOpenFisca = new OpenFiscaPeriodes();
	periodesOpenFisca.put(String.valueOf(dateDebutSimulation.getYear()), true);
	return periodesOpenFisca;
    }

    public OpenFiscaPeriodes getPeriodeOpenfiscaCalculAideACalculer(LocalDate dateDebutSimulation, int numeroMoisSimule) {
	OpenFiscaPeriodes periodesOpenFisca = new OpenFiscaPeriodes();
	ObjectMapper mapper = new ObjectMapper();
	LocalDate dateDeclenchementCalculAide = dateDebutSimulation.plusMonths((long) numeroMoisSimule - 1);
	periodesOpenFisca.put(getPeriodeFormatee(dateDeclenchementCalculAide), mapper.nullNode());
	return periodesOpenFisca;
    }

    public OpenFiscaPeriodes getPeriodeOpenfiscaCalculAgepi(LocalDate dateDebutSimulation) {
	OpenFiscaPeriodes periodesOpenFisca = new OpenFiscaPeriodes();
	ObjectMapper mapper = new ObjectMapper();
	periodesOpenFisca.put(getPeriodeFormatee(dateDebutSimulation), mapper.nullNode());
	return periodesOpenFisca;
    }

    public String getPeriodeFormatee(LocalDate datePeriode) {
	return datePeriode.getYear() + "-" + dateUtile.getMonthFromLocalDate(datePeriode);
    }

    public String getPeriodeFormateeRessourceFinanciere(LocalDate dateDebutSimulation, int numeroMoisSimule, int numeroPeriodeOpenfisca) {
	LocalDate dateDeclenchementCalculAide = dateDebutSimulation.plusMonths((long) numeroMoisSimule);
	LocalDate dateDebutPeriodeOpenfisca = dateDeclenchementCalculAide.minusMonths(NOMBRE_MOIS_PERIODE_OPENFISCA - ((long) numeroPeriodeOpenfisca));
	return getPeriodeFormatee(dateDebutPeriodeOpenfisca);
    }

    public String getPeriodeFormateeSalaires(LocalDate dateDebutSimulation, int numeroMoisSimule, int numeroPeriodeOpenfisca) {
	LocalDate dateDeclenchementCalculAide = dateDebutSimulation.plusMonths((long) numeroMoisSimule);
	LocalDate dateDebutPeriodeOpenfisca = dateDeclenchementCalculAide.minusMonths(NOMBRE_MOIS_PERIODE_OPENFISCA_SALAIRES - ((long) numeroPeriodeOpenfisca));
	return getPeriodeFormatee(dateDebutPeriodeOpenfisca);
    }

    public String getPeriodeOpenfiscaCalculAide(LocalDate dateDebutSimulation, int numeroMoisSimule) {
	LocalDate dateDeclenchementCalculAide = dateDebutSimulation.plusMonths((long) numeroMoisSimule - 1);
	return getPeriodeFormatee(dateDeclenchementCalculAide);
    }

    /**
     * La simulation se fait sur N si le numeroMoisMontantARecuperer est < NUMERO_MOIS_PERIODE cela veut dire que l'on est sur un mois avant la simulation
     * 
     * @param numeroMoisMontantARecuperer : numéro du mois pour lequel on souhaite récupérer le montant de l'aide
     * @return true si numeroMoisMontantARecuperer concerne un mois de la période de simulation false si numeroMoisMontantARecuperer concerne un mois
     *         avant la période de simulation
     */
    private boolean isNumeroMoisMontantARecupererDansPeriodeSimulation(int numeroMoisMontantARecuperer) {
	return numeroMoisMontantARecuperer > NUMERO_MOIS_PERIODE;
    }
}
