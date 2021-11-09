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

import fr.poleemploi.estime.commun.enumerations.TypePopulation;
import fr.poleemploi.estime.commun.enumerations.TypesContratTravail;
import fr.poleemploi.estime.logique.simulateur.aides.poleemploi.utile.AgepiUtile;
import fr.poleemploi.estime.services.ressources.Aide;
import fr.poleemploi.estime.services.ressources.DemandeurEmploi;
import utile.tests.Utile;


@ContextConfiguration
@SpringBootTest
@TestPropertySource(locations="classpath:application-test.properties")
class AgepiUtileTestsMayotte2 {
    
    public static final String CODE_POSTAL_MAYOTTE = "97600";
    public static final String CODE_POSTAL_METROPOLITAIN = "72300";
    public static final int PREMIER_MOIS_SIMULATION = 1;
    
    @Autowired
    private AgepiUtile agepiUtile;
    
    @Autowired
    Utile utileTests;
    
    @Configuration
    @ComponentScan({"utile.tests","fr.poleemploi.estime"})
    public static class SpringConfig {

    }
    
    /**************************************  département Mayotte *****************************************************/
    
    @Test
    void isNotEligibleMayotteTest1() {
        
        //Si DE Mayotte, futur contrat CDI, pas en couple, 1 enfant 10 ans à charge et allocation journalière net = 14.38€ (< seuil max.)
        boolean isEnCouple = false;
        int nbEnfant = 1;
        DemandeurEmploi demandeurEmploi = utileTests.creerBaseDemandeurEmploi(TypePopulation.ARE.getLibelle(), isEnCouple, nbEnfant);
        demandeurEmploi.getFuturTravail().setTypeContrat(TypesContratTravail.CDI.name());
        demandeurEmploi.getInformationsPersonnelles().setCodePostal(CODE_POSTAL_MAYOTTE);
        demandeurEmploi.getRessourcesFinancieres().getAidesPoleEmploi().getAllocationARE().setAllocationJournaliereNet(14.38f);
        demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(0).getInformationsPersonnelles().setDateNaissance(utileTests.getDateNaissanceFromAge(10)); 
        
        //Lorsque l'on vérifie son éligibilité
        boolean isEligible = agepiUtile.isEligible(PREMIER_MOIS_SIMULATION, demandeurEmploi);
        
        //Alors le DE n'est pas éligible à l'AGEPI
        assertThat(isEligible).isFalse();
    }
    
    @Test
    void isNotEligibleMayotteTest2() {
        
        //Si DE Mayotte, futur contrat CDI, en couple, 1 enfant 9 ans à charge et allocation journalière net = 14.68€ (< seuil max.) 
        boolean isEnCouple = true;
        int nbEnfant = 1;
        DemandeurEmploi demandeurEmploi = utileTests.creerBaseDemandeurEmploi(TypePopulation.ARE.getLibelle(), isEnCouple, nbEnfant);
        demandeurEmploi.getFuturTravail().setTypeContrat(TypesContratTravail.CDI.name());
        demandeurEmploi.getInformationsPersonnelles().setCodePostal(CODE_POSTAL_MAYOTTE);
        demandeurEmploi.getRessourcesFinancieres().getAidesPoleEmploi().getAllocationARE().setAllocationJournaliereNet(14.68f);
        demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(0).getInformationsPersonnelles().setDateNaissance(utileTests.getDateNaissanceFromAge(9)); 

        //Lorsque l'on vérifie son éligibilité
        boolean isEligible = agepiUtile.isEligible(PREMIER_MOIS_SIMULATION, demandeurEmploi);
        
        //Alors le DE n'est pas éligible à l'AGEPI
        assertThat(isEligible).isFalse();
    }
    
    
    
    @Test
    void isNotEligibleMayotteTest3() {
        
        //Si DE Mayotte, futur contrat CDI, pas en couple, 1 enfant 9 ans à charge et allocation journalière net = 30.28€ (> seuil max.)
        boolean isEnCouple = false;
        int nbEnfant = 1;
        DemandeurEmploi demandeurEmploi = utileTests.creerBaseDemandeurEmploi(TypePopulation.ARE.getLibelle(), isEnCouple, nbEnfant);
        demandeurEmploi.getFuturTravail().setTypeContrat(TypesContratTravail.CDI.name());
        demandeurEmploi.getInformationsPersonnelles().setCodePostal(CODE_POSTAL_MAYOTTE);
        demandeurEmploi.getRessourcesFinancieres().getAidesPoleEmploi().getAllocationARE().setAllocationJournaliereNet(30.28f);
        demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(0).getInformationsPersonnelles().setDateNaissance(utileTests.getDateNaissanceFromAge(9));
                
        //Lorsque l'on vérifie son éligibilité
        boolean isEligible = agepiUtile.isEligible(PREMIER_MOIS_SIMULATION, demandeurEmploi);
        
        //Alors le DE n'est pas éligible à l'AGEPI
        assertThat(isEligible).isFalse();
    }
    
    @Test
    void isNotEligibleMayotteTest4() throws ParseException {
        
        //Si DE Mayotte, futur contrat CDD de 2 mois ( < minimum exigé), pas en couple, 1 enfant 9 ans à charge et allocation journalière net = 30.28€ (> seuil max.)
        boolean isEnCouple = false;
        int nbEnfant = 1;
        DemandeurEmploi demandeurEmploi = utileTests.creerBaseDemandeurEmploi(TypePopulation.ARE.getLibelle(), isEnCouple, nbEnfant);
        demandeurEmploi.getFuturTravail().setTypeContrat(TypesContratTravail.CDD.name());
        demandeurEmploi.getFuturTravail().setNombreMoisContratCDD(2);
        demandeurEmploi.getInformationsPersonnelles().setCodePostal(CODE_POSTAL_MAYOTTE);
        demandeurEmploi.getRessourcesFinancieres().getAidesPoleEmploi().getAllocationARE().setAllocationJournaliereNet(30.28f);
        demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(0).getInformationsPersonnelles().setDateNaissance(utileTests.getDateNaissanceFromAge(9));           
        
        //Lorsque l'on vérifie son éligibilité
        boolean isEligible = agepiUtile.isEligible(PREMIER_MOIS_SIMULATION, demandeurEmploi);
        
        //Alors le DE n'est pas éligible à l'AGEPI
        assertThat(isEligible).isFalse();
    }
    
    /***  dureeTravailEnHeuresSemaine < 15h ****/
    
    @Test
    void calculerMontantAgepiMayotteTest1() {
        
        //Si DE Mayotte, futur contrat CDI avec 10h/semaine, 1 enfant à charge de 9ans 
        boolean isEnCouple = false;
        int nbEnfant = 1;
        DemandeurEmploi demandeurEmploi = utileTests.creerBaseDemandeurEmploi(TypePopulation.ARE.getLibelle(), isEnCouple, nbEnfant);
        demandeurEmploi.getFuturTravail().setTypeContrat(TypesContratTravail.CDI.name());
        demandeurEmploi.getFuturTravail().setNombreHeuresTravailleesSemaine(10);
        demandeurEmploi.getInformationsPersonnelles().setCodePostal(CODE_POSTAL_MAYOTTE);
        demandeurEmploi.getRessourcesFinancieres().getAidesPoleEmploi().getAllocationARE().setAllocationJournaliereNet(14.68f);
        demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(0).getInformationsPersonnelles().setDateNaissance(utileTests.getDateNaissanceFromAge(9));        
            
        //Lorsque l'on calcul le montant de l'agepi
        Aide agepi = agepiUtile.simulerAide(demandeurEmploi);
        
        //alors le montant retourné est de 85€
        assertThat(agepi.getMontant()).isEqualTo(85);
    }
    
    
    @Test
    void calculerMontantAgepiMayotteTest2() {
        
        //Si DE Mayotte, futur contrat CDI avec 10h/semaine, 2 enfants à charge de 9ans et 8 ans
        boolean isEnCouple = false;
        int nbEnfant = 2;
        DemandeurEmploi demandeurEmploi = utileTests.creerBaseDemandeurEmploi(TypePopulation.ARE.getLibelle(), isEnCouple, nbEnfant);
        demandeurEmploi.getFuturTravail().setTypeContrat(TypesContratTravail.CDI.name());
        demandeurEmploi.getFuturTravail().setNombreHeuresTravailleesSemaine(10);
        demandeurEmploi.getInformationsPersonnelles().setCodePostal(CODE_POSTAL_MAYOTTE);
        demandeurEmploi.getRessourcesFinancieres().getAidesPoleEmploi().getAllocationARE().setAllocationJournaliereNet(14.68f);
        demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(0).getInformationsPersonnelles().setDateNaissance(utileTests.getDateNaissanceFromAge(9)); 
        demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(1).getInformationsPersonnelles().setDateNaissance(utileTests.getDateNaissanceFromAge(8));              
        
        //Lorsque l'on calcul le montant de l'agepi
        Aide agepi = agepiUtile.simulerAide(demandeurEmploi);
        
        //alors le montant retourné est de 97€
        assertThat(agepi.getMontant()).isEqualTo(97);
    }
    
    @Test
    void calculerMontantAgepiMayotteTest3() {
        
        //Si DE Mayotte, futur contrat CDI avec 10h/semaine, 3 enfants à charge de 9ans, 8 ans et 8 ans
        boolean isEnCouple = false;
        int nbEnfant = 3;
        DemandeurEmploi demandeurEmploi = utileTests.creerBaseDemandeurEmploi(TypePopulation.ARE.getLibelle(), isEnCouple, nbEnfant);
        demandeurEmploi.getFuturTravail().setTypeContrat(TypesContratTravail.CDI.name());
        demandeurEmploi.getFuturTravail().setNombreHeuresTravailleesSemaine(10);
        demandeurEmploi.getInformationsPersonnelles().setCodePostal(CODE_POSTAL_MAYOTTE);
        demandeurEmploi.getRessourcesFinancieres().getAidesPoleEmploi().getAllocationARE().setAllocationJournaliereNet(14.68f);
        demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(0).getInformationsPersonnelles().setDateNaissance(utileTests.getDateNaissanceFromAge(9)); 
        demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(1).getInformationsPersonnelles().setDateNaissance(utileTests.getDateNaissanceFromAge(8));
        demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(2).getInformationsPersonnelles().setDateNaissance(utileTests.getDateNaissanceFromAge(8));                
        
        //Lorsque l'on calcul le montant de l'agepi
        Aide agepi = agepiUtile.simulerAide(demandeurEmploi);
        
        //alors le montant retourné est de 110€
        assertThat(agepi.getMontant()).isEqualTo(110);
    }
    
    @Test
    void calculerMontantAgepiMayotteTest4() {
        
        //Si DE Mayotte, futur contrat CDI avec 10h/semaine, 4 enfants à charge de 9ans, 8 ans, 8 ans et 5 ans
        boolean isEnCouple = false;
        int nbEnfant = 4;
        DemandeurEmploi demandeurEmploi = utileTests.creerBaseDemandeurEmploi(TypePopulation.ARE.getLibelle(), isEnCouple, nbEnfant);
        demandeurEmploi.getFuturTravail().setTypeContrat(TypesContratTravail.CDI.name());
        demandeurEmploi.getFuturTravail().setNombreHeuresTravailleesSemaine(10);
        demandeurEmploi.getInformationsPersonnelles().setCodePostal(CODE_POSTAL_MAYOTTE);
        demandeurEmploi.getRessourcesFinancieres().getAidesPoleEmploi().getAllocationARE().setAllocationJournaliereNet(14.68f);
        demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(0).getInformationsPersonnelles().setDateNaissance(utileTests.getDateNaissanceFromAge(9)); 
        demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(1).getInformationsPersonnelles().setDateNaissance(utileTests.getDateNaissanceFromAge(8));
        demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(2).getInformationsPersonnelles().setDateNaissance(utileTests.getDateNaissanceFromAge(8));
        demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(3).getInformationsPersonnelles().setDateNaissance(utileTests.getDateNaissanceFromAge(5));
        
        //Lorsque l'on calcul le montant de l'agepi
        Aide agepi = agepiUtile.simulerAide(demandeurEmploi);
        
        //alors le montant retourné est de 110€
        assertThat(agepi.getMontant()).isEqualTo(110);
    }
    
    @Test
    void calculerMontantAgepiMayotteTest5() {
        
        //Si DE Mayotte, futur contrat CDI avec 10h/semaine, 1 enfant à charge de 15ans
        boolean isEnCouple = false;
        int nbEnfant = 1;
        DemandeurEmploi demandeurEmploi = utileTests.creerBaseDemandeurEmploi(TypePopulation.ARE.getLibelle(), isEnCouple, nbEnfant);
        demandeurEmploi.getFuturTravail().setTypeContrat(TypesContratTravail.CDI.name());
        demandeurEmploi.getFuturTravail().setNombreHeuresTravailleesSemaine(10);
        demandeurEmploi.getInformationsPersonnelles().setCodePostal(CODE_POSTAL_MAYOTTE);
        demandeurEmploi.getRessourcesFinancieres().getAidesPoleEmploi().getAllocationARE().setAllocationJournaliereNet(14.68f);
        demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(0).getInformationsPersonnelles().setDateNaissance(utileTests.getDateNaissanceFromAge(15)); 
        
        //Lorsque l'on calcul le montant de l'agepi
        Aide agepi = agepiUtile.simulerAide(demandeurEmploi);
        
        //alors le montant retourné est de 0€
        assertThat(agepi.getMontant()).isEqualTo(0);
    }
    
    /*** dureeTravailEnHeuresSemaine = 15h ****/
    
    @Test
    void calculerMontantAgepiMayotteTest6() {
        
        //Si DE Mayotte, futur contrat CDI avec 15h/semaine, 1 enfant à charge de 9ans
        boolean isEnCouple = false;
        int nbEnfant = 1;
        DemandeurEmploi demandeurEmploi = utileTests.creerBaseDemandeurEmploi(TypePopulation.ARE.getLibelle(), isEnCouple, nbEnfant);
        demandeurEmploi.getFuturTravail().setTypeContrat(TypesContratTravail.CDI.name());
        demandeurEmploi.getFuturTravail().setNombreHeuresTravailleesSemaine(15);
        demandeurEmploi.getInformationsPersonnelles().setCodePostal(CODE_POSTAL_MAYOTTE);
        demandeurEmploi.getRessourcesFinancieres().getAidesPoleEmploi().getAllocationARE().setAllocationJournaliereNet(14.68f);
        demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(0).getInformationsPersonnelles().setDateNaissance(utileTests.getDateNaissanceFromAge(9)); 
      
        //Lorsque l'on calcul le montant de l'agepi
        Aide agepi = agepiUtile.simulerAide(demandeurEmploi);
        
        //alors le montant retourné est de 200€
        assertThat(agepi.getMontant()).isEqualTo(200);
    }
    
    
    @Test
    void calculerMontantAgepiMayotteTest7() {
        
        //Si DE Mayotte, futur contrat CDI avec 15h/semaine, 2 enfants à charge de 9ans et 8ans
        boolean isEnCouple = false;
        int nbEnfant = 2;
        DemandeurEmploi demandeurEmploi = utileTests.creerBaseDemandeurEmploi(TypePopulation.ARE.getLibelle(), isEnCouple, nbEnfant);
        demandeurEmploi.getFuturTravail().setTypeContrat(TypesContratTravail.CDI.name());
        demandeurEmploi.getFuturTravail().setNombreHeuresTravailleesSemaine(15);
        demandeurEmploi.getInformationsPersonnelles().setCodePostal(CODE_POSTAL_MAYOTTE);
        demandeurEmploi.getRessourcesFinancieres().getAidesPoleEmploi().getAllocationARE().setAllocationJournaliereNet(14.68f);
        demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(0).getInformationsPersonnelles().setDateNaissance(utileTests.getDateNaissanceFromAge(9)); 
        demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(1).getInformationsPersonnelles().setDateNaissance(utileTests.getDateNaissanceFromAge(8));
      
        //Lorsque l'on calcul le montant de l'agepi
        Aide agepi = agepiUtile.simulerAide(demandeurEmploi);
        
        //alors le montant retourné est de 230€
        assertThat(agepi.getMontant()).isEqualTo(230);
    }
    
    @Test
    void calculerMontantAgepiMayotteTest8() {
        
        //Si DE Mayotte, futur contrat CDI avec 15h/semaine, 3 enfants à charge de 9ans, 8ans et 8ans
        boolean isEnCouple = false;
        int nbEnfant = 3;
        DemandeurEmploi demandeurEmploi = utileTests.creerBaseDemandeurEmploi(TypePopulation.ARE.getLibelle(), isEnCouple, nbEnfant);
        demandeurEmploi.getFuturTravail().setTypeContrat(TypesContratTravail.CDI.name());
        demandeurEmploi.getFuturTravail().setNombreHeuresTravailleesSemaine(15);
        demandeurEmploi.getInformationsPersonnelles().setCodePostal(CODE_POSTAL_MAYOTTE);
        demandeurEmploi.getRessourcesFinancieres().getAidesPoleEmploi().getAllocationARE().setAllocationJournaliereNet(14.68f);
        demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(0).getInformationsPersonnelles().setDateNaissance(utileTests.getDateNaissanceFromAge(9)); 
        demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(1).getInformationsPersonnelles().setDateNaissance(utileTests.getDateNaissanceFromAge(8));
        demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(2).getInformationsPersonnelles().setDateNaissance(utileTests.getDateNaissanceFromAge(8));
      
        //Lorsque l'on calcul le montant de l'agepi
        Aide agepi = agepiUtile.simulerAide(demandeurEmploi);
        
        //alors le montant retourné est de 260€
        assertThat(agepi.getMontant()).isEqualTo(260);
    }
    
    @Test
    void calculerMontantAgepiMayotteTest9() {
        
        //Si DE Mayotte, futur contrat CDI avec 15h/semaine, 4 enfants à charge de 9ans, 8ans, 8ans et 5ans
        boolean isEnCouple = false;
        int nbEnfant = 4;
        DemandeurEmploi demandeurEmploi = utileTests.creerBaseDemandeurEmploi(TypePopulation.ARE.getLibelle(), isEnCouple, nbEnfant);
        demandeurEmploi.getFuturTravail().setTypeContrat(TypesContratTravail.CDI.name());
        demandeurEmploi.getFuturTravail().setNombreHeuresTravailleesSemaine(15);
        demandeurEmploi.getInformationsPersonnelles().setCodePostal(CODE_POSTAL_MAYOTTE);
        demandeurEmploi.getRessourcesFinancieres().getAidesPoleEmploi().getAllocationARE().setAllocationJournaliereNet(14.68f);
        demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(0).getInformationsPersonnelles().setDateNaissance(utileTests.getDateNaissanceFromAge(9)); 
        demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(1).getInformationsPersonnelles().setDateNaissance(utileTests.getDateNaissanceFromAge(8));
        demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(2).getInformationsPersonnelles().setDateNaissance(utileTests.getDateNaissanceFromAge(8));
        demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(3).getInformationsPersonnelles().setDateNaissance(utileTests.getDateNaissanceFromAge(5));
      
        //Lorsque l'on calcul le montant de l'agepi
        Aide agepi = agepiUtile.simulerAide(demandeurEmploi);
        
        //alors le montant retourné est de 260€
        assertThat(agepi.getMontant()).isEqualTo(260);
    }
}
