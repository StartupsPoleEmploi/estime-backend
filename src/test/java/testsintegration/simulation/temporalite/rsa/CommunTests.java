package testsintegration.simulation.temporalite.rsa;

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
import fr.poleemploi.estime.commun.enumerations.TypesContratTravail;
import fr.poleemploi.estime.commun.utile.DateUtile;
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
    
    @Autowired
    private UtileTests testUtile;

    @SpyBean
    private EmploiStoreDevClient detailIndemnisationPoleEmploiClient;

    @SpyBean
    protected DateUtile dateUtile;
    
    protected DemandeurEmploi createDemandeurEmploi(int prochaineDeclarationRSA) throws ParseException {
        DemandeurEmploi demandeurEmploi = new DemandeurEmploi();

        InformationsPersonnelles informationsPersonnelles = new InformationsPersonnelles();
        informationsPersonnelles.setNom("LANCELEUR");
        informationsPersonnelles.setPrenom("Jérémie");
        informationsPersonnelles.setDateNaissance(testUtile.getDate("05-07-1986"));
        informationsPersonnelles.setNationalite(Nationalites.FRANCAISE.getValeur());
        informationsPersonnelles.setCodePostal("44200");
        informationsPersonnelles.setIsProprietaireSansPretOuLogeGratuit(false);
        demandeurEmploi.setInformationsPersonnelles(informationsPersonnelles);

        BeneficiaireAidesSociales beneficiaireAidesSociales = new BeneficiaireAidesSociales();
        beneficiaireAidesSociales.setBeneficiaireAAH(false);
        beneficiaireAidesSociales.setBeneficiaireARE(false);
        beneficiaireAidesSociales.setBeneficiaireASS(false);
        beneficiaireAidesSociales.setBeneficiaireRSA(true);
        demandeurEmploi.setBeneficiaireAidesSociales(beneficiaireAidesSociales);

        SituationFamiliale situationFamiliale = new SituationFamiliale();
        List<Personne> personnesACharge = new ArrayList<Personne>();
        testUtile.createPersonne(personnesACharge, 9);        
        situationFamiliale.setPersonnesACharge(personnesACharge);
        situationFamiliale.setIsEnCouple(false);
        situationFamiliale.setIsSeulPlusDe18Mois(false);
        demandeurEmploi.setSituationFamiliale(situationFamiliale);

        FuturTravail futurTravail = new FuturTravail();
        futurTravail.setTypeContrat(TypesContratTravail.CDI.name());
        
        Salaire salaire = new Salaire();
        salaire.setMontantNet(700);
        salaire.setMontantBrut(912);        
        futurTravail.setSalaire(salaire);
        
        futurTravail.setDistanceKmDomicileTravail(80);
        futurTravail.setNombreHeuresTravailleesSemaine(20);
        futurTravail.setNombreTrajetsDomicileTravail(12);
        demandeurEmploi.setFuturTravail(futurTravail);

        RessourcesFinancieres ressourcesFinancieres = new RessourcesFinancieres();
        AllocationsCAF allocationsCAF = new AllocationsCAF();
        allocationsCAF.setAllocationsFamilialesMensuellesNetFoyer(90);
        allocationsCAF.setAllocationMensuelleNetRSA(400f);
        allocationsCAF.setProchaineDeclarationRSA(prochaineDeclarationRSA);
        ressourcesFinancieres.setAllocationsCAF(allocationsCAF);
        demandeurEmploi.setRessourcesFinancieres(ressourcesFinancieres); 

        return demandeurEmploi;
    }

    protected void initMocks() throws ParseException, JsonIOException, JsonSyntaxException, FileNotFoundException, URISyntaxException, JSONException {
        //mock création date de demande de simulation
        doReturn(testUtile.getDate("20-10-2020")).when(dateUtile).getDateJour();

        //mock retour appel détail indemnisation de l'ESD 
        DetailIndemnisationESD detailIndemnisationESD = createDetailIndemnisationESDPopulationRSA();        
        doReturn(detailIndemnisationESD).when(detailIndemnisationPoleEmploiClient).callDetailIndemnisationEndPoint(Mockito.any(String.class)); 
    }
    
    private DetailIndemnisationESD createDetailIndemnisationESDPopulationRSA() {
        DetailIndemnisationESD detailIndemnisationESD = new DetailIndemnisationESD();
        detailIndemnisationESD.setBeneficiaireAAH(false);
        detailIndemnisationESD.setBeneficiaireAssuranceChomage(false);
        detailIndemnisationESD.setBeneficiairePrestationSolidarite(false);
        detailIndemnisationESD.setBeneficiaireRSA(true);
        return detailIndemnisationESD;
    }
}
