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
import fr.poleemploi.estime.commun.utile.demandeuremploi.RessourcesFinancieresUtile;
import fr.poleemploi.estime.logique.simulateur.aides.utile.AideUtile;
import fr.poleemploi.estime.services.ressources.Aide;
import fr.poleemploi.estime.services.ressources.DemandeurEmploi;
import fr.poleemploi.estime.services.ressources.SimulationAides;

@Component
public class AidesLogementUtile {

    @Autowired
    private OpenFiscaClient openFiscaClient;

    @Autowired
    private RessourcesFinancieresUtile ressourcesFinancieresUtile;

    @Autowired
    private AideUtile aideUtile;

    public void simulerAidesLogement(SimulationAides simulationAides, Map<String, Aide> aidesPourCeMois, LocalDate dateDebutSimulation, int numeroMoisSimule, DemandeurEmploi demandeurEmploi) {
        int prochaineDeclarationTrimestrielle = demandeurEmploi.getRessourcesFinancieres().getAidesCAF().getProchaineDeclarationTrimestrielle();
        if (isAideLogementACalculer(numeroMoisSimule, prochaineDeclarationTrimestrielle)) {
            reporterAideLogement(simulationAides, aidesPourCeMois, numeroMoisSimule, demandeurEmploi, prochaineDeclarationTrimestrielle);
        } else if (isAideLogementAVerser(numeroMoisSimule, prochaineDeclarationTrimestrielle)) {
            calculerAideLogement(simulationAides, aidesPourCeMois, dateDebutSimulation, numeroMoisSimule - 1, demandeurEmploi);
        } else {
            reporterAideLogement(simulationAides, aidesPourCeMois, numeroMoisSimule, demandeurEmploi, prochaineDeclarationTrimestrielle);
        }
    }

    /**
     * Fonction permettant de déterminer si le montant des aides au logement doit être calculé ce mois-ci
     * 
     * @param numeroMoisSimule
     * @param prochaineDeclarationTrimestrielle
     * @return
     */
    private boolean isAideLogementACalculer(int numeroMoisSimule, int prochaineDeclarationTrimestrielle) {
        return (numeroMoisSimule == 1 || (prochaineDeclarationTrimestrielle == numeroMoisSimule) || (prochaineDeclarationTrimestrielle == numeroMoisSimule - 3)
                || (prochaineDeclarationTrimestrielle == numeroMoisSimule - 6));
    }

    /**
     * Fonction permettant de déterminer si on a calculé le montant des aides au logement le mois précédent et s'il doit être versé ce mois-ci
     * 
     * @param numeroMoisSimule
     * @param prochaineDeclarationTrimestrielle
     * @return
     */
    private boolean isAideLogementAVerser(int numeroMoisSimule, int prochaineDeclarationTrimestrielle) {
        return (numeroMoisSimule == 2 
                || ((prochaineDeclarationTrimestrielle == 0) && (numeroMoisSimule == 1 || numeroMoisSimule == 4))
                || ((prochaineDeclarationTrimestrielle == 1) && (numeroMoisSimule == 2 || numeroMoisSimule == 5))
                || ((prochaineDeclarationTrimestrielle == 2) && (numeroMoisSimule == 3 || numeroMoisSimule == 6)) 
                || ((prochaineDeclarationTrimestrielle == 3) && (numeroMoisSimule == 4)));
    }

    private void reporterAideLogement(SimulationAides simulationAides, Map<String, Aide> aidesPourCeMois, int numeroMoisSimule, DemandeurEmploi demandeurEmploi,
            int prochaineDeclarationTrimestrielle) {
        Optional<Aide> aideLogementMoisPrecedent = getAideLogementSimuleeMoisPrecedent(simulationAides, demandeurEmploi, numeroMoisSimule);
        if (aideLogementMoisPrecedent.isPresent()) {
            aidesPourCeMois.put(aideLogementMoisPrecedent.get().getCode(), aideLogementMoisPrecedent.get());
        } else if (isEligiblePourReportAideLogementDeclare(prochaineDeclarationTrimestrielle, numeroMoisSimule)) {
            Aide aideLogement = getAideLogementDeclare(demandeurEmploi);
            aidesPourCeMois.put(aideLogement.getCode(), aideLogement);
        }
    }

    private void calculerAideLogement(SimulationAides simulationAides, Map<String, Aide> aidesPourCeMois, LocalDate dateDebutSimulation, int numeroMoisSimule, DemandeurEmploi demandeurEmploi) {
        OpenFiscaRetourSimulation openFiscaRetourSimulation = openFiscaClient.calculerAideLogement(simulationAides, demandeurEmploi, dateDebutSimulation, numeroMoisSimule);

        if (openFiscaRetourSimulation.getMontantAideLogement() > 0) {
            Aide aideLogement = creerAideLogement(openFiscaRetourSimulation.getMontantAideLogement(), openFiscaRetourSimulation.getTypeAideLogement(), false);
            aidesPourCeMois.put(aideLogement.getCode(), aideLogement);
        }
    }

    private Aide getAideLogementDeclare(DemandeurEmploi demandeurEmploi) {
        float montantDeclare = ressourcesFinancieresUtile.getMontantAideLogementDeclare(demandeurEmploi);
        String typeAideLogement = ressourcesFinancieresUtile.getTypeAideLogementDeclare(demandeurEmploi);
        return creerAideLogement(montantDeclare, typeAideLogement, true);
    }

    private Optional<Aide> getAideLogementSimuleeMoisPrecedent(SimulationAides simulationAides, DemandeurEmploi demandeurEmploi, int numeroMoisSimule) {
        int moisNMoins1 = numeroMoisSimule - 1;
        Optional<Aide> aidePourCeMois = aideUtile.getAidePourCeMoisSimule(simulationAides, Aides.AIDE_PERSONNALISEE_LOGEMENT.getCode(), moisNMoins1);
        if (aidePourCeMois.isEmpty())
            aidePourCeMois = aideUtile.getAidePourCeMoisSimule(simulationAides, Aides.ALLOCATION_LOGEMENT_FAMILIALE.getCode(), moisNMoins1);
        if (aidePourCeMois.isEmpty())
            aidePourCeMois = aideUtile.getAidePourCeMoisSimule(simulationAides, Aides.ALLOCATION_LOGEMENT_SOCIALE.getCode(), moisNMoins1);
        return aidePourCeMois;
    }

    private boolean isEligiblePourReportAideLogementDeclare(int prochaineDeclarationTrimestrielle, int numeroMoisSimule) {
        return numeroMoisSimule == 1 || numeroMoisSimule <= prochaineDeclarationTrimestrielle;
    }

    private Aide creerAideLogement(float montantAideLogement, String typeAide, boolean isAideReportee) {
        Aide aideLogement = new Aide();
        if (typeAide.equals(Aides.AIDE_PERSONNALISEE_LOGEMENT.getCode())) {
            aideLogement = ajouterAidePersonnaliseeLogement(montantAideLogement, isAideReportee);
        }
        if (typeAide.equals(Aides.ALLOCATION_LOGEMENT_FAMILIALE.getCode())) {
            aideLogement = ajouterAllocationLogementFamiliale(montantAideLogement, isAideReportee);
        }
        if (typeAide.equals(Aides.ALLOCATION_LOGEMENT_SOCIALE.getCode())) {
            aideLogement = ajouterAllocationLogementSociale(montantAideLogement, isAideReportee);
        }
        return aideLogement;
    }

    private Aide ajouterAidePersonnaliseeLogement(float montant, boolean isReportee) {
        Aide aidePersonnaliseeLogement = new Aide();
        aidePersonnaliseeLogement.setCode(Aides.AIDE_PERSONNALISEE_LOGEMENT.getCode());
        aidePersonnaliseeLogement.setMessageAlerte(MessagesInformatifs.CHANGEMENT_MONTANT_PRESTATIONS_FAMILIALES.getMessage());
        aidePersonnaliseeLogement.setMontant(montant);
        aidePersonnaliseeLogement.setNom(Aides.AIDE_PERSONNALISEE_LOGEMENT.getNom());
        aidePersonnaliseeLogement.setOrganisme(Organismes.CAF.getNom());
        aidePersonnaliseeLogement.setReportee(isReportee);
        return aidePersonnaliseeLogement;
    }

    private Aide ajouterAllocationLogementFamiliale(float montant, boolean isReportee) {
        Aide allocationLogementFamiliale = new Aide();
        allocationLogementFamiliale.setCode(Aides.ALLOCATION_LOGEMENT_FAMILIALE.getCode());
        allocationLogementFamiliale.setMessageAlerte(MessagesInformatifs.CHANGEMENT_MONTANT_PRESTATIONS_FAMILIALES.getMessage());
        allocationLogementFamiliale.setMontant(montant);
        allocationLogementFamiliale.setNom(Aides.ALLOCATION_LOGEMENT_FAMILIALE.getNom());
        allocationLogementFamiliale.setOrganisme(Organismes.CAF.getNom());
        allocationLogementFamiliale.setReportee(isReportee);
        return allocationLogementFamiliale;
    }

    private Aide ajouterAllocationLogementSociale(float montant, boolean isReportee) {
        Aide allocationLogementSociale = new Aide();
        allocationLogementSociale.setCode(Aides.ALLOCATION_LOGEMENT_SOCIALE.getCode());
        allocationLogementSociale.setMessageAlerte(MessagesInformatifs.CHANGEMENT_MONTANT_PRESTATIONS_FAMILIALES.getMessage());
        allocationLogementSociale.setMontant(montant);
        allocationLogementSociale.setNom(Aides.ALLOCATION_LOGEMENT_SOCIALE.getNom());
        allocationLogementSociale.setOrganisme(Organismes.CAF.getNom());
        allocationLogementSociale.setReportee(isReportee);
        return allocationLogementSociale;
    }

    public boolean isEligibleAidesLogement(DemandeurEmploi demandeurEmploi) {
        boolean isEligible = false;
        if (demandeurEmploi.getInformationsPersonnelles() != null && demandeurEmploi.getInformationsPersonnelles().getLogement() != null
                && demandeurEmploi.getInformationsPersonnelles().getLogement().getStatutOccupationLogement() != null
                && (demandeurEmploi.getInformationsPersonnelles().getLogement().getStatutOccupationLogement().isLocataireHLM()
                        || demandeurEmploi.getInformationsPersonnelles().getLogement().getStatutOccupationLogement().isLocataireMeuble()
                        || demandeurEmploi.getInformationsPersonnelles().getLogement().getStatutOccupationLogement().isLocataireNonMeuble()
                        || demandeurEmploi.getInformationsPersonnelles().getLogement().getStatutOccupationLogement().isProprietaireAvecEmprunt())) {
            isEligible = true;
        }
        return isEligible;
    }

}
