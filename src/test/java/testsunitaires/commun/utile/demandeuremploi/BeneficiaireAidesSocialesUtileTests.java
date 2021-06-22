package testsunitaires.commun.utile.demandeuremploi;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import fr.poleemploi.estime.commun.utile.demandeuremploi.BeneficiaireAidesSocialesUtile;
import fr.poleemploi.estime.services.ressources.AllocationsPoleEmploi;
import fr.poleemploi.estime.services.ressources.BeneficiaireAidesSociales;
import fr.poleemploi.estime.services.ressources.DemandeurEmploi;
import fr.poleemploi.estime.services.ressources.InformationsPersonnelles;
import fr.poleemploi.estime.services.ressources.RessourcesFinancieres;


@SpringBootTest
@ContextConfiguration
@TestPropertySource(locations="classpath:application-test.properties")
class BeneficiaireAidesSocialesUtileTests {
    
    public static final String CODE_POSTAL_MAYOTTE = "97600";
    public static final String CODE_POSTAL_METROPOLITAIN = "72300";
    
    @Autowired
    private BeneficiaireAidesSocialesUtile beneficiaireAidesSocialesUtile;
    
    @Configuration
    @ComponentScan({"utiletests","fr.poleemploi.estime"})
    public static class SpringConfig {

    }
    
    @Test
    void isBeneficiaireAREAvecMontantAREInferieurEgaleSeuilMaxEligibiliteTest1() {
        
        //si DE France Metropolitaine et montant journalier = 29,38 euros (= seuil max.)
        DemandeurEmploi demandeurEmploi =  new DemandeurEmploi();
        
        InformationsPersonnelles informationsPersonnelles = new InformationsPersonnelles();
        informationsPersonnelles.setCodePostal(CODE_POSTAL_METROPOLITAIN);
        demandeurEmploi.setInformationsPersonnelles(informationsPersonnelles);
        
        BeneficiaireAidesSociales beneficiaireAidesSociales = new BeneficiaireAidesSociales();
        beneficiaireAidesSociales.setBeneficiaireARE(true);
        demandeurEmploi.setBeneficiaireAidesSociales(beneficiaireAidesSociales);
        
        RessourcesFinancieres ressourcesFinancieres = new RessourcesFinancieres();
        AllocationsPoleEmploi allocationsPE = new AllocationsPoleEmploi();
        allocationsPE.setAllocationJournaliereNet(29.38f);
        ressourcesFinancieres.setAllocationsPoleEmploi(allocationsPE);
        demandeurEmploi.setRessourcesFinancieres(ressourcesFinancieres);
        
        //lorsque l'on appelle isBeneficiaireAidePEouCAF
        boolean beneficiaire = beneficiaireAidesSocialesUtile.isBeneficiaireAidePEouCAF(demandeurEmploi);
        
        //alors le résultat est DE beneficiaire
        assertThat(beneficiaire).isTrue();
    }
    
    @Test
    void isBeneficiaireAREAvecMontantAREInferieurEgaleSeuilMaxEligibiliteTest2() {
        
        //si DE France Metropolitaine et montant journalier = 18,38 euros (< seuil max.)
        DemandeurEmploi demandeurEmploi =  new DemandeurEmploi();
       
        InformationsPersonnelles informationsPersonnelles = new InformationsPersonnelles();
        informationsPersonnelles.setCodePostal(CODE_POSTAL_METROPOLITAIN);
        demandeurEmploi.setInformationsPersonnelles(informationsPersonnelles);
        
        BeneficiaireAidesSociales beneficiaireAidesSociales = new BeneficiaireAidesSociales();
        beneficiaireAidesSociales.setBeneficiaireARE(true);
        demandeurEmploi.setBeneficiaireAidesSociales(beneficiaireAidesSociales);
        
        RessourcesFinancieres ressourcesFinancieres = new RessourcesFinancieres();
        AllocationsPoleEmploi allocationsPE = new AllocationsPoleEmploi();
        allocationsPE.setAllocationJournaliereNet(18.38f);
        ressourcesFinancieres.setAllocationsPoleEmploi(allocationsPE);
        demandeurEmploi.setRessourcesFinancieres(ressourcesFinancieres);
        
        //lorsque l'on appelle isBeneficiaireAidePEouCAF
        boolean beneficiaire = beneficiaireAidesSocialesUtile.isBeneficiaireAidePEouCAF(demandeurEmploi);
        
        //alors le résultat est DE beneficiaire
        assertThat(beneficiaire).isTrue();
    }
    
    @Test
    void isBeneficiaireAREAvecMontantAREInferieurEgaleSeuilMaxEligibiliteTest3() {
        
        //si DE Mayotte et montant journalier = 14.68 euros (= seuil max.)
        DemandeurEmploi demandeurEmploi =  new DemandeurEmploi();
       
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
        
        //lorsque l'on appelle isBeneficiaireAidePEouCAF
        boolean beneficiaire = beneficiaireAidesSocialesUtile.isBeneficiaireAidePEouCAF(demandeurEmploi);
        
        //alors le résultat est DE beneficiaire
        assertThat(beneficiaire).isTrue();
    }
    
    @Test
    void isBeneficiaireAREAvecMontantAREInferieurEgaleSeuilMaxEligibiliteTest4() {
        
        //si DE Mayotte et montant journalier = 12.68 euros (< seuil max.)
        DemandeurEmploi demandeurEmploi =  new DemandeurEmploi();
       
        InformationsPersonnelles informationsPersonnelles = new InformationsPersonnelles();
        informationsPersonnelles.setCodePostal(CODE_POSTAL_MAYOTTE);
        demandeurEmploi.setInformationsPersonnelles(informationsPersonnelles);
        
        BeneficiaireAidesSociales beneficiaireAidesSociales = new BeneficiaireAidesSociales();
        beneficiaireAidesSociales.setBeneficiaireARE(true);
        demandeurEmploi.setBeneficiaireAidesSociales(beneficiaireAidesSociales);
        
        RessourcesFinancieres ressourcesFinancieres = new RessourcesFinancieres();
        AllocationsPoleEmploi allocationsPE = new AllocationsPoleEmploi();
        allocationsPE.setAllocationJournaliereNet(12.68f);
        ressourcesFinancieres.setAllocationsPoleEmploi(allocationsPE);
        demandeurEmploi.setRessourcesFinancieres(ressourcesFinancieres);
        
        //lorsque l'on appelle isBeneficiaireAidePEouCAF
        boolean beneficiaire = beneficiaireAidesSocialesUtile.isBeneficiaireAidePEouCAF(demandeurEmploi);
        
        //alors le résultat est DE beneficiaire
        assertThat(beneficiaire).isTrue();
    }
    
    @Test
    void isNotBeneficiaireAREAvecMontantAREInferieurEgaleSeuilMaxEligibiliteTest1() {
        
        //si DE France Metropolitaine et montant journalier = 39,38 euros (> seuil max.)
        DemandeurEmploi demandeurEmploi =  new DemandeurEmploi();
       
        InformationsPersonnelles informationsPersonnelles = new InformationsPersonnelles();
        informationsPersonnelles.setCodePostal(CODE_POSTAL_METROPOLITAIN);
        demandeurEmploi.setInformationsPersonnelles(informationsPersonnelles);
        
        BeneficiaireAidesSociales beneficiaireAidesSociales = new BeneficiaireAidesSociales();
        beneficiaireAidesSociales.setBeneficiaireARE(true);
        demandeurEmploi.setBeneficiaireAidesSociales(beneficiaireAidesSociales);
        
        RessourcesFinancieres ressourcesFinancieres = new RessourcesFinancieres();
        AllocationsPoleEmploi allocationsPE = new AllocationsPoleEmploi();
        allocationsPE.setAllocationJournaliereNet(39.38f);
        ressourcesFinancieres.setAllocationsPoleEmploi(allocationsPE);
        demandeurEmploi.setRessourcesFinancieres(ressourcesFinancieres);
        
        //lorsque l'on appelle isBeneficiaireAidePEouCAF
        boolean beneficiaire = beneficiaireAidesSocialesUtile.isBeneficiaireAidePEouCAF(demandeurEmploi);
        
        //alors le résultat est DE beneficiaire
        assertThat(beneficiaire).isFalse();
    }
    
    @Test
    void isNotBeneficiaireAREAvecMontantAREInferieurEgaleSeuilMaxEligibiliteTest2() {
        
        //si DE Mayotte et montant journalier = 16.68 euros (> seuil max.)
        DemandeurEmploi demandeurEmploi =  new DemandeurEmploi();
       
        InformationsPersonnelles informationsPersonnelles = new InformationsPersonnelles();
        informationsPersonnelles.setCodePostal(CODE_POSTAL_MAYOTTE);
        demandeurEmploi.setInformationsPersonnelles(informationsPersonnelles);
        
        BeneficiaireAidesSociales beneficiaireAidesSociales = new BeneficiaireAidesSociales();
        beneficiaireAidesSociales.setBeneficiaireARE(true);
        demandeurEmploi.setBeneficiaireAidesSociales(beneficiaireAidesSociales);
        
        RessourcesFinancieres ressourcesFinancieres = new RessourcesFinancieres();
        AllocationsPoleEmploi allocationsPE = new AllocationsPoleEmploi();
        allocationsPE.setAllocationJournaliereNet(16.68f);
        ressourcesFinancieres.setAllocationsPoleEmploi(allocationsPE);
        demandeurEmploi.setRessourcesFinancieres(ressourcesFinancieres);
        
        //lorsque l'on appelle isBeneficiaireAidePEouCAF
        boolean beneficiaire = beneficiaireAidesSocialesUtile.isBeneficiaireAidePEouCAF(demandeurEmploi);
        
        //alors le résultat est DE beneficiaire
        assertThat(beneficiaire).isFalse();
    }
}
