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
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import fr.poleemploi.estime.clientsexternes.poleemploiio.ressources.DetailIndemnisationPEIOOut;
import fr.poleemploi.estime.commun.enumerations.AideEnum;
import fr.poleemploi.estime.commun.enumerations.TypeContratTravailEnum;
import fr.poleemploi.estime.commun.enumerations.TypePopulationEnum;
import fr.poleemploi.estime.commun.utile.DateUtile;
import fr.poleemploi.estime.services.ressources.Aide;
import fr.poleemploi.estime.services.ressources.AidesCAF;
import fr.poleemploi.estime.services.ressources.AidesFamiliales;
import fr.poleemploi.estime.services.ressources.AidesLogement;
import fr.poleemploi.estime.services.ressources.AidesPoleEmploi;
import fr.poleemploi.estime.services.ressources.AllocationARE;
import fr.poleemploi.estime.services.ressources.AllocationASS;
import fr.poleemploi.estime.services.ressources.AllocationsLogement;
import fr.poleemploi.estime.services.ressources.BeneficiaireAides;
import fr.poleemploi.estime.services.ressources.Coordonnees;
import fr.poleemploi.estime.services.ressources.DemandeurEmploi;
import fr.poleemploi.estime.services.ressources.FuturTravail;
import fr.poleemploi.estime.services.ressources.InformationsPersonnelles;
import fr.poleemploi.estime.services.ressources.Logement;
import fr.poleemploi.estime.services.ressources.MoisTravailleAvantSimulation;
import fr.poleemploi.estime.services.ressources.PeriodeTravailleeAvantSimulation;
import fr.poleemploi.estime.services.ressources.Personne;
import fr.poleemploi.estime.services.ressources.RessourcesFinancieresAvantSimulation;
import fr.poleemploi.estime.services.ressources.Salaire;
import fr.poleemploi.estime.services.ressources.SituationFamiliale;

@Component
public class Utile {

    @SpyBean
    private RestTemplate restTemplate;

    @Autowired
    protected DateUtile dateUtile;

    public static final int NOMBRE_MOIS_SALAIRES_AVANT_SIMULATION = 14;
    public static final int AGE_MAX_ENFANT_AGEPI = 10;

    @Value("${openfisca-api-uri}")
    private String openFiscaURI;

    public Salaire[] ajouterSalaire(Salaire[] salaires, Salaire salaire, int numeroMois) {
	salaires[numeroMois] = salaire;
	return salaires;
    }

    /***
     * Méthode permettant de créer une aide pour le mock de la temporalité sur la création de l'aide après appel peio.
     * Seul le code de l'aide est nécessaire car nous ne testons pas le montant de l'aide 
     * mais juste la présence de l'aide au bon mois dans la temporalité en fonction du profil du DE.
     * Exemple : si DE célibataire avec 1 enfant, une aide avec code AGEPI doit être présente sur le 1er mois.
     * @param codeAide
     * @return
     */
    public Aide creerAidePourMock(String codeAide) {
	Aide aide = new Aide();
	aide.setCode(codeAide);
	return aide;
    }

    public DemandeurEmploi creerBaseDemandeurEmploi(TypePopulationEnum population, boolean isEnCouple, int nbEnfant) {
	DemandeurEmploi demandeurEmploi = new DemandeurEmploi();
	demandeurEmploi.setIdPoleEmploi("idPoleEmploi");

	InformationsPersonnelles informationsPersonnelles = new InformationsPersonnelles();
	informationsPersonnelles.setNom("DUPONT");
	informationsPersonnelles.setPrenom("DANIEL");
	Logement logement = new Logement();
	logement.setStatutOccupationLogement(null);
	logement.setCoordonnees(creerCoordonnees());
	informationsPersonnelles.setLogement(logement);
	demandeurEmploi.setInformationsPersonnelles(informationsPersonnelles);

	demandeurEmploi.setBeneficiaireAides(creerBeneficiaireAides(population));

	SituationFamiliale situationFamiliale = new SituationFamiliale();
	intiSituationFamiliale(isEnCouple, nbEnfant, situationFamiliale);
	demandeurEmploi.setSituationFamiliale(situationFamiliale);

	FuturTravail futurTravail = new FuturTravail();
	futurTravail.setTypeContrat(TypeContratTravailEnum.CDI.name());
	Salaire salaire = new Salaire();
	futurTravail.setSalaire(salaire);
	demandeurEmploi.setFuturTravail(futurTravail);

	RessourcesFinancieresAvantSimulation ressourcesFinancieres = new RessourcesFinancieresAvantSimulation();
	initRessourcesFinancieres(ressourcesFinancieres, population);
	demandeurEmploi.setRessourcesFinancieresAvantSimulation(ressourcesFinancieres);

	return demandeurEmploi;
    }

    public DemandeurEmploi creerBaseDemandeurEmploiParcoursComplementARE() {
	DemandeurEmploi demandeurEmploi = new DemandeurEmploi();
	demandeurEmploi.setIdPoleEmploi("idPoleEmploi");

	FuturTravail futurTravail = new FuturTravail();
	Salaire salaire = new Salaire();
	futurTravail.setSalaire(salaire);
	demandeurEmploi.setFuturTravail(futurTravail);

	RessourcesFinancieresAvantSimulation ressourcesFinancieres = new RessourcesFinancieresAvantSimulation();
	AidesPoleEmploi aidesPoleEmploi = new AidesPoleEmploi();
	AllocationARE allocationARE = new AllocationARE();
	aidesPoleEmploi.setAllocationARE(allocationARE);
	ressourcesFinancieres.setAidesPoleEmploi(aidesPoleEmploi);
	demandeurEmploi.setRessourcesFinancieresAvantSimulation(ressourcesFinancieres);

	return demandeurEmploi;
    }

    public DemandeurEmploi creerBaseDemandeurEmploi(TypePopulationEnum population, boolean isEnCouple, List<Personne> personnesACharge) {
	DemandeurEmploi demandeurEmploi = creerBaseDemandeurEmploi(population, isEnCouple, personnesACharge.size());

	demandeurEmploi.getSituationFamiliale().setPersonnesACharge(personnesACharge);

	return demandeurEmploi;
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

    public DetailIndemnisationPEIOOut creerDetailIndemnisationPEIO(TypePopulationEnum population) {
	switch (population) {
	case AAH:
	    return creerDetailIndemnisationPEIO(true, false, false, false);
	case ARE:
	    return creerDetailIndemnisationPEIO(false, true, false, false);
	case ASS:
	    return creerDetailIndemnisationPEIO(false, false, true, false);
	case RSA:
	    return creerDetailIndemnisationPEIO(false, false, false, true);
	case AAH_ASS:
	    return creerDetailIndemnisationPEIO(true, false, true, false);
	case AAH_ARE:
	    return creerDetailIndemnisationPEIO(true, true, false, false);
	default:
	    return null;
	}
    }

    public MoisTravailleAvantSimulation[] createMoisTravaillesAvantSimulation(Salaire[] salaires) {
	MoisTravailleAvantSimulation[] moisTravaillesAvantSimulation = new MoisTravailleAvantSimulation[NOMBRE_MOIS_SALAIRES_AVANT_SIMULATION];
	int index = 0;
	for (MoisTravailleAvantSimulation moisTravailleAvantSimulation : moisTravaillesAvantSimulation) {
	    moisTravailleAvantSimulation = new MoisTravailleAvantSimulation();
	    Salaire salaire = new Salaire();
	    boolean isSansSalaire = true;
	    LocalDate date = dateUtile.convertDateToLocalDate(new Date());

	    if (index < salaires.length && salaires[index] != null) {
		salaire.setMontantMensuelBrut(salaires[index].getMontantMensuelBrut());
		salaire.setMontantMensuelNet(salaires[index].getMontantMensuelNet());
		if (salaires[index].getMontantMensuelNet() > 0) {
		    isSansSalaire = false;
		}
		date = dateUtile.enleverMoisALocalDate(date, index);
	    }
	    moisTravailleAvantSimulation.setSalaire(salaire);
	    moisTravailleAvantSimulation.setSansSalaire(isSansSalaire);
	    moisTravailleAvantSimulation.setDate(date);
	    moisTravaillesAvantSimulation[index] = moisTravailleAvantSimulation;
	    index++;
	}
	return moisTravaillesAvantSimulation;
    }

    public PeriodeTravailleeAvantSimulation creerPeriodeTravailleeAvantSimulation(float montantNet, float montantBrut, int nombreMois) {

	PeriodeTravailleeAvantSimulation periodeTravailleeAvantSimulation = new PeriodeTravailleeAvantSimulation();
	Salaire[] salaires = creerSalaires(montantNet, montantBrut, nombreMois);
	periodeTravailleeAvantSimulation.setMois(createMoisTravaillesAvantSimulation(salaires));
	return periodeTravailleeAvantSimulation;
    }

    public Salaire[] creerSalaires(float montantNet, float montantBrut, int nombreMois) {
	Salaire[] salaires = new Salaire[NOMBRE_MOIS_SALAIRES_AVANT_SIMULATION];
	//TODO REFACTO JLA pas nécessaire
	for (int index = 0; index < NOMBRE_MOIS_SALAIRES_AVANT_SIMULATION; index++) {
	    salaires[index] = new Salaire();
	    if (index < nombreMois) {
		salaires[index].setMontantMensuelBrut(montantBrut);
		salaires[index].setMontantMensuelNet(montantNet);
	    }
	}
	return salaires;
    }

    public Salaire creerSalaire(float montantNet, float montantBrut) {
	Salaire salaire = new Salaire();
	salaire.setMontantMensuelNet(montantNet);
	salaire.setMontantMensuelBrut(montantBrut);
	return salaire;
    }

    public LocalDate getDate(String dateString) throws ParseException {
	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
	return LocalDate.parse(dateString, formatter);
    }

    public LocalDate getDateNaissanceFromAge(int age) {
	LocalDate dateJour = dateUtile.getDateJour();
	LocalDate minusYears = dateJour.minusYears(age);
	LocalDate minusMonths = minusYears.minusMonths(1);
	return minusMonths;
    }

    public String getStringFromJsonFile(String nameFile) throws JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException {

	URL resource = getClass().getClassLoader().getResource(nameFile);
	File file = new File(resource.toURI());

	JsonObject jsonObject = new JsonObject();
	JsonElement jsonElement = JsonParser.parseReader(new FileReader(file));
	jsonObject = jsonElement.getAsJsonObject();

	return jsonObject.toString();
    }

    private AidesLogement creerAidesLogement() {
	AidesLogement aidesLogement = new AidesLogement();
	aidesLogement.setAidePersonnaliseeLogement(new AllocationsLogement());
	aidesLogement.setAllocationLogementFamiliale(new AllocationsLogement());
	aidesLogement.setAllocationLogementSociale(new AllocationsLogement());
	return aidesLogement;
    }

    private BeneficiaireAides creerBeneficiaireAides(TypePopulationEnum population) {
	switch (population) {
	case AAH:
	    return creerBeneficiaireAides(true, false, false, false);
	case ARE:
	    return creerBeneficiaireAides(false, true, false, false);
	case ASS:
	    return creerBeneficiaireAides(false, false, true, false);
	case RSA:
	    return creerBeneficiaireAides(false, false, false, true);
	case AAH_ASS:
	    return creerBeneficiaireAides(true, false, true, false);
	case AAH_ARE:
	    return creerBeneficiaireAides(true, true, false, false);
	default:
	    return creerBeneficiaireAides(false, false, false, false);
	}
    }

    private DetailIndemnisationPEIOOut creerDetailIndemnisationPEIO(boolean beneficiaireAAH, boolean beneficiaireARE, boolean beneficiaireASS, boolean beneficiaireRSA) {
	DetailIndemnisationPEIOOut detailIndemnisationPEIO = new DetailIndemnisationPEIOOut();
	detailIndemnisationPEIO.setBeneficiaireAAH(beneficiaireAAH);
	detailIndemnisationPEIO.setBeneficiaireAssuranceChomage(beneficiaireARE);
	detailIndemnisationPEIO.setBeneficiaireAideSolidarite(beneficiaireASS);
	detailIndemnisationPEIO.setBeneficiaireRSA(beneficiaireRSA);
	return detailIndemnisationPEIO;
    }

    private void initRessourcesFinancieres(RessourcesFinancieresAvantSimulation ressourcesFinancieres, TypePopulationEnum population) {
	switch (population) {
	case AAH:
	    ressourcesFinancieres.setAidesCAF(creerAideCAF(population.getLibelle()));
	    break;
	case RSA:
	    ressourcesFinancieres.setAidesCAF(creerAideCAF(population.getLibelle()));
	    break;
	case ARE:
	    ressourcesFinancieres.setAidesPoleEmploi(creerAidePoleEmploi(population.getLibelle()));
	    break;
	case ASS:
	    ressourcesFinancieres.setAidesPoleEmploi(creerAidePoleEmploi(population.getLibelle()));
	    break;
	case AAH_ASS:
	    ressourcesFinancieres.setAidesPoleEmploi(creerAidePoleEmploi(AideEnum.ALLOCATION_SOLIDARITE_SPECIFIQUE.getCode()));
	    ressourcesFinancieres.setAidesCAF(creerAideCAF(AideEnum.ALLOCATION_ADULTES_HANDICAPES.getCode()));
	    break;
	case AAH_ARE:
	    ressourcesFinancieres.setAidesPoleEmploi(creerAidePoleEmploi(AideEnum.AIDE_RETOUR_EMPLOI.getCode()));
	    ressourcesFinancieres.setAidesCAF(creerAideCAF(AideEnum.ALLOCATION_ADULTES_HANDICAPES.getCode()));
	    break;
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

    private AidesPoleEmploi creerAidePoleEmploi(String population) {
	AidesPoleEmploi aidesPoleEmploi = new AidesPoleEmploi();
	if (AideEnum.ALLOCATION_SOLIDARITE_SPECIFIQUE.getCode().equals(population)) {
	    AllocationASS allocationASS = new AllocationASS();
	    aidesPoleEmploi.setAllocationASS(allocationASS);
	}
	if (AideEnum.AIDE_RETOUR_EMPLOI.getCode().equals(population)) {
	    AllocationARE allocationARE = new AllocationARE();
	    allocationARE.setAllocationJournaliereBrute(0.0f);
	    aidesPoleEmploi.setAllocationARE(allocationARE);
	}
	if (AideEnum.AIDE_RETOUR_EMPLOI.getCode().equals(population)) {
	    AllocationARE allocationARE = new AllocationARE();
	    allocationARE.setAllocationJournaliereBrute(0.0f);
	    aidesPoleEmploi.setAllocationARE(allocationARE);
	}
	return aidesPoleEmploi;
    }

    private AidesCAF creerAideCAF(String population) {
	AidesCAF aidesCAF = new AidesCAF();
	if (AideEnum.ALLOCATION_ADULTES_HANDICAPES.getCode().equals(population)) {
	    aidesCAF.setAllocationAAH(0f);
	}
	if (AideEnum.RSA.getCode().equals(population)) {
	    aidesCAF.setAllocationRSA(0f);
	    aidesCAF.setProchaineDeclarationTrimestrielle(0);
	}
	return aidesCAF;
    }

    private Coordonnees creerCoordonnees() {
	Coordonnees coordonnees = new Coordonnees();
	coordonnees.setCodePostal("44200");
	coordonnees.setCodeInsee("44109");
	return coordonnees;
    }
}
