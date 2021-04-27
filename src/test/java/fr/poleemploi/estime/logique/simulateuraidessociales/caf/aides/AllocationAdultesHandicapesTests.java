package fr.poleemploi.estime.logique.simulateuraidessociales.caf.aides;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import fr.poleemploi.estime.commun.enumerations.AidesSociales;
import fr.poleemploi.estime.services.ressources.AideSociale;
import fr.poleemploi.estime.services.ressources.AllocationsCAF;
import fr.poleemploi.estime.services.ressources.DemandeurEmploi;
import fr.poleemploi.estime.services.ressources.FuturTravail;
import fr.poleemploi.estime.services.ressources.RessourcesFinancieres;


@SpringBootTest
@ContextConfiguration
@TestPropertySource(locations="classpath:application-test.properties")
class AllocationAdultesHandicapesTests {

    @Autowired
    private AllocationAdultesHandicapes allocationAdultesHandicapes;

    @Configuration
    @ComponentScan({"fr.poleemploi.test","fr.poleemploi.estime"})
    public static class SpringConfig {

    }
    
  
  
    @Test
    void simulerAAHTest1() {


        //Si DE a déjà travaillé 6 mois sur les 6 derniers mois
        //futur travail avec salaire brut = 1000 euros (supérieur au palier AAH)
        //AAH = 451 euros 
        DemandeurEmploi demandeurEmploi = new DemandeurEmploi();
        FuturTravail futurTravail = new FuturTravail();
        futurTravail.setSalaireMensuelBrut(1000);
        demandeurEmploi.setFuturTravail(futurTravail);
        RessourcesFinancieres ressourcesFinancieres = new RessourcesFinancieres();
        ressourcesFinancieres.setNombreMoisTravaillesDerniersMois(6);
        AllocationsCAF allocationsCAF = new AllocationsCAF();
        allocationsCAF.setAllocationMensuelleNetAAH(450f);
        ressourcesFinancieres.setAllocationsCAF(allocationsCAF);
        demandeurEmploi.setRessourcesFinancieres(ressourcesFinancieres);

        //Lorsque l'on appel simulerAAH 
        Map<String, AideSociale>  aidesEligiblesPourCeMois = new HashMap<>();
        int numeroMoisSimule = 1;
        allocationAdultesHandicapes.simulerAAH(aidesEligiblesPourCeMois, numeroMoisSimule, demandeurEmploi);

        //Alors l'AAH n'apparait pas dans les aides du mois
        //60% du salaire = 600
        //AAH - 60% du salaire = 450 - 600 = -150 donc AAH = 0 euros
        assertThat(aidesEligiblesPourCeMois.size()).isEqualTo(0);
    }
    
    @Test
    void simulerAAHTest2() {

        //Si DE a déjà travaillé 6 mois sur les 6 derniers mois
        //futur travail avec salaire brut = 400 euros
        //AAH = 450 euros 
        DemandeurEmploi demandeurEmploi = new DemandeurEmploi();
        FuturTravail futurTravail = new FuturTravail();
        futurTravail.setSalaireMensuelBrut(400);
        demandeurEmploi.setFuturTravail(futurTravail);
        RessourcesFinancieres ressourcesFinancieres = new RessourcesFinancieres();
        ressourcesFinancieres.setNombreMoisTravaillesDerniersMois(6);
        AllocationsCAF allocationsCAF = new AllocationsCAF();
        allocationsCAF.setAllocationMensuelleNetAAH(450f);
        ressourcesFinancieres.setAllocationsCAF(allocationsCAF);
        demandeurEmploi.setRessourcesFinancieres(ressourcesFinancieres);

        //Lorsque l'on appel simulerAAH
        Map<String, AideSociale>  aidesEligiblesPourCeMois = new HashMap<>();
        int numeroMoisSimule = 1;
        allocationAdultesHandicapes.simulerAAH(aidesEligiblesPourCeMois, numeroMoisSimule, demandeurEmploi);

        //Alors le montant de l'AAH est de 370 euros
        //20% du salaire = 80
        //AAH - 60% du salaire = 450 - 80 = 370 euros
        assertThat(aidesEligiblesPourCeMois.get(AidesSociales.ALLOCATION_ADULTES_HANDICAPES.getCode())).satisfies(aideAAH -> { 
            assertThat(aideAAH.getMontant()).isEqualTo(370);
        });
    }
    
    @Test
    void simulerAAHTest3() {

        //Si DE a déjà travaillé 6 mois sur les 6 derniers mois
        //futur travail avec salaire brut = 500 euros
        //AAH = 450 euros 
        DemandeurEmploi demandeurEmploi = new DemandeurEmploi();
        FuturTravail futurTravail = new FuturTravail();
        futurTravail.setSalaireMensuelBrut(500);
        demandeurEmploi.setFuturTravail(futurTravail);
        RessourcesFinancieres ressourcesFinancieres = new RessourcesFinancieres();
        ressourcesFinancieres.setNombreMoisTravaillesDerniersMois(6);
        AllocationsCAF allocationsCAF = new AllocationsCAF();
        allocationsCAF.setAllocationMensuelleNetAAH(450f);
        ressourcesFinancieres.setAllocationsCAF(allocationsCAF);
        demandeurEmploi.setRessourcesFinancieres(ressourcesFinancieres);

        //Lorsque l'on appel simulerAAH
        Map<String, AideSociale>  aidesEligiblesPourCeMois = new HashMap<>();
        int numeroMoisSimule = 1;
        allocationAdultesHandicapes.simulerAAH(aidesEligiblesPourCeMois, numeroMoisSimule, demandeurEmploi);

        //Alors le montant de l'AAH est de 150 euros
        //60% du salaire = 300
        //AAH - 60% du salaire = 450 - 300 = 150 euros
        assertThat(aidesEligiblesPourCeMois.get(AidesSociales.ALLOCATION_ADULTES_HANDICAPES.getCode())).satisfies(aideAAH -> { 
            assertThat(aideAAH.getMontant()).isEqualTo(150);
        });
    }
    
}
