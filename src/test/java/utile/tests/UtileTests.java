package utile.tests;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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

import fr.poleemploi.estime.services.ressources.InformationsPersonnelles;
import fr.poleemploi.estime.services.ressources.Personne;

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
    
    
}
