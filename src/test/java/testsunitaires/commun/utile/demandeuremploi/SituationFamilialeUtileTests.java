package testsunitaires.commun.utile.demandeuremploi;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import fr.poleemploi.estime.commun.utile.demandeuremploi.SituationFamilialeUtile;
import fr.poleemploi.estime.logique.simulateuraidessociales.poleemploi.aides.Agepi;
import fr.poleemploi.estime.services.ressources.DemandeurEmploi;
import fr.poleemploi.estime.services.ressources.Personne;
import fr.poleemploi.estime.services.ressources.SituationFamiliale;
import utile.tests.UtileTests;


@ContextConfiguration
@SpringBootTest
@TestPropertySource(locations="classpath:application-test.properties")
class SituationFamilialeUtileTests {
    
    @Autowired
    private SituationFamilialeUtile situationFamilialeUtile;
    
    @Autowired
    private UtileTests testUtile;
    
    @Configuration
    @ComponentScan({"utile.tests","fr.poleemploi.estime"})
    public static class SpringConfig {

    }
  
    @Test
    void getNombreEnfantAChargeInferieurAgeTest1() {
        
        //si 1 enfant à charge de 15 ans
        DemandeurEmploi demandeurEmploi =  new DemandeurEmploi();
       
        SituationFamiliale situationFamiliale = new SituationFamiliale();
        List<Personne> personnesACharge = new ArrayList<Personne>();
        testUtile.createPersonne(personnesACharge, 15);        
        situationFamiliale.setPersonnesACharge(personnesACharge);
        demandeurEmploi.setSituationFamiliale(situationFamiliale);
        
        //lorsque l'on appelle getNombreEnfantAChargeInferieurAgeLimite avec age limite Agepi.AGE_MAX_ENFANT
        int nombreEnfant = situationFamilialeUtile.getNombrePersonnesAChargeAgeInferieureAgeLimite(demandeurEmploi, Agepi.AGE_MAX_ENFANT);
        
        //alors le nombre enfant à charge retournée est de 0
        assertThat(nombreEnfant).isEqualTo(0);
    }
    
    @Test
    void getNombreEnfantAChargeInferieurAgeTest2() {
        
        //si 3 enfants à charge de 15 ans, 10 ans et 9 ans
        DemandeurEmploi demandeurEmploi =  new DemandeurEmploi();
        
        SituationFamiliale situationFamiliale = new SituationFamiliale();
        List<Personne> personnesACharge = new ArrayList<Personne>();
        testUtile.createPersonne(personnesACharge, 15);
        testUtile.createPersonne(personnesACharge, 10);
        testUtile.createPersonne(personnesACharge, 9); 
        situationFamiliale.setPersonnesACharge(personnesACharge);
        demandeurEmploi.setSituationFamiliale(situationFamiliale);
        
        //lorsque l'on appelle getNombreEnfantAChargeInferieurAgeLimite avec age limite Agepi.AGE_MAX_ENFANT
        int nombreEnfant = situationFamilialeUtile.getNombrePersonnesAChargeAgeInferieureAgeLimite(demandeurEmploi, Agepi.AGE_MAX_ENFANT);
        
        //alors le nombre enfant à charge retournée est de 1
        assertThat(nombreEnfant).isEqualTo(1);
    }
}
