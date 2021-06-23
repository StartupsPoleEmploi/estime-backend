package testsunitaires.logique.simulateuraidessociales.poleemploi.aides;

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

import fr.poleemploi.estime.commun.enumerations.TypesContratTravail;
import fr.poleemploi.estime.logique.simulateuraidessociales.poleemploi.aides.Agepi;
import fr.poleemploi.estime.services.ressources.AideSociale;
import fr.poleemploi.estime.services.ressources.DemandeurEmploi;
import fr.poleemploi.estime.services.ressources.FuturTravail;
import fr.poleemploi.estime.services.ressources.InformationsPersonnelles;
import fr.poleemploi.estime.services.ressources.Personne;
import fr.poleemploi.estime.services.ressources.SituationFamiliale;
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
    UtileTests testUtile;
    
    @Configuration
    @ComponentScan({"utile.tests","fr.poleemploi.estime"})
    public static class SpringConfig {

    }
    
    /**************************************  département Mayotte *****************************************************/
    
    @Test
    void calculerMontantAgepiMayotteTest10() {
        
        //Si DE Mayotte, futur contrat CDI avec 15h/semaine, 1 enfant à charge de 12ans
        DemandeurEmploi demandeurEmploi = new DemandeurEmploi();
        
        FuturTravail futurContratTravail = new FuturTravail();
        futurContratTravail.setTypeContrat(TypesContratTravail.CDI.name());
        futurContratTravail.setNombreHeuresTravailleesSemaine(15);
        demandeurEmploi.setFuturTravail(futurContratTravail);
        
        InformationsPersonnelles informationsPersonnelles = new InformationsPersonnelles();
        informationsPersonnelles.setCodePostal(CODE_POSTAL_MAYOTTE);
        demandeurEmploi.setInformationsPersonnelles(informationsPersonnelles);

        SituationFamiliale situationFamiliale = new SituationFamiliale();
        List<Personne> personnesACharge = new ArrayList<Personne>();
        testUtile.createPersonne(personnesACharge, 12);  
        situationFamiliale.setPersonnesACharge(personnesACharge);
        demandeurEmploi.setSituationFamiliale(situationFamiliale);
        
        //Lorsque l'on calcul le montant de l'agepi
        AideSociale agepi = agepiUtile.calculer(demandeurEmploi);
        
        //alors le montant retourné est de 0 euros
        assertThat(agepi.getMontant()).isEqualTo(0);
    }
    
    /***  dureeTravailEnHeuresSemaine 20h ****/
    
    @Test
    void calculerMontantAgepiMayotteTest11() {
        
        //Si DE Mayotte, futur contrat CDI avec 20h/semaine, 1 enfant à charge de 9ans
        DemandeurEmploi demandeurEmploi = new DemandeurEmploi();
        
        FuturTravail futurContratTravail = new FuturTravail();
        futurContratTravail.setTypeContrat(TypesContratTravail.CDI.name());
        futurContratTravail.setNombreHeuresTravailleesSemaine(20);
        demandeurEmploi.setFuturTravail(futurContratTravail);
        
        InformationsPersonnelles informationsPersonnelles = new InformationsPersonnelles();
        informationsPersonnelles.setCodePostal(CODE_POSTAL_MAYOTTE);
        demandeurEmploi.setInformationsPersonnelles(informationsPersonnelles);
        
        SituationFamiliale situationFamiliale = new SituationFamiliale();
        List<Personne> personnesACharge = new ArrayList<Personne>();
        testUtile.createPersonne(personnesACharge, 9);  
        situationFamiliale.setPersonnesACharge(personnesACharge);
        demandeurEmploi.setSituationFamiliale(situationFamiliale);
        
        //Lorsque l'on calcul le montant de l'agepi
        AideSociale agepi = agepiUtile.calculer(demandeurEmploi);
        
        //alors le montant retourné est de 200 euros
        assertThat(agepi.getMontant()).isEqualTo(200);
    }
    
    
    @Test
    void calculerMontantAgepiMayotteTest12() {
        
        //Si DE Mayotte, futur contrat CDI avec 20h/semaine, 2 enfants à charge de 9ans et 8ans
        DemandeurEmploi demandeurEmploi = new DemandeurEmploi();
        
        FuturTravail futurContratTravail = new FuturTravail();
        futurContratTravail.setTypeContrat(TypesContratTravail.CDI.name());
        futurContratTravail.setNombreHeuresTravailleesSemaine(20);
        demandeurEmploi.setFuturTravail(futurContratTravail);
        
        InformationsPersonnelles informationsPersonnelles = new InformationsPersonnelles();
        informationsPersonnelles.setCodePostal(CODE_POSTAL_MAYOTTE);
        demandeurEmploi.setInformationsPersonnelles(informationsPersonnelles);
        
        SituationFamiliale situationFamiliale = new SituationFamiliale();
        List<Personne> personnesACharge = new ArrayList<Personne>();
        testUtile.createPersonne(personnesACharge, 9);  
        testUtile.createPersonne(personnesACharge, 8); 
        situationFamiliale.setPersonnesACharge(personnesACharge);
        demandeurEmploi.setSituationFamiliale(situationFamiliale);
        
        //Lorsque l'on calcul le montant de l'agepi
        AideSociale agepi = agepiUtile.calculer(demandeurEmploi);
        
        //alors le montant retourné est de 230 euros
        assertThat(agepi.getMontant()).isEqualTo(230);
    }
    
    @Test
    void calculerMontantAgepiMayotteTest13() {
        
        //Si DE Mayotte, futur contrat CDI avec 20h/semaine, 3 enfants à charge de 9ans, 8ans et 8ans
        DemandeurEmploi demandeurEmploi = new DemandeurEmploi();
        
        FuturTravail futurContratTravail = new FuturTravail();
        futurContratTravail.setTypeContrat(TypesContratTravail.CDI.name());
        futurContratTravail.setNombreHeuresTravailleesSemaine(20);
        demandeurEmploi.setFuturTravail(futurContratTravail);
        
        InformationsPersonnelles informationsPersonnelles = new InformationsPersonnelles();
        informationsPersonnelles.setCodePostal(CODE_POSTAL_MAYOTTE);
        demandeurEmploi.setInformationsPersonnelles(informationsPersonnelles);

        SituationFamiliale situationFamiliale = new SituationFamiliale();
        List<Personne> personnesACharge = new ArrayList<Personne>();
        testUtile.createPersonne(personnesACharge, 9);  
        testUtile.createPersonne(personnesACharge, 8); 
        testUtile.createPersonne(personnesACharge, 8); 
        situationFamiliale.setPersonnesACharge(personnesACharge);
        demandeurEmploi.setSituationFamiliale(situationFamiliale);
        
        //Lorsque l'on calcul le montant de l'agepi
        AideSociale agepi = agepiUtile.calculer(demandeurEmploi);
        
        //alors le montant retourné est de 260 euros
        assertThat(agepi.getMontant()).isEqualTo(260);
    }
    
    @Test
    void calculerMontantAgepiMayotteTest14() {
        
        //Si DE Mayotte, futur contrat CDI avec 20h/semaine, 4 enfants à charge de 9ans, 8ans, 8ans et 5ans
        DemandeurEmploi demandeurEmploi = new DemandeurEmploi();
        
        FuturTravail futurContratTravail = new FuturTravail();
        futurContratTravail.setTypeContrat(TypesContratTravail.CDI.name());
        futurContratTravail.setNombreHeuresTravailleesSemaine(20);
        demandeurEmploi.setFuturTravail(futurContratTravail);
        
        InformationsPersonnelles informationsPersonnelles = new InformationsPersonnelles();
        informationsPersonnelles.setCodePostal(CODE_POSTAL_MAYOTTE);
        demandeurEmploi.setInformationsPersonnelles(informationsPersonnelles);

        SituationFamiliale situationFamiliale = new SituationFamiliale();
        List<Personne> personnesACharge = new ArrayList<Personne>();
        testUtile.createPersonne(personnesACharge, 9);  
        testUtile.createPersonne(personnesACharge, 8); 
        testUtile.createPersonne(personnesACharge, 8); 
        testUtile.createPersonne(personnesACharge, 5); 
        situationFamiliale.setPersonnesACharge(personnesACharge);
        demandeurEmploi.setSituationFamiliale(situationFamiliale);
        
        //Lorsque l'on calcul le montant de l'agepi
        AideSociale agepi = agepiUtile.calculer(demandeurEmploi);
        
        //alors le montant retourné est de 260 euros
        assertThat(agepi.getMontant()).isEqualTo(260);
    }
    
    @Test
    void calculerMontantAgepiMayotteTest15() {
        
        //Si DE Mayotte, futur contrat CDI avec 20h/semaine, pas d'enfants à charge
        DemandeurEmploi demandeurEmploi = new DemandeurEmploi();
        
        FuturTravail futurContratTravail = new FuturTravail();
        futurContratTravail.setTypeContrat(TypesContratTravail.CDI.name());
        futurContratTravail.setNombreHeuresTravailleesSemaine(20);
        demandeurEmploi.setFuturTravail(futurContratTravail);
        
        InformationsPersonnelles informationsPersonnelles = new InformationsPersonnelles();
        informationsPersonnelles.setCodePostal(CODE_POSTAL_MAYOTTE);
        demandeurEmploi.setInformationsPersonnelles(informationsPersonnelles);

        SituationFamiliale situationFamiliale = new SituationFamiliale();
        List<Personne> personnesACharge = new ArrayList<Personne>();
        testUtile.createPersonne(personnesACharge, 13);  
        situationFamiliale.setPersonnesACharge(personnesACharge);
        demandeurEmploi.setSituationFamiliale(situationFamiliale);
        
        //Lorsque l'on calcul le montant de l'agepi
        AideSociale agepi = agepiUtile.calculer(demandeurEmploi);
        
        //alors le montant retourné est de 0 euros
        assertThat(agepi.getMontant()).isEqualTo(0);
    }
    
 /***  dureeTravailEnHeuresSemaine = 35h ****/
    
    @Test
    void calculerMontantAgepiMayotteTest16() {
        
        //Si DE Mayotte, futur contrat CDI avec 35h/semaine, 1 enfant à charge de 9ans
        DemandeurEmploi demandeurEmploi = new DemandeurEmploi();
        
        FuturTravail futurContratTravail = new FuturTravail();
        futurContratTravail.setTypeContrat(TypesContratTravail.CDI.name());
        futurContratTravail.setNombreHeuresTravailleesSemaine(35);
        demandeurEmploi.setFuturTravail(futurContratTravail);
        
        InformationsPersonnelles informationsPersonnelles = new InformationsPersonnelles();
        informationsPersonnelles.setCodePostal(CODE_POSTAL_MAYOTTE);
        demandeurEmploi.setInformationsPersonnelles(informationsPersonnelles);
        
        SituationFamiliale situationFamiliale = new SituationFamiliale();
        List<Personne> personnesACharge = new ArrayList<Personne>();
        testUtile.createPersonne(personnesACharge, 9);  
        situationFamiliale.setPersonnesACharge(personnesACharge);
        demandeurEmploi.setSituationFamiliale(situationFamiliale);
        
        //Lorsque l'on calcul le montant de l'agepi
        AideSociale agepi = agepiUtile.calculer(demandeurEmploi);
        
        //alors le montant retourné est de 200 euros
        assertThat(agepi.getMontant()).isEqualTo(200);
    }
    
    
    @Test
    void calculerMontantAgepiMayotteTest17() {
        
        //Si DE Mayotte, futur contrat CDI avec 35h/semaine, 2 enfants à charge de 9ans et 8ans
        DemandeurEmploi demandeurEmploi = new DemandeurEmploi();
        
        FuturTravail futurContratTravail = new FuturTravail();
        futurContratTravail.setTypeContrat(TypesContratTravail.CDI.name());
        futurContratTravail.setNombreHeuresTravailleesSemaine(35);
        demandeurEmploi.setFuturTravail(futurContratTravail);
        
        InformationsPersonnelles informationsPersonnelles = new InformationsPersonnelles();
        informationsPersonnelles.setCodePostal(CODE_POSTAL_MAYOTTE);
        demandeurEmploi.setInformationsPersonnelles(informationsPersonnelles);
        
        SituationFamiliale situationFamiliale = new SituationFamiliale();
        List<Personne> personnesACharge = new ArrayList<Personne>();
        testUtile.createPersonne(personnesACharge, 9);  
        testUtile.createPersonne(personnesACharge, 8); 
        situationFamiliale.setPersonnesACharge(personnesACharge);
        demandeurEmploi.setSituationFamiliale(situationFamiliale);
        
        //Lorsque l'on calcul le montant de l'agepi
        AideSociale agepi = agepiUtile.calculer(demandeurEmploi);
        
        //alors le montant retourné est de 230 euros
        assertThat(agepi.getMontant()).isEqualTo(230);
    }
    
    @Test
    void calculerMontantAgepiMayotteTest18() {
        
        //Si DE Mayotte, futur contrat CDI avec 35h/semaine, 3 enfants à charge de 9ans, 8ans et 8ans
        DemandeurEmploi demandeurEmploi = new DemandeurEmploi();
        
        FuturTravail futurContratTravail = new FuturTravail();
        futurContratTravail.setTypeContrat(TypesContratTravail.CDI.name());
        futurContratTravail.setNombreHeuresTravailleesSemaine(35);
        demandeurEmploi.setFuturTravail(futurContratTravail);
        
        InformationsPersonnelles informationsPersonnelles = new InformationsPersonnelles();
        informationsPersonnelles.setCodePostal(CODE_POSTAL_MAYOTTE);
        demandeurEmploi.setInformationsPersonnelles(informationsPersonnelles);

        SituationFamiliale situationFamiliale = new SituationFamiliale();
        List<Personne> personnesACharge = new ArrayList<Personne>();
        testUtile.createPersonne(personnesACharge, 9);  
        testUtile.createPersonne(personnesACharge, 8); 
        testUtile.createPersonne(personnesACharge, 8); 
        situationFamiliale.setPersonnesACharge(personnesACharge);
        demandeurEmploi.setSituationFamiliale(situationFamiliale);
        
        //Lorsque l'on calcul le montant de l'agepi
        AideSociale agepi = agepiUtile.calculer(demandeurEmploi);
        
        //alors le montant retourné est de 260 euros
        assertThat(agepi.getMontant()).isEqualTo(260);
    }
    
    @Test
    void calculerMontantAgepiMayotteTest19() {
        
        //Si DE Mayotte, futur contrat CDI avec 35h/semaine, 4 enfants à charge de 9ans, 8ans, 8ans et 5ans
        DemandeurEmploi demandeurEmploi = new DemandeurEmploi();
        
        FuturTravail futurContratTravail = new FuturTravail();
        futurContratTravail.setTypeContrat(TypesContratTravail.CDI.name());
        futurContratTravail.setNombreHeuresTravailleesSemaine(35);
        demandeurEmploi.setFuturTravail(futurContratTravail);
        
        InformationsPersonnelles informationsPersonnelles = new InformationsPersonnelles();
        informationsPersonnelles.setCodePostal(CODE_POSTAL_MAYOTTE);
        demandeurEmploi.setInformationsPersonnelles(informationsPersonnelles);
        
        SituationFamiliale situationFamiliale = new SituationFamiliale();
        List<Personne> personnesACharge = new ArrayList<Personne>();
        testUtile.createPersonne(personnesACharge, 9);  
        testUtile.createPersonne(personnesACharge, 8); 
        testUtile.createPersonne(personnesACharge, 8); 
        testUtile.createPersonne(personnesACharge, 5); 
        situationFamiliale.setPersonnesACharge(personnesACharge);
        demandeurEmploi.setSituationFamiliale(situationFamiliale);
        
        //Lorsque l'on calcul le montant de l'agepi
        AideSociale agepi = agepiUtile.calculer(demandeurEmploi);
        
        //alors le montant retourné est de 260 euros
        assertThat(agepi.getMontant()).isEqualTo(260);
    }
    
    @Test
    void calculerMontantAgepiMayotteTest20() {
        
        //Si DE Mayotte, futur contrat CDI avec 35h/semaine, pas d'enfants à charge
        DemandeurEmploi demandeurEmploi = new DemandeurEmploi();
        
        FuturTravail futurContratTravail = new FuturTravail();
        futurContratTravail.setTypeContrat(TypesContratTravail.CDI.name());
        futurContratTravail.setNombreHeuresTravailleesSemaine(35);
        demandeurEmploi.setFuturTravail(futurContratTravail);
        
        InformationsPersonnelles informationsPersonnelles = new InformationsPersonnelles();
        informationsPersonnelles.setCodePostal(CODE_POSTAL_MAYOTTE);
        demandeurEmploi.setInformationsPersonnelles(informationsPersonnelles);

        SituationFamiliale situationFamiliale = new SituationFamiliale();
        List<Personne> personnesACharge = new ArrayList<Personne>();
        testUtile.createPersonne(personnesACharge, 10);  
        situationFamiliale.setPersonnesACharge(personnesACharge);
        demandeurEmploi.setSituationFamiliale(situationFamiliale);
        
        //Lorsque l'on calcul le montant de l'agepi
        AideSociale agepi = agepiUtile.calculer(demandeurEmploi);
        
        //alors le montant retourné est de 0 euros
        assertThat(agepi.getMontant()).isEqualTo(0);
    }
    
    /***  dureeTravailEnHeuresSemaine 40h ****/
    
    @Test
    void calculerMontantAgepiMayotteTest21() {
        
        //Si DE Mayotte, futur contrat CDI avec 40h/semaine, 1 enfant à charge de 9ans
        DemandeurEmploi demandeurEmploi = new DemandeurEmploi();
        
        FuturTravail futurContratTravail = new FuturTravail();
        futurContratTravail.setTypeContrat(TypesContratTravail.CDI.name());
        futurContratTravail.setNombreHeuresTravailleesSemaine(40);
        demandeurEmploi.setFuturTravail(futurContratTravail);
        
        InformationsPersonnelles informationsPersonnelles = new InformationsPersonnelles();
        informationsPersonnelles.setCodePostal(CODE_POSTAL_MAYOTTE);
        demandeurEmploi.setInformationsPersonnelles(informationsPersonnelles);

        SituationFamiliale situationFamiliale = new SituationFamiliale();
        List<Personne> personnesACharge = new ArrayList<Personne>();
        testUtile.createPersonne(personnesACharge, 9);  
        situationFamiliale.setPersonnesACharge(personnesACharge);
        demandeurEmploi.setSituationFamiliale(situationFamiliale);
        
        //Lorsque l'on calcul le montant de l'agepi
        AideSociale agepi = agepiUtile.calculer(demandeurEmploi);
        
        //alors le montant retourné est de 200 euros
        assertThat(agepi.getMontant()).isEqualTo(200);
    }
    
    @Test
    void calculerMontantAgepiMayotteTest22() {
        
        //Si DE Mayotte, futur contrat CDI avec 40h/semaine, 2 enfants à charge de 9ans et 8ans
        DemandeurEmploi demandeurEmploi = new DemandeurEmploi();
        
        FuturTravail futurContratTravail = new FuturTravail();
        futurContratTravail.setTypeContrat(TypesContratTravail.CDI.name());
        futurContratTravail.setNombreHeuresTravailleesSemaine(40);
        demandeurEmploi.setFuturTravail(futurContratTravail);
        
        InformationsPersonnelles informationsPersonnelles = new InformationsPersonnelles();
        informationsPersonnelles.setCodePostal(CODE_POSTAL_MAYOTTE);
        demandeurEmploi.setInformationsPersonnelles(informationsPersonnelles);

        SituationFamiliale situationFamiliale = new SituationFamiliale();
        List<Personne> personnesACharge = new ArrayList<Personne>();
        testUtile.createPersonne(personnesACharge, 9);  
        testUtile.createPersonne(personnesACharge, 8); 
        situationFamiliale.setPersonnesACharge(personnesACharge);
        demandeurEmploi.setSituationFamiliale(situationFamiliale);
        
        //Lorsque l'on calcul le montant de l'agepi
        AideSociale agepi = agepiUtile.calculer(demandeurEmploi);
        
        //alors le montant retourné est de 230 euros
        assertThat(agepi.getMontant()).isEqualTo(230);
    }
    
    @Test
    void calculerMontantAgepiMayotteTest23() {
        
        //Si DE Mayotte, futur contrat CDI avec 40h/semaine, 3 enfants à charge de 9ans, 8ans et 8ans
        DemandeurEmploi demandeurEmploi = new DemandeurEmploi();
        
        FuturTravail futurContratTravail = new FuturTravail();
        futurContratTravail.setTypeContrat(TypesContratTravail.CDI.name());
        futurContratTravail.setNombreHeuresTravailleesSemaine(40);
        demandeurEmploi.setFuturTravail(futurContratTravail);
        
        InformationsPersonnelles informationsPersonnelles = new InformationsPersonnelles();
        informationsPersonnelles.setCodePostal(CODE_POSTAL_MAYOTTE);
        demandeurEmploi.setInformationsPersonnelles(informationsPersonnelles);
        
        SituationFamiliale situationFamiliale = new SituationFamiliale();
        List<Personne> personnesACharge = new ArrayList<Personne>();
        testUtile.createPersonne(personnesACharge, 9);  
        testUtile.createPersonne(personnesACharge, 8); 
        testUtile.createPersonne(personnesACharge, 8); 
        situationFamiliale.setPersonnesACharge(personnesACharge);
        demandeurEmploi.setSituationFamiliale(situationFamiliale);
        
        //Lorsque l'on calcul le montant de l'agepi
        AideSociale agepi = agepiUtile.calculer(demandeurEmploi);
        
        //alors le montant retourné est de 260 euros
        assertThat(agepi.getMontant()).isEqualTo(260);
    }
    
    @Test
    void calculerMontantAgepiMayotteTest24() {
        
        //Si DE Mayotte, futur contrat CDI avec 40h/semaine, 4 enfants à charge de 9ans, 8ans, 5ans et 8ans
        DemandeurEmploi demandeurEmploi = new DemandeurEmploi();
        
        FuturTravail futurContratTravail = new FuturTravail();
        futurContratTravail.setTypeContrat(TypesContratTravail.CDI.name());
        futurContratTravail.setNombreHeuresTravailleesSemaine(40);
        demandeurEmploi.setFuturTravail(futurContratTravail);
        
        InformationsPersonnelles informationsPersonnelles = new InformationsPersonnelles();
        informationsPersonnelles.setCodePostal(CODE_POSTAL_MAYOTTE);
        demandeurEmploi.setInformationsPersonnelles(informationsPersonnelles);

        SituationFamiliale situationFamiliale = new SituationFamiliale();
        List<Personne> personnesACharge = new ArrayList<Personne>();
        testUtile.createPersonne(personnesACharge, 9);  
        testUtile.createPersonne(personnesACharge, 8); 
        testUtile.createPersonne(personnesACharge, 8); 
        testUtile.createPersonne(personnesACharge, 5); 
        situationFamiliale.setPersonnesACharge(personnesACharge);
        demandeurEmploi.setSituationFamiliale(situationFamiliale);
        
        //Lorsque l'on calcul le montant de l'agepi
        AideSociale agepi = agepiUtile.calculer(demandeurEmploi);
        
        //alors le montant retourné est de 260 euros
        assertThat(agepi.getMontant()).isEqualTo(260);
    }
    
    @Test
    void calculerMontantAgepiMayotteTest25() {
        
        //Si DE Mayotte, futur contrat CDI avec 40h/semaine, pas d'enfants à charge
        DemandeurEmploi demandeurEmploi = new DemandeurEmploi();
        
        FuturTravail futurContratTravail = new FuturTravail();
        futurContratTravail.setTypeContrat(TypesContratTravail.CDI.name());
        futurContratTravail.setNombreHeuresTravailleesSemaine(40);
        demandeurEmploi.setFuturTravail(futurContratTravail);
        
        InformationsPersonnelles informationsPersonnelles = new InformationsPersonnelles();
        informationsPersonnelles.setCodePostal(CODE_POSTAL_MAYOTTE);
        demandeurEmploi.setInformationsPersonnelles(informationsPersonnelles);

        SituationFamiliale situationFamiliale = new SituationFamiliale();
        List<Personne> personnesACharge = new ArrayList<Personne>();
        testUtile.createPersonne(personnesACharge, 16); 
        situationFamiliale.setPersonnesACharge(personnesACharge);
        demandeurEmploi.setSituationFamiliale(situationFamiliale);
        
        //Lorsque l'on calcul le montant de l'agepi
        AideSociale agepi = agepiUtile.calculer(demandeurEmploi);
        
        //alors le montant retourné est de 0 euros
        assertThat(agepi.getMontant()).isEqualTo(0);
    }  
}
