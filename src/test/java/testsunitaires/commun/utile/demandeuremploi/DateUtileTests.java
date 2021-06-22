package testsunitaires.commun.utile.demandeuremploi;

import static org.assertj.core.api.Assertions.assertThat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import fr.poleemploi.estime.commun.utile.DateUtile;
import utiletests.TestUtile;


@ContextConfiguration
@SpringBootTest
@TestPropertySource(locations="classpath:application-test.properties")
class DateUtileTests {

    @Autowired
    private DateUtile dateUtile;
    
    @Autowired
    private TestUtile testUtile;
    
    @Configuration
    @ComponentScan({"utiletests","fr.poleemploi.estime"})
    public static class SpringConfig {

    }
    
    @Test
    void getNbrMoisEntreDeuxDatesTest() throws ParseException {
        
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        String dateDebutString = "05-07-2012";
        String dateFinString = "05-12-2012";
 
        Date dateDebut = formatter.parse(dateDebutString);
        Date dateFin = formatter.parse(dateFinString);
        
        long nbrMoisEntreDeuxDates = dateUtile.getNbrMoisEntreDeuxDates(dateDebut, dateFin);
        
        assertThat(nbrMoisEntreDeuxDates).isEqualTo(5); 
    }
    
    @Test
    void getNbrMoisEntreDeuxDatesTest2() throws ParseException {
        
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        String dateDebutString = "25-07-2012";
        String dateFinString = "05-12-2012";
 
        Date dateDebut = formatter.parse(dateDebutString);
        Date dateFin = formatter.parse(dateFinString);
        
        long nbrMoisEntreDeuxDates = dateUtile.getNbrMoisEntreDeuxDates(dateDebut, dateFin);
        
        assertThat(nbrMoisEntreDeuxDates).isEqualTo(4); 
    }
    
    @Test
    void getDatePremierJourDuMois() throws ParseException {
        String dateCouranteString = "13-10-2020";
        LocalDate dateCouranteLocalDate = testUtile.getDate(dateCouranteString);
        
        LocalDate datePremierJourDuMois = dateUtile.getDatePremierJourDuMois(dateCouranteLocalDate);
        
        assertThat(datePremierJourDuMois).satisfies(d -> { 
            assertThat(d.getDayOfMonth()).isEqualTo(1);
            assertThat(d.getMonthValue()).isEqualTo(10);
            assertThat(d.getYear()).isEqualTo(2020);
        });
    }
    
    @Test
    void getDateDernierJourDuMois() throws ParseException {
        String dateCouranteString = "13-10-2020";
        LocalDate dateCouranteLocalDate = testUtile.getDate(dateCouranteString);
        
        LocalDate datePremierJourDuMois = dateUtile.getDateDernierJourDuMois(dateCouranteLocalDate);
        
        assertThat(datePremierJourDuMois).satisfies(d -> { 
            assertThat(d.getDayOfMonth()).isEqualTo(31);
            assertThat(d.getMonthValue()).isEqualTo(10);
            assertThat(d.getYear()).isEqualTo(2020);
        });
    }
    
}
