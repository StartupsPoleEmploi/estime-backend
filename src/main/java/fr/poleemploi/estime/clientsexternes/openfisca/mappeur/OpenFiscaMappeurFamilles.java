package fr.poleemploi.estime.clientsexternes.openfisca.mappeur;

import static fr.poleemploi.estime.clientsexternes.openfisca.mappeur.ParametresOpenFisca.AF;
import static fr.poleemploi.estime.clientsexternes.openfisca.mappeur.ParametresOpenFisca.APL;
import static fr.poleemploi.estime.clientsexternes.openfisca.mappeur.ParametresOpenFisca.CONJOINT;
import static fr.poleemploi.estime.clientsexternes.openfisca.mappeur.ParametresOpenFisca.DEMANDEUR;
import static fr.poleemploi.estime.clientsexternes.openfisca.mappeur.ParametresOpenFisca.ENFANT;
import static fr.poleemploi.estime.clientsexternes.openfisca.mappeur.ParametresOpenFisca.ENFANTS;
import static fr.poleemploi.estime.clientsexternes.openfisca.mappeur.ParametresOpenFisca.PARENTS;
import static fr.poleemploi.estime.clientsexternes.openfisca.mappeur.ParametresOpenFisca.PPA;
import static fr.poleemploi.estime.clientsexternes.openfisca.mappeur.ParametresOpenFisca.RSA;
import static fr.poleemploi.estime.clientsexternes.openfisca.mappeur.ParametresOpenFisca.RSA_ISOLEMENT_RECENT;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.github.tsohr.JSONArray;
import com.github.tsohr.JSONObject;

import fr.poleemploi.estime.commun.utile.demandeuremploi.BeneficiaireAidesSocialesUtile;
import fr.poleemploi.estime.commun.utile.demandeuremploi.RessourcesFinancieresUtile;
import fr.poleemploi.estime.commun.utile.demandeuremploi.SituationFamilialeUtile;
import fr.poleemploi.estime.services.ressources.DemandeurEmploi;
import fr.poleemploi.estime.services.ressources.Personne;

@Component
public class OpenFiscaMappeurFamilles {
    
    @Autowired
    private OpenFiscaMappeurPeriode openFiscaPeriodeMappeur;

    @Autowired
    private OpenFiscaMappeurPrimeActivite openFiscaMappeurPrimeActivite;
    
    @Autowired
    private OpenFiscaMappeurRSA openFiscaMappeurRSA;
    
    @Autowired
    private SituationFamilialeUtile situationFamilialeUtile;

    @Autowired
    private RessourcesFinancieresUtile ressourcesFinancieresUtile;

    @Autowired
    private BeneficiaireAidesSocialesUtile beneficiaireAidesSocialesUtile;
        
    
    public JSONObject creerFamilleJSON(DemandeurEmploi demandeurEmploi, Optional<List<Personne>> personneAChargeInferieur25ansOptional, LocalDate dateDebutSimulation, int numeroMoisSimule) {
        JSONObject famille = new JSONObject();
        famille.put(PARENTS, creerParentsJSON(demandeurEmploi));
        famille.put(ENFANTS, creerPersonnesAChargeJSON(personneAChargeInferieur25ansOptional));
        if(ressourcesFinancieresUtile.hasAllocationLogement(demandeurEmploi)) {
            famille.put(APL, openFiscaPeriodeMappeur.creerPeriodesAllocationsLogementMensuellesNetFoyer(demandeurEmploi.getRessourcesFinancieres().getAllocationsCAF().getAllocationsLogementMensuellesNetFoyer(), dateDebutSimulation, numeroMoisSimule));            
        }
        if(ressourcesFinancieresUtile.hasAllocationFamiliale(demandeurEmploi)) {
            famille.put(AF, openFiscaPeriodeMappeur.creerPeriodesAvecValeurJSON(demandeurEmploi.getRessourcesFinancieres().getAllocationsCAF().getAllocationsFamilialesMensuellesNetFoyer(), dateDebutSimulation, numeroMoisSimule));                   
        }
        if(beneficiaireAidesSocialesUtile.isBeneficiaireRSA(demandeurEmploi)) {
            float montantRsaDemandeur = ressourcesFinancieresUtile.getAllocationsRSANet(demandeurEmploi);
            if(montantRsaDemandeur > 0) {
                famille.put(RSA, openFiscaMappeurRSA.creerRSAJson(dateDebutSimulation, numeroMoisSimule));
                famille.put(RSA_ISOLEMENT_RECENT, openFiscaPeriodeMappeur.creerPeriodesAvecValeurJSON(!situationFamilialeUtile.isSeulPlusDe18Mois(demandeurEmploi), dateDebutSimulation, numeroMoisSimule));
            }
        } else {
            float montantRsaFamille = situationFamilialeUtile.calculerMontantRsaFamille(demandeurEmploi);
            if(montantRsaFamille > 0) {
               famille.put(RSA, openFiscaPeriodeMappeur.creerPeriodesAvecValeurJSON(montantRsaFamille, dateDebutSimulation, numeroMoisSimule));
            }
        }
        famille.put(PPA, openFiscaMappeurPrimeActivite.creerPrimeActiviteJSON(dateDebutSimulation, numeroMoisSimule));
        return famille;
    }
    
    private JSONArray creerParentsJSON(DemandeurEmploi demandeurEmploi) {
        JSONArray parentsArray = new JSONArray();
        parentsArray.put(DEMANDEUR);
        if (situationFamilialeUtile.isEnCouple(demandeurEmploi)) {
            parentsArray.put(CONJOINT);            
        }
        return parentsArray;
    }    
    
    private JSONArray creerPersonnesAChargeJSON(Optional<List<Personne>> personneAChargeInferieur25ansOptional) {
        JSONArray personnesAChargeJSON = new JSONArray();
        if(personneAChargeInferieur25ansOptional.isPresent()) {
            for (int i = 1; i <= personneAChargeInferieur25ansOptional.get().size(); i++) {
                personnesAChargeJSON.put(ENFANT + i);            
            }
        }
        return personnesAChargeJSON;
    }  
}
