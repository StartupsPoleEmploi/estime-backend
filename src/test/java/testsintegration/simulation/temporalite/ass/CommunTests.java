package testsintegration.simulation.temporalite.ass;

import static org.mockito.Mockito.doReturn;

import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.text.ParseException;

import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.test.mock.mockito.SpyBean;

import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import fr.poleemploi.estime.clientsexternes.emploistoredev.EmploiStoreDevClient;
import fr.poleemploi.estime.clientsexternes.emploistoredev.ressources.DetailIndemnisationESD;
import fr.poleemploi.estime.commun.enumerations.Nationalites;
import fr.poleemploi.estime.commun.enumerations.TypePopulation;
import fr.poleemploi.estime.commun.enumerations.TypesContratTravail;
import fr.poleemploi.estime.commun.utile.DateUtile;
import fr.poleemploi.estime.services.ressources.AllocationsCAF;
import fr.poleemploi.estime.services.ressources.DemandeurEmploi;
import utile.tests.UtileTests;

public class CommunTests {
    
    @Autowired
    private UtileTests utileTests;

    @SpyBean
    private EmploiStoreDevClient detailIndemnisationPoleEmploiClient;

    @SpyBean
    protected DateUtile dateUtile;  
   

    protected DemandeurEmploi createDemandeurEmploi() throws ParseException {
        boolean isEnCouple = false;
        int nbEnfant = 1;
        DemandeurEmploi demandeurEmploi = utileTests.creerBaseDemandeurEmploi(TypePopulation.ASS.getLibelle(), isEnCouple, nbEnfant);
        demandeurEmploi.getInformationsPersonnelles().setDateNaissance(utileTests.getDate("05-07-1986"));
        demandeurEmploi.getInformationsPersonnelles().setNationalite(Nationalites.FRANCAISE.getValeur());
        demandeurEmploi.getInformationsPersonnelles().setCodePostal("44200");
        demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(0).getInformationsPersonnelles().setDateNaissance(utileTests.getDateNaissanceFromAge(9));
        demandeurEmploi.getFuturTravail().setTypeContrat(TypesContratTravail.CDI.name());
        demandeurEmploi.getFuturTravail().setNombreHeuresTravailleesSemaine(20);
        demandeurEmploi.getFuturTravail().getSalaire().setMontantNet(1245);
        demandeurEmploi.getFuturTravail().getSalaire().setMontantBrut(1600);
        demandeurEmploi.getFuturTravail().setDistanceKmDomicileTravail(80);
        demandeurEmploi.getFuturTravail().setNombreTrajetsDomicileTravail(12);
        demandeurEmploi.getRessourcesFinancieres().setHasTravailleAuCoursDerniersMois(false);
        demandeurEmploi.getRessourcesFinancieres().getAllocationsPoleEmploi().setAllocationJournaliereNet(16.89f);   
        demandeurEmploi.getRessourcesFinancieres().getAllocationsPoleEmploi().setDateDerniereOuvertureDroitASS(utileTests.getDate("14-04-2020"));
        AllocationsCAF allocationsCAF = new AllocationsCAF();
        allocationsCAF.setAllocationsFamilialesMensuellesNetFoyer(90);
        demandeurEmploi.getRessourcesFinancieres().setAllocationsCAF(allocationsCAF);

        return demandeurEmploi;
    }

    protected void initMocks() throws ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException, JSONException {
        //mock création date de demande de simulation
        doReturn(utileTests.getDate("20-10-2020")).when(dateUtile).getDateJour();

        //mock retour appel détail indemnisation de l'ESD 
        DetailIndemnisationESD detailIndemnisationESD = utileTests.creerDetailIndemnisationESD(TypePopulation.ASS.getLibelle());         
        doReturn(detailIndemnisationESD).when(detailIndemnisationPoleEmploiClient).callDetailIndemnisationEndPoint(Mockito.any(String.class)); 
    }
}
