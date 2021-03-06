package fr.poleemploi.estime.logique.simulateuraidessociales.caf.aides;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fr.poleemploi.estime.clientsexternes.openfisca.OpenFiscaClient;
import fr.poleemploi.estime.commun.enumerations.AidesSociales;
import fr.poleemploi.estime.commun.enumerations.Organismes;
import fr.poleemploi.estime.commun.utile.demandeuremploi.BeneficiaireAidesSocialesUtile;
import fr.poleemploi.estime.commun.utile.demandeuremploi.InformationsPersonnellesUtile;
import fr.poleemploi.estime.commun.utile.demandeuremploi.RessourcesFinancieresUtile;
import fr.poleemploi.estime.logique.simulateuraidessociales.utile.AideSocialeUtile;
import fr.poleemploi.estime.services.ressources.AideSociale;
import fr.poleemploi.estime.services.ressources.DemandeurEmploi;
import fr.poleemploi.estime.services.ressources.SimulationAidesSociales;
import fr.poleemploi.estime.services.ressources.SimulationMensuelle;

@Component
public class PrimeActivite {

    public static final int AGE_MAX_PERSONNE_A_CHARGE = 25;
    //le montant calculé au mois N est reporté au mois N+1 et N+2, la validité du montant s'étale donc sur 3 mois
    public static final int NBR_MOIS_VALIDITE_MONTANT = 3;

    @Autowired
    private OpenFiscaClient openFiscaClient;

    @Autowired
    private InformationsPersonnellesUtile informationsPersonnellesUtile;

    @Autowired
    private AideSocialeUtile aideSocialeUtile;

    @Autowired
    private BeneficiaireAidesSocialesUtile beneficiaireAidesSocialesUtile;
    
    @Autowired
    private RessourcesFinancieresUtile ressourcesFinancieresUtile;
    

    public boolean isEligible(DemandeurEmploi demandeurEmploi) {
        return informationsPersonnellesUtile.isFrancais(demandeurEmploi.getInformationsPersonnelles())
                || informationsPersonnellesUtile.isEuropeenOuSuisse(demandeurEmploi.getInformationsPersonnelles())
                || (informationsPersonnellesUtile.isNotFrancaisOuEuropeenOuSuisse(demandeurEmploi.getInformationsPersonnelles())
                        && informationsPersonnellesUtile.isTitreSejourEnFranceValide(demandeurEmploi.getInformationsPersonnelles()));            
    }

    public void simulerPrimeActivite(SimulationAidesSociales simulationAidesSociales, Map<String, AideSociale>  aidesEligiblesPourCeMois, LocalDate dateDebutSimulation, int numeroMoisSimule, DemandeurEmploi demandeurEmploi) {
        List<SimulationMensuelle> simulationsMensuelles = simulationAidesSociales.getSimulationsMensuelles();

        Optional<AideSociale> primeActiviteMoisPrecedent = getPrimeActiviteSimuleeMoisPrecedent(simulationsMensuelles, numeroMoisSimule);     

        if(primeActiviteMoisPrecedent.isPresent()){
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
                AideSociale primeActiviteMoisSimule = creerAideAvecMontantPrimeActiviteMoisPrecedent(primeActiviteMoisPrecedent.get());
                aidesEligiblesPourCeMois.put(AidesSociales.PRIME_ACTIVITE.getCode(), primeActiviteMoisSimule);                    
            }
        } else if(isPrimeActiviteACalculerEnFonctionDuRSA(primeActiviteMoisPrecedent, numeroMoisSimule, demandeurEmploi)) {
            Optional<AideSociale> aidePrimeActiviteOptional = calculer(simulationAidesSociales, demandeurEmploi, dateDebutSimulation, numeroMoisSimule);
            if(aidePrimeActiviteOptional.isPresent()) {
                AideSociale aideSocialeMoisPrecedent = aidePrimeActiviteOptional.get();

                aidesEligiblesPourCeMois.put(AidesSociales.PRIME_ACTIVITE.getCode(), aideSocialeMoisPrecedent);
            }
        } 
    }

    private boolean isPrimeActiviteACalculerEnFonctionDuRSA(Optional<AideSociale> primeActiviteMoisPrecedent, int numeroMoisSimule, DemandeurEmploi demandeurEmploi) {
        if(beneficiaireAidesSocialesUtile.isBeneficiaireRSA(demandeurEmploi)) {
            Integer prochaineDeclarationRSA = demandeurEmploi.getRessourcesFinancieres().getAllocationsCAF().getProchaineDeclarationRSA();
            return isRSAACalculer(numeroMoisSimule, prochaineDeclarationRSA);
        } 
        return isPrimeActiviteACalculer(primeActiviteMoisPrecedent, numeroMoisSimule, demandeurEmploi);
    }
    
    private boolean isPrimeActiviteACalculer(Optional<AideSociale> primeActiviteMoisPrecedent, int numeroMoisSimule, DemandeurEmploi demandeurEmploi) {
        if(primeActiviteMoisPrecedent.isEmpty()) {
            if(beneficiaireAidesSocialesUtile.isBeneficiaireASS(demandeurEmploi) && beneficiaireAidesSocialesUtile.isBeneficiaireAAH(demandeurEmploi)) {
                if(ressourcesFinancieresUtile.hasTravailleAuCoursDerniersMois(demandeurEmploi)) {
                    return isMoisPourCalculPrimeActiviteASS(numeroMoisSimule, demandeurEmploi);                    
                } else  {
                    return isPrimeActiviteACalculerDemandeurAAH(numeroMoisSimule);
                }
            } else {                
                if(beneficiaireAidesSocialesUtile.isBeneficiaireAAH(demandeurEmploi)) {
                    return isPrimeActiviteACalculerDemandeurAAH(numeroMoisSimule);
                }
                if(beneficiaireAidesSocialesUtile.isBeneficiaireASS(demandeurEmploi)) {
                    return isPrimeActiviteACalculerDemandeurASS(demandeurEmploi, numeroMoisSimule);
                }
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

    private Optional<AideSociale> getPrimeActiviteSimuleeMoisPrecedent(List<SimulationMensuelle> simulationsMensuelles, int numeroMoisSimule) {
        int moisNMoins1 = numeroMoisSimule - 1; 
        return getPrimeActiviteSimuleePourNumeroMois(simulationsMensuelles, moisNMoins1);
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
        float montantPrimeActivite = openFiscaClient.calculerMontantPrimeActivite(simulationAidesSociales, demandeurEmploi, dateDebutSimulation, numeroMoisSimule);
        if(montantPrimeActivite > 0) {
            AideSociale aidePrimeActivite = new AideSociale();
            aidePrimeActivite.setCode(AidesSociales.PRIME_ACTIVITE.getCode());
            Optional<String> detailAideOptional = aideSocialeUtile.getDescription(AidesSociales.PRIME_ACTIVITE.getNomFichierDetail());
            if(detailAideOptional.isPresent()) {
                aidePrimeActivite.setDetail(detailAideOptional.get());            
            }
            aidePrimeActivite.setMontant(montantPrimeActivite); 
            aidePrimeActivite.setNom(AidesSociales.PRIME_ACTIVITE.getNom());
            aidePrimeActivite.setOrganisme(Organismes.CAF.getNomCourt());
            aidePrimeActivite.setReportee(false);
            return Optional.of(aidePrimeActivite);
        }
        return Optional.empty();
    }
   
    private AideSociale creerAideAvecMontantPrimeActiviteMoisPrecedent(AideSociale aidePrimeActiviteMoisPrecedent) {
        AideSociale aidePrimeActivite = new AideSociale();
        aidePrimeActivite.setCode(aidePrimeActiviteMoisPrecedent.getCode());
        aidePrimeActivite.setDetail(aidePrimeActiviteMoisPrecedent.getDetail());            
        aidePrimeActivite.setMontant(aidePrimeActiviteMoisPrecedent.getMontant()); 
        aidePrimeActivite.setNom(aidePrimeActiviteMoisPrecedent.getNom());
        aidePrimeActivite.setOrganisme(Organismes.CAF.getNomCourt());
        aidePrimeActivite.setReportee(true);
        return aidePrimeActivite;
    }
    
    private boolean isRSAACalculer(int numeroMoisSimule, int prochaineDeclarationRSA) {
        return ((prochaineDeclarationRSA == numeroMoisSimule)
                || (prochaineDeclarationRSA == numeroMoisSimule-3)
                || (prochaineDeclarationRSA == numeroMoisSimule-6));       
    }
    
    private boolean isMoisPourCalculPrimeActiviteASS(int numeroMoisSimule, DemandeurEmploi demandeurEmploi) {
        int nombreMoisTravaillesDerniersMois = demandeurEmploi.getRessourcesFinancieres().getNombreMoisTravaillesDerniersMois().intValue();
        return nombreMoisTravaillesDerniersMois == 1 && numeroMoisSimule == 4 
           || (nombreMoisTravaillesDerniersMois == 2 && (numeroMoisSimule == 3 || numeroMoisSimule == 6)) 
           || (nombreMoisTravaillesDerniersMois == 3 && (numeroMoisSimule == 2 || numeroMoisSimule == 5));
    }
}
