package utile.tests;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import fr.poleemploi.estime.clientsexternes.poleemploiio.ressources.AgepiPEIOOut;
import fr.poleemploi.estime.clientsexternes.poleemploiio.ressources.AideMobilitePEIOOut;
import fr.poleemploi.estime.clientsexternes.poleemploiio.ressources.ArePEIOOut;
import fr.poleemploi.estime.clientsexternes.poleemploiio.ressources.DecisionAgepiAPI;
import fr.poleemploi.estime.clientsexternes.poleemploiio.ressources.DecisionAideMobiliteAPI;
import fr.poleemploi.estime.clientsexternes.poleemploiio.ressources.DetailIndemnisationPEIO;
import fr.poleemploi.estime.commun.enumerations.Aides;
import fr.poleemploi.estime.services.ressources.AidesCAF;
import fr.poleemploi.estime.services.ressources.AidesFamiliales;
import fr.poleemploi.estime.services.ressources.AidesLogement;
import fr.poleemploi.estime.services.ressources.AidesPoleEmploi;
import fr.poleemploi.estime.services.ressources.AllocationARE;
import fr.poleemploi.estime.services.ressources.AllocationASS;
import fr.poleemploi.estime.services.ressources.AllocationsLogement;
import fr.poleemploi.estime.services.ressources.BeneficiaireAides;
import fr.poleemploi.estime.services.ressources.DemandeurEmploi;
import fr.poleemploi.estime.services.ressources.FuturTravail;
import fr.poleemploi.estime.services.ressources.InformationsPersonnelles;
import fr.poleemploi.estime.services.ressources.Logement;
import fr.poleemploi.estime.services.ressources.MoisTravailleAvantSimulation;
import fr.poleemploi.estime.services.ressources.PeriodeTravailleeAvantSimulation;
import fr.poleemploi.estime.services.ressources.Personne;
import fr.poleemploi.estime.services.ressources.RessourcesFinancieres;
import fr.poleemploi.estime.services.ressources.Salaire;
import fr.poleemploi.estime.services.ressources.SituationFamiliale;
import fr.poleemploi.estime.services.ressources.StatutOccupationLogement;

@Component
public class Utile {

    @SpyBean
    private RestTemplate restTemplate;

    @Value("${openfisca-api-uri}")
    private String openFiscaURI;

    public String getStringFromJsonFile(String nameFile) throws JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException {

	URL resource = getClass().getClassLoader().getResource(nameFile);
	File file = new File(resource.toURI());

	JsonObject jsonObject = new JsonObject();
	JsonElement jsonElement = JsonParser.parseReader(new FileReader(file));
	jsonObject = jsonElement.getAsJsonObject();

	return jsonObject.toString();
    }

    public LocalDate getDateNaissanceFromAge(int age) {
	LocalDate dateJour = LocalDate.now();
	LocalDate minusYears = dateJour.minusYears(age);
	LocalDate minusMonths = minusYears.minusMonths(1);
	return minusMonths;
    }

    public LocalDate getDate(String dateString) throws ParseException {
	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
	return LocalDate.parse(dateString, formatter);
    }

    public DemandeurEmploi creerBaseDemandeurEmploi(String population, boolean isEnCouple, int nbEnfant) {
	DemandeurEmploi demandeurEmploi = new DemandeurEmploi();
	demandeurEmploi.setIdPoleEmploi("idPoleEmploi");

	InformationsPersonnelles informationsPersonnelles = new InformationsPersonnelles();
	informationsPersonnelles.setNom("DUPONT");
	informationsPersonnelles.setPrenom("DANIEL");
	Logement logement = new Logement();
	StatutOccupationLogement statutOccupationLogement = new StatutOccupationLogement();
	logement.setStatutOccupationLogement(statutOccupationLogement);
	informationsPersonnelles.setLogement(logement);
	demandeurEmploi.setInformationsPersonnelles(informationsPersonnelles);

	demandeurEmploi.setBeneficiaireAides(creerBeneficiaireAides(population));

	SituationFamiliale situationFamiliale = new SituationFamiliale();
	intiSituationFamiliale(isEnCouple, nbEnfant, situationFamiliale);
	demandeurEmploi.setSituationFamiliale(situationFamiliale);

	FuturTravail futurTravail = new FuturTravail();
	Salaire salaire = new Salaire();
	futurTravail.setSalaire(salaire);
	demandeurEmploi.setFuturTravail(futurTravail);

	RessourcesFinancieres ressourcesFinancieres = new RessourcesFinancieres();
	initRessourcesFinancieres(ressourcesFinancieres, population);
	demandeurEmploi.setRessourcesFinancieres(ressourcesFinancieres);

	return demandeurEmploi;
    }

    public BeneficiaireAides creerBeneficiaireAides(String population) {
	switch (population) {
	case "AAH":
	    return creerBeneficiaireAides(true, false, false, false);
	case "ARE":
	    return creerBeneficiaireAides(false, true, false, false);
	case "ASS":
	    return creerBeneficiaireAides(false, false, true, false);
	case "RSA":
	    return creerBeneficiaireAides(false, false, false, true);
	case "AAH_ASS":
	    return creerBeneficiaireAides(true, false, true, false);
	default:
	    return creerBeneficiaireAides(false, false, false, false);
	}
    }

    public DetailIndemnisationPEIO creerDetailIndemnisationPEIO(String population) {
	switch (population) {
	case "AAH":
	    return creerDetailIndemnisationPEIO(true, false, false, false);
	case "ARE":
	    return creerDetailIndemnisationPEIO(false, true, false, false);
	case "ASS":
	    return creerDetailIndemnisationPEIO(false, false, true, false);
	case "RSA":
	    return creerDetailIndemnisationPEIO(false, false, false, true);
	case "AAH_ASS":
	    return creerDetailIndemnisationPEIO(true, false, true, false);
	default:
	    return null;
	}
    }

    public AgepiPEIOOut creerAgepiPEIOOut(boolean decisionAgepi) {
	AgepiPEIOOut agepiPEIOOut = new AgepiPEIOOut();
	DecisionAgepiAPI decisionAgepiAPI = new DecisionAgepiAPI();
	if (decisionAgepi) {
	    decisionAgepiAPI.setMontant(400f);
	    decisionAgepiAPI.setDateDecision("20-10-2020");

	} else {
	    decisionAgepiAPI.setMontant(0);
	    decisionAgepiAPI.setDateDecision("20-10-2020");
	}
	agepiPEIOOut.setDecisionAgepiAPI(decisionAgepiAPI);

	return agepiPEIOOut;
    }

    public AideMobilitePEIOOut creerAideMobilitePEIOOut(boolean decisionAideMobilite) {
	AideMobilitePEIOOut aideMobilitePEIOut = new AideMobilitePEIOOut();
	DecisionAideMobiliteAPI decisionAideMobiliteAPI = new DecisionAideMobiliteAPI();
	if (decisionAideMobilite) {
	    decisionAideMobiliteAPI.setMontant(400f);
	    decisionAideMobiliteAPI.setDateDecision("20-10-2020");

	} else {
	    decisionAideMobiliteAPI.setMontant(0);
	    decisionAideMobiliteAPI.setDateDecision("20-10-2020");
	}
	aideMobilitePEIOut.setDecisionAideMobiliteAPI(decisionAideMobiliteAPI);

	return aideMobilitePEIOut;
    }

    public ArePEIOOut creerArePEIOOut(boolean decisionAre) {
	ArePEIOOut arePEIOOut = new ArePEIOOut();
	if (decisionAre) {
	    arePEIOOut.setAllocationMensuelle(400f);
	    arePEIOOut.setMontantCRC(0);
	    arePEIOOut.setMontantCRDS(0);
	    arePEIOOut.setMontantCSG(0);
	    arePEIOOut.setSalaireRetenuActiviteReprise(0);
	    arePEIOOut.setSoldePrevisionnelReliquat(0);

	} else {
	    arePEIOOut.setAllocationMensuelle(0f);
	    arePEIOOut.setMontantCRC(0);
	    arePEIOOut.setMontantCRDS(0);
	    arePEIOOut.setMontantCSG(0);
	    arePEIOOut.setSalaireRetenuActiviteReprise(0);
	    arePEIOOut.setSoldePrevisionnelReliquat(0);
	}
	return arePEIOOut;
    }

    public AidesLogement creerAidePersonnaliseeLogement(float montant) {
	AidesLogement aidesLogement = creerAidesLogement();
	AllocationsLogement aidePersonnaliseeLogement = new AllocationsLogement();
	aidePersonnaliseeLogement.setMoisNMoins1(montant);
	aidePersonnaliseeLogement.setMoisNMoins2(montant);
	aidePersonnaliseeLogement.setMoisNMoins3(montant);
	aidesLogement.setAidePersonnaliseeLogement(aidePersonnaliseeLogement);
	return aidesLogement;
    }

    public AidesLogement creerAidesLogement() {
	AidesLogement aidesLogement = new AidesLogement();
	aidesLogement.setAidePersonnaliseeLogement(new AllocationsLogement());
	aidesLogement.setAllocationLogementFamiliale(new AllocationsLogement());
	aidesLogement.setAllocationLogementSociale(new AllocationsLogement());
	return aidesLogement;
    }

    public PeriodeTravailleeAvantSimulation creerPeriodeTravailleeAvantSimulation(float salaireBrutMoisMois1, float salaireNetMoisMois1, float salaireBrutMoisMois2, float salaireNetMoisMois2, float salaireBrutMoisMois3, float salaireNetMoisMois3) {

	PeriodeTravailleeAvantSimulation periodeTravailleeAvantSimulation = new PeriodeTravailleeAvantSimulation();
	periodeTravailleeAvantSimulation.setMoisMoins1(creerMoisTravailleAvantSimulation(salaireBrutMoisMois1, salaireNetMoisMois1));
	periodeTravailleeAvantSimulation.setMoisMoins2(creerMoisTravailleAvantSimulation(salaireBrutMoisMois2, salaireNetMoisMois2));
	periodeTravailleeAvantSimulation.setMoisMoins3(creerMoisTravailleAvantSimulation(salaireBrutMoisMois3, salaireNetMoisMois3));

	return periodeTravailleeAvantSimulation;
    }

    private MoisTravailleAvantSimulation creerMoisTravailleAvantSimulation(float montantBrut, float montantNet) {
	MoisTravailleAvantSimulation moisTravailleAvantSimulation = new MoisTravailleAvantSimulation();
	Salaire salaire = new Salaire();
	salaire.setMontantNet(montantNet);
	salaire.setMontantBrut(montantBrut);
	if (montantNet > 0) {
	    moisTravailleAvantSimulation.setSansSalaire(false);
	} else {
	    moisTravailleAvantSimulation.setSansSalaire(true);
	}
	moisTravailleAvantSimulation.setSalaire(salaire);
	return moisTravailleAvantSimulation;
    }

    private void initRessourcesFinancieres(RessourcesFinancieres ressourcesFinancieres, String population) {
	switch (population) {
	case "AAH":
	case "RSA":
	case "ARE":
	case "ASS":
	    ressourcesFinancieres.setAidesPoleEmploi(creerAidePoleEmploi(population));
	    break;
	case "AAH_ASS":
	    ressourcesFinancieres.setAidesPoleEmploi(creerAidePoleEmploi(Aides.ALLOCATION_SOLIDARITE_SPECIFIQUE.getCode()));
	default:
	    break;
	}
	AllocationsLogement allocationsLogement = new AllocationsLogement();
	AidesLogement aidesLogement = new AidesLogement();
	aidesLogement.setAidePersonnaliseeLogement(allocationsLogement);
	aidesLogement.setAllocationLogementFamiliale(allocationsLogement);
	aidesLogement.setAllocationLogementSociale(allocationsLogement);
	AidesFamiliales aidesFamiliales = new AidesFamiliales();
	AidesCAF aidesCAF = new AidesCAF();
	aidesCAF.setAidesLogement(aidesLogement);
	aidesCAF.setAidesFamiliales(aidesFamiliales);
	ressourcesFinancieres.setAidesCAF(aidesCAF);
    }

    private void intiSituationFamiliale(boolean isEnCouple, int nbEnfant, SituationFamiliale situationFamiliale) {
	situationFamiliale.setIsEnCouple(isEnCouple);
	if (isEnCouple) {
	    Personne conjoint = new Personne();
	    situationFamiliale.setConjoint(conjoint);
	}
	if (nbEnfant > 0) {
	    List<Personne> personnesACharge = new ArrayList<>();
	    for (int i = 0; i < nbEnfant; i++) {
		Personne personneACharge = new Personne();
		InformationsPersonnelles informationsPersonnelles = new InformationsPersonnelles();
		personneACharge.setInformationsPersonnelles(informationsPersonnelles);
		personnesACharge.add(personneACharge);
	    }
	    situationFamiliale.setPersonnesACharge(personnesACharge);
	}
    }

    private BeneficiaireAides creerBeneficiaireAides(boolean beneficiaireAAH, boolean beneficiaireARE, boolean beneficiaireASS, boolean beneficiaireRSA) {
	BeneficiaireAides beneficiaireAides = new BeneficiaireAides();
	beneficiaireAides.setBeneficiaireAAH(beneficiaireAAH);
	beneficiaireAides.setBeneficiaireASS(beneficiaireASS);
	beneficiaireAides.setBeneficiaireARE(beneficiaireARE);
	beneficiaireAides.setBeneficiaireRSA(beneficiaireRSA);
	return beneficiaireAides;
    }

    private DetailIndemnisationPEIO creerDetailIndemnisationPEIO(boolean beneficiaireAAH, boolean beneficiaireARE, boolean beneficiaireASS, boolean beneficiaireRSA) {
	DetailIndemnisationPEIO detailIndemnisationPEIO = new DetailIndemnisationPEIO();
	detailIndemnisationPEIO.setBeneficiaireAAH(beneficiaireAAH);
	detailIndemnisationPEIO.setBeneficiaireAssuranceChomage(beneficiaireARE);
	detailIndemnisationPEIO.setBeneficiaireAideSolidarite(beneficiaireASS);
	detailIndemnisationPEIO.setBeneficiaireRSA(beneficiaireRSA);
	return detailIndemnisationPEIO;
    }

    private AidesPoleEmploi creerAidePoleEmploi(String population) {
	AidesPoleEmploi aidesPoleEmploi = new AidesPoleEmploi();
	if (Aides.ALLOCATION_SOLIDARITE_SPECIFIQUE.getCode().equals(population)) {
	    AllocationASS allocationASS = new AllocationASS();
	    aidesPoleEmploi.setAllocationASS(allocationASS);
	}
	if (Aides.ALLOCATION_RETOUR_EMPLOI.getCode().equals(population)) {
	    AllocationARE allocationARE = new AllocationARE();
	    aidesPoleEmploi.setAllocationARE(allocationARE);
	}
	return aidesPoleEmploi;
    }
}
