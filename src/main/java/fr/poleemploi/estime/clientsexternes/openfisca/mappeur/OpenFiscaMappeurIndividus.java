package fr.poleemploi.estime.clientsexternes.openfisca.mappeur;

import static fr.poleemploi.estime.clientsexternes.openfisca.mappeur.ParametresOpenFisca.AAH;
import static fr.poleemploi.estime.clientsexternes.openfisca.mappeur.ParametresOpenFisca.ASI;
import static fr.poleemploi.estime.clientsexternes.openfisca.mappeur.ParametresOpenFisca.ASS;
import static fr.poleemploi.estime.clientsexternes.openfisca.mappeur.ParametresOpenFisca.DATE_NAISSANCE;
import static fr.poleemploi.estime.clientsexternes.openfisca.mappeur.ParametresOpenFisca.ENFANT;
import static fr.poleemploi.estime.clientsexternes.openfisca.mappeur.ParametresOpenFisca.ENFANT_A_CHARGE;
import static fr.poleemploi.estime.clientsexternes.openfisca.mappeur.ParametresOpenFisca.PENSIONS_ALIMENTAIRES_PERCUES;
import static fr.poleemploi.estime.clientsexternes.openfisca.mappeur.ParametresOpenFisca.PENSION_INVALIDITE;
import static fr.poleemploi.estime.clientsexternes.openfisca.mappeur.ParametresOpenFisca.REVENUS_LOCATIFS;
import static fr.poleemploi.estime.clientsexternes.openfisca.mappeur.ParametresOpenFisca.SALAIRE_NET;
import static fr.poleemploi.estime.clientsexternes.openfisca.mappeur.ParametresOpenFisca.STATUT_MARITAL;
import static fr.poleemploi.estime.clientsexternes.openfisca.mappeur.ParametresOpenFisca.TNS_AUTRES_REVENUS;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.stereotype.Component;

import fr.poleemploi.estime.commun.enumerations.AidesSociales;
import fr.poleemploi.estime.commun.enumerations.StatutsMarital;
import fr.poleemploi.estime.commun.utile.DateUtile;
import fr.poleemploi.estime.commun.utile.demandeuremploi.RessourcesFinancieresUtile;
import fr.poleemploi.estime.services.ressources.DemandeurEmploi;
import fr.poleemploi.estime.services.ressources.Personne;
import fr.poleemploi.estime.services.ressources.SimulationAidesSociales;

@Component
public class OpenFiscaMappeurIndividus {
    
    @Autowired
    private DateUtile dateUtile;
    
    @Autowired
    private OpenFiscaMappeurRessourcesPersonne openFiscaMappeurRessourcesFinancieres;
    
    @Autowired
    private OpenFiscaMappeurPeriode openFiscaMappeurPeriode;
    
    @Autowired
    private RessourcesFinancieresUtile ressourcesFinancieresUtile;
    
    public JSONObject creerConjointJSON(Personne conjoint, LocalDate dateDebutSimulation, int numeroMoisSimule) throws JSONException {
        JSONObject conjointJSON = new JSONObject();
        openFiscaMappeurRessourcesFinancieres.addRessourcesFinancieresPersonne(conjointJSON, conjoint, dateDebutSimulation, numeroMoisSimule);
        return conjointJSON;
    }
    
    public JSONObject creerDemandeurJSON(SimulationAidesSociales simulationAidesSociales, DemandeurEmploi demandeurEmploi, LocalDate dateDebutSimulation, int numeroMoisSimule) throws JSONException {
        JSONObject demandeurJSON = new JSONObject();
        demandeurJSON.put(DATE_NAISSANCE, openFiscaMappeurPeriode.creerPeriodesAvecValeurJSON(getDateNaissance(demandeurEmploi), dateDebutSimulation, numeroMoisSimule));            
        demandeurJSON.put(STATUT_MARITAL, openFiscaMappeurPeriode.creerPeriodesAvecValeurJSON(getStatutMarital(demandeurEmploi), dateDebutSimulation, numeroMoisSimule));
        demandeurJSON.put(SALAIRE_NET, openFiscaMappeurPeriode.creerPeriodesSalaireJSON(demandeurEmploi, dateDebutSimulation, numeroMoisSimule));
        addRessourcesFinancieresDemandeur(demandeurJSON, demandeurEmploi, simulationAidesSociales, numeroMoisSimule, dateDebutSimulation);
        return demandeurJSON;
    }
    
    public void ajouterPersonneACharge(JSONObject individu, List<Personne> personnesACharge, LocalDate dateJour, int numeroMoisSimule) throws JSONException {
        int index = 1;
        for (Personne personneACharge : personnesACharge) {
            individu.put(ENFANT + index++, creerEnfantJSON(personneACharge, dateJour, numeroMoisSimule));
        }
    }
    
    private void addRessourcesFinancieresDemandeur(JSONObject demandeurJSON, DemandeurEmploi demandeurEmploi, SimulationAidesSociales simulationAidesSociales, int numeroMoisSimule, LocalDate dateDebutSimulation) throws JSONException {
        if(ressourcesFinancieresUtile.hasAllocationAdultesHandicapes(demandeurEmploi)) {
            String codeAideAAH = AidesSociales.ALLOCATION_ADULTES_HANDICAPES.getCode();
            demandeurJSON.put(AAH, openFiscaMappeurPeriode.creerPeriodesAideSocialeJSON(demandeurEmploi, simulationAidesSociales, codeAideAAH, dateDebutSimulation, numeroMoisSimule));            
        }
        if(ressourcesFinancieresUtile.hasAllocationSolidariteSpecifique(demandeurEmploi.getRessourcesFinancieres())) {
            String codeAideASS = AidesSociales.ALLOCATION_SOLIDARITE_SPECIFIQUE.getCode();
            demandeurJSON.put(ASS, openFiscaMappeurPeriode.creerPeriodesAideSocialeJSON(demandeurEmploi, simulationAidesSociales, codeAideASS, dateDebutSimulation, numeroMoisSimule));
        }
        if(ressourcesFinancieresUtile.hasRevenusImmobilier(demandeurEmploi)) {
            float revenusImmobilierSur1Mois = ressourcesFinancieresUtile.getRevenusImmobilierSur1Mois(demandeurEmploi.getRessourcesFinancieres());
            demandeurJSON.put(REVENUS_LOCATIFS, openFiscaMappeurPeriode.creerPeriodesSur36MoisAvecValeurJSON(revenusImmobilierSur1Mois, dateDebutSimulation));
        } 
        if(ressourcesFinancieresUtile.hasRevenusTravailleurIndependant(demandeurEmploi)) {
            demandeurJSON.put(TNS_AUTRES_REVENUS, openFiscaMappeurPeriode.creerPeriodesAnneesAvecValeurJSON(demandeurEmploi.getRessourcesFinancieres().getRevenusCreateurEntreprise3DerniersMois(), dateDebutSimulation));
        }
        if(ressourcesFinancieresUtile.hasPensionsAlimentaires(demandeurEmploi)) {
            demandeurJSON.put(PENSIONS_ALIMENTAIRES_PERCUES, openFiscaMappeurPeriode.creerPeriodesAvecValeurJSON(demandeurEmploi.getRessourcesFinancieres().getAllocationsCAF().getPensionsAlimentairesFoyer(), dateDebutSimulation, numeroMoisSimule));
        }
        if(ressourcesFinancieresUtile.hasPensionInvalidite(demandeurEmploi)) {
            demandeurJSON.put(PENSION_INVALIDITE, openFiscaMappeurPeriode.creerPeriodesAvecValeurJSON(demandeurEmploi.getRessourcesFinancieres().getAllocationsCPAM().getPensionInvalidite(), dateDebutSimulation, numeroMoisSimule));
            demandeurJSON.put(ASI, openFiscaMappeurPeriode.creerPeriodesAvecValeurJSON(0, dateDebutSimulation, numeroMoisSimule));
        }
    }
    
    private JSONObject creerEnfantJSON(Personne personneACharge, LocalDate dateDebutSimulation, int numeroMoisSimule) throws JSONException {
        JSONObject enfant = new JSONObject();
        enfant.put(DATE_NAISSANCE, openFiscaMappeurPeriode.creerPeriodesAvecValeurJSON(dateUtile.convertDateToString(personneACharge.getInformationsPersonnelles().getDateNaissance(), DateUtile.DATE_FORMAT_YYYY_MM_DD), dateDebutSimulation, numeroMoisSimule));
        enfant.put(ENFANT_A_CHARGE, creerEnfantACharge(dateDebutSimulation));
        openFiscaMappeurRessourcesFinancieres.addRessourcesFinancieresPersonne(enfant, personneACharge, dateDebutSimulation, numeroMoisSimule);
        return enfant;
    }
    
    private JSONObject creerEnfantACharge(LocalDate dateDebutSimulation) throws JSONException {
        JSONObject enfantACharge = new JSONObject();
        enfantACharge.put(String.valueOf(dateDebutSimulation.getYear()), true);
        return enfantACharge;
    }
    
    private String getStatutMarital(DemandeurEmploi demandeurEmploi) {
        if(demandeurEmploi.getSituationFamiliale().getIsEnCouple().booleanValue()) {
            return StatutsMarital.MARIE.getLibelle();
        }
        return StatutsMarital.CELIBATAIRE.getLibelle();
    }
    
    private String getDateNaissance(DemandeurEmploi demandeurEmploi) {
        return dateUtile.convertDateToString(demandeurEmploi.getInformationsPersonnelles().getDateNaissance(), DateUtile.DATE_FORMAT_YYYY_MM_DD);
    }
}
