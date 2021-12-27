package fr.poleemploi.estime.logique.simulateur.aides.poleemploi.utile;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fr.poleemploi.estime.clientsexternes.poleemploiio.PoleEmploiIOClient;
import fr.poleemploi.estime.clientsexternes.poleemploiio.ressources.simulateuraides.agepi.AgepiPEIOOut;
import fr.poleemploi.estime.commun.enumerations.AideEnum;
import fr.poleemploi.estime.commun.enumerations.MessageInformatifEnum;
import fr.poleemploi.estime.commun.enumerations.OrganismeEnum;
import fr.poleemploi.estime.commun.utile.demandeuremploi.BeneficiaireAidesUtile;
import fr.poleemploi.estime.commun.utile.demandeuremploi.DemandeurEmploiUtile;
import fr.poleemploi.estime.commun.utile.demandeuremploi.FuturTravailUtile;
import fr.poleemploi.estime.commun.utile.demandeuremploi.SituationFamilialeUtile;
import fr.poleemploi.estime.logique.simulateur.aides.utile.AideUtile;
import fr.poleemploi.estime.logique.simulateur.aides.utile.SimulateurAidesUtile;
import fr.poleemploi.estime.services.ressources.Aide;
import fr.poleemploi.estime.services.ressources.DemandeurEmploi;

/**
 * Classe permettant de calculer le montant de l'AGEPI (Aide à la garde d'enfants pour parent isolé).
 * 

 * @author ijla5100
 *
 */
@Component
public class AgepiUtile {

    public static final int AGE_MAX_ENFANT = 10;
    public static final int NBR_ENFANT_PALIER_INTERMEDIAIRE = 2;
    public static final int NBR_ENFANT_PALIER_MAX = 3;
    public static final int NBR_ENFANT_PALIER_MIN = 1;
    public static final int NBR_HEURE_PALIER_INTERMEDIAIRE = 15;

    @Autowired
    private BeneficiaireAidesUtile beneficiaireAidesUtile;

    @Autowired
    private DemandeurEmploiUtile demandeurEmploiUtile;

    @Autowired
    private FuturTravailUtile futurTravailUtile;

    @Autowired
    private SituationFamilialeUtile situationFamilialeUtile;

    @Autowired
    private AideUtile aideeUtile;

    @Autowired
    private SimulateurAidesUtile simulateurAidesUtile;

    @Autowired
    private PoleEmploiIOClient poleEmploiIOClient;

    /** Qui peut percevoir l'Agepi ?
     *   Vous pouvez percevoir l'Agepi si vous remplissez toutes les conditions suivantes :
     *     Vous êtes demandeur d'emploi et vous allez reprendre une activité (qui est en cours d'activité dans le cadre d'un emploi d'avenir).
     *     Vous êtes créateur ou repreneur d'entreprise.
     *     Vous n'êtes pas indemnisé par Pôle emploi ou votre allocation chômage journalière est inférieure ou égale à 29,38€, 14.68€ pour Mayotte.
     *     Vous élevez seul 1 ou plusieurs enfants de moins de 10 ans dont vous avez la charge et la garde.
     * @param numeroMoisSimule 
     */
    public boolean isEligible(int numeroMoisSimule, DemandeurEmploi demandeurEmploi) {
        return simulateurAidesUtile.isPremierMois(numeroMoisSimule) && isSituationFamilialeEligibleAgepi(demandeurEmploi)
                && futurTravailUtile.isFuturContratTravailEligible(demandeurEmploi.getFuturTravail())
                && (demandeurEmploiUtile.isSansRessourcesFinancieres(demandeurEmploi) || beneficiaireAidesUtile.isBeneficiaireAidePEouCAF(demandeurEmploi));
    }

    public Optional<Aide> simulerAide(DemandeurEmploi demandeurEmploi) {
        Optional<AgepiPEIOOut> optionalAgepiOut = poleEmploiIOClient.getAgepiSimulateurAides(demandeurEmploi);
        if (optionalAgepiOut.isPresent()) {
            AgepiPEIOOut agepiOut = optionalAgepiOut.get();
            if (agepiOut.getDecisionAgepiAPI().getNature().equals("Demande attribuée")) {
                float montantAide = agepiOut.getDecisionAgepiAPI().getMontant();
                return Optional.of(creerAide(montantAide));
            } 
        } 
        return Optional.empty();
    }

    private Aide creerAide(float montantAide) {
        Aide agepi = new Aide();
        agepi.setCode(AideEnum.AGEPI.getCode());
        Optional<String> detailAideOptional = aideeUtile.getDescription(AideEnum.AGEPI.getNomFichierDetail());
        if (detailAideOptional.isPresent()) {
            agepi.setDetail(detailAideOptional.get());
        }
        agepi.setMessageAlerte(MessageInformatifEnum.AGEPI_IDF.getMessage());
        agepi.setNom(AideEnum.AGEPI.getNom());
        agepi.setOrganisme(OrganismeEnum.PE.getNom());
        agepi.setMontant(montantAide);
        agepi.setReportee(false);
        return agepi;
    }

    /**
     * Demandeur Emploi éligible si élève seul 1 ou plusieurs enfants de moins de 10 ans dont il a la charge et la garde.
     * @param demandeurEmploi
     * @return true si éligible AGEPI
     */
    private boolean isSituationFamilialeEligibleAgepi(DemandeurEmploi demandeurEmploi) {
        if (!demandeurEmploi.getSituationFamiliale().getIsEnCouple().booleanValue()) {
            return situationFamilialeUtile.getNombrePersonnesAChargeAgeInferieureAgeLimite(demandeurEmploi, AgepiUtile.AGE_MAX_ENFANT) > 0;
        }
        return false;
    }
}
