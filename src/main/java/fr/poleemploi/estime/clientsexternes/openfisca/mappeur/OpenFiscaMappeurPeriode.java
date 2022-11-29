package fr.poleemploi.estime.clientsexternes.openfisca.mappeur;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import fr.poleemploi.estime.clientsexternes.openfisca.ressources.OpenFiscaIndividu;
import fr.poleemploi.estime.clientsexternes.openfisca.ressources.OpenFiscaPeriodes;
import fr.poleemploi.estime.commun.enumerations.AideEnum;
import fr.poleemploi.estime.commun.utile.DateUtile;
import fr.poleemploi.estime.commun.utile.demandeuremploi.PeriodeTravailleeAvantSimulationUtile;
import fr.poleemploi.estime.commun.utile.demandeuremploi.RessourcesFinancieresAvantSimulationUtile;
import fr.poleemploi.estime.logique.simulateur.aides.caf.utile.AllocationAdultesHandicapesUtile;
import fr.poleemploi.estime.logique.simulateur.aides.poleemploi.utile.AllocationSolidariteSpecifiqueUtile;
import fr.poleemploi.estime.logique.simulateur.aides.poleemploi.utile.AreUtile;
import fr.poleemploi.estime.logique.simulateur.aides.utile.AideUtile;
import fr.poleemploi.estime.services.ressources.AllocationsLogement;
import fr.poleemploi.estime.services.ressources.DemandeurEmploi;
import fr.poleemploi.estime.services.ressources.MicroEntreprise;
import fr.poleemploi.estime.services.ressources.Personne;
import fr.poleemploi.estime.services.ressources.Salaire;
import fr.poleemploi.estime.services.ressources.Simulation;

@Component
public class OpenFiscaMappeurPeriode {

    public static final int NUMERO_MOIS_PERIODE = 0;
    public static final int NOMBRE_MOIS_PERIODE_OPENFISCA_TNS_AUTO_ENTREPRENEUR_CHIFFRE_AFFAIRES = 3;
    public static final int NOMBRE_MOIS_PERIODE_OPENFISCA = 4;
    public static final int NOMBRE_MOIS_SIMULATION = 6;
    public static final int NOMBRE_MOIS_PERIODE_OPENFISCA_COMPLEMENT_ARE = 7;
    public static final int NOMBRE_MOIS_PERIODE_OPENFISCA_SALAIRES = 13;
    public static final int NOMBRE_MOIS_PERIODE_36 = 36;
    public static final String TYPE_ACTIVITE_CHOMEUR = "chomeur";
    public static final String TYPE_ACTIVITE_ACTIF = "actif";

    @Autowired
    private AllocationSolidariteSpecifiqueUtile allocationSolidariteSpecifiqueUtile;

    @Autowired
    private AllocationAdultesHandicapesUtile allocationAdultesHandicapesUtile;

    @Autowired
    private DateUtile dateUtile;

    @Autowired
    private AideUtile aideUtile;

    @Autowired
    private AreUtile areUtile;

    @Autowired
    private PeriodeTravailleeAvantSimulationUtile periodeTravailleeAvantSimulationUtile;

    @Autowired
    private RessourcesFinancieresAvantSimulationUtile ressourcesFinancieresAvantSimulationUtile;

    public OpenFiscaPeriodes creerPeriodesOpenFisca(Object valeur, LocalDate dateDebutSimulation) {
	OpenFiscaPeriodes periodesOpenFisca = new OpenFiscaPeriodes();
	for (int numeroMoisPeriode = -NOMBRE_MOIS_PERIODE_OPENFISCA; numeroMoisPeriode < NOMBRE_MOIS_SIMULATION; numeroMoisPeriode++) {
	    periodesOpenFisca.put(getPeriodeFormateePlusMonth(dateDebutSimulation, numeroMoisPeriode), valeur);
	}
	return periodesOpenFisca;
    }

    public OpenFiscaPeriodes creerPeriodesActiviteOpenFisca(LocalDate dateDebutSimulation) {
	OpenFiscaPeriodes periodesOpenFisca = new OpenFiscaPeriodes();
	for (int numeroMoisPeriode = -NOMBRE_MOIS_PERIODE_OPENFISCA; numeroMoisPeriode < NOMBRE_MOIS_SIMULATION; numeroMoisPeriode++) {
	    if (numeroMoisPeriode < 0) {
		periodesOpenFisca.put(getPeriodeFormateePlusMonth(dateDebutSimulation, numeroMoisPeriode), TYPE_ACTIVITE_CHOMEUR);
	    } else {
		periodesOpenFisca.put(getPeriodeFormateePlusMonth(dateDebutSimulation, numeroMoisPeriode), TYPE_ACTIVITE_ACTIF);
	    }
	}
	return periodesOpenFisca;
    }

    public OpenFiscaPeriodes creerPeriodeUniqueAREOpenFisca(DemandeurEmploi demandeurEmploi, Object valeur, LocalDate dateDebutSimulation) {
	if (periodeTravailleeAvantSimulationUtile.hasSalairesAvantPeriodeSimulation(demandeurEmploi, 0)) {
	    return creerPeriodeUniqueEffectivePremierMoisOpenFisca(valeur, dateDebutSimulation);
	}
	return creerPeriodeUniqueEffectiveDeuxiemeMoisOpenFisca(valeur, dateDebutSimulation);
    }

    public OpenFiscaPeriodes creerPeriodeUniqueEffectivePremierMoisOpenFisca(Object valeur, LocalDate dateDebutSimulation) {
	return creerPeriodeUniqueEffectiveMoisXOpenFisca(valeur, dateDebutSimulation, 0);
    }

    public OpenFiscaPeriodes creerPeriodeUniqueEffectiveDeuxiemeMoisOpenFisca(Object valeur, LocalDate dateDebutSimulation) {
	return creerPeriodeUniqueEffectiveMoisXOpenFisca(valeur, dateDebutSimulation, 1);
    }

    private OpenFiscaPeriodes creerPeriodeUniqueEffectiveMoisXOpenFisca(Object valeur, LocalDate dateDebutSimulation, int numeroMoisEffectif) {
	OpenFiscaPeriodes periodesOpenFisca = new OpenFiscaPeriodes();
	ObjectMapper mapper = new ObjectMapper();
	for (int numeroMoisPeriode = numeroMoisEffectif - 1; numeroMoisPeriode < NOMBRE_MOIS_SIMULATION; numeroMoisPeriode++) {
	    if (numeroMoisPeriode == numeroMoisEffectif - 1) {
		periodesOpenFisca.put(getPeriodeFormateePlusMonth(dateDebutSimulation, numeroMoisPeriode), valeur);
	    } else {
		periodesOpenFisca.put(getPeriodeFormateePlusMonth(dateDebutSimulation, numeroMoisPeriode), mapper.nullNode());
	    }
	}
	return periodesOpenFisca;
    }

    public OpenFiscaPeriodes creerPeriodesCalculeesOpenFisca(LocalDate dateDebutSimulation) {
	OpenFiscaPeriodes periodesOpenFisca = new OpenFiscaPeriodes();
	ObjectMapper mapper = new ObjectMapper();
	for (int numeroMoisPeriode = -NOMBRE_MOIS_PERIODE_OPENFISCA; numeroMoisPeriode < NOMBRE_MOIS_SIMULATION; numeroMoisPeriode++) {
	    periodesOpenFisca.put(getPeriodeFormateePlusMonth(dateDebutSimulation, numeroMoisPeriode), mapper.nullNode());
	}
	return periodesOpenFisca;
    }

    public OpenFiscaPeriodes creerPeriodesCalculeesNouvelleAideOpenFisca(LocalDate dateDebutSimulation) {
	OpenFiscaPeriodes periodesOpenFisca = new OpenFiscaPeriodes();
	ObjectMapper mapper = new ObjectMapper();
	for (int numeroMoisPeriode = 0; numeroMoisPeriode < NOMBRE_MOIS_SIMULATION; numeroMoisPeriode++) {
	    periodesOpenFisca.put(getPeriodeFormateePlusMonth(dateDebutSimulation, numeroMoisPeriode), mapper.nullNode());
	}
	return periodesOpenFisca;
    }

    public OpenFiscaPeriodes creerPeriodesCalculeesAREOpenFisca(DemandeurEmploi demandeurEmploi, LocalDate dateDebutSimulation) {
	if (periodeTravailleeAvantSimulationUtile.hasSalairesAvantPeriodeSimulation(demandeurEmploi, 0)) {
	    return creerPeriodesCalculeesEffectivePremierMoisOpenFisca(dateDebutSimulation);
	}
	return creerPeriodesCalculeesEffectiveDeuxiemeMoisOpenFisca(dateDebutSimulation);
    }

    public OpenFiscaPeriodes creerPeriodesCalculeesEffectivePremierMoisOpenFisca(LocalDate dateDebutSimulation) {
	return creerPeriodesCalculeesEffectiveMoisXOpenFisca(dateDebutSimulation, 0);
    }

    public OpenFiscaPeriodes creerPeriodesCalculeesEffectiveDeuxiemeMoisOpenFisca(LocalDate dateDebutSimulation) {
	return creerPeriodesCalculeesEffectiveMoisXOpenFisca(dateDebutSimulation, 1);
    }

    private OpenFiscaPeriodes creerPeriodesCalculeesEffectiveMoisXOpenFisca(LocalDate dateDebutSimulation, int numeroMoisEffectif) {
	OpenFiscaPeriodes periodesOpenFisca = new OpenFiscaPeriodes();
	ObjectMapper mapper = new ObjectMapper();
	for (int numeroMoisPeriode = numeroMoisEffectif - 1; numeroMoisPeriode < NOMBRE_MOIS_SIMULATION; numeroMoisPeriode++) {
	    periodesOpenFisca.put(getPeriodeFormateePlusMonth(dateDebutSimulation, numeroMoisPeriode), mapper.nullNode());
	}
	return periodesOpenFisca;
    }

    public OpenFiscaPeriodes creerPeriodeCalculeeUniqueOpenFisca(LocalDate dateDebutSimulation) {
	ObjectMapper mapper = new ObjectMapper();
	return creerPeriodeUniqueOpenFisca(mapper.nullNode(), dateDebutSimulation);
    }

    public OpenFiscaPeriodes creerPeriodeUniqueOpenFisca(Object valeur, LocalDate dateDebutSimulation) {
	OpenFiscaPeriodes periodesOpenFisca = new OpenFiscaPeriodes();
	periodesOpenFisca.put(getPeriodeFormatee(dateDebutSimulation), valeur);
	return periodesOpenFisca;
    }

    public OpenFiscaPeriodes creerPeriodesAREAvantSimulation(DemandeurEmploi demandeurEmploi, LocalDate dateDebutSimulation) {
	OpenFiscaPeriodes periodesOpenFisca = new OpenFiscaPeriodes();
	int moisPassageComplementARE = areUtile.numeroMoisPassageAuComplementARE(demandeurEmploi);
	for (int numeroMoisPeriode = -NOMBRE_MOIS_PERIODE_OPENFISCA; numeroMoisPeriode < moisPassageComplementARE; numeroMoisPeriode++) {
	    periodesOpenFisca.put(getPeriodeFormateePlusMonth(dateDebutSimulation, numeroMoisPeriode),
		    areUtile.calculerMontantMensuelARE(demandeurEmploi, numeroMoisPeriode, dateDebutSimulation));
	}
	return periodesOpenFisca;
    }

    public OpenFiscaPeriodes creerPeriodesValeurNulleEgaleZero(Object valeur, LocalDate dateDebutSimulation) {
	OpenFiscaPeriodes periodesOpenFisca = new OpenFiscaPeriodes();
	for (int numeroMoisPeriode = -NOMBRE_MOIS_PERIODE_OPENFISCA; numeroMoisPeriode < NOMBRE_MOIS_SIMULATION; numeroMoisPeriode++) {
	    if (valeur != null) {
		periodesOpenFisca.put(getPeriodeFormateePlusMonth(dateDebutSimulation, numeroMoisPeriode), valeur);
	    } else {
		periodesOpenFisca.put(getPeriodeFormateePlusMonth(dateDebutSimulation, numeroMoisPeriode), 0.0);
	    }
	}
	return periodesOpenFisca;
    }

    public OpenFiscaPeriodes creerPeriodesRevenusImmobilier(Object valeur, LocalDate dateDebutSimulation) {
	OpenFiscaPeriodes periodesOpenFisca = new OpenFiscaPeriodes();
	LocalDate datePlusNbrPeriodeMois = dateUtile.ajouterMoisALocalDate(dateDebutSimulation, NOMBRE_MOIS_SIMULATION);
	for (int nombreMoisAEnlever = NOMBRE_MOIS_PERIODE_36; nombreMoisAEnlever >= 1; nombreMoisAEnlever--) {
	    LocalDate datePeriode = dateUtile.enleverMoisALocalDate(datePlusNbrPeriodeMois, nombreMoisAEnlever);
	    periodesOpenFisca.put(getPeriodeFormatee(datePeriode), valeur);
	}
	return periodesOpenFisca;
    }

    public OpenFiscaPeriodes creerPeriodesAPL(DemandeurEmploi demandeurEmploi, LocalDate dateDebutSimulation) {
	AllocationsLogement apl = demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesCAF().getAidesLogement().getAidePersonnaliseeLogement();
	int prochaineDeclarationTrimestrielle = ressourcesFinancieresAvantSimulationUtile.getProchaineDeclarationTrimestrielle(demandeurEmploi);
	return creerPeriodesAllocationsLogement(apl, dateDebutSimulation, prochaineDeclarationTrimestrielle);

    }

    public OpenFiscaPeriodes creerPeriodesALS(DemandeurEmploi demandeurEmploi, LocalDate dateDebutSimulation) {
	AllocationsLogement als = demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesCAF().getAidesLogement().getAllocationLogementSociale();
	int prochaineDeclarationTrimestrielle = ressourcesFinancieresAvantSimulationUtile.getProchaineDeclarationTrimestrielle(demandeurEmploi);
	return creerPeriodesAllocationsLogement(als, dateDebutSimulation, prochaineDeclarationTrimestrielle);
    }

    public OpenFiscaPeriodes creerPeriodesALF(DemandeurEmploi demandeurEmploi, LocalDate dateDebutSimulation) {
	AllocationsLogement alf = demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesCAF().getAidesLogement().getAllocationLogementFamiliale();
	int prochaineDeclarationTrimestrielle = ressourcesFinancieresAvantSimulationUtile.getProchaineDeclarationTrimestrielle(demandeurEmploi);
	return creerPeriodesAllocationsLogement(alf, dateDebutSimulation, prochaineDeclarationTrimestrielle);
    }

    public OpenFiscaPeriodes creerPeriodesAllocationsLogement(AllocationsLogement allocationsLogement, LocalDate dateDebutSimulation, int prochaineDeclarationTrimestrielle) {
	OpenFiscaPeriodes periodesOpenFisca = new OpenFiscaPeriodes();
	ObjectMapper mapper = new ObjectMapper();
	if (allocationsLogement == null) {
	    allocationsLogement = new AllocationsLogement();
	}
	for (int numeroMoisPeriode = -NOMBRE_MOIS_PERIODE_OPENFISCA; numeroMoisPeriode < NOMBRE_MOIS_SIMULATION; numeroMoisPeriode++) {
	    handlePeriodesAllocationsLogement(allocationsLogement, dateDebutSimulation, prochaineDeclarationTrimestrielle, numeroMoisPeriode, periodesOpenFisca, mapper);

	}
	return periodesOpenFisca;
    }

    private void handlePeriodesAllocationsLogement(AllocationsLogement allocationsLogement, LocalDate dateDebutSimulation, int prochaineDeclarationTrimestrielle, int numeroMoisPeriode, OpenFiscaPeriodes periodesOpenFisca, ObjectMapper mapper) {

	if ((prochaineDeclarationTrimestrielle == 1 || prochaineDeclarationTrimestrielle == 2) && numeroMoisPeriode < 0) {
	    if (numeroMoisPeriode <= -3) {
		periodesOpenFisca.put(getPeriodeFormateePlusMonth(dateDebutSimulation, numeroMoisPeriode), allocationsLogement.getMoisNMoins3());
	    } else if (numeroMoisPeriode == -2) {
		periodesOpenFisca.put(getPeriodeFormateePlusMonth(dateDebutSimulation, numeroMoisPeriode), allocationsLogement.getMoisNMoins2());
	    } else if (numeroMoisPeriode == -1) {
		periodesOpenFisca.put(getPeriodeFormateePlusMonth(dateDebutSimulation, numeroMoisPeriode), allocationsLogement.getMoisNMoins1());
	    }
	} else if ((prochaineDeclarationTrimestrielle == 0 || prochaineDeclarationTrimestrielle == 3) && numeroMoisPeriode < -1) {
	    if (numeroMoisPeriode == -4) {
		periodesOpenFisca.put(getPeriodeFormateePlusMonth(dateDebutSimulation, numeroMoisPeriode), allocationsLogement.getMoisNMoins3());
	    } else if (numeroMoisPeriode == -3) {
		periodesOpenFisca.put(getPeriodeFormateePlusMonth(dateDebutSimulation, numeroMoisPeriode), allocationsLogement.getMoisNMoins2());
	    } else if (numeroMoisPeriode == -2) {
		periodesOpenFisca.put(getPeriodeFormateePlusMonth(dateDebutSimulation, numeroMoisPeriode), allocationsLogement.getMoisNMoins1());
	    }
	} else {
	    periodesOpenFisca.put(getPeriodeFormateePlusMonth(dateDebutSimulation, numeroMoisPeriode), mapper.nullNode());
	}
    }

    public void creerPeriodesSalaireDemandeur(OpenFiscaIndividu demandeurOpenFisca, DemandeurEmploi demandeurEmploi, LocalDate dateDebutSimulation) {
	OpenFiscaPeriodes periodesOpenFiscaSalaireDeBase = new OpenFiscaPeriodes();
	OpenFiscaPeriodes periodesOpenFiscaSalaireImposable = new OpenFiscaPeriodes();

	for (int numeroMoisPeriodeOpenfisca = -NOMBRE_MOIS_PERIODE_OPENFISCA_SALAIRES; numeroMoisPeriodeOpenfisca < NOMBRE_MOIS_SIMULATION; numeroMoisPeriodeOpenfisca++) {
	    Salaire salairePourSimulation = periodeTravailleeAvantSimulationUtile.getSalaireAvantPeriodeSimulation(demandeurEmploi, numeroMoisPeriodeOpenfisca);
	    periodesOpenFiscaSalaireDeBase.put(getPeriodeFormateePlusMonth(dateDebutSimulation, numeroMoisPeriodeOpenfisca), salairePourSimulation.getMontantMensuelBrut());
	    periodesOpenFiscaSalaireImposable.put(getPeriodeFormateePlusMonth(dateDebutSimulation, numeroMoisPeriodeOpenfisca), salairePourSimulation.getMontantMensuelNet());
	}

	demandeurOpenFisca.setSalaireDeBase(periodesOpenFiscaSalaireDeBase);
	demandeurOpenFisca.setSalaireImposable(periodesOpenFiscaSalaireImposable);
    }

    public void creerPeriodesSalairePersonne(OpenFiscaIndividu personneOpenFisca, Personne personne, LocalDate dateDebutSimulation) {
	OpenFiscaPeriodes periodesOpenFiscaSalaireDeBase = new OpenFiscaPeriodes();
	OpenFiscaPeriodes periodesOpenFiscaSalaireImposable = new OpenFiscaPeriodes();

	for (int numeroMoisPeriodeOpenfisca = -NOMBRE_MOIS_PERIODE_OPENFISCA_SALAIRES; numeroMoisPeriodeOpenfisca < NOMBRE_MOIS_SIMULATION; numeroMoisPeriodeOpenfisca++) {
	    Salaire salairePourSimulation = periodeTravailleeAvantSimulationUtile.getSalaireAvantPeriodeSimulationPersonne(personne, numeroMoisPeriodeOpenfisca);
	    periodesOpenFiscaSalaireDeBase.put(getPeriodeFormateePlusMonth(dateDebutSimulation, numeroMoisPeriodeOpenfisca), salairePourSimulation.getMontantMensuelBrut());
	    periodesOpenFiscaSalaireImposable.put(getPeriodeFormateePlusMonth(dateDebutSimulation, numeroMoisPeriodeOpenfisca), salairePourSimulation.getMontantMensuelNet());
	}

	personneOpenFisca.setSalaireDeBase(periodesOpenFiscaSalaireDeBase);
	personneOpenFisca.setSalaireImposable(periodesOpenFiscaSalaireImposable);
    }

    public OpenFiscaPeriodes creerPeriodesOpenFiscaAide(DemandeurEmploi demandeurEmploi, Simulation simulation, String codeAide, LocalDate dateDebutSimulation, int numeroMoisSimule) {
	OpenFiscaPeriodes periodesOpenFisca = new OpenFiscaPeriodes();
	int numeroMoisMontantARecuperer = numeroMoisSimule - (OpenFiscaMappeurPeriode.NOMBRE_MOIS_PERIODE_OPENFISCA - 1);
	for (int numeroMoisPeriode = -NOMBRE_MOIS_PERIODE_OPENFISCA; numeroMoisPeriode < NOMBRE_MOIS_SIMULATION; numeroMoisPeriode++) {
	    float montantAide = 0;
	    if (isNumeroMoisMontantARecupererDansPeriodeSimulation(numeroMoisMontantARecuperer)) {
		montantAide = aideUtile.getMontantAidePourCeMoisSimule(simulation, codeAide, numeroMoisMontantARecuperer);
	    } else {
		montantAide = aideUtile.getMontantAideAvantSimulation(numeroMoisMontantARecuperer, demandeurEmploi, codeAide, dateDebutSimulation);
	    }
	    periodesOpenFisca.put(getPeriodeFormateePlusMonth(dateDebutSimulation, numeroMoisPeriode), montantAide);
	    numeroMoisMontantARecuperer++;
	}
	return periodesOpenFisca;
    }

    public OpenFiscaPeriodes creerPeriodesOpenFiscaARE(DemandeurEmploi demandeurEmploi, Simulation simulation, LocalDate dateDebutSimulation, int numeroMoisSimule) {
	OpenFiscaPeriodes periodesOpenFisca = new OpenFiscaPeriodes();
	int numeroMoisMontantARecuperer = numeroMoisSimule - (OpenFiscaMappeurPeriode.NOMBRE_MOIS_PERIODE_OPENFISCA - 1);
	for (int numeroMoisPeriode = NUMERO_MOIS_PERIODE; numeroMoisPeriode < NOMBRE_MOIS_SIMULATION; numeroMoisPeriode++) {
	    float montantAide = 0;
	    if (!isNumeroMoisMontantARecupererDansPeriodeSimulation(numeroMoisMontantARecuperer)) {
		montantAide = aideUtile.getMontantAideAvantSimulation(numeroMoisMontantARecuperer, demandeurEmploi, AideEnum.AIDE_RETOUR_EMPLOI.getCode(), dateDebutSimulation);
	    } else if (isNumeroMoisComplementARE(numeroMoisMontantARecuperer)) {
		montantAide = aideUtile.getMontantAidePourCeMoisSimule(simulation, AideEnum.COMPLEMENT_AIDE_RETOUR_EMPLOI.getCode(), numeroMoisMontantARecuperer);
	    } else {
		montantAide = aideUtile.getMontantAidePourCeMoisSimule(simulation, AideEnum.AIDE_RETOUR_EMPLOI.getCode(), numeroMoisMontantARecuperer);
	    }
	    periodesOpenFisca.put(getPeriodeFormateePlusMonth(dateDebutSimulation, numeroMoisPeriode), montantAide);
	    numeroMoisMontantARecuperer++;
	}
	return periodesOpenFisca;
    }

    public OpenFiscaPeriodes creerPeriodesOpenFiscaASS(DemandeurEmploi demandeurEmploi, LocalDate dateDebutSimulation) {
	OpenFiscaPeriodes periodesOpenFisca = new OpenFiscaPeriodes();
	for (int numeroMoisPeriode = -NOMBRE_MOIS_PERIODE_OPENFISCA; numeroMoisPeriode < NOMBRE_MOIS_SIMULATION; numeroMoisPeriode++) {
	    float montantAide = 0;
	    if (isNumeroMoisMontantARecupererDansPeriodeSimulation(numeroMoisPeriode)) {
		montantAide = allocationSolidariteSpecifiqueUtile.calculerMontantSiEligible(demandeurEmploi, numeroMoisPeriode, dateDebutSimulation);
	    } else {
		montantAide = aideUtile.getMontantAideAvantSimulation(numeroMoisPeriode, demandeurEmploi, AideEnum.ALLOCATION_SOLIDARITE_SPECIFIQUE.getCode(), dateDebutSimulation);
	    }
	    periodesOpenFisca.put(getPeriodeFormateePlusMonth(dateDebutSimulation, numeroMoisPeriode), montantAide);
	}
	return periodesOpenFisca;
    }

    public OpenFiscaPeriodes creerPeriodesOpenFiscaAAH(DemandeurEmploi demandeurEmploi, LocalDate dateDebutSimulation) {
	OpenFiscaPeriodes periodesOpenFisca = new OpenFiscaPeriodes();
	for (int numeroMoisPeriode = -NOMBRE_MOIS_PERIODE_OPENFISCA; numeroMoisPeriode < NOMBRE_MOIS_SIMULATION; numeroMoisPeriode++) {
	    float montantAide = 0;
	    if (isNumeroMoisMontantARecupererDansPeriodeSimulation(numeroMoisPeriode)) {
		montantAide = allocationAdultesHandicapesUtile.calculerMontant(demandeurEmploi, numeroMoisPeriode);
	    } else {
		montantAide = aideUtile.getMontantAideAvantSimulation(numeroMoisPeriode, demandeurEmploi, AideEnum.ALLOCATION_ADULTES_HANDICAPES.getCode(), dateDebutSimulation);
	    }
	    periodesOpenFisca.put(getPeriodeFormateePlusMonth(dateDebutSimulation, numeroMoisPeriode), montantAide);
	}
	return periodesOpenFisca;
    }

    public OpenFiscaPeriodes creerPeriodesOpenFiscaRSA(DemandeurEmploi demandeurEmploi, LocalDate dateDebutSimulation) {
	float prochaineDeclarationRSA = ressourcesFinancieresAvantSimulationUtile.getProchaineDeclarationTrimestrielle(demandeurEmploi);
	float montantRSADemandeur = demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesCAF().getAllocationRSA();

	OpenFiscaPeriodes periodesOpenFisca = new OpenFiscaPeriodes();
	ObjectMapper mapper = new ObjectMapper();
	for (int numeroMoisPeriode = -NOMBRE_MOIS_PERIODE_OPENFISCA; numeroMoisPeriode < NOMBRE_MOIS_SIMULATION; numeroMoisPeriode++) {
	    if (numeroMoisPeriode < prochaineDeclarationRSA - 1) {
		periodesOpenFisca.put(getPeriodeFormateePlusMonth(dateDebutSimulation, numeroMoisPeriode), montantRSADemandeur);
	    } else {
		periodesOpenFisca.put(getPeriodeFormateePlusMonth(dateDebutSimulation, numeroMoisPeriode), mapper.nullNode());
	    }
	}
	return periodesOpenFisca;
    }

    public OpenFiscaPeriodes creerPeriodesAnnees(Object valeur, LocalDate dateDebutSimulation) {
	OpenFiscaPeriodes periodesOpenFisca = new OpenFiscaPeriodes();
	int anneeDateSimulation = dateUtile.ajouterMoisALocalDate(dateDebutSimulation, NOMBRE_MOIS_SIMULATION).getYear();
	String anneeMoinsUnAnneeSimulation = String.valueOf(anneeDateSimulation - 1);
	String anneeMoinsDeuxAnneeSimulation = String.valueOf(anneeDateSimulation - 2);
	periodesOpenFisca.put(anneeMoinsUnAnneeSimulation, valeur);
	periodesOpenFisca.put(anneeMoinsDeuxAnneeSimulation, valeur);
	return periodesOpenFisca;
    }

    public OpenFiscaPeriodes creerPeriodesChiffreAffairesMicroEntreprise(MicroEntreprise microEntreprise, LocalDate dateDebutSimulation) {
	OpenFiscaPeriodes periodesOpenFisca = new OpenFiscaPeriodes();
	int anneeDateSimulation = dateUtile.ajouterMoisALocalDate(dateDebutSimulation, NOMBRE_MOIS_SIMULATION).getYear();
	periodesOpenFisca.put(String.valueOf(anneeDateSimulation), microEntreprise.getChiffreAffairesN());
	periodesOpenFisca.put(String.valueOf(anneeDateSimulation - 1), microEntreprise.getChiffreAffairesNMoins1());
	periodesOpenFisca.put(String.valueOf(anneeDateSimulation - 2), microEntreprise.getChiffreAffairesNMoins2());
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

    public String getPeriodeFormatee(LocalDate datePeriode) {
	return datePeriode.getYear() + "-" + dateUtile.getMonthFromLocalDate(datePeriode);
    }

    public String getPeriodeFormateeRessourceFinanciere(LocalDate dateDebutSimulation, int numeroMoisSimule, int numeroPeriodeOpenfisca) {
	LocalDate dateDeclenchementCalculAide = dateDebutSimulation.plusMonths(numeroMoisSimule);
	LocalDate dateDebutPeriodeOpenfisca = dateDeclenchementCalculAide.minusMonths(NOMBRE_MOIS_PERIODE_OPENFISCA - ((long) numeroPeriodeOpenfisca));
	return getPeriodeFormatee(dateDebutPeriodeOpenfisca);
    }

    public String getPeriodeFormateePlusMonth(LocalDate dateDebutSimulation, int numeroPeriodeOpenfisca) {
	LocalDate dateDebutPeriodeOpenfisca = dateDebutSimulation.plusMonths(numeroPeriodeOpenfisca);
	return getPeriodeFormatee(dateDebutPeriodeOpenfisca);
    }

    public String getPeriodeCalculAgepiEtAideMobilite(LocalDate dateDebutSimulation) {
	LocalDate dateDeclenchementCalculAide = dateDebutSimulation.minusMonths(1);
	return getPeriodeFormatee(dateDeclenchementCalculAide);
    }

    public String getPeriodeNumeroMoisSimule(LocalDate dateDebutSimulation, int numeroMoisSimule) {
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
	return numeroMoisMontantARecuperer >= NUMERO_MOIS_PERIODE;
    }

    /**
     * La simulation se fait sur N si le numeroMoisMontantARecuperer est < NUMERO_MOIS_PERIODE cela veut dire que l'on est sur un mois avant la simulation
     * 
     * @param numeroMoisMontantARecuperer : numéro du mois pour lequel on souhaite récupérer le montant de l'aide
     * @return true si numeroMoisMontantARecuperer concerne un mois de la période de simulation false si numeroMoisMontantARecuperer concerne un mois
     *         avant la période de simulation
     */
    private boolean isNumeroMoisComplementARE(int numeroMoisMontantARecuperer) {
	return numeroMoisMontantARecuperer > NUMERO_MOIS_PERIODE + 1;
    }
}
