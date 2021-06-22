package utiletests;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
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
import fr.poleemploi.estime.commun.enumerations.AidesSociales;
import fr.poleemploi.estime.commun.enumerations.Organismes;
import fr.poleemploi.estime.services.ressources.AideSociale;
import fr.poleemploi.estime.services.ressources.InformationsPersonnelles;
import fr.poleemploi.estime.services.ressources.Personne;
import fr.poleemploi.estime.services.ressources.SimulationMensuelle;

@Component
public class TestUtile {

    @SpyBean
    private RestTemplate restTemplate;

    @Value("${openfisca-api-uri}")
    private String openFiscaURI;
    
    public SimulationMensuelle createSimulationMensuelleASSetRSA(float montantASS, float montantRSA, LocalDate dateSimulation, boolean isRSAReportee) {
        SimulationMensuelle simulationMensuelle = new SimulationMensuelle();
        HashMap<String, AideSociale> aides = new HashMap<>();
        aides.put(AidesSociales.ALLOCATION_SOLIDARITE_SPECIFIQUE.getCode(), createSimulationMensuelAideSocialASS(montantASS));
        aides.put(AidesSociales.RSA.getCode(), createSimulationMensuelAideSocialRSA(montantRSA, isRSAReportee));
        simulationMensuelle.setDatePremierJourMoisSimule(dateSimulation);
        simulationMensuelle.setMesAides(aides);
        return simulationMensuelle;        
    }

    public SimulationMensuelle createSimulationMensuelleASS(float montantASS) {
        SimulationMensuelle simulationMensuelle = new SimulationMensuelle();
        HashMap<String, AideSociale> aides = new HashMap<>();
        aides.put(AidesSociales.ALLOCATION_SOLIDARITE_SPECIFIQUE.getCode(), createSimulationMensuelAideSocialASS(montantASS));
        simulationMensuelle.setMesAides(aides);
        return simulationMensuelle;
    }
    
    public SimulationMensuelle createSimulationMensuelleRSA(float montantRSA, LocalDate dateSimulation, boolean isRSAReportee) {
        SimulationMensuelle simulationMensuelle = new SimulationMensuelle();
        HashMap<String, AideSociale> aides = new HashMap<>();
        aides.put(AidesSociales.RSA.getCode(), createSimulationMensuelAideSocialRSA(montantRSA, isRSAReportee));
        simulationMensuelle.setDatePremierJourMoisSimule(dateSimulation);
        simulationMensuelle.setMesAides(aides);
        return simulationMensuelle;        
    }

    private AideSociale createSimulationMensuelAideSocialASS(float montantASS) {
        AideSociale aideSociale = new AideSociale();
        aideSociale.setCode(AidesSociales.ALLOCATION_SOLIDARITE_SPECIFIQUE.getCode());
        aideSociale.setOrganisme(Organismes.PE.getNomCourt());
        aideSociale.setMontant(montantASS);
        return aideSociale;
    }
    
    private AideSociale createSimulationMensuelAideSocialRSA(float montantRSA, boolean isRSAReportee) {
        AideSociale aideSociale = new AideSociale();
        aideSociale.setCode(AidesSociales.RSA.getCode());
        aideSociale.setReportee(isRSAReportee);
        aideSociale.setOrganisme(Organismes.CAF.getNomCourt());
        aideSociale.setMontant(montantRSA);
        return aideSociale;
    }

    public DetailIndemnisationESD createDetailIndemnisationESDPopulationASS() {
        DetailIndemnisationESD detailIndemnisationESD = new DetailIndemnisationESD();
        detailIndemnisationESD.setBeneficiaireAAH(false);
        detailIndemnisationESD.setBeneficiaireAssuranceChomage(false);
        detailIndemnisationESD.setBeneficiairePrestationSolidarite(true);
        detailIndemnisationESD.setBeneficiaireRSA(false);
        return detailIndemnisationESD;
    }

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
    
    public Personne createPersonne(LocalDate dateNaissance) {
        Personne personne1 = new Personne();
        InformationsPersonnelles informationsPersonnelles = new InformationsPersonnelles();
        informationsPersonnelles.setDateNaissance(dateNaissance);
        personne1.setInformationsPersonnelles(informationsPersonnelles);
        return personne1;
    }

    public void createPersonne(List<Personne> personnesACharge, int age) {
        Personne personne1 = new Personne();
        InformationsPersonnelles informationsPersonnelles = new InformationsPersonnelles();
        informationsPersonnelles.setDateNaissance(getDateNaissanceFromAge(age));
        personne1.setInformationsPersonnelles(informationsPersonnelles);
        personnesACharge.add(personne1);
    }


    public void createPersonne(List<Personne> personnesACharge, LocalDate dateNaissance) {
        Personne personne1 = new Personne();
        InformationsPersonnelles informationsPersonnelles = new InformationsPersonnelles();
        informationsPersonnelles.setDateNaissance(dateNaissance);
        personne1.setInformationsPersonnelles(informationsPersonnelles);
        personnesACharge.add(personne1);
    }

    public LocalDate getDate(String dateString) throws ParseException {
        DateTimeFormatter formatter =  DateTimeFormatter.ofPattern("dd-MM-yyyy");
        return  LocalDate.parse(dateString, formatter);
    }
}
