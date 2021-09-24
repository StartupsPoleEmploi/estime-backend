package fr.poleemploi.estime.logique.simulateur.aides.caf.utile;

import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fr.poleemploi.estime.clientsexternes.openfisca.OpenFiscaClient;
import fr.poleemploi.estime.clientsexternes.openfisca.OpenFiscaRetourSimulation;
import fr.poleemploi.estime.commun.enumerations.Aides;
import fr.poleemploi.estime.commun.enumerations.MessagesInformatifs;
import fr.poleemploi.estime.commun.enumerations.Organismes;
import fr.poleemploi.estime.commun.utile.demandeuremploi.BeneficiaireAidesUtile;
import fr.poleemploi.estime.commun.utile.demandeuremploi.PeriodeTravailleeAvantSimulationUtile;
import fr.poleemploi.estime.commun.utile.demandeuremploi.RessourcesFinancieresUtile;
import fr.poleemploi.estime.logique.simulateur.aides.utile.AideUtile;
import fr.poleemploi.estime.services.ressources.Aide;
import fr.poleemploi.estime.services.ressources.DemandeurEmploi;
import fr.poleemploi.estime.services.ressources.SimulationAides;

@Component
public class PrimeActiviteAidesFamilialesUtile {

    @Autowired
    private OpenFiscaClient openFiscaClient;

    @Autowired
    private AideUtile aideeUtile;

    @Autowired
    private BeneficiaireAidesUtile beneficiaireAidesUtile;

    @Autowired
    private PeriodeTravailleeAvantSimulationUtile periodeTravailleeAvantSimulationUtile;

    @Autowired
    private RessourcesFinancieresUtile ressourcesFinancieresUtile;

    public void simulerAide(SimulationAides simulationAides, Map<String, Aide> aidesPourCeMois, LocalDate dateDebutSimulation, int numeroMoisSimule, DemandeurEmploi demandeurEmploi) {
        Optional<Aide> primeActiviteMoisPrecedent = getPrimeActiviteMoisPrecedent(simulationAides, numeroMoisSimule);
        if (primeActiviteMoisPrecedent.isPresent()) {
            reporterPrimeActiviteMoisPrecedent(simulationAides, aidesPourCeMois, dateDebutSimulation, numeroMoisSimule, demandeurEmploi, primeActiviteMoisPrecedent);
        } else if (isPrimeActiviteACalculer(primeActiviteMoisPrecedent, numeroMoisSimule, demandeurEmploi)) {
            calculerPrimeActivite(simulationAides, aidesPourCeMois, dateDebutSimulation, numeroMoisSimule, demandeurEmploi);
        }
    }

    public Aide creerAidePrimeActivite(float montantPrimeActivite, boolean isAideReportee) {
        Aide aidePrimeActivite = new Aide();
        aidePrimeActivite.setCode(Aides.PRIME_ACTIVITE.getCode());
        Optional<String> detailAideOptional = aideeUtile.getDescription(Aides.PRIME_ACTIVITE.getNomFichierDetail());
        if (detailAideOptional.isPresent()) {
            aidePrimeActivite.setDetail(detailAideOptional.get());
        }
        aidePrimeActivite.setMessageAlerte(MessagesInformatifs.PPA_AUTOMATIQUE_SI_BENEFICIAIRE_RSA.getMessage());
        aidePrimeActivite.setMontant(montantPrimeActivite);
        aidePrimeActivite.setNom(Aides.PRIME_ACTIVITE.getNom());
        aidePrimeActivite.setOrganisme(Organismes.CAF.getNomCourt());
        aidePrimeActivite.setReportee(isAideReportee);
        return aidePrimeActivite;
    }

    public Optional<Aide> getPrimeActiviteMoisPrecedent(SimulationAides simulationAides, int numeroMoisSimule) {
        int moisNMoins1 = numeroMoisSimule - 1;
        return aideeUtile.getAidePourCeMoisSimule(simulationAides, Aides.PRIME_ACTIVITE.getCode(), moisNMoins1);
    }

    private void calculerPrimeActivite(SimulationAides simulationAides, Map<String, Aide> aidesPourCeMois, LocalDate dateDebutSimulation, int numeroMoisSimule, DemandeurEmploi demandeurEmploi) {
        OpenFiscaRetourSimulation openFiscaRetourSimulation = openFiscaClient.calculerPrimeActivite(simulationAides, demandeurEmploi, dateDebutSimulation, numeroMoisSimule);
        if (openFiscaRetourSimulation.getMontantPrimeActivite() > 0) {
            Aide primeActivite = creerAidePrimeActivite(openFiscaRetourSimulation.getMontantPrimeActivite(), false);
            aidesPourCeMois.put(Aides.PRIME_ACTIVITE.getCode(), primeActivite);
        }
    }

    private boolean isPrimeActiviteACalculer(Optional<Aide> primeActiviteMoisPrecedent, int numeroMoisSimule, DemandeurEmploi demandeurEmploi) {
        if (primeActiviteMoisPrecedent.isEmpty()) {
            // Si le demandeur est bénéficiare de l'ASS et de l'AAH, c'est la temporatlité de l'ASS qui est appliquée
            if (beneficiaireAidesUtile.isBeneficiaireASS(demandeurEmploi) || (beneficiaireAidesUtile.isBeneficiaireASS(demandeurEmploi) && beneficiaireAidesUtile.isBeneficiaireAAH(demandeurEmploi))) {
                return isPrimeActiviteACalculerDemandeurASS(demandeurEmploi, numeroMoisSimule);
            } else if (beneficiaireAidesUtile.isBeneficiaireAAH(demandeurEmploi)) {
                return isPrimeActiviteACalculerDemandeurAAH(numeroMoisSimule);
            }
        }
        return false;
    }

    private boolean isPrimeActiviteACalculerDemandeurASS(DemandeurEmploi demandeurEmploi, int numeroMoisSimule) {
        return !ressourcesFinancieresUtile.hasTravailleAuCoursDerniersMoisAvantSimulation(demandeurEmploi) && numeroMoisSimule == 5
                || (ressourcesFinancieresUtile.hasTravailleAuCoursDerniersMoisAvantSimulation(demandeurEmploi) && isMoisPourCalculPrimeActiviteASS(numeroMoisSimule, demandeurEmploi));
    }

    private boolean isPrimeActiviteACalculerDemandeurAAH(int numeroMoisSimule) {
        return numeroMoisSimule == 2 || numeroMoisSimule == 5;
    }

    private boolean isMoisPourCalculPrimeActiviteASS(int numeroMoisSimule, DemandeurEmploi demandeurEmploi) {
        int nombreMoisTravaillesDerniersMois = periodeTravailleeAvantSimulationUtile.getNombreMoisTravaillesAuCoursDes3DerniersMoisAvantSimulation(demandeurEmploi);
        return nombreMoisTravaillesDerniersMois == 1 && numeroMoisSimule == 4 || (nombreMoisTravaillesDerniersMois == 2 && (numeroMoisSimule == 3 || numeroMoisSimule == 6))
                || (nombreMoisTravaillesDerniersMois == 3 && (numeroMoisSimule == 2 || numeroMoisSimule == 5));
    }

    /**
     * Méthode permettant de reporter le montant de la prime d'activité ou de recalculer son montant. Le montant de la prime d'activité calculée à mois N
     * est reporté sur les 2 prochains mois N+1 N+2, et doit être recalculé après les 2 mois de report.
     * 
     * Exemple :
     * 
     * | mois 1 | mois 2 | mois 3 | mois 4 | | montant calculé | montant reporté | montant reporté | montant calculé |
     * 
     */
    private void reporterPrimeActiviteMoisPrecedent(SimulationAides simulationAides, Map<String, Aide> aidesPourCeMois, LocalDate dateDebutSimulation, int numeroMoisSimule,
            DemandeurEmploi demandeurEmploi, Optional<Aide> primeActiviteMoisPrecedentOptional) {

        // le montant calculé au mois N est reporté au mois N+1 et N+2, la validité du montant s'étale donc sur 3 mois
        int moisMoinsPeriodeValiditeMontant = numeroMoisSimule - 3;

        Optional<Aide> primeActiviteDebutPeriodeValiditeMontant = aideeUtile.getAidePourCeMoisSimule(simulationAides, Aides.PRIME_ACTIVITE.getCode(), moisMoinsPeriodeValiditeMontant);

        // si montant prime d'activité en N - NBR_MOIS_VALIDITE_MONTANT n'a pas été reporté alors on recalcule le montant
        // sinon on reporte le montant de la prime d'activité précédente si prime d'activité il y a eu.
        if (primeActiviteDebutPeriodeValiditeMontant.isPresent() && !primeActiviteDebutPeriodeValiditeMontant.get().isReportee()) {
            calculerPrimeActivite(simulationAides, aidesPourCeMois, dateDebutSimulation, numeroMoisSimule, demandeurEmploi);
        } else if (primeActiviteMoisPrecedentOptional.isPresent()) {
            Aide primeActiviteMoisPrecedent = primeActiviteMoisPrecedentOptional.get();
            Aide primeActiviteMoisSimule = creerAidePrimeActivite(primeActiviteMoisPrecedent.getMontant(), true);
            aidesPourCeMois.put(Aides.PRIME_ACTIVITE.getCode(), primeActiviteMoisSimule);
        }
    }
}
