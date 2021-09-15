package fr.poleemploi.estime.logique.simulateur.aides.caf.utile;

import java.util.Map;

import org.springframework.stereotype.Component;

import fr.poleemploi.estime.commun.enumerations.Aides;
import fr.poleemploi.estime.commun.enumerations.MessagesInformatifs;
import fr.poleemploi.estime.commun.enumerations.Organismes;
import fr.poleemploi.estime.services.ressources.Aide;
import fr.poleemploi.estime.services.ressources.DemandeurEmploi;

@Component
public class AidesFamilialesUtile {

    public void simulerAllocationsFamiliales(Map<String, Aide> aidesPourCeMois, int numeroMoisSimule, DemandeurEmploi demandeurEmploi) {
        float montantAllocationsFamiliales = demandeurEmploi.getRessourcesFinancieres().getAidesCAF().getAidesFamiliales().getAllocationsFamiliales();
        ajouterAllocationsFamiliales(aidesPourCeMois, montantAllocationsFamiliales);
    }

    public void simulerComplementFamilial(Map<String, Aide> aidesPourCeMois, int numeroMoisSimule, DemandeurEmploi demandeurEmploi) {
        float montantAllocationsFamiliales = demandeurEmploi.getRessourcesFinancieres().getAidesCAF().getAidesFamiliales().getComplementFamilial();
        ajouterComplementFamilial(aidesPourCeMois, montantAllocationsFamiliales);
    }

    public void simulerAllocationSoutienFamilial(Map<String, Aide> aidesPourCeMois, int numeroMoisSimule, DemandeurEmploi demandeurEmploi) {
        float montantAllocationsFamiliales = demandeurEmploi.getRessourcesFinancieres().getAidesCAF().getAidesFamiliales().getAllocationSoutienFamilial();
        ajouterAllocationSoutienFamilial(aidesPourCeMois, montantAllocationsFamiliales);
    }

    private void ajouterAllocationsFamiliales(Map<String, Aide> aidesPourCeMois, float montant) {
        Aide allocationsFamiliales = new Aide();
        allocationsFamiliales.setCode(Aides.ALLOCATIONS_FAMILIALES.getCode());
        allocationsFamiliales.setMessageAlerte(MessagesInformatifs.CHANGEMENT_MONTANT_PRESTATIONS_FAMILIALES.getMessage());
        allocationsFamiliales.setMontant(montant);
        allocationsFamiliales.setNom(Aides.ALLOCATIONS_FAMILIALES.getNom());
        allocationsFamiliales.setOrganisme(Organismes.CAF.getNom());
        allocationsFamiliales.setReportee(true);
        aidesPourCeMois.put(Aides.ALLOCATIONS_FAMILIALES.getCode(), allocationsFamiliales);
    }

    private void ajouterComplementFamilial(Map<String, Aide> aidesPourCeMois, float montant) {
        Aide complementFamilial = new Aide();
        complementFamilial.setCode(Aides.COMPLEMENT_FAMILIAL.getCode());
        complementFamilial.setMessageAlerte(MessagesInformatifs.CHANGEMENT_MONTANT_PRESTATIONS_FAMILIALES.getMessage());
        complementFamilial.setMontant(montant);
        complementFamilial.setNom(Aides.COMPLEMENT_FAMILIAL.getNom());
        complementFamilial.setOrganisme(Organismes.CAF.getNom());
        complementFamilial.setReportee(true);
        aidesPourCeMois.put(Aides.COMPLEMENT_FAMILIAL.getCode(), complementFamilial);
    }

    private void ajouterAllocationSoutienFamilial(Map<String, Aide> aidesPourCeMois, float montant) {
        Aide allocationSoutienFamilial = new Aide();
        allocationSoutienFamilial.setCode(Aides.ALLOCATION_SOUTIEN_FAMILIAL.getCode());
        allocationSoutienFamilial.setMessageAlerte(MessagesInformatifs.CHANGEMENT_MONTANT_PRESTATIONS_FAMILIALES.getMessage());
        allocationSoutienFamilial.setMontant(montant);
        allocationSoutienFamilial.setNom(Aides.ALLOCATION_SOUTIEN_FAMILIAL.getNom());
        allocationSoutienFamilial.setOrganisme(Organismes.CAF.getNom());
        allocationSoutienFamilial.setReportee(true);
        aidesPourCeMois.put(Aides.ALLOCATION_SOUTIEN_FAMILIAL.getCode(), allocationSoutienFamilial);
    }
}
