package fr.poleemploi.estime.logique.simulateur.aides;

import java.time.LocalDate;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fr.poleemploi.estime.clientsexternes.openfisca.OpenFiscaClient;
import fr.poleemploi.estime.clientsexternes.openfisca.OpenFiscaRetourSimulation;
import fr.poleemploi.estime.clientsexternes.openfisca.ressources.OpenFiscaRoot;
import fr.poleemploi.estime.commun.enumerations.SituationAppelOpenFiscaEnum;
import fr.poleemploi.estime.logique.simulateur.aides.caf.utile.AidesLogementUtile;
import fr.poleemploi.estime.logique.simulateur.aides.caf.utile.PrimeActiviteRSAUtile;
import fr.poleemploi.estime.logique.simulateur.aides.caf.utile.PrimeActiviteUtile;
import fr.poleemploi.estime.logique.simulateur.aides.caf.utile.RSAUtile;
import fr.poleemploi.estime.logique.simulateur.aides.caf.utile.TemporaliteCAFUtile;
import fr.poleemploi.estime.logique.simulateur.aides.poleemploi.utile.AgepiUtile;
import fr.poleemploi.estime.logique.simulateur.aides.poleemploi.utile.AideMobiliteUtile;
import fr.poleemploi.estime.logique.simulateur.aides.poleemploi.utile.AreUtile;
import fr.poleemploi.estime.logique.simulateur.aides.poleemploi.utile.TemporalitePEUtile;
import fr.poleemploi.estime.services.ressources.Aide;
import fr.poleemploi.estime.services.ressources.DemandeurEmploi;
import fr.poleemploi.estime.services.ressources.Simulation;

@Component
public class TemporaliteOpenFiscaUtile {

    @Autowired
    private OpenFiscaClient openFiscaClient;

    @Autowired
    private TemporaliteCAFUtile temporaliteCAFUtile;

    @Autowired
    private TemporalitePEUtile temporalitePEUtile;

    @Autowired
    private AidesLogementUtile aidesLogementUtile;

    @Autowired
    private PrimeActiviteUtile primeActiviteUtile;

    @Autowired
    private PrimeActiviteRSAUtile primeActiviteRSAUtile;

    @Autowired
    private RSAUtile rsaUtile;

    @Autowired
    private AreUtile areUtile;

    @Autowired
    private AgepiUtile agepiUtile;

    @Autowired
    private AideMobiliteUtile aideMobiliteUtile;

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
    public void simulerTemporaliteAppelOpenfisca(OpenFiscaRoot openFiscaRoot, Simulation simulation, Map<String, Aide> aidesPourCeMois, LocalDate dateDebutSimulation, int numeroMoisSimule, DemandeurEmploi demandeurEmploi) {

	if (temporalitePEUtile.isAgepiEtAideMobiliteAVerser(numeroMoisSimule)) {
	    verserAide(openFiscaRoot, aidesPourCeMois, dateDebutSimulation, numeroMoisSimule, SituationAppelOpenFiscaEnum.AGEPI_AM);
	}
	// Si l'on doit verser l'aide au logement et la prime d'activité pour les demandeurs ASS/AAH/ARE/RSA
	if (temporaliteCAFUtile.isAideLogementAVerser(numeroMoisSimule, demandeurEmploi)) {
	    verserAide(openFiscaRoot, aidesPourCeMois, dateDebutSimulation, numeroMoisSimule, SituationAppelOpenFiscaEnum.AL);
	} else {
	    aidesLogementUtile.reporterAideLogement(simulation, aidesPourCeMois, numeroMoisSimule, demandeurEmploi);
	}
	if (temporaliteCAFUtile.isRSAAVerser(numeroMoisSimule, demandeurEmploi)) {
	    verserAide(openFiscaRoot, aidesPourCeMois, dateDebutSimulation, numeroMoisSimule, SituationAppelOpenFiscaEnum.RSA);
	} else {
	    rsaUtile.reporterRsa(simulation, aidesPourCeMois, numeroMoisSimule, demandeurEmploi);
	}
	if (temporaliteCAFUtile.isPrimeActiviteAVerser(numeroMoisSimule, demandeurEmploi)) {
	    verserAide(openFiscaRoot, aidesPourCeMois, dateDebutSimulation, numeroMoisSimule, SituationAppelOpenFiscaEnum.PPA);
	} else {
	    primeActiviteUtile.reporterPrimeActivite(simulation, aidesPourCeMois, demandeurEmploi, dateDebutSimulation, numeroMoisSimule);
	}
	if (temporalitePEUtile.isAREAReporter(demandeurEmploi, numeroMoisSimule)) {
	    areUtile.reporterARE(aidesPourCeMois, demandeurEmploi, numeroMoisSimule, dateDebutSimulation);
	}
	if (temporalitePEUtile.isComplementAREAVerser(demandeurEmploi, numeroMoisSimule)) {
	    verserAide(openFiscaRoot, aidesPourCeMois, dateDebutSimulation, numeroMoisSimule, SituationAppelOpenFiscaEnum.ARE);
	}
    }

    public void verserAide(OpenFiscaRoot openFiscaRoot, Map<String, Aide> aidesPourCeMois, LocalDate dateDebutSimulation, int numeroMoisSimule, SituationAppelOpenFiscaEnum situationAppelOpenFisca) {
	OpenFiscaRetourSimulation openFiscaRetourSimulation = openFiscaClient.calculerAidesSelonSituation(openFiscaRoot, dateDebutSimulation, numeroMoisSimule,
		situationAppelOpenFisca);

	if (openFiscaRetourSimulation.getMontantAideLogement() > 0) {
	    Aide aideLogement = aidesLogementUtile.creerAideLogement(openFiscaRetourSimulation.getMontantAideLogement(), openFiscaRetourSimulation.getTypeAideLogement(), false);
	    aidesPourCeMois.put(aideLogement.getCode(), aideLogement);
	}
	if (openFiscaRetourSimulation.getMontantPrimeActivite() > 0) {
	    Aide primeActivite = primeActiviteUtile.creerAidePrimeActivite(openFiscaRetourSimulation.getMontantPrimeActivite(), false, dateDebutSimulation, numeroMoisSimule);
	    aidesPourCeMois.put(primeActivite.getCode(), primeActivite);
	}
	if (openFiscaRetourSimulation.getMontantRSA() > 0) {
	    Aide rsa = rsaUtile.creerAideRSA(openFiscaRetourSimulation.getMontantRSA(), false);
	    aidesPourCeMois.put(rsa.getCode(), rsa);
	}
	if (openFiscaRetourSimulation.getMontantComplementARE() > 0) {
	    Aide complementARE = areUtile.creerComplementARE(openFiscaRetourSimulation.getMontantComplementARE(),
		    openFiscaRetourSimulation.getNombreJoursRestantsARE() - openFiscaRetourSimulation.getNombreJoursIndemnisesComplementARE());
	    aidesPourCeMois.put(complementARE.getCode(), complementARE);
	}
	if (openFiscaRetourSimulation.getMontantAgepi() > 0) {
	    Aide agepi = agepiUtile.creerAgepi(openFiscaRetourSimulation.getMontantAgepi());
	    aidesPourCeMois.put(agepi.getCode(), agepi);
	}
	if (openFiscaRetourSimulation.getMontantAideMobilite() > 0) {
	    Aide aideMobilite = aideMobiliteUtile.creerAideMobilite(openFiscaRetourSimulation.getMontantAideMobilite());
	    aidesPourCeMois.put(aideMobilite.getCode(), aideMobilite);
	}
	if (openFiscaRetourSimulation.getMontantPrimeActivite() > 0 && openFiscaRetourSimulation.getMontantRSA() > 0) {
	    Aide primeActivite = primeActiviteRSAUtile.creerAidePrimeActivite(openFiscaRetourSimulation.getMontantPrimeActivite(), false);
	    aidesPourCeMois.put(primeActivite.getCode(), primeActivite);
	}
    }
}
