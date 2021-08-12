package testsunitaires.logique.simulateur.prestationssociales.poleemploi.utile;

import static org.assertj.core.api.Assertions.assertThat;

import java.text.ParseException;
import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import fr.poleemploi.estime.commun.enumerations.MessagesInformatifs;
import fr.poleemploi.estime.logique.simulateur.aides.poleemploi.utile.AllocationSolidariteSpecifiqueUtile;
import fr.poleemploi.estime.services.ressources.Aide;
import fr.poleemploi.estime.services.ressources.AidesPoleEmploi;
import fr.poleemploi.estime.services.ressources.AllocationASS;
import fr.poleemploi.estime.services.ressources.DemandeurEmploi;
import fr.poleemploi.estime.services.ressources.RessourcesFinancieres;
import utile.tests.UtileTests;


@ContextConfiguration
@SpringBootTest
@TestPropertySource(locations="classpath:application-test.properties")
class AllocationSolidariteSpecifiqueUtileTestsPart2 {

    @Autowired
    private AllocationSolidariteSpecifiqueUtile allocationSolidariteSpecifiqueUtile;

    @Autowired
    private UtileTests testUtile;
    
    private LocalDate dateDebutSimulation;
    
    @Configuration
    @ComponentScan({"utile.tests","fr.poleemploi.estime"})
    public static class SpringConfig {

    }
    
    @BeforeEach
    void initBeforeTest() throws ParseException {        
        dateDebutSimulation = testUtile.getDate("01-11-2020");
    }
    
    
    /***********  tests du calcul montant ASS **********/
    
    @Test
    void calculerMontantTest1() throws ParseException {
        
        //Si DE avec montant ASS journalier net = 16,89€
        //Mois simulé novembre 2021 (30 jours) 
        //Date derniere ouverture droit 14/04/2020 soit date fin droit 14/10/2020 (avant 3ème mois simulé)
        
        DemandeurEmploi demandeurEmploi = new DemandeurEmploi();
        RessourcesFinancieres ressourcesFinancieres = new RessourcesFinancieres();
        AidesPoleEmploi aidesPoleEmploi = new AidesPoleEmploi();
        AllocationASS allocationASS = new AllocationASS();
        allocationASS.setAllocationJournaliereNet(16.89f);
        allocationASS.setDateDerniereOuvertureDroit(testUtile.getDate("14-04-2020"));
        aidesPoleEmploi.setAllocationASS(allocationASS);
        ressourcesFinancieres.setAidesPoleEmploi(aidesPoleEmploi);
        demandeurEmploi.setRessourcesFinancieres(ressourcesFinancieres);
        
        String dateMoisSimuleJourMoisString = "01-11-2021";
        LocalDate dateMoisSimuleJourMoisDroitASS = testUtile.getDate(dateMoisSimuleJourMoisString);
        
        //Lorsque je calcul le montant de l'ASS sur le mois total 
        Optional<Aide> ass = allocationSolidariteSpecifiqueUtile.simulerAide(demandeurEmploi, dateMoisSimuleJourMoisDroitASS, dateDebutSimulation);

        //alors 
        //le montant de l'ASS sur le mois de novembre 2021 est de 506€
        assertThat(ass.get().getMontant()).isEqualTo(506);
        //le message d'alerte sur le renouvellement de l'aide est présent
        assertThat(ass.get().getMessageAlerte()).isEqualTo(MessagesInformatifs.ASS_DEMANDE_RENOUVELLEMENT.getMessage());
    }
    
    @Test
    void calculerMontantTest2() throws ParseException {
        
        //Si DE avec montant ASS journalier net = 16,89€
        //Mois simulé décembre 2021 (31 jours) 
        //Date derniere ouverture droit 14/04/2020 soit date fin droit 14/10/2020 (avant 3ème mois simulé)
        
        DemandeurEmploi demandeurEmploi = new DemandeurEmploi();
        RessourcesFinancieres ressourcesFinancieres = new RessourcesFinancieres();
        AidesPoleEmploi aidesPoleEmploi = new AidesPoleEmploi();
        AllocationASS allocationASS = new AllocationASS();
        allocationASS.setAllocationJournaliereNet(16.89f);
        allocationASS.setDateDerniereOuvertureDroit(testUtile.getDate("14-04-2020"));
        aidesPoleEmploi.setAllocationASS(allocationASS);
        ressourcesFinancieres.setAidesPoleEmploi(aidesPoleEmploi);
        demandeurEmploi.setRessourcesFinancieres(ressourcesFinancieres);
        
        String dateMoisSimuleJourMoisString = "01-12-2021";
        LocalDate dateMoisSimuleJourMoisDroitASS = testUtile.getDate(dateMoisSimuleJourMoisString);
        
        //Lorsque je calcul le montant de l'ASS sur le mois total 
        Optional<Aide> ass = allocationSolidariteSpecifiqueUtile.simulerAide(demandeurEmploi, dateMoisSimuleJourMoisDroitASS, dateDebutSimulation);

        //alors le montant de l'ASS sur le mois de décembre 2021 est de 523€
        assertThat(ass.get().getMontant()).isEqualTo(523);
        //le message d'alerte sur le renouvellement de l'aide est présent
        assertThat(ass.get().getMessageAlerte()).isEqualTo(MessagesInformatifs.ASS_DEMANDE_RENOUVELLEMENT.getMessage());
    }
    
    @Test
    void calculerMontantTest3() throws ParseException {
        
        //Si DE avec montant ASS journalier net = 16,89€ 
        //Mois simulé janvier 2021 (31 jours) 
        //Date derniere ouverture droit 14/09/2020 soit date fin droit 14/03/2021 (après 3ème mois simulé)
        
        DemandeurEmploi demandeurEmploi = new DemandeurEmploi();
        RessourcesFinancieres ressourcesFinancieres = new RessourcesFinancieres();
        AidesPoleEmploi aidesPoleEmploi = new AidesPoleEmploi();
        AllocationASS allocationASS = new AllocationASS();
        allocationASS.setAllocationJournaliereNet(16.89f);
        allocationASS.setDateDerniereOuvertureDroit(testUtile.getDate("14-09-2020"));
        aidesPoleEmploi.setAllocationASS(allocationASS);
        ressourcesFinancieres.setAidesPoleEmploi(aidesPoleEmploi);
        demandeurEmploi.setRessourcesFinancieres(ressourcesFinancieres);
        
        String dateMoisSimuleJourMoisString = "01-01-2021";
        LocalDate dateMoisSimuleJourMoisDroitASS = testUtile.getDate(dateMoisSimuleJourMoisString);

        //Lorsque je calcul le montant de l'ASS sur le mois total 
         Optional<Aide> ass = allocationSolidariteSpecifiqueUtile.simulerAide(demandeurEmploi, dateMoisSimuleJourMoisDroitASS, dateDebutSimulation);

        //alors 
        //le montant de l'ASS sur le mois de janvier 2021 est de 523€
        assertThat(ass.get().getMontant()).isEqualTo(523);
        //le message d'alerte sur le renouvellement de l'aide n'est pas présent
        assertThat(ass.get().getMessageAlerte()).isNull();
    }
    
    @Test
    void calculerMontantTest4() throws ParseException {
        
        //Si DE avec montant ASS journalier net = 16,89€
        //Mois simulé février 2021 (28 jours) 
        //Date derniere ouverture droit 14/09/2020 soit date fin droit 14/03/2021 (après 3ème mois simulé)
        
        DemandeurEmploi demandeurEmploi = new DemandeurEmploi();
        RessourcesFinancieres ressourcesFinancieres = new RessourcesFinancieres();
        AidesPoleEmploi aidesPoleEmploi = new AidesPoleEmploi();
        AllocationASS allocationASS = new AllocationASS();
        allocationASS.setAllocationJournaliereNet(16.89f);
        allocationASS.setDateDerniereOuvertureDroit(testUtile.getDate("14-09-2020"));
        aidesPoleEmploi.setAllocationASS(allocationASS);
        ressourcesFinancieres.setAidesPoleEmploi(aidesPoleEmploi);
        demandeurEmploi.setRessourcesFinancieres(ressourcesFinancieres);
        
        String dateMoisSimuleJourMoisString = "01-02-2021";
        LocalDate dateMoisSimuleJourMoisDroitASS = testUtile.getDate(dateMoisSimuleJourMoisString);
        
        //Lorsque je calcul le montant de l'ASS sur le mois total 
        Optional<Aide> ass = allocationSolidariteSpecifiqueUtile.simulerAide(demandeurEmploi, dateMoisSimuleJourMoisDroitASS, dateDebutSimulation);

        //alors 
        //le montant de l'ASS sur le mois de février 2021 est de 472€
        assertThat(ass.get().getMontant()).isEqualTo(472);
        //le message d'alerte sur le renouvellement de l'aide n'est pas présent
        assertThat(ass.get().getMessageAlerte()).isNull();;
    }
}
