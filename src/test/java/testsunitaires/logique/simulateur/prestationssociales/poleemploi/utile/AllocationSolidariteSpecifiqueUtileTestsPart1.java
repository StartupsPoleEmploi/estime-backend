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

import fr.poleemploi.estime.commun.enumerations.TypesContratTravail;
import fr.poleemploi.estime.logique.simulateur.prestationssociales.poleemploi.utile.AllocationSolidariteSpecifiqueUtile;
import fr.poleemploi.estime.services.ressources.AllocationASS;
import fr.poleemploi.estime.services.ressources.DemandeurEmploi;
import fr.poleemploi.estime.services.ressources.FuturTravail;
import fr.poleemploi.estime.services.ressources.PrestationsPoleEmploi;
import fr.poleemploi.estime.services.ressources.RessourcesFinancieres;
import fr.poleemploi.estime.services.ressources.Salaire;
import fr.poleemploi.estime.services.ressources.SalaireAvantPeriodeSimulation;
import fr.poleemploi.estime.services.ressources.SalairesAvantPeriodeSimulation;


@ContextConfiguration
@SpringBootTest
@TestPropertySource(locations="classpath:application-test.properties")
class AllocationSolidariteSpecifiqueUtileTestsPart1 {

    @Autowired
    private AllocationSolidariteSpecifiqueUtile allocationSolidariteSpecifiqueUtile;
    
    @Configuration
    @ComponentScan({"utile.tests","fr.poleemploi.estime"})
    public static class SpringConfig {

    }
    
    /***** tests nombre mois eligible ASS si CDD de 1 mois *****/
    
    @Test
    void getNombreMoisEligibleCDD1MoisTest() throws ParseException {
        
        //Si DE, futur contrat CDD 1 mois et cumulé ASS+Salaire 0 mois sur les 3 derniers mois
        DemandeurEmploi demandeurEmploi = new DemandeurEmploi();
        RessourcesFinancieres ressourcesFinancieres = new RessourcesFinancieres();
        ressourcesFinancieres.setHasTravailleAuCoursDerniersMois(false);
        PrestationsPoleEmploi prestationsPoleEmploi = new PrestationsPoleEmploi();
        AllocationASS allocationASS = new AllocationASS();
        allocationASS.setAllocationJournaliereNet(16.89f);
        prestationsPoleEmploi.setAllocationASS(allocationASS);        
        ressourcesFinancieres.setPrestationsPoleEmploi(prestationsPoleEmploi);
        demandeurEmploi.setRessourcesFinancieres(ressourcesFinancieres);
        
        FuturTravail futurTravail = new FuturTravail();
        futurTravail.setTypeContrat(TypesContratTravail.CDD.name());
        futurTravail.setNombreMoisContratCDD(1);
        demandeurEmploi.setFuturTravail(futurTravail);
        
        //Lorsque je calcul le nombre de mois éligible à l'ASS
        int nombreMoisEligible = allocationSolidariteSpecifiqueUtile.getNombreMoisEligibles(demandeurEmploi);
        
        //alors la simulation se fait sur 1 mois et le DE a droit à 1 mois d'ASS
        assertThat(nombreMoisEligible).isEqualTo(1);
    }
    
    @Test
    void getNombreMoisEligibleCDD1MoisTest1() throws ParseException {
        
        //Si DE, futur contrat CDD 1 mois et cumulé ASS+Salaire 1 mois sur les 3 derniers mois
        DemandeurEmploi demandeurEmploi = new DemandeurEmploi();
        RessourcesFinancieres ressourcesFinancieres = new RessourcesFinancieres();
        ressourcesFinancieres.setHasTravailleAuCoursDerniersMois(true);
        ressourcesFinancieres.setNombreMoisTravaillesDerniersMois(1);
        SalairesAvantPeriodeSimulation salairesAvantPeriodeSimulation = new SalairesAvantPeriodeSimulation();
        SalaireAvantPeriodeSimulation salaireAvantPeriodeSimulationMoisDemande = new SalaireAvantPeriodeSimulation();
        Salaire salaireMoisDemande = new Salaire();
        salaireMoisDemande.setMontantNet(850);
        salaireMoisDemande.setMontantBrut(1101);
        salaireAvantPeriodeSimulationMoisDemande.setSalaire(salaireMoisDemande);
        salairesAvantPeriodeSimulation.setSalaireMoisDemandeSimulation(salaireAvantPeriodeSimulationMoisDemande);
        SalaireAvantPeriodeSimulation salaireAvantPeriodeSimulationMoisMoins1Mois = new SalaireAvantPeriodeSimulation();
        Salaire salaireMoisMoins1Mois = new Salaire();
        salaireMoisMoins1Mois.setMontantNet(0);
        salaireMoisMoins1Mois.setMontantBrut(0); 
        salaireAvantPeriodeSimulationMoisMoins1Mois.setSalaire(salaireMoisMoins1Mois);
        salairesAvantPeriodeSimulation.setSalaireMoisMoins1MoisDemandeSimulation(salaireAvantPeriodeSimulationMoisMoins1Mois);
        SalaireAvantPeriodeSimulation salaireAvantPeriodeSimulationMoisMoins2Mois = new SalaireAvantPeriodeSimulation();
        Salaire salaireMoisMoins2Mois = new Salaire();
        salaireMoisMoins2Mois.setMontantNet(0);
        salaireMoisMoins2Mois.setMontantBrut(0); 
        salaireAvantPeriodeSimulationMoisMoins2Mois.setSalaire(salaireMoisMoins2Mois);
        salairesAvantPeriodeSimulation.setSalaireMoisMoins2MoisDemandeSimulation(salaireAvantPeriodeSimulationMoisMoins2Mois);
        ressourcesFinancieres.setSalairesAvantPeriodeSimulation(salairesAvantPeriodeSimulation);
        PrestationsPoleEmploi prestationsPoleEmploi = new PrestationsPoleEmploi();
        AllocationASS allocationASS = new AllocationASS();
        allocationASS.setAllocationJournaliereNet(16.89f);
        prestationsPoleEmploi.setAllocationASS(allocationASS);
        ressourcesFinancieres.setPrestationsPoleEmploi(prestationsPoleEmploi);
        demandeurEmploi.setRessourcesFinancieres(ressourcesFinancieres);
        
        FuturTravail futurTravail = new FuturTravail();
        futurTravail.setTypeContrat(TypesContratTravail.CDD.name());
        futurTravail.setNombreMoisContratCDD(1);
        demandeurEmploi.setFuturTravail(futurTravail);
        
        //Lorsque je calcul le nombre de mois éligible à l'ASS
        int nombreMoisEligible = allocationSolidariteSpecifiqueUtile.getNombreMoisEligibles(demandeurEmploi);
        
        //alors la simulation se fait sur 1 mois et le DE a droit à 0 mois d'ASS
        assertThat(nombreMoisEligible).isEqualTo(0);
    }
    
    @Test
    void getNombreMoisEligibleCDD1MoisTest2() throws ParseException {
        
        //Si DE, futur contrat CDD 1 mois et cumulé ASS+Salaire 2 mois sur les 3 derniers mois
        DemandeurEmploi demandeurEmploi = new DemandeurEmploi();
        RessourcesFinancieres ressourcesFinancieres = new RessourcesFinancieres();
        ressourcesFinancieres.setHasTravailleAuCoursDerniersMois(true);
        ressourcesFinancieres.setNombreMoisTravaillesDerniersMois(2);
        SalairesAvantPeriodeSimulation salairesAvantPeriodeSimulation = new SalairesAvantPeriodeSimulation();
        SalaireAvantPeriodeSimulation salaireAvantPeriodeSimulationMoisDemande = new SalaireAvantPeriodeSimulation();
        Salaire salaireMoisDemande = new Salaire();
        salaireMoisDemande.setMontantNet(850);
        salaireMoisDemande.setMontantBrut(1101);
        salaireAvantPeriodeSimulationMoisDemande.setSalaire(salaireMoisDemande);
        salairesAvantPeriodeSimulation.setSalaireMoisDemandeSimulation(salaireAvantPeriodeSimulationMoisDemande);
        SalaireAvantPeriodeSimulation salaireAvantPeriodeSimulationMoisMoins1Mois = new SalaireAvantPeriodeSimulation();
        Salaire salaireMoisMoins1Mois = new Salaire();
        salaireMoisMoins1Mois.setMontantNet(800);
        salaireMoisMoins1Mois.setMontantBrut(1038); 
        salaireAvantPeriodeSimulationMoisMoins1Mois.setSalaire(salaireMoisMoins1Mois);
        salairesAvantPeriodeSimulation.setSalaireMoisMoins1MoisDemandeSimulation(salaireAvantPeriodeSimulationMoisMoins1Mois);
        SalaireAvantPeriodeSimulation salaireAvantPeriodeSimulationMoisMoins2Mois = new SalaireAvantPeriodeSimulation();
        Salaire salaireMoisMoins2Mois = new Salaire();
        salaireMoisMoins2Mois.setMontantNet(0);
        salaireMoisMoins2Mois.setMontantBrut(0); 
        salaireAvantPeriodeSimulationMoisMoins2Mois.setSalaire(salaireMoisMoins2Mois);
        salairesAvantPeriodeSimulation.setSalaireMoisMoins2MoisDemandeSimulation(salaireAvantPeriodeSimulationMoisMoins2Mois);
        ressourcesFinancieres.setSalairesAvantPeriodeSimulation(salairesAvantPeriodeSimulation);
        PrestationsPoleEmploi prestationsPoleEmploi = new PrestationsPoleEmploi();
        AllocationASS allocationASS = new AllocationASS();
        allocationASS.setAllocationJournaliereNet(16.89f);
        prestationsPoleEmploi.setAllocationASS(allocationASS);
        ressourcesFinancieres.setPrestationsPoleEmploi(prestationsPoleEmploi);
        demandeurEmploi.setRessourcesFinancieres(ressourcesFinancieres);
        
        FuturTravail futurTravail = new FuturTravail();
        futurTravail.setTypeContrat(TypesContratTravail.CDD.name());
        futurTravail.setNombreMoisContratCDD(1);
        demandeurEmploi.setFuturTravail(futurTravail);
        
        //Lorsque je calcul le nombre de mois éligible à l'ASS
        int nombreMoisEligible = allocationSolidariteSpecifiqueUtile.getNombreMoisEligibles(demandeurEmploi);
        
        //alors la simulation se fait sur 1 mois et alors le DE a droit à 0 mois d'ASS
        assertThat(nombreMoisEligible).isEqualTo(0);
    }
    
    
    @Test
    void getNombreMoisEligibleCDD1MoisTest3() throws ParseException {
        
        //Si DE, futur contrat CDD 1 mois et cumulé ASS+Salaire 3 mois sur les 3 derniers mois
        DemandeurEmploi demandeurEmploi = new DemandeurEmploi();
        RessourcesFinancieres ressourcesFinancieres = new RessourcesFinancieres();
        ressourcesFinancieres.setHasTravailleAuCoursDerniersMois(true);
        ressourcesFinancieres.setHasTravailleAuCoursDerniersMois(true);
        ressourcesFinancieres.setNombreMoisTravaillesDerniersMois(3);
        SalairesAvantPeriodeSimulation salairesAvantPeriodeSimulation = new SalairesAvantPeriodeSimulation();
        SalaireAvantPeriodeSimulation salaireAvantPeriodeSimulationMoisDemande = new SalaireAvantPeriodeSimulation();
        Salaire salaireMoisDemande = new Salaire();
        salaireMoisDemande.setMontantNet(850);
        salaireMoisDemande.setMontantBrut(1101);
        salaireAvantPeriodeSimulationMoisDemande.setSalaire(salaireMoisDemande);
        salairesAvantPeriodeSimulation.setSalaireMoisDemandeSimulation(salaireAvantPeriodeSimulationMoisDemande);
        SalaireAvantPeriodeSimulation salaireAvantPeriodeSimulationMoisMoins1Mois = new SalaireAvantPeriodeSimulation();
        Salaire salaireMoisMoins1Mois = new Salaire();
        salaireMoisMoins1Mois.setMontantNet(800);
        salaireMoisMoins1Mois.setMontantBrut(1038); 
        salaireAvantPeriodeSimulationMoisMoins1Mois.setSalaire(salaireMoisMoins1Mois);
        salairesAvantPeriodeSimulation.setSalaireMoisMoins1MoisDemandeSimulation(salaireAvantPeriodeSimulationMoisMoins1Mois);
        SalaireAvantPeriodeSimulation salaireAvantPeriodeSimulationMoisMoins2Mois = new SalaireAvantPeriodeSimulation();
        Salaire salaireMoisMoins2Mois = new Salaire();
        salaireMoisMoins2Mois.setMontantNet(800);
        salaireMoisMoins2Mois.setMontantBrut(1038); 
        salaireAvantPeriodeSimulationMoisMoins2Mois.setSalaire(salaireMoisMoins2Mois);
        salairesAvantPeriodeSimulation.setSalaireMoisMoins2MoisDemandeSimulation(salaireAvantPeriodeSimulationMoisMoins2Mois);
        ressourcesFinancieres.setSalairesAvantPeriodeSimulation(salairesAvantPeriodeSimulation);
        PrestationsPoleEmploi prestationsPoleEmploi = new PrestationsPoleEmploi();
        AllocationASS allocationASS = new AllocationASS();
        allocationASS.setAllocationJournaliereNet(16.89f);
        prestationsPoleEmploi.setAllocationASS(allocationASS);
        ressourcesFinancieres.setPrestationsPoleEmploi(prestationsPoleEmploi);
        demandeurEmploi.setRessourcesFinancieres(ressourcesFinancieres);
        
        FuturTravail futurTravail = new FuturTravail();
        futurTravail.setTypeContrat(TypesContratTravail.CDD.name());
        futurTravail.setNombreMoisContratCDD(1);
        demandeurEmploi.setFuturTravail(futurTravail);
        
        //Lorsque je calcul le nombre de mois éligible à l'ASS
        int nombreMoisEligible = allocationSolidariteSpecifiqueUtile.getNombreMoisEligibles(demandeurEmploi);
        
        //alors la simulation se fait sur 1 mois et le DE a droit à 0 mois d'ASS
        assertThat(nombreMoisEligible).isEqualTo(0);
    }
    
    /***** tests nombre mois eligible ASS si CDD de 2 mois *****/
    
    @Test
    void getNombreMoisEligibleCDD2MoisTest() throws ParseException {
        
        //Si DE, futur contrat CDD 2 mois et cumulé ASS+Salaire 0 mois sur les 3 derniers mois
        DemandeurEmploi demandeurEmploi = new DemandeurEmploi();
        RessourcesFinancieres ressourcesFinancieres = new RessourcesFinancieres();
        ressourcesFinancieres.setHasTravailleAuCoursDerniersMois(false);
        PrestationsPoleEmploi prestationsPoleEmploi = new PrestationsPoleEmploi();
        AllocationASS allocationASS = new AllocationASS();
        allocationASS.setAllocationJournaliereNet(16.89f);
        prestationsPoleEmploi.setAllocationASS(allocationASS);
        ressourcesFinancieres.setPrestationsPoleEmploi(prestationsPoleEmploi);
        demandeurEmploi.setRessourcesFinancieres(ressourcesFinancieres);
        
        FuturTravail futurTravail = new FuturTravail();
        futurTravail.setTypeContrat(TypesContratTravail.CDD.name());
        futurTravail.setNombreMoisContratCDD(2);
        demandeurEmploi.setFuturTravail(futurTravail);
        
        //Lorsque je calcul le nombre de mois éligible à l'ASS
        int nombreMoisEligible = allocationSolidariteSpecifiqueUtile.getNombreMoisEligibles(demandeurEmploi);
        
        //alors la simulation se fait sur 2 mois et le DE a droit à 1 mois d'ASS
        assertThat(nombreMoisEligible).isEqualTo(2);
    }
    
    @Test
    void getNombreMoisEligibleCDD2MoisTest1() throws ParseException {
        
        //Si DE, futur contrat CDD 2 mois et cumulé ASS+Salaire 1 mois sur les 3 derniers mois
        DemandeurEmploi demandeurEmploi = new DemandeurEmploi();
        RessourcesFinancieres ressourcesFinancieres = new RessourcesFinancieres();
        ressourcesFinancieres.setHasTravailleAuCoursDerniersMois(true);
        ressourcesFinancieres.setNombreMoisTravaillesDerniersMois(1);
        SalairesAvantPeriodeSimulation salairesAvantPeriodeSimulation = new SalairesAvantPeriodeSimulation();
        SalaireAvantPeriodeSimulation salaireAvantPeriodeSimulationMoisDemande = new SalaireAvantPeriodeSimulation();
        Salaire salaireMoisDemande = new Salaire();
        salaireMoisDemande.setMontantNet(850);
        salaireMoisDemande.setMontantBrut(1101);
        salaireAvantPeriodeSimulationMoisDemande.setSalaire(salaireMoisDemande);
        salairesAvantPeriodeSimulation.setSalaireMoisDemandeSimulation(salaireAvantPeriodeSimulationMoisDemande);
        SalaireAvantPeriodeSimulation salaireAvantPeriodeSimulationMoisMoins1Mois = new SalaireAvantPeriodeSimulation();
        Salaire salaireMoisMoins1Mois = new Salaire();
        salaireMoisMoins1Mois.setMontantNet(0);
        salaireMoisMoins1Mois.setMontantBrut(0); 
        salaireAvantPeriodeSimulationMoisMoins1Mois.setSalaire(salaireMoisMoins1Mois);
        salairesAvantPeriodeSimulation.setSalaireMoisMoins1MoisDemandeSimulation(salaireAvantPeriodeSimulationMoisMoins1Mois);
        SalaireAvantPeriodeSimulation salaireAvantPeriodeSimulationMoisMoins2Mois = new SalaireAvantPeriodeSimulation();
        Salaire salaireMoisMoins2Mois = new Salaire();
        salaireMoisMoins2Mois.setMontantNet(0);
        salaireMoisMoins2Mois.setMontantBrut(0); 
        salaireAvantPeriodeSimulationMoisMoins2Mois.setSalaire(salaireMoisMoins2Mois);
        salairesAvantPeriodeSimulation.setSalaireMoisMoins2MoisDemandeSimulation(salaireAvantPeriodeSimulationMoisMoins2Mois);
        ressourcesFinancieres.setSalairesAvantPeriodeSimulation(salairesAvantPeriodeSimulation);
        PrestationsPoleEmploi prestationsPoleEmploi = new PrestationsPoleEmploi();
        AllocationASS allocationASS = new AllocationASS();
        allocationASS.setAllocationJournaliereNet(16.89f);
        prestationsPoleEmploi.setAllocationASS(allocationASS);
        ressourcesFinancieres.setPrestationsPoleEmploi(prestationsPoleEmploi);
        demandeurEmploi.setRessourcesFinancieres(ressourcesFinancieres);
        
        FuturTravail futurTravail = new FuturTravail();
        futurTravail.setTypeContrat(TypesContratTravail.CDD.name());
        futurTravail.setNombreMoisContratCDD(2);
        demandeurEmploi.setFuturTravail(futurTravail);
        
        //Lorsque je calcul le nombre de mois éligible à l'ASS
        int nombreMoisEligible = allocationSolidariteSpecifiqueUtile.getNombreMoisEligibles(demandeurEmploi);
        
        //alors le DE a droit à 1 mois d'ASS
        assertThat(nombreMoisEligible).isEqualTo(1);
    }
    
    @Test
    void getNombreMoisEligibleCDD2MoisTest2() throws ParseException {
        
        //Si DE, futur contrat CDD 2 mois et cumulé ASS+Salaire 2 mois sur les 3 derniers mois
        DemandeurEmploi demandeurEmploi = new DemandeurEmploi();
        RessourcesFinancieres ressourcesFinancieres = new RessourcesFinancieres();
        ressourcesFinancieres.setHasTravailleAuCoursDerniersMois(true);
        ressourcesFinancieres.setNombreMoisTravaillesDerniersMois(2);
        SalairesAvantPeriodeSimulation salairesAvantPeriodeSimulation = new SalairesAvantPeriodeSimulation();
        SalaireAvantPeriodeSimulation salaireAvantPeriodeSimulationMoisDemande = new SalaireAvantPeriodeSimulation();
        Salaire salaireMoisDemande = new Salaire();
        salaireMoisDemande.setMontantNet(850);
        salaireMoisDemande.setMontantBrut(1101);
        salaireAvantPeriodeSimulationMoisDemande.setSalaire(salaireMoisDemande);
        salairesAvantPeriodeSimulation.setSalaireMoisDemandeSimulation(salaireAvantPeriodeSimulationMoisDemande);
        SalaireAvantPeriodeSimulation salaireAvantPeriodeSimulationMoisMoins1Mois = new SalaireAvantPeriodeSimulation();
        Salaire salaireMoisMoins1Mois = new Salaire();
        salaireMoisMoins1Mois.setMontantNet(800);
        salaireMoisMoins1Mois.setMontantBrut(1038); 
        salaireAvantPeriodeSimulationMoisMoins1Mois.setSalaire(salaireMoisMoins1Mois);
        salairesAvantPeriodeSimulation.setSalaireMoisMoins1MoisDemandeSimulation(salaireAvantPeriodeSimulationMoisMoins1Mois);
        SalaireAvantPeriodeSimulation salaireAvantPeriodeSimulationMoisMoins2Mois = new SalaireAvantPeriodeSimulation();
        Salaire salaireMoisMoins2Mois = new Salaire();
        salaireMoisMoins2Mois.setMontantNet(0);
        salaireMoisMoins2Mois.setMontantBrut(0); 
        salaireAvantPeriodeSimulationMoisMoins2Mois.setSalaire(salaireMoisMoins2Mois);
        salairesAvantPeriodeSimulation.setSalaireMoisMoins2MoisDemandeSimulation(salaireAvantPeriodeSimulationMoisMoins2Mois);
        ressourcesFinancieres.setSalairesAvantPeriodeSimulation(salairesAvantPeriodeSimulation);
        PrestationsPoleEmploi prestationsPoleEmploi = new PrestationsPoleEmploi();
        AllocationASS allocationASS = new AllocationASS();
        allocationASS.setAllocationJournaliereNet(16.89f);
        prestationsPoleEmploi.setAllocationASS(allocationASS);
        ressourcesFinancieres.setPrestationsPoleEmploi(prestationsPoleEmploi);
        demandeurEmploi.setRessourcesFinancieres(ressourcesFinancieres);
        
        FuturTravail futurTravail = new FuturTravail();
        futurTravail.setTypeContrat(TypesContratTravail.CDD.name());
        futurTravail.setNombreMoisContratCDD(2);
        demandeurEmploi.setFuturTravail(futurTravail);
        
        //Lorsque je calcul le nombre de mois éligible à l'ASS
        int nombreMoisEligible = allocationSolidariteSpecifiqueUtile.getNombreMoisEligibles(demandeurEmploi);
        
        //alors la simulation se fait sur 2 mois et le DE a droit à 0 mois d'ASS
        assertThat(nombreMoisEligible).isEqualTo(0);
    }
    
    
    @Test
    void getNombreMoisEligibleCDD2MoisTest3() throws ParseException {
        
        //Si DE, futur contrat CDD 2 mois et cumulé ASS+Salaire 3 mois sur les 3 derniers mois
        DemandeurEmploi demandeurEmploi = new DemandeurEmploi();
        RessourcesFinancieres ressourcesFinancieres = new RessourcesFinancieres();
        ressourcesFinancieres.setHasTravailleAuCoursDerniersMois(true);
        ressourcesFinancieres.setNombreMoisTravaillesDerniersMois(3);
        SalairesAvantPeriodeSimulation salairesAvantPeriodeSimulation = new SalairesAvantPeriodeSimulation();
        SalaireAvantPeriodeSimulation salaireAvantPeriodeSimulationMoisDemande = new SalaireAvantPeriodeSimulation();
        Salaire salaireMoisDemande = new Salaire();
        salaireMoisDemande.setMontantNet(850);
        salaireMoisDemande.setMontantBrut(1101);
        salaireAvantPeriodeSimulationMoisDemande.setSalaire(salaireMoisDemande);
        salairesAvantPeriodeSimulation.setSalaireMoisDemandeSimulation(salaireAvantPeriodeSimulationMoisDemande);
        SalaireAvantPeriodeSimulation salaireAvantPeriodeSimulationMoisMoins1Mois = new SalaireAvantPeriodeSimulation();
        Salaire salaireMoisMoins1Mois = new Salaire();
        salaireMoisMoins1Mois.setMontantNet(800);
        salaireMoisMoins1Mois.setMontantBrut(1038); 
        salaireAvantPeriodeSimulationMoisMoins1Mois.setSalaire(salaireMoisMoins1Mois);
        salairesAvantPeriodeSimulation.setSalaireMoisMoins1MoisDemandeSimulation(salaireAvantPeriodeSimulationMoisMoins1Mois);
        SalaireAvantPeriodeSimulation salaireAvantPeriodeSimulationMoisMoins2Mois = new SalaireAvantPeriodeSimulation();
        Salaire salaireMoisMoins2Mois = new Salaire();
        salaireMoisMoins2Mois.setMontantNet(800);
        salaireMoisMoins2Mois.setMontantBrut(1038); 
        salaireAvantPeriodeSimulationMoisMoins2Mois.setSalaire(salaireMoisMoins2Mois);
        salairesAvantPeriodeSimulation.setSalaireMoisMoins2MoisDemandeSimulation(salaireAvantPeriodeSimulationMoisMoins2Mois);
        ressourcesFinancieres.setSalairesAvantPeriodeSimulation(salairesAvantPeriodeSimulation);
        PrestationsPoleEmploi prestationsPoleEmploi = new PrestationsPoleEmploi();
        AllocationASS allocationASS = new AllocationASS();
        allocationASS.setAllocationJournaliereNet(16.89f);
        prestationsPoleEmploi.setAllocationASS(allocationASS);
        ressourcesFinancieres.setPrestationsPoleEmploi(prestationsPoleEmploi);
        demandeurEmploi.setRessourcesFinancieres(ressourcesFinancieres);
        
        FuturTravail futurTravail = new FuturTravail();
        futurTravail.setTypeContrat(TypesContratTravail.CDD.name());
        futurTravail.setNombreMoisContratCDD(2);
        demandeurEmploi.setFuturTravail(futurTravail);
        
        //Lorsque je calcul le nombre de mois éligible à l'ASS
        int nombreMoisEligible = allocationSolidariteSpecifiqueUtile.getNombreMoisEligibles(demandeurEmploi);
        
        //alors la simulation se fait sur 2 mois le DE a droit à 0 mois d'ASS
        assertThat(nombreMoisEligible).isEqualTo(0);
    }
   
    /***** tests nombre mois eligible ASS si CDD de 3 mois *****/
    
    @Test
    void getNombreMoisEligibleCDD3MoisTest() throws ParseException {
        
        //Si DE, futur contrat CDD 3 mois et cumulé ASS+Salaire 0 mois sur les 3 derniers mois
        DemandeurEmploi demandeurEmploi = new DemandeurEmploi();
        RessourcesFinancieres ressourcesFinancieres = new RessourcesFinancieres();
        ressourcesFinancieres.setHasTravailleAuCoursDerniersMois(false);
        PrestationsPoleEmploi prestationsPoleEmploi = new PrestationsPoleEmploi();
        AllocationASS allocationASS = new AllocationASS();
        allocationASS.setAllocationJournaliereNet(16.89f);
        prestationsPoleEmploi.setAllocationASS(allocationASS);
        ressourcesFinancieres.setPrestationsPoleEmploi(prestationsPoleEmploi);
        demandeurEmploi.setRessourcesFinancieres(ressourcesFinancieres);
        
        FuturTravail futurTravail = new FuturTravail();
        futurTravail.setTypeContrat(TypesContratTravail.CDD.name());
        futurTravail.setNombreMoisContratCDD(3);
        demandeurEmploi.setFuturTravail(futurTravail);
        
        //Lorsque je calcul le nombre de mois éligible à l'ASS
        int nombreMoisEligible = allocationSolidariteSpecifiqueUtile.getNombreMoisEligibles(demandeurEmploi);
        
        //alors la simulation se fait sur 3 mois et le DE a droit à 3 mois d'ASS
        assertThat(nombreMoisEligible).isEqualTo(3);
    }
    
    @Test
    void getNombreMoisEligibleCDD3MoisTest1() throws ParseException {
        
        //Si DE, futur contrat CDD 3 mois et cumulé ASS+Salaire 1 mois sur les 3 derniers mois
        DemandeurEmploi demandeurEmploi = new DemandeurEmploi();
        RessourcesFinancieres ressourcesFinancieres = new RessourcesFinancieres();
        ressourcesFinancieres.setHasTravailleAuCoursDerniersMois(true);
        ressourcesFinancieres.setNombreMoisTravaillesDerniersMois(1);
        SalairesAvantPeriodeSimulation salairesAvantPeriodeSimulation = new SalairesAvantPeriodeSimulation();
        SalaireAvantPeriodeSimulation salaireAvantPeriodeSimulationMoisDemande = new SalaireAvantPeriodeSimulation();
        Salaire salaireMoisDemande = new Salaire();
        salaireMoisDemande.setMontantNet(850);
        salaireMoisDemande.setMontantBrut(1101);
        salaireAvantPeriodeSimulationMoisDemande.setSalaire(salaireMoisDemande);
        salairesAvantPeriodeSimulation.setSalaireMoisDemandeSimulation(salaireAvantPeriodeSimulationMoisDemande);
        SalaireAvantPeriodeSimulation salaireAvantPeriodeSimulationMoisMoins1Mois = new SalaireAvantPeriodeSimulation();
        Salaire salaireMoisMoins1Mois = new Salaire();
        salaireMoisMoins1Mois.setMontantNet(0);
        salaireMoisMoins1Mois.setMontantBrut(0); 
        salaireAvantPeriodeSimulationMoisMoins1Mois.setSalaire(salaireMoisMoins1Mois);
        salairesAvantPeriodeSimulation.setSalaireMoisMoins1MoisDemandeSimulation(salaireAvantPeriodeSimulationMoisMoins1Mois);
        SalaireAvantPeriodeSimulation salaireAvantPeriodeSimulationMoisMoins2Mois = new SalaireAvantPeriodeSimulation();
        Salaire salaireMoisMoins2Mois = new Salaire();
        salaireMoisMoins2Mois.setMontantNet(0);
        salaireMoisMoins2Mois.setMontantBrut(0); 
        salaireAvantPeriodeSimulationMoisMoins2Mois.setSalaire(salaireMoisMoins2Mois);
        salairesAvantPeriodeSimulation.setSalaireMoisMoins2MoisDemandeSimulation(salaireAvantPeriodeSimulationMoisMoins2Mois);
        ressourcesFinancieres.setSalairesAvantPeriodeSimulation(salairesAvantPeriodeSimulation);
        PrestationsPoleEmploi prestationsPoleEmploi = new PrestationsPoleEmploi();
        AllocationASS allocationASS = new AllocationASS();
        allocationASS.setAllocationJournaliereNet(16.89f);
        prestationsPoleEmploi.setAllocationASS(allocationASS);
        ressourcesFinancieres.setPrestationsPoleEmploi(prestationsPoleEmploi);
        demandeurEmploi.setRessourcesFinancieres(ressourcesFinancieres);
        
        FuturTravail futurTravail = new FuturTravail();
        futurTravail.setTypeContrat(TypesContratTravail.CDD.name());
        futurTravail.setNombreMoisContratCDD(3);
        demandeurEmploi.setFuturTravail(futurTravail);
        
        //Lorsque je calcul le nombre de mois éligible à l'ASS
        int nombreMoisEligible = allocationSolidariteSpecifiqueUtile.getNombreMoisEligibles(demandeurEmploi);
        
        //alors la simulation se fait sur 3 mois et le DE a droit à 2 mois d'ASS
        assertThat(nombreMoisEligible).isEqualTo(2);
    }
    
    @Test
    void getNombreMoisEligibleCDD3MoisTest2() throws ParseException {
        
        //Si DE, futur contrat CDD 3 mois et cumulé ASS+Salaire 2 mois sur les 3 derniers mois
        DemandeurEmploi demandeurEmploi = new DemandeurEmploi();
        RessourcesFinancieres ressourcesFinancieres = new RessourcesFinancieres();
        ressourcesFinancieres.setHasTravailleAuCoursDerniersMois(true);
        ressourcesFinancieres.setNombreMoisTravaillesDerniersMois(2);
        SalairesAvantPeriodeSimulation salairesAvantPeriodeSimulation = new SalairesAvantPeriodeSimulation();
        SalaireAvantPeriodeSimulation salaireAvantPeriodeSimulationMoisDemande = new SalaireAvantPeriodeSimulation();
        Salaire salaireMoisDemande = new Salaire();
        salaireMoisDemande.setMontantNet(850);
        salaireMoisDemande.setMontantBrut(1101);
        salaireAvantPeriodeSimulationMoisDemande.setSalaire(salaireMoisDemande);
        salairesAvantPeriodeSimulation.setSalaireMoisDemandeSimulation(salaireAvantPeriodeSimulationMoisDemande);
        SalaireAvantPeriodeSimulation salaireAvantPeriodeSimulationMoisMoins1Mois = new SalaireAvantPeriodeSimulation();
        Salaire salaireMoisMoins1Mois = new Salaire();
        salaireMoisMoins1Mois.setMontantNet(800);
        salaireMoisMoins1Mois.setMontantBrut(1038); 
        salaireAvantPeriodeSimulationMoisMoins1Mois.setSalaire(salaireMoisMoins1Mois);
        salairesAvantPeriodeSimulation.setSalaireMoisMoins1MoisDemandeSimulation(salaireAvantPeriodeSimulationMoisMoins1Mois);
        SalaireAvantPeriodeSimulation salaireAvantPeriodeSimulationMoisMoins2Mois = new SalaireAvantPeriodeSimulation();
        Salaire salaireMoisMoins2Mois = new Salaire();
        salaireMoisMoins2Mois.setMontantNet(0);
        salaireMoisMoins2Mois.setMontantBrut(0); 
        salaireAvantPeriodeSimulationMoisMoins2Mois.setSalaire(salaireMoisMoins2Mois);
        salairesAvantPeriodeSimulation.setSalaireMoisMoins2MoisDemandeSimulation(salaireAvantPeriodeSimulationMoisMoins2Mois);
        ressourcesFinancieres.setSalairesAvantPeriodeSimulation(salairesAvantPeriodeSimulation);
        PrestationsPoleEmploi prestationsPoleEmploi = new PrestationsPoleEmploi();
        AllocationASS allocationASS = new AllocationASS();
        allocationASS.setAllocationJournaliereNet(16.89f);
        prestationsPoleEmploi.setAllocationASS(allocationASS);
        ressourcesFinancieres.setPrestationsPoleEmploi(prestationsPoleEmploi);
        demandeurEmploi.setRessourcesFinancieres(ressourcesFinancieres);
        
        FuturTravail futurTravail = new FuturTravail();
        futurTravail.setTypeContrat(TypesContratTravail.CDD.name());
        futurTravail.setNombreMoisContratCDD(3);
        demandeurEmploi.setFuturTravail(futurTravail);
        
        //Lorsque je calcul le nombre de mois éligible à l'ASS
        int nombreMoisEligible = allocationSolidariteSpecifiqueUtile.getNombreMoisEligibles(demandeurEmploi);
        
        //alors la simulation se fait sur 3 mois et le DE a droit à 1 mois d'ASS
        assertThat(nombreMoisEligible).isEqualTo(1);
    }
    
    
    @Test
    void getNombreMoisEligibleCDD3MoisTest3() throws ParseException {
        
        //Si DE, futur contrat CDD 3 mois et cumulé ASS+Salaire 3 mois sur les 3 derniers mois
        DemandeurEmploi demandeurEmploi = new DemandeurEmploi();
        RessourcesFinancieres ressourcesFinancieres = new RessourcesFinancieres();
        ressourcesFinancieres.setHasTravailleAuCoursDerniersMois(true);
        ressourcesFinancieres.setNombreMoisTravaillesDerniersMois(3);
        SalairesAvantPeriodeSimulation salairesAvantPeriodeSimulation = new SalairesAvantPeriodeSimulation();
        SalaireAvantPeriodeSimulation salaireAvantPeriodeSimulationMoisDemande = new SalaireAvantPeriodeSimulation();
        Salaire salaireMoisDemande = new Salaire();
        salaireMoisDemande.setMontantNet(850);
        salaireMoisDemande.setMontantBrut(1101);
        salaireAvantPeriodeSimulationMoisDemande.setSalaire(salaireMoisDemande);
        salairesAvantPeriodeSimulation.setSalaireMoisDemandeSimulation(salaireAvantPeriodeSimulationMoisDemande);
        SalaireAvantPeriodeSimulation salaireAvantPeriodeSimulationMoisMoins1Mois = new SalaireAvantPeriodeSimulation();
        Salaire salaireMoisMoins1Mois = new Salaire();
        salaireMoisMoins1Mois.setMontantNet(800);
        salaireMoisMoins1Mois.setMontantBrut(1038); 
        salaireAvantPeriodeSimulationMoisMoins1Mois.setSalaire(salaireMoisMoins1Mois);
        salairesAvantPeriodeSimulation.setSalaireMoisMoins1MoisDemandeSimulation(salaireAvantPeriodeSimulationMoisMoins1Mois);
        SalaireAvantPeriodeSimulation salaireAvantPeriodeSimulationMoisMoins2Mois = new SalaireAvantPeriodeSimulation();
        Salaire salaireMoisMoins2Mois = new Salaire();
        salaireMoisMoins2Mois.setMontantNet(800);
        salaireMoisMoins2Mois.setMontantBrut(1038); 
        salaireAvantPeriodeSimulationMoisMoins2Mois.setSalaire(salaireMoisMoins2Mois);
        salairesAvantPeriodeSimulation.setSalaireMoisMoins2MoisDemandeSimulation(salaireAvantPeriodeSimulationMoisMoins2Mois);
        ressourcesFinancieres.setSalairesAvantPeriodeSimulation(salairesAvantPeriodeSimulation);
        PrestationsPoleEmploi prestationsPoleEmploi = new PrestationsPoleEmploi();
        AllocationASS allocationASS = new AllocationASS();
        allocationASS.setAllocationJournaliereNet(16.89f);
        prestationsPoleEmploi.setAllocationASS(allocationASS);
        ressourcesFinancieres.setPrestationsPoleEmploi(prestationsPoleEmploi);
        demandeurEmploi.setRessourcesFinancieres(ressourcesFinancieres);
        
        FuturTravail futurTravail = new FuturTravail();
        futurTravail.setTypeContrat(TypesContratTravail.CDD.name());
        futurTravail.setNombreMoisContratCDD(3);
        demandeurEmploi.setFuturTravail(futurTravail);
        
        //Lorsque je calcul le nombre de mois éligible à l'ASS
        int nombreMoisEligible = allocationSolidariteSpecifiqueUtile.getNombreMoisEligibles(demandeurEmploi);
        
        //alors la simulation se fait sur 3 mois et le DE a droit à 0 mois d'ASS
        assertThat(nombreMoisEligible).isEqualTo(0);
    }
    
    /***** tests nombre mois eligible ASS si CDD de 4 mois *****/
    
    @Test
    void getNombreMoisEligibleCDD4MoisTest() throws ParseException {
        
        //Si DE, futur contrat CDD 4 mois et cumulé ASS+Salaire 0 mois sur les 3 derniers mois
        DemandeurEmploi demandeurEmploi = new DemandeurEmploi();
        RessourcesFinancieres ressourcesFinancieres = new RessourcesFinancieres();
        ressourcesFinancieres.setHasTravailleAuCoursDerniersMois(false);
        PrestationsPoleEmploi prestationsPoleEmploi = new PrestationsPoleEmploi();
        AllocationASS allocationASS = new AllocationASS();
        allocationASS.setAllocationJournaliereNet(16.89f);
        prestationsPoleEmploi.setAllocationASS(allocationASS);
        ressourcesFinancieres.setPrestationsPoleEmploi(prestationsPoleEmploi);
        demandeurEmploi.setRessourcesFinancieres(ressourcesFinancieres);
        
        FuturTravail futurTravail = new FuturTravail();
        futurTravail.setTypeContrat(TypesContratTravail.CDD.name());
        futurTravail.setNombreMoisContratCDD(4);
        demandeurEmploi.setFuturTravail(futurTravail);
        
        //Lorsque je calcul le nombre de mois éligible à l'ASS
        int nombreMoisEligible = allocationSolidariteSpecifiqueUtile.getNombreMoisEligibles(demandeurEmploi);
        
        //alors la simulation se fait sur 4 mois et le DE a droit à 3 mois d'ASS
        assertThat(nombreMoisEligible).isEqualTo(3);
    }
    
    @Test
    void getNombreMoisEligibleCDD4MoisTest1() throws ParseException {
        
        //Si DE, futur contrat CDD 4 mois et cumulé ASS+Salaire 1 mois sur les 3 derniers mois
        DemandeurEmploi demandeurEmploi = new DemandeurEmploi();
        RessourcesFinancieres ressourcesFinancieres = new RessourcesFinancieres();
        ressourcesFinancieres.setHasTravailleAuCoursDerniersMois(true);
        ressourcesFinancieres.setNombreMoisTravaillesDerniersMois(1);
        SalairesAvantPeriodeSimulation salairesAvantPeriodeSimulation = new SalairesAvantPeriodeSimulation();
        SalaireAvantPeriodeSimulation salaireAvantPeriodeSimulationMoisDemande = new SalaireAvantPeriodeSimulation();
        Salaire salaireMoisDemande = new Salaire();
        salaireMoisDemande.setMontantNet(850);
        salaireMoisDemande.setMontantBrut(1101);
        salaireAvantPeriodeSimulationMoisDemande.setSalaire(salaireMoisDemande);
        salairesAvantPeriodeSimulation.setSalaireMoisDemandeSimulation(salaireAvantPeriodeSimulationMoisDemande);
        SalaireAvantPeriodeSimulation salaireAvantPeriodeSimulationMoisMoins1Mois = new SalaireAvantPeriodeSimulation();
        Salaire salaireMoisMoins1Mois = new Salaire();
        salaireMoisMoins1Mois.setMontantNet(0);
        salaireMoisMoins1Mois.setMontantBrut(0); 
        salaireAvantPeriodeSimulationMoisMoins1Mois.setSalaire(salaireMoisMoins1Mois);
        salairesAvantPeriodeSimulation.setSalaireMoisMoins1MoisDemandeSimulation(salaireAvantPeriodeSimulationMoisMoins1Mois);
        SalaireAvantPeriodeSimulation salaireAvantPeriodeSimulationMoisMoins2Mois = new SalaireAvantPeriodeSimulation();
        Salaire salaireMoisMoins2Mois = new Salaire();
        salaireMoisMoins2Mois.setMontantNet(0);
        salaireMoisMoins2Mois.setMontantBrut(0); 
        salaireAvantPeriodeSimulationMoisMoins2Mois.setSalaire(salaireMoisMoins2Mois);
        salairesAvantPeriodeSimulation.setSalaireMoisMoins2MoisDemandeSimulation(salaireAvantPeriodeSimulationMoisMoins2Mois);
        ressourcesFinancieres.setSalairesAvantPeriodeSimulation(salairesAvantPeriodeSimulation);
        PrestationsPoleEmploi prestationsPoleEmploi = new PrestationsPoleEmploi();
        AllocationASS allocationASS = new AllocationASS();
        allocationASS.setAllocationJournaliereNet(16.89f);
        prestationsPoleEmploi.setAllocationASS(allocationASS);
        ressourcesFinancieres.setPrestationsPoleEmploi(prestationsPoleEmploi);
        demandeurEmploi.setRessourcesFinancieres(ressourcesFinancieres);
        
        FuturTravail futurTravail = new FuturTravail();
        futurTravail.setTypeContrat(TypesContratTravail.CDD.name());
        futurTravail.setNombreMoisContratCDD(4);
        demandeurEmploi.setFuturTravail(futurTravail);
        
        //Lorsque je calcul le nombre de mois éligible à l'ASS
        int nombreMoisEligible = allocationSolidariteSpecifiqueUtile.getNombreMoisEligibles(demandeurEmploi);
        
        //alors la simulation se fait sur 4 mois et  le DE a droit à 2 mois d'ASS
        assertThat(nombreMoisEligible).isEqualTo(2);
    }
    
    @Test
    void getNombreMoisEligibleCDD4MoisTest2() throws ParseException {
        
        //Si DE, futur contrat CDD 4 mois et cumulé ASS+Salaire 2 mois sur les 3 derniers mois
        DemandeurEmploi demandeurEmploi = new DemandeurEmploi();
        RessourcesFinancieres ressourcesFinancieres = new RessourcesFinancieres();
        ressourcesFinancieres.setHasTravailleAuCoursDerniersMois(true);
        ressourcesFinancieres.setNombreMoisTravaillesDerniersMois(2);
        SalairesAvantPeriodeSimulation salairesAvantPeriodeSimulation = new SalairesAvantPeriodeSimulation();
        SalaireAvantPeriodeSimulation salaireAvantPeriodeSimulationMoisDemande = new SalaireAvantPeriodeSimulation();
        Salaire salaireMoisDemande = new Salaire();
        salaireMoisDemande.setMontantNet(850);
        salaireMoisDemande.setMontantBrut(1101);
        salaireAvantPeriodeSimulationMoisDemande.setSalaire(salaireMoisDemande);
        salairesAvantPeriodeSimulation.setSalaireMoisDemandeSimulation(salaireAvantPeriodeSimulationMoisDemande);
        SalaireAvantPeriodeSimulation salaireAvantPeriodeSimulationMoisMoins1Mois = new SalaireAvantPeriodeSimulation();
        Salaire salaireMoisMoins1Mois = new Salaire();
        salaireMoisMoins1Mois.setMontantNet(800);
        salaireMoisMoins1Mois.setMontantBrut(1038); 
        salaireAvantPeriodeSimulationMoisMoins1Mois.setSalaire(salaireMoisMoins1Mois);
        salairesAvantPeriodeSimulation.setSalaireMoisMoins1MoisDemandeSimulation(salaireAvantPeriodeSimulationMoisMoins1Mois);
        SalaireAvantPeriodeSimulation salaireAvantPeriodeSimulationMoisMoins2Mois = new SalaireAvantPeriodeSimulation();
        Salaire salaireMoisMoins2Mois = new Salaire();
        salaireMoisMoins2Mois.setMontantNet(0);
        salaireMoisMoins2Mois.setMontantBrut(0); 
        salaireAvantPeriodeSimulationMoisMoins2Mois.setSalaire(salaireMoisMoins2Mois);
        salairesAvantPeriodeSimulation.setSalaireMoisMoins2MoisDemandeSimulation(salaireAvantPeriodeSimulationMoisMoins2Mois);
        ressourcesFinancieres.setSalairesAvantPeriodeSimulation(salairesAvantPeriodeSimulation);
        PrestationsPoleEmploi prestationsPoleEmploi = new PrestationsPoleEmploi();
        AllocationASS allocationASS = new AllocationASS();
        allocationASS.setAllocationJournaliereNet(16.89f);
        prestationsPoleEmploi.setAllocationASS(allocationASS);
        ressourcesFinancieres.setPrestationsPoleEmploi(prestationsPoleEmploi);
        demandeurEmploi.setRessourcesFinancieres(ressourcesFinancieres);
        
        FuturTravail futurTravail = new FuturTravail();
        futurTravail.setTypeContrat(TypesContratTravail.CDD.name());
        futurTravail.setNombreMoisContratCDD(4);
        demandeurEmploi.setFuturTravail(futurTravail);
        
        //Lorsque je calcul le nombre de mois éligible à l'ASS
        int nombreMoisEligible = allocationSolidariteSpecifiqueUtile.getNombreMoisEligibles(demandeurEmploi);
        
        //alors le DE a droit à 1 mois d'ASS
        assertThat(nombreMoisEligible).isEqualTo(1);
    }
    
    
    @Test
    void getNombreMoisEligibleCDD4MoisTest3() throws ParseException {
        
        //Si DE, futur contrat CDD 4 mois et cumulé ASS+Salaire 3 mois sur les 3 derniers mois
        DemandeurEmploi demandeurEmploi = new DemandeurEmploi();
        RessourcesFinancieres ressourcesFinancieres = new RessourcesFinancieres();
        ressourcesFinancieres.setHasTravailleAuCoursDerniersMois(true);
        ressourcesFinancieres.setNombreMoisTravaillesDerniersMois(3);
        SalairesAvantPeriodeSimulation salairesAvantPeriodeSimulation = new SalairesAvantPeriodeSimulation();
        SalaireAvantPeriodeSimulation salaireAvantPeriodeSimulationMoisDemande = new SalaireAvantPeriodeSimulation();
        Salaire salaireMoisDemande = new Salaire();
        salaireMoisDemande.setMontantNet(850);
        salaireMoisDemande.setMontantBrut(1101);
        salaireAvantPeriodeSimulationMoisDemande.setSalaire(salaireMoisDemande);
        salairesAvantPeriodeSimulation.setSalaireMoisDemandeSimulation(salaireAvantPeriodeSimulationMoisDemande);
        SalaireAvantPeriodeSimulation salaireAvantPeriodeSimulationMoisMoins1Mois = new SalaireAvantPeriodeSimulation();
        Salaire salaireMoisMoins1Mois = new Salaire();
        salaireMoisMoins1Mois.setMontantNet(800);
        salaireMoisMoins1Mois.setMontantBrut(1038); 
        salaireAvantPeriodeSimulationMoisMoins1Mois.setSalaire(salaireMoisMoins1Mois);
        salairesAvantPeriodeSimulation.setSalaireMoisMoins1MoisDemandeSimulation(salaireAvantPeriodeSimulationMoisMoins1Mois);
        SalaireAvantPeriodeSimulation salaireAvantPeriodeSimulationMoisMoins2Mois = new SalaireAvantPeriodeSimulation();
        Salaire salaireMoisMoins2Mois = new Salaire();
        salaireMoisMoins2Mois.setMontantNet(800);
        salaireMoisMoins2Mois.setMontantBrut(1038); 
        salaireAvantPeriodeSimulationMoisMoins2Mois.setSalaire(salaireMoisMoins2Mois);
        salairesAvantPeriodeSimulation.setSalaireMoisMoins2MoisDemandeSimulation(salaireAvantPeriodeSimulationMoisMoins2Mois);
        ressourcesFinancieres.setSalairesAvantPeriodeSimulation(salairesAvantPeriodeSimulation);
        PrestationsPoleEmploi prestationsPoleEmploi = new PrestationsPoleEmploi();
        AllocationASS allocationASS = new AllocationASS();
        allocationASS.setAllocationJournaliereNet(16.89f);
        prestationsPoleEmploi.setAllocationASS(allocationASS);
        ressourcesFinancieres.setPrestationsPoleEmploi(prestationsPoleEmploi);
        demandeurEmploi.setRessourcesFinancieres(ressourcesFinancieres);
        
        FuturTravail futurTravail = new FuturTravail();
        futurTravail.setTypeContrat(TypesContratTravail.CDD.name());
        futurTravail.setNombreMoisContratCDD(4);
        demandeurEmploi.setFuturTravail(futurTravail);
        
        //Lorsque je calcul le nombre de mois éligible à l'ASS
        int nombreMoisEligible = allocationSolidariteSpecifiqueUtile.getNombreMoisEligibles(demandeurEmploi);
        
        //alors la simulation se fait sur 4 mois et le DE a droit à 0 mois d'ASS
        assertThat(nombreMoisEligible).isEqualTo(0);
    }
}
