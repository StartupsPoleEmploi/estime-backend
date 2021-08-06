package fr.poleemploi.estime.logique.simulateur.prestationssociales.caf.utile;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fr.poleemploi.estime.commun.enumerations.Organismes;
import fr.poleemploi.estime.commun.enumerations.PrestationsSociales;
import fr.poleemploi.estime.commun.utile.demandeuremploi.RessourcesFinancieresUtile;
import fr.poleemploi.estime.logique.simulateur.prestationssociales.utile.PrestationSocialeUtile;
import fr.poleemploi.estime.logique.simulateur.prestationssociales.utile.SimulateurPrestationsSocialesUtile;
import fr.poleemploi.estime.services.ressources.DemandeurEmploi;
import fr.poleemploi.estime.services.ressources.PrestationSociale;

@Component
public class AllocationAdultesHandicapesUtile {

    public static final float MONTANT_SALAIRE_BRUT_PALIER = 446.38f;
    public static final float POURCENTAGE_SALAIRE_PALIER_1 = 0.2f;
    public static final float POURCENTAGE_SALAIRE_PALIER_2 = 0.6f;
    
    @Autowired
    private PrestationSocialeUtile prestationSocialeeUtile;

    @Autowired
    private RessourcesFinancieresUtile ressourcesFinancieresUtile;
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
    public void simulerPrestationSociale(Map<String, PrestationSociale>  prestationsSocialesPourCeMois, int numeroMoisSimule, DemandeurEmploi demandeurEmploi) {
        int nombreMoisTravaillesDerniersMois = ressourcesFinancieresUtile.getNombreMoisTravaillesDerniersMois(demandeurEmploi, true); 
        int diffNbrMoisSimulationEtNbrMoisTravailles = SimulateurPrestationsSocialesUtile.NOMBRE_MOIS_MAX_A_SIMULER - nombreMoisTravaillesDerniersMois;
        if(numeroMoisSimule > diffNbrMoisSimulationEtNbrMoisTravailles) {
            float montantAllocationAAHReduit = calculerMontantReduit(demandeurEmploi);
            if(montantAllocationAAHReduit > 0) {
                ajouterPrestationSocialeeAAH(prestationsSocialesPourCeMois, montantAllocationAAHReduit);             
            }
        } else {
            //le demandeur cumule son AAH avant la simulation
            float montantAllocationAAHAvantSimulation = demandeurEmploi.getRessourcesFinancieres().getPrestationsCAF().getAllocationMensuelleNetAAH();
            ajouterPrestationSocialeeAAH(prestationsSocialesPourCeMois, montantAllocationAAHAvantSimulation);
        }
    }
    
    /**
     * Méthode permettant de calculer le montant réduit de l'AAH
     * si le salaire brut est inférieur à 466,38€, alors montant AAH moins 20% du salaire brut 
     * si le salaire brut est supérieur à 466,38€, alors montant AAH moins 60% du salaire brut 
     * @return montant AAH réduit
     */
    private float calculerMontantReduit(DemandeurEmploi demandeurEmploi) {
        BigDecimal montantAllocationAAHAvantSimulation = BigDecimal.valueOf(demandeurEmploi.getRessourcesFinancieres().getPrestationsCAF().getAllocationMensuelleNetAAH());
        
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
    
    private void ajouterPrestationSocialeeAAH(Map<String, PrestationSociale>  prestationsSocialesPourCeMois, float montant) {
        PrestationSociale allocationAAH = new PrestationSociale();
        allocationAAH.setCode(PrestationsSociales.ALLOCATION_ADULTES_HANDICAPES.getCode());
        Optional<String> detailAideOptional = prestationSocialeeUtile.getDescription(PrestationsSociales.ALLOCATION_ADULTES_HANDICAPES.getNomFichierDetail());
        if(detailAideOptional.isPresent()) {
            allocationAAH.setDetail(detailAideOptional.get());            
        }
        allocationAAH.setMontant(montant);
        allocationAAH.setNom(PrestationsSociales.ALLOCATION_ADULTES_HANDICAPES.getNom());
        allocationAAH.setOrganisme(Organismes.CAF.getNom());
        allocationAAH.setReportee(false);
        prestationsSocialesPourCeMois.put(PrestationsSociales.ALLOCATION_ADULTES_HANDICAPES.getCode(), allocationAAH);
    }
}
