package testsintegration.simulation.temporalite.aah;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;

import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

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
import fr.poleemploi.estime.commun.enumerations.TypesContratTravail;
import fr.poleemploi.estime.commun.utile.DateUtile;
import fr.poleemploi.estime.commun.utile.SuiviUtilisateurUtile;
import fr.poleemploi.estime.services.ressources.AllocationsCAF;
import fr.poleemploi.estime.services.ressources.BeneficiaireAidesSociales;
import fr.poleemploi.estime.services.ressources.DemandeurEmploi;
import fr.poleemploi.estime.services.ressources.FuturTravail;
import fr.poleemploi.estime.services.ressources.InformationsPersonnelles;
import fr.poleemploi.estime.services.ressources.Personne;
import fr.poleemploi.estime.services.ressources.RessourcesFinancieres;
import fr.poleemploi.estime.services.ressources.Salaire;
import fr.poleemploi.estime.services.ressources.SituationFamiliale;
import utile.tests.UtileTests;

public class CommunTests {
    
    @SpyBean
    protected DateUtile dateUtile;        
    
    @Autowired
    private UtileTests utileTests;
    
    @SpyBean
    private SuiviUtilisateurUtile suiviUtilisateurUtile;
    
    @SpyBean
    private EmploiStoreDevClient detailIndemnisationPoleEmploiClient;

    protected DemandeurEmploi createDemandeurEmploi() throws ParseException {
        DemandeurEmploi demandeurEmploi = new DemandeurEmploi();
        demandeurEmploi.setIdPoleEmploi("idPoleEmploi");
        InformationsPersonnelles informationsPersonnelles = new InformationsPersonnelles();
        informationsPersonnelles.setNom("LANCELEUR");
        informationsPersonnelles.setPrenom("Jérémie");
        informationsPersonnelles.setDateNaissance(utileTests.getDate("05-07-1986"));
        informationsPersonnelles.setNationalite(Nationalites.FRANCAISE.getValeur());
        informationsPersonnelles.setCodePostal("44200");
        demandeurEmploi.setInformationsPersonnelles(informationsPersonnelles);

        BeneficiaireAidesSociales beneficiaireAidesSociales = new BeneficiaireAidesSociales();
        beneficiaireAidesSociales.setBeneficiaireAAH(true);
        beneficiaireAidesSociales.setBeneficiaireARE(false);
        beneficiaireAidesSociales.setBeneficiaireASS(false);
        beneficiaireAidesSociales.setBeneficiaireRSA(false);
        demandeurEmploi.setBeneficiaireAidesSociales(beneficiaireAidesSociales);

        SituationFamiliale situationFamiliale = new SituationFamiliale();
        List<Personne> personnesACharge = new ArrayList<Personne>();
        utileTests.createPersonne(personnesACharge, 9);        
        situationFamiliale.setPersonnesACharge(personnesACharge);
        situationFamiliale.setIsEnCouple(false);
        demandeurEmploi.setSituationFamiliale(situationFamiliale);

        FuturTravail futurTravail = new FuturTravail();
        futurTravail.setTypeContrat(TypesContratTravail.CDI.name());
        Salaire salaire = new Salaire();
        salaire.setMontantNet(940);
        salaire.setMontantBrut(1200);        
        futurTravail.setSalaire(salaire);
        futurTravail.setDistanceKmDomicileTravail(80);
        futurTravail.setNombreHeuresTravailleesSemaine(35);
        futurTravail.setNombreTrajetsDomicileTravail(12);
        demandeurEmploi.setFuturTravail(futurTravail);

        RessourcesFinancieres ressourcesFinancieres = new RessourcesFinancieres();
        AllocationsCAF allocationsCAF = new AllocationsCAF();
        allocationsCAF.setAllocationMensuelleNetAAH(900f);
        allocationsCAF.setAllocationsFamilialesMensuellesNetFoyer(90);
        ressourcesFinancieres.setAllocationsCAF(allocationsCAF);
        demandeurEmploi.setRessourcesFinancieres(ressourcesFinancieres); 

        return demandeurEmploi;
    }

    protected void initMocks(DemandeurEmploi demandeurEmploi) throws ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException, JSONException {
        //mock tracer parcours utilisateur 
        doNothing().when(suiviUtilisateurUtile).tracerParcoursUtilisateur(
                demandeurEmploi.getIdPoleEmploi(), 
                null, 
                null, 
                null, 
                ParcoursUtilisateur.SIMULATION_EFFECTUEE.getParcours(),
                demandeurEmploi.getBeneficiaireAidesSociales(),
                false);
        
        //mock création date de demande de simulation
        doReturn(utileTests.getDate("20-10-2020")).when(dateUtile).getDateJour();

        //mock retour appel détail indemnisation de l'ESD 
        DetailIndemnisationESD detailIndemnisationESD = createDetailIndemnisationESDPopulationAAH();        
        doReturn(detailIndemnisationESD).when(detailIndemnisationPoleEmploiClient).callDetailIndemnisationEndPoint(Mockito.any(String.class)); 
    }
    
    private DetailIndemnisationESD createDetailIndemnisationESDPopulationAAH() {
        DetailIndemnisationESD detailIndemnisationESD = new DetailIndemnisationESD();
        detailIndemnisationESD.setBeneficiaireAAH(true);
        detailIndemnisationESD.setBeneficiaireAssuranceChomage(false);
        detailIndemnisationESD.setBeneficiairePrestationSolidarite(false);
        detailIndemnisationESD.setBeneficiaireRSA(false);
        return detailIndemnisationESD;
    }
}
