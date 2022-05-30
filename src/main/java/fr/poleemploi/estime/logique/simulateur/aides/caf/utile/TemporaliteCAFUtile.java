package fr.poleemploi.estime.logique.simulateur.aides.caf.utile;

import java.time.LocalDate;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fr.poleemploi.estime.clientsexternes.openfisca.OpenFiscaClient;
import fr.poleemploi.estime.clientsexternes.openfisca.OpenFiscaRetourSimulation;
import fr.poleemploi.estime.commun.enumerations.SituationAppelOpenFiscaEnum;
import fr.poleemploi.estime.commun.utile.demandeuremploi.BeneficiaireAidesUtile;
import fr.poleemploi.estime.services.ressources.Aide;
import fr.poleemploi.estime.services.ressources.DemandeurEmploi;
import fr.poleemploi.estime.services.ressources.Simulation;

@Component
public class TemporaliteCAFUtile {

    @Autowired
    private OpenFiscaClient openFiscaClient;

    @Autowired
    private BeneficiaireAidesUtile beneficiaireAidesUtile;

    @Autowired
    private PrimeActiviteRSAUtile primeActiviteRSAUtile;

    @Autowired
    private AidesLogementUtile aidesLogementUtile;

    @Autowired
    private PrimeActiviteUtile primeActiviteUtile;

    @Autowired
    private PrimeActiviteAAHUtile primeActiviteAAHUtile;

    @Autowired
    private PrimeActiviteAREUtile primeActiviteAREUtile;

    @Autowired
    private PrimeActiviteASSUtile primeActiviteASSUtile;

    @Autowired
    private RSAUtile rsaUtile;

    @Autowired
    private AgepiUtile agepiUtile;

    /**
     * Fonction qui permet de déterminer les appels vers openfisca à effectuer, 
     * cette méthode permet notamment de réduire les appels en rassemblant les appels pour les différentes aides qui doivent s'effectuer dans le même mois
     * 
     * Cette méthode s'appuie sur les temporalités des différentes aides en les aggrègeant
     * 
     * @param simulation
     * @param aidesPourCeMois
     * @param dateDebutSimulation
     * @param numeroMoisSimule
     * @param demandeurEmploi
     */
    public void simulerTemporaliteAppelOpenfisca(Simulation simulation, Map<String, Aide> aidesPourCeMois, LocalDate dateDebutSimulation, int numeroMoisSimule, DemandeurEmploi demandeurEmploi) {

	boolean isAideLogementAVerser = isAideLogementAVerser(numeroMoisSimule, demandeurEmploi);
	boolean isRSAAVerser = isRSAAVerser(numeroMoisSimule, demandeurEmploi);
	boolean isPrimeActiviteAVerser = isPrimeActiviteAVerser(numeroMoisSimule, demandeurEmploi);
	boolean isAgepiAVerser = isAgepiAVerser(numeroMoisSimule);

	if (isAgepiAVerser) {
	    verserAgepi(simulation, aidesPourCeMois, dateDebutSimulation, numeroMoisSimule, demandeurEmploi);
	}
	// Si l'on doit verser l'aide au logement et la prime d'activité pour les demandeurs ASS/AAH/ARE/RSA
	if (isAideLogementAVerser && isRSAAVerser && isPrimeActiviteAVerser) {
	    verserAideLogementAvecPrimeActiviteEtRSA(simulation, aidesPourCeMois, dateDebutSimulation, numeroMoisSimule, demandeurEmploi);
	} else {
	    if (isAideLogementAVerser && isRSAAVerser) {
		verserAideLogementAvecRSA(simulation, aidesPourCeMois, dateDebutSimulation, numeroMoisSimule, demandeurEmploi);
		primeActiviteUtile.reporterPrimeActivite(simulation, aidesPourCeMois, numeroMoisSimule);
	    } else if (isAideLogementAVerser && isPrimeActiviteAVerser) {
		verserAideLogementAvecPrimeActivite(simulation, aidesPourCeMois, dateDebutSimulation, numeroMoisSimule, demandeurEmploi);
		rsaUtile.reporterRsa(simulation, aidesPourCeMois, numeroMoisSimule, demandeurEmploi);
	    } else if (isPrimeActiviteAVerser && isRSAAVerser) {
		verserPrimeActiviteAvecRSA(simulation, aidesPourCeMois, dateDebutSimulation, numeroMoisSimule, demandeurEmploi);
		aidesLogementUtile.reporterAideLogement(simulation, aidesPourCeMois, numeroMoisSimule, demandeurEmploi);
	    } else if (isAideLogementAVerser) {
		verserAideLogement(simulation, aidesPourCeMois, dateDebutSimulation, numeroMoisSimule, demandeurEmploi);
		rsaUtile.reporterRsa(simulation, aidesPourCeMois, numeroMoisSimule, demandeurEmploi);
		primeActiviteUtile.reporterPrimeActivite(simulation, aidesPourCeMois, numeroMoisSimule);
	    } else if (isPrimeActiviteAVerser) {
		verserPrimeActivite(simulation, aidesPourCeMois, dateDebutSimulation, numeroMoisSimule, demandeurEmploi);
		rsaUtile.reporterRsa(simulation, aidesPourCeMois, numeroMoisSimule, demandeurEmploi);
		aidesLogementUtile.reporterAideLogement(simulation, aidesPourCeMois, numeroMoisSimule, demandeurEmploi);
	    } else if (isRSAAVerser) {
		verserRSA(simulation, aidesPourCeMois, dateDebutSimulation, numeroMoisSimule, demandeurEmploi);
		aidesLogementUtile.reporterAideLogement(simulation, aidesPourCeMois, numeroMoisSimule, demandeurEmploi);
		primeActiviteUtile.reporterPrimeActivite(simulation, aidesPourCeMois, numeroMoisSimule);
	    } else {
		aidesLogementUtile.reporterAideLogement(simulation, aidesPourCeMois, numeroMoisSimule, demandeurEmploi);
		primeActiviteUtile.reporterPrimeActivite(simulation, aidesPourCeMois, numeroMoisSimule);
		rsaUtile.reporterRsa(simulation, aidesPourCeMois, numeroMoisSimule, demandeurEmploi);
	    }
	}
    }

    //    private void handleAgepiAVerser(Simulation simulation, Map<String, Aide> aidesPourCeMois, LocalDate dateDebutSimulation, int numeroMoisSimule, DemandeurEmploi demandeurEmploi, boolean isAideLogementAVerser, boolean isPrimeActiviteAVerser, boolean isRSAAVerser) {
    //	if (isAideLogementAVerser && isRSAAVerser && isPrimeActiviteAVerser) {
    //	    verserAgepiAvecAideLogementEtPrimeActiviteEtRSA(simulation, aidesPourCeMois, dateDebutSimulation, numeroMoisSimule, demandeurEmploi);
    //	} else {
    //	    if (isAideLogementAVerser && isRSAAVerser) {
    //		verserAgepiAvecAideLogementEtRSA(simulation, aidesPourCeMois, dateDebutSimulation, numeroMoisSimule, demandeurEmploi);
    //		primeActiviteUtile.reporterPrimeActivite(simulation, aidesPourCeMois, numeroMoisSimule);
    //	    } else if (isAideLogementAVerser && isPrimeActiviteAVerser) {
    //		verserAgepiAvecAideLogementEtPrimeActivite(simulation, aidesPourCeMois, dateDebutSimulation, numeroMoisSimule, demandeurEmploi);
    //		rsaUtile.reporterRsa(simulation, aidesPourCeMois, numeroMoisSimule, demandeurEmploi);
    //	    } else if (isPrimeActiviteAVerser && isRSAAVerser) {
    //		verserAgepiAvecPrimeActiviteEtRSA(simulation, aidesPourCeMois, dateDebutSimulation, numeroMoisSimule, demandeurEmploi);
    //		aidesLogementUtile.reporterAideLogement(simulation, aidesPourCeMois, numeroMoisSimule, demandeurEmploi);
    //	    } else if (isAideLogementAVerser) {
    //		verserAgepiAvecAideLogement(simulation, aidesPourCeMois, dateDebutSimulation, numeroMoisSimule, demandeurEmploi);
    //		rsaUtile.reporterRsa(simulation, aidesPourCeMois, numeroMoisSimule, demandeurEmploi);
    //		primeActiviteUtile.reporterPrimeActivite(simulation, aidesPourCeMois, numeroMoisSimule);
    //	    } else if (isPrimeActiviteAVerser) {
    //		verserAgepiAvecPrimeActivite(simulation, aidesPourCeMois, dateDebutSimulation, numeroMoisSimule, demandeurEmploi);
    //		rsaUtile.reporterRsa(simulation, aidesPourCeMois, numeroMoisSimule, demandeurEmploi);
    //		aidesLogementUtile.reporterAideLogement(simulation, aidesPourCeMois, numeroMoisSimule, demandeurEmploi);
    //	    } else if (isRSAAVerser) {
    //		verserAgepiAvecRSA(simulation, aidesPourCeMois, dateDebutSimulation, numeroMoisSimule, demandeurEmploi);
    //		aidesLogementUtile.reporterAideLogement(simulation, aidesPourCeMois, numeroMoisSimule, demandeurEmploi);
    //		primeActiviteUtile.reporterPrimeActivite(simulation, aidesPourCeMois, numeroMoisSimule);
    //	    } else {
    //		verserAgepi(simulation, aidesPourCeMois, dateDebutSimulation, numeroMoisSimule, demandeurEmploi);
    //		aidesLogementUtile.reporterAideLogement(simulation, aidesPourCeMois, numeroMoisSimule, demandeurEmploi);
    //		primeActiviteUtile.reporterPrimeActivite(simulation, aidesPourCeMois, numeroMoisSimule);
    //		rsaUtile.reporterRsa(simulation, aidesPourCeMois, numeroMoisSimule, demandeurEmploi);
    //	    }
    //	}
    //    }

    private boolean isRSAAVerser(int numeroMoisSimule, DemandeurEmploi demandeurEmploi) {
	boolean isRSAAVerser = false;
	if (beneficiaireAidesUtile.isBeneficiaireRSA(demandeurEmploi)) {
	    isRSAAVerser = rsaUtile.isRSAAVerser(numeroMoisSimule, demandeurEmploi);
	}
	return isRSAAVerser;
    }

    private boolean isAideLogementAVerser(int numeroMoisSimule, DemandeurEmploi demandeurEmploi) {
	return aidesLogementUtile.isEligibleAidesLogement(demandeurEmploi) && aidesLogementUtile.isAideLogementAVerser(numeroMoisSimule, demandeurEmploi);
    }

    private boolean isPrimeActiviteAVerser(int numeroMoisSimule, DemandeurEmploi demandeurEmploi) {
	boolean isPrimeActiviteAVerser = false;
	if (beneficiaireAidesUtile.isBeneficiaireRSA(demandeurEmploi)) {
	    isPrimeActiviteAVerser = primeActiviteRSAUtile.isPrimeActiviteAVerser(numeroMoisSimule, demandeurEmploi);
	}
	if (beneficiaireAidesUtile.isBeneficiaireASS(demandeurEmploi)) {
	    isPrimeActiviteAVerser = primeActiviteASSUtile.isPrimeActiviteAVerser(numeroMoisSimule, demandeurEmploi);
	} else if (beneficiaireAidesUtile.isBeneficiaireAAH(demandeurEmploi)) {
	    isPrimeActiviteAVerser = primeActiviteAAHUtile.isPrimeActiviteAVerser(numeroMoisSimule, demandeurEmploi);
	} else if (beneficiaireAidesUtile.isBeneficiaireARE(demandeurEmploi)) {
	    isPrimeActiviteAVerser = primeActiviteAREUtile.isPrimeActiviteAVerser(numeroMoisSimule, demandeurEmploi);
	}
	return isPrimeActiviteAVerser;
    }

    private boolean isAgepiAVerser(int numeroMoisSimule) {
	return agepiUtile.isEligible(numeroMoisSimule);
    }

    private void verserAgepi(Simulation simulation, Map<String, Aide> aidesPourCeMois, LocalDate dateDebutSimulation, int numeroMoisSimule, DemandeurEmploi demandeurEmploi) {
	OpenFiscaRetourSimulation openFiscaRetourSimulation = openFiscaClient.calculerAidesSelonSituation(simulation, demandeurEmploi, dateDebutSimulation, numeroMoisSimule,
		SituationAppelOpenFiscaEnum.AGEPI);
	verserAide(aidesPourCeMois, openFiscaRetourSimulation);
    }

    //    private void verserAgepiAvecAideLogementEtPrimeActiviteEtRSA(Simulation simulation, Map<String, Aide> aidesPourCeMois, LocalDate dateDebutSimulation, int numeroMoisSimule, DemandeurEmploi demandeurEmploi) {
    //	OpenFiscaRetourSimulation openFiscaRetourSimulation = openFiscaClient.calculerAidesSelonSituation(simulation, demandeurEmploi, dateDebutSimulation, numeroMoisSimule,
    //		SituationAppelOpenFiscaEnum.AGEPI_PPA_RSA_AL);
    //	verserAide(aidesPourCeMois, openFiscaRetourSimulation);
    //    }
    //
    //    private void verserAgepiAvecAideLogementEtPrimeActivite(Simulation simulation, Map<String, Aide> aidesPourCeMois, LocalDate dateDebutSimulation, int numeroMoisSimule, DemandeurEmploi demandeurEmploi) {
    //	OpenFiscaRetourSimulation openFiscaRetourSimulation = openFiscaClient.calculerAidesSelonSituation(simulation, demandeurEmploi, dateDebutSimulation, numeroMoisSimule,
    //		SituationAppelOpenFiscaEnum.AGEPI_PPA_AL);
    //	verserAide(aidesPourCeMois, openFiscaRetourSimulation);
    //    }
    //
    //    private void verserAgepiAvecAideLogementEtRSA(Simulation simulation, Map<String, Aide> aidesPourCeMois, LocalDate dateDebutSimulation, int numeroMoisSimule, DemandeurEmploi demandeurEmploi) {
    //	OpenFiscaRetourSimulation openFiscaRetourSimulation = openFiscaClient.calculerAidesSelonSituation(simulation, demandeurEmploi, dateDebutSimulation, numeroMoisSimule,
    //		SituationAppelOpenFiscaEnum.AGEPI_RSA_AL);
    //	verserAide(aidesPourCeMois, openFiscaRetourSimulation);
    //    }
    //
    //    private void verserAgepiAvecAideLogement(Simulation simulation, Map<String, Aide> aidesPourCeMois, LocalDate dateDebutSimulation, int numeroMoisSimule, DemandeurEmploi demandeurEmploi) {
    //	OpenFiscaRetourSimulation openFiscaRetourSimulation = openFiscaClient.calculerAidesSelonSituation(simulation, demandeurEmploi, dateDebutSimulation, numeroMoisSimule,
    //		SituationAppelOpenFiscaEnum.AGEPI_AL);
    //	verserAide(aidesPourCeMois, openFiscaRetourSimulation);
    //    }
    //
    //    private void verserAgepiAvecPrimeActiviteEtRSA(Simulation simulation, Map<String, Aide> aidesPourCeMois, LocalDate dateDebutSimulation, int numeroMoisSimule, DemandeurEmploi demandeurEmploi) {
    //	OpenFiscaRetourSimulation openFiscaRetourSimulation = openFiscaClient.calculerAidesSelonSituation(simulation, demandeurEmploi, dateDebutSimulation, numeroMoisSimule,
    //		SituationAppelOpenFiscaEnum.AGEPI_PPA_RSA);
    //	verserAide(aidesPourCeMois, openFiscaRetourSimulation);
    //    }
    //
    //    private void verserAgepiAvecPrimeActivite(Simulation simulation, Map<String, Aide> aidesPourCeMois, LocalDate dateDebutSimulation, int numeroMoisSimule, DemandeurEmploi demandeurEmploi) {
    //	OpenFiscaRetourSimulation openFiscaRetourSimulation = openFiscaClient.calculerAidesSelonSituation(simulation, demandeurEmploi, dateDebutSimulation, numeroMoisSimule,
    //		SituationAppelOpenFiscaEnum.AGEPI_PPA);
    //	verserAide(aidesPourCeMois, openFiscaRetourSimulation);
    //    }
    //
    //    private void verserAgepiAvecRSA(Simulation simulation, Map<String, Aide> aidesPourCeMois, LocalDate dateDebutSimulation, int numeroMoisSimule, DemandeurEmploi demandeurEmploi) {
    //	OpenFiscaRetourSimulation openFiscaRetourSimulation = openFiscaClient.calculerAidesSelonSituation(simulation, demandeurEmploi, dateDebutSimulation, numeroMoisSimule,
    //		SituationAppelOpenFiscaEnum.AGEPI_RSA);
    //	verserAide(aidesPourCeMois, openFiscaRetourSimulation);
    //    }

    private void verserAideLogementAvecPrimeActiviteEtRSA(Simulation simulation, Map<String, Aide> aidesPourCeMois, LocalDate dateDebutSimulation, int numeroMoisSimule, DemandeurEmploi demandeurEmploi) {
	OpenFiscaRetourSimulation openFiscaRetourSimulation = openFiscaClient.calculerAidesSelonSituation(simulation, demandeurEmploi, dateDebutSimulation, numeroMoisSimule,
		SituationAppelOpenFiscaEnum.PPA_RSA_AL);
	verserAide(aidesPourCeMois, openFiscaRetourSimulation);
    }

    private void verserAideLogementAvecPrimeActivite(Simulation simulation, Map<String, Aide> aidesPourCeMois, LocalDate dateDebutSimulation, int numeroMoisSimule, DemandeurEmploi demandeurEmploi) {
	OpenFiscaRetourSimulation openFiscaRetourSimulation = openFiscaClient.calculerAidesSelonSituation(simulation, demandeurEmploi, dateDebutSimulation, numeroMoisSimule,
		SituationAppelOpenFiscaEnum.PPA_AL);
	verserAide(aidesPourCeMois, openFiscaRetourSimulation);
    }

    private void verserAideLogementAvecRSA(Simulation simulation, Map<String, Aide> aidesPourCeMois, LocalDate dateDebutSimulation, int numeroMoisSimule, DemandeurEmploi demandeurEmploi) {
	OpenFiscaRetourSimulation openFiscaRetourSimulation = openFiscaClient.calculerAidesSelonSituation(simulation, demandeurEmploi, dateDebutSimulation, numeroMoisSimule,
		SituationAppelOpenFiscaEnum.RSA_AL);
	verserAide(aidesPourCeMois, openFiscaRetourSimulation);
    }

    private void verserAideLogement(Simulation simulation, Map<String, Aide> aidesPourCeMois, LocalDate dateDebutSimulation, int numeroMoisSimule, DemandeurEmploi demandeurEmploi) {
	OpenFiscaRetourSimulation openFiscaRetourSimulation = openFiscaClient.calculerAidesSelonSituation(simulation, demandeurEmploi, dateDebutSimulation, numeroMoisSimule,
		SituationAppelOpenFiscaEnum.AL);
	verserAide(aidesPourCeMois, openFiscaRetourSimulation);
    }

    private void verserPrimeActiviteAvecRSA(Simulation simulation, Map<String, Aide> aidesPourCeMois, LocalDate dateDebutSimulation, int numeroMoisSimule, DemandeurEmploi demandeurEmploi) {
	OpenFiscaRetourSimulation openFiscaRetourSimulation = openFiscaClient.calculerAidesSelonSituation(simulation, demandeurEmploi, dateDebutSimulation, numeroMoisSimule,
		SituationAppelOpenFiscaEnum.PPA_RSA);
	verserAide(aidesPourCeMois, openFiscaRetourSimulation);
    }

    private void verserPrimeActivite(Simulation simulation, Map<String, Aide> aidesPourCeMois, LocalDate dateDebutSimulation, int numeroMoisSimule, DemandeurEmploi demandeurEmploi) {
	OpenFiscaRetourSimulation openFiscaRetourSimulation = openFiscaClient.calculerAidesSelonSituation(simulation, demandeurEmploi, dateDebutSimulation, numeroMoisSimule,
		SituationAppelOpenFiscaEnum.PPA);
	verserAide(aidesPourCeMois, openFiscaRetourSimulation);
    }

    private void verserRSA(Simulation simulation, Map<String, Aide> aidesPourCeMois, LocalDate dateDebutSimulation, int numeroMoisSimule, DemandeurEmploi demandeurEmploi) {
	OpenFiscaRetourSimulation openFiscaRetourSimulation = openFiscaClient.calculerAidesSelonSituation(simulation, demandeurEmploi, dateDebutSimulation, numeroMoisSimule,
		SituationAppelOpenFiscaEnum.RSA);
	verserAide(aidesPourCeMois, openFiscaRetourSimulation);
    }

    private void verserAide(Map<String, Aide> aidesPourCeMois, OpenFiscaRetourSimulation openFiscaRetourSimulation) {
	if (openFiscaRetourSimulation.getMontantAideLogement() > 0) {
	    Aide aideLogement = aidesLogementUtile.creerAideLogement(openFiscaRetourSimulation.getMontantAideLogement(), openFiscaRetourSimulation.getTypeAideLogement(), false);
	    aidesPourCeMois.put(aideLogement.getCode(), aideLogement);
	}
	if (openFiscaRetourSimulation.getMontantPrimeActivite() > 0) {
	    Aide primeActivite = primeActiviteUtile.creerAidePrimeActivite(openFiscaRetourSimulation.getMontantPrimeActivite(), false);
	    aidesPourCeMois.put(primeActivite.getCode(), primeActivite);
	}
	if (openFiscaRetourSimulation.getMontantRSA() > 0) {
	    Aide rsa = rsaUtile.creerAideRSA(openFiscaRetourSimulation.getMontantRSA(), false);
	    aidesPourCeMois.put(rsa.getCode(), rsa);
	}
	if (openFiscaRetourSimulation.getMontantPrimeActivite() > 0 && openFiscaRetourSimulation.getMontantRSA() > 0) {
	    Aide primeActivite = primeActiviteRSAUtile.creerAidePrimeActivite(openFiscaRetourSimulation.getMontantPrimeActivite(), false);
	    aidesPourCeMois.put(primeActivite.getCode(), primeActivite);
	}
	if (openFiscaRetourSimulation.getMontantAgepi() > 0) {
	    Aide agepi = agepiUtile.creerAideAgepi(openFiscaRetourSimulation.getMontantAgepi());
	    aidesPourCeMois.put(agepi.getCode(), agepi);
	}
    }
}
