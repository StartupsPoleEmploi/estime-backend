package testsintegration.simulation.temporalite.are;

import java.text.ParseException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;

import fr.poleemploi.estime.clientsexternes.poleemploiio.PoleEmploiIOClient;
import fr.poleemploi.estime.commun.enumerations.NationaliteEnum;
import fr.poleemploi.estime.commun.enumerations.TypeContratTravailEnum;
import fr.poleemploi.estime.commun.enumerations.TypePopulationEnum;
import fr.poleemploi.estime.commun.utile.AccessTokenUtile;
import fr.poleemploi.estime.commun.utile.DateUtile;
import fr.poleemploi.estime.commun.utile.SuiviUtilisateurUtile;
import fr.poleemploi.estime.services.ressources.AllocationARE;
import fr.poleemploi.estime.services.ressources.DemandeurEmploi;
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
    private PoleEmploiIOClient poleEmploiIOClient;

    @SpyBean
    private AccessTokenUtile accessTokenUtile;

    protected DemandeurEmploi createDemandeurEmploi(int prochaineDeclarationTrimestrielle) throws ParseException {
	boolean isEnCouple = false;
	int nbEnfant = 1;
	DemandeurEmploi demandeurEmploi = utile.creerBaseDemandeurEmploi(TypePopulationEnum.ARE.getLibelle(), isEnCouple, nbEnfant);

	demandeurEmploi.getInformationsPersonnelles().setDateNaissance(utile.getDate("05-07-1986"));
	demandeurEmploi.getInformationsPersonnelles().setNationalite(NationaliteEnum.FRANCAISE.getValeur());
	demandeurEmploi.getInformationsPersonnelles().setCodePostal("44200");

	demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(0).getInformationsPersonnelles().setDateNaissance(utile.getDateNaissanceFromAge(9));

	demandeurEmploi.getFuturTravail().setTypeContrat(TypeContratTravailEnum.CDI.name());
	demandeurEmploi.getFuturTravail().setNombreHeuresTravailleesSemaine(35);
	demandeurEmploi.getFuturTravail().getSalaire().setMontantNet(940);
	demandeurEmploi.getFuturTravail().getSalaire().setMontantBrut(1200);
	demandeurEmploi.getFuturTravail().setDistanceKmDomicileTravail(80);
	demandeurEmploi.getFuturTravail().setNombreTrajetsDomicileTravail(12);
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

}
