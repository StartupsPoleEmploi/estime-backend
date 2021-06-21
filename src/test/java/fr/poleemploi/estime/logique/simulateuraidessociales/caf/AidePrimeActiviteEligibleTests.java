package fr.poleemploi.estime.logique.simulateuraidessociales.caf;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import fr.poleemploi.estime.commun.enumerations.Nationalites;
import fr.poleemploi.estime.logique.simulateuraidessociales.caf.aides.PrimeActivite;
import fr.poleemploi.estime.services.ressources.DemandeurEmploi;
import fr.poleemploi.estime.services.ressources.InformationsPersonnelles;


@ContextConfiguration
@SpringBootTest
@TestPropertySource(locations="classpath:application-test.properties")
class AidePrimeActiviteEligibleTests {

    @Autowired
    private PrimeActivite primeActivite;
    
    @Configuration
    @ComponentScan({"fr.poleemploi.test","fr.poleemploi.estime"})
    public static class SpringConfig {

    }
    
    @Test
    void isEligibleTest1() {
        
        DemandeurEmploi demandeurEmploi = new DemandeurEmploi();
        InformationsPersonnelles informationsPersonnelles = new InformationsPersonnelles();
        informationsPersonnelles.setNationalite(Nationalites.FRANCAISE.getValeur());
        demandeurEmploi.setInformationsPersonnelles(informationsPersonnelles);
        
        boolean isEligible = primeActivite.isEligible(demandeurEmploi);
        
        assertThat(isEligible).isTrue();
    }
    
    @Test
    void isEligibleTest2() {
        
        DemandeurEmploi demandeurEmploi = new DemandeurEmploi();
        InformationsPersonnelles informationsPersonnelles = new InformationsPersonnelles();
        informationsPersonnelles.setNationalite(Nationalites.EUROPEEN_OU_SUISSE.getValeur());
        demandeurEmploi.setInformationsPersonnelles(informationsPersonnelles);
        
        boolean isEligible = primeActivite.isEligible(demandeurEmploi);
        
        assertThat(isEligible).isTrue();
    }
    
    @Test
    void isEligibleTest3() {
        
        DemandeurEmploi demandeurEmploi = new DemandeurEmploi();
        InformationsPersonnelles informationsPersonnelles = new InformationsPersonnelles();
        informationsPersonnelles.setNationalite(Nationalites.AUTRE.getValeur());
        informationsPersonnelles.setTitreSejourEnFranceValide(true);
        demandeurEmploi.setInformationsPersonnelles(informationsPersonnelles);
        
        boolean isEligible = primeActivite.isEligible(demandeurEmploi);
        
        assertThat(isEligible).isTrue();
    }
    
    @Test
    void isEligibleTest4() {
        
        DemandeurEmploi demandeurEmploi = new DemandeurEmploi();
        InformationsPersonnelles informationsPersonnelles = new InformationsPersonnelles();
        informationsPersonnelles.setNationalite(Nationalites.AUTRE.getValeur());
        informationsPersonnelles.setTitreSejourEnFranceValide(false);
        demandeurEmploi.setInformationsPersonnelles(informationsPersonnelles);
        
        boolean isEligible = primeActivite.isEligible(demandeurEmploi);
        
        assertThat(isEligible).isFalse();
    }
}
