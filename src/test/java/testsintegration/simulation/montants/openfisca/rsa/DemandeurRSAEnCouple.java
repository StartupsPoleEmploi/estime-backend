package testsintegration.simulation.montants.openfisca.rsa;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import fr.poleemploi.estime.clientsexternes.openfisca.OpenFiscaClient;
import fr.poleemploi.estime.services.ressources.AllocationsCAF;
import fr.poleemploi.estime.services.ressources.AllocationsLogementMensuellesNetFoyer;
import fr.poleemploi.estime.services.ressources.AllocationsPoleEmploi;
import fr.poleemploi.estime.services.ressources.BeneficiaireAidesSociales;
import fr.poleemploi.estime.services.ressources.DemandeurEmploi;
import fr.poleemploi.estime.services.ressources.FuturTravail;
import fr.poleemploi.estime.services.ressources.InformationsPersonnelles;
import fr.poleemploi.estime.services.ressources.Personne;
import fr.poleemploi.estime.services.ressources.RessourcesFinancieres;
import fr.poleemploi.estime.services.ressources.Salaire;
import fr.poleemploi.estime.services.ressources.SimulationAidesSociales;
import fr.poleemploi.estime.services.ressources.SimulationMensuelle;
import fr.poleemploi.estime.services.ressources.SituationFamiliale;
import utile.tests.UtileTests;



@ContextConfiguration
@SpringBootTest
@TestPropertySource(locations="classpath:application-test.properties")
class DemandeurRSAEnCouple extends CommunTests {
    
    @Autowired
    private OpenFiscaClient openFiscaClient;
   
    @Autowired
    private UtileTests utileTests;
    
    @Configuration
    @ComponentScan({"utile.tests","fr.poleemploi.estime"})
    public static class SpringConfig {

    }
    
    @Test
    void calculerRSAEnCoupleConjointAvecSalaireSansEnfant() throws JSONException, ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException {

        // Si DE France Métropolitaine (née le 08011979),
        // en location, nat.Fra, CP 67 000,
        // en couple salaire 1200€ net
        // Contrat 20h 700€, RSA 400€ 80 € APL, 80€ Alloc fam
        float montantRSA = 400.0f;
        float montantASSJournalier = 16.89f;
        float montantASSMois1 = 523.6f;
        float montantASSMois2 = 523.6f;
        int prochaineDeclarationRSA = 3;
        int apl = 80;
        int af = 80;
        boolean logeGratuitement = false;
        boolean seulPlusDe18Mois = false;
        boolean enCouple = true;
        LocalDate dateNaissance = utileTests.getDate("08-01-1979");
        LocalDate dateDebutPeriodeSimulee = utileTests.getDate("01-03-2021");
        LocalDate dateDebutPeriodeSimuleeMois1 = utileTests.getDate("01-03-2021");
        LocalDate dateDebutPeriodeSimuleeMois2 = utileTests.getDate("01-04-2021");
        
        DemandeurEmploi demandeurEmploi =  new DemandeurEmploi();
        FuturTravail futurTravail = new FuturTravail();
        Salaire salaire = new Salaire();
        salaire.setMontantNet(700);
        salaire.setMontantBrut(912);
        futurTravail.setSalaire(salaire);
        demandeurEmploi.setFuturTravail(futurTravail);
        
        RessourcesFinancieres ressourcesFinancieres = new RessourcesFinancieres();
        AllocationsPoleEmploi allocationsPoleEmploi = new AllocationsPoleEmploi();
        allocationsPoleEmploi.setAllocationJournaliereNet(montantASSJournalier);
        ressourcesFinancieres.setAllocationsPoleEmploi(allocationsPoleEmploi);
        AllocationsCAF allocationsCAF = new AllocationsCAF();
        AllocationsLogementMensuellesNetFoyer allocationsLogementMensuellesNetFoyer = new AllocationsLogementMensuellesNetFoyer();
        allocationsLogementMensuellesNetFoyer.setMoisN(apl);
        allocationsLogementMensuellesNetFoyer.setMoisNMoins1(apl);
        allocationsLogementMensuellesNetFoyer.setMoisNMoins2(apl);
        allocationsLogementMensuellesNetFoyer.setMoisNMoins3(apl);
        allocationsCAF.setAllocationsLogementMensuellesNetFoyer(allocationsLogementMensuellesNetFoyer);
        allocationsCAF.setAllocationsFamilialesMensuellesNetFoyer(af);
        allocationsCAF.setAllocationMensuelleNetRSA(montantRSA);
        allocationsCAF.setProchaineDeclarationRSA(prochaineDeclarationRSA);
        ressourcesFinancieres.setAllocationsCAF(allocationsCAF);
        demandeurEmploi.setRessourcesFinancieres(ressourcesFinancieres);
        
        BeneficiaireAidesSociales beneficiaireAidesSociales = new BeneficiaireAidesSociales();
        beneficiaireAidesSociales.setBeneficiaireRSA(true);
        demandeurEmploi.setBeneficiaireAidesSociales(beneficiaireAidesSociales);
        
        SimulationAidesSociales simulationAidesSociales = new SimulationAidesSociales();
        List<SimulationMensuelle> simulationsMensuelles = new ArrayList<>();
        simulationsMensuelles.add(createSimulationMensuelleASSetRSA(montantASSMois1, montantRSA, dateDebutPeriodeSimuleeMois1, false));
        simulationsMensuelles.add(createSimulationMensuelleASSetRSA(montantASSMois2, montantRSA, dateDebutPeriodeSimuleeMois2, true));
        simulationAidesSociales.setSimulationsMensuelles(simulationsMensuelles);
        
        SituationFamiliale situationFamiliale = new SituationFamiliale();
        situationFamiliale.setIsEnCouple(enCouple); 
        Personne conjoint = new Personne();
        RessourcesFinancieres ressourcesFinancieresConjoint = new RessourcesFinancieres();
        Salaire salaireConjoint = new Salaire();
        salaireConjoint.setMontantNet(1200);
        salaireConjoint.setMontantBrut(1544);
        ressourcesFinancieresConjoint.setSalaire(salaireConjoint);
        conjoint.setRessourcesFinancieres(ressourcesFinancieresConjoint);
        situationFamiliale.setConjoint(conjoint);
        situationFamiliale.setIsSeulPlusDe18Mois(seulPlusDe18Mois);
        demandeurEmploi.setSituationFamiliale(situationFamiliale);
        
        InformationsPersonnelles informationsPersonnelles = new InformationsPersonnelles();
        informationsPersonnelles.setDateNaissance(dateNaissance);
        informationsPersonnelles.setIsProprietaireSansPretOuLogeGratuit(logeGratuitement);
        demandeurEmploi.setInformationsPersonnelles(informationsPersonnelles);

        //Lorsque je fais une simulation le 01/02/2021
        float montantRsaMois3 = openFiscaClient.calculerMontantRSA(simulationAidesSociales, demandeurEmploi, dateDebutPeriodeSimulee, 3);
            
        assertThat(montantRsaMois3).isEqualTo(0);       
    }
    
    @Test
    void calculerRSAEnCoupleConjointAvecSalaire1Enfant() throws JSONException, ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException {

        // Si DE France Métropolitaine (née le 08011979),
        // en location, nat.Fra, CP 67 000,
        // en couple salaire 1200€ net
        // 1 enfant 01/02/2014 Contrat 20h 700€, RSA 400€ 80 € APL, 80€ Alloc fam
        float montantRSA = 400.0f;
        float montantASSJournalier = 16.89f;
        float montantASSMois1 = 523.6f;
        float montantASSMois2 = 523.6f;
        int prochaineDeclarationRSA = 3;
        int apl = 80;
        int af = 80;
        boolean logeGratuitement = false;
        boolean seulPlusDe18Mois = false;
        boolean enCouple = true;
        int agePersonneACharge = 6;
        LocalDate dateNaissance = utileTests.getDate("08-01-1979");
        LocalDate dateDebutPeriodeSimulee = utileTests.getDate("01-03-2021");
        LocalDate dateDebutPeriodeSimuleeMois1 = utileTests.getDate("01-03-2021");
        LocalDate dateDebutPeriodeSimuleeMois2 = utileTests.getDate("01-04-2021");
        
        DemandeurEmploi demandeurEmploi =  new DemandeurEmploi();
        FuturTravail futurTravail = new FuturTravail();
        Salaire salaire = new Salaire();
        salaire.setMontantNet(700);
        salaire.setMontantBrut(912);
        futurTravail.setSalaire(salaire);
        demandeurEmploi.setFuturTravail(futurTravail);
        
        RessourcesFinancieres ressourcesFinancieres = new RessourcesFinancieres();
        AllocationsPoleEmploi allocationsPoleEmploi = new AllocationsPoleEmploi();
        allocationsPoleEmploi.setAllocationJournaliereNet(montantASSJournalier);
        ressourcesFinancieres.setAllocationsPoleEmploi(allocationsPoleEmploi);
        AllocationsCAF allocationsCAF = new AllocationsCAF();
        AllocationsLogementMensuellesNetFoyer allocationsLogementMensuellesNetFoyer = new AllocationsLogementMensuellesNetFoyer();
        allocationsLogementMensuellesNetFoyer.setMoisN(apl);
        allocationsLogementMensuellesNetFoyer.setMoisNMoins1(apl);
        allocationsLogementMensuellesNetFoyer.setMoisNMoins2(apl);
        allocationsLogementMensuellesNetFoyer.setMoisNMoins3(apl);
        allocationsCAF.setAllocationsLogementMensuellesNetFoyer(allocationsLogementMensuellesNetFoyer);
        allocationsCAF.setAllocationsFamilialesMensuellesNetFoyer(af);
        allocationsCAF.setAllocationMensuelleNetRSA(montantRSA);
        allocationsCAF.setProchaineDeclarationRSA(prochaineDeclarationRSA);
        ressourcesFinancieres.setAllocationsCAF(allocationsCAF);
        demandeurEmploi.setRessourcesFinancieres(ressourcesFinancieres);
        
        BeneficiaireAidesSociales beneficiaireAidesSociales = new BeneficiaireAidesSociales();
        beneficiaireAidesSociales.setBeneficiaireRSA(true);
        demandeurEmploi.setBeneficiaireAidesSociales(beneficiaireAidesSociales);
        
        SimulationAidesSociales simulationAidesSociales = new SimulationAidesSociales();
        List<SimulationMensuelle> simulationsMensuelles = new ArrayList<>();
        simulationsMensuelles.add(createSimulationMensuelleASSetRSA(montantASSMois1, montantRSA, dateDebutPeriodeSimuleeMois1, false));
        simulationsMensuelles.add(createSimulationMensuelleASSetRSA(montantASSMois2, montantRSA, dateDebutPeriodeSimuleeMois2, true));
        simulationAidesSociales.setSimulationsMensuelles(simulationsMensuelles);
        
        SituationFamiliale situationFamiliale = new SituationFamiliale();
        List<Personne> personnesACharge = new ArrayList<Personne>();
        utileTests.createPersonne(personnesACharge, agePersonneACharge);
        situationFamiliale.setPersonnesACharge(personnesACharge);
        situationFamiliale.setIsEnCouple(enCouple); 
        Personne conjoint = new Personne();
        RessourcesFinancieres ressourcesFinancieresConjoint = new RessourcesFinancieres();
        Salaire salaireConjoint = new Salaire();
        salaireConjoint.setMontantNet(1200);
        salaireConjoint.setMontantBrut(1544);
        ressourcesFinancieresConjoint.setSalaire(salaireConjoint);
        conjoint.setRessourcesFinancieres(ressourcesFinancieresConjoint);
        situationFamiliale.setConjoint(conjoint);
        situationFamiliale.setIsSeulPlusDe18Mois(seulPlusDe18Mois);
        demandeurEmploi.setSituationFamiliale(situationFamiliale);
        
        InformationsPersonnelles informationsPersonnelles = new InformationsPersonnelles();
        informationsPersonnelles.setDateNaissance(dateNaissance);
        informationsPersonnelles.setIsProprietaireSansPretOuLogeGratuit(logeGratuitement);
        demandeurEmploi.setInformationsPersonnelles(informationsPersonnelles);

        //Lorsque je fais une simulation le 01/02/2021
        float montantRsaMois3 = openFiscaClient.calculerMontantRSA(simulationAidesSociales, demandeurEmploi, dateDebutPeriodeSimulee, 3);
            
        assertThat(montantRsaMois3).isEqualTo(0);       
    }
    
    
    @Test
    void calculerRSAEnCoupleConjointSansRessourceSansEnfant() throws JSONException, ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException {

        // Si DE France Métropolitaine (née le 08011979),
        // en location, nat.Fra, CP 67 000,
        // en couple conjoint sans ressource
        // Contrat 20h 700€, RSA 400€ 80 € APL, 80€ Alloc fam
        float montantRSA = 400.0f;
        float montantASSJournalier = 16.89f;
        float montantASSMois1 = 523.6f;
        float montantASSMois2 = 523.6f;
        int prochaineDeclarationRSA = 3;
        int apl = 80;
        int af = 80;
        boolean logeGratuitement = false;
        boolean seulPlusDe18Mois = false;
        boolean enCouple = true;
        LocalDate dateNaissance = utileTests.getDate("08-01-1979");
        LocalDate dateDebutPeriodeSimulee = utileTests.getDate("01-03-2021");
        LocalDate dateDebutPeriodeSimuleeMois1 = utileTests.getDate("01-03-2021");
        LocalDate dateDebutPeriodeSimuleeMois2 = utileTests.getDate("01-04-2021");
        
        DemandeurEmploi demandeurEmploi =  new DemandeurEmploi();
        FuturTravail futurTravail = new FuturTravail();
        Salaire salaire = new Salaire();
        salaire.setMontantNet(700);
        salaire.setMontantBrut(912);
        futurTravail.setSalaire(salaire);
        demandeurEmploi.setFuturTravail(futurTravail);
        
        RessourcesFinancieres ressourcesFinancieres = new RessourcesFinancieres();
        AllocationsPoleEmploi allocationsPoleEmploi = new AllocationsPoleEmploi();
        allocationsPoleEmploi.setAllocationJournaliereNet(montantASSJournalier);
        ressourcesFinancieres.setAllocationsPoleEmploi(allocationsPoleEmploi);
        AllocationsCAF allocationsCAF = new AllocationsCAF();
        AllocationsLogementMensuellesNetFoyer allocationsLogementMensuellesNetFoyer = new AllocationsLogementMensuellesNetFoyer();
        allocationsLogementMensuellesNetFoyer.setMoisN(apl);
        allocationsLogementMensuellesNetFoyer.setMoisNMoins1(apl);
        allocationsLogementMensuellesNetFoyer.setMoisNMoins2(apl);
        allocationsLogementMensuellesNetFoyer.setMoisNMoins3(apl);
        allocationsCAF.setAllocationsLogementMensuellesNetFoyer(allocationsLogementMensuellesNetFoyer);
        allocationsCAF.setAllocationsFamilialesMensuellesNetFoyer(af);
        allocationsCAF.setAllocationMensuelleNetRSA(montantRSA);
        allocationsCAF.setProchaineDeclarationRSA(prochaineDeclarationRSA);
        ressourcesFinancieres.setAllocationsCAF(allocationsCAF);
        demandeurEmploi.setRessourcesFinancieres(ressourcesFinancieres);
        
        BeneficiaireAidesSociales beneficiaireAidesSociales = new BeneficiaireAidesSociales();
        beneficiaireAidesSociales.setBeneficiaireRSA(true);
        demandeurEmploi.setBeneficiaireAidesSociales(beneficiaireAidesSociales);
        
        SimulationAidesSociales simulationAidesSociales = new SimulationAidesSociales();
        List<SimulationMensuelle> simulationsMensuelles = new ArrayList<>();
        simulationsMensuelles.add(createSimulationMensuelleASSetRSA(montantASSMois1, montantRSA, dateDebutPeriodeSimuleeMois1, false));
        simulationsMensuelles.add(createSimulationMensuelleASSetRSA(montantASSMois2, montantRSA, dateDebutPeriodeSimuleeMois2, true));
        simulationAidesSociales.setSimulationsMensuelles(simulationsMensuelles);
        
        SituationFamiliale situationFamiliale = new SituationFamiliale();
        situationFamiliale.setIsEnCouple(enCouple); 
        Personne conjoint = new Personne();
        situationFamiliale.setConjoint(conjoint);
        situationFamiliale.setIsSeulPlusDe18Mois(seulPlusDe18Mois);
        demandeurEmploi.setSituationFamiliale(situationFamiliale);
        
        InformationsPersonnelles informationsPersonnelles = new InformationsPersonnelles();
        informationsPersonnelles.setDateNaissance(dateNaissance);
        informationsPersonnelles.setIsProprietaireSansPretOuLogeGratuit(logeGratuitement);
        demandeurEmploi.setInformationsPersonnelles(informationsPersonnelles);

        //Lorsque je fais une simulation le 01/02/2021
        float montantRsaMois3 = openFiscaClient.calculerMontantRSA(simulationAidesSociales, demandeurEmploi, dateDebutPeriodeSimulee, 3);

        //TODO: vérifier montant CAF
        assertThat(montantRsaMois3).isEqualTo(318);       
    }
    
    @Test
    void calculerRSAEnCoupleConjointSansRessource1Enfant() throws JSONException, ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException {

        // Si DE France Métropolitaine (née le 08011979),
        // en location, nat.Fra, CP 67 000,
        // en couple conjoint sans ressource
        // 1 enfant 01/02/2014 Contrat 20h 700€, RSA 400€ 80 € APL, 80€ Alloc fam
        float montantRSA = 400.0f;
        float montantASSJournalier = 16.89f;
        float montantASSMois1 = 523.6f;
        float montantASSMois2 = 523.6f;
        int prochaineDeclarationRSA = 3;
        int apl = 80;
        int af = 80;
        boolean logeGratuitement = false;
        boolean seulPlusDe18Mois = false;
        boolean enCouple = true;
        int agePersonneACharge = 6;
        LocalDate dateNaissance = utileTests.getDate("08-01-1979");
        LocalDate dateDebutPeriodeSimulee = utileTests.getDate("01-03-2021");
        LocalDate dateDebutPeriodeSimuleeMois1 = utileTests.getDate("01-03-2021");
        LocalDate dateDebutPeriodeSimuleeMois2 = utileTests.getDate("01-04-2021");
        
        DemandeurEmploi demandeurEmploi =  new DemandeurEmploi();
        FuturTravail futurTravail = new FuturTravail();
        Salaire salaire = new Salaire();
        salaire.setMontantNet(700);
        salaire.setMontantBrut(912);
        futurTravail.setSalaire(salaire);
        demandeurEmploi.setFuturTravail(futurTravail);
        
        RessourcesFinancieres ressourcesFinancieres = new RessourcesFinancieres();
        AllocationsPoleEmploi allocationsPoleEmploi = new AllocationsPoleEmploi();
        allocationsPoleEmploi.setAllocationJournaliereNet(montantASSJournalier);
        ressourcesFinancieres.setAllocationsPoleEmploi(allocationsPoleEmploi);
        AllocationsCAF allocationsCAF = new AllocationsCAF();
        allocationsCAF.setAllocationsLogementMensuellesNetFoyer(creerAllocationsLogementMensuellesNetFoyer(apl));
        allocationsCAF.setAllocationsFamilialesMensuellesNetFoyer(af);
        allocationsCAF.setAllocationMensuelleNetRSA(montantRSA);
        allocationsCAF.setProchaineDeclarationRSA(prochaineDeclarationRSA);
        ressourcesFinancieres.setAllocationsCAF(allocationsCAF);
        demandeurEmploi.setRessourcesFinancieres(ressourcesFinancieres);
        
        demandeurEmploi.setBeneficiaireAidesSociales(creerBeneficiaireRSA());
        
        SimulationAidesSociales simulationAidesSociales = new SimulationAidesSociales();
        List<SimulationMensuelle> simulationsMensuelles = new ArrayList<>();
        simulationsMensuelles.add(createSimulationMensuelleASSetRSA(montantASSMois1, montantRSA, dateDebutPeriodeSimuleeMois1, false));
        simulationsMensuelles.add(createSimulationMensuelleASSetRSA(montantASSMois2, montantRSA, dateDebutPeriodeSimuleeMois2, true));
        simulationAidesSociales.setSimulationsMensuelles(simulationsMensuelles);
        
        SituationFamiliale situationFamiliale = new SituationFamiliale();
        List<Personne> personnesACharge = new ArrayList<Personne>();
        utileTests.createPersonne(personnesACharge, agePersonneACharge);
        situationFamiliale.setPersonnesACharge(personnesACharge);
        situationFamiliale.setIsEnCouple(enCouple); 
        Personne conjoint = new Personne();
        situationFamiliale.setConjoint(conjoint);
        situationFamiliale.setIsSeulPlusDe18Mois(seulPlusDe18Mois);
        demandeurEmploi.setSituationFamiliale(situationFamiliale);
        
        InformationsPersonnelles informationsPersonnelles = new InformationsPersonnelles();
        informationsPersonnelles.setDateNaissance(dateNaissance);
        informationsPersonnelles.setIsProprietaireSansPretOuLogeGratuit(logeGratuitement);
        demandeurEmploi.setInformationsPersonnelles(informationsPersonnelles);

        //Lorsque je fais une simulation le 01/02/2021
        float montantRsaMois3 = openFiscaClient.calculerMontantRSA(simulationAidesSociales, demandeurEmploi, dateDebutPeriodeSimulee, 3);

        //TODO: vérifier montant CAF
        assertThat(montantRsaMois3).isEqualTo(487);       
    }



}
