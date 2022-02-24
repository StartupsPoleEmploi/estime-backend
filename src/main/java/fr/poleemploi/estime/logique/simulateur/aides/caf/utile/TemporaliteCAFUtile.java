package fr.poleemploi.estime.logique.simulateur.aides.caf.utile;

import java.time.LocalDate;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fr.poleemploi.estime.clientsexternes.openfisca.OpenFiscaClient;
import fr.poleemploi.estime.clientsexternes.openfisca.OpenFiscaRetourSimulation;
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

    /**
     * Fonction qui permet de déterminer les appels vers openfisca à effectuer, 
     * cette méthode permet notamment de réduire les appels en rassemblant les appels pour les différentes aides qui doivent s'effectuer dans le même mois
     * 
     * Cette méthode s'appuie sur les temporalités des différentes aides en les aggrègeant
     * 
     * @param simulationAides
     * @param aidesPourCeMois
     * @param dateDebutSimulation
     * @param numeroMoisSimule
     * @param demandeurEmploi
     */
    public void simulerTemporaliteAppelOpenfisca(Simulation simulationAides, Map<String, Aide> aidesPourCeMois, LocalDate dateDebutSimulation, int numeroMoisSimule, DemandeurEmploi demandeurEmploi) {

	// Si l'on doit verser l'aide au logement et la prime d'activité pour les demandeurs ASS/AAH/ARE
	if (isAideLogementAvecPrimeActiviteAVerser(numeroMoisSimule, demandeurEmploi)) {
	    verserAideLogementAvecPrimeActivite(simulationAides, aidesPourCeMois, dateDebutSimulation, numeroMoisSimule - 1, demandeurEmploi);
	}
	// Si l'on doit verser l'aide au logement, prime d'activité et le RSA pour les demandeurs RSA
	else if (isAideLogementAvecPrimeActiviteEtRSAAVerser(numeroMoisSimule, demandeurEmploi)) {
	    verserAideLogementAvecPrimeActiviteEtRSA(simulationAides, aidesPourCeMois, dateDebutSimulation, numeroMoisSimule - 1, demandeurEmploi);
	}
	// Si l'on doit verser uniquement l'aide au logement pour les demandeurs ASS/AAH/RSA
	else if (isAideLogementAVerser(numeroMoisSimule, demandeurEmploi)) {
	    verserAideLogement(simulationAides, aidesPourCeMois, dateDebutSimulation, numeroMoisSimule - 1, demandeurEmploi);
	    primeActiviteRSAUtile.reporterRsaEtPrimeActivite(simulationAides, aidesPourCeMois, numeroMoisSimule, demandeurEmploi);
	    primeActiviteUtile.reporterPrimeActivite(simulationAides, aidesPourCeMois, numeroMoisSimule);
	}
	// Si l'on doit verser la prime d'activité et le RSA pour les demandeurs RSA
	else if (isPrimeActiviteEtRSAAVerser(numeroMoisSimule, demandeurEmploi)) {
	    verserPrimeActiviteEtRSA(simulationAides, aidesPourCeMois, dateDebutSimulation, numeroMoisSimule - 1, demandeurEmploi);
	    aidesLogementUtile.reporterAideLogement(simulationAides, aidesPourCeMois, numeroMoisSimule, demandeurEmploi);
	}
	// Si l'on doit verser la prime d'activité pour les demandeurs ASS/AAH 
	else if (isPrimeActiviteAVerser(numeroMoisSimule, demandeurEmploi)) {
	    verserPrimeActivite(simulationAides, aidesPourCeMois, dateDebutSimulation, numeroMoisSimule, demandeurEmploi);
	    aidesLogementUtile.reporterAideLogement(simulationAides, aidesPourCeMois, numeroMoisSimule, demandeurEmploi);
	}
	// Sinon, on reporte ce qui doit l'être
	else {
	    primeActiviteRSAUtile.reporterRsaEtPrimeActivite(simulationAides, aidesPourCeMois, numeroMoisSimule, demandeurEmploi);
	    aidesLogementUtile.reporterAideLogement(simulationAides, aidesPourCeMois, numeroMoisSimule, demandeurEmploi);
	    primeActiviteUtile.reporterPrimeActivite(simulationAides, aidesPourCeMois, numeroMoisSimule);
	}
    }

    private boolean isAideLogementAvecPrimeActiviteEtRSAAVerser(int numeroMoisSimule, DemandeurEmploi demandeurEmploi) {
	boolean isAideLogementAvecPrimeActiviteEtRSAAVerser = false;
	if (aidesLogementUtile.isEligibleAidesLogement(demandeurEmploi) && aidesLogementUtile.isAideLogementAVerser(numeroMoisSimule, demandeurEmploi)
		&& beneficiaireAidesUtile.isBeneficiaireRSA(demandeurEmploi)) {
	    isAideLogementAvecPrimeActiviteEtRSAAVerser = primeActiviteRSAUtile.isRSAAVerser(numeroMoisSimule, demandeurEmploi);
	}
	return isAideLogementAvecPrimeActiviteEtRSAAVerser;
    }

    private boolean isAideLogementAvecPrimeActiviteAVerser(int numeroMoisSimule, DemandeurEmploi demandeurEmploi) {
	boolean isAideLogementAvecPrimeActiviteAVerser = false;
	if (aidesLogementUtile.isEligibleAidesLogement(demandeurEmploi) && aidesLogementUtile.isAideLogementAVerser(numeroMoisSimule, demandeurEmploi)
		&& beneficiaireAidesUtile.isBeneficiaireASS(demandeurEmploi)) {
	    isAideLogementAvecPrimeActiviteAVerser = primeActiviteASSUtile.isPrimeActiviteAVerser(numeroMoisSimule, demandeurEmploi);
	} else if (aidesLogementUtile.isEligibleAidesLogement(demandeurEmploi) && aidesLogementUtile.isAideLogementAVerser(numeroMoisSimule, demandeurEmploi)
		&& beneficiaireAidesUtile.isBeneficiaireAAH(demandeurEmploi)) {
	    isAideLogementAvecPrimeActiviteAVerser = primeActiviteAAHUtile.isPrimeActiviteAVerser(numeroMoisSimule, demandeurEmploi);
	} else if (aidesLogementUtile.isEligibleAidesLogement(demandeurEmploi) && aidesLogementUtile.isAideLogementAVerser(numeroMoisSimule, demandeurEmploi)
		&& beneficiaireAidesUtile.isBeneficiaireARE(demandeurEmploi)) {
	    isAideLogementAvecPrimeActiviteAVerser = primeActiviteAREUtile.isPrimeActiviteAVerser(numeroMoisSimule, demandeurEmploi);
	}

	return isAideLogementAvecPrimeActiviteAVerser;
    }

    private boolean isAideLogementAVerser(int numeroMoisSimule, DemandeurEmploi demandeurEmploi) {
	return aidesLogementUtile.isEligibleAidesLogement(demandeurEmploi) && aidesLogementUtile.isAideLogementAVerser(numeroMoisSimule, demandeurEmploi);
    }

    private boolean isPrimeActiviteAVerser(int numeroMoisSimule, DemandeurEmploi demandeurEmploi) {
	boolean isPrimeActiviteAVerser = false;
	if (beneficiaireAidesUtile.isBeneficiaireASS(demandeurEmploi)) {
	    isPrimeActiviteAVerser = primeActiviteASSUtile.isPrimeActiviteAVerser(numeroMoisSimule, demandeurEmploi);
	} else if (beneficiaireAidesUtile.isBeneficiaireAAH(demandeurEmploi)) {
	    isPrimeActiviteAVerser = primeActiviteAAHUtile.isPrimeActiviteAVerser(numeroMoisSimule, demandeurEmploi);
	} else if (beneficiaireAidesUtile.isBeneficiaireARE(demandeurEmploi)) {
	    isPrimeActiviteAVerser = primeActiviteAREUtile.isPrimeActiviteAVerser(numeroMoisSimule, demandeurEmploi);
	}
	return isPrimeActiviteAVerser;
    }

    private boolean isPrimeActiviteEtRSAAVerser(int numeroMoisSimule, DemandeurEmploi demandeurEmploi) {
	boolean isPrimeActiviteEtRSAAVerser = false;
	if (beneficiaireAidesUtile.isBeneficiaireRSA(demandeurEmploi)) {
	    isPrimeActiviteEtRSAAVerser = primeActiviteRSAUtile.isRSAAVerser(numeroMoisSimule, demandeurEmploi);
	}
	return isPrimeActiviteEtRSAAVerser;
    }

    private void verserAideLogementAvecPrimeActiviteEtRSA(Simulation simulationAides, Map<String, Aide> aidesPourCeMois, LocalDate dateDebutSimulation, int numeroMoisSimule, DemandeurEmploi demandeurEmploi) {
	OpenFiscaRetourSimulation openFiscaRetourSimulation = openFiscaClient.calculerAideLogementAvecPrimeActiviteEtRSA(simulationAides, demandeurEmploi, dateDebutSimulation,
		numeroMoisSimule);

	verserAide(aidesPourCeMois, openFiscaRetourSimulation);
    }

    private void verserAideLogementAvecPrimeActivite(Simulation simulationAides, Map<String, Aide> aidesPourCeMois, LocalDate dateDebutSimulation, int numeroMoisSimule, DemandeurEmploi demandeurEmploi) {
	OpenFiscaRetourSimulation openFiscaRetourSimulation = openFiscaClient.calculerAideLogementAvecPrimeActivite(simulationAides, demandeurEmploi, dateDebutSimulation,
		numeroMoisSimule);

	verserAide(aidesPourCeMois, openFiscaRetourSimulation);
    }

    private void verserAideLogement(Simulation simulationAides, Map<String, Aide> aidesPourCeMois, LocalDate dateDebutSimulation, int numeroMoisSimule, DemandeurEmploi demandeurEmploi) {
	OpenFiscaRetourSimulation openFiscaRetourSimulation = openFiscaClient.calculerAideLogement(simulationAides, demandeurEmploi, dateDebutSimulation, numeroMoisSimule);

	verserAide(aidesPourCeMois, openFiscaRetourSimulation);
    }

    private void verserPrimeActiviteEtRSA(Simulation simulationAides, Map<String, Aide> aidesPourCeMois, LocalDate dateDebutSimulation, int numeroMoisSimule, DemandeurEmploi demandeurEmploi) {
	OpenFiscaRetourSimulation openFiscaRetourSimulation = openFiscaClient.calculerRsaAvecPrimeActivite(simulationAides, demandeurEmploi, dateDebutSimulation, numeroMoisSimule);

	verserAide(aidesPourCeMois, openFiscaRetourSimulation);
    }

    private void verserPrimeActivite(Simulation simulationAides, Map<String, Aide> aidesPourCeMois, LocalDate dateDebutSimulation, int numeroMoisSimule, DemandeurEmploi demandeurEmploi) {
	OpenFiscaRetourSimulation openFiscaRetourSimulation = openFiscaClient.calculerPrimeActivite(simulationAides, demandeurEmploi, dateDebutSimulation, numeroMoisSimule);

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
	    Aide rsa = primeActiviteRSAUtile.creerAideRSA(openFiscaRetourSimulation.getMontantRSA(), false);
	    aidesPourCeMois.put(rsa.getCode(), rsa);
	}
    }
}
