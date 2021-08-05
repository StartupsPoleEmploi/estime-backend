package fr.poleemploi.estime.logique.simulateuraidessociales.caf.aides;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fr.poleemploi.estime.clientsexternes.openfisca.OpenFiscaClient;
import fr.poleemploi.estime.clientsexternes.openfisca.OpenFiscaRetourSimulation;
import fr.poleemploi.estime.commun.enumerations.AidesSociales;
import fr.poleemploi.estime.commun.enumerations.Organismes;
import fr.poleemploi.estime.commun.utile.demandeuremploi.BeneficiaireAidesSocialesUtile;
import fr.poleemploi.estime.commun.utile.demandeuremploi.RessourcesFinancieresUtile;
import fr.poleemploi.estime.logique.simulateuraidessociales.utile.AideSocialeUtile;
import fr.poleemploi.estime.services.ressources.AideSociale;
import fr.poleemploi.estime.services.ressources.DemandeurEmploi;
import fr.poleemploi.estime.services.ressources.SimulationAidesSociales;
import fr.poleemploi.estime.services.ressources.SimulationMensuelle;

@Component
public class PrimeActivite {
   
    //le montant calculé au mois N est reporté au mois N+1 et N+2, la validité du montant s'étale donc sur 3 mois
    public static final int NBR_MOIS_VALIDITE_MONTANT = 3;

    @Autowired
    private OpenFiscaClient openFiscaClient;
    
    @Autowired
    private AideSocialeUtile aideSocialeUtile;
    
    @Autowired
    private BeneficiaireAidesSocialesUtile beneficiaireAidesSocialesUtile;
    
    @Autowired
    private RessourcesFinancieresUtile ressourcesFinancieresUtile;


    public void simulerPrimeActivite(SimulationAidesSociales simulationAidesSociales, Map<String, AideSociale>  aidesEligiblesPourCeMois, LocalDate dateDebutSimulation, int numeroMoisSimule, DemandeurEmploi demandeurEmploi) {
        List<SimulationMensuelle> simulationsMensuelles = simulationAidesSociales.getSimulationsMensuelles();

        Optional<AideSociale> primeActiviteMoisPrecedent = getPrimeActiviteMoisPrecedent(simulationsMensuelles, numeroMoisSimule);     

        if(isPrimeActiviteACalculer(primeActiviteMoisPrecedent, numeroMoisSimule, demandeurEmploi)) {
            Optional<AideSociale> aidePrimeActiviteOptional = calculer(simulationAidesSociales, demandeurEmploi, dateDebutSimulation, numeroMoisSimule);
            if(aidePrimeActiviteOptional.isPresent()) {
                AideSociale aideSocialeMoisPrecedent = aidePrimeActiviteOptional.get();

                aidesEligiblesPourCeMois.put(AidesSociales.PRIME_ACTIVITE.getCode(), aideSocialeMoisPrecedent);
            }
        } else if(primeActiviteMoisPrecedent.isPresent()){
            int moisMoinsPeriodeValiditeMontant = numeroMoisSimule - NBR_MOIS_VALIDITE_MONTANT; 
            Optional<AideSociale> primeActiviteDebutPeriodeValiditeMontant = getPrimeActiviteSimuleePourNumeroMois(simulationsMensuelles, moisMoinsPeriodeValiditeMontant);

            //si prime d'activité en N - NBR_MOIS_VALIDITE_MONTANT et que l'aide n'a pas été reportée
            //alors on recalcule la prime d'activité
            if(primeActiviteDebutPeriodeValiditeMontant.isPresent() && !primeActiviteDebutPeriodeValiditeMontant.get().isReportee()) {
                Optional<AideSociale> aidePrimeActiviteOptional = calculer(simulationAidesSociales, demandeurEmploi, dateDebutSimulation, numeroMoisSimule);
                if(aidePrimeActiviteOptional.isPresent()) {
                    AideSociale aideSocialeMoisPrecedent = aidePrimeActiviteOptional.get();
                    aidesEligiblesPourCeMois.put(AidesSociales.PRIME_ACTIVITE.getCode(), aideSocialeMoisPrecedent);
                }
                //sinon on reporte le montant de la prime d'activité de N-1
            } else  {
                AideSociale primeActiviteMoisPRecedent = primeActiviteMoisPrecedent.get();
                AideSociale primeActiviteMoisSimule = creerAidesSocialePrimeActivite(primeActiviteMoisPRecedent.getMontant(),true); 
                aidesEligiblesPourCeMois.put(AidesSociales.PRIME_ACTIVITE.getCode(), primeActiviteMoisSimule);                    
            }
        } 
    }
    
    public AideSociale creerAidesSocialePrimeActivite(float montantPrimeActivite, boolean isAideReportee) {
        AideSociale aidePrimeActivite = new AideSociale();
        aidePrimeActivite.setCode(AidesSociales.PRIME_ACTIVITE.getCode());
        Optional<String> detailAideOptional = aideSocialeUtile.getDescription(AidesSociales.PRIME_ACTIVITE.getNomFichierDetail());
        if(detailAideOptional.isPresent()) {
            aidePrimeActivite.setDetail(detailAideOptional.get());            
        }
        aidePrimeActivite.setMontant(montantPrimeActivite); 
        aidePrimeActivite.setNom(AidesSociales.PRIME_ACTIVITE.getNom());
        aidePrimeActivite.setOrganisme(Organismes.CAF.getNomCourt());
        aidePrimeActivite.setReportee(isAideReportee);
        return aidePrimeActivite;
    }
    
    public Optional<AideSociale> getPrimeActiviteMoisPrecedent(List<SimulationMensuelle> simulationsMensuelles, int numeroMoisSimule) {
        int moisNMoins1 = numeroMoisSimule - 1; 
        return getPrimeActiviteSimuleePourNumeroMois(simulationsMensuelles, moisNMoins1);
    }
    
    private boolean isPrimeActiviteACalculer(Optional<AideSociale> primeActiviteMoisPrecedent, int numeroMoisSimule, DemandeurEmploi demandeurEmploi) {
        if(primeActiviteMoisPrecedent.isEmpty()) {    
            
            //Si le demandeur est bénéficiare de l'ASS et de l'AAH, c'est la temporatlité de l'ASS qui est appliquée
            if(beneficiaireAidesSocialesUtile.isBeneficiaireASS(demandeurEmploi) || (beneficiaireAidesSocialesUtile.isBeneficiaireASS(demandeurEmploi) && beneficiaireAidesSocialesUtile.isBeneficiaireAAH(demandeurEmploi))) {
                return isPrimeActiviteACalculerDemandeurASS(demandeurEmploi, numeroMoisSimule);
            } else if(beneficiaireAidesSocialesUtile.isBeneficiaireAAH(demandeurEmploi)) {
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

    private Optional<AideSociale> getPrimeActiviteSimuleePourNumeroMois(List<SimulationMensuelle> simulationsMensuelles, int numeroMois) {
        int indexMoisPrecedent = numeroMois - 1;
        if(indexMoisPrecedent >= 0 && indexMoisPrecedent < simulationsMensuelles.size()) {
            SimulationMensuelle simulationMensuelle = simulationsMensuelles.get(indexMoisPrecedent);
            if(simulationMensuelle.getMesAides() != null) {
                AideSociale aideSociale = simulationMensuelle.getMesAides().get(AidesSociales.PRIME_ACTIVITE.getCode());
                if(aideSociale != null) {
                    return Optional.of(aideSociale);
                }                            
            }
        }
        return Optional.empty();
    }

    private Optional<AideSociale> calculer(SimulationAidesSociales simulationAidesSociales, DemandeurEmploi demandeurEmploi, LocalDate dateDebutSimulation, int numeroMoisSimule) {
        OpenFiscaRetourSimulation openFiscaRetourSimulation = openFiscaClient.calculerPrimeActivite(simulationAidesSociales, demandeurEmploi, dateDebutSimulation, numeroMoisSimule);
        if(openFiscaRetourSimulation.getMontantPrimeActivite() > 0) {
            return Optional.of(creerAidesSocialePrimeActivite(openFiscaRetourSimulation.getMontantPrimeActivite(), false));
        }
        return Optional.empty();
    }
   
    private boolean isMoisPourCalculPrimeActiviteASS(int numeroMoisSimule, DemandeurEmploi demandeurEmploi) {
        int nombreMoisTravaillesDerniersMois = ressourcesFinancieresUtile.getNombreMoisTravaillesDerniersMois(demandeurEmploi, false);
        return nombreMoisTravaillesDerniersMois == 1 && numeroMoisSimule == 4 
           || (nombreMoisTravaillesDerniersMois == 2 && (numeroMoisSimule == 3 || numeroMoisSimule == 6)) 
           || (nombreMoisTravaillesDerniersMois == 3 && (numeroMoisSimule == 2 || numeroMoisSimule == 5));
    }
}
