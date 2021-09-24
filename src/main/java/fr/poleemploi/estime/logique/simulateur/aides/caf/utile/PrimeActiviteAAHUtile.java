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
import fr.poleemploi.estime.logique.simulateur.aides.utile.AideUtile;
import fr.poleemploi.estime.services.ressources.Aide;
import fr.poleemploi.estime.services.ressources.DemandeurEmploi;
import fr.poleemploi.estime.services.ressources.SimulationAides;

@Component
public class PrimeActiviteAAHUtile {

    @Autowired
    private OpenFiscaClient openFiscaClient;

    @Autowired
    private AideUtile aideeUtile;

    @Autowired
    private PrimeActiviteASSUtile primeActiviteUtile;

    public void simulerAide(SimulationAides simulationAides, Map<String, Aide> aidesPourCeMois, LocalDate dateDebutSimulation, int numeroMoisSimule, DemandeurEmploi demandeurEmploi) {

        int prochaineDeclarationTrimestrielle = demandeurEmploi.getRessourcesFinancieres().getAidesCAF().getProchaineDeclarationTrimestrielle();

        if (isPrimeActiviteACalculer(numeroMoisSimule, prochaineDeclarationTrimestrielle)) {
            reporterPrimeActivite(simulationAides, aidesPourCeMois, numeroMoisSimule, demandeurEmploi, prochaineDeclarationTrimestrielle);
        } else if (isPrimeActiviteAVerser(numeroMoisSimule, prochaineDeclarationTrimestrielle)) {
            calculerPrimeActivite(simulationAides, aidesPourCeMois, dateDebutSimulation, numeroMoisSimule, demandeurEmploi);
        } else {
            reporterPrimeActivite(simulationAides, aidesPourCeMois, numeroMoisSimule, demandeurEmploi, prochaineDeclarationTrimestrielle);
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

    private void calculerPrimeActivite(SimulationAides simulationAides, Map<String, Aide> aidesPourCeMois, LocalDate dateDebutSimulation, int numeroMoisSimule, DemandeurEmploi demandeurEmploi) {
        OpenFiscaRetourSimulation openFiscaRetourSimulation = openFiscaClient.calculerPrimeActivite(simulationAides, demandeurEmploi, dateDebutSimulation, numeroMoisSimule);
        if (openFiscaRetourSimulation.getMontantPrimeActivite() > 0) {
            Aide primeActivite = creerAidePrimeActivite(openFiscaRetourSimulation.getMontantPrimeActivite(), false);
            aidesPourCeMois.put(Aides.PRIME_ACTIVITE.getCode(), primeActivite);
        }
    }

    private boolean isPrimeActiviteACalculer(int numeroMoisSimule, int prochaineDeclarationTrimestrielle) {
        return numeroMoisSimule == 1 || ((prochaineDeclarationTrimestrielle == numeroMoisSimule) || (prochaineDeclarationTrimestrielle == numeroMoisSimule - 3)
                || (prochaineDeclarationTrimestrielle == numeroMoisSimule - 6));

    }

    private boolean isPrimeActiviteAVerser(int numeroMoisSimule, int prochaineDeclarationTrimestrielle) {
        return numeroMoisSimule == 2 || ((prochaineDeclarationTrimestrielle == numeroMoisSimule - 1) || (prochaineDeclarationTrimestrielle == numeroMoisSimule - 4));
    }

    private void reporterPrimeActivite(SimulationAides simulationAides, Map<String, Aide> aidesPourCeMois, int numeroMoisSimule, DemandeurEmploi demandeurEmploi,
            int prochaineDeclarationTrimestrielle) {
        Optional<Aide> primeActiviteMoisPrecedent = primeActiviteUtile.getPrimeActiviteMoisPrecedent(simulationAides, numeroMoisSimule);
        if (primeActiviteMoisPrecedent.isPresent()) {
            aidesPourCeMois.put(Aides.PRIME_ACTIVITE.getCode(), primeActiviteMoisPrecedent.get());
        }
    }
}
