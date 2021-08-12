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

import fr.poleemploi.estime.clientsexternes.poleemploiio.ressources.DetailIndemnisationESD;
import fr.poleemploi.estime.commun.enumerations.Aides;
import fr.poleemploi.estime.services.ressources.AidesCAF;
import fr.poleemploi.estime.services.ressources.AidesPoleEmploi;
import fr.poleemploi.estime.services.ressources.AllocationARE;
import fr.poleemploi.estime.services.ressources.AllocationASS;
import fr.poleemploi.estime.services.ressources.AllocationsLogement;
import fr.poleemploi.estime.services.ressources.BeneficiaireAides;
import fr.poleemploi.estime.services.ressources.DemandeurEmploi;
import fr.poleemploi.estime.services.ressources.FuturTravail;
import fr.poleemploi.estime.services.ressources.InformationsPersonnelles;
import fr.poleemploi.estime.services.ressources.MoisTravailleAvantSimulation;
import fr.poleemploi.estime.services.ressources.PeriodeTravailleeAvantSimulation;
import fr.poleemploi.estime.services.ressources.Personne;
import fr.poleemploi.estime.services.ressources.RessourcesFinancieres;
import fr.poleemploi.estime.services.ressources.Salaire;
import fr.poleemploi.estime.services.ressources.SituationFamiliale;


@Component
public class UtileTests {

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
        DateTimeFormatter formatter =  DateTimeFormatter.ofPattern("dd-MM-yyyy");
        return  LocalDate.parse(dateString, formatter);
    }   

    public DemandeurEmploi creerBaseDemandeurEmploi(String population, boolean isEnCouple, int nbEnfant) {
        DemandeurEmploi demandeurEmploi = new DemandeurEmploi();
        demandeurEmploi.setIdPoleEmploi("idPoleEmploi");

        InformationsPersonnelles informationsPersonnelles = new InformationsPersonnelles();
        informationsPersonnelles.setNom("DUPONT");
        informationsPersonnelles.setPrenom("DANIEL");
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


    public DetailIndemnisationESD creerDetailIndemnisationESD(String population) {
        switch (population) {
        case "AAH":
            return creerDetailIndemnisationESD(true, false, false, false);    
        case "ARE":
            return creerDetailIndemnisationESD(false, true, false, false);
        case "ASS":
            return creerDetailIndemnisationESD(false, false, true, false);
        case "RSA":
            return creerDetailIndemnisationESD(false, false, false, true);
        case "AAH_ASS":
            return creerDetailIndemnisationESD(true, false, true, false);
        default:
            return null;
        }
    }

    public AllocationsLogement creerallocationsLogement(float montant) {
        AllocationsLogement apl = new AllocationsLogement();
        apl.setMoisN(montant);
        apl.setMoisNMoins1(montant);
        apl.setMoisNMoins2(montant);
        apl.setMoisNMoins3(montant);
        return apl;
    }

    public PeriodeTravailleeAvantSimulation creerPeriodeTravailleeAvantSimulation(
            float salaireBrutMoisMois1, float salaireNetMoisMois1,
            float salaireBrutMoisMois2, float salaireNetMoisMois2,
            float salaireBrutMoisMois3, float salaireNetMoisMois3) {

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
        if(montantNet > 0) {
            moisTravailleAvantSimulation.setSansSalaire(false);
        } else {
            moisTravailleAvantSimulation.setSansSalaire(true);
        }
        moisTravailleAvantSimulation.setSalaire(salaire);
        return moisTravailleAvantSimulation;
    }

    private void initRessourcesFinancieres(RessourcesFinancieres ressourcesFinancieres, String population) {
        switch (population) {
        case "AAH" :
        case "RSA":
            ressourcesFinancieres.setAidesCAF(new AidesCAF());
            break;
        case "ARE":
        case "ASS":
            ressourcesFinancieres.setAidesPoleEmploi(creerAidePoleEmploi(population));
            break;
        case "AAH_ASS":
            ressourcesFinancieres.setAidesCAF(new AidesCAF());
            ressourcesFinancieres.setAidesPoleEmploi(creerAidePoleEmploi(Aides.ALLOCATION_SOLIDARITE_SPECIFIQUE.getCode()));            
        default:
            break;
        }        
    }

    private void intiSituationFamiliale(boolean isEnCouple, int nbEnfant, SituationFamiliale situationFamiliale) {
        situationFamiliale.setIsEnCouple(isEnCouple);
        if(isEnCouple) {
            Personne conjoint = new Personne();
            situationFamiliale.setConjoint(conjoint);
        }
        if(nbEnfant > 0) {
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

    private DetailIndemnisationESD creerDetailIndemnisationESD(boolean beneficiaireAAH, boolean beneficiaireARE, boolean beneficiaireASS, boolean beneficiaireRSA) {
        DetailIndemnisationESD detailIndemnisationESD = new DetailIndemnisationESD();
        detailIndemnisationESD.setBeneficiaireAAH(beneficiaireAAH);
        detailIndemnisationESD.setBeneficiaireAssuranceChomage(beneficiaireARE);
        detailIndemnisationESD.setBeneficiaireAideSolidarite(beneficiaireASS);
        detailIndemnisationESD.setBeneficiaireRSA(beneficiaireRSA);
        return detailIndemnisationESD;
    }

    private AidesPoleEmploi creerAidePoleEmploi(String population) {
        AidesPoleEmploi aidesPoleEmploi = new AidesPoleEmploi();
        if(Aides.ALLOCATION_SOLIDARITE_SPECIFIQUE.getCode().equals(population)) {
            AllocationASS allocationASS = new AllocationASS();
            aidesPoleEmploi.setAllocationASS(allocationASS);
        }
        if(Aides.ALLOCATION_RETOUR_EMPLOI.getCode().equals(population)) {
            AllocationARE allocationARE = new AllocationARE();
            aidesPoleEmploi.setAllocationARE(allocationARE);
        }
        return aidesPoleEmploi;
    }
}
