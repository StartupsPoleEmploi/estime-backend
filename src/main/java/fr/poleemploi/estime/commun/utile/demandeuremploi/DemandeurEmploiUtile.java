package fr.poleemploi.estime.commun.utile.demandeuremploi;

import java.time.LocalDate;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fr.poleemploi.estime.clientsexternes.poleemploiio.PoleEmploiIOClient;
import fr.poleemploi.estime.clientsexternes.poleemploiio.ressources.CoordonneesPEIOOut;
import fr.poleemploi.estime.clientsexternes.poleemploiio.ressources.EtatCivilPEIOOut;
import fr.poleemploi.estime.commun.enumerations.exceptions.LoggerMessages;
import fr.poleemploi.estime.commun.utile.DateUtile;
import fr.poleemploi.estime.commun.utile.StagingEnvironnementUtile;
import fr.poleemploi.estime.commun.utile.StringUtile;
import fr.poleemploi.estime.services.ressources.AidesCAF;
import fr.poleemploi.estime.services.ressources.AidesLogement;
import fr.poleemploi.estime.services.ressources.AllocationsLogement;
import fr.poleemploi.estime.services.ressources.Coordonnees;
import fr.poleemploi.estime.services.ressources.DemandeurEmploi;
import fr.poleemploi.estime.services.ressources.Individu;
import fr.poleemploi.estime.services.ressources.InformationsPersonnelles;
import fr.poleemploi.estime.services.ressources.Logement;
import fr.poleemploi.estime.services.ressources.RessourcesFinancieresAvantSimulation;

@Component
public class DemandeurEmploiUtile {

    @Autowired
    private PoleEmploiIOClient poleEmploiIOClient;

    @Autowired
    private DateUtile dateUtile;

    @Autowired
    private StringUtile stringUtile;

    @Autowired
    private StagingEnvironnementUtile stagingEnvironnementUtile;

    @Autowired
    private CodeDepartementUtile codeDepartementUtile;

    private static final Logger LOGGER = LoggerFactory.getLogger(DemandeurEmploiUtile.class);

    public void creerDemandeurEmploiVide(DemandeurEmploi demandeurEmploi) {
	demandeurEmploi.setIdEstime(creerIdEstime());
	demandeurEmploi.setRessourcesFinancieresAvantSimulation(creerRessourcesAvantSimulation());
	demandeurEmploi.setInformationsPersonnelles(creerInformationsPersonnelles());
    }

    public String creerIdEstime() {
	return stringUtile.generateRandomString();
    }

    private RessourcesFinancieresAvantSimulation creerRessourcesAvantSimulation() {
	RessourcesFinancieresAvantSimulation ressourcesFinancieresAvantSimulation = new RessourcesFinancieresAvantSimulation();
	ressourcesFinancieresAvantSimulation.setAidesCAF(creerAidesCAF());

	return ressourcesFinancieresAvantSimulation;
    }

    private InformationsPersonnelles creerInformationsPersonnelles() {
	InformationsPersonnelles informationsPersonnelles = new InformationsPersonnelles();
	Logement logement = creerLogement();
	logement.setCoordonnees(creerCoordonnees());
	informationsPersonnelles.setLogement(logement);

	return informationsPersonnelles;
    }

    public boolean isSansRessourcesFinancieres(DemandeurEmploi demandeurEmploi) {
	return demandeurEmploi.getRessourcesFinancieresAvantSimulation() == null;
    }

    public void addRessourcesFinancieres(DemandeurEmploi demandeurEmploi, Individu individu) {
	if (individu.getRessourcesFinancieres() != null) {
	    demandeurEmploi.setRessourcesFinancieresAvantSimulation(individu.getRessourcesFinancieres());
	} else {
	    demandeurEmploi.setRessourcesFinancieresAvantSimulation(new RessourcesFinancieresAvantSimulation());
	}
	demandeurEmploi.getRessourcesFinancieresAvantSimulation().setAidesCAF(creerAidesCAF());
	demandeurEmploi.getRessourcesFinancieresAvantSimulation().setNombreMoisTravaillesDerniersMois(0);
    }

    public void addDateNaissance(InformationsPersonnelles informationsPersonnelles, String bearerToken) {
	try {
	    Optional<EtatCivilPEIOOut> etatCivilPEIOOptional = poleEmploiIOClient.getEtatCivil(bearerToken);
	    if (etatCivilPEIOOptional.isPresent()) {
		EtatCivilPEIOOut etatCivilPEIO = etatCivilPEIOOptional.get();
		if (etatCivilPEIO.getDateDeNaissance() != null) {
		    LocalDate dateNaissanceLocalDate = dateUtile.convertDateToLocalDate(etatCivilPEIO.getDateDeNaissance());
		    informationsPersonnelles.setDateNaissance(dateNaissanceLocalDate);
		}
	    }
	} catch (Exception e) {
	    String messageError = String.format(LoggerMessages.RETOUR_SERVICE_KO.getMessage(), e.getMessage(), "peio api date de naissance");
	    LOGGER.error(messageError);
	}
    }

    public void addCodeDepartement(InformationsPersonnelles informationsPersonnelles, String bearerToken) {
	try {
	    Optional<CoordonneesPEIOOut> coordonneesESDOptional = poleEmploiIOClient.getCoordonnees(bearerToken);
	    if (coordonneesESDOptional.isPresent()) {
		CoordonneesPEIOOut coordonneesESD = coordonneesESDOptional.get();
		addCoordonnees(informationsPersonnelles, coordonneesESD.getCodePostal());
	    }
	} catch (Exception e) {
	    String messageError = String.format(LoggerMessages.RETOUR_SERVICE_KO.getMessage(), e.getMessage(), "peio api coordonnees");
	    LOGGER.error(messageError);
	}
    }

    public void addCoordonnees(InformationsPersonnelles informationsPersonnelles, String codePostal) {
	Coordonnees coordonnees = new Coordonnees();
	coordonnees.setCodePostal(codePostal);
	coordonnees.setDeMayotte(codeDepartementUtile.isDeMayotte(codePostal));
	coordonnees.setDesDOM(codeDepartementUtile.isDesDOM(codePostal));
	informationsPersonnelles.getLogement().setCoordonnees(coordonnees);
    }

    public void addInformationsPersonnelles(DemandeurEmploi demandeurEmploi, Individu individu) {
	InformationsPersonnelles informationsPersonnelles = new InformationsPersonnelles();
	Logement logement = creerLogement();
	logement.setStatutOccupationLogement(null);
	logement.setCoordonnees(creerCoordonnees());
	informationsPersonnelles.setLogement(logement);
	informationsPersonnelles.setEmail(individu.getInformationsPersonnelles().getEmail());

	if (individu.getPeConnectAuthorization() != null) {
	    if (stagingEnvironnementUtile.isNotLocalhostEnvironnement()) {
		String bearerToken = individu.getPeConnectAuthorization().getBearerToken();
		addCodeDepartement(informationsPersonnelles, bearerToken);
		addDateNaissance(informationsPersonnelles, bearerToken);
	    } else {
		stagingEnvironnementUtile.bouchonnerCodeDepartementEtDateNaissance(informationsPersonnelles);
	    }
	}

	demandeurEmploi.setInformationsPersonnelles(informationsPersonnelles);
    }

    public void miseAJourCoordonnees(DemandeurEmploi demandeurEmploi) {
	if (demandeurEmploi.getInformationsPersonnelles() != null && demandeurEmploi.getInformationsPersonnelles().getLogement() != null
		&& demandeurEmploi.getInformationsPersonnelles().getLogement().getCoordonnees() != null
		&& demandeurEmploi.getInformationsPersonnelles().getLogement().getCoordonnees().getCodePostal() != null) {
	    String codePostal = demandeurEmploi.getInformationsPersonnelles().getLogement().getCoordonnees().getCodePostal();
	    Coordonnees coordonnees = demandeurEmploi.getInformationsPersonnelles().getLogement().getCoordonnees();
	    coordonnees.setCodePostal(codePostal);
	    coordonnees.setDeMayotte(codeDepartementUtile.isDeMayotte(codePostal));
	    coordonnees.setDesDOM(codeDepartementUtile.isDesDOM(codePostal));
	    demandeurEmploi.getInformationsPersonnelles().getLogement().setCoordonnees(coordonnees);
	}
    }

    private AidesCAF creerAidesCAF() {
	AidesCAF aidesCAF = new AidesCAF();
	aidesCAF.setAidesLogement(creerAidesLogement());
	return aidesCAF;
    }

    private AidesLogement creerAidesLogement() {
	AidesLogement aidesLogement = new AidesLogement();
	aidesLogement.setAidePersonnaliseeLogement(creerAllocationsLogement());
	aidesLogement.setAllocationLogementFamiliale(creerAllocationsLogement());
	aidesLogement.setAllocationLogementSociale(creerAllocationsLogement());
	return aidesLogement;
    }

    private AllocationsLogement creerAllocationsLogement() {
	AllocationsLogement allocationsLogement = new AllocationsLogement();
	allocationsLogement.setMoisNMoins1(0);
	allocationsLogement.setMoisNMoins2(0);
	allocationsLogement.setMoisNMoins3(0);
	return allocationsLogement;
    }

    private Logement creerLogement() {
	Logement logement = new Logement();
	logement.setChambre(false);
	logement.setConventionne(false);
	logement.setColloc(false);
	logement.setCrous(false);
	logement.setMontantLoyer(null);
	logement.setCoordonnees(null);
	logement.setStatutOccupationLogement(null);
	return logement;
    }

    private Coordonnees creerCoordonnees() {
	Coordonnees coordonnees = new Coordonnees();
	coordonnees.setCodeInsee("");
	coordonnees.setCodePostal("");
	coordonnees.setDeMayotte(false);
	coordonnees.setDesDOM(true);
	return coordonnees;
    }
}
