package fr.poleemploi.estime.logique.simulateuraidessociales.poleemploi.aides;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fr.poleemploi.estime.commun.enumerations.AidesSociales;
import fr.poleemploi.estime.commun.enumerations.NombresJoursIndemnises;
import fr.poleemploi.estime.commun.enumerations.Organismes;
import fr.poleemploi.estime.commun.utile.demandeuremploi.BeneficiaireAidesSocialesUtile;
import fr.poleemploi.estime.commun.utile.demandeuremploi.DemandeurEmploiUtile;
import fr.poleemploi.estime.commun.utile.demandeuremploi.FuturTravailUtile;
import fr.poleemploi.estime.commun.utile.demandeuremploi.InformationsPersonnellesUtile;
import fr.poleemploi.estime.logique.simulateuraidessociales.utile.AideSocialeUtile;
import fr.poleemploi.estime.logique.simulateuraidessociales.utile.SimulateurAidesSocialesUtile;
import fr.poleemploi.estime.services.ressources.AideSociale;
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
public class AideMobilite {

    public static final int INDEMNITE_JOUR_REPAS = 6;
    public static final float INDEMNITE_KILOMETRIQUE = 0.2f;
    public static final int INDEMNITE_NUITEE = 30;
    public static final int TRAJET_KM_ALLER_RETOUR_MINIMUM  = 60;
    public static final int TRAJET_KM_ALLER_RETOUR_MINIMUM_DOM = 20;
    private static final int NOMBRE_MAX_JOURS_INDEMNISES = 20;


    @Autowired
    private AideSocialeUtile aideSocialeUtile;

    @Autowired
    private BeneficiaireAidesSocialesUtile beneficiaireAidesSocialesUtile;

    @Autowired
    private DemandeurEmploiUtile demandeurEmploiUtile;

    @Autowired
    private FuturTravailUtile futurTravailUtile;

    @Autowired
    private InformationsPersonnellesUtile informationsPersonnellesUtile;

    @Autowired
    private SimulateurAidesSocialesUtile simulateurAidesSocialesUtile;


    public AideSociale calculer(DemandeurEmploi demandeurEmploi) {
        AideSociale aideMobilite = new AideSociale();
        aideMobilite.setCode(AidesSociales.AIDE_MOBILITE.getCode());
        Optional<String> detailAideOptional = aideSocialeUtile.getDescription(AidesSociales.AIDE_MOBILITE.getNomFichierDetail());
        if(detailAideOptional.isPresent()) {
            aideMobilite.setDetail(detailAideOptional.get());            
        }
        aideMobilite.setMontant(calculerMontantAideMobilite(demandeurEmploi));
        aideMobilite.setNom(AidesSociales.AIDE_MOBILITE.getNom());
        aideMobilite.setOrganisme(Organismes.PE.getNom());
        aideMobilite.setReportee(false);
        return aideMobilite;
    }

    public boolean isEligible(int numeroMoisSimule, DemandeurEmploi demandeurEmploi) {
        return simulateurAidesSocialesUtile.isPremierMois(numeroMoisSimule) &&
                futurTravailUtile.isFuturContratTravailEligible(demandeurEmploi.getFuturTravail()) &&
                isDistanceAllerRetourDomicileFuturTavailEligible(demandeurEmploi) &&
                (demandeurEmploiUtile.isSansRessourcesFinancieres(demandeurEmploi) || beneficiaireAidesSocialesUtile.isBeneficiaireAidePEouCAF(demandeurEmploi));
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
