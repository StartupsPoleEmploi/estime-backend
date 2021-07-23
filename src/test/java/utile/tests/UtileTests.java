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

import fr.poleemploi.estime.clientsexternes.emploistoredev.ressources.DetailIndemnisationESD;
import fr.poleemploi.estime.services.ressources.AllocationsCAF;
import fr.poleemploi.estime.services.ressources.AllocationsLogementMensuellesNetFoyer;
import fr.poleemploi.estime.services.ressources.AllocationsPoleEmploi;
import fr.poleemploi.estime.services.ressources.BeneficiaireAidesSociales;
import fr.poleemploi.estime.services.ressources.DemandeurEmploi;
import fr.poleemploi.estime.services.ressources.FuturTravail;
import fr.poleemploi.estime.services.ressources.InformationsPersonnelles;
import fr.poleemploi.estime.services.ressources.Personne;
import fr.poleemploi.estime.services.ressources.RessourcesFinancieres;
import fr.poleemploi.estime.services.ressources.Salaire;
import fr.poleemploi.estime.services.ressources.SalairesAvantPeriodeSimulation;
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

        demandeurEmploi.setBeneficiaireAidesSociales(creerBeneficiaireAidesSociales(population));

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

    public BeneficiaireAidesSociales creerBeneficiaireAidesSociales(String population) {
        switch (population) {
        case "AAH":
            return creerBeneficiaireAidesSociales(true, false, false, false);    
        case "ARE":
            return creerBeneficiaireAidesSociales(false, true, false, false);
        case "ASS":
            return creerBeneficiaireAidesSociales(false, false, true, false);
        case "RSA":
            return creerBeneficiaireAidesSociales(false, false, false, true);
        default:
            return creerBeneficiaireAidesSociales(false, false, false, false);
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
        default:
            return null;
        }
    }

    public AllocationsLogementMensuellesNetFoyer creerAllocationsLogementMensuellesNetFoyer(float montant) {
        AllocationsLogementMensuellesNetFoyer apl = new AllocationsLogementMensuellesNetFoyer();
        apl.setMoisN(montant);
        apl.setMoisNMoins1(montant);
        apl.setMoisNMoins2(montant);
        apl.setMoisNMoins3(montant);
        return apl;
    }

    public SalairesAvantPeriodeSimulation creerSalairesAvantPeriodeSimulation(
            float salaireNetMoisM0, float salaireBrutMoisM0, 
            float salaireNetMoisMoins1, float salaireBrutMoisMoins1, 
            float salaireNetMoisMoins2, float salaireBrutMoisMoins2) {
        
        SalairesAvantPeriodeSimulation salairesAvantPeriodeSimulation = new SalairesAvantPeriodeSimulation();
        salairesAvantPeriodeSimulation.setSalaireMoisDemandeSimulation(creerSalaire(salaireNetMoisM0, salaireBrutMoisM0));
        salairesAvantPeriodeSimulation.setSalaireMoisMoins1MoisDemandeSimulation(creerSalaire(salaireNetMoisMoins1, salaireBrutMoisMoins1));
        salairesAvantPeriodeSimulation.setSalaireMoisMoins2MoisDemandeSimulation(creerSalaire(salaireNetMoisMoins2, salaireBrutMoisMoins2));

        return salairesAvantPeriodeSimulation;
    }

    private Salaire creerSalaire(float salaireNet, float salaireBrut) {
        Salaire salaireMoisM = new Salaire();
        salaireMoisM.setMontantNet(salaireNet);
        salaireMoisM.setMontantBrut(salaireBrut);
        return salaireMoisM;
    }

    private void initRessourcesFinancieres(RessourcesFinancieres ressourcesFinancieres, String population) {
        switch (population) {
        case "AAH" :
        case "RSA":
            AllocationsCAF allocationsCAF = new AllocationsCAF();
            ressourcesFinancieres.setAllocationsCAF(allocationsCAF);
            break;
        case "ARE":
        case "ASS":
            AllocationsPoleEmploi allocationsPoleEmploi = new AllocationsPoleEmploi();
            ressourcesFinancieres.setAllocationsPoleEmploi(allocationsPoleEmploi);
            break;
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

    private BeneficiaireAidesSociales creerBeneficiaireAidesSociales(boolean beneficiaireAAH, boolean beneficiaireARE, boolean beneficiaireASS, boolean beneficiaireRSA) {
        BeneficiaireAidesSociales beneficiaireAidesSociales = new BeneficiaireAidesSociales();
        beneficiaireAidesSociales.setBeneficiaireAAH(beneficiaireAAH);
        beneficiaireAidesSociales.setBeneficiaireASS(beneficiaireASS);
        beneficiaireAidesSociales.setBeneficiaireARE(beneficiaireARE);
        beneficiaireAidesSociales.setBeneficiaireRSA(beneficiaireRSA);
        return beneficiaireAidesSociales;
    }

    private DetailIndemnisationESD creerDetailIndemnisationESD(boolean beneficiaireAAH, boolean beneficiaireARE, boolean beneficiaireASS, boolean beneficiaireRSA) {
        DetailIndemnisationESD detailIndemnisationESD = new DetailIndemnisationESD();
        detailIndemnisationESD.setBeneficiaireAAH(beneficiaireAAH);
        detailIndemnisationESD.setBeneficiaireAssuranceChomage(beneficiaireARE);
        detailIndemnisationESD.setBeneficiairePrestationSolidarite(beneficiaireASS);
        detailIndemnisationESD.setBeneficiaireRSA(beneficiaireRSA);
        return detailIndemnisationESD;
    }
    
   
}
