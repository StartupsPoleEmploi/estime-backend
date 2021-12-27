package testsunitaires.logique.simulateur.prestationssociales.poleemploi.utile;

import static org.assertj.core.api.Assertions.assertThat;

import java.text.ParseException;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import fr.poleemploi.estime.commun.enumerations.TypeContratTravailEnum;
import fr.poleemploi.estime.commun.enumerations.TypePopulationEnum;
import fr.poleemploi.estime.logique.simulateur.aides.poleemploi.utile.AgepiUtile;
import fr.poleemploi.estime.services.ressources.DemandeurEmploi;
import utile.tests.Utile;


@ContextConfiguration
@SpringBootTest
@TestPropertySource(locations="classpath:application-test.properties")
class AgepiUtileTestsMayotte1 {
    
    public static final String CODE_POSTAL_MAYOTTE = "97600";
    public static final String CODE_POSTAL_METROPOLITAIN = "72300";
    public static final int PREMIER_MOIS_SIMULATION = 1;
    
    @Autowired
    private AgepiUtile agepi;
    
    @Autowired
    Utile utile;
    
    @Configuration
    @ComponentScan({"utile.tests","fr.poleemploi.estime"})
    public static class SpringConfig {

    }

/**************************************  département Mayotte *****************************************************/
    
    @Test
    void isEligibleMayotteTest1() {
        
        //Si DE Mayotte, futur contrat CDI, pas en couple, 1 enfant 9 ans à charge et allocation journalière net = 14.68€ (= seuil max.)
        boolean isEnCouple = false;
        int nbEnfant = 1;
        DemandeurEmploi demandeurEmploi = utile.creerBaseDemandeurEmploi(TypePopulationEnum.ARE.getLibelle(), isEnCouple, nbEnfant);
        demandeurEmploi.getFuturTravail().setTypeContrat(TypeContratTravailEnum.CDI.name());
        demandeurEmploi.getInformationsPersonnelles().setCodePostal(CODE_POSTAL_MAYOTTE);
        demandeurEmploi.getRessourcesFinancieres().getAidesPoleEmploi().getAllocationARE().setAllocationJournaliereNet(14.68f);
        demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(0).getInformationsPersonnelles().setDateNaissance(utile.getDateNaissanceFromAge(9));                      
        
        //Lorsque l'on vérifie son éligibilité
        boolean isEligible = agepi.isEligible(PREMIER_MOIS_SIMULATION, demandeurEmploi);
        
        //Alors le DE est éligible à l'AGEPI
        assertThat(isEligible).isTrue();
    }
    
    @Test
    void isEligibleMayotteTest2() {
        
        //Si DE Mayotte, futur contrat CDI, pas en couple, 1 enfant 9 ans à charge et allocation journalière net = 14.38€ (< seuil max.)
        boolean isEnCouple = false;
        int nbEnfant = 1;
        DemandeurEmploi demandeurEmploi = utile.creerBaseDemandeurEmploi(TypePopulationEnum.ARE.getLibelle(), isEnCouple, nbEnfant);
        demandeurEmploi.getFuturTravail().setTypeContrat(TypeContratTravailEnum.CDI.name());
        demandeurEmploi.getInformationsPersonnelles().setCodePostal(CODE_POSTAL_MAYOTTE);
        demandeurEmploi.getRessourcesFinancieres().getAidesPoleEmploi().getAllocationARE().setAllocationJournaliereNet(14.38f);
        demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(0).getInformationsPersonnelles().setDateNaissance(utile.getDateNaissanceFromAge(9));                      
               
        //Lorsque l'on vérifie son éligibilité
        boolean isEligible = agepi.isEligible(PREMIER_MOIS_SIMULATION, demandeurEmploi);
        
        //Alors le DE est éligible à l'AGEPI
        assertThat(isEligible).isTrue();
    }
    
    @Test
    void isEligibleMayotteTest3() throws ParseException {
        
        //Si DE Mayotte, futur contrat CDD de 3 mois ( = minimum exigé) , pas en couple, 1 enfant 9 ans à charge et allocation journalière net = 14.38€ (< seuil max.)
        boolean isEnCouple = false;
        int nbEnfant = 1;
        DemandeurEmploi demandeurEmploi = utile.creerBaseDemandeurEmploi(TypePopulationEnum.ARE.getLibelle(), isEnCouple, nbEnfant);
        demandeurEmploi.getFuturTravail().setTypeContrat(TypeContratTravailEnum.CDD.name());
        demandeurEmploi.getFuturTravail().setNombreMoisContratCDD(3);
        demandeurEmploi.getInformationsPersonnelles().setCodePostal(CODE_POSTAL_MAYOTTE);
        demandeurEmploi.getRessourcesFinancieres().getAidesPoleEmploi().getAllocationARE().setAllocationJournaliereNet(14.38f);
        demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(0).getInformationsPersonnelles().setDateNaissance(utile.getDateNaissanceFromAge(9));                       
               
        //Lorsque l'on vérifie son éligibilité
        boolean isEligible = agepi.isEligible(PREMIER_MOIS_SIMULATION, demandeurEmploi);
        
        //Alors le DE est éligible à l'AGEPI
        assertThat(isEligible).isTrue();
    }
    
    @Test
    void isEligibleMayotteTest4() throws ParseException {
        
        //Si DE Mayotte, futur contrat CDD de 4 mois ( > minimum exigé), pas en couple, 1 enfant 9 ans à charge et allocation journalière net = 14.38€ (< seuil max.)
        boolean isEnCouple = false;
        int nbEnfant = 1;
        DemandeurEmploi demandeurEmploi = utile.creerBaseDemandeurEmploi(TypePopulationEnum.ARE.getLibelle(), isEnCouple, nbEnfant);
        demandeurEmploi.getFuturTravail().setTypeContrat(TypeContratTravailEnum.CDD.name());
        demandeurEmploi.getFuturTravail().setNombreMoisContratCDD(4);
        demandeurEmploi.getInformationsPersonnelles().setCodePostal(CODE_POSTAL_MAYOTTE);
        demandeurEmploi.getRessourcesFinancieres().getAidesPoleEmploi().getAllocationARE().setAllocationJournaliereNet(14.38f);
        demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(0).getInformationsPersonnelles().setDateNaissance(utile.getDateNaissanceFromAge(9));                       
                 
        //Lorsque l'on vérifie son éligibilité
        boolean isEligible = agepi.isEligible(PREMIER_MOIS_SIMULATION, demandeurEmploi);
        
        //Alors le DE est éligible à l'AGEPI
        assertThat(isEligible).isTrue();
    }
    
    @Test
    void isEligibleMayotteTest5() {
        
        //Si DE Mayotte, futur contrat CDI, pas en couple, 1 enfant 9 ans à charge et bénéficiaire du RSA
        boolean isEnCouple = false;
        int nbEnfant = 1;
        DemandeurEmploi demandeurEmploi = utile.creerBaseDemandeurEmploi(TypePopulationEnum.RSA.getLibelle(), isEnCouple, nbEnfant);
        demandeurEmploi.getFuturTravail().setTypeContrat(TypeContratTravailEnum.CDI.name());
        demandeurEmploi.getInformationsPersonnelles().setCodePostal(CODE_POSTAL_MAYOTTE);       
        demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(0).getInformationsPersonnelles().setDateNaissance(utile.getDateNaissanceFromAge(9));                               
        
        //Lorsque l'on vérifie son éligibilité
        boolean isEligible = agepi.isEligible(PREMIER_MOIS_SIMULATION, demandeurEmploi);
        
        //Alors le DE est éligible à l'AGEPI
        assertThat(isEligible).isTrue();
    }
    
    @Test
    void isEligibleMayotteTest6() {
        
        //Si DE Mayotte, futur contrat CDI, pas en couple, 1 enfant 9 ans à charge et bénéficiaire de AAH
        boolean isEnCouple = false;
        int nbEnfant = 1;
        DemandeurEmploi demandeurEmploi = utile.creerBaseDemandeurEmploi(TypePopulationEnum.AAH.getLibelle(), isEnCouple, nbEnfant);
        demandeurEmploi.getFuturTravail().setTypeContrat(TypeContratTravailEnum.CDI.name());
        demandeurEmploi.getInformationsPersonnelles().setCodePostal(CODE_POSTAL_MAYOTTE);      
        demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(0).getInformationsPersonnelles().setDateNaissance(utile.getDateNaissanceFromAge(9));                                      
        
        //Lorsque l'on vérifie son éligibilité
        boolean isEligible = agepi.isEligible(PREMIER_MOIS_SIMULATION, demandeurEmploi);
        
        //Alors le DE est éligible à l'AGEPI
        assertThat(isEligible).isTrue();
    }
    
    @Test
    void isEligibleMayotteTest7() {
        
        //Si DE Mayotte, futur contrat CDI, pas en couple, 1 enfant 9 ans à charge et bénéficiaire de ASS
        boolean isEnCouple = false;
        int nbEnfant = 1;
        DemandeurEmploi demandeurEmploi = utile.creerBaseDemandeurEmploi(TypePopulationEnum.ASS.getLibelle(), isEnCouple, nbEnfant);
        demandeurEmploi.getFuturTravail().setTypeContrat(TypeContratTravailEnum.CDI.name());
        demandeurEmploi.getInformationsPersonnelles().setCodePostal(CODE_POSTAL_MAYOTTE);
        demandeurEmploi.getRessourcesFinancieres().getAidesPoleEmploi().getAllocationASS().setAllocationJournaliereNet(14.38f);
        demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(0).getInformationsPersonnelles().setDateNaissance(utile.getDateNaissanceFromAge(9));    

        //Lorsque l'on vérifie son éligibilité
        boolean isEligible = agepi.isEligible(PREMIER_MOIS_SIMULATION, demandeurEmploi);
        
        //Alors le DE est éligible à l'AGEPI
        assertThat(isEligible).isTrue();
    }
    
    @Test
    void isEligibleMayotteTest8() {
        
        //Si DE Mayotte, futur contrat CDI, pas en couple, 1 enfant 9 ans à charge et sans ressources
        boolean isEnCouple = false;
        int nbEnfant = 1;
        DemandeurEmploi demandeurEmploi = utile.creerBaseDemandeurEmploi("NON BENEFICIAIRE", isEnCouple, nbEnfant);
        demandeurEmploi.getFuturTravail().setTypeContrat(TypeContratTravailEnum.CDI.name());
        demandeurEmploi.getInformationsPersonnelles().setCodePostal(CODE_POSTAL_MAYOTTE);
        demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(0).getInformationsPersonnelles().setDateNaissance(utile.getDateNaissanceFromAge(9)); 
        demandeurEmploi.setRessourcesFinancieres(null);
        
        //Lorsque l'on vérifie son éligibilité
        boolean isEligible = agepi.isEligible(PREMIER_MOIS_SIMULATION, demandeurEmploi);
        
        //Alors le DE est éligible à l'AGEPI
        assertThat(isEligible).isTrue();
    }
    
  
}
