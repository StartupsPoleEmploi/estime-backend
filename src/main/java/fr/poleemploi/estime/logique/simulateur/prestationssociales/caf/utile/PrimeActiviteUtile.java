package fr.poleemploi.estime.logique.simulateur.prestationssociales.caf.utile;

import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fr.poleemploi.estime.clientsexternes.openfisca.OpenFiscaClient;
import fr.poleemploi.estime.clientsexternes.openfisca.OpenFiscaRetourSimulation;
import fr.poleemploi.estime.commun.enumerations.Organismes;
import fr.poleemploi.estime.commun.enumerations.PrestationsSociales;
import fr.poleemploi.estime.commun.utile.demandeuremploi.BeneficiairePrestationsSocialesUtile;
import fr.poleemploi.estime.commun.utile.demandeuremploi.RessourcesFinancieresUtile;
import fr.poleemploi.estime.logique.simulateur.prestationssociales.utile.PrestationSocialeUtile;
import fr.poleemploi.estime.services.ressources.DemandeurEmploi;
import fr.poleemploi.estime.services.ressources.PrestationSociale;
import fr.poleemploi.estime.services.ressources.SimulationPrestationsSociales;

@Component
public class PrimeActiviteUtile {
   
    

    @Autowired
    private OpenFiscaClient openFiscaClient;
    
    @Autowired
    private PrestationSocialeUtile prestationSocialeeUtile;
    
    @Autowired
    private BeneficiairePrestationsSocialesUtile beneficiairePrestationsSocialesUtile;
    
    @Autowired
    private RessourcesFinancieresUtile ressourcesFinancieresUtile;


    public void simulerPrestationSociale(SimulationPrestationsSociales simulationPrestationsSociales, Map<String, PrestationSociale>  prestationsSocialesPourCeMois, LocalDate dateDebutSimulation, int numeroMoisSimule, DemandeurEmploi demandeurEmploi) {
        
        Optional<PrestationSociale> primeActiviteMoisPrecedent = getPrimeActiviteMoisPrecedent(simulationPrestationsSociales,  numeroMoisSimule);     
        
        if(primeActiviteMoisPrecedent.isPresent()) {
            reporterPrimeActiviteMoisPrecedent(simulationPrestationsSociales, prestationsSocialesPourCeMois, dateDebutSimulation, numeroMoisSimule, demandeurEmploi, primeActiviteMoisPrecedent);            
        } else if(isPrimeActiviteACalculer(primeActiviteMoisPrecedent, numeroMoisSimule, demandeurEmploi)) {
            calculerPrimeActivite(simulationPrestationsSociales, prestationsSocialesPourCeMois, dateDebutSimulation, numeroMoisSimule, demandeurEmploi);            
        }
    }

    

    public PrestationSociale creerPrestationSocialePrimeActivite(float montantPrimeActivite, boolean isAideReportee) {
        PrestationSociale aidePrimeActivite = new PrestationSociale();
        aidePrimeActivite.setCode(PrestationsSociales.PRIME_ACTIVITE.getCode());
        Optional<String> detailAideOptional = prestationSocialeeUtile.getDescription(PrestationsSociales.PRIME_ACTIVITE.getNomFichierDetail());
        if(detailAideOptional.isPresent()) {
            aidePrimeActivite.setDetail(detailAideOptional.get());            
        }
        aidePrimeActivite.setMontant(montantPrimeActivite); 
        aidePrimeActivite.setNom(PrestationsSociales.PRIME_ACTIVITE.getNom());
        aidePrimeActivite.setOrganisme(Organismes.CAF.getNomCourt());
        aidePrimeActivite.setReportee(isAideReportee);
        return aidePrimeActivite;
    }
    
    public Optional<PrestationSociale> getPrimeActiviteMoisPrecedent(SimulationPrestationsSociales simulationPrestationsSociales, int numeroMoisSimule) {
        int moisNMoins1 = numeroMoisSimule - 1; 
        return prestationSocialeeUtile.getPrestationSocialePourCeMoisSimule(simulationPrestationsSociales, PrestationsSociales.PRIME_ACTIVITE.getCode(), moisNMoins1);
    }
    

    
    private void calculerPrimeActivite(SimulationPrestationsSociales simulationPrestationsSociales, Map<String, PrestationSociale> prestationsSocialesPourCeMois, LocalDate dateDebutSimulation, int numeroMoisSimule, DemandeurEmploi demandeurEmploi) {
        OpenFiscaRetourSimulation openFiscaRetourSimulation = openFiscaClient.calculerPrimeActivite(simulationPrestationsSociales, demandeurEmploi, dateDebutSimulation, numeroMoisSimule);
        if(openFiscaRetourSimulation.getMontantPrimeActivite() > 0) {
            PrestationSociale primeActivite = creerPrestationSocialePrimeActivite(openFiscaRetourSimulation.getMontantPrimeActivite(), false);
            prestationsSocialesPourCeMois.put(PrestationsSociales.PRIME_ACTIVITE.getCode(), primeActivite);
        }
    }
    
    private boolean isPrimeActiviteACalculer(Optional<PrestationSociale> primeActiviteMoisPrecedent, int numeroMoisSimule, DemandeurEmploi demandeurEmploi) {
        if(primeActiviteMoisPrecedent.isEmpty()) {                
            //Si le demandeur est bénéficiare de l'ASS et de l'AAH, c'est la temporatlité de l'ASS qui est appliquée
            if(beneficiairePrestationsSocialesUtile.isBeneficiaireASS(demandeurEmploi) || (beneficiairePrestationsSocialesUtile.isBeneficiaireASS(demandeurEmploi) && beneficiairePrestationsSocialesUtile.isBeneficiaireAAH(demandeurEmploi))) {
                return isPrimeActiviteACalculerDemandeurASS(demandeurEmploi, numeroMoisSimule);
            } else if(beneficiairePrestationsSocialesUtile.isBeneficiaireAAH(demandeurEmploi)) {
                return isPrimeActiviteACalculerDemandeurAAH(numeroMoisSimule);
            }
        }     
        return false;
    }
    
    private boolean isPrimeActiviteACalculerDemandeurASS(DemandeurEmploi demandeurEmploi, int numeroMoisSimule) {
        return !ressourcesFinancieresUtile.hasTravailleAuCoursDerniersMois(demandeurEmploi) && numeroMoisSimule == 5
                || (ressourcesFinancieresUtile.hasTravailleAuCoursDerniersMois(demandeurEmploi) && isMoisPourCalculPrimeActiviteASS(numeroMoisSimule, demandeurEmploi));
    }
    
    private boolean isPrimeActiviteACalculerDemandeurAAH(int numeroMoisSimule) {
        return numeroMoisSimule == 2 || numeroMoisSimule == 5;
    }    

    private boolean isMoisPourCalculPrimeActiviteASS(int numeroMoisSimule, DemandeurEmploi demandeurEmploi) {
        int nombreMoisTravaillesDerniersMois = ressourcesFinancieresUtile.getNombreMoisTravaillesDerniersMois(demandeurEmploi, false);
        return nombreMoisTravaillesDerniersMois == 1 && numeroMoisSimule == 4 
           || (nombreMoisTravaillesDerniersMois == 2 && (numeroMoisSimule == 3 || numeroMoisSimule == 6)) 
           || (nombreMoisTravaillesDerniersMois == 3 && (numeroMoisSimule == 2 || numeroMoisSimule == 5));
    }
    
  //TODO JLA :  revoir les explications
    private void reporterPrimeActiviteMoisPrecedent(SimulationPrestationsSociales simulationPrestationsSociales, Map<String, PrestationSociale> prestationsSocialesPourCeMois, LocalDate dateDebutSimulation, int numeroMoisSimule, DemandeurEmploi demandeurEmploi, Optional<PrestationSociale> primeActiviteMoisPrecedentOptional) {
      
        //le montant calculé au mois N est reporté au mois N+1 et N+2, la validité du montant s'étale donc sur 3 mois       
        int moisMoinsPeriodeValiditeMontant = numeroMoisSimule - 3; 
        Optional<PrestationSociale> primeActiviteDebutPeriodeValiditeMontant = prestationSocialeeUtile.getPrestationSocialePourCeMoisSimule(simulationPrestationsSociales, PrestationsSociales.PRIME_ACTIVITE.getCode(), moisMoinsPeriodeValiditeMontant);   

        //si prime d'activité en N - NBR_MOIS_VALIDITE_MONTANT et que l'aide n'a pas été reportée
        //alors on recalcule la prime d'activité
        if(primeActiviteDebutPeriodeValiditeMontant.isPresent() && !primeActiviteDebutPeriodeValiditeMontant.get().isReportee()) {
            calculerPrimeActivite(simulationPrestationsSociales, prestationsSocialesPourCeMois, dateDebutSimulation, numeroMoisSimule, demandeurEmploi);
        } else if(primeActiviteMoisPrecedentOptional.isPresent()) {
            PrestationSociale primeActiviteMoisPrecedent = primeActiviteMoisPrecedentOptional.get();
            PrestationSociale primeActiviteMoisSimule = creerPrestationSocialePrimeActivite(primeActiviteMoisPrecedent.getMontant(),true); 
            prestationsSocialesPourCeMois.put(PrestationsSociales.PRIME_ACTIVITE.getCode(), primeActiviteMoisSimule);                    
        }
    }
}
