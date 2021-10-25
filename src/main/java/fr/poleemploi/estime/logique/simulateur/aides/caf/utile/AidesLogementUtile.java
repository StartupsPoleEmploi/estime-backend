package fr.poleemploi.estime.logique.simulateur.aides.caf.utile;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fr.poleemploi.estime.commun.enumerations.Aides;
import fr.poleemploi.estime.commun.enumerations.MessagesInformatifs;
import fr.poleemploi.estime.commun.enumerations.Organismes;
import fr.poleemploi.estime.commun.utile.demandeuremploi.RessourcesFinancieresUtile;
import fr.poleemploi.estime.services.ressources.Aide;
import fr.poleemploi.estime.services.ressources.DemandeurEmploi;

@Component
public class AidesLogementUtile {

    @Autowired
    private RessourcesFinancieresUtile ressourcesFinancieresUtile;

    public void simulerAidesLogement(Map<String, Aide> aidesPourCeMois, DemandeurEmploi demandeurEmploi, int numeroMoisSimule) {
        if (isEligibleAPL(demandeurEmploi))
            simulerAidePersonnaliseeLogement(aidesPourCeMois, demandeurEmploi, numeroMoisSimule);
        if (isEligibleALS(demandeurEmploi))
            simulerAllocationLogementSociale(aidesPourCeMois, demandeurEmploi, numeroMoisSimule);
        if (isEligibleALF(demandeurEmploi))
            simulerAllocationLogementFamiliale(aidesPourCeMois, demandeurEmploi, numeroMoisSimule);
    }

    public void simulerAidePersonnaliseeLogement(Map<String, Aide> aidesPourCeMois, DemandeurEmploi demandeurEmploi, int numeroMoisSimule) {
        float montantAidePersonnaliseeLogement = 0;
        if (isMoisAReporter(demandeurEmploi, numeroMoisSimule)) montantAidePersonnaliseeLogement = ressourcesFinancieresUtile.getAllocationsLogementSur1Mois(demandeurEmploi.getRessourcesFinancieres().getAidesCAF().getAidesLogement().getAidePersonnaliseeLogement());
        ajouterAidePersonnaliseeLogement(aidesPourCeMois, montantAidePersonnaliseeLogement);
    }
    
    public void simulerAllocationLogementFamiliale(Map<String, Aide> aidesPourCeMois, DemandeurEmploi demandeurEmploi, int numeroMoisSimule) {
        float montantAllocationLogementFamiliale = 0;
        if (isMoisAReporter(demandeurEmploi, numeroMoisSimule)) montantAllocationLogementFamiliale = ressourcesFinancieresUtile.getAllocationsLogementSur1Mois(demandeurEmploi.getRessourcesFinancieres().getAidesCAF().getAidesLogement().getAllocationLogementFamiliale());
        ajouterAllocationLogementFamiliale(aidesPourCeMois, montantAllocationLogementFamiliale);
    }

    public void simulerAllocationLogementSociale(Map<String, Aide> aidesPourCeMois, DemandeurEmploi demandeurEmploi, int numeroMoisSimule) {
        float montantAllocationLogementSociale = 0;
        if (isMoisAReporter(demandeurEmploi, numeroMoisSimule)) montantAllocationLogementSociale = ressourcesFinancieresUtile.getAllocationsLogementSur1Mois(demandeurEmploi.getRessourcesFinancieres().getAidesCAF().getAidesLogement().getAllocationLogementSociale());
        ajouterAllocationLogementSociale(aidesPourCeMois, montantAllocationLogementSociale);
    }
    
    public boolean isMoisAReporter(DemandeurEmploi demandeurEmploi, int numeroMoisSimule) {
        boolean isMoisAReporter = false;
        if(demandeurEmploi.getRessourcesFinancieres() != null
            && demandeurEmploi.getRessourcesFinancieres().getAidesCAF() != null 
            && demandeurEmploi.getRessourcesFinancieres().getAidesCAF().getProchaineDeclarationTrimestrielle() != null) {
            int prochaineDeclarationTrimestrielle = demandeurEmploi.getRessourcesFinancieres().getAidesCAF().getProchaineDeclarationTrimestrielle();
            isMoisAReporter = prochaineDeclarationTrimestrielle != 0 && prochaineDeclarationTrimestrielle != 3 && numeroMoisSimule == 1;
        } else {
            isMoisAReporter = numeroMoisSimule == 1;
        }
        return isMoisAReporter;        
    }

    private void ajouterAidePersonnaliseeLogement(Map<String, Aide> aidesPourCeMois, float montant) {
        Aide aidePersonnaliseeLogement = new Aide();
        aidePersonnaliseeLogement.setCode(Aides.AIDE_PERSONNALISEE_LOGEMENT.getCode());
        aidePersonnaliseeLogement.setMessageAlerte(MessagesInformatifs.CHANGEMENT_MONTANT_PRESTATIONS_FAMILIALES.getMessage());
        aidePersonnaliseeLogement.setMontant(montant);
        aidePersonnaliseeLogement.setNom(Aides.AIDE_PERSONNALISEE_LOGEMENT.getNom());
        aidePersonnaliseeLogement.setOrganisme(Organismes.CAF.getNom());
        aidePersonnaliseeLogement.setReportee(true);
        aidesPourCeMois.put(Aides.AIDE_PERSONNALISEE_LOGEMENT.getCode(), aidePersonnaliseeLogement);
    }

    private void ajouterAllocationLogementFamiliale(Map<String, Aide> aidesPourCeMois, float montant) {
        Aide allocationLogementFamiliale = new Aide();
        allocationLogementFamiliale.setCode(Aides.ALLOCATION_LOGEMENT_FAMILIALE.getCode());
        allocationLogementFamiliale.setMessageAlerte(MessagesInformatifs.CHANGEMENT_MONTANT_PRESTATIONS_FAMILIALES.getMessage());
        allocationLogementFamiliale.setMontant(montant);
        allocationLogementFamiliale.setNom(Aides.ALLOCATION_LOGEMENT_FAMILIALE.getNom());
        allocationLogementFamiliale.setOrganisme(Organismes.CAF.getNom());
        allocationLogementFamiliale.setReportee(true);
        aidesPourCeMois.put(Aides.ALLOCATION_LOGEMENT_FAMILIALE.getCode(), allocationLogementFamiliale);
    }

    private void ajouterAllocationLogementSociale(Map<String, Aide> aidesPourCeMois, float montant) {
        Aide allocationLogementSociale = new Aide();
        allocationLogementSociale.setCode(Aides.ALLOCATION_LOGEMENT_SOCIALE.getCode());
        allocationLogementSociale.setMessageAlerte(MessagesInformatifs.CHANGEMENT_MONTANT_PRESTATIONS_FAMILIALES.getMessage());
        allocationLogementSociale.setMontant(montant);
        allocationLogementSociale.setNom(Aides.ALLOCATION_LOGEMENT_SOCIALE.getNom());
        allocationLogementSociale.setOrganisme(Organismes.CAF.getNom());
        allocationLogementSociale.setReportee(true);
        aidesPourCeMois.put(Aides.ALLOCATION_LOGEMENT_SOCIALE.getCode(), allocationLogementSociale);
    }
    
    public boolean isEligibleAPL(DemandeurEmploi demandeurEmploi) {
        return ressourcesFinancieresUtile.hasAidePersonnaliseeLogement(demandeurEmploi);
    }

    public boolean isEligibleALF(DemandeurEmploi demandeurEmploi) {
        return ressourcesFinancieresUtile.hasAllocationLogementFamiliale(demandeurEmploi);
    }

    public boolean isEligibleALS(DemandeurEmploi demandeurEmploi) {
        return ressourcesFinancieresUtile.hasAllocationLogementSociale(demandeurEmploi);
    }
}
