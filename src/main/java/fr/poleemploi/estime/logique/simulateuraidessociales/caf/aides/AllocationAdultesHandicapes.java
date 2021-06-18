package fr.poleemploi.estime.logique.simulateuraidessociales.caf.aides;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fr.poleemploi.estime.commun.enumerations.AidesSociales;
import fr.poleemploi.estime.commun.enumerations.Organismes;
import fr.poleemploi.estime.logique.simulateuraidessociales.utile.AideSocialeUtile;
import fr.poleemploi.estime.logique.simulateuraidessociales.utile.SimulateurAidesSocialesUtile;
import fr.poleemploi.estime.services.ressources.AideSociale;
import fr.poleemploi.estime.services.ressources.DemandeurEmploi;

@Component
public class AllocationAdultesHandicapes {

    public static final float MONTANT_SALAIRE_BRUT_PALIER = 446.38f;
    public static final float POURCENTAGE_SALAIRE_PALIER_1 = 0.2f;
    public static final float POURCENTAGE_SALAIRE_PALIER_2 = 0.6f;
    
    @Autowired
    private AideSocialeUtile aideSocialeUtile;
    
    /**
     * Méthode permettant de calculer le montant de l'AAH sur la période de simulation de N mois
     * Voici la règle sur 6 mois :
     * 
     * si déjà travaillé 6 mois, AAH réduite cumulée avec salaire en M1 M2 M3 M4 M5 M6
     * si déjà travaillé 5 mois, AAH + salaire en M1 puis AAH réduite cumulée avec salaire en  M2 M3 M4 M5 M6
     * si déjà travaillé 4 mois, AAH + salaire en M1 et M2 puis AAH réduite cumulée avec salaire en  M3 M4 M5 M6
     * si déjà travaillé 3 mois, AAH + salaire en M1 M2 M3 puis AAH réduite cumulée avec salaire en  M4 M5 M6
     * si déjà travaillé 2 mois, AAH + salaire en M1 M2 M3 M4 puis AAH réduite cumulée avec salaire en  M5 M6
     * si déjà travaillé 1 mois, AAH + salaire en M1 M2 M3 M4 M5 puis AAH réduite cumulée avec salaire en  M6
     *
     */
    public void simulerAAH(Map<String, AideSociale>  aidesEligiblesPourCeMois, int numeroMoisSimule, DemandeurEmploi demandeurEmploi) {
        int nombreMoisTravaillesDerniersMois = demandeurEmploi.getRessourcesFinancieres().getNombreMoisTravaillesDerniersMois();
        int diffNbrMoisSimulationEtNbrMoisTravailles = SimulateurAidesSocialesUtile.NOMBRE_MOIS_MAX_A_SIMULER - nombreMoisTravaillesDerniersMois;
        
        if(numeroMoisSimule > diffNbrMoisSimulationEtNbrMoisTravailles) {
            float montantAllocationAAHReduit = calculerMontantReduit(demandeurEmploi);
            if(montantAllocationAAHReduit > 0) {
                ajouterAideSocialeAAH(aidesEligiblesPourCeMois, montantAllocationAAHReduit);                
            }
        } else {
            //le demandeur cumule son AAH avant la simulation
            float montantAllocationAAHAvantSimulation = demandeurEmploi.getRessourcesFinancieres().getAllocationsCAF().getAllocationMensuelleNetAAH();
            ajouterAideSocialeAAH(aidesEligiblesPourCeMois, montantAllocationAAHAvantSimulation);
        }
    }
    
    /**
     * Méthode permettant de calculer le montant réduit de l'AAH
     * si le salaire brut est inférieur à 466,38 euros, alors montant AAH moins 20% du salaire brut 
     * si le salaire brut est supérieur à 466,38 euros, alors montant AAH moins 60% du salaire brut 
     * @return montant AAH réduit
     */
    private float calculerMontantReduit(DemandeurEmploi demandeurEmploi) {
        BigDecimal montantAllocationAAHAvantSimulation = BigDecimal.valueOf(demandeurEmploi.getRessourcesFinancieres().getAllocationsCAF().getAllocationMensuelleNetAAH());
        
        BigDecimal partSalaireADeduire = calculerPartSalairePourDeduction(demandeurEmploi);
        float montantReduitAllocationAAH = montantAllocationAAHAvantSimulation.subtract(partSalaireADeduire).setScale(0, RoundingMode.DOWN).floatValue();
        
        if(montantReduitAllocationAAH > 0) {
            return montantReduitAllocationAAH;
        } else {
            return 0;
        }   
    }
        
    private BigDecimal calculerPartSalairePourDeduction(DemandeurEmploi demandeurEmploi) {
        BigDecimal salaireBrut = BigDecimal.valueOf(demandeurEmploi.getFuturTravail().getSalaire().getMontantBrut());
        if(isSalaireInferieurOuEgalSalaireBrutPalier(salaireBrut)) {
            return salaireBrut.multiply(BigDecimal.valueOf(POURCENTAGE_SALAIRE_PALIER_1)).setScale(0, RoundingMode.DOWN);           
        } else {
            return salaireBrut.multiply(BigDecimal.valueOf(POURCENTAGE_SALAIRE_PALIER_2)).setScale(0, RoundingMode.DOWN);
        }
    }
    
    private boolean isSalaireInferieurOuEgalSalaireBrutPalier(BigDecimal salaireBrut) {
        return salaireBrut.compareTo(BigDecimal.valueOf(MONTANT_SALAIRE_BRUT_PALIER)) <= 0;
    }
    
    private void ajouterAideSocialeAAH(Map<String, AideSociale>  aidesEligiblesPourCeMois, float montant) {
        AideSociale aideSocialeAAH = new AideSociale();
        aideSocialeAAH.setCode(AidesSociales.ALLOCATION_ADULTES_HANDICAPES.getCode());
        Optional<String> detailAideOptional = aideSocialeUtile.getDescription(AidesSociales.ALLOCATION_ADULTES_HANDICAPES.getNomFichierDetail());
        if(detailAideOptional.isPresent()) {
            aideSocialeAAH.setDetail(detailAideOptional.get());            
        }
        aideSocialeAAH.setMontant(montant);
        aideSocialeAAH.setNom(AidesSociales.ALLOCATION_ADULTES_HANDICAPES.getNom());
        aideSocialeAAH.setOrganisme(Organismes.CAF.getNom());
        aideSocialeAAH.setReportee(false);
        aidesEligiblesPourCeMois.put(AidesSociales.ALLOCATION_ADULTES_HANDICAPES.getCode(), aideSocialeAAH);
    }
}
