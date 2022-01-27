package fr.poleemploi.estime.logique.simulateur.aides.poleemploi.utile;

import java.math.BigDecimal;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fr.poleemploi.estime.clientsexternes.poleemploiio.PoleEmploiIOClient;
import fr.poleemploi.estime.commun.enumerations.AideEnum;
import fr.poleemploi.estime.commun.enumerations.MessageInformatifEnum;
import fr.poleemploi.estime.commun.enumerations.OrganismeEnum;
import fr.poleemploi.estime.commun.utile.demandeuremploi.BeneficiaireAidesUtile;
import fr.poleemploi.estime.commun.utile.demandeuremploi.DemandeurEmploiUtile;
import fr.poleemploi.estime.commun.utile.demandeuremploi.FuturTravailUtile;
import fr.poleemploi.estime.commun.utile.demandeuremploi.InformationsPersonnellesUtile;
import fr.poleemploi.estime.logique.simulateur.aides.utile.AideUtile;
import fr.poleemploi.estime.logique.simulateur.aides.utile.SimulateurAidesUtile;
import fr.poleemploi.estime.services.ressources.Aide;
import fr.poleemploi.estime.services.ressources.DemandeurEmploi;

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
    public static final int TRAJET_KM_ALLER_RETOUR_MINIMUM = 60;
    public static final int TRAJET_KM_ALLER_RETOUR_MINIMUM_DOM = 20;

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
    private PoleEmploiIOClient poleEmploiIOClient;

    public Optional<Aide> simulerAide(DemandeurEmploi demandeurEmploi) {
	float montantAideMobiliteSimule = poleEmploiIOClient.getMontantAideMobiliteSimulateurAides(demandeurEmploi);
	if (montantAideMobiliteSimule > 0) {
	    return Optional.of(creerAide(montantAideMobiliteSimule));
	}
	return Optional.empty();
    }

    public boolean isEligible(int numeroMoisSimule, DemandeurEmploi demandeurEmploi) {
	return simulateurAidesUtile.isPremierMois(numeroMoisSimule) && futurTravailUtile.isFuturContratTravailEligible(demandeurEmploi.getFuturTravail())
		&& isDistanceAllerRetourDomicileFuturTavailEligible(demandeurEmploi)
		&& (demandeurEmploiUtile.isSansRessourcesFinancieres(demandeurEmploi) || beneficiaireAidesUtile.isBeneficiaireAidePEouCAF(demandeurEmploi));
    }

    private Aide creerAide(float montantAide) {
	Aide aideMobilite = new Aide();
	aideMobilite.setCode(AideEnum.AIDE_MOBILITE.getCode());
	Optional<String> detailAideOptional = aideeUtile.getDescription(AideEnum.AIDE_MOBILITE.getNomFichierDetail());
	if (detailAideOptional.isPresent()) {
	    aideMobilite.setDetail(detailAideOptional.get());
	}
	aideMobilite.setMessageAlerte(MessageInformatifEnum.AIDE_MOBILITE.getMessage());
	aideMobilite.setMontant(montantAide);
	aideMobilite.setNom(AideEnum.AIDE_MOBILITE.getNom());
	aideMobilite.setOrganisme(OrganismeEnum.PE.getNomCourt());
	aideMobilite.setReportee(false);
	return aideMobilite;
    }

    private boolean isDistanceAllerRetourDomicileFuturTavailEligible(DemandeurEmploi demandeurEmploi) {
	float distanceKmDomicileTravailAllerRetour = calculerDistanceAllerRetourDomicileTravail(demandeurEmploi);
	return (!informationsPersonnellesUtile.isDeMayotte(demandeurEmploi) && distanceKmDomicileTravailAllerRetour > TRAJET_KM_ALLER_RETOUR_MINIMUM)
		|| (informationsPersonnellesUtile.isDeMayotte(demandeurEmploi) && distanceKmDomicileTravailAllerRetour > TRAJET_KM_ALLER_RETOUR_MINIMUM_DOM);
    }

    private float calculerDistanceAllerRetourDomicileTravail(DemandeurEmploi demandeurEmploi) {
	return BigDecimal.valueOf(demandeurEmploi.getFuturTravail().getDistanceKmDomicileTravail()).multiply(BigDecimal.valueOf(2)).floatValue();

    }
}
