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
import fr.poleemploi.estime.commun.utile.demandeuremploi.InformationsPersonnellesUtile;
import fr.poleemploi.estime.services.ressources.AideSociale;
import fr.poleemploi.estime.services.ressources.DemandeurEmploi;
import fr.poleemploi.estime.services.ressources.SimulationAidesSociales;
import fr.poleemploi.estime.services.ressources.SimulationMensuelle;

@Component
public class RSA {
    //le montant calculé au mois N est reporté au mois N+1 et N+2, la validité du montant s'étale donc sur 3 mois
    public static final int NBR_MOIS_VALIDITE_MONTANT = 3;

    @Autowired
    private OpenFiscaClient openFiscaClient;

    @Autowired
    private InformationsPersonnellesUtile informationsPersonnellesUtile;

    public boolean isEligible(DemandeurEmploi demandeurEmploi) {
        return informationsPersonnellesUtile.isFrancais(demandeurEmploi.getInformationsPersonnelles())
                || informationsPersonnellesUtile.isEuropeenOuSuisse(demandeurEmploi.getInformationsPersonnelles())
                || (informationsPersonnellesUtile.isNotFrancaisOuEuropeenOuSuisse(demandeurEmploi.getInformationsPersonnelles())
                        && informationsPersonnellesUtile.isTitreSejourEnFranceValide(demandeurEmploi.getInformationsPersonnelles()));            
    }

    public void simulerRSA(SimulationAidesSociales simulationAidesSociales, Map<String, AideSociale>  aidesEligiblesPourCeMois, LocalDate dateDebutSimulation, int numeroMoisSimule, DemandeurEmploi demandeurEmploi) {
        List<SimulationMensuelle> simulationsMensuelles = simulationAidesSociales.getSimulationsMensuelles();
        int prochaineDeclarationRSA = demandeurEmploi.getRessourcesFinancieres().getAllocationsCAF().getProchaineDeclarationRSA();
        if(isRSAACalculer(numeroMoisSimule, prochaineDeclarationRSA)) {
            Optional<AideSociale> aideRsaOptional = calculer(simulationAidesSociales, demandeurEmploi, dateDebutSimulation, numeroMoisSimule);
            if(aideRsaOptional.isPresent()) {
                aidesEligiblesPourCeMois.put(AidesSociales.RSA.getCode(), aideRsaOptional.get());
            }
        } else {
            Optional<AideSociale> rsaMoisPrecedent = getRSASimuleeMoisPrecedent(simulationsMensuelles, numeroMoisSimule);
            if (rsaMoisPrecedent.isPresent()) {
                aidesEligiblesPourCeMois.put(AidesSociales.RSA.getCode(), rsaMoisPrecedent.get());
            } else if(isEligiblePourReportRSA(prochaineDeclarationRSA, numeroMoisSimule)) {
                aidesEligiblesPourCeMois.put(AidesSociales.RSA.getCode(), getRSADeclare(demandeurEmploi));
            }
        }
    }
    
    private boolean isEligiblePourReportRSA(int prochaineDeclarationRSA, int numeroMoisSimule) {
        return (numeroMoisSimule == 1 && (prochaineDeclarationRSA == 0 || prochaineDeclarationRSA == 2 || prochaineDeclarationRSA == 3)) 
                || (numeroMoisSimule == 2 && (prochaineDeclarationRSA == 0 || prochaineDeclarationRSA == 3)) ;
    }

    private Optional<AideSociale> calculer(SimulationAidesSociales simulationAidesSociales, DemandeurEmploi demandeurEmploi, LocalDate dateDebutSimulation, int numeroMoisSimule) {
        float montantRSA = openFiscaClient.calculerMontantRSA(simulationAidesSociales, demandeurEmploi, dateDebutSimulation, numeroMoisSimule);
        if(montantRSA > 0) {
            AideSociale aideSocialeRSA = creerAideSocialeRSA(montantRSA, false);
            return Optional.of(aideSocialeRSA);
        }
        return Optional.empty();
    }
    
    private AideSociale creerAideSocialeRSA(float montantRSA, boolean isAideReportee) {
        AideSociale aideRSA = new AideSociale();
        aideRSA.setCode(AidesSociales.RSA.getCode());
        aideRSA.setMontant(montantRSA); 
        aideRSA.setNom(AidesSociales.RSA.getNom());
        aideRSA.setOrganisme(Organismes.CAF.getNomCourt());
        aideRSA.setReportee(isAideReportee);
        return aideRSA;
    }
    
    private AideSociale getRSADeclare(DemandeurEmploi demandeurEmploi) {        
        float montantDeclare = demandeurEmploi.getRessourcesFinancieres().getAllocationsCAF().getAllocationMensuelleNetRSA();
        return creerAideSocialeRSA(montantDeclare, true);        
    }

    private Optional<AideSociale> getRSASimuleePourNumeroMois(List<SimulationMensuelle> simulationsMensuelles, int numeroMois) {
        int indexMoisPrecedent = numeroMois - 1;
        if(indexMoisPrecedent >= 0 && indexMoisPrecedent < simulationsMensuelles.size()) {
            SimulationMensuelle simulationMensuelle = simulationsMensuelles.get(indexMoisPrecedent);
            if(simulationMensuelle.getMesAides() != null) {
                AideSociale aideSociale = simulationMensuelle.getMesAides().get(AidesSociales.RSA.getCode());
                if(aideSociale != null) {
                    return Optional.of(aideSociale);
                }                            
            }
        }
        return Optional.empty();
    }
    
    private Optional<AideSociale> getRSASimuleeMoisPrecedent(List<SimulationMensuelle> simulationsMensuelles, int numeroMoisSimule) {
        int moisNMoins1 = numeroMoisSimule - 1;
        return getRSASimuleePourNumeroMois(simulationsMensuelles, moisNMoins1);
    }
    
    private boolean isRSAACalculer(int numeroMoisSimule, int prochaineDeclarationRSA) {
        return ((prochaineDeclarationRSA == numeroMoisSimule)
                || (prochaineDeclarationRSA == numeroMoisSimule-3)
                || (prochaineDeclarationRSA == numeroMoisSimule-6));       
    }
}
