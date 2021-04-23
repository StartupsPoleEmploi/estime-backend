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
class AideAgepiTestsHorsMayotte1 {
    
    public static final String CODE_POSTAL_MAYOTTE = "97600";
    public static final String CODE_POSTAL_METROPOLITAIN = "72300";
    public static final int PREMIER_MOIS_SIMULATION = 1;
    
    @Autowired
    private Agepi agepi;
    
    @Autowired
    TestUtile testUtile;
    
    @Configuration
    @ComponentScan({"fr.poleemploi.test","fr.poleemploi.estime"})
    public static class SpringConfig {

    }
    
    /************* département hors Mayotte *********/
    
    @Test
    void isEligibleTest1() {
        
        //Si DE France Métropolitaine, futur contrat CDI, pas en couple, 1 enfant 9 ans à charge et allocation journalière net = 29.38 euros (= seuil max.)
        DemandeurEmploi demandeurEmploi = new DemandeurEmploi();
        
        FuturTravail futurContratTravail = new FuturTravail();
        futurContratTravail.setTypeContrat(TypesContratTravail.CDI.name());
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
        
        SituationFamiliale situationFamiliale = new SituationFamiliale();
        List<Personne> personnesACharge = new ArrayList<Personne>();
        testUtile.createPersonne(personnesACharge, 9); 
        
        situationFamiliale.setPersonnesACharge(personnesACharge);
        situationFamiliale.setIsEnCouple(false);
        demandeurEmploi.setSituationFamiliale(situationFamiliale);
        
        
        //Lorsque l'on vérifie son éligibilité
        boolean isEligible = agepi.isEligible(PREMIER_MOIS_SIMULATION, demandeurEmploi);
        
        //Alors le DE est éligible à l'AGEPI
        assertThat(isEligible).isTrue();
    }
    
    @Test
    void isEligibleTest2() {
        
        //Si DE France Métropolitaine, futur contrat CDI, pas en couple, 1 enfant 9 ans à charge et allocation journalière net = 14.38 euros (< seuil max.)
        DemandeurEmploi demandeurEmploi = new DemandeurEmploi();
        
        FuturTravail futurContratTravail = new FuturTravail();
        futurContratTravail.setTypeContrat(TypesContratTravail.CDI.name());
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
        
        SituationFamiliale situationFamiliale = new SituationFamiliale();
        List<Personne> personnesACharge = new ArrayList<Personne>();
        testUtile.createPersonne(personnesACharge, 9); 
        
        situationFamiliale.setPersonnesACharge(personnesACharge);
        situationFamiliale.setIsEnCouple(false);
        demandeurEmploi.setSituationFamiliale(situationFamiliale);
        
        
        //Lorsque l'on vérifie son éligibilité
        boolean isEligible = agepi.isEligible(PREMIER_MOIS_SIMULATION, demandeurEmploi);
        
        //Alors le DE est éligible à l'AGEPI
        assertThat(isEligible).isTrue();
    }
    
    @Test
    void isEligibleTest3() throws ParseException {
        
        //Si DE France Métropolitaine, futur contrat CDD de 3 mois ( = minimum exigé) , pas en couple, 1 enfant 9 ans à charge et allocation journalière net = 29.38 euros (= seuil max.)
        DemandeurEmploi demandeurEmploi = new DemandeurEmploi();
        
        FuturTravail futurContratTravail = new FuturTravail();
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
        
        SituationFamiliale situationFamiliale = new SituationFamiliale();
        List<Personne> personnesACharge = new ArrayList<Personne>();
        testUtile.createPersonne(personnesACharge, 9); 
        
        situationFamiliale.setPersonnesACharge(personnesACharge);
        situationFamiliale.setIsEnCouple(false);
        demandeurEmploi.setSituationFamiliale(situationFamiliale);
        
        
        //Lorsque l'on vérifie son éligibilité
        boolean isEligible = agepi.isEligible(PREMIER_MOIS_SIMULATION, demandeurEmploi);
        
        //Alors le DE est éligible à l'AGEPI
        assertThat(isEligible).isTrue();
    }
    
    @Test
    void isEligibleTest4() throws ParseException {
        
        //Si DE France Métropolitaine, futur contrat CDD de 4 mois ( > minimum exigé), pas en couple, 1 enfant 9 ans à charge et allocation journalière net = 29.38 euros (= seuil max.)
        DemandeurEmploi demandeurEmploi = new DemandeurEmploi();
        
        FuturTravail futurContratTravail = new FuturTravail();
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
        
        SituationFamiliale situationFamiliale = new SituationFamiliale();
        List<Personne> personnesACharge = new ArrayList<Personne>();
        testUtile.createPersonne(personnesACharge, 9); 
        
        situationFamiliale.setPersonnesACharge(personnesACharge);
        situationFamiliale.setIsEnCouple(false);
        demandeurEmploi.setSituationFamiliale(situationFamiliale);
        
        
        //Lorsque l'on vérifie son éligibilité
        boolean isEligible = agepi.isEligible(PREMIER_MOIS_SIMULATION, demandeurEmploi);
        
        //Alors le DE est éligible à l'AGEPI
        assertThat(isEligible).isTrue();
    }
    
    @Test
    void isEligibleTest5() {
        
        //Si DE France Métropolitaine, futur contrat CDI, pas en couple, 1 enfant 9 ans à charge et bénéficiaire du RSA
        DemandeurEmploi demandeurEmploi = new DemandeurEmploi();
        
        FuturTravail futurContratTravail = new FuturTravail();
        futurContratTravail.setTypeContrat(TypesContratTravail.CDI.name());
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
        
        SituationFamiliale situationFamiliale = new SituationFamiliale();
        List<Personne> personnesACharge = new ArrayList<Personne>();
        testUtile.createPersonne(personnesACharge, 9); 
        
        situationFamiliale.setPersonnesACharge(personnesACharge);
        situationFamiliale.setIsEnCouple(false);
        demandeurEmploi.setSituationFamiliale(situationFamiliale);
        
        
        //Lorsque l'on vérifie son éligibilité
        boolean isEligible = agepi.isEligible(PREMIER_MOIS_SIMULATION, demandeurEmploi);
        
        //Alors le DE est éligible à l'AGEPI
        assertThat(isEligible).isTrue();
    }
    
    @Test
    void isEligibleTest6() {
        
        //Si DE France Métropolitaine, futur contrat CDI, pas en couple, 1 enfant 9 ans à charge et bénéficiaire de AAH
        DemandeurEmploi demandeurEmploi = new DemandeurEmploi();
        
        FuturTravail futurContratTravail = new FuturTravail();
        futurContratTravail.setTypeContrat(TypesContratTravail.CDI.name());
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
        
        SituationFamiliale situationFamiliale = new SituationFamiliale();
        List<Personne> personnesACharge = new ArrayList<Personne>();
        testUtile.createPersonne(personnesACharge, 9); 
        
        situationFamiliale.setPersonnesACharge(personnesACharge);
        situationFamiliale.setIsEnCouple(false);
        demandeurEmploi.setSituationFamiliale(situationFamiliale);
        
        
        //Lorsque l'on vérifie son éligibilité
        boolean isEligible = agepi.isEligible(PREMIER_MOIS_SIMULATION, demandeurEmploi);
        
        //Alors le DE est éligible à l'AGEPI
        assertThat(isEligible).isTrue();
    }
    
    @Test
    void isEligibleTest7() {
        
        //Si DE France Métropolitaine, futur contrat CDI, pas en couple, 1 enfant 9 ans à charge et bénéficiaire de ASS
        DemandeurEmploi demandeurEmploi = new DemandeurEmploi();
        
        FuturTravail futurContratTravail = new FuturTravail();
        futurContratTravail.setTypeContrat(TypesContratTravail.CDI.name());
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
        
        SituationFamiliale situationFamiliale = new SituationFamiliale();
        List<Personne> personnesACharge = new ArrayList<Personne>();
        testUtile.createPersonne(personnesACharge, 9); 
        
        situationFamiliale.setPersonnesACharge(personnesACharge);
        situationFamiliale.setIsEnCouple(false);
        demandeurEmploi.setSituationFamiliale(situationFamiliale);
        
        
        //Lorsque l'on vérifie son éligibilité
        boolean isEligible = agepi.isEligible(PREMIER_MOIS_SIMULATION, demandeurEmploi);
        
        //Alors le DE est éligible à l'AGEPI
        assertThat(isEligible).isTrue();
    }
    
    @Test
    void isEligibleTest8() {
        
        //Si DE France Métropolitaine, futur contrat CDI, pas en couple, 1 enfant 9 ans à charge et sans ressources
        DemandeurEmploi demandeurEmploi = new DemandeurEmploi();
        
        FuturTravail futurContratTravail = new FuturTravail();
        futurContratTravail.setTypeContrat(TypesContratTravail.CDI.name());
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
        
        SituationFamiliale situationFamiliale = new SituationFamiliale();
        List<Personne> personnesACharge = new ArrayList<Personne>();
        testUtile.createPersonne(personnesACharge, 9); 
        situationFamiliale.setPersonnesACharge(personnesACharge);
        situationFamiliale.setIsEnCouple(false);
        demandeurEmploi.setSituationFamiliale(situationFamiliale);
        
        
        //Lorsque l'on vérifie son éligibilité
        boolean isEligible = agepi.isEligible(PREMIER_MOIS_SIMULATION, demandeurEmploi);
        
        //Alors le DE est éligible à l'AGEPI
        assertThat(isEligible).isTrue();
    }
}
