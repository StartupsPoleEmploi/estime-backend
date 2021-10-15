package fr.poleemploi.estime.logique.simulateur.aides.poleemploi.utile;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.Period;
import java.time.temporal.TemporalAdjusters;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.stereotype.Component;

import fr.poleemploi.estime.clientsexternes.openfisca.AgepiApiClient;
import fr.poleemploi.estime.clientsexternes.poleemploiio.PoleEmploiIOClient;
import fr.poleemploi.estime.clientsexternes.poleemploiio.ressources.AgepiPEIOIn;
import fr.poleemploi.estime.clientsexternes.poleemploiio.ressources.AgepiPEIOOut;
import fr.poleemploi.estime.commun.enumerations.Aides;
import fr.poleemploi.estime.commun.enumerations.MessagesInformatifs;
import fr.poleemploi.estime.commun.enumerations.MontantsParPalierAgepi;
import fr.poleemploi.estime.commun.enumerations.Organismes;
import fr.poleemploi.estime.commun.utile.demandeuremploi.BeneficiaireAidesUtile;
import fr.poleemploi.estime.commun.utile.demandeuremploi.DemandeurEmploiUtile;
import fr.poleemploi.estime.commun.utile.demandeuremploi.FuturTravailUtile;
import fr.poleemploi.estime.commun.utile.demandeuremploi.InformationsPersonnellesUtile;
import fr.poleemploi.estime.commun.utile.demandeuremploi.SituationFamilialeUtile;
import fr.poleemploi.estime.logique.simulateur.aides.utile.AideUtile;
import fr.poleemploi.estime.logique.simulateur.aides.utile.SimulateurAidesUtile;
import fr.poleemploi.estime.services.ressources.Aide;
import fr.poleemploi.estime.services.ressources.DemandeurEmploi;
import fr.poleemploi.estime.services.ressources.Personne;

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
    private InformationsPersonnellesUtile informationsPersonnellesUtile;

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
    private PoleEmploiIOClient emploiStoreDevClient;
	
    private AgepiApiClient agepiApiClient;


    /** Qui peut percevoir l'Agepi ?
     *   Vous pouvez percevoir l'Agepi si vous remplissez toutes les conditions suivantes :
     *     Vous êtes demandeur d'emploi et vous allez reprendre une activité (qui est en cours d'activité dans le cadre d'un emploi d'avenir).
     *     Vous êtes créateur ou repreneur d'entreprise.
     *     Vous n'êtes pas indemnisé par Pôle emploi ou votre allocation chômage journalière est inférieure ou égale à 29,38€, 14.68€ pour Mayotte.
     *     Vous élevez seul 1 ou plusieurs enfants de moins de 10 ans dont vous avez la charge et la garde.
     * @param numeroMoisSimule 
     */
    public boolean isEligible(int numeroMoisSimule, DemandeurEmploi demandeurEmploi) {
        return  simulateurAidesUtile.isPremierMois(numeroMoisSimule) &&
                isSituationFamilialeEligibleAgepi(demandeurEmploi) &&
                futurTravailUtile.isFuturContratTravailEligible(demandeurEmploi.getFuturTravail()) &&
                (demandeurEmploiUtile.isSansRessourcesFinancieres(demandeurEmploi) || beneficiaireAidesUtile.isBeneficiaireAidePEouCAF(demandeurEmploi));
    }

    public Aide simulerAide(DemandeurEmploi demandeurEmploi) {
        //float montantAgepi = calculerMontantAgepi(demandeurEmploi);
        AgepiPEIOIn agepiIn = new AgepiPEIOIn();
        agepiIn = remplirAgepiIn(demandeurEmploi);
		AgepiPEIOOut agepiOut = emploiStoreDevClient.callAgepiEndPoint(agepiIn);
		float montantAgepi = agepiOut.getMontant();
        return creerAide(montantAgepi);   
    }

    private AgepiPEIOIn remplirAgepiIn(DemandeurEmploi demandeurEmploi) {
    	AgepiPEIOIn agepiPEIOIn = new AgepiPEIOIn();
    	agepiPEIOIn.setContexte("Reprise");
    	agepiPEIOIn.setDateActionReclassement(LocalDate.now().with(TemporalAdjusters.firstDayOfNextMonth()).toString());
    	agepiPEIOIn.setDateDepot(LocalDate.now().withDayOfMonth(1).toString());
    	agepiPEIOIn.setDureePeriodeEmploiOuFormation(demandeurEmploi.getRessourcesFinancieres().getNombreMoisTravaillesDerniersMois());
    	agepiPEIOIn.setEleveSeulEnfants(!demandeurEmploi.getSituationFamiliale().getIsEnCouple());
    	agepiPEIOIn.setIntensite((int) Math.round(demandeurEmploi.getFuturTravail().getNombreHeuresTravailleesSemaine()));
    	agepiPEIOIn.setLieuFormationOuEmploi("France");
    	agepiPEIOIn.setNatureContratTravail(demandeurEmploi.getFuturTravail().getTypeContrat());
    	
        int nombreEnfants = 0;
        int nombreEnfantsMoinsDixAns = 0;
        for(Personne personneACharge:demandeurEmploi.getSituationFamiliale().getPersonnesACharge()) {
        	LocalDate today = LocalDate.now();
        	int agePersonneACharge = Period.between(personneACharge.getInformationsPersonnelles().getDateNaissance(), today).getYears();
        	if(agePersonneACharge < 10) {
        		++nombreEnfants;
        		++nombreEnfantsMoinsDixAns;
        	}else if(agePersonneACharge < 18) {
        		++nombreEnfants;
        	}
        }
    	
    	agepiPEIOIn.setNombreEnfants(nombreEnfants);
    	agepiPEIOIn.setNombreEnfantsMoins10Ans(nombreEnfantsMoinsDixAns);
    	agepiPEIOIn.setOrigine("W");
    	agepiPEIOIn.setTypeIntensite("Hebdomadaire");
		return agepiPEIOIn;
	}

	private float calculerMontantAgepi(DemandeurEmploi demandeurEmploi) {
        int nombreEnfantACharge = situationFamilialeUtile.getNombrePersonnesAChargeAgeInferieureAgeLimite(demandeurEmploi, AGE_MAX_ENFANT);
        if(nombreEnfantACharge > 0) {
            switch (nombreEnfantACharge) {

            case NBR_ENFANT_PALIER_MIN:
                return determinerMontantAgepi(
                        MontantsParPalierAgepi.NBR_ENFANT_PALIER_MIN_NBR_HEURE_PALIER_INTERMEDIAIRE, 
                        MontantsParPalierAgepi.NBR_ENFANT_PALIER_MIN_NBR_HEURE_PALIER_MAX, 
                        demandeurEmploi);
            case NBR_ENFANT_PALIER_INTERMEDIAIRE:
                return determinerMontantAgepi(
                        MontantsParPalierAgepi.NBR_ENFANT_PALIER_INTERMEDIAIRE_NBR_HEURE_PALIER_INTERMEDIAIRE, 
                        MontantsParPalierAgepi.NBR_ENFANT_PALIER_INTERMEDIAIRE_NBR_HEURE_PALIER_MAX, 
                        demandeurEmploi);
            default:
                return determinerMontantAgepi(
                        MontantsParPalierAgepi.NBR_ENFANT_PALIER_MAX_NBR_HEURE_PALIER_INTERMEDIAIRE, 
                        MontantsParPalierAgepi.NBR_ENFANT_PALIER_MAX_NBR_HEURE_PALIER_MAX, 
                        demandeurEmploi);
            }
        }
        return 0;
    }
    
    private Aide creerAide(float montantAide) {
        Aide agepi = new Aide();
        agepi.setCode(Aides.AGEPI.getCode());
        Optional<String> detailAideOptional = aideeUtile.getDescription(Aides.AGEPI.getNomFichierDetail());
        if(detailAideOptional.isPresent()) {
            agepi.setDetail(detailAideOptional.get());            
        }
	agepi.setMessageAlerte(MessagesInformatifs.AGEPI_IDF.getMessage());
        agepi.setNom(Aides.AGEPI.getNom());
        agepi.setOrganisme(Organismes.PE.getNom());
        agepi.setMontant(montantAide);
        agepi.setReportee(false);
        return agepi;
    }

    private float determinerMontantAgepiApiExterne(MontantsParPalierAgepi montantPalierIntermediaire, MontantsParPalierAgepi montantPalierMax, DemandeurEmploi demandeurEmploi) throws JSONException {
    	agepiApiClient = new AgepiApiClient();
    	agepiApiClient.envoyerDonneesDemandeurEmploi(demandeurEmploi);
    	return 0;
    }
    
    private float determinerMontantAgepi(MontantsParPalierAgepi montantPalierIntermediaire, MontantsParPalierAgepi montantPalierMax, DemandeurEmploi demandeurEmploi){
        float nombreHeuresTravailleesSemaine = demandeurEmploi.getFuturTravail().getNombreHeuresTravailleesSemaine();
        try {
        	this.agepiApiClient = new AgepiApiClient();
        	System.out.println("======================");
            System.out.println(this.agepiApiClient.creerJsonDemandeur(demandeurEmploi).toString());
        		
        } catch (JSONException e) {
			e.printStackTrace();
		}       
        if(nombreHeuresTravailleesSemaine < NBR_HEURE_PALIER_INTERMEDIAIRE) {
            int montantHorsMayotte = montantPalierIntermediaire.getMontant();
            return informationsPersonnellesUtile.isDeMayotte(demandeurEmploi) ? calculerMontantMayotte(montantHorsMayotte) : montantHorsMayotte;
        } else {
            int montantHorsMayotte = montantPalierMax.getMontant();
            return informationsPersonnellesUtile.isDeMayotte(demandeurEmploi) ? calculerMontantMayotte(montantHorsMayotte) : montantHorsMayotte;
        }
                
    }

    /**
     * 
     * @param montantHorsMayotte
     * @return montant arrondi au supérieur à 0 chiffre après la virgule 
     */
    private float calculerMontantMayotte(int montantHorsMayotte) {
        return BigDecimal.valueOf(montantHorsMayotte).divide(BigDecimal.valueOf(2)).setScale(0, RoundingMode.DOWN).floatValue();
    }

    /**
     * Demandeur Emploi éligible si élève seul 1 ou plusieurs enfants de moins de 10 ans dont il a la charge et la garde.
     * @param demandeurEmploi
     * @return true si éligible AGEPI
     */
    private boolean isSituationFamilialeEligibleAgepi(DemandeurEmploi demandeurEmploi) {
        if(!demandeurEmploi.getSituationFamiliale().getIsEnCouple().booleanValue()) {
            return situationFamilialeUtile.getNombrePersonnesAChargeAgeInferieureAgeLimite(demandeurEmploi, AgepiUtile.AGE_MAX_ENFANT) > 0;
        }
        return false;
    }
}
