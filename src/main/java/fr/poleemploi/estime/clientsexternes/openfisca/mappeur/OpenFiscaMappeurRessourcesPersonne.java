package fr.poleemploi.estime.clientsexternes.openfisca.mappeur;

import static fr.poleemploi.estime.clientsexternes.openfisca.mappeur.ParametresOpenFisca.AAH;
import static fr.poleemploi.estime.clientsexternes.openfisca.mappeur.ParametresOpenFisca.ASS;
import static fr.poleemploi.estime.clientsexternes.openfisca.mappeur.ParametresOpenFisca.CHOMAGE_NET;
import static fr.poleemploi.estime.clientsexternes.openfisca.mappeur.ParametresOpenFisca.PENSION_INVALIDITE;
import static fr.poleemploi.estime.clientsexternes.openfisca.mappeur.ParametresOpenFisca.SALAIRE_BASE;
import static fr.poleemploi.estime.clientsexternes.openfisca.mappeur.ParametresOpenFisca.SALAIRE_IMPOSABLE;
import static fr.poleemploi.estime.clientsexternes.openfisca.mappeur.ParametresOpenFisca.TNS_AUTO_ENTREPRENEUR_CHIFFRE_AFFAIRES;
import static fr.poleemploi.estime.clientsexternes.openfisca.mappeur.ParametresOpenFisca.TNS_AUTRES_REVENUS;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.github.tsohr.JSONObject;

import fr.poleemploi.estime.commun.utile.demandeuremploi.BeneficiaireAidesSocialesUtile;
import fr.poleemploi.estime.commun.utile.demandeuremploi.RessourcesFinancieresUtile;
import fr.poleemploi.estime.services.ressources.Personne;
import fr.poleemploi.estime.services.ressources.RessourcesFinancieres;
import fr.poleemploi.estime.services.ressources.Salaire; 

@Component
public class OpenFiscaMappeurRessourcesPersonne {

    @Autowired
    private OpenFiscaMappeurPeriode openFiscaMappeurPeriode;
    
    @Autowired
    private BeneficiaireAidesSocialesUtile beneficiaireAidesSocialesUtile;
    
    @Autowired
    private RessourcesFinancieresUtile ressourcesFinancieresUtile;

    public void addRessourcesFinancieresPersonne(JSONObject personneJSON, Personne personne, LocalDate dateDebutSimulation, int numeroMoisSimule){
        RessourcesFinancieres ressourcesFinancieres = personne.getRessourcesFinancieres();
        if(ressourcesFinancieres != null) {
            if(ressourcesFinancieresUtile.hasSalaire(ressourcesFinancieres)) {
                Salaire salaire = ressourcesFinancieres.getSalaire();
                personneJSON.put(SALAIRE_BASE, openFiscaMappeurPeriode.creerPeriodes(salaire.getMontantBrut(), dateDebutSimulation, numeroMoisSimule, OpenFiscaMappeurPeriode.NOMBRE_MOIS_PERIODE_OPENFISCA));                  
                personneJSON.put(SALAIRE_IMPOSABLE, openFiscaMappeurPeriode.creerPeriodes(salaire.getMontantNet(), dateDebutSimulation, numeroMoisSimule, OpenFiscaMappeurPeriode.NOMBRE_MOIS_PERIODE_OPENFISCA));
            }
            if(ressourcesFinancieresUtile.hasBeneficesTravailleurIndependant(ressourcesFinancieres)) {
                personneJSON.put(TNS_AUTRES_REVENUS, openFiscaMappeurPeriode.creerPeriodesAnnees(ressourcesFinancieres.getBeneficesTravailleurIndependantDernierExercice(), dateDebutSimulation));
            } 
            if(ressourcesFinancieresUtile.hasRevenusMicroEntreprise(ressourcesFinancieres)) {
                personneJSON.put(TNS_AUTO_ENTREPRENEUR_CHIFFRE_AFFAIRES, openFiscaMappeurPeriode.creerPeriodes(this.ressourcesFinancieresUtile.getRevenusMicroEntrepriseSur1Mois(ressourcesFinancieres), dateDebutSimulation, numeroMoisSimule, OpenFiscaMappeurPeriode.NOMBRE_MOIS_PERIODE_OPENFISCA_TNS_AUTO_ENTREPRENEUR_CHIFFRE_AFFAIRES));
            }
            addRessourcesFinancieresCAF(personneJSON, ressourcesFinancieres, dateDebutSimulation, numeroMoisSimule);
            addRessourcesFinancieresPoleEmploi(personneJSON, personne, dateDebutSimulation, numeroMoisSimule);
            addRessourcesFinancieresCPAM(personneJSON, ressourcesFinancieres, dateDebutSimulation, numeroMoisSimule);
        }
    }

    private void addRessourcesFinancieresCAF(JSONObject personneJSON, RessourcesFinancieres ressourcesFinancieres, LocalDate dateDebutSimulation, int numeroMoisSimule){
        if(ressourcesFinancieres.getAllocationsCAF() != null 
           && ressourcesFinancieres.getAllocationsCAF().getAllocationMensuelleNetAAH() != null
           && ressourcesFinancieres.getAllocationsCAF().getAllocationMensuelleNetAAH() > 0) {
            personneJSON.put(AAH, openFiscaMappeurPeriode.creerPeriodes(ressourcesFinancieres.getAllocationsCAF().getAllocationMensuelleNetAAH(), dateDebutSimulation, numeroMoisSimule, OpenFiscaMappeurPeriode.NOMBRE_MOIS_PERIODE_OPENFISCA));                                
        }
    }

    private void addRessourcesFinancieresPoleEmploi(JSONObject personneJSON, Personne personne, LocalDate dateDebutSimulation, int numeroMoisSimule){
        RessourcesFinancieres ressourcesFinancieres = personne.getRessourcesFinancieres();
        if(ressourcesFinancieres.getAllocationsPoleEmploi() != null) {
            if(beneficiaireAidesSocialesUtile.isBeneficiaireARE(personne)
               && ressourcesFinancieres.getAllocationsPoleEmploi().getAllocationMensuelleNet() != null 
               && ressourcesFinancieres.getAllocationsPoleEmploi().getAllocationMensuelleNet() > 0) {
                personneJSON.put(CHOMAGE_NET, openFiscaMappeurPeriode.creerPeriodes(ressourcesFinancieres.getAllocationsPoleEmploi().getAllocationMensuelleNet(), dateDebutSimulation, numeroMoisSimule, OpenFiscaMappeurPeriode.NOMBRE_MOIS_PERIODE_OPENFISCA));                                
            }
            if(beneficiaireAidesSocialesUtile.isBeneficiaireASS(personne)
               && ressourcesFinancieres.getAllocationsPoleEmploi().getAllocationMensuelleNet() != null 
               && ressourcesFinancieres.getAllocationsPoleEmploi().getAllocationMensuelleNet() > 0) {
                personneJSON.put(ASS, openFiscaMappeurPeriode.creerPeriodes(ressourcesFinancieres.getAllocationsPoleEmploi().getAllocationMensuelleNet(), dateDebutSimulation, numeroMoisSimule, OpenFiscaMappeurPeriode.NOMBRE_MOIS_PERIODE_OPENFISCA));                                
            }
        }
    }
    
    private void addRessourcesFinancieresCPAM(JSONObject personneJSON, RessourcesFinancieres ressourcesFinancieres, LocalDate dateDebutPeriodeSimulee, int numeroMoisSimule){
        if(ressourcesFinancieres.getAllocationsCPAM() != null 
           && ressourcesFinancieres.getAllocationsCPAM().getPensionInvalidite() != null
           && ressourcesFinancieres.getAllocationsCPAM().getPensionInvalidite() > 0) {
            personneJSON.put(PENSION_INVALIDITE, openFiscaMappeurPeriode.creerPeriodes(ressourcesFinancieres.getAllocationsCPAM().getPensionInvalidite(), dateDebutPeriodeSimulee, numeroMoisSimule, OpenFiscaMappeurPeriode.NOMBRE_MOIS_PERIODE_OPENFISCA));                                
        }
    }
}
