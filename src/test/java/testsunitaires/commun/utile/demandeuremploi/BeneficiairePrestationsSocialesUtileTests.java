package testsunitaires.commun.utile.demandeuremploi;

import static org.assertj.core.api.Assertions.assertThat;

import java.text.ParseException;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import fr.poleemploi.estime.commun.enumerations.TypePopulation;
import fr.poleemploi.estime.commun.utile.demandeuremploi.BeneficiaireAidesUtile;
import fr.poleemploi.estime.services.ressources.DemandeurEmploi;
import utile.tests.Utile;


@SpringBootTest
@ContextConfiguration
@TestPropertySource(locations="classpath:application-test.properties")
class BeneficiaireAidesUtileTests {
    
    public static final String CODE_POSTAL_MAYOTTE = "97600";
    public static final String CODE_POSTAL_METROPOLITAIN = "72300";
    
    @Autowired
    private BeneficiaireAidesUtile beneficiaireAidesUtile;
    
    @Autowired
    private Utile utileTests;
    
    @Configuration
    @ComponentScan({"utile.tests","fr.poleemploi.estime"})
    public static class SpringConfig {

    }
    
    @Test
    void isBeneficiaireAREAvecMontantAREInferieurEgaleSeuilMaxEligibiliteTest1() throws ParseException {
        
        //si DE France Metropolitaine et montant journalier = 29,38€ (= seuil max.)
        DemandeurEmploi demandeurEmploi =  utileTests.creerBaseDemandeurEmploi(TypePopulation.ARE.getLibelle(), false, 0);
        demandeurEmploi.getInformationsPersonnelles().setCodePostal(CODE_POSTAL_METROPOLITAIN);
        demandeurEmploi.getRessourcesFinancieres().getAidesPoleEmploi().getAllocationARE().setAllocationJournaliereNet(29.38f);

        //lorsque l'on appelle isBeneficiaireAidePEouCAF
        boolean beneficiaire = beneficiaireAidesUtile.isBeneficiaireAidePEouCAF(demandeurEmploi);
        
        //alors le résultat est DE beneficiaire
        assertThat(beneficiaire).isTrue();
    }
    
    @Test
    void isBeneficiaireAREAvecMontantAREInferieurEgaleSeuilMaxEligibiliteTest2() throws ParseException {
        
        //si DE France Metropolitaine et montant journalier = 18,38€ (< seuil max.)
        DemandeurEmploi demandeurEmploi =  utileTests.creerBaseDemandeurEmploi(TypePopulation.ARE.getLibelle(), false, 0);
        demandeurEmploi.getInformationsPersonnelles().setCodePostal(CODE_POSTAL_METROPOLITAIN);
        demandeurEmploi.getRessourcesFinancieres().getAidesPoleEmploi().getAllocationARE().setAllocationJournaliereNet(18.38f);
        
        //lorsque l'on appelle isBeneficiaireAidePEouCAF
        boolean beneficiaire = beneficiaireAidesUtile.isBeneficiaireAidePEouCAF(demandeurEmploi);
        
        //alors le résultat est DE beneficiaire
        assertThat(beneficiaire).isTrue();
    }
    
    @Test
    void isBeneficiaireAREAvecMontantAREInferieurEgaleSeuilMaxEligibiliteTest3() throws ParseException {
        
        //si DE Mayotte et montant journalier = 14.68€ (= seuil max.)
        DemandeurEmploi demandeurEmploi =  utileTests.creerBaseDemandeurEmploi(TypePopulation.ARE.getLibelle(), false, 0);
        demandeurEmploi.getInformationsPersonnelles().setCodePostal(CODE_POSTAL_METROPOLITAIN);
        demandeurEmploi.getRessourcesFinancieres().getAidesPoleEmploi().getAllocationARE().setAllocationJournaliereNet(14.68f);      
        
        //lorsque l'on appelle isBeneficiaireAidePEouCAF
        boolean beneficiaire = beneficiaireAidesUtile.isBeneficiaireAidePEouCAF(demandeurEmploi);
        
        //alors le résultat est DE beneficiaire
        assertThat(beneficiaire).isTrue();
    }
    
    @Test
    void isBeneficiaireAREAvecMontantAREInferieurEgaleSeuilMaxEligibiliteTest4() throws ParseException {
        
        //si DE Mayotte et montant journalier = 12.68€ (< seuil max.)
        DemandeurEmploi demandeurEmploi =  utileTests.creerBaseDemandeurEmploi(TypePopulation.ARE.getLibelle(), false, 0);
        demandeurEmploi.getInformationsPersonnelles().setCodePostal(CODE_POSTAL_MAYOTTE);
        demandeurEmploi.getRessourcesFinancieres().getAidesPoleEmploi().getAllocationARE().setAllocationJournaliereNet(12.68f);  
        
        //lorsque l'on appelle isBeneficiaireAidePEouCAF
        boolean beneficiaire = beneficiaireAidesUtile.isBeneficiaireAidePEouCAF(demandeurEmploi);
        
        //alors le résultat est DE beneficiaire
        assertThat(beneficiaire).isTrue();
    }
    
    @Test
    void isNotBeneficiaireAREAvecMontantAREInferieurEgaleSeuilMaxEligibiliteTest1() throws ParseException {
        
        //si DE Mayotte et montant journalier = 39,38€ (> seuil max.)
        DemandeurEmploi demandeurEmploi =  utileTests.creerBaseDemandeurEmploi(TypePopulation.ARE.getLibelle(), false, 0);
        demandeurEmploi.getInformationsPersonnelles().setCodePostal(CODE_POSTAL_MAYOTTE);
        demandeurEmploi.getRessourcesFinancieres().getAidesPoleEmploi().getAllocationARE().setAllocationJournaliereNet(39.38f);        
        
        //lorsque l'on appelle isBeneficiaireAidePEouCAF
        boolean beneficiaire = beneficiaireAidesUtile.isBeneficiaireAidePEouCAF(demandeurEmploi);
        
        //alors le résultat est DE beneficiaire
        assertThat(beneficiaire).isFalse();
    }
    
    @Test
    void isNotBeneficiaireAREAvecMontantAREInferieurEgaleSeuilMaxEligibiliteTest2() throws ParseException {
        
        //si DE Mayotte et montant journalier = 16.68€ (> seuil max.)
        DemandeurEmploi demandeurEmploi =  utileTests.creerBaseDemandeurEmploi(TypePopulation.ARE.getLibelle(), false, 0);
        demandeurEmploi.getInformationsPersonnelles().setCodePostal(CODE_POSTAL_MAYOTTE);
        demandeurEmploi.getRessourcesFinancieres().getAidesPoleEmploi().getAllocationARE().setAllocationJournaliereNet(16.68f);   
        
        //lorsque l'on appelle isBeneficiaireAidePEouCAF
        boolean beneficiaire = beneficiaireAidesUtile.isBeneficiaireAidePEouCAF(demandeurEmploi);
        
        //alors le résultat est DE beneficiaire
        assertThat(beneficiaire).isFalse();
    }
}
