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

import fr.poleemploi.estime.commun.enumerations.TypesContratTravail;
import fr.poleemploi.estime.logique.simulateuraidessociales.poleemploi.aides.AideMobilite;
import fr.poleemploi.estime.services.ressources.AideSociale;
import fr.poleemploi.estime.services.ressources.AllocationsPoleEmploi;
import fr.poleemploi.estime.services.ressources.BeneficiaireAidesSociales;
import fr.poleemploi.estime.services.ressources.DemandeurEmploi;
import fr.poleemploi.estime.services.ressources.FuturTravail;
import fr.poleemploi.estime.services.ressources.InformationsPersonnelles;
import fr.poleemploi.estime.services.ressources.RessourcesFinancieres;
import utiletests.TestUtile;


@ContextConfiguration
@SpringBootTest
@TestPropertySource(locations="classpath:application-test.properties")
class AideMobiliteTestsHorsMayotte {

    public static final String CODE_POSTAL_METROPOLITAIN = "72300";
    public static final int PREMIER_MOIS_SIMULATION = 1;
    
    @Autowired
    private AideMobilite aideMobiliteUtile;
    
    @Autowired
    TestUtile testUtile;
    
    @Configuration
    @ComponentScan({"utiletests","fr.poleemploi.estime"})
    public static class SpringConfig {

    }
      
    /**************************************  département hors Mayotte  *****************************************************/
    
    @Test
    void isEligibleTest1() {
        
        //Si DE France Métropolitaine, futur contrat CDI, kilométrage domicile -> taf = 31kms  et allocation journalière net = 29.38 euros
        DemandeurEmploi demandeurEmploi = new DemandeurEmploi();
        
        FuturTravail futurContratTravail = new FuturTravail();
        futurContratTravail.setTypeContrat(TypesContratTravail.CDI.name());
        futurContratTravail.setDistanceKmDomicileTravail(31);
        demandeurEmploi.setFuturTravail(futurContratTravail);
        
        InformationsPersonnelles informationsPersonnelles = new InformationsPersonnelles();
        informationsPersonnelles.setCodePostal(CODE_POSTAL_METROPOLITAIN);
        demandeurEmploi.setInformationsPersonnelles(informationsPersonnelles);
        
        BeneficiaireAidesSociales beneficiaireAidesSociales = new BeneficiaireAidesSociales();
        beneficiaireAidesSociales.setBeneficiaireASS(false);
        beneficiaireAidesSociales.setBeneficiaireAAH(false);
        beneficiaireAidesSociales.setBeneficiaireARE(true);
        beneficiaireAidesSociales.setBeneficiaireRSA(false);
        demandeurEmploi.setBeneficiaireAidesSociales(beneficiaireAidesSociales);
        
        RessourcesFinancieres ressourcesFinancieres = new RessourcesFinancieres();
        AllocationsPoleEmploi allocationsPE = new AllocationsPoleEmploi();
        allocationsPE.setAllocationJournaliereNet(29.38f);
        ressourcesFinancieres.setAllocationsPoleEmploi(allocationsPE);
        demandeurEmploi.setRessourcesFinancieres(ressourcesFinancieres);
        
        //Lorsque l'on vérifie son éligibilité
        boolean isEligible = aideMobiliteUtile.isEligible(PREMIER_MOIS_SIMULATION, demandeurEmploi);
        
        //Alors le DE est éligible à l'aideMobiliteUtile
        assertThat(isEligible).isTrue();
    }
    
    @Test
    void isEligibleTest2() {
        
        //Si DE France Métropolitaine, futur contrat CDI, kilométrage domicile -> taf = 31kms et allocation journalière net = 14.38 euros
        DemandeurEmploi demandeurEmploi = new DemandeurEmploi();
        
        FuturTravail futurContratTravail = new FuturTravail();
        futurContratTravail.setTypeContrat(TypesContratTravail.CDI.name());
        futurContratTravail.setDistanceKmDomicileTravail(31);
        demandeurEmploi.setFuturTravail(futurContratTravail);
        
        InformationsPersonnelles informationsPersonnelles = new InformationsPersonnelles();
        informationsPersonnelles.setCodePostal(CODE_POSTAL_METROPOLITAIN);
        demandeurEmploi.setInformationsPersonnelles(informationsPersonnelles);
        
        BeneficiaireAidesSociales beneficiaireAidesSociales = new BeneficiaireAidesSociales();
        beneficiaireAidesSociales.setBeneficiaireASS(false);
        beneficiaireAidesSociales.setBeneficiaireAAH(false);
        beneficiaireAidesSociales.setBeneficiaireARE(true);
        beneficiaireAidesSociales.setBeneficiaireRSA(false);
        demandeurEmploi.setBeneficiaireAidesSociales(beneficiaireAidesSociales);
        
        RessourcesFinancieres ressourcesFinancieres = new RessourcesFinancieres();
        AllocationsPoleEmploi allocationsPE = new AllocationsPoleEmploi();
        allocationsPE.setAllocationJournaliereNet(14.38f);
        ressourcesFinancieres.setAllocationsPoleEmploi(allocationsPE);
        demandeurEmploi.setRessourcesFinancieres(ressourcesFinancieres);
        
        //Lorsque l'on vérifie son éligibilité
        boolean isEligible = aideMobiliteUtile.isEligible(PREMIER_MOIS_SIMULATION, demandeurEmploi);
        
        //Alors le DE est éligible à l'aideMobiliteUtile
        assertThat(isEligible).isTrue();
    }
    
    @Test
    void isEligibleTest3() throws ParseException {
        
        //Si DE France Métropolitaine, futur contrat CDD de 3 mois, kilométrage domicile -> taf = 31kms et allocation journalière net = 29.38 euros
        DemandeurEmploi demandeurEmploi = new DemandeurEmploi();
        
        FuturTravail futurContratTravail = new FuturTravail();
        futurContratTravail.setDistanceKmDomicileTravail(31);
        futurContratTravail.setTypeContrat(TypesContratTravail.CDD.name());
        futurContratTravail.setNombreMoisContratCDD(3);
        demandeurEmploi.setFuturTravail(futurContratTravail);
        
        InformationsPersonnelles informationsPersonnelles = new InformationsPersonnelles();
        informationsPersonnelles.setCodePostal(CODE_POSTAL_METROPOLITAIN);
        demandeurEmploi.setInformationsPersonnelles(informationsPersonnelles);
        
        BeneficiaireAidesSociales beneficiaireAidesSociales = new BeneficiaireAidesSociales();
        beneficiaireAidesSociales.setBeneficiaireASS(false);
        beneficiaireAidesSociales.setBeneficiaireAAH(false);
        beneficiaireAidesSociales.setBeneficiaireARE(true);
        beneficiaireAidesSociales.setBeneficiaireRSA(false);
        demandeurEmploi.setBeneficiaireAidesSociales(beneficiaireAidesSociales);
        
        RessourcesFinancieres ressourcesFinancieres = new RessourcesFinancieres();
        AllocationsPoleEmploi allocationsPE = new AllocationsPoleEmploi();
        allocationsPE.setAllocationJournaliereNet(29.38f);
        ressourcesFinancieres.setAllocationsPoleEmploi(allocationsPE);
        demandeurEmploi.setRessourcesFinancieres(ressourcesFinancieres);

        //Lorsque l'on vérifie son éligibilité
        boolean isEligible = aideMobiliteUtile.isEligible(PREMIER_MOIS_SIMULATION, demandeurEmploi);
        
        //Alors le DE est éligible à l'aideMobiliteUtile
        assertThat(isEligible).isTrue();
    }
    
    @Test
    void isEligibleTest4() throws ParseException {
        
        //Si DE France Métropolitaine, futur contrat CDD de 4 mois, kilométrage domicile -> taf = 31kms et allocation journalière net = 29.38 euros
        DemandeurEmploi demandeurEmploi = new DemandeurEmploi();
        
        FuturTravail futurContratTravail = new FuturTravail();
        futurContratTravail.setDistanceKmDomicileTravail(31);
        futurContratTravail.setTypeContrat(TypesContratTravail.CDD.name());
        futurContratTravail.setNombreMoisContratCDD(4);
        demandeurEmploi.setFuturTravail(futurContratTravail);
        
        InformationsPersonnelles informationsPersonnelles = new InformationsPersonnelles();
        informationsPersonnelles.setCodePostal(CODE_POSTAL_METROPOLITAIN);
        demandeurEmploi.setInformationsPersonnelles(informationsPersonnelles);
        
        BeneficiaireAidesSociales beneficiaireAidesSociales = new BeneficiaireAidesSociales();
        beneficiaireAidesSociales.setBeneficiaireASS(false);
        beneficiaireAidesSociales.setBeneficiaireAAH(false);
        beneficiaireAidesSociales.setBeneficiaireARE(true);
        beneficiaireAidesSociales.setBeneficiaireRSA(false);
        demandeurEmploi.setBeneficiaireAidesSociales(beneficiaireAidesSociales);
        
        RessourcesFinancieres ressourcesFinancieres = new RessourcesFinancieres();
        AllocationsPoleEmploi allocationsPE = new AllocationsPoleEmploi();
        allocationsPE.setAllocationJournaliereNet(29.38f);
        ressourcesFinancieres.setAllocationsPoleEmploi(allocationsPE);
        demandeurEmploi.setRessourcesFinancieres(ressourcesFinancieres);
        
        //Lorsque l'on vérifie son éligibilité
        boolean isEligible = aideMobiliteUtile.isEligible(PREMIER_MOIS_SIMULATION, demandeurEmploi);
        
        //Alors le DE est éligible à l'aideMobiliteUtile
        assertThat(isEligible).isTrue();
    }
    
    @Test
    void isEligibleTest5() {
        
        //Si DE France Métropolitaine, futur contrat CDI, kilométrage domicile -> taf = 31kms et sans ressources
        DemandeurEmploi demandeurEmploi = new DemandeurEmploi();
        
        FuturTravail futurContratTravail = new FuturTravail();
        futurContratTravail.setTypeContrat(TypesContratTravail.CDI.name());
        futurContratTravail.setDistanceKmDomicileTravail(31);
        demandeurEmploi.setFuturTravail(futurContratTravail);
        
        InformationsPersonnelles informationsPersonnelles = new InformationsPersonnelles();
        informationsPersonnelles.setCodePostal(CODE_POSTAL_METROPOLITAIN);
        demandeurEmploi.setInformationsPersonnelles(informationsPersonnelles);
        
        BeneficiaireAidesSociales beneficiaireAidesSociales = new BeneficiaireAidesSociales();
        beneficiaireAidesSociales.setBeneficiaireASS(false);
        beneficiaireAidesSociales.setBeneficiaireAAH(false);
        beneficiaireAidesSociales.setBeneficiaireARE(false);
        beneficiaireAidesSociales.setBeneficiaireRSA(false);
        demandeurEmploi.setBeneficiaireAidesSociales(beneficiaireAidesSociales);
        
        //Lorsque l'on vérifie son éligibilité
        boolean isEligible = aideMobiliteUtile.isEligible(PREMIER_MOIS_SIMULATION, demandeurEmploi);
        
        //Alors le DE est éligible à l'aideMobiliteUtile
        assertThat(isEligible).isTrue();
    }
    
    @Test
    void isEligibleTest6() {
        
        //Si DE France Métropolitaine, futur contrat CDI, kilométrage domicile -> taf = 31kms et bénéficiaire RSA
        DemandeurEmploi demandeurEmploi = new DemandeurEmploi();
        
        FuturTravail futurContratTravail = new FuturTravail();
        futurContratTravail.setTypeContrat(TypesContratTravail.CDI.name());
        futurContratTravail.setDistanceKmDomicileTravail(31);
        demandeurEmploi.setFuturTravail(futurContratTravail);
        
        InformationsPersonnelles informationsPersonnelles = new InformationsPersonnelles();
        informationsPersonnelles.setCodePostal(CODE_POSTAL_METROPOLITAIN);
        demandeurEmploi.setInformationsPersonnelles(informationsPersonnelles);
        
        BeneficiaireAidesSociales beneficiaireAidesSociales = new BeneficiaireAidesSociales();
        beneficiaireAidesSociales.setBeneficiaireASS(false);
        beneficiaireAidesSociales.setBeneficiaireAAH(false);
        beneficiaireAidesSociales.setBeneficiaireARE(false);
        beneficiaireAidesSociales.setBeneficiaireRSA(true);
        demandeurEmploi.setBeneficiaireAidesSociales(beneficiaireAidesSociales);
        
        //Lorsque l'on vérifie son éligibilité
        boolean isEligible = aideMobiliteUtile.isEligible(PREMIER_MOIS_SIMULATION, demandeurEmploi);
        
        //Alors le DE est éligible à l'aideMobiliteUtile
        assertThat(isEligible).isTrue();
    }
    
    @Test
    void isEligibleTest7() {
        
        //Si DE France Métropolitaine, futur contrat CDI, kilométrage domicile -> taf = 31kms et bénéficiaire AAH
        DemandeurEmploi demandeurEmploi = new DemandeurEmploi();
        
        FuturTravail futurContratTravail = new FuturTravail();
        futurContratTravail.setTypeContrat(TypesContratTravail.CDI.name());
        futurContratTravail.setDistanceKmDomicileTravail(31);
        demandeurEmploi.setFuturTravail(futurContratTravail);
        
        InformationsPersonnelles informationsPersonnelles = new InformationsPersonnelles();
        informationsPersonnelles.setCodePostal(CODE_POSTAL_METROPOLITAIN);
        demandeurEmploi.setInformationsPersonnelles(informationsPersonnelles);
        
        BeneficiaireAidesSociales beneficiaireAidesSociales = new BeneficiaireAidesSociales();
        beneficiaireAidesSociales.setBeneficiaireASS(false);
        beneficiaireAidesSociales.setBeneficiaireAAH(true);
        beneficiaireAidesSociales.setBeneficiaireARE(false);
        beneficiaireAidesSociales.setBeneficiaireRSA(false);
        demandeurEmploi.setBeneficiaireAidesSociales(beneficiaireAidesSociales);
        
        //Lorsque l'on vérifie son éligibilité
        boolean isEligible = aideMobiliteUtile.isEligible(PREMIER_MOIS_SIMULATION, demandeurEmploi);
        
        //Alors le DE est éligible à l'aideMobiliteUtile
        assertThat(isEligible).isTrue();
    }
    
    @Test
    void isEligibleTest8() {
        
        //Si DE France Métropolitaine, futur contrat CDI, kilométrage domicile -> taf = 31kms et bénéficiaire ASS
        DemandeurEmploi demandeurEmploi = new DemandeurEmploi();
        
        FuturTravail futurContratTravail = new FuturTravail();
        futurContratTravail.setTypeContrat(TypesContratTravail.CDI.name());
        futurContratTravail.setDistanceKmDomicileTravail(31);
        demandeurEmploi.setFuturTravail(futurContratTravail);
        
        InformationsPersonnelles informationsPersonnelles = new InformationsPersonnelles();
        informationsPersonnelles.setCodePostal(CODE_POSTAL_METROPOLITAIN);
        demandeurEmploi.setInformationsPersonnelles(informationsPersonnelles);
        
        BeneficiaireAidesSociales beneficiaireAidesSociales = new BeneficiaireAidesSociales();
        beneficiaireAidesSociales.setBeneficiaireASS(true);
        beneficiaireAidesSociales.setBeneficiaireAAH(false);
        beneficiaireAidesSociales.setBeneficiaireARE(false);
        beneficiaireAidesSociales.setBeneficiaireRSA(false);
        demandeurEmploi.setBeneficiaireAidesSociales(beneficiaireAidesSociales);
        
        //Lorsque l'on vérifie son éligibilité
        boolean isEligible = aideMobiliteUtile.isEligible(PREMIER_MOIS_SIMULATION, demandeurEmploi);
        
        //Alors le DE est éligible à l'aideMobiliteUtile
        assertThat(isEligible).isTrue();
    }
    
    @Test
    void isNotEligibleTest1() {
        
        //Si DE France Métropolitaine, futur contrat CDI, kilométrage domicile -> taf = 30kms et allocation journalière net = 14.38 euros
        DemandeurEmploi demandeurEmploi = new DemandeurEmploi();
        
        FuturTravail futurContratTravail = new FuturTravail();
        futurContratTravail.setDistanceKmDomicileTravail(30);
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
        
        //Lorsque l'on vérifie son éligibilité
        boolean isEligible = aideMobiliteUtile.isEligible(PREMIER_MOIS_SIMULATION, demandeurEmploi);
        
        //Alors le DE n'est pas éligible à l'aideMobiliteUtile
        assertThat(isEligible).isFalse();
    }
    
    @Test
    void isNotEligibleTest2() {
        
        //Si DE France Métropolitaine, futur contrat CDI, kilométrage domicile -> taf = 25kms et allocation journalière net = 14.38 euros
        DemandeurEmploi demandeurEmploi = new DemandeurEmploi();
        
        FuturTravail futurContratTravail = new FuturTravail();
        futurContratTravail.setDistanceKmDomicileTravail(25);
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
        
        //Lorsque l'on vérifie son éligibilité
        boolean isEligible = aideMobiliteUtile.isEligible(PREMIER_MOIS_SIMULATION, demandeurEmploi);
        
        //Alors le DE n'est pas éligible à l'aideMobiliteUtile
        assertThat(isEligible).isFalse();
    }
    
    @Test
    void isNotEligibleTest3() {
        
        //Si DE France Métropolitaine, futur contrat CDI, kilométrage domicile -> taf = 31kms et allocation journalière net = 30.28 euros 
        DemandeurEmploi demandeurEmploi = new DemandeurEmploi();
        
        FuturTravail futurContratTravail = new FuturTravail();
        futurContratTravail.setDistanceKmDomicileTravail(30);
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
        
        //Lorsque l'on vérifie son éligibilité
        boolean isEligible = aideMobiliteUtile.isEligible(PREMIER_MOIS_SIMULATION, demandeurEmploi);
        
        //Alors le DE n'est pas éligible à l'aideMobiliteUtile
        assertThat(isEligible).isFalse();
    }
    
    @Test
    void isNotEligibleTest4() throws ParseException {
        
        //Si DE France Métropolitaine, futur contrat CDD de 2 mois, kilométrage domicile -> taf = 31kms et allocation journalière net = 14.28 euros
        DemandeurEmploi demandeurEmploi = new DemandeurEmploi();
        
        FuturTravail futurContratTravail = new FuturTravail();
        futurContratTravail.setDistanceKmDomicileTravail(31);
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
        allocationsPE.setAllocationJournaliereNet(14.28f);
        ressourcesFinancieres.setAllocationsPoleEmploi(allocationsPE);
        demandeurEmploi.setRessourcesFinancieres(ressourcesFinancieres);
         
        //Lorsque l'on vérifie son éligibilité
        boolean isEligible = aideMobiliteUtile.isEligible(PREMIER_MOIS_SIMULATION, demandeurEmploi);
        
        //Alors le DE n'est pas éligible à l'aideMobiliteUtile
        assertThat(isEligible).isFalse();
    }
    
/******************************************* tests calculerMontantAideMobiliteTest1 *********************************************************************************************/
    
    @Test
    void calculerMontantAideMobiliteTest1() {
        
        //Si kilométrage domicile -> taf = 32kms + 20 trajets
        //et nbr heures hebdo travaillées = 10h
        DemandeurEmploi demandeurEmploi = new DemandeurEmploi();
        
        FuturTravail futurTravail = new FuturTravail();
        futurTravail.setTypeContrat(TypesContratTravail.CDI.name());
        futurTravail.setNombreHeuresTravailleesSemaine(10);
        futurTravail.setDistanceKmDomicileTravail(32);
        futurTravail.setNombreTrajetsDomicileTravail(20);
        demandeurEmploi.setFuturTravail(futurTravail);
        
        InformationsPersonnelles informationsPersonnelles = new InformationsPersonnelles();
        informationsPersonnelles.setCodePostal(CODE_POSTAL_METROPOLITAIN);
        demandeurEmploi.setInformationsPersonnelles(informationsPersonnelles);
        
        BeneficiaireAidesSociales beneficiaireAidesSociales = new BeneficiaireAidesSociales();
        beneficiaireAidesSociales.setBeneficiaireARE(true);
        demandeurEmploi.setBeneficiaireAidesSociales(beneficiaireAidesSociales);
        
        RessourcesFinancieres ressourcesFinancieres = new RessourcesFinancieres();
        AllocationsPoleEmploi allocationsPE = new AllocationsPoleEmploi();
        allocationsPE.setAllocationJournaliereNet(34.38f);
        ressourcesFinancieres.setAllocationsPoleEmploi(allocationsPE);
        demandeurEmploi.setRessourcesFinancieres(ressourcesFinancieres);
        
        //Lorsque l'on calcul le montant de l'aide à la mobilité
        AideSociale aideMobilite = aideMobiliteUtile.calculer(demandeurEmploi);
        
        //alors le montant retourné est de 286 euros
        assertThat(aideMobilite.getMontant()).isEqualTo(286f);
    }
    
    @Test
    void calculerMontantAideMobiliteTest2() {
        
        //Si kilométrage domicile -> taf = 0kms + 0 trajet
        //et nbr heures hebdo travaillées = 10h
        DemandeurEmploi demandeurEmploi = new DemandeurEmploi();
        
        FuturTravail futurTravail = new FuturTravail();
        futurTravail.setTypeContrat(TypesContratTravail.CDI.name());
        futurTravail.setNombreHeuresTravailleesSemaine(10);
        demandeurEmploi.setFuturTravail(futurTravail);
        
        InformationsPersonnelles informationsPersonnelles = new InformationsPersonnelles();
        informationsPersonnelles.setCodePostal(CODE_POSTAL_METROPOLITAIN);
        demandeurEmploi.setInformationsPersonnelles(informationsPersonnelles);
        
        BeneficiaireAidesSociales beneficiaireAidesSociales = new BeneficiaireAidesSociales();
        beneficiaireAidesSociales.setBeneficiaireARE(true);
        demandeurEmploi.setBeneficiaireAidesSociales(beneficiaireAidesSociales);
        
        RessourcesFinancieres ressourcesFinancieres = new RessourcesFinancieres();
        AllocationsPoleEmploi allocationsPE = new AllocationsPoleEmploi();
        allocationsPE.setAllocationJournaliereNet(34.38f);
        ressourcesFinancieres.setAllocationsPoleEmploi(allocationsPE);
        demandeurEmploi.setRessourcesFinancieres(ressourcesFinancieres);
        
        //Lorsque l'on calcul le montant de l'aide à la mobilité
        AideSociale aideMobilite = aideMobiliteUtile.calculer(demandeurEmploi);
        
        //alors le montant retourné est de 30 euros
        assertThat(aideMobilite.getMontant()).isEqualTo(30f);
    }
}
