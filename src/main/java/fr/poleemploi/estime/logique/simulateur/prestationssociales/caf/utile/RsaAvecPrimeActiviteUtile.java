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
import fr.poleemploi.estime.logique.simulateur.prestationssociales.utile.PrestationSocialeUtile;
import fr.poleemploi.estime.services.ressources.DemandeurEmploi;
import fr.poleemploi.estime.services.ressources.PrestationSociale;
import fr.poleemploi.estime.services.ressources.SimulationPrestationsSociales;

@Component
public class RsaAvecPrimeActiviteUtile {
    
    @Autowired
    private OpenFiscaClient openFiscaClient;
    
    @Autowired
    private PrestationSocialeUtile prestationSocialeUtile;
    
    @Autowired
    private PrimeActiviteUtile primeActiviteUtile;


    public void simulerPrestationsSociales(SimulationPrestationsSociales simulationPrestationsSociales, Map<String, PrestationSociale>  prestationsSocialesPourCeMois, LocalDate dateDebutSimulation, int numeroMoisSimule, DemandeurEmploi demandeurEmploi) {
        int prochaineDeclarationRSA = demandeurEmploi.getRessourcesFinancieres().getPrestationsCAF().getProchaineDeclarationRSA();
        if(isRSAACalculer(numeroMoisSimule, prochaineDeclarationRSA)) {
            calculerRsaEtPrimeActivite(simulationPrestationsSociales, prestationsSocialesPourCeMois, dateDebutSimulation, numeroMoisSimule, demandeurEmploi);
        } else {
            reporterRsaEtPrimeActivite(simulationPrestationsSociales, prestationsSocialesPourCeMois, numeroMoisSimule, demandeurEmploi, prochaineDeclarationRSA);
        }
    }

    private void reporterRsaEtPrimeActivite(SimulationPrestationsSociales simulationPrestationsSociales, Map<String, PrestationSociale> prestationsSocialesPourCeMois, int numeroMoisSimule, DemandeurEmploi demandeurEmploi, int prochaineDeclarationRSA) {
        Optional<PrestationSociale> rsaMoisPrecedent = getRSASimuleeMoisPrecedent(simulationPrestationsSociales, numeroMoisSimule);
        if (rsaMoisPrecedent.isPresent()) {
            prestationsSocialesPourCeMois.put(PrestationsSociales.RSA.getCode(), rsaMoisPrecedent.get());
        } else if(isEligiblePourReportRSA(prochaineDeclarationRSA, numeroMoisSimule)) {
            prestationsSocialesPourCeMois.put(PrestationsSociales.RSA.getCode(), getRSADeclare(demandeurEmploi));
        }
        Optional<PrestationSociale> primeActiviteMoisPrecedent = primeActiviteUtile.getPrimeActiviteMoisPrecedent(simulationPrestationsSociales, numeroMoisSimule);
        if (primeActiviteMoisPrecedent.isPresent()) {
            prestationsSocialesPourCeMois.put(PrestationsSociales.PRIME_ACTIVITE.getCode(), primeActiviteMoisPrecedent.get());       
        }
    }
    
    private void calculerRsaEtPrimeActivite(SimulationPrestationsSociales simulationPrestationsSociales, Map<String, PrestationSociale> prestationsSocialesPourCeMois, LocalDate dateDebutSimulation, int numeroMoisSimule, DemandeurEmploi demandeurEmploi) {
        OpenFiscaRetourSimulation openFiscaRetourSimulation = openFiscaClient.calculerRsaAvecPrimeActivite(simulationPrestationsSociales, demandeurEmploi, dateDebutSimulation, numeroMoisSimule);
        if(openFiscaRetourSimulation.getMontantRSA() > 0) {
            PrestationSociale rsa = creerPrestationSocialeeRSA(openFiscaRetourSimulation.getMontantRSA(), false);
            prestationsSocialesPourCeMois.put(rsa.getCode(), rsa);
        }
        if(openFiscaRetourSimulation.getMontantPrimeActivite() > 0) {
            PrestationSociale primeActivite = primeActiviteUtile.creerPrestationSocialePrimeActivite(openFiscaRetourSimulation.getMontantPrimeActivite(), false);
            prestationsSocialesPourCeMois.put(primeActivite.getCode(), primeActivite);
        }        
    }
    
    private PrestationSociale creerPrestationSocialeeRSA(float montantRSA, boolean isAideReportee) {
        PrestationSociale aideRSA = new PrestationSociale();
        aideRSA.setCode(PrestationsSociales.RSA.getCode());
        aideRSA.setMontant(montantRSA); 
        aideRSA.setNom(PrestationsSociales.RSA.getNom());
        aideRSA.setOrganisme(Organismes.CAF.getNomCourt());
        aideRSA.setReportee(isAideReportee);
        return aideRSA;
    }
    
    private PrestationSociale getRSADeclare(DemandeurEmploi demandeurEmploi) {        
        float montantDeclare = demandeurEmploi.getRessourcesFinancieres().getPrestationsCAF().getAllocationMensuelleNetRSA();
        return creerPrestationSocialeeRSA(montantDeclare, true);        
    }
    
    private Optional<PrestationSociale> getRSASimuleeMoisPrecedent(SimulationPrestationsSociales simulationPrestationsSociales, int numeroMoisSimule) {
        int moisNMoins1 = numeroMoisSimule - 1;
        return prestationSocialeUtile.getPrestationSocialePourCeMoisSimule(simulationPrestationsSociales, PrestationsSociales.RSA.getCode(), moisNMoins1);
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
