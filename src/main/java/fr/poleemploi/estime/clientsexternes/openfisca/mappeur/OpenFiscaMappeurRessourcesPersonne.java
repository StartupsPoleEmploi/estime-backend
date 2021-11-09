package fr.poleemploi.estime.clientsexternes.openfisca.mappeur;

import static fr.poleemploi.estime.clientsexternes.openfisca.mappeur.ParametresOpenFisca.AAH;
import static fr.poleemploi.estime.clientsexternes.openfisca.mappeur.ParametresOpenFisca.ASS;
import static fr.poleemploi.estime.clientsexternes.openfisca.mappeur.ParametresOpenFisca.BENEFICES_MICRO_ENTREPRISE;
import static fr.poleemploi.estime.clientsexternes.openfisca.mappeur.ParametresOpenFisca.CHIFFRE_AFFAIRES_INDEPENDANT;
import static fr.poleemploi.estime.clientsexternes.openfisca.mappeur.ParametresOpenFisca.CHOMAGE_NET;
import static fr.poleemploi.estime.clientsexternes.openfisca.mappeur.ParametresOpenFisca.PENSION_INVALIDITE;
import static fr.poleemploi.estime.clientsexternes.openfisca.mappeur.ParametresOpenFisca.SALAIRE_BASE;
import static fr.poleemploi.estime.clientsexternes.openfisca.mappeur.ParametresOpenFisca.SALAIRE_IMPOSABLE;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.github.tsohr.JSONObject;

import fr.poleemploi.estime.commun.utile.demandeuremploi.PersonneUtile;
import fr.poleemploi.estime.commun.utile.demandeuremploi.RessourcesFinancieresUtile;
import fr.poleemploi.estime.services.ressources.Personne;
import fr.poleemploi.estime.services.ressources.RessourcesFinancieres;
import fr.poleemploi.estime.services.ressources.Salaire; 

@Component
public class OpenFiscaMappeurRessourcesPersonne {
    
    @Autowired
    private OpenFiscaMappeurPeriode openFiscaMappeurPeriode;
    
    @Autowired
    private PersonneUtile personneUtile;
    

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
                personneJSON.put(CHIFFRE_AFFAIRES_INDEPENDANT, openFiscaMappeurPeriode.creerPeriodesAnnees(ressourcesFinancieres.getChiffreAffairesIndependantDernierExercice(), dateDebutSimulation));
            } 
            if(ressourcesFinancieresUtile.hasRevenusMicroEntreprise(ressourcesFinancieres)) {
                personneJSON.put(BENEFICES_MICRO_ENTREPRISE, openFiscaMappeurPeriode.creerPeriodesAnnees(ressourcesFinancieres.getBeneficesMicroEntrepriseDernierExercice(), dateDebutSimulation));
            }
            addRessourcesFinancieresCAF(personneJSON, ressourcesFinancieres, dateDebutSimulation, numeroMoisSimule);
            addRessourcesFinancieresPoleEmploi(personneJSON, personne, dateDebutSimulation, numeroMoisSimule);
            addRessourcesFinancieresCPAM(personneJSON, ressourcesFinancieres, dateDebutSimulation, numeroMoisSimule);
        }
    }

    private void addRessourcesFinancieresCAF(JSONObject personneJSON, RessourcesFinancieres ressourcesFinancieres, LocalDate dateDebutSimulation, int numeroMoisSimule){
        if(ressourcesFinancieres.getAidesCAF() != null 
           && ressourcesFinancieres.getAidesCAF().getAllocationAAH() != null
           && ressourcesFinancieres.getAidesCAF().getAllocationAAH() > 0) {
            personneJSON.put(AAH, openFiscaMappeurPeriode.creerPeriodes(ressourcesFinancieres.getAidesCAF().getAllocationAAH(), dateDebutSimulation, numeroMoisSimule, OpenFiscaMappeurPeriode.NOMBRE_MOIS_PERIODE_OPENFISCA));                                
        }
    }

    private void addRessourcesFinancieresPoleEmploi(JSONObject personneJSON, Personne personne, LocalDate dateDebutSimulation, int numeroMoisSimule){
        RessourcesFinancieres ressourcesFinancieres = personne.getRessourcesFinancieres();
        if(personneUtile.hasAllocationARE(personne)) {
            personneJSON.put(CHOMAGE_NET, openFiscaMappeurPeriode.creerPeriodes(ressourcesFinancieres.getAidesPoleEmploi().getAllocationARE().getAllocationMensuelleNet(), dateDebutSimulation, numeroMoisSimule, OpenFiscaMappeurPeriode.NOMBRE_MOIS_PERIODE_OPENFISCA));                                
        }
        if(personneUtile.hasAllocationASS(personne)) {
            personneJSON.put(ASS, openFiscaMappeurPeriode.creerPeriodes(ressourcesFinancieres.getAidesPoleEmploi().getAllocationASS().getAllocationMensuelleNet(), dateDebutSimulation, numeroMoisSimule, OpenFiscaMappeurPeriode.NOMBRE_MOIS_PERIODE_OPENFISCA));                                
        }
    }
    
    private void addRessourcesFinancieresCPAM(JSONObject personneJSON, RessourcesFinancieres ressourcesFinancieres, LocalDate dateDebutPeriodeSimulee, int numeroMoisSimule){
        if(ressourcesFinancieres.getAidesCPAM() != null 
           && ressourcesFinancieres.getAidesCPAM().getPensionInvalidite() != null
           && ressourcesFinancieres.getAidesCPAM().getPensionInvalidite() > 0) {
            personneJSON.put(PENSION_INVALIDITE, openFiscaMappeurPeriode.creerPeriodes(ressourcesFinancieres.getAidesCPAM().getPensionInvalidite(), dateDebutPeriodeSimulee, numeroMoisSimule, OpenFiscaMappeurPeriode.NOMBRE_MOIS_PERIODE_OPENFISCA));                                
        }
    }
}
