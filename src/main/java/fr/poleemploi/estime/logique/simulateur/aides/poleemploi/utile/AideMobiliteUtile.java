package fr.poleemploi.estime.logique.simulateur.aides.poleemploi.utile;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.Period;
import java.time.temporal.TemporalAdjusters;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fr.poleemploi.estime.clientsexternes.poleemploiio.PoleEmploiIOClient;
import fr.poleemploi.estime.clientsexternes.poleemploiio.ressources.AgepiPEIOIn;
import fr.poleemploi.estime.clientsexternes.poleemploiio.ressources.AgepiPEIOOut;
import fr.poleemploi.estime.clientsexternes.poleemploiio.ressources.AideMobilitePEIOIn;
import fr.poleemploi.estime.clientsexternes.poleemploiio.ressources.AideMobilitePEIOOut;
import fr.poleemploi.estime.clientsexternes.poleemploiio.ressources.PeConnectAuthorizationPEIO;
import fr.poleemploi.estime.commun.enumerations.Aides;
import fr.poleemploi.estime.commun.enumerations.NombresJoursIndemnises;
import fr.poleemploi.estime.commun.enumerations.Organismes;
import fr.poleemploi.estime.commun.enumerations.exceptions.InternalServerMessages;
import fr.poleemploi.estime.commun.enumerations.exceptions.LoggerMessages;
import fr.poleemploi.estime.commun.utile.AccesTokenUtile;
import fr.poleemploi.estime.commun.utile.demandeuremploi.BeneficiaireAidesUtile;
import fr.poleemploi.estime.commun.utile.demandeuremploi.DemandeurEmploiUtile;
import fr.poleemploi.estime.commun.utile.demandeuremploi.FuturTravailUtile;
import fr.poleemploi.estime.commun.utile.demandeuremploi.InformationsPersonnellesUtile;
import fr.poleemploi.estime.logique.simulateur.aides.utile.AideUtile;
import fr.poleemploi.estime.logique.simulateur.aides.utile.SimulateurAidesUtile;
import fr.poleemploi.estime.services.exceptions.InternalServerException;
import fr.poleemploi.estime.services.ressources.Aide;
import fr.poleemploi.estime.services.ressources.DemandeurEmploi;
import fr.poleemploi.estime.services.ressources.Personne;

/**
 * 
 * Vous pouvez percevoir de l'aide à la mobilité si vous êtes dans l'une des situations suivantes :
 *      Non indemnisé par l'allocation chômage (tout en étant inscrit comme demandeur d'emploi)
 *      En cours d'activité dans le cadre d'un emploi d'avenir
 *      Créateur (ou repreneur) d'entreprise, dans le cadre d'une reprise d'emploi, et vous bénéficiez du statut de salarié de l'entreprise
 *      Vous percevez une allocation chômage inférieure ou égale à l'allocation d'aide au retour à l'emploi (ARE) (soit 29,38 € par jour)
 *      Vous allez reprendre une activité (par exemple, CDD ou contrat de travail temporaire)
 * 
 * @author ijla5100
 *
 */
@Component
public class AideMobiliteUtile {

	public static final int INDEMNITE_JOUR_REPAS = 6;
	public static final float INDEMNITE_KILOMETRIQUE = 0.2f;
	public static final int INDEMNITE_NUITEE = 30;
	public static final int TRAJET_KM_ALLER_RETOUR_MINIMUM  = 60;
	public static final int TRAJET_KM_ALLER_RETOUR_MINIMUM_DOM = 20;
	private static final int NOMBRE_MAX_JOURS_INDEMNISES = 20;
	private static final Logger LOGGER = LoggerFactory.getLogger(AideMobiliteUtile.class);

	@Autowired
	private AccesTokenUtile accesTokenUtile;

	@Autowired
	private AideUtile aideeUtile;

	@Autowired
	private BeneficiaireAidesUtile beneficiaireAidesUtile;

	@Autowired
	private DemandeurEmploiUtile demandeurEmploiUtile;

	@Autowired
	private FuturTravailUtile futurTravailUtile;

	@Autowired
	private InformationsPersonnellesUtile informationsPersonnellesUtile;

	@Autowired
	private SimulateurAidesUtile simulateurAidesUtile;

	@Autowired
	private PoleEmploiIOClient emploiStoreDevClient;


	public Aide simulerAide(DemandeurEmploi demandeurEmploi) {
		float montantAideMobilite = calculerMontantAideMobilite(demandeurEmploi);
		return creerAide(montantAideMobilite);
		//TODO : lier simuler aide avec l'interfacage Aide Mobilite API
		//    	PeConnectAuthorizationPEIO peConnectAuthorizationESD = emploiStoreDevClient.callAccessTokenEndPoint(code, redirectURI, nonce); 
		//    	String bearerToken = accesTokenUtile.getBearerToken(peConnectAuthorizationESD.getAccessToken());
		//    	String bearerToken = "PLACEHOLDER";
		//    	AideMobilitePEIOIn aideMobiliteIn;
		//        aideMobiliteIn = remplirAideMobiliteIn(demandeurEmploi);
		//        Optional<AideMobilitePEIOOut> optionalAideMobiliteOut = emploiStoreDevClient.callAideMobiliteEndPoint(aideMobiliteIn, bearerToken);
		//        if(optionalAideMobiliteOut.isPresent()) { 
		//        	AideMobilitePEIOOut aideMobiliteOut = optionalAideMobiliteOut.get();
		//        	float montantAideMobilite = aideMobiliteOut.getMontant();
		//        	return creerAide(montantAideMobilite);
		//        }else {
		//            LOGGER.error(LoggerMessages.USER_INFO_KO.getMessage());
		//            throw new InternalServerException(InternalServerMessages.SIMULATION_IMPOSSIBLE.getMessage());
		//        } 
		//float montantAideMobilite = calculerMontantAideMobilite(demandeurEmploi);
	}

	private AideMobilitePEIOIn remplirAideMobiliteIn(DemandeurEmploi demandeurEmploi) {
		AideMobilitePEIOIn aideMobilitePEIOIn = new AideMobilitePEIOIn();
		aideMobilitePEIOIn.setContexte("Reprise");
		aideMobilitePEIOIn.setDateActionReclassement(LocalDate.now().with(TemporalAdjusters.firstDayOfNextMonth()).toString());
		aideMobilitePEIOIn.setDateDepot(LocalDate.now().withDayOfMonth(1).toString());
		aideMobilitePEIOIn.setDureePeriodeEmploiOuFormation(demandeurEmploi.getRessourcesFinancieres().getNombreMoisTravaillesDerniersMois());
		aideMobilitePEIOIn.setNatureContratTravail(demandeurEmploi.getFuturTravail().getTypeContrat());
		aideMobilitePEIOIn.setOrigine("W");
		aideMobilitePEIOIn.setDistanceDomicileActionReclassement(Math.round(demandeurEmploi.getFuturTravail().getDistanceKmDomicileTravail()));
		aideMobilitePEIOIn.setNombreAllersRetours(demandeurEmploi.getFuturTravail().getNombreTrajetsDomicileTravail());
		aideMobilitePEIOIn.setNombreRepas(getNombreRepas(demandeurEmploi));
		aideMobilitePEIOIn.setNombreNuitees(getNombreNuitees(demandeurEmploi));

		return aideMobilitePEIOIn;
	}

	private int getNombreNuitees(DemandeurEmploi demandeurEmploi) {
		int nombreJoursIndemnises = getNombrejoursIndemnises(demandeurEmploi.getFuturTravail().getNombreHeuresTravailleesSemaine());
		if(nombreJoursIndemnises - 1 > 0 ) {
			return nombreJoursIndemnises - 1;
		}else {
			return 0;
		}
	}

	private int getNombreRepas(DemandeurEmploi demandeurEmploi) {
		float nombreHeuresHebdoTravaillees = demandeurEmploi.getFuturTravail().getNombreHeuresTravailleesSemaine();
		int nombreRepas = 0;
		for (NombresJoursIndemnises nombresJoursTravailles : NombresJoursIndemnises.values()) {
			if(nombreHeuresHebdoTravaillees >= nombresJoursTravailles.getNombreHeuresMinHebdo() && nombreHeuresHebdoTravaillees <= nombresJoursTravailles.getNombreHeuresMaxHebdo()) {
				nombreRepas = nombresJoursTravailles.getNombreRepasIndemnises();
			}
		}

		return nombreRepas;
	}

	public boolean isEligible(int numeroMoisSimule, DemandeurEmploi demandeurEmploi) {
		return simulateurAidesUtile.isPremierMois(numeroMoisSimule) &&
				futurTravailUtile.isFuturContratTravailEligible(demandeurEmploi.getFuturTravail()) &&
				isDistanceAllerRetourDomicileFuturTavailEligible(demandeurEmploi) &&
				(demandeurEmploiUtile.isSansRessourcesFinancieres(demandeurEmploi) || beneficiaireAidesUtile.isBeneficiaireAidePEouCAF(demandeurEmploi));
	}

	private float calculerMontantAideMobilite(DemandeurEmploi demandeurEmploi) {
		BigDecimal montantAideFraisKilometriques = calculerMontantAideFraisKilometriques(demandeurEmploi);
		BigDecimal montantAideFraisRepas = calculerMontantAideFraisRepas(demandeurEmploi); 
		return montantAideFraisKilometriques.add(montantAideFraisRepas).setScale(0, RoundingMode.DOWN).floatValue();
	}

	private BigDecimal calculerMontantAideFraisRepas(DemandeurEmploi demandeurEmploi) {
		int nombreJoursIndemnises = getNombrejoursIndemnises(demandeurEmploi.getFuturTravail().getNombreHeuresTravailleesSemaine());
		return BigDecimal.valueOf(nombreJoursIndemnises).multiply(BigDecimal.valueOf(INDEMNITE_JOUR_REPAS));
	}

	private BigDecimal calculerMontantAideFraisKilometriques(DemandeurEmploi demandeurEmploi) {
		float distanceKmAllerRetourDomicileTravail = calculerDistanceAllerRetourDomicileTravail(demandeurEmploi);
		if((informationsPersonnellesUtile.isDeFranceMetropolitaine(demandeurEmploi) && distanceKmAllerRetourDomicileTravail >= TRAJET_KM_ALLER_RETOUR_MINIMUM) 
				|| (informationsPersonnellesUtile.isDesDOM(demandeurEmploi) && distanceKmAllerRetourDomicileTravail >= TRAJET_KM_ALLER_RETOUR_MINIMUM_DOM)) {
			BigDecimal nombreKmParMois = caluclerNombreKilometresMensuels(demandeurEmploi);
			return nombreKmParMois.multiply(BigDecimal.valueOf(INDEMNITE_KILOMETRIQUE)).setScale(0, RoundingMode.DOWN);
		}         

		return BigDecimal.ZERO;
	}

	private BigDecimal caluclerNombreKilometresMensuels(DemandeurEmploi demandeurEmploi) {
		int nombreTrajetsDomicileTravail = demandeurEmploi.getFuturTravail().getNombreTrajetsDomicileTravail();
		if(nombreTrajetsDomicileTravail > 0) {
			float distanceKmDomicileTravailAllerRetour = calculerDistanceAllerRetourDomicileTravail(demandeurEmploi);
			return BigDecimal.valueOf(distanceKmDomicileTravailAllerRetour).multiply(BigDecimal.valueOf(nombreTrajetsDomicileTravail)); 
		}
		return BigDecimal.ZERO;
	}

	private Aide creerAide(float montantAide) {
		Aide aideMobilite = new Aide();
		aideMobilite.setCode(Aides.AIDE_MOBILITE.getCode());
		Optional<String> detailAideOptional = aideeUtile.getDescription(Aides.AIDE_MOBILITE.getNomFichierDetail());
		if(detailAideOptional.isPresent()) {
			aideMobilite.setDetail(detailAideOptional.get());            
		}
		aideMobilite.setMontant(montantAide);
		aideMobilite.setNom(Aides.AIDE_MOBILITE.getNom());
		aideMobilite.setOrganisme(Organismes.PE.getNom());
		aideMobilite.setReportee(false);
		return aideMobilite;
	}

	private boolean isDistanceAllerRetourDomicileFuturTavailEligible(DemandeurEmploi demandeurEmploi) {
		float distanceKmDomicileTravailAllerRetour = calculerDistanceAllerRetourDomicileTravail(demandeurEmploi);
		return (!informationsPersonnellesUtile.isDeMayotte(demandeurEmploi) &&  distanceKmDomicileTravailAllerRetour > TRAJET_KM_ALLER_RETOUR_MINIMUM) 
				|| (informationsPersonnellesUtile.isDeMayotte(demandeurEmploi) && distanceKmDomicileTravailAllerRetour > TRAJET_KM_ALLER_RETOUR_MINIMUM_DOM);
	}

	private float calculerDistanceAllerRetourDomicileTravail(DemandeurEmploi demandeurEmploi) {
		return BigDecimal.valueOf(demandeurEmploi.getFuturTravail().getDistanceKmDomicileTravail()).multiply(BigDecimal.valueOf(2)).floatValue();

	}

	private int getNombrejoursIndemnises(float nombreHeuresHebdoTravaillees) {
		for (NombresJoursIndemnises nombresJoursTravailles : NombresJoursIndemnises.values()) {
			if(nombreHeuresHebdoTravaillees >= nombresJoursTravailles.getNombreHeuresMinHebdo() && nombreHeuresHebdoTravaillees <= nombresJoursTravailles.getNombreHeuresMaxHebdo()) {
				return nombresJoursTravailles.getNombreRepasIndemnises();
			}
		}
		return NOMBRE_MAX_JOURS_INDEMNISES;
	}
}
