package testsintegration.simulation.temporalite.are;

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

import fr.poleemploi.estime.clientsexternes.poleemploiio.PoleEmploiIOClient;
import fr.poleemploi.estime.clientsexternes.poleemploiio.ressources.DetailIndemnisationPEIO;
import fr.poleemploi.estime.commun.enumerations.Nationalites;
import fr.poleemploi.estime.commun.enumerations.ParcoursUtilisateur;
import fr.poleemploi.estime.commun.enumerations.TypePopulation;
import fr.poleemploi.estime.commun.enumerations.TypesContratTravail;
import fr.poleemploi.estime.commun.utile.DateUtile;
import fr.poleemploi.estime.commun.utile.SuiviUtilisateurUtile;
import fr.poleemploi.estime.services.ressources.AllocationARE;
import fr.poleemploi.estime.services.ressources.DemandeurEmploi;
import fr.poleemploi.estime.services.ressources.PeConnectAuthorization;
import fr.poleemploi.estime.services.ressources.Salaire;
import utile.tests.Utile;

public class Commun {

    @Autowired
    protected Utile utile;

    @SpyBean
    protected DateUtile dateUtile;        
    
    @SpyBean
    private SuiviUtilisateurUtile suiviUtilisateurUtile;
    
    @SpyBean
    private PoleEmploiIOClient detailIndemnisationPoleEmploiClient;
    
    private final String accessToken ="I892kTPmjDJVxi7oIrdaaVr7oTE";
    
    protected DemandeurEmploi createDemandeurEmploi(int prochaineDeclarationTrimestrielle) throws ParseException {
    	PeConnectAuthorization peConnectAuthorization = new PeConnectAuthorization();
    	peConnectAuthorization.setAccessToken(this.accessToken);
        boolean isEnCouple = false;
        int nbEnfant = 1;
        DemandeurEmploi demandeurEmploi = utile.creerBaseDemandeurEmploi(TypePopulation.ARE.getLibelle(), isEnCouple, nbEnfant);
        
        demandeurEmploi.getInformationsPersonnelles().setDateNaissance(utile.getDate("05-07-1986"));
        demandeurEmploi.getInformationsPersonnelles().setNationalite(Nationalites.FRANCAISE.getValeur());
        demandeurEmploi.getInformationsPersonnelles().setCodePostal("44200");
        
        demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(0).getInformationsPersonnelles().setDateNaissance(utile.getDateNaissanceFromAge(9));
        
        demandeurEmploi.getFuturTravail().setTypeContrat(TypesContratTravail.CDI.name());
        demandeurEmploi.getFuturTravail().setNombreHeuresTravailleesSemaine(35);
        demandeurEmploi.getFuturTravail().getSalaire().setMontantNet(940);
        demandeurEmploi.getFuturTravail().getSalaire().setMontantBrut(1200);
        demandeurEmploi.getFuturTravail().setDistanceKmDomicileTravail(80);
        demandeurEmploi.getFuturTravail().setNombreTrajetsDomicileTravail(12);
        demandeurEmploi.setPeConnectAuthorization(peConnectAuthorization);       
        Salaire salaire = new Salaire();
        salaire.setMontantBrut(350);
        salaire.setMontantNet(200);
        demandeurEmploi.getRessourcesFinancieres().setSalaire(salaire);
        
        AllocationARE allocationARE = new AllocationARE();
        allocationARE.setAllocationJournaliereNet(15f);
        allocationARE.setAllocationMensuelleNet(150f);
        allocationARE.setConcerneDegressivite(false);
        allocationARE.setNombreJoursRestants(15f);
        allocationARE.setMontantJournalierBrut(27f);
        allocationARE.setSalaireJournalierReferenceBrut(45f);
        demandeurEmploi.getRessourcesFinancieres().getAidesPoleEmploi().setAllocationARE(allocationARE);
    
        return demandeurEmploi;
    }
    
    

    protected void initMocks(DemandeurEmploi demandeurEmploi) throws ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException, JSONException {
        //mock tracer parcours utilisateur 
        doNothing().when(suiviUtilisateurUtile).tracerParcoursUtilisateur(
                demandeurEmploi.getIdPoleEmploi(), 
                ParcoursUtilisateur.SIMULATION_EFFECTUEE.getParcours(),
                demandeurEmploi.getBeneficiaireAides(),
                demandeurEmploi.getInformationsPersonnelles());
        
        //mock création date de demande de simulation
        doReturn(utile.getDate("20-10-2020")).when(dateUtile).getDateJour();

        //mock retour appel détail indemnisation de l'ESD 
        DetailIndemnisationPEIO detailIndemnisationESD = utile.creerDetailIndemnisationPEIO(TypePopulation.AAH.getLibelle());        
        doReturn(detailIndemnisationESD).when(detailIndemnisationPoleEmploiClient).callDetailIndemnisationEndPoint(Mockito.any(String.class)); 
    }    
}

