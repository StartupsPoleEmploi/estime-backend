package testsunitaires.logique.simulateuraidessociales.poleemploi.aides;

import static org.assertj.core.api.Assertions.assertThat;

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
import fr.poleemploi.estime.services.ressources.AideSociale;
import fr.poleemploi.estime.services.ressources.DemandeurEmploi;
import utile.tests.UtileTests;


@ContextConfiguration
@SpringBootTest
@TestPropertySource(locations="classpath:application-test.properties")
class AgepiTestsMayotte3 {
    
    public static final String CODE_POSTAL_MAYOTTE = "97600";
    public static final String CODE_POSTAL_METROPOLITAIN = "72300";
    
    @Autowired
    private Agepi agepiUtile;
    
    @Autowired
    UtileTests utileTests;
    
    @Configuration
    @ComponentScan({"utile.tests","fr.poleemploi.estime"})
    public static class SpringConfig {

    }
    
    /**************************************  département Mayotte *****************************************************/
    
    @Test
    void calculerMontantAgepiMayotteTest10() {
        
        //Si DE Mayotte, futur contrat CDI avec 15h/semaine, 1 enfant à charge de 12ans
        boolean isEnCouple = false;
        int nbEnfant = 1;
        DemandeurEmploi demandeurEmploi = utileTests.creerBaseDemandeurEmploi(TypePopulation.ARE.getLibelle(), isEnCouple, nbEnfant);
        demandeurEmploi.getFuturTravail().setTypeContrat(TypesContratTravail.CDI.name());
        demandeurEmploi.getFuturTravail().setNombreHeuresTravailleesSemaine(15);
        demandeurEmploi.getInformationsPersonnelles().setCodePostal(CODE_POSTAL_MAYOTTE);
        demandeurEmploi.getRessourcesFinancieres().getPrestationsPoleEmploi().getAllocationARE().setAllocationJournaliereNet(14.68f);
        demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(0).getInformationsPersonnelles().setDateNaissance(utileTests.getDateNaissanceFromAge(12)); 
       
        //Lorsque l'on calcul le montant de l'agepi
        AideSociale agepi = agepiUtile.calculer(demandeurEmploi);
        
        //alors le montant retourné est de 0€
        assertThat(agepi.getMontant()).isEqualTo(0);
    }
    
    /***  dureeTravailEnHeuresSemaine 20h ****/
    
    @Test
    void calculerMontantAgepiMayotteTest11() {
        
        //Si DE Mayotte, futur contrat CDI avec 20h/semaine, 1 enfant à charge de 9ans
        boolean isEnCouple = false;
        int nbEnfant = 1;
        DemandeurEmploi demandeurEmploi = utileTests.creerBaseDemandeurEmploi(TypePopulation.ARE.getLibelle(), isEnCouple, nbEnfant);
        demandeurEmploi.getFuturTravail().setTypeContrat(TypesContratTravail.CDI.name());
        demandeurEmploi.getFuturTravail().setNombreHeuresTravailleesSemaine(20);
        demandeurEmploi.getInformationsPersonnelles().setCodePostal(CODE_POSTAL_MAYOTTE);
        demandeurEmploi.getRessourcesFinancieres().getPrestationsPoleEmploi().getAllocationARE().setAllocationJournaliereNet(14.68f);
        demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(0).getInformationsPersonnelles().setDateNaissance(utileTests.getDateNaissanceFromAge(9)); 
       
        //Lorsque l'on calcul le montant de l'agepi
        AideSociale agepi = agepiUtile.calculer(demandeurEmploi);
        
        //alors le montant retourné est de 200€
        assertThat(agepi.getMontant()).isEqualTo(200);
    }
    
    
    @Test
    void calculerMontantAgepiMayotteTest12() {
        
        //Si DE Mayotte, futur contrat CDI avec 20h/semaine, 2 enfants à charge de 9ans et 8ans
        boolean isEnCouple = false;
        int nbEnfant = 2;
        DemandeurEmploi demandeurEmploi = utileTests.creerBaseDemandeurEmploi(TypePopulation.ARE.getLibelle(), isEnCouple, nbEnfant);
        demandeurEmploi.getFuturTravail().setTypeContrat(TypesContratTravail.CDI.name());
        demandeurEmploi.getFuturTravail().setNombreHeuresTravailleesSemaine(20);
        demandeurEmploi.getInformationsPersonnelles().setCodePostal(CODE_POSTAL_MAYOTTE);
        demandeurEmploi.getRessourcesFinancieres().getPrestationsPoleEmploi().getAllocationARE().setAllocationJournaliereNet(14.68f);
        demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(0).getInformationsPersonnelles().setDateNaissance(utileTests.getDateNaissanceFromAge(9)); 
        demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(1).getInformationsPersonnelles().setDateNaissance(utileTests.getDateNaissanceFromAge(8));
       
        //Lorsque l'on calcul le montant de l'agepi
        AideSociale agepi = agepiUtile.calculer(demandeurEmploi);
        
        //alors le montant retourné est de 230€
        assertThat(agepi.getMontant()).isEqualTo(230);
    }
    
    @Test
    void calculerMontantAgepiMayotteTest13() {
        
        //Si DE Mayotte, futur contrat CDI avec 20h/semaine, 3 enfants à charge de 9ans, 8ans et 8ans
        boolean isEnCouple = false;
        int nbEnfant = 3;
        DemandeurEmploi demandeurEmploi = utileTests.creerBaseDemandeurEmploi(TypePopulation.ARE.getLibelle(), isEnCouple, nbEnfant);
        demandeurEmploi.getFuturTravail().setTypeContrat(TypesContratTravail.CDI.name());
        demandeurEmploi.getFuturTravail().setNombreHeuresTravailleesSemaine(20);
        demandeurEmploi.getInformationsPersonnelles().setCodePostal(CODE_POSTAL_MAYOTTE);
        demandeurEmploi.getRessourcesFinancieres().getPrestationsPoleEmploi().getAllocationARE().setAllocationJournaliereNet(14.68f);
        demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(0).getInformationsPersonnelles().setDateNaissance(utileTests.getDateNaissanceFromAge(9)); 
        demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(1).getInformationsPersonnelles().setDateNaissance(utileTests.getDateNaissanceFromAge(8));
        demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(2).getInformationsPersonnelles().setDateNaissance(utileTests.getDateNaissanceFromAge(8));
               
        //Lorsque l'on calcul le montant de l'agepi
        AideSociale agepi = agepiUtile.calculer(demandeurEmploi);
        
        //alors le montant retourné est de 260€
        assertThat(agepi.getMontant()).isEqualTo(260);
    }
    
    @Test
    void calculerMontantAgepiMayotteTest14() {
        
        //Si DE Mayotte, futur contrat CDI avec 20h/semaine, 4 enfants à charge de 9ans, 8ans, 8ans et 5ans
        boolean isEnCouple = false;
        int nbEnfant = 4;
        DemandeurEmploi demandeurEmploi = utileTests.creerBaseDemandeurEmploi(TypePopulation.ARE.getLibelle(), isEnCouple, nbEnfant);
        demandeurEmploi.getFuturTravail().setTypeContrat(TypesContratTravail.CDI.name());
        demandeurEmploi.getFuturTravail().setNombreHeuresTravailleesSemaine(20);
        demandeurEmploi.getInformationsPersonnelles().setCodePostal(CODE_POSTAL_MAYOTTE);
        demandeurEmploi.getRessourcesFinancieres().getPrestationsPoleEmploi().getAllocationARE().setAllocationJournaliereNet(14.68f);
        demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(0).getInformationsPersonnelles().setDateNaissance(utileTests.getDateNaissanceFromAge(9)); 
        demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(1).getInformationsPersonnelles().setDateNaissance(utileTests.getDateNaissanceFromAge(8));
        demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(2).getInformationsPersonnelles().setDateNaissance(utileTests.getDateNaissanceFromAge(8));
        demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(3).getInformationsPersonnelles().setDateNaissance(utileTests.getDateNaissanceFromAge(5));
             
        //Lorsque l'on calcul le montant de l'agepi
        AideSociale agepi = agepiUtile.calculer(demandeurEmploi);
        
        //alors le montant retourné est de 260€
        assertThat(agepi.getMontant()).isEqualTo(260);
    }
    
    @Test
    void calculerMontantAgepiMayotteTest15() {
        
        //Si DE Mayotte, futur contrat CDI avec 20h/semaine, pas d'enfants à charge
        boolean isEnCouple = false;
        int nbEnfant = 1;
        DemandeurEmploi demandeurEmploi = utileTests.creerBaseDemandeurEmploi(TypePopulation.ARE.getLibelle(), isEnCouple, nbEnfant);
        demandeurEmploi.getFuturTravail().setTypeContrat(TypesContratTravail.CDI.name());
        demandeurEmploi.getFuturTravail().setNombreHeuresTravailleesSemaine(20);
        demandeurEmploi.getInformationsPersonnelles().setCodePostal(CODE_POSTAL_MAYOTTE);
        demandeurEmploi.getRessourcesFinancieres().getPrestationsPoleEmploi().getAllocationARE().setAllocationJournaliereNet(14.68f);      
        demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(0).getInformationsPersonnelles().setDateNaissance(utileTests.getDateNaissanceFromAge(13));
         
        //Lorsque l'on calcul le montant de l'agepi
        AideSociale agepi = agepiUtile.calculer(demandeurEmploi);
        
        //alors le montant retourné est de 0€
        assertThat(agepi.getMontant()).isEqualTo(0);
    }
    
 /***  dureeTravailEnHeuresSemaine = 35h ****/
    
    @Test
    void calculerMontantAgepiMayotteTest16() {
        
        //Si DE Mayotte, futur contrat CDI avec 35h/semaine, 1 enfant à charge de 9ans
        boolean isEnCouple = false;
        int nbEnfant = 1;
        DemandeurEmploi demandeurEmploi = utileTests.creerBaseDemandeurEmploi(TypePopulation.ARE.getLibelle(), isEnCouple, nbEnfant);
        demandeurEmploi.getFuturTravail().setTypeContrat(TypesContratTravail.CDI.name());
        demandeurEmploi.getFuturTravail().setNombreHeuresTravailleesSemaine(35);
        demandeurEmploi.getInformationsPersonnelles().setCodePostal(CODE_POSTAL_MAYOTTE);
        demandeurEmploi.getRessourcesFinancieres().getPrestationsPoleEmploi().getAllocationARE().setAllocationJournaliereNet(14.68f);      
        demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(0).getInformationsPersonnelles().setDateNaissance(utileTests.getDateNaissanceFromAge(9));

        //Lorsque l'on calcul le montant de l'agepi
        AideSociale agepi = agepiUtile.calculer(demandeurEmploi);
        
        //alors le montant retourné est de 200€
        assertThat(agepi.getMontant()).isEqualTo(200);
    }
    
    
    @Test
    void calculerMontantAgepiMayotteTest17() {
        
        //Si DE Mayotte, futur contrat CDI avec 35h/semaine, 2 enfants à charge de 9ans et 8ans
        boolean isEnCouple = false;
        int nbEnfant = 2;
        DemandeurEmploi demandeurEmploi = utileTests.creerBaseDemandeurEmploi(TypePopulation.ARE.getLibelle(), isEnCouple, nbEnfant);
        demandeurEmploi.getFuturTravail().setTypeContrat(TypesContratTravail.CDI.name());
        demandeurEmploi.getFuturTravail().setNombreHeuresTravailleesSemaine(35);
        demandeurEmploi.getInformationsPersonnelles().setCodePostal(CODE_POSTAL_MAYOTTE);
        demandeurEmploi.getRessourcesFinancieres().getPrestationsPoleEmploi().getAllocationARE().setAllocationJournaliereNet(14.68f);      
        demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(0).getInformationsPersonnelles().setDateNaissance(utileTests.getDateNaissanceFromAge(9));
        demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(1).getInformationsPersonnelles().setDateNaissance(utileTests.getDateNaissanceFromAge(8));
        
        //Lorsque l'on calcul le montant de l'agepi
        AideSociale agepi = agepiUtile.calculer(demandeurEmploi);
        
        //alors le montant retourné est de 230€
        assertThat(agepi.getMontant()).isEqualTo(230);
    }
    
    @Test
    void calculerMontantAgepiMayotteTest18() {
        
        //Si DE Mayotte, futur contrat CDI avec 35h/semaine, 3 enfants à charge de 9ans, 8ans et 8ans
        boolean isEnCouple = false;
        int nbEnfant = 3;
        DemandeurEmploi demandeurEmploi = utileTests.creerBaseDemandeurEmploi(TypePopulation.ARE.getLibelle(), isEnCouple, nbEnfant);
        demandeurEmploi.getFuturTravail().setTypeContrat(TypesContratTravail.CDI.name());
        demandeurEmploi.getFuturTravail().setNombreHeuresTravailleesSemaine(35);
        demandeurEmploi.getInformationsPersonnelles().setCodePostal(CODE_POSTAL_MAYOTTE);
        demandeurEmploi.getRessourcesFinancieres().getPrestationsPoleEmploi().getAllocationARE().setAllocationJournaliereNet(14.68f);      
        demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(0).getInformationsPersonnelles().setDateNaissance(utileTests.getDateNaissanceFromAge(9));
        demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(1).getInformationsPersonnelles().setDateNaissance(utileTests.getDateNaissanceFromAge(8));
        demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(2).getInformationsPersonnelles().setDateNaissance(utileTests.getDateNaissanceFromAge(8));
        
        //Lorsque l'on calcul le montant de l'agepi
        AideSociale agepi = agepiUtile.calculer(demandeurEmploi);
        
        //alors le montant retourné est de 260€
        assertThat(agepi.getMontant()).isEqualTo(260);
    }
    
    @Test
    void calculerMontantAgepiMayotteTest19() {
        
        //Si DE Mayotte, futur contrat CDI avec 35h/semaine, 4 enfants à charge de 9ans, 8ans, 8ans et 5ans
        boolean isEnCouple = false;
        int nbEnfant = 4;
        DemandeurEmploi demandeurEmploi = utileTests.creerBaseDemandeurEmploi(TypePopulation.ARE.getLibelle(), isEnCouple, nbEnfant);
        demandeurEmploi.getFuturTravail().setTypeContrat(TypesContratTravail.CDI.name());
        demandeurEmploi.getFuturTravail().setNombreHeuresTravailleesSemaine(35);
        demandeurEmploi.getInformationsPersonnelles().setCodePostal(CODE_POSTAL_MAYOTTE);
        demandeurEmploi.getRessourcesFinancieres().getPrestationsPoleEmploi().getAllocationARE().setAllocationJournaliereNet(14.68f);      
        demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(0).getInformationsPersonnelles().setDateNaissance(utileTests.getDateNaissanceFromAge(9));
        demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(1).getInformationsPersonnelles().setDateNaissance(utileTests.getDateNaissanceFromAge(8));
        demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(2).getInformationsPersonnelles().setDateNaissance(utileTests.getDateNaissanceFromAge(8));
        demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(3).getInformationsPersonnelles().setDateNaissance(utileTests.getDateNaissanceFromAge(5));
                
        //Lorsque l'on calcul le montant de l'agepi
        AideSociale agepi = agepiUtile.calculer(demandeurEmploi);
        
        //alors le montant retourné est de 260€
        assertThat(agepi.getMontant()).isEqualTo(260);
    }
    
    @Test
    void calculerMontantAgepiMayotteTest20() {
        
        //Si DE Mayotte, futur contrat CDI avec 35h/semaine, pas d'enfants à charge
        boolean isEnCouple = false;
        int nbEnfant = 0;
        DemandeurEmploi demandeurEmploi = utileTests.creerBaseDemandeurEmploi(TypePopulation.ARE.getLibelle(), isEnCouple, nbEnfant);
        demandeurEmploi.getFuturTravail().setTypeContrat(TypesContratTravail.CDI.name());
        demandeurEmploi.getFuturTravail().setNombreHeuresTravailleesSemaine(35);
        demandeurEmploi.getInformationsPersonnelles().setCodePostal(CODE_POSTAL_MAYOTTE);
        demandeurEmploi.getRessourcesFinancieres().getPrestationsPoleEmploi().getAllocationARE().setAllocationJournaliereNet(14.68f);      
               
        //Lorsque l'on calcul le montant de l'agepi
        AideSociale agepi = agepiUtile.calculer(demandeurEmploi);
        
        //alors le montant retourné est de 0€
        assertThat(agepi.getMontant()).isEqualTo(0);
    }
    
    /***  dureeTravailEnHeuresSemaine 40h ****/
    
    @Test
    void calculerMontantAgepiMayotteTest21() {
        
        //Si DE Mayotte, futur contrat CDI avec 40h/semaine, 1 enfant à charge de 9ans
        boolean isEnCouple = false;
        int nbEnfant = 1;
        DemandeurEmploi demandeurEmploi = utileTests.creerBaseDemandeurEmploi(TypePopulation.ARE.getLibelle(), isEnCouple, nbEnfant);
        demandeurEmploi.getFuturTravail().setTypeContrat(TypesContratTravail.CDI.name());
        demandeurEmploi.getFuturTravail().setNombreHeuresTravailleesSemaine(40);
        demandeurEmploi.getInformationsPersonnelles().setCodePostal(CODE_POSTAL_MAYOTTE);
        demandeurEmploi.getRessourcesFinancieres().getPrestationsPoleEmploi().getAllocationARE().setAllocationJournaliereNet(14.68f);      
        demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(0).getInformationsPersonnelles().setDateNaissance(utileTests.getDateNaissanceFromAge(9));
        
        //Lorsque l'on calcul le montant de l'agepi
        AideSociale agepi = agepiUtile.calculer(demandeurEmploi);
        
        //alors le montant retourné est de 200€
        assertThat(agepi.getMontant()).isEqualTo(200);
    }
    
    @Test
    void calculerMontantAgepiMayotteTest22() {
        
        //Si DE Mayotte, futur contrat CDI avec 40h/semaine, 2 enfants à charge de 9ans et 8ans
        boolean isEnCouple = false;
        int nbEnfant = 2;
        DemandeurEmploi demandeurEmploi = utileTests.creerBaseDemandeurEmploi(TypePopulation.ARE.getLibelle(), isEnCouple, nbEnfant);
        demandeurEmploi.getFuturTravail().setTypeContrat(TypesContratTravail.CDI.name());
        demandeurEmploi.getFuturTravail().setNombreHeuresTravailleesSemaine(40);
        demandeurEmploi.getInformationsPersonnelles().setCodePostal(CODE_POSTAL_MAYOTTE);
        demandeurEmploi.getRessourcesFinancieres().getPrestationsPoleEmploi().getAllocationARE().setAllocationJournaliereNet(14.68f);      
        demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(0).getInformationsPersonnelles().setDateNaissance(utileTests.getDateNaissanceFromAge(9));
        demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(1).getInformationsPersonnelles().setDateNaissance(utileTests.getDateNaissanceFromAge(8));
        
        //Lorsque l'on calcul le montant de l'agepi
        AideSociale agepi = agepiUtile.calculer(demandeurEmploi);
        
        //alors le montant retourné est de 230€
        assertThat(agepi.getMontant()).isEqualTo(230);
    }
    
    @Test
    void calculerMontantAgepiMayotteTest23() {
        
        //Si DE Mayotte, futur contrat CDI avec 40h/semaine, 3 enfants à charge de 9ans, 8ans et 8ans
        boolean isEnCouple = false;
        int nbEnfant = 3;
        DemandeurEmploi demandeurEmploi = utileTests.creerBaseDemandeurEmploi(TypePopulation.ARE.getLibelle(), isEnCouple, nbEnfant);
        demandeurEmploi.getFuturTravail().setTypeContrat(TypesContratTravail.CDI.name());
        demandeurEmploi.getFuturTravail().setNombreHeuresTravailleesSemaine(40);
        demandeurEmploi.getInformationsPersonnelles().setCodePostal(CODE_POSTAL_MAYOTTE);
        demandeurEmploi.getRessourcesFinancieres().getPrestationsPoleEmploi().getAllocationARE().setAllocationJournaliereNet(14.68f);      
        demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(0).getInformationsPersonnelles().setDateNaissance(utileTests.getDateNaissanceFromAge(9));
        demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(1).getInformationsPersonnelles().setDateNaissance(utileTests.getDateNaissanceFromAge(8));
        demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(2).getInformationsPersonnelles().setDateNaissance(utileTests.getDateNaissanceFromAge(8));
        
        //Lorsque l'on calcul le montant de l'agepi
        AideSociale agepi = agepiUtile.calculer(demandeurEmploi);
        
        //alors le montant retourné est de 260€
        assertThat(agepi.getMontant()).isEqualTo(260);
    }
    
    @Test
    void calculerMontantAgepiMayotteTest24() {
        
        //Si DE Mayotte, futur contrat CDI avec 40h/semaine, 4 enfants à charge de 9ans, 8ans, 5ans et 8ans
        boolean isEnCouple = false;
        int nbEnfant = 4;
        DemandeurEmploi demandeurEmploi = utileTests.creerBaseDemandeurEmploi(TypePopulation.ARE.getLibelle(), isEnCouple, nbEnfant);
        demandeurEmploi.getFuturTravail().setTypeContrat(TypesContratTravail.CDI.name());
        demandeurEmploi.getFuturTravail().setNombreHeuresTravailleesSemaine(40);
        demandeurEmploi.getInformationsPersonnelles().setCodePostal(CODE_POSTAL_MAYOTTE);
        demandeurEmploi.getRessourcesFinancieres().getPrestationsPoleEmploi().getAllocationARE().setAllocationJournaliereNet(14.68f);      
        demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(0).getInformationsPersonnelles().setDateNaissance(utileTests.getDateNaissanceFromAge(9));
        demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(1).getInformationsPersonnelles().setDateNaissance(utileTests.getDateNaissanceFromAge(8));
        demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(2).getInformationsPersonnelles().setDateNaissance(utileTests.getDateNaissanceFromAge(8));
        demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(3).getInformationsPersonnelles().setDateNaissance(utileTests.getDateNaissanceFromAge(5));
               
        //Lorsque l'on calcul le montant de l'agepi
        AideSociale agepi = agepiUtile.calculer(demandeurEmploi);
        
        //alors le montant retourné est de 260€
        assertThat(agepi.getMontant()).isEqualTo(260);
    }
    
    @Test
    void calculerMontantAgepiMayotteTest25() {
        
        //Si DE Mayotte, futur contrat CDI avec 40h/semaine, pas d'enfants à charge
        boolean isEnCouple = false;
        int nbEnfant = 0;
        DemandeurEmploi demandeurEmploi = utileTests.creerBaseDemandeurEmploi(TypePopulation.ARE.getLibelle(), isEnCouple, nbEnfant);
        demandeurEmploi.getFuturTravail().setTypeContrat(TypesContratTravail.CDI.name());
        demandeurEmploi.getFuturTravail().setNombreHeuresTravailleesSemaine(40);
        demandeurEmploi.getInformationsPersonnelles().setCodePostal(CODE_POSTAL_MAYOTTE);
        demandeurEmploi.getRessourcesFinancieres().getPrestationsPoleEmploi().getAllocationARE().setAllocationJournaliereNet(14.68f);              
        
        //Lorsque l'on calcul le montant de l'agepi
        AideSociale agepi = agepiUtile.calculer(demandeurEmploi);
        
        //alors le montant retourné est de 0€
        assertThat(agepi.getMontant()).isEqualTo(0);
    }  
}
