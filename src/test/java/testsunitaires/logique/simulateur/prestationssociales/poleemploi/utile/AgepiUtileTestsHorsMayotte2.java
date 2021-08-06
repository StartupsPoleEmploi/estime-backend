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
import fr.poleemploi.estime.logique.simulateur.prestationssociales.poleemploi.utile.AgepiUtile;
import fr.poleemploi.estime.services.ressources.PrestationSociale;
import fr.poleemploi.estime.services.ressources.DemandeurEmploi;
import utile.tests.UtileTests;


@ContextConfiguration
@SpringBootTest
@TestPropertySource(locations="classpath:application-test.properties")
class AgepiUtileTestsHorsMayotte2 {
    
    public static final String CODE_POSTAL_MAYOTTE = "97600";
    public static final String CODE_POSTAL_METROPOLITAIN = "72300";
    public static final int PREMIER_MOIS_SIMULATION = 1;
    
    @Autowired
    private AgepiUtile agepiUtile;
    
    @Autowired
    UtileTests utileTests;
    
    @Configuration
    @ComponentScan({"utile.tests","fr.poleemploi.estime"})
    public static class SpringConfig {

    }
    
    /************* département hors Mayotte *********/
    
    
    @Test
    void isNotEligibleTest1() {
        
        //Si DE France Métropolitaine, futur contrat CDI, pas en couple, 1 enfant 10 ans à charge et allocation journalière net = 14.38€ (< seuil max.)
        boolean isEnCouple = false;
        int nbEnfant = 1;
        DemandeurEmploi demandeurEmploi = utileTests.creerBaseDemandeurEmploi(TypePopulation.ARE.getLibelle(), isEnCouple, nbEnfant);
        demandeurEmploi.getFuturTravail().setTypeContrat(TypesContratTravail.CDI.name());
        demandeurEmploi.getInformationsPersonnelles().setCodePostal(CODE_POSTAL_METROPOLITAIN);
        demandeurEmploi.getRessourcesFinancieres().getPrestationsPoleEmploi().getAllocationARE().setAllocationJournaliereNet(14.38f);
        demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(0).getInformationsPersonnelles().setDateNaissance(utileTests.getDateNaissanceFromAge(10));

        //Lorsque l'on vérifie son éligibilité
        boolean isEligible = agepiUtile.isEligible(PREMIER_MOIS_SIMULATION, demandeurEmploi);
        
        //Alors le DE n'est pas éligible à l'AGEPI (isFalse)
        assertThat(isEligible).isFalse();
    }
    
    @Test
    void isNotEligibleTest2() {
        
        //Si DE France Métropolitaine, futur contrat CDI, en couple, 1 enfant 9 ans à charge et allocation journalière net = 14.28€ (< seuil max.) 
        boolean isEnCouple = true;
        int nbEnfant = 1;
        DemandeurEmploi demandeurEmploi = utileTests.creerBaseDemandeurEmploi(TypePopulation.ARE.getLibelle(), isEnCouple, nbEnfant);
        demandeurEmploi.getFuturTravail().setTypeContrat(TypesContratTravail.CDI.name());
        demandeurEmploi.getInformationsPersonnelles().setCodePostal(CODE_POSTAL_METROPOLITAIN);
        demandeurEmploi.getRessourcesFinancieres().getPrestationsPoleEmploi().getAllocationARE().setAllocationJournaliereNet(14.28f);
        demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(0).getInformationsPersonnelles().setDateNaissance(utileTests.getDateNaissanceFromAge(9));
        
        //Lorsque l'on vérifie son éligibilité
        boolean isEligible = agepiUtile.isEligible(PREMIER_MOIS_SIMULATION, demandeurEmploi);
        
        //Alors le DE n'est pas éligible à l'AGEPI
        assertThat(isEligible).isFalse();
    }
    
    
    
    @Test
    void isNotEligibleTest3() {
        
        //Si DE France Métropolitaine, futur contrat CDI, pas en couple, 1 enfant 9 ans à charge et allocation journalière net = 30.28€ (> seuil max.)
        boolean isEnCouple = false;
        int nbEnfant = 1;
        DemandeurEmploi demandeurEmploi = utileTests.creerBaseDemandeurEmploi(TypePopulation.ARE.getLibelle(), isEnCouple, nbEnfant);
        demandeurEmploi.getFuturTravail().setTypeContrat(TypesContratTravail.CDI.name());
        demandeurEmploi.getInformationsPersonnelles().setCodePostal(CODE_POSTAL_METROPOLITAIN);
        demandeurEmploi.getRessourcesFinancieres().getPrestationsPoleEmploi().getAllocationARE().setAllocationJournaliereNet(30.28f);
        demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(0).getInformationsPersonnelles().setDateNaissance(utileTests.getDateNaissanceFromAge(9));               
        
        //Lorsque l'on vérifie son éligibilité
        boolean isEligible = agepiUtile.isEligible(PREMIER_MOIS_SIMULATION, demandeurEmploi);
        
        //Alors le DE n'est pas éligible à l'AGEPI
        assertThat(isEligible).isFalse();
    }
    
    @Test
    void isNotEligibleTest4() throws ParseException {
        
        //Si DE France Métropolitaine, futur contrat CDD de 2 mois ( < minimum exigé), pas en couple, 1 enfant 9 ans à charge et allocation journalière net = 30.28€ (> seuil max.)
        boolean isEnCouple = false;
        int nbEnfant = 1;
        DemandeurEmploi demandeurEmploi = utileTests.creerBaseDemandeurEmploi(TypePopulation.ARE.getLibelle(), isEnCouple, nbEnfant);
        demandeurEmploi.getFuturTravail().setTypeContrat(TypesContratTravail.CDD.name());
        demandeurEmploi.getFuturTravail().setNombreMoisContratCDD(2);
        demandeurEmploi.getInformationsPersonnelles().setCodePostal(CODE_POSTAL_METROPOLITAIN);
        demandeurEmploi.getRessourcesFinancieres().getPrestationsPoleEmploi().getAllocationARE().setAllocationJournaliereNet(30.28f);
        demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(0).getInformationsPersonnelles().setDateNaissance(utileTests.getDateNaissanceFromAge(9));

        //Lorsque l'on vérifie son éligibilité
        boolean isEligible = agepiUtile.isEligible(PREMIER_MOIS_SIMULATION, demandeurEmploi);
        
        //Alors le DE n'est pas éligible à l'AGEPI
        assertThat(isEligible).isFalse();
    }
    

    /***  dureeTravailEnHeuresSemaine < 15h ****/
    
    @Test
    void simulerPrestationSocialeMontantAgepiTest1() {
        
        //Si DE France métropolitaine, futur contrat CDI avec 10h/semaine, 1 enfant à charge de 9ans 
        boolean isEnCouple = false;
        int nbEnfant = 1;
        DemandeurEmploi demandeurEmploi = utileTests.creerBaseDemandeurEmploi(TypePopulation.ARE.getLibelle(), isEnCouple, nbEnfant);
        demandeurEmploi.getFuturTravail().setTypeContrat(TypesContratTravail.CDI.name());
        demandeurEmploi.getFuturTravail().setNombreHeuresTravailleesSemaine(10);
        demandeurEmploi.getInformationsPersonnelles().setCodePostal(CODE_POSTAL_METROPOLITAIN);
        demandeurEmploi.getRessourcesFinancieres().getPrestationsPoleEmploi().getAllocationARE().setAllocationJournaliereNet(30.28f);
        demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(0).getInformationsPersonnelles().setDateNaissance(utileTests.getDateNaissanceFromAge(9));
       
        //Lorsque l'on calcul le montant de l'agepi
        PrestationSociale agepi = agepiUtile.simulerPrestationSociale(demandeurEmploi);
        
        //alors le montant retourné est de 170€
        assertThat(agepi.getMontant()).isEqualTo(170);
    }
    
    
    @Test
    void simulerPrestationSocialeMontantAgepiTest2() {
        
        //Si DE France métropolitaine, futur contrat CDI avec 10h/semaine, 2 enfants à charge de 9ans et 8 ans
        boolean isEnCouple = false;
        int nbEnfant = 2;
        DemandeurEmploi demandeurEmploi = utileTests.creerBaseDemandeurEmploi(TypePopulation.ARE.getLibelle(), isEnCouple, nbEnfant);
        demandeurEmploi.getFuturTravail().setTypeContrat(TypesContratTravail.CDI.name());
        demandeurEmploi.getFuturTravail().setNombreHeuresTravailleesSemaine(10);
        demandeurEmploi.getInformationsPersonnelles().setCodePostal(CODE_POSTAL_METROPOLITAIN);
        demandeurEmploi.getRessourcesFinancieres().getPrestationsPoleEmploi().getAllocationARE().setAllocationJournaliereNet(30.28f);
        demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(0).getInformationsPersonnelles().setDateNaissance(utileTests.getDateNaissanceFromAge(9));
        demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(1).getInformationsPersonnelles().setDateNaissance(utileTests.getDateNaissanceFromAge(8));
       
        //Lorsque l'on calcul le montant de l'agepi
        PrestationSociale agepi = agepiUtile.simulerPrestationSociale(demandeurEmploi);
        
        //alors le montant retourné est de 195€
        assertThat(agepi.getMontant()).isEqualTo(195);
    }
    
    @Test
    void simulerPrestationSocialeMontantAgepiTest3() {
        
        //Si DE France métropolitaine, futur contrat CDI avec 10h/semaine, 3 enfants à charge de 9ans, 8 ans et 8 ans
        boolean isEnCouple = false;
        int nbEnfant = 3;
        DemandeurEmploi demandeurEmploi = utileTests.creerBaseDemandeurEmploi(TypePopulation.ARE.getLibelle(), isEnCouple, nbEnfant);
        demandeurEmploi.getFuturTravail().setTypeContrat(TypesContratTravail.CDI.name());
        demandeurEmploi.getFuturTravail().setNombreHeuresTravailleesSemaine(10);
        demandeurEmploi.getInformationsPersonnelles().setCodePostal(CODE_POSTAL_METROPOLITAIN);
        demandeurEmploi.getRessourcesFinancieres().getPrestationsPoleEmploi().getAllocationARE().setAllocationJournaliereNet(30.28f);
        demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(0).getInformationsPersonnelles().setDateNaissance(utileTests.getDateNaissanceFromAge(9));
        demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(1).getInformationsPersonnelles().setDateNaissance(utileTests.getDateNaissanceFromAge(8));
        demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(2).getInformationsPersonnelles().setDateNaissance(utileTests.getDateNaissanceFromAge(8));

        //Lorsque l'on calcul le montant de l'agepi
        PrestationSociale agepi = agepiUtile.simulerPrestationSociale(demandeurEmploi);
        
        //alors le montant retourné est de 220€
        assertThat(agepi.getMontant()).isEqualTo(220);
    }
    
    @Test
    void simulerPrestationSocialeMontantAgepiTest4() {
        
        //Si DE France métropolitaine, futur contrat CDI avec 10h/semaine, 4 enfants à charge de 9ans, 8 ans, 8 ans et 5 ans
        boolean isEnCouple = false;
        int nbEnfant = 4;
        DemandeurEmploi demandeurEmploi = utileTests.creerBaseDemandeurEmploi(TypePopulation.ARE.getLibelle(), isEnCouple, nbEnfant);
        demandeurEmploi.getFuturTravail().setTypeContrat(TypesContratTravail.CDI.name());
        demandeurEmploi.getFuturTravail().setNombreHeuresTravailleesSemaine(10);
        demandeurEmploi.getInformationsPersonnelles().setCodePostal(CODE_POSTAL_METROPOLITAIN);
        demandeurEmploi.getRessourcesFinancieres().getPrestationsPoleEmploi().getAllocationARE().setAllocationJournaliereNet(30.28f);
        demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(0).getInformationsPersonnelles().setDateNaissance(utileTests.getDateNaissanceFromAge(9));
        demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(1).getInformationsPersonnelles().setDateNaissance(utileTests.getDateNaissanceFromAge(8));
        demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(2).getInformationsPersonnelles().setDateNaissance(utileTests.getDateNaissanceFromAge(8));
        demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(3).getInformationsPersonnelles().setDateNaissance(utileTests.getDateNaissanceFromAge(5));
        
        //Lorsque l'on calcul le montant de l'agepi
        PrestationSociale agepi = agepiUtile.simulerPrestationSociale(demandeurEmploi);
        
        //alors le montant retourné est de 220€
        assertThat(agepi.getMontant()).isEqualTo(220);
    }
    
    @Test
    void simulerPrestationSocialeMontantAgepiTest5() {
        
        //Si DE France métropolitaine, futur contrat CDI avec 10h/semaine, 1 enfant à charge de 15ans
        boolean isEnCouple = false;
        int nbEnfant = 1;
        DemandeurEmploi demandeurEmploi = utileTests.creerBaseDemandeurEmploi(TypePopulation.ARE.getLibelle(), isEnCouple, nbEnfant);
        demandeurEmploi.getFuturTravail().setTypeContrat(TypesContratTravail.CDI.name());
        demandeurEmploi.getFuturTravail().setNombreHeuresTravailleesSemaine(10);
        demandeurEmploi.getInformationsPersonnelles().setCodePostal(CODE_POSTAL_METROPOLITAIN);
        demandeurEmploi.getRessourcesFinancieres().getPrestationsPoleEmploi().getAllocationARE().setAllocationJournaliereNet(30.28f);
        demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(0).getInformationsPersonnelles().setDateNaissance(utileTests.getDateNaissanceFromAge(15));

        //Lorsque l'on calcul le montant de l'agepi
        PrestationSociale agepi = agepiUtile.simulerPrestationSociale(demandeurEmploi);
        
        //alors le montant retourné est de 0€
        assertThat(agepi.getMontant()).isEqualTo(0);
    }
    
    /*** dureeTravailEnHeuresSemaine = 15h ****/
    
    @Test
    void simulerPrestationSocialeMontantAgepiTest6() {
        
        //Si DE France métropolitaine, futur contrat CDI avec 15h/semaine, 1 enfant à charge de 9ans
        boolean isEnCouple = false;
        int nbEnfant = 1;
        DemandeurEmploi demandeurEmploi = utileTests.creerBaseDemandeurEmploi(TypePopulation.ARE.getLibelle(), isEnCouple, nbEnfant);
        demandeurEmploi.getFuturTravail().setTypeContrat(TypesContratTravail.CDI.name());
        demandeurEmploi.getFuturTravail().setNombreHeuresTravailleesSemaine(15);
        demandeurEmploi.getInformationsPersonnelles().setCodePostal(CODE_POSTAL_METROPOLITAIN);
        demandeurEmploi.getRessourcesFinancieres().getPrestationsPoleEmploi().getAllocationARE().setAllocationJournaliereNet(30.28f);
        demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(0).getInformationsPersonnelles().setDateNaissance(utileTests.getDateNaissanceFromAge(9));
        
        //Lorsque l'on calcul le montant de l'agepi
        PrestationSociale agepi = agepiUtile.simulerPrestationSociale(demandeurEmploi);
        
        //alors le montant retourné est de 400€
        assertThat(agepi.getMontant()).isEqualTo(400);
    }
    
    
    @Test
    void simulerPrestationSocialeMontantAgepiTest7() {
        
        //Si DE France métropolitaine, futur contrat CDI avec 15h/semaine, 2 enfants à charge de 9ans et 8ans
        boolean isEnCouple = false;
        int nbEnfant = 2;
        DemandeurEmploi demandeurEmploi = utileTests.creerBaseDemandeurEmploi(TypePopulation.ARE.getLibelle(), isEnCouple, nbEnfant);
        demandeurEmploi.getFuturTravail().setTypeContrat(TypesContratTravail.CDI.name());
        demandeurEmploi.getFuturTravail().setNombreHeuresTravailleesSemaine(15);
        demandeurEmploi.getInformationsPersonnelles().setCodePostal(CODE_POSTAL_METROPOLITAIN);
        demandeurEmploi.getRessourcesFinancieres().getPrestationsPoleEmploi().getAllocationARE().setAllocationJournaliereNet(30.28f);
        demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(0).getInformationsPersonnelles().setDateNaissance(utileTests.getDateNaissanceFromAge(9));
        demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(1).getInformationsPersonnelles().setDateNaissance(utileTests.getDateNaissanceFromAge(8));

        //Lorsque l'on calcul le montant de l'agepi
        PrestationSociale agepi = agepiUtile.simulerPrestationSociale(demandeurEmploi);
        
        //alors le montant retourné est de 460€
        assertThat(agepi.getMontant()).isEqualTo(460);
    }
    
    @Test
    void simulerPrestationSocialeMontantAgepiTest8() {
        
        //Si DE France métropolitaine, futur contrat CDI avec 15h/semaine, 3 enfants à charge de 9ans, 8ans et 8ans
        boolean isEnCouple = false;
        int nbEnfant = 3;
        DemandeurEmploi demandeurEmploi = utileTests.creerBaseDemandeurEmploi(TypePopulation.ARE.getLibelle(), isEnCouple, nbEnfant);
        demandeurEmploi.getFuturTravail().setTypeContrat(TypesContratTravail.CDI.name());
        demandeurEmploi.getFuturTravail().setNombreHeuresTravailleesSemaine(15);
        demandeurEmploi.getInformationsPersonnelles().setCodePostal(CODE_POSTAL_METROPOLITAIN);
        demandeurEmploi.getRessourcesFinancieres().getPrestationsPoleEmploi().getAllocationARE().setAllocationJournaliereNet(30.28f);
        demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(0).getInformationsPersonnelles().setDateNaissance(utileTests.getDateNaissanceFromAge(9));
        demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(1).getInformationsPersonnelles().setDateNaissance(utileTests.getDateNaissanceFromAge(8));
        demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(2).getInformationsPersonnelles().setDateNaissance(utileTests.getDateNaissanceFromAge(8));
        
        //Lorsque l'on calcul le montant de l'agepi
        PrestationSociale agepi = agepiUtile.simulerPrestationSociale(demandeurEmploi);
        
        //alors le montant retourné est de 520€
        assertThat(agepi.getMontant()).isEqualTo(520);
    }
    
    @Test
    void simulerPrestationSocialeMontantAgepiTest9() {
        
        //Si DE France métropolitaine, futur contrat CDI avec 15h/semaine, 4 enfants à charge de 9ans, 8ans, 8ans et 5ans
        boolean isEnCouple = false;
        int nbEnfant = 4;
        DemandeurEmploi demandeurEmploi = utileTests.creerBaseDemandeurEmploi(TypePopulation.ARE.getLibelle(), isEnCouple, nbEnfant);
        demandeurEmploi.getFuturTravail().setTypeContrat(TypesContratTravail.CDI.name());
        demandeurEmploi.getFuturTravail().setNombreHeuresTravailleesSemaine(15);
        demandeurEmploi.getInformationsPersonnelles().setCodePostal(CODE_POSTAL_METROPOLITAIN);
        demandeurEmploi.getRessourcesFinancieres().getPrestationsPoleEmploi().getAllocationARE().setAllocationJournaliereNet(30.28f);
        demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(0).getInformationsPersonnelles().setDateNaissance(utileTests.getDateNaissanceFromAge(9));
        demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(1).getInformationsPersonnelles().setDateNaissance(utileTests.getDateNaissanceFromAge(8));
        demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(2).getInformationsPersonnelles().setDateNaissance(utileTests.getDateNaissanceFromAge(8));
        demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(3).getInformationsPersonnelles().setDateNaissance(utileTests.getDateNaissanceFromAge(5));
               
        //Lorsque l'on calcul le montant de l'agepi
        PrestationSociale agepi = agepiUtile.simulerPrestationSociale(demandeurEmploi);
        
        //alors le montant retourné est de 520€
        assertThat(agepi.getMontant()).isEqualTo(520);
    }
}
