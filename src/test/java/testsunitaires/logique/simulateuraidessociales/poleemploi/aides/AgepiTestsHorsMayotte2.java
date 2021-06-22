package testsunitaires.logique.simulateuraidessociales.poleemploi.aides;

import static org.assertj.core.api.Assertions.assertThat;

import java.text.ParseException;
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
import fr.poleemploi.estime.services.ressources.AllocationsPoleEmploi;
import fr.poleemploi.estime.services.ressources.BeneficiaireAidesSociales;
import fr.poleemploi.estime.services.ressources.DemandeurEmploi;
import fr.poleemploi.estime.services.ressources.FuturTravail;
import fr.poleemploi.estime.services.ressources.InformationsPersonnelles;
import fr.poleemploi.estime.services.ressources.Personne;
import fr.poleemploi.estime.services.ressources.RessourcesFinancieres;
import fr.poleemploi.estime.services.ressources.SituationFamiliale;
import utiletests.TestUtile;


@ContextConfiguration
@SpringBootTest
@TestPropertySource(locations="classpath:application-test.properties")
class AgepiTestsHorsMayotte2 {
    
    public static final String CODE_POSTAL_MAYOTTE = "97600";
    public static final String CODE_POSTAL_METROPOLITAIN = "72300";
    public static final int PREMIER_MOIS_SIMULATION = 1;
    
    @Autowired
    private Agepi agepiUtile;
    
    @Autowired
    TestUtile testUtile;
    
    @Configuration
    @ComponentScan({"utiletests","fr.poleemploi.estime"})
    public static class SpringConfig {

    }
    
    /************* département hors Mayotte *********/
    
    
    @Test
    void isNotEligibleTest1() {
        
        //Si DE France Métropolitaine, futur contrat CDI, pas en couple, 1 enfant 10 ans à charge et allocation journalière net = 14.38 euros (< seuil max.)
        DemandeurEmploi demandeurEmploi = new DemandeurEmploi();
        
        FuturTravail futurContratTravail = new FuturTravail();
        futurContratTravail.setTypeContrat(TypesContratTravail.CDI.name());
        demandeurEmploi.setFuturTravail(futurContratTravail);
        
        InformationsPersonnelles informationsPersonnelles = new InformationsPersonnelles();
        informationsPersonnelles.setCodePostal(CODE_POSTAL_METROPOLITAIN);
        demandeurEmploi.setInformationsPersonnelles(informationsPersonnelles);
        
        BeneficiaireAidesSociales beneficiaireAidesSociales = new BeneficiaireAidesSociales();
        beneficiaireAidesSociales.setBeneficiaireARE(true);
        demandeurEmploi.setBeneficiaireAidesSociales(beneficiaireAidesSociales);
        
        RessourcesFinancieres ressourcesFinancieres = new RessourcesFinancieres();
        AllocationsPoleEmploi allocationsPE = new AllocationsPoleEmploi();
        allocationsPE.setAllocationJournaliereNet(14.38f);
        ressourcesFinancieres.setAllocationsPoleEmploi(allocationsPE);
        demandeurEmploi.setRessourcesFinancieres(ressourcesFinancieres); 
        
        SituationFamiliale situationFamiliale = new SituationFamiliale();
        List<Personne> personnesACharge = new ArrayList<Personne>();
        testUtile.createPersonne(personnesACharge, 10); 
        
        situationFamiliale.setPersonnesACharge(personnesACharge);
        situationFamiliale.setIsEnCouple(false);
        demandeurEmploi.setSituationFamiliale(situationFamiliale);
        
        
        //Lorsque l'on vérifie son éligibilité
        boolean isEligible = agepiUtile.isEligible(PREMIER_MOIS_SIMULATION, demandeurEmploi);
        
        //Alors le DE n'est pas éligible à l'AGEPI (isFalse)
        assertThat(isEligible).isFalse();
    }
    
    @Test
    void isNotEligibleTest2() {
        
        //Si DE France Métropolitaine, futur contrat CDI, en couple, 1 enfant 9 ans à charge et allocation journalière net = 14.28 euros (< seuil max.) 
        DemandeurEmploi demandeurEmploi = new DemandeurEmploi();
        
        FuturTravail futurContratTravail = new FuturTravail();
        futurContratTravail.setTypeContrat(TypesContratTravail.CDI.name());
        demandeurEmploi.setFuturTravail(futurContratTravail);
        
        InformationsPersonnelles informationsPersonnelles = new InformationsPersonnelles();
        informationsPersonnelles.setCodePostal(CODE_POSTAL_METROPOLITAIN);
        demandeurEmploi.setInformationsPersonnelles(informationsPersonnelles);
        
        BeneficiaireAidesSociales beneficiaireAidesSociales = new BeneficiaireAidesSociales();
        beneficiaireAidesSociales.setBeneficiaireARE(true);
        demandeurEmploi.setBeneficiaireAidesSociales(beneficiaireAidesSociales);
        
        RessourcesFinancieres ressourcesFinancieres = new RessourcesFinancieres();
        AllocationsPoleEmploi allocationsPE = new AllocationsPoleEmploi();
        allocationsPE.setAllocationJournaliereNet(14.28f);
        ressourcesFinancieres.setAllocationsPoleEmploi(allocationsPE);
        demandeurEmploi.setRessourcesFinancieres(ressourcesFinancieres); 
        
        SituationFamiliale situationFamiliale = new SituationFamiliale();
        List<Personne> personnesACharge = new ArrayList<Personne>();
        testUtile.createPersonne(personnesACharge, 9); 
        
        situationFamiliale.setPersonnesACharge(personnesACharge);
        situationFamiliale.setIsEnCouple(true);
        demandeurEmploi.setSituationFamiliale(situationFamiliale);
        
        
        //Lorsque l'on vérifie son éligibilité
        boolean isEligible = agepiUtile.isEligible(PREMIER_MOIS_SIMULATION, demandeurEmploi);
        
        //Alors le DE n'est pas éligible à l'AGEPI
        assertThat(isEligible).isFalse();
    }
    
    
    
    @Test
    void isNotEligibleTest3() {
        
        //Si DE France Métropolitaine, futur contrat CDI, pas en couple, 1 enfant 9 ans à charge et allocation journalière net = 30.28 euros (> seuil max.)
        DemandeurEmploi demandeurEmploi = new DemandeurEmploi();
        
        FuturTravail futurContratTravail = new FuturTravail();
        futurContratTravail.setTypeContrat(TypesContratTravail.CDI.name());
        demandeurEmploi.setFuturTravail(futurContratTravail);
        
        InformationsPersonnelles informationsPersonnelles = new InformationsPersonnelles();
        informationsPersonnelles.setCodePostal(CODE_POSTAL_METROPOLITAIN);
        demandeurEmploi.setInformationsPersonnelles(informationsPersonnelles);
        
        BeneficiaireAidesSociales beneficiaireAidesSociales = new BeneficiaireAidesSociales();
        beneficiaireAidesSociales.setBeneficiaireARE(true);
        demandeurEmploi.setBeneficiaireAidesSociales(beneficiaireAidesSociales);
        
        RessourcesFinancieres ressourcesFinancieres = new RessourcesFinancieres();
        AllocationsPoleEmploi allocationsPE = new AllocationsPoleEmploi();
        allocationsPE.setAllocationJournaliereNet(30.28f);
        ressourcesFinancieres.setAllocationsPoleEmploi(allocationsPE);
        demandeurEmploi.setRessourcesFinancieres(ressourcesFinancieres); 
        
        SituationFamiliale situationFamiliale = new SituationFamiliale();
        List<Personne> personnesACharge = new ArrayList<Personne>();
        testUtile.createPersonne(personnesACharge, 9); 
        
        situationFamiliale.setPersonnesACharge(personnesACharge);
        situationFamiliale.setIsEnCouple(false);
        demandeurEmploi.setSituationFamiliale(situationFamiliale);
        
        
        //Lorsque l'on vérifie son éligibilité
        boolean isEligible = agepiUtile.isEligible(PREMIER_MOIS_SIMULATION, demandeurEmploi);
        
        //Alors le DE n'est pas éligible à l'AGEPI
        assertThat(isEligible).isFalse();
    }
    
    @Test
    void isNotEligibleTest4() throws ParseException {
        
        //Si DE France Métropolitaine, futur contrat CDD de 2 mois ( < minimum exigé), pas en couple, 1 enfant 9 ans à charge et allocation journalière net = 30.28 euros (> seuil max.)
        DemandeurEmploi demandeurEmploi = new DemandeurEmploi();
        
        FuturTravail futurContratTravail = new FuturTravail();
        futurContratTravail.setTypeContrat(TypesContratTravail.CDD.name());
        futurContratTravail.setNombreMoisContratCDD(2);
        demandeurEmploi.setFuturTravail(futurContratTravail);
        
        InformationsPersonnelles informationsPersonnelles = new InformationsPersonnelles();
        informationsPersonnelles.setCodePostal(CODE_POSTAL_METROPOLITAIN);
        demandeurEmploi.setInformationsPersonnelles(informationsPersonnelles);
        
        BeneficiaireAidesSociales beneficiaireAidesSociales = new BeneficiaireAidesSociales();
        beneficiaireAidesSociales.setBeneficiaireARE(true);
        demandeurEmploi.setBeneficiaireAidesSociales(beneficiaireAidesSociales);
        
        RessourcesFinancieres ressourcesFinancieres = new RessourcesFinancieres();
        AllocationsPoleEmploi allocationsPE = new AllocationsPoleEmploi();
        allocationsPE.setAllocationJournaliereNet(30.28f);
        ressourcesFinancieres.setAllocationsPoleEmploi(allocationsPE);
        demandeurEmploi.setRessourcesFinancieres(ressourcesFinancieres); 
        
        SituationFamiliale situationFamiliale = new SituationFamiliale();
        List<Personne> personnesACharge = new ArrayList<Personne>();
        testUtile.createPersonne(personnesACharge, 9); 
        
        situationFamiliale.setPersonnesACharge(personnesACharge);
        situationFamiliale.setIsEnCouple(false);
        demandeurEmploi.setSituationFamiliale(situationFamiliale);
        
        
        //Lorsque l'on vérifie son éligibilité
        boolean isEligible = agepiUtile.isEligible(PREMIER_MOIS_SIMULATION, demandeurEmploi);
        
        //Alors le DE n'est pas éligible à l'AGEPI
        assertThat(isEligible).isFalse();
    }
    

    /***  dureeTravailEnHeuresSemaine < 15h ****/
    
    @Test
    void calculerMontantAgepiTest1() {
        
        //Si DE France métropolitaine, futur contrat CDI avec 10h/semaine, 1 enfant à charge de 9ans 
        DemandeurEmploi demandeurEmploi = new DemandeurEmploi();
       
        FuturTravail futurContratTravail = new FuturTravail();
        futurContratTravail.setTypeContrat(TypesContratTravail.CDI.name());
        futurContratTravail.setNombreHeuresTravailleesSemaine(10);
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
        
        //alors le montant retourné est de 170 euros
        assertThat(agepi.getMontant()).isEqualTo(170);
    }
    
    
    @Test
    void calculerMontantAgepiTest2() {
        
        //Si DE France métropolitaine, futur contrat CDI avec 10h/semaine, 2 enfants à charge de 9ans et 8 ans
        DemandeurEmploi demandeurEmploi = new DemandeurEmploi();
        
        FuturTravail futurContratTravail = new FuturTravail();
        futurContratTravail.setTypeContrat(TypesContratTravail.CDI.name());
        futurContratTravail.setNombreHeuresTravailleesSemaine(10);
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
        
        //alors le montant retourné est de 195 euros
        assertThat(agepi.getMontant()).isEqualTo(195);
    }
    
    @Test
    void calculerMontantAgepiTest3() {
        
        //Si DE France métropolitaine, futur contrat CDI avec 10h/semaine, 3 enfants à charge de 9ans, 8 ans et 8 ans
        DemandeurEmploi demandeurEmploi = new DemandeurEmploi();
        
        FuturTravail futurContratTravail = new FuturTravail();
        futurContratTravail.setTypeContrat(TypesContratTravail.CDI.name());
        futurContratTravail.setNombreHeuresTravailleesSemaine(10);
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
        
        //alors le montant retourné est de 220 euros
        assertThat(agepi.getMontant()).isEqualTo(220);
    }
    
    @Test
    void calculerMontantAgepiTest4() {
        
        //Si DE France métropolitaine, futur contrat CDI avec 10h/semaine, 4 enfants à charge de 9ans, 8 ans, 8 ans et 5 ans
        DemandeurEmploi demandeurEmploi = new DemandeurEmploi();
        
        FuturTravail futurContratTravail = new FuturTravail();
        futurContratTravail.setTypeContrat(TypesContratTravail.CDI.name());
        futurContratTravail.setNombreHeuresTravailleesSemaine(10);
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
        
        //alors le montant retourné est de 220 euros
        assertThat(agepi.getMontant()).isEqualTo(220);
    }
    
    @Test
    void calculerMontantAgepiTest5() {
        
        //Si DE France métropolitaine, futur contrat CDI avec 10h/semaine, 1 enfant à charge de 15ans
        DemandeurEmploi demandeurEmploi = new DemandeurEmploi();
        
        FuturTravail futurContratTravail = new FuturTravail();
        futurContratTravail.setTypeContrat(TypesContratTravail.CDI.name());
        futurContratTravail.setNombreHeuresTravailleesSemaine(10);
        demandeurEmploi.setFuturTravail(futurContratTravail);
        
        InformationsPersonnelles informationsPersonnelles = new InformationsPersonnelles();
        informationsPersonnelles.setCodePostal(CODE_POSTAL_METROPOLITAIN);
        demandeurEmploi.setInformationsPersonnelles(informationsPersonnelles);
        
        SituationFamiliale situationFamiliale = new SituationFamiliale();
        List<Personne> personnesACharge = new ArrayList<Personne>();
        testUtile.createPersonne(personnesACharge, 15); 
        situationFamiliale.setPersonnesACharge(personnesACharge);
        demandeurEmploi.setSituationFamiliale(situationFamiliale);
        
        //Lorsque l'on calcul le montant de l'agepi
        AideSociale agepi = agepiUtile.calculer(demandeurEmploi);
        
        //alors le montant retourné est de 0 euros
        assertThat(agepi.getMontant()).isEqualTo(0);
    }
    
    /*** dureeTravailEnHeuresSemaine = 15h ****/
    
    @Test
    void calculerMontantAgepiTest6() {
        
        //Si DE France métropolitaine, futur contrat CDI avec 15h/semaine, 1 enfant à charge de 9ans
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
        testUtile.createPersonne(personnesACharge, 9);  
        situationFamiliale.setPersonnesACharge(personnesACharge);
        demandeurEmploi.setSituationFamiliale(situationFamiliale);
        
        //Lorsque l'on calcul le montant de l'agepi
        AideSociale agepi = agepiUtile.calculer(demandeurEmploi);
        
        //alors le montant retourné est de 400 euros
        assertThat(agepi.getMontant()).isEqualTo(400);
    }
    
    
    @Test
    void calculerMontantAgepiTest7() {
        
        //Si DE France métropolitaine, futur contrat CDI avec 15h/semaine, 2 enfants à charge de 9ans et 8ans
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
    void calculerMontantAgepiTest8() {
        
        //Si DE France métropolitaine, futur contrat CDI avec 15h/semaine, 3 enfants à charge de 9ans, 8ans et 8ans
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
    void calculerMontantAgepiTest9() {
        
        //Si DE France métropolitaine, futur contrat CDI avec 15h/semaine, 4 enfants à charge de 9ans, 8ans, 8ans et 5ans
        DemandeurEmploi demandeurEmploi = new DemandeurEmploi();
        
        FuturTravail futurContratTravail = new FuturTravail();
        futurContratTravail.setTypeContrat(TypesContratTravail.CDI.name());
        futurContratTravail.setNombreHeuresTravailleesSemaine(15);
        demandeurEmploi.setFuturTravail(futurContratTravail);
        
        InformationsPersonnelles informationsPersonnelles = new InformationsPersonnelles();
        informationsPersonnelles.setCodePostal(CODE_POSTAL_METROPOLITAIN);
        demandeurEmploi.setInformationsPersonnelles(informationsPersonnelles);
        
        //Si nombre enfant = 4
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
}
