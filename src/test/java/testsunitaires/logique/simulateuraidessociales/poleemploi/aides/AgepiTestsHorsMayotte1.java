package testsunitaires.logique.simulateuraidessociales.poleemploi.aides;

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
import fr.poleemploi.estime.commun.enumerations.TypesContratTravail;
import fr.poleemploi.estime.logique.simulateuraidessociales.poleemploi.aides.Agepi;
import fr.poleemploi.estime.services.ressources.DemandeurEmploi;
import utile.tests.UtileTests;


@ContextConfiguration
@SpringBootTest
@TestPropertySource(locations="classpath:application-test.properties")
class AgepiTestsHorsMayotte1 {
    
    public static final String CODE_POSTAL_MAYOTTE = "97600";
    public static final String CODE_POSTAL_METROPOLITAIN = "72300";
    public static final int PREMIER_MOIS_SIMULATION = 1;
    
    @Autowired
    private Agepi agepi;
    
    @Autowired
    UtileTests utileTests;
    
    @Configuration
    @ComponentScan({"utile.tests","fr.poleemploi.estime"})
    public static class SpringConfig {

    }
    
    /************* département hors Mayotte *********/
    
    @Test
    void isEligibleTest1() {
        
        //Si DE France Métropolitaine, futur contrat CDI, pas en couple, 1 enfant 9 ans à charge et allocation journalière net = 29.38 euros (= seuil max.)
        boolean isEnCouple = false;
        int nbEnfant = 1;
        DemandeurEmploi demandeurEmploi = utileTests.creerBaseDemandeurEmploi(TypePopulation.ARE.getLibelle(), isEnCouple, nbEnfant);
        demandeurEmploi.getFuturTravail().setTypeContrat(TypesContratTravail.CDI.name());
        demandeurEmploi.getInformationsPersonnelles().setCodePostal(CODE_POSTAL_METROPOLITAIN);
        demandeurEmploi.getRessourcesFinancieres().getAllocationsPoleEmploi().setAllocationJournaliereNet(29.38f);
        demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(0).getInformationsPersonnelles().setDateNaissance(utileTests.getDateNaissanceFromAge(9));
        
        //Lorsque l'on vérifie son éligibilité
        boolean isEligible = agepi.isEligible(PREMIER_MOIS_SIMULATION, demandeurEmploi);
        
        //Alors le DE est éligible à l'AGEPI
        assertThat(isEligible).isTrue();
    }
    
    @Test
    void isEligibleTest2() {
        
        //Si DE France Métropolitaine, futur contrat CDI, pas en couple, 1 enfant 9 ans à charge et allocation journalière net = 14.38 euros (< seuil max.)
        boolean isEnCouple = false;
        int nbEnfant = 1;
        DemandeurEmploi demandeurEmploi = utileTests.creerBaseDemandeurEmploi(TypePopulation.ARE.getLibelle(), isEnCouple, nbEnfant);
        demandeurEmploi.getFuturTravail().setTypeContrat(TypesContratTravail.CDI.name());
        demandeurEmploi.getInformationsPersonnelles().setCodePostal(CODE_POSTAL_METROPOLITAIN);
        demandeurEmploi.getRessourcesFinancieres().getAllocationsPoleEmploi().setAllocationJournaliereNet(14.38f);
        demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(0).getInformationsPersonnelles().setDateNaissance(utileTests.getDateNaissanceFromAge(9));
        
        //Lorsque l'on vérifie son éligibilité
        boolean isEligible = agepi.isEligible(PREMIER_MOIS_SIMULATION, demandeurEmploi);
        
        //Alors le DE est éligible à l'AGEPI
        assertThat(isEligible).isTrue();
    }
    
    @Test
    void isEligibleTest3() throws ParseException {
        
        //Si DE France Métropolitaine, futur contrat CDD de 3 mois ( = minimum exigé) , pas en couple, 1 enfant 9 ans à charge et allocation journalière net = 29.38 euros (= seuil max.)
        boolean isEnCouple = false;
        int nbEnfant = 1;
        DemandeurEmploi demandeurEmploi = utileTests.creerBaseDemandeurEmploi(TypePopulation.ARE.getLibelle(), isEnCouple, nbEnfant);
        demandeurEmploi.getFuturTravail().setTypeContrat(TypesContratTravail.CDD.name());
        demandeurEmploi.getFuturTravail().setNombreMoisContratCDD(3);
        demandeurEmploi.getInformationsPersonnelles().setCodePostal(CODE_POSTAL_METROPOLITAIN);
        demandeurEmploi.getRessourcesFinancieres().getAllocationsPoleEmploi().setAllocationJournaliereNet(29.38f);
        demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(0).getInformationsPersonnelles().setDateNaissance(utileTests.getDateNaissanceFromAge(9));        
        
        //Lorsque l'on vérifie son éligibilité
        boolean isEligible = agepi.isEligible(PREMIER_MOIS_SIMULATION, demandeurEmploi);
        
        //Alors le DE est éligible à l'AGEPI
        assertThat(isEligible).isTrue();
    }
    
    @Test
    void isEligibleTest4() throws ParseException {
        
        //Si DE France Métropolitaine, futur contrat CDD de 4 mois ( > minimum exigé), pas en couple, 1 enfant 9 ans à charge et allocation journalière net = 29.38 euros (= seuil max.)
        boolean isEnCouple = false;
        int nbEnfant = 1;
        DemandeurEmploi demandeurEmploi = utileTests.creerBaseDemandeurEmploi(TypePopulation.ARE.getLibelle(), isEnCouple, nbEnfant);
        demandeurEmploi.getFuturTravail().setTypeContrat(TypesContratTravail.CDD.name());
        demandeurEmploi.getFuturTravail().setNombreMoisContratCDD(4);
        demandeurEmploi.getInformationsPersonnelles().setCodePostal(CODE_POSTAL_METROPOLITAIN);
        demandeurEmploi.getRessourcesFinancieres().getAllocationsPoleEmploi().setAllocationJournaliereNet(29.38f);
        demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(0).getInformationsPersonnelles().setDateNaissance(utileTests.getDateNaissanceFromAge(9));

        //Lorsque l'on vérifie son éligibilité
        boolean isEligible = agepi.isEligible(PREMIER_MOIS_SIMULATION, demandeurEmploi);
        
        //Alors le DE est éligible à l'AGEPI
        assertThat(isEligible).isTrue();
    }
    
    @Test
    void isEligibleTest5() {
        
        //Si DE France Métropolitaine, futur contrat CDI, pas en couple, 1 enfant 9 ans à charge et bénéficiaire du RSA
        boolean isEnCouple = false;
        int nbEnfant = 1;
        DemandeurEmploi demandeurEmploi = utileTests.creerBaseDemandeurEmploi(TypePopulation.RSA.getLibelle(), isEnCouple, nbEnfant);
        demandeurEmploi.getFuturTravail().setTypeContrat(TypesContratTravail.CDI.name());
        demandeurEmploi.getInformationsPersonnelles().setCodePostal(CODE_POSTAL_METROPOLITAIN);
        demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(0).getInformationsPersonnelles().setDateNaissance(utileTests.getDateNaissanceFromAge(9));
        
        //Lorsque l'on vérifie son éligibilité
        boolean isEligible = agepi.isEligible(PREMIER_MOIS_SIMULATION, demandeurEmploi);
        
        //Alors le DE est éligible à l'AGEPI
        assertThat(isEligible).isTrue();
    }
    
    @Test
    void isEligibleTest6() {
        
        //Si DE France Métropolitaine, futur contrat CDI, pas en couple, 1 enfant 9 ans à charge et bénéficiaire de AAH
        boolean isEnCouple = false;
        int nbEnfant = 1;
        DemandeurEmploi demandeurEmploi = utileTests.creerBaseDemandeurEmploi(TypePopulation.AAH.getLibelle(), isEnCouple, nbEnfant);
        demandeurEmploi.getFuturTravail().setTypeContrat(TypesContratTravail.CDI.name());
        demandeurEmploi.getInformationsPersonnelles().setCodePostal(CODE_POSTAL_METROPOLITAIN);
        demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(0).getInformationsPersonnelles().setDateNaissance(utileTests.getDateNaissanceFromAge(9));        
        
        //Lorsque l'on vérifie son éligibilité
        boolean isEligible = agepi.isEligible(PREMIER_MOIS_SIMULATION, demandeurEmploi);
        
        //Alors le DE est éligible à l'AGEPI
        assertThat(isEligible).isTrue();
    }
    
    @Test
    void isEligibleTest7() {
        
        //Si DE France Métropolitaine, futur contrat CDI, pas en couple, 1 enfant 9 ans à charge et bénéficiaire de ASS
        boolean isEnCouple = false;
        int nbEnfant = 1;
        DemandeurEmploi demandeurEmploi = utileTests.creerBaseDemandeurEmploi(TypePopulation.ASS.getLibelle(), isEnCouple, nbEnfant);
        demandeurEmploi.getFuturTravail().setTypeContrat(TypesContratTravail.CDI.name());
        demandeurEmploi.getInformationsPersonnelles().setCodePostal(CODE_POSTAL_METROPOLITAIN);
        demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(0).getInformationsPersonnelles().setDateNaissance(utileTests.getDateNaissanceFromAge(9));      
        
        //Lorsque l'on vérifie son éligibilité
        boolean isEligible = agepi.isEligible(PREMIER_MOIS_SIMULATION, demandeurEmploi);
        
        //Alors le DE est éligible à l'AGEPI
        assertThat(isEligible).isTrue();
    }
    
    @Test
    void isEligibleTest8() {
        
        //Si DE France Métropolitaine, futur contrat CDI, pas en couple, 1 enfant 9 ans à charge et sans ressources
        boolean isEnCouple = false;
        int nbEnfant = 1;
        DemandeurEmploi demandeurEmploi = utileTests.creerBaseDemandeurEmploi("NON BENEFICIAIRE", isEnCouple, nbEnfant);
        demandeurEmploi.getFuturTravail().setTypeContrat(TypesContratTravail.CDI.name());
        demandeurEmploi.getInformationsPersonnelles().setCodePostal(CODE_POSTAL_METROPOLITAIN);
        demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(0).getInformationsPersonnelles().setDateNaissance(utileTests.getDateNaissanceFromAge(9));
        demandeurEmploi.setRessourcesFinancieres(null);
        
        //Lorsque l'on vérifie son éligibilité
        boolean isEligible = agepi.isEligible(PREMIER_MOIS_SIMULATION, demandeurEmploi);
        
        //Alors le DE est éligible à l'AGEPI
        assertThat(isEligible).isTrue();
    }
}
