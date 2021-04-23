package fr.poleemploi.estime.clientsexternes.openfisca.mappeur;

import static fr.poleemploi.estime.clientsexternes.openfisca.mappeur.ParametresOpenFisca.AAH;
import static fr.poleemploi.estime.clientsexternes.openfisca.mappeur.ParametresOpenFisca.ASS;
import static fr.poleemploi.estime.clientsexternes.openfisca.mappeur.ParametresOpenFisca.CHOMAGE_NET;
import static fr.poleemploi.estime.clientsexternes.openfisca.mappeur.ParametresOpenFisca.PENSION_INVALIDITE;
import static fr.poleemploi.estime.clientsexternes.openfisca.mappeur.ParametresOpenFisca.SALAIRE_NET;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.stereotype.Component;

import fr.poleemploi.estime.commun.utile.demandeuremploi.BeneficiaireAidesSocialesUtile;
import fr.poleemploi.estime.services.ressources.Personne;
import fr.poleemploi.estime.services.ressources.RessourcesFinancieres; 

@Component
public class OpenFiscaMappeurRessourcesPersonne {

    @Autowired
    private OpenFiscaMappeurPeriode openFiscaMappeurPeriode;
    
    @Autowired
    private BeneficiaireAidesSocialesUtile beneficiaireAidesSocialesUtile;

    public void addRessourcesFinancieresPersonne(JSONObject personneJSON, Personne personne, LocalDate dateDebutSimulation, int numeroMoisSimule) throws JSONException {
        RessourcesFinancieres ressourcesFinancieres = personne.getRessourcesFinancieres();
        if(ressourcesFinancieres != null) {
            if(ressourcesFinancieres.getSalaireNet() > 0) {
                personneJSON.put(SALAIRE_NET, openFiscaMappeurPeriode.creerPeriodesAvecValeurJSON(ressourcesFinancieres.getSalaireNet(), dateDebutSimulation, numeroMoisSimule));                  
            }
            addRessourcesFinancieresCAF(personneJSON, ressourcesFinancieres, dateDebutSimulation, numeroMoisSimule);
            addRessourcesFinancieresPoleEmploi(personneJSON, personne, dateDebutSimulation, numeroMoisSimule);
            addRessourcesFinancieresCPAM(personneJSON, ressourcesFinancieres, dateDebutSimulation, numeroMoisSimule);
        }
    }

    private void addRessourcesFinancieresCAF(JSONObject personneJSON, RessourcesFinancieres ressourcesFinancieres, LocalDate dateDebutSimulation, int numeroMoisSimule) throws JSONException {
        if(ressourcesFinancieres.getAllocationsCAF() != null 
           && ressourcesFinancieres.getAllocationsCAF().getAllocationMensuelleNetAAH() != null
           && ressourcesFinancieres.getAllocationsCAF().getAllocationMensuelleNetAAH() > 0) {
            personneJSON.put(AAH, openFiscaMappeurPeriode.creerPeriodesAvecValeurJSON(ressourcesFinancieres.getAllocationsCAF().getAllocationMensuelleNetAAH(), dateDebutSimulation, numeroMoisSimule));                                
        }
    }

    private void addRessourcesFinancieresPoleEmploi(JSONObject personneJSON, Personne personne, LocalDate dateDebutSimulation, int numeroMoisSimule) throws JSONException {
        RessourcesFinancieres ressourcesFinancieres = personne.getRessourcesFinancieres();
        if(ressourcesFinancieres.getAllocationsPoleEmploi() != null) {
            if(beneficiaireAidesSocialesUtile.isBeneficiaireARE(personne)
               && ressourcesFinancieres.getAllocationsPoleEmploi().getAllocationMensuelleNet() != null 
               && ressourcesFinancieres.getAllocationsPoleEmploi().getAllocationMensuelleNet() > 0) {
                personneJSON.put(CHOMAGE_NET, openFiscaMappeurPeriode.creerPeriodesAvecValeurJSON(ressourcesFinancieres.getAllocationsPoleEmploi().getAllocationMensuelleNet(), dateDebutSimulation, numeroMoisSimule));                                
            }
            if(beneficiaireAidesSocialesUtile.isBeneficiaireASS(personne)
               && ressourcesFinancieres.getAllocationsPoleEmploi().getAllocationMensuelleNet() != null 
               && ressourcesFinancieres.getAllocationsPoleEmploi().getAllocationMensuelleNet() > 0) {
                personneJSON.put(ASS, openFiscaMappeurPeriode.creerPeriodesAvecValeurJSON(ressourcesFinancieres.getAllocationsPoleEmploi().getAllocationMensuelleNet(), dateDebutSimulation, numeroMoisSimule));                                
            }
        }
    }
    
    private void addRessourcesFinancieresCPAM(JSONObject personneJSON, RessourcesFinancieres ressourcesFinancieres, LocalDate dateDebutPeriodeSimulee, int numeroMoisSimule) throws JSONException {
        if(ressourcesFinancieres.getAllocationsCPAM() != null 
           && ressourcesFinancieres.getAllocationsCPAM().getPensionInvalidite() != null
           && ressourcesFinancieres.getAllocationsCPAM().getPensionInvalidite() > 0) {
            personneJSON.put(PENSION_INVALIDITE, openFiscaMappeurPeriode.creerPeriodesAvecValeurJSON(ressourcesFinancieres.getAllocationsCPAM().getPensionInvalidite(), dateDebutPeriodeSimulee, numeroMoisSimule));                                
        }
    }
}
