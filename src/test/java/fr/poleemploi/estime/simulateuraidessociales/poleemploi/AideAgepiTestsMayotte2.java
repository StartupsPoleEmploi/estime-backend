package fr.poleemploi.estime.simulateuraidessociales.poleemploi;

import static org.assertj.core.api.Assertions.assertThat;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

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
import fr.poleemploi.test.utile.TestUtile;

@ExtendWith(SpringExtension.class)
@ContextConfiguration
@SpringBootTest
@AutoConfigureTestDatabase
class AideAgepiTestsMayotte2 {
    
    public static final String CODE_POSTAL_MAYOTTE = "97600";
    public static final String CODE_POSTAL_METROPOLITAIN = "72300";
    public static final int PREMIER_MOIS_SIMULATION = 1;
    
    @Autowired
    private Agepi agepiUtile;
    
    @Autowired
    TestUtile testUtile;
    
    @Configuration
    @ComponentScan({"fr.poleemploi.test","fr.poleemploi.estime"})
    public static class SpringConfig {

    }
    
    /**************************************  département Mayotte *****************************************************/
    
    @Test
    void isNotEligibleMayotteTest1() {
        
        //Si DE Mayotte, futur contrat CDI, pas en couple, 1 enfant 10 ans à charge et allocation journalière net = 14.38 euros (< seuil max.)
        DemandeurEmploi demandeurEmploi = new DemandeurEmploi();
        
        FuturTravail futurContratTravail = new FuturTravail();
        futurContratTravail.setTypeContrat(TypesContratTravail.CDI.name());
        demandeurEmploi.setFuturTravail(futurContratTravail);
        
        InformationsPersonnelles informationsPersonnelles = new InformationsPersonnelles();
        informationsPersonnelles.setCodePostal(CODE_POSTAL_MAYOTTE);
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
        
        //Alors le DE n'est pas éligible à l'AGEPI
        assertThat(isEligible).isFalse();
    }
    
    @Test
    void isNotEligibleMayotteTest2() {
        
        //Si DE Mayotte, futur contrat CDI, en couple, 1 enfant 9 ans à charge et allocation journalière net = 14.68 euros (< seuil max.) 
        DemandeurEmploi demandeurEmploi = new DemandeurEmploi();
        
        FuturTravail futurContratTravail = new FuturTravail();
        futurContratTravail.setTypeContrat(TypesContratTravail.CDI.name());
        demandeurEmploi.setFuturTravail(futurContratTravail);
        
        InformationsPersonnelles informationsPersonnelles = new InformationsPersonnelles();
        informationsPersonnelles.setCodePostal(CODE_POSTAL_MAYOTTE);
        demandeurEmploi.setInformationsPersonnelles(informationsPersonnelles);
        
        BeneficiaireAidesSociales beneficiaireAidesSociales = new BeneficiaireAidesSociales();
        beneficiaireAidesSociales.setBeneficiaireARE(true);
        demandeurEmploi.setBeneficiaireAidesSociales(beneficiaireAidesSociales);
        
        RessourcesFinancieres ressourcesFinancieres = new RessourcesFinancieres();
        AllocationsPoleEmploi allocationsPE = new AllocationsPoleEmploi();
        allocationsPE.setAllocationJournaliereNet(14.68f);
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
    void isNotEligibleMayotteTest3() {
        
        //Si DE Mayotte, futur contrat CDI, pas en couple, 1 enfant 9 ans à charge et allocation journalière net = 30.28 euros (> seuil max.)
        DemandeurEmploi demandeurEmploi = new DemandeurEmploi();
        
        FuturTravail futurContratTravail = new FuturTravail();
        futurContratTravail.setTypeContrat(TypesContratTravail.CDI.name());
        demandeurEmploi.setFuturTravail(futurContratTravail);
        
        InformationsPersonnelles informationsPersonnelles = new InformationsPersonnelles();
        informationsPersonnelles.setCodePostal(CODE_POSTAL_MAYOTTE);
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
    void isNotEligibleMayotteTest4() throws ParseException {
        
        //Si DE Mayotte, futur contrat CDD de 2 mois ( < minimum exigé), pas en couple, 1 enfant 9 ans à charge et allocation journalière net = 30.28 euros (> seuil max.)
        DemandeurEmploi demandeurEmploi = new DemandeurEmploi();
        
        FuturTravail futurContratTravail = new FuturTravail();
        futurContratTravail.setTypeContrat(TypesContratTravail.CDD.name());
        futurContratTravail.setNombreMoisContratCDD(2);
        demandeurEmploi.setFuturTravail(futurContratTravail);
        
        InformationsPersonnelles informationsPersonnelles = new InformationsPersonnelles();
        informationsPersonnelles.setCodePostal(CODE_POSTAL_MAYOTTE);
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
    void calculerMontantAgepiMayotteTest1() {
        
        //Si DE Mayotte, futur contrat CDI avec 10h/semaine, 1 enfant à charge de 9ans 
        DemandeurEmploi demandeurEmploi = new DemandeurEmploi();
       
        FuturTravail futurContratTravail = new FuturTravail();
        futurContratTravail.setTypeContrat(TypesContratTravail.CDI.name());
        futurContratTravail.setNombreHeuresTravailleesSemaine(10);
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
        
        //alors le montant retourné est de 85 euros
        assertThat(agepi.getMontant()).isEqualTo(85);
    }
    
    
    @Test
    void calculerMontantAgepiMayotteTest2() {
        
        //Si DE Mayotte, futur contrat CDI avec 10h/semaine, 2 enfants à charge de 9ans et 8 ans
        DemandeurEmploi demandeurEmploi = new DemandeurEmploi();
        
        FuturTravail futurContratTravail = new FuturTravail();
        futurContratTravail.setTypeContrat(TypesContratTravail.CDI.name());
        futurContratTravail.setNombreHeuresTravailleesSemaine(10);
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
        
        //alors le montant retourné est de 98 euros
        assertThat(agepi.getMontant()).isEqualTo(98);
    }
    
    @Test
    void calculerMontantAgepiMayotteTest3() {
        
        //Si DE Mayotte, futur contrat CDI avec 10h/semaine, 3 enfants à charge de 9ans, 8 ans et 8 ans
        DemandeurEmploi demandeurEmploi = new DemandeurEmploi();
        
        FuturTravail futurContratTravail = new FuturTravail();
        futurContratTravail.setTypeContrat(TypesContratTravail.CDI.name());
        futurContratTravail.setNombreHeuresTravailleesSemaine(10);
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
        
        //alors le montant retourné est de 110 euros
        assertThat(agepi.getMontant()).isEqualTo(110);
    }
    
    @Test
    void calculerMontantAgepiMayotteTest4() {
        
        //Si DE Mayotte, futur contrat CDI avec 10h/semaine, 4 enfants à charge de 9ans, 8 ans, 8 ans et 5 ans
        DemandeurEmploi demandeurEmploi = new DemandeurEmploi();
        
        FuturTravail futurContratTravail = new FuturTravail();
        futurContratTravail.setTypeContrat(TypesContratTravail.CDI.name());
        futurContratTravail.setNombreHeuresTravailleesSemaine(10);
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
        
        //alors le montant retourné est de 110 euros
        assertThat(agepi.getMontant()).isEqualTo(110);
    }
    
    @Test
    void calculerMontantAgepiMayotteTest5() {
        
        //Si DE Mayotte, futur contrat CDI avec 10h/semaine, 1 enfant à charge de 15ans
        DemandeurEmploi demandeurEmploi = new DemandeurEmploi();
        
        FuturTravail futurContratTravail = new FuturTravail();
        futurContratTravail.setTypeContrat(TypesContratTravail.CDI.name());
        futurContratTravail.setNombreHeuresTravailleesSemaine(10);
        demandeurEmploi.setFuturTravail(futurContratTravail);
        
        InformationsPersonnelles informationsPersonnelles = new InformationsPersonnelles();
        informationsPersonnelles.setCodePostal(CODE_POSTAL_MAYOTTE);
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
    void calculerMontantAgepiMayotteTest6() {
        
        //Si DE Mayotte, futur contrat CDI avec 15h/semaine, 1 enfant à charge de 9ans
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
        testUtile.createPersonne(personnesACharge, 9);  
        situationFamiliale.setPersonnesACharge(personnesACharge);
        demandeurEmploi.setSituationFamiliale(situationFamiliale);
        
        //Lorsque l'on calcul le montant de l'agepi
        AideSociale agepi = agepiUtile.calculer(demandeurEmploi);
        
        //alors le montant retourné est de 200 euros
        assertThat(agepi.getMontant()).isEqualTo(200);
    }
    
    
    @Test
    void calculerMontantAgepiMayotteTest7() {
        
        //Si DE Mayotte, futur contrat CDI avec 15h/semaine, 2 enfants à charge de 9ans et 8ans
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
    void calculerMontantAgepiMayotteTest8() {
        
        //Si DE Mayotte, futur contrat CDI avec 15h/semaine, 3 enfants à charge de 9ans, 8ans et 8ans
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
    void calculerMontantAgepiMayotteTest9() {
        
        //Si DE Mayotte, futur contrat CDI avec 15h/semaine, 4 enfants à charge de 9ans, 8ans, 8ans et 5ans
        DemandeurEmploi demandeurEmploi = new DemandeurEmploi();
        
        FuturTravail futurContratTravail = new FuturTravail();
        futurContratTravail.setTypeContrat(TypesContratTravail.CDI.name());
        futurContratTravail.setNombreHeuresTravailleesSemaine(15);
        demandeurEmploi.setFuturTravail(futurContratTravail);
        
        InformationsPersonnelles informationsPersonnelles = new InformationsPersonnelles();
        informationsPersonnelles.setCodePostal(CODE_POSTAL_MAYOTTE);
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
        
        //alors le montant retourné est de 260 euros
        assertThat(agepi.getMontant()).isEqualTo(260);
    }
}
