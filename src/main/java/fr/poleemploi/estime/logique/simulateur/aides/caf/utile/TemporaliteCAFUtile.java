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

	boolean isAideLogementAVerser = false;
	boolean isRSAAVerser = false;
	boolean isPrimeActiviteAVerser = false;

	isAideLogementAVerser = isAideLogementAVerser(numeroMoisSimule, demandeurEmploi);
	isRSAAVerser = isRSAAVerser(numeroMoisSimule, demandeurEmploi);
	isPrimeActiviteAVerser = isPrimeActiviteAVerser(numeroMoisSimule, demandeurEmploi);

	// Si l'on doit verser l'aide au logement et la prime d'activité pour les demandeurs ASS/AAH/ARE/RSA
	if (isAideLogementAVerser && isRSAAVerser && isPrimeActiviteAVerser) {
	    verserAide(simulation, aidesPourCeMois, dateDebutSimulation, numeroMoisSimule, demandeurEmploi, SituationAppelOpenFiscaEnum.AL_PPA_RSA);
	} else {
	    if (isAideLogementAVerser && isRSAAVerser) {
		verserAide(simulation, aidesPourCeMois, dateDebutSimulation, numeroMoisSimule, demandeurEmploi, SituationAppelOpenFiscaEnum.AL_RSA);
		primeActiviteUtile.reporterPrimeActivite(simulation, aidesPourCeMois, numeroMoisSimule);
	    } else if (isAideLogementAVerser && isPrimeActiviteAVerser) {
		verserAide(simulation, aidesPourCeMois, dateDebutSimulation, numeroMoisSimule, demandeurEmploi, SituationAppelOpenFiscaEnum.AL_PPA);
		rsaUtile.reporterRsa(simulation, aidesPourCeMois, numeroMoisSimule, demandeurEmploi);
	    } else if (isPrimeActiviteAVerser && isRSAAVerser) {
		verserAide(simulation, aidesPourCeMois, dateDebutSimulation, numeroMoisSimule, demandeurEmploi, SituationAppelOpenFiscaEnum.RSA_PPA);
		aidesLogementUtile.reporterAideLogement(simulation, aidesPourCeMois, numeroMoisSimule, demandeurEmploi);
	    } else if (isAideLogementAVerser) {
		verserAide(simulation, aidesPourCeMois, dateDebutSimulation, numeroMoisSimule, demandeurEmploi, SituationAppelOpenFiscaEnum.AL);
		rsaUtile.reporterRsa(simulation, aidesPourCeMois, numeroMoisSimule, demandeurEmploi);
		primeActiviteUtile.reporterPrimeActivite(simulation, aidesPourCeMois, numeroMoisSimule);
	    } else if (isPrimeActiviteAVerser) {
		verserAide(simulation, aidesPourCeMois, dateDebutSimulation, numeroMoisSimule, demandeurEmploi, SituationAppelOpenFiscaEnum.PPA);
		rsaUtile.reporterRsa(simulation, aidesPourCeMois, numeroMoisSimule, demandeurEmploi);
		aidesLogementUtile.reporterAideLogement(simulation, aidesPourCeMois, numeroMoisSimule, demandeurEmploi);
	    } else if (isRSAAVerser) {
		verserAide(simulation, aidesPourCeMois, dateDebutSimulation, numeroMoisSimule, demandeurEmploi, SituationAppelOpenFiscaEnum.RSA);
		aidesLogementUtile.reporterAideLogement(simulation, aidesPourCeMois, numeroMoisSimule, demandeurEmploi);
		primeActiviteUtile.reporterPrimeActivite(simulation, aidesPourCeMois, numeroMoisSimule);
	    } else {
		aidesLogementUtile.reporterAideLogement(simulation, aidesPourCeMois, numeroMoisSimule, demandeurEmploi);
		primeActiviteUtile.reporterPrimeActivite(simulation, aidesPourCeMois, numeroMoisSimule);
		rsaUtile.reporterRsa(simulation, aidesPourCeMois, numeroMoisSimule, demandeurEmploi);
	    }
	}
    }

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

    private void verserAide(Simulation simulation, Map<String, Aide> aidesPourCeMois, LocalDate dateDebutSimulation, int numeroMoisSimule, DemandeurEmploi demandeurEmploi, SituationAppelOpenFiscaEnum situationAppelOpenFisca) {
	OpenFiscaRetourSimulation openFiscaRetourSimulation = openFiscaClient.calculerAidesSelonSituation(simulation, demandeurEmploi, dateDebutSimulation, numeroMoisSimule - 1,
		situationAppelOpenFisca);

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
    }
}
