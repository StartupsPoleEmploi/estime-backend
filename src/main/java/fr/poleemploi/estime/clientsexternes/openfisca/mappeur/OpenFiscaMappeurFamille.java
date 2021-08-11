package fr.poleemploi.estime.clientsexternes.openfisca.mappeur;

import static fr.poleemploi.estime.clientsexternes.openfisca.mappeur.ParametresOpenFisca.AF;
import static fr.poleemploi.estime.clientsexternes.openfisca.mappeur.ParametresOpenFisca.APL;
import static fr.poleemploi.estime.clientsexternes.openfisca.mappeur.ParametresOpenFisca.ASF;
import static fr.poleemploi.estime.clientsexternes.openfisca.mappeur.ParametresOpenFisca.CF;
import static fr.poleemploi.estime.clientsexternes.openfisca.mappeur.ParametresOpenFisca.CONJOINT;
import static fr.poleemploi.estime.clientsexternes.openfisca.mappeur.ParametresOpenFisca.DEMANDEUR;
import static fr.poleemploi.estime.clientsexternes.openfisca.mappeur.ParametresOpenFisca.ENFANT;
import static fr.poleemploi.estime.clientsexternes.openfisca.mappeur.ParametresOpenFisca.ENFANTS;
import static fr.poleemploi.estime.clientsexternes.openfisca.mappeur.ParametresOpenFisca.PARENTS;
import static fr.poleemploi.estime.clientsexternes.openfisca.mappeur.ParametresOpenFisca.PPA;
import static fr.poleemploi.estime.clientsexternes.openfisca.mappeur.ParametresOpenFisca.PRESTATION_ACCUEIL_JEUNE_ENFANT;
import static fr.poleemploi.estime.clientsexternes.openfisca.mappeur.ParametresOpenFisca.RSA;
import static fr.poleemploi.estime.clientsexternes.openfisca.mappeur.ParametresOpenFisca.RSA_ISOLEMENT_RECENT;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.github.tsohr.JSONArray;
import com.github.tsohr.JSONObject;

import fr.poleemploi.estime.commun.utile.demandeuremploi.BeneficiaireAidesUtile;
import fr.poleemploi.estime.commun.utile.demandeuremploi.RessourcesFinancieresUtile;
import fr.poleemploi.estime.commun.utile.demandeuremploi.SituationFamilialeUtile;
import fr.poleemploi.estime.services.ressources.AidesFamiliales;
import fr.poleemploi.estime.services.ressources.DemandeurEmploi;
import fr.poleemploi.estime.services.ressources.Personne;

@Component
public class OpenFiscaMappeurFamille {
    
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
    private BeneficiaireAidesUtile beneficiaireAidesUtile;
        
    
    public JSONObject creerFamilleJSON(DemandeurEmploi demandeurEmploi, Optional<List<Personne>> personneAChargeInferieur25ansOptional, LocalDate dateDebutSimulation, int numeroMoisSimule) {
        JSONObject famille = new JSONObject();
        famille.put(PARENTS, creerParentsJSON(demandeurEmploi));
        famille.put(ENFANTS, creerPersonnesAChargeJSON(personneAChargeInferieur25ansOptional));
        if(ressourcesFinancieresUtile.hasAllocationLogement(demandeurEmploi)) {
            famille.put(APL, openFiscaPeriodeMappeur.creerPeriodesallocationsLogement(demandeurEmploi.getRessourcesFinancieres().getAidesCAF().getAllocationsLogement(), dateDebutSimulation, numeroMoisSimule));            
        }
        if(ressourcesFinancieresUtile.hasAidesFamiliales(demandeurEmploi)) {
            AidesFamiliales aidesFamiliales = demandeurEmploi.getRessourcesFinancieres().getAidesCAF().getAidesFamiliales();
            famille.put(ASF, openFiscaPeriodeMappeur.creerPeriodes(aidesFamiliales.getAllocationSoutienFamilial(), dateDebutSimulation, numeroMoisSimule, OpenFiscaMappeurPeriode.NOMBRE_MOIS_PERIODE_OPENFISCA));                   
            famille.put(AF, openFiscaPeriodeMappeur.creerPeriodes(aidesFamiliales.getAllocationsFamiliales(), dateDebutSimulation, numeroMoisSimule, OpenFiscaMappeurPeriode.NOMBRE_MOIS_PERIODE_OPENFISCA));
            famille.put(CF, openFiscaPeriodeMappeur.creerPeriodes(aidesFamiliales.getComplementFamilial(), dateDebutSimulation, numeroMoisSimule, OpenFiscaMappeurPeriode.NOMBRE_MOIS_PERIODE_OPENFISCA));
            famille.put(PRESTATION_ACCUEIL_JEUNE_ENFANT, openFiscaPeriodeMappeur.creerPeriodes(aidesFamiliales.getAideAccueilJeuneEnfant(), dateDebutSimulation, numeroMoisSimule, OpenFiscaMappeurPeriode.NOMBRE_MOIS_PERIODE_OPENFISCA));
        }
        if(beneficiaireAidesUtile.isBeneficiaireRSA(demandeurEmploi)) {
            float montantRsaDemandeur = ressourcesFinancieresUtile.getAllocationsRSANet(demandeurEmploi);
            if(montantRsaDemandeur > 0) {
                famille.put(RSA, openFiscaMappeurRSA.creerRSAJson(dateDebutSimulation, numeroMoisSimule));
                famille.put(RSA_ISOLEMENT_RECENT, openFiscaPeriodeMappeur.creerPeriodes(!situationFamilialeUtile.isSeulPlusDe18Mois(demandeurEmploi), dateDebutSimulation, numeroMoisSimule, OpenFiscaMappeurPeriode.NOMBRE_MOIS_PERIODE_OPENFISCA));
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
