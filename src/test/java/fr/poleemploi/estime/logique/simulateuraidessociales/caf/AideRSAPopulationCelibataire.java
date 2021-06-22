package fr.poleemploi.estime.logique.simulateuraidessociales.caf;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.time.LocalDate;

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
import fr.poleemploi.estime.services.ressources.BeneficiaireAidesSociales;
import fr.poleemploi.estime.services.ressources.DemandeurEmploi;
import fr.poleemploi.estime.services.ressources.FuturTravail;
import fr.poleemploi.estime.services.ressources.InformationsPersonnelles;
import fr.poleemploi.estime.services.ressources.RessourcesFinancieres;
import fr.poleemploi.estime.services.ressources.Salaire;
import fr.poleemploi.estime.services.ressources.SimulationAidesSociales;
import fr.poleemploi.estime.services.ressources.SituationFamiliale;
import fr.poleemploi.test.utile.TestUtile;


@ContextConfiguration
@SpringBootTest
@TestPropertySource(locations="classpath:application-test.properties")
class AideRSAPopulationCelibataire {
    
    @Autowired
    private OpenFiscaClient openFiscaClient;

    @Autowired
    private TestUtile testUtile;
    
    @Configuration
    @ComponentScan({"fr.poleemploi.test","fr.poleemploi.estime"})
    public static class SpringConfig {

    }
    
    /*********************        période simulation            ****************************
          M1           M2           M3              M4             M5            M6 
      01/02/2021   01/03/2021    01/03/2021     01/04/2021     01/05/2021    01/06/2021
      
    ****************************************************************************************/
    
    @Test
    void calculerRSACelibataireSansEnfant() throws JSONException, ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException {

        // Si DE France Métropolitaine né le 08/01/1979
        // celibataire
        // Contrat 35h CDI 1231€ net / 1583€ brut
        // RSA 500€ - APL 310€
        // déclaration trimestriel M
        boolean logeGratuitement = false;
        LocalDate dateDebutPeriodeSimulee = testUtile.getDate("01-02-2021");
        
        DemandeurEmploi demandeurEmploi =  new DemandeurEmploi();
        FuturTravail futurTravail = new FuturTravail();
        Salaire salaire = new Salaire();
        salaire.setMontantNet(1231);
        salaire.setMontantBrut(1583);
        futurTravail.setSalaire(salaire);
        demandeurEmploi.setFuturTravail(futurTravail);
        
        RessourcesFinancieres ressourcesFinancieres = new RessourcesFinancieres();
        AllocationsCAF allocationsCAF = new AllocationsCAF();        
        allocationsCAF.setAllocationMensuelleNetRSA(500f);
        allocationsCAF.setProchaineDeclarationRSA(0);
        allocationsCAF.setAllocationsLogementMensuellesNetFoyer(creerAllocationsLogementMensuellesNetFoyer(310));
        ressourcesFinancieres.setAllocationsCAF(allocationsCAF);
        demandeurEmploi.setRessourcesFinancieres(ressourcesFinancieres);
        
        demandeurEmploi.setBeneficiaireAidesSociales(creerBeneficiaireRSA());
        
        SituationFamiliale situationFamiliale = new SituationFamiliale();
        situationFamiliale.setIsEnCouple(false);
        situationFamiliale.setIsSeulPlusDe18Mois(true);
        demandeurEmploi.setSituationFamiliale(situationFamiliale);
        
        InformationsPersonnelles informationsPersonnelles = new InformationsPersonnelles();
        informationsPersonnelles.setDateNaissance(testUtile.getDate("08-01-1979"));
        informationsPersonnelles.setIsProprietaireSansPretOuLogeGratuit(logeGratuitement);
        demandeurEmploi.setInformationsPersonnelles(informationsPersonnelles);

        //Lorsque je fais une simulation le 01/02/2021
        SimulationAidesSociales simulationAidesSociales = new SimulationAidesSociales();
        float montantRsaMois3 = openFiscaClient.calculerMontantRSA(simulationAidesSociales, demandeurEmploi, dateDebutPeriodeSimulee, 1);
        //TODO: vérifier montant CAF
        assertThat(montantRsaMois3).isEqualTo(497);       
    }
    
    
    private AllocationsLogementMensuellesNetFoyer creerAllocationsLogementMensuellesNetFoyer(int montant) {
        
        AllocationsLogementMensuellesNetFoyer allocationsLogementMensuellesNetFoyer = new AllocationsLogementMensuellesNetFoyer();
        allocationsLogementMensuellesNetFoyer.setMoisN(montant);
        allocationsLogementMensuellesNetFoyer.setMoisNMoins1(montant);
        allocationsLogementMensuellesNetFoyer.setMoisNMoins2(montant);
        allocationsLogementMensuellesNetFoyer.setMoisNMoins3(montant);
        return allocationsLogementMensuellesNetFoyer;
    }
    
    
    private BeneficiaireAidesSociales creerBeneficiaireRSA() {
        BeneficiaireAidesSociales beneficiaireAidesSociales = new BeneficiaireAidesSociales();
        beneficiaireAidesSociales.setBeneficiaireRSA(true);
        beneficiaireAidesSociales.setBeneficiaireARE(false);
        beneficiaireAidesSociales.setBeneficiaireAAH(false);
        beneficiaireAidesSociales.setBeneficiaireASS(false);
        return beneficiaireAidesSociales;
    }
  
}
