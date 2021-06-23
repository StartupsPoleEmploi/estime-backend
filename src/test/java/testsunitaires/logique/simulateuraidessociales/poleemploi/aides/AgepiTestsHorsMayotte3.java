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
class AgepiTestsHorsMayotte3 {
    
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
    
    /************* département hors Mayotte *********/
    
    @Test
    void calculerMontantAgepiTest10() {
        
        //Si DE France métropolitaine, futur contrat CDI avec 15h/semaine, 1 enfant à charge de 12ans
        DemandeurEmploi demandeurEmploi = new DemandeurEmploi();
        
        FuturTravail futurContratTravail = new FuturTravail();
        futurContratTravail.setTypeContrat(TypesContratTravail.CDI.name());
        futurContratTravail.setNombreHeuresTravailleesSemaine(15);
        demandeurEmploi.setFuturTravail(futurContratTravail);
        
        InformationsPersonnelles informationsPersonnelles = new InformationsPersonnelles();
        informationsPersonnelles.setCodePostal(CODE_POSTAL_METROPOLITAIN);
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
    void calculerMontantAgepiTest11() {
        
        //Si DE France métropolitaine, futur contrat CDI avec 20h/semaine, 1 enfant à charge de 9ans
        DemandeurEmploi demandeurEmploi = new DemandeurEmploi();
        
        FuturTravail futurContratTravail = new FuturTravail();
        futurContratTravail.setTypeContrat(TypesContratTravail.CDI.name());
        futurContratTravail.setNombreHeuresTravailleesSemaine(20);
        demandeurEmploi.setFuturTravail(futurContratTravail);
        
        InformationsPersonnelles informationsPersonnelles = new InformationsPersonnelles();
        informationsPersonnelles.setCodePostal(CODE_POSTAL_METROPOLITAIN);
        demandeurEmploi.setInformationsPersonnelles(informationsPersonnelles);
        
        SituationFamiliale situationFamiliale = new SituationFamiliale();
        List<Personne> personnesACharge = new ArrayList<Personne>();
        testUtile.createPersonne(personnesACharge, 9);  
        situationFamiliale.setPersonnesACharge(personnesACharge);
        demandeurEmploi.setSituationFamiliale(situationFamiliale);
        
        //Lorsque l'on calcul le montant de l'agepi
        AideSociale agepi = agepiUtile.calculer(demandeurEmploi);
        
        //alors le montant retourné est de 400 euros
        assertThat(agepi.getMontant()).isEqualTo(400);
    }
    
    
    @Test
    void calculerMontantAgepiTest12() {
        
        //Si DE France métropolitaine, futur contrat CDI avec 20h/semaine, 2 enfants à charge de 9ans et 8ans
        DemandeurEmploi demandeurEmploi = new DemandeurEmploi();
        
        FuturTravail futurContratTravail = new FuturTravail();
        futurContratTravail.setTypeContrat(TypesContratTravail.CDI.name());
        futurContratTravail.setNombreHeuresTravailleesSemaine(20);
        demandeurEmploi.setFuturTravail(futurContratTravail);
        
        InformationsPersonnelles informationsPersonnelles = new InformationsPersonnelles();
        informationsPersonnelles.setCodePostal(CODE_POSTAL_METROPOLITAIN);
        demandeurEmploi.setInformationsPersonnelles(informationsPersonnelles);
        
        SituationFamiliale situationFamiliale = new SituationFamiliale();
        List<Personne> personnesACharge = new ArrayList<Personne>();
        testUtile.createPersonne(personnesACharge, 9);  
        testUtile.createPersonne(personnesACharge, 8); 
        situationFamiliale.setPersonnesACharge(personnesACharge);
        demandeurEmploi.setSituationFamiliale(situationFamiliale);
        
        //Lorsque l'on calcul le montant de l'agepi
        AideSociale agepi = agepiUtile.calculer(demandeurEmploi);
        
        //alors le montant retourné est de 460 euros
        assertThat(agepi.getMontant()).isEqualTo(460);
    }
    
    @Test
    void calculerMontantAgepiTest13() {
        
        //Si DE France métropolitaine, futur contrat CDI avec 20h/semaine, 3 enfants à charge de 9ans, 8ans et 8ans
        DemandeurEmploi demandeurEmploi = new DemandeurEmploi();
        
        FuturTravail futurContratTravail = new FuturTravail();
        futurContratTravail.setTypeContrat(TypesContratTravail.CDI.name());
        futurContratTravail.setNombreHeuresTravailleesSemaine(20);
        demandeurEmploi.setFuturTravail(futurContratTravail);
        
        InformationsPersonnelles informationsPersonnelles = new InformationsPersonnelles();
        informationsPersonnelles.setCodePostal(CODE_POSTAL_METROPOLITAIN);
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
        
        //alors le montant retourné est de 520 euros
        assertThat(agepi.getMontant()).isEqualTo(520);
    }
    
    @Test
    void calculerMontantAgepiTest14() {
        
        //Si DE France métropolitaine, futur contrat CDI avec 20h/semaine, 4 enfants à charge de 9ans, 8ans, 8ans et 5ans
        DemandeurEmploi demandeurEmploi = new DemandeurEmploi();
        
        FuturTravail futurContratTravail = new FuturTravail();
        futurContratTravail.setTypeContrat(TypesContratTravail.CDI.name());
        futurContratTravail.setNombreHeuresTravailleesSemaine(20);
        demandeurEmploi.setFuturTravail(futurContratTravail);
        
        InformationsPersonnelles informationsPersonnelles = new InformationsPersonnelles();
        informationsPersonnelles.setCodePostal(CODE_POSTAL_METROPOLITAIN);
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
        
        //alors le montant retourné est de 520 euros
        assertThat(agepi.getMontant()).isEqualTo(520);
    }
    
    @Test
    void calculerMontantAgepiTest15() {
        
        //Si DE France métropolitaine, futur contrat CDI avec 20h/semaine, pas d'enfants à charge
        DemandeurEmploi demandeurEmploi = new DemandeurEmploi();
        
        FuturTravail futurContratTravail = new FuturTravail();
        futurContratTravail.setTypeContrat(TypesContratTravail.CDI.name());
        futurContratTravail.setNombreHeuresTravailleesSemaine(20);
        demandeurEmploi.setFuturTravail(futurContratTravail);
        
        InformationsPersonnelles informationsPersonnelles = new InformationsPersonnelles();
        informationsPersonnelles.setCodePostal(CODE_POSTAL_METROPOLITAIN);
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
    void calculerMontantAgepiTest16() {
        
        //Si DE France métropolitaine, futur contrat CDI avec 35h/semaine, 1 enfant à charge de 9ans
        DemandeurEmploi demandeurEmploi = new DemandeurEmploi();
        
        FuturTravail futurContratTravail = new FuturTravail();
        futurContratTravail.setTypeContrat(TypesContratTravail.CDI.name());
        futurContratTravail.setNombreHeuresTravailleesSemaine(35);
        demandeurEmploi.setFuturTravail(futurContratTravail);
        
        InformationsPersonnelles informationsPersonnelles = new InformationsPersonnelles();
        informationsPersonnelles.setCodePostal(CODE_POSTAL_METROPOLITAIN);
        demandeurEmploi.setInformationsPersonnelles(informationsPersonnelles);
        
        SituationFamiliale situationFamiliale = new SituationFamiliale();
        List<Personne> personnesACharge = new ArrayList<Personne>();
        testUtile.createPersonne(personnesACharge, 9);  
        situationFamiliale.setPersonnesACharge(personnesACharge);
        demandeurEmploi.setSituationFamiliale(situationFamiliale);
        
        //Lorsque l'on calcul le montant de l'agepi
        AideSociale agepi = agepiUtile.calculer(demandeurEmploi);
        
        //alors le montant retourné est de 400 euros
        assertThat(agepi.getMontant()).isEqualTo(400);
    }
    
    
    @Test
    void calculerMontantAgepiTest17() {
        
        //Si DE France métropolitaine, futur contrat CDI avec 35h/semaine, 2 enfants à charge de 9ans et 8ans
        DemandeurEmploi demandeurEmploi = new DemandeurEmploi();
        
        FuturTravail futurContratTravail = new FuturTravail();
        futurContratTravail.setTypeContrat(TypesContratTravail.CDI.name());
        futurContratTravail.setNombreHeuresTravailleesSemaine(35);
        demandeurEmploi.setFuturTravail(futurContratTravail);
        
        InformationsPersonnelles informationsPersonnelles = new InformationsPersonnelles();
        informationsPersonnelles.setCodePostal(CODE_POSTAL_METROPOLITAIN);
        demandeurEmploi.setInformationsPersonnelles(informationsPersonnelles);
        
        SituationFamiliale situationFamiliale = new SituationFamiliale();
        List<Personne> personnesACharge = new ArrayList<Personne>();
        testUtile.createPersonne(personnesACharge, 9);  
        testUtile.createPersonne(personnesACharge, 8); 
        situationFamiliale.setPersonnesACharge(personnesACharge);
        demandeurEmploi.setSituationFamiliale(situationFamiliale);
        
        //Lorsque l'on calcul le montant de l'agepi
        AideSociale agepi = agepiUtile.calculer(demandeurEmploi);
        
        //alors le montant retourné est de 460 euros
        assertThat(agepi.getMontant()).isEqualTo(460);
    }
    
    @Test
    void calculerMontantAgepiTest18() {
        
        //Si DE France métropolitaine, futur contrat CDI avec 35h/semaine, 3 enfants à charge de 9ans, 8ans et 8ans
        DemandeurEmploi demandeurEmploi = new DemandeurEmploi();
        
        FuturTravail futurContratTravail = new FuturTravail();
        futurContratTravail.setTypeContrat(TypesContratTravail.CDI.name());
        futurContratTravail.setNombreHeuresTravailleesSemaine(35);
        demandeurEmploi.setFuturTravail(futurContratTravail);
        
        InformationsPersonnelles informationsPersonnelles = new InformationsPersonnelles();
        informationsPersonnelles.setCodePostal(CODE_POSTAL_METROPOLITAIN);
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
        
        //alors le montant retourné est de 520 euros
        assertThat(agepi.getMontant()).isEqualTo(520);
    }
    
    @Test
    void calculerMontantAgepiTest19() {
        
        //Si DE France métropolitaine, futur contrat CDI avec 35h/semaine, 4 enfants à charge de 9ans, 8ans, 8ans et 5ans
        DemandeurEmploi demandeurEmploi = new DemandeurEmploi();
        
        FuturTravail futurContratTravail = new FuturTravail();
        futurContratTravail.setTypeContrat(TypesContratTravail.CDI.name());
        futurContratTravail.setNombreHeuresTravailleesSemaine(35);
        demandeurEmploi.setFuturTravail(futurContratTravail);
        
        InformationsPersonnelles informationsPersonnelles = new InformationsPersonnelles();
        informationsPersonnelles.setCodePostal(CODE_POSTAL_METROPOLITAIN);
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
        
        //alors le montant retourné est de 520 euros
        assertThat(agepi.getMontant()).isEqualTo(520);
    }
    
    @Test
    void calculerMontantAgepiTest20() {
        
        //Si DE France métropolitaine, futur contrat CDI avec 35h/semaine, pas d'enfants à charge
        DemandeurEmploi demandeurEmploi = new DemandeurEmploi();
        
        FuturTravail futurContratTravail = new FuturTravail();
        futurContratTravail.setTypeContrat(TypesContratTravail.CDI.name());
        futurContratTravail.setNombreHeuresTravailleesSemaine(35);
        demandeurEmploi.setFuturTravail(futurContratTravail);
        
        InformationsPersonnelles informationsPersonnelles = new InformationsPersonnelles();
        informationsPersonnelles.setCodePostal(CODE_POSTAL_METROPOLITAIN);
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
    void calculerMontantAgepiTest21() {
        
        //Si DE France métropolitaine, futur contrat CDI avec 40h/semaine, 1 enfant à charge de 9ans
        DemandeurEmploi demandeurEmploi = new DemandeurEmploi();
        
        FuturTravail futurContratTravail = new FuturTravail();
        futurContratTravail.setTypeContrat(TypesContratTravail.CDI.name());
        futurContratTravail.setNombreHeuresTravailleesSemaine(40);
        demandeurEmploi.setFuturTravail(futurContratTravail);
        
        InformationsPersonnelles informationsPersonnelles = new InformationsPersonnelles();
        informationsPersonnelles.setCodePostal(CODE_POSTAL_METROPOLITAIN);
        demandeurEmploi.setInformationsPersonnelles(informationsPersonnelles);

        SituationFamiliale situationFamiliale = new SituationFamiliale();
        List<Personne> personnesACharge = new ArrayList<Personne>();
        testUtile.createPersonne(personnesACharge, 9);  
        situationFamiliale.setPersonnesACharge(personnesACharge);
        demandeurEmploi.setSituationFamiliale(situationFamiliale);
        
        //Lorsque l'on calcul le montant de l'agepi
        AideSociale agepi = agepiUtile.calculer(demandeurEmploi);
        
        //alors le montant retourné est de 400 euros
        assertThat(agepi.getMontant()).isEqualTo(400);
    }
    
    @Test
    void calculerMontantAgepiTest22() {
        
        //Si DE France métropolitaine, futur contrat CDI avec 40h/semaine, 2 enfants à charge de 9ans et 8ans
        DemandeurEmploi demandeurEmploi = new DemandeurEmploi();
        
        FuturTravail futurContratTravail = new FuturTravail();
        futurContratTravail.setTypeContrat(TypesContratTravail.CDI.name());
        futurContratTravail.setNombreHeuresTravailleesSemaine(40);
        demandeurEmploi.setFuturTravail(futurContratTravail);
        
        InformationsPersonnelles informationsPersonnelles = new InformationsPersonnelles();
        informationsPersonnelles.setCodePostal(CODE_POSTAL_METROPOLITAIN);
        demandeurEmploi.setInformationsPersonnelles(informationsPersonnelles);

        SituationFamiliale situationFamiliale = new SituationFamiliale();
        List<Personne> personnesACharge = new ArrayList<Personne>();
        testUtile.createPersonne(personnesACharge, 9);  
        testUtile.createPersonne(personnesACharge, 8); 
        situationFamiliale.setPersonnesACharge(personnesACharge);
        demandeurEmploi.setSituationFamiliale(situationFamiliale);
        
        //Lorsque l'on calcul le montant de l'agepi
        AideSociale agepi = agepiUtile.calculer(demandeurEmploi);
        
        //alors le montant retourné est de 460 euros
        assertThat(agepi.getMontant()).isEqualTo(460);
    }
    
    @Test
    void calculerMontantAgepiTest23() {
        
        //Si DE France métropolitaine, futur contrat CDI avec 40h/semaine, 3 enfants à charge de 9ans, 8ans et 8ans
        DemandeurEmploi demandeurEmploi = new DemandeurEmploi();
        
        FuturTravail futurContratTravail = new FuturTravail();
        futurContratTravail.setTypeContrat(TypesContratTravail.CDI.name());
        futurContratTravail.setNombreHeuresTravailleesSemaine(40);
        demandeurEmploi.setFuturTravail(futurContratTravail);
        
        InformationsPersonnelles informationsPersonnelles = new InformationsPersonnelles();
        informationsPersonnelles.setCodePostal(CODE_POSTAL_METROPOLITAIN);
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
        
        //alors le montant retourné est de 520 euros
        assertThat(agepi.getMontant()).isEqualTo(520);
    }

    @Test
    void calculerMontantAgepiTest24() {
        
        //Si DE France métropolitaine, futur contrat CDI avec 40h/semaine, 4 enfants à charge de 9ans, 8ans, 5ans et 8ans
        DemandeurEmploi demandeurEmploi = new DemandeurEmploi();
        
        FuturTravail futurContratTravail = new FuturTravail();
        futurContratTravail.setTypeContrat(TypesContratTravail.CDI.name());
        futurContratTravail.setNombreHeuresTravailleesSemaine(40);
        demandeurEmploi.setFuturTravail(futurContratTravail);
        
        InformationsPersonnelles informationsPersonnelles = new InformationsPersonnelles();
        informationsPersonnelles.setCodePostal(CODE_POSTAL_METROPOLITAIN);
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
        
        //alors le montant retourné est de 520 euros
        assertThat(agepi.getMontant()).isEqualTo(520);
    }
    
    @Test
    void calculerMontantAgepiTest25() {
        
        //Si DE France métropolitaine, futur contrat CDI avec 40h/semaine, pas d'enfants à charge
        DemandeurEmploi demandeurEmploi = new DemandeurEmploi();
        
        FuturTravail futurContratTravail = new FuturTravail();
        futurContratTravail.setTypeContrat(TypesContratTravail.CDI.name());
        futurContratTravail.setNombreHeuresTravailleesSemaine(40);
        demandeurEmploi.setFuturTravail(futurContratTravail);
        
        InformationsPersonnelles informationsPersonnelles = new InformationsPersonnelles();
        informationsPersonnelles.setCodePostal(CODE_POSTAL_METROPOLITAIN);
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
