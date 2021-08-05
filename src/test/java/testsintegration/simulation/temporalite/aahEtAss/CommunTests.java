package testsintegration.simulation.temporalite.aahEtAss;

import static org.mockito.Mockito.doNothing;
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
import fr.poleemploi.estime.commun.enumerations.ParcoursUtilisateur;
import fr.poleemploi.estime.commun.enumerations.TypePopulation;
import fr.poleemploi.estime.commun.enumerations.TypesContratTravail;
import fr.poleemploi.estime.commun.utile.DateUtile;
import fr.poleemploi.estime.commun.utile.SuiviUtilisateurUtile;
import fr.poleemploi.estime.services.ressources.DemandeurEmploi;
import fr.poleemploi.estime.services.ressources.PrestationsFamiliales;
import utile.tests.UtileTests;

public class CommunTests {

    @Autowired
    protected UtileTests utileTests;

    @SpyBean
    protected DateUtile dateUtile;

    @SpyBean
    private SuiviUtilisateurUtile suiviUtilisateurUtile;

    @SpyBean
    private EmploiStoreDevClient detailIndemnisationPoleEmploiClient;

    protected DemandeurEmploi createDemandeurEmploi() throws ParseException {

        boolean isEnCouple = false;
        int nbEnfant = 1;
        DemandeurEmploi demandeurEmploi = utileTests.creerBaseDemandeurEmploi(TypePopulation.AAH_ASS.getLibelle(), isEnCouple, nbEnfant);
        
        demandeurEmploi.getInformationsPersonnelles().setDateNaissance(utileTests.getDate("05-07-1986"));
        demandeurEmploi.getInformationsPersonnelles().setNationalite(Nationalites.FRANCAISE.getValeur());
        demandeurEmploi.getInformationsPersonnelles().setCodePostal("44200");
        
        demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(0).getInformationsPersonnelles().setDateNaissance(utileTests.getDateNaissanceFromAge(9));
        
        demandeurEmploi.getFuturTravail().setTypeContrat(TypesContratTravail.CDI.name());
        demandeurEmploi.getFuturTravail().setNombreHeuresTravailleesSemaine(35);
        demandeurEmploi.getFuturTravail().getSalaire().setMontantNet(940);
        demandeurEmploi.getFuturTravail().getSalaire().setMontantBrut(1200);
        demandeurEmploi.getFuturTravail().setDistanceKmDomicileTravail(80);
        demandeurEmploi.getFuturTravail().setNombreTrajetsDomicileTravail(12);
       
        demandeurEmploi.getRessourcesFinancieres().setHasTravailleAuCoursDerniersMois(false);
        demandeurEmploi.getRessourcesFinancieres().setNombreMoisTravaillesDerniersMois(0);
   
        PrestationsFamiliales prestationsFamiliales = new PrestationsFamiliales();
        prestationsFamiliales.setAllocationsFamiliales(0);
        prestationsFamiliales.setAllocationSoutienFamilial(117);
        prestationsFamiliales.setComplementFamilial(0);   
        demandeurEmploi.getRessourcesFinancieres().getPrestationsCAF().setPrestationsFamiliales(prestationsFamiliales);
        demandeurEmploi.getRessourcesFinancieres().getPrestationsCAF().setAllocationMensuelleNetAAH(900f);
        
        demandeurEmploi.getRessourcesFinancieres().getPrestationsPoleEmploi().getAllocationASS().setAllocationJournaliereNet(16.89f);
        demandeurEmploi.getRessourcesFinancieres().getPrestationsPoleEmploi().getAllocationASS().setDateDerniereOuvertureDroitASS(utileTests.getDate("14-04-2020"));

        return demandeurEmploi;
    }

    protected void initMocks(DemandeurEmploi demandeurEmploi) throws ParseException, JsonIOException,
            JsonSyntaxException, FileNotFoundException, URISyntaxException, JSONException {
        // mock tracer parcours utilisateur
        doNothing().when(suiviUtilisateurUtile).tracerParcoursUtilisateur(demandeurEmploi.getIdPoleEmploi(),
                ParcoursUtilisateur.SIMULATION_EFFECTUEE.getParcours(), demandeurEmploi.getBeneficiaireAidesSociales());

        // mock création date de demande de simulation
        doReturn(utileTests.getDate("20-10-2020")).when(dateUtile).getDateJour();

        // mock retour appel détail indemnisation de l'ESD
        DetailIndemnisationESD detailIndemnisationESD = utileTests
                .creerDetailIndemnisationESD(TypePopulation.AAH_ASS.getLibelle());
        doReturn(detailIndemnisationESD).when(detailIndemnisationPoleEmploiClient)
                .callDetailIndemnisationEndPoint(Mockito.any(String.class));
    }
}
