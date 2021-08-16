package fr.poleemploi.estime.logique.simulateur.aides.caf.utile;

import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fr.poleemploi.estime.clientsexternes.openfisca.OpenFiscaClient;
import fr.poleemploi.estime.clientsexternes.openfisca.OpenFiscaRetourSimulation;
import fr.poleemploi.estime.commun.enumerations.Aides;
import fr.poleemploi.estime.commun.enumerations.Organismes;
import fr.poleemploi.estime.logique.simulateur.aides.utile.AideUtile;
import fr.poleemploi.estime.services.ressources.Aide;
import fr.poleemploi.estime.services.ressources.DemandeurEmploi;
import fr.poleemploi.estime.services.ressources.SimulationAides;

@Component
public class RsaAvecPrimeActiviteUtile {
    
    @Autowired
    private OpenFiscaClient openFiscaClient;
    
    @Autowired
    private AideUtile aideUtile;
    
    @Autowired
    private PrimeActiviteUtile primeActiviteUtile;


    public void simulerAides(SimulationAides simulationAides, Map<String, Aide>  aidesPourCeMois, LocalDate dateDebutSimulation, int numeroMoisSimule, DemandeurEmploi demandeurEmploi) {
        int prochaineDeclarationRSA = demandeurEmploi.getRessourcesFinancieres().getAidesCAF().getProchaineDeclarationRSA();
        if(isRSAACalculer(numeroMoisSimule, prochaineDeclarationRSA)) {
            calculerRsaEtPrimeActivite(simulationAides, aidesPourCeMois, dateDebutSimulation, numeroMoisSimule, demandeurEmploi);
        } else {
            reporterRsaEtPrimeActivite(simulationAides, aidesPourCeMois, numeroMoisSimule, demandeurEmploi, prochaineDeclarationRSA);
        }
    }

    private void reporterRsaEtPrimeActivite(SimulationAides simulationAides, Map<String, Aide> aidesPourCeMois, int numeroMoisSimule, DemandeurEmploi demandeurEmploi, int prochaineDeclarationRSA) {
        Optional<Aide> rsaMoisPrecedent = getRSASimuleeMoisPrecedent(simulationAides, numeroMoisSimule);
        if (rsaMoisPrecedent.isPresent()) {
            aidesPourCeMois.put(Aides.RSA.getCode(), rsaMoisPrecedent.get());
        } else if(isEligiblePourReportRSA(prochaineDeclarationRSA, numeroMoisSimule)) {
            aidesPourCeMois.put(Aides.RSA.getCode(), getRSADeclare(demandeurEmploi));
        }
        Optional<Aide> primeActiviteMoisPrecedent = primeActiviteUtile.getPrimeActiviteMoisPrecedent(simulationAides, numeroMoisSimule);
        if (primeActiviteMoisPrecedent.isPresent()) {
            aidesPourCeMois.put(Aides.PRIME_ACTIVITE.getCode(), primeActiviteMoisPrecedent.get());       
        }
    }
    
    private void calculerRsaEtPrimeActivite(SimulationAides simulationAides, Map<String, Aide> aidesPourCeMois, LocalDate dateDebutSimulation, int numeroMoisSimule, DemandeurEmploi demandeurEmploi) {
        OpenFiscaRetourSimulation openFiscaRetourSimulation = openFiscaClient.calculerRsaAvecPrimeActivite(simulationAides, demandeurEmploi, dateDebutSimulation, numeroMoisSimule);
        if(openFiscaRetourSimulation.getMontantRSA() > 0) {
            Aide rsa = creerAideeRSA(openFiscaRetourSimulation.getMontantRSA(), false);
            aidesPourCeMois.put(rsa.getCode(), rsa);
        }
        if(openFiscaRetourSimulation.getMontantPrimeActivite() > 0) {
            Aide primeActivite = primeActiviteUtile.creerAidePrimeActivite(openFiscaRetourSimulation.getMontantPrimeActivite(), false);
            aidesPourCeMois.put(primeActivite.getCode(), primeActivite);
        }        
    }
    
    private Aide creerAideeRSA(float montantRSA, boolean isAideReportee) {
        Aide aideRSA = new Aide();
        aideRSA.setCode(Aides.RSA.getCode());
        aideRSA.setMontant(montantRSA); 
        aideRSA.setNom(Aides.RSA.getNom());
        aideRSA.setOrganisme(Organismes.CAF.getNomCourt());
        aideRSA.setReportee(isAideReportee);
        return aideRSA;
    }
    
    private Aide getRSADeclare(DemandeurEmploi demandeurEmploi) {        
        float montantDeclare = demandeurEmploi.getRessourcesFinancieres().getAidesCAF().getAllocationRSA();
        return creerAideeRSA(montantDeclare, true);        
    }
    
    private Optional<Aide> getRSASimuleeMoisPrecedent(SimulationAides simulationAides, int numeroMoisSimule) {
        int moisNMoins1 = numeroMoisSimule - 1;
        return aideUtile.getAidePourCeMoisSimule(simulationAides, Aides.RSA.getCode(), moisNMoins1);
    }
    
    private boolean isEligiblePourReportRSA(int prochaineDeclarationRSA, int numeroMoisSimule) {
        return (numeroMoisSimule == 1 && (prochaineDeclarationRSA == 0 || prochaineDeclarationRSA == 2 || prochaineDeclarationRSA == 3)) 
                || (numeroMoisSimule == 2 && (prochaineDeclarationRSA == 0 || prochaineDeclarationRSA == 3)) ;
    }
    
    private boolean isRSAACalculer(int numeroMoisSimule, int prochaineDeclarationRSA) {
        return ((prochaineDeclarationRSA == numeroMoisSimule)
                || (prochaineDeclarationRSA == numeroMoisSimule-3)
                || (prochaineDeclarationRSA == numeroMoisSimule-6));       
    }
}