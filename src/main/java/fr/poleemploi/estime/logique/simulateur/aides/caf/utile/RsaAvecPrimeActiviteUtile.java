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

    public void simulerAides(SimulationAides simulationAides, Map<String, Aide> aidesPourCeMois, LocalDate dateDebutSimulation, int numeroMoisSimule, DemandeurEmploi demandeurEmploi) {
        int prochaineDeclarationTrimestrielle = demandeurEmploi.getRessourcesFinancieres().getAidesCAF().getProchaineDeclarationTrimestrielle();
        if (isRSAACalculer(numeroMoisSimule, prochaineDeclarationTrimestrielle)) {
            reporterRsaEtPrimeActivite(simulationAides, aidesPourCeMois, numeroMoisSimule, demandeurEmploi, prochaineDeclarationTrimestrielle);
        } else if (isRSAAVerser(numeroMoisSimule, prochaineDeclarationTrimestrielle)) {
            calculerRsaEtPrimeActivite(simulationAides, aidesPourCeMois, dateDebutSimulation, numeroMoisSimule - 1, demandeurEmploi);
        } else {
            reporterRsaEtPrimeActivite(simulationAides, aidesPourCeMois, numeroMoisSimule, demandeurEmploi, prochaineDeclarationTrimestrielle);
        }
    }

    private void reporterRsaEtPrimeActivite(SimulationAides simulationAides, Map<String, Aide> aidesPourCeMois, int numeroMoisSimule, DemandeurEmploi demandeurEmploi,
            int prochaineDeclarationTrimestrielle) {
        Optional<Aide> rsaMoisPrecedent = getRSASimuleeMoisPrecedent(simulationAides, numeroMoisSimule);
        if (rsaMoisPrecedent.isPresent()) {
            aidesPourCeMois.put(Aides.RSA.getCode(), rsaMoisPrecedent.get());
        } else if (isEligiblePourReportRSADeclare(prochaineDeclarationTrimestrielle, numeroMoisSimule)) {
            aidesPourCeMois.put(Aides.RSA.getCode(), getRSADeclare(demandeurEmploi));
        }
        Optional<Aide> primeActiviteMoisPrecedent = primeActiviteUtile.getPrimeActiviteMoisPrecedent(simulationAides, numeroMoisSimule);
        if (primeActiviteMoisPrecedent.isPresent()) {
            aidesPourCeMois.put(Aides.PRIME_ACTIVITE.getCode(), primeActiviteMoisPrecedent.get());
        }
    }

    private void calculerRsaEtPrimeActivite(SimulationAides simulationAides, Map<String, Aide> aidesPourCeMois, LocalDate dateDebutSimulation, int numeroMoisSimule, DemandeurEmploi demandeurEmploi) {
        OpenFiscaRetourSimulation openFiscaRetourSimulation = openFiscaClient.calculerRsaAvecPrimeActivite(simulationAides, demandeurEmploi, dateDebutSimulation, numeroMoisSimule);

        if (openFiscaRetourSimulation.getMontantRSA() > 0) {
            Aide rsa = creerAideeRSA(openFiscaRetourSimulation.getMontantRSA(), false);
            aidesPourCeMois.put(rsa.getCode(), rsa);
        }
        if (openFiscaRetourSimulation.getMontantPrimeActivite() > 0) {
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

    private boolean isEligiblePourReportRSADeclare(int prochaineDeclarationTrimestrielle, int numeroMoisSimule) {
        return numeroMoisSimule <= prochaineDeclarationTrimestrielle;
    }

    /**
     * Fonction permettant de déterminer si le RSA doit être calculé ce mois-ci
     * 
     * @param numeroMoisSimule
     * @param prochaineDeclarationTrimestrielle
     * @return
     */
    private boolean isRSAACalculer(int numeroMoisSimule, int prochaineDeclarationTrimestrielle) {
        return ((prochaineDeclarationTrimestrielle == numeroMoisSimule) || (prochaineDeclarationTrimestrielle == numeroMoisSimule - 3) || (prochaineDeclarationTrimestrielle == numeroMoisSimule - 6));
    }

    /**
     * Fonction permettant de déterminer si on a calculé le montant du RSA le mois précédent et s'il doit être versé ce mois-ci
     * 
     * @param numeroMoisSimule
     * @param prochaineDeclarationTrimestrielle
     * @return
     */
    private boolean isRSAAVerser(int numeroMoisSimule, int prochaineDeclarationTrimestrielle) {
        return (((prochaineDeclarationTrimestrielle == 0) && (numeroMoisSimule == 1 || numeroMoisSimule == 4))
                || ((prochaineDeclarationTrimestrielle == 1) && (numeroMoisSimule == 2 || numeroMoisSimule == 5))
                || ((prochaineDeclarationTrimestrielle == 2) && (numeroMoisSimule == 3 || numeroMoisSimule == 6)) || ((prochaineDeclarationTrimestrielle == 3) && (numeroMoisSimule == 4)));
    }
}
