package fr.poleemploi.estime.logique.simulateur.aides.caf.utile;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fr.poleemploi.estime.commun.enumerations.Aides;
import fr.poleemploi.estime.commun.enumerations.MessagesInformatifs;
import fr.poleemploi.estime.commun.enumerations.Organismes;
import fr.poleemploi.estime.commun.utile.demandeuremploi.RessourcesFinancieresUtile;
import fr.poleemploi.estime.commun.utile.demandeuremploi.SituationFamilialeUtile;
import fr.poleemploi.estime.services.ressources.Aide;
import fr.poleemploi.estime.services.ressources.DemandeurEmploi;

@Component
public class AidesFamilialesUtile {

    @Autowired
    private RessourcesFinancieresUtile ressourcesFinancieresUtile;

    @Autowired
    private SituationFamilialeUtile situationFamilialeUtile;

    public void simulerAidesFamiliales(Map<String, Aide> aidesPourCeMois, DemandeurEmploi demandeurEmploi, int numeroMoisSimule) {
        if (isEligibleAllocationsFamiliales(demandeurEmploi))
            simulerAllocationsFamiliales(aidesPourCeMois, demandeurEmploi);
        if (isEligibleAllocationSoutienFamilial(demandeurEmploi))
            simulerAllocationSoutienFamilial(aidesPourCeMois, demandeurEmploi);
        if (isEligibleComplementFamilial(demandeurEmploi))
            simulerComplementFamilial(aidesPourCeMois, demandeurEmploi);
        if (isEligiblePrestationAccueilJeuneEnfant(demandeurEmploi, numeroMoisSimule))
            simulerPrestationAccueilJeuneEnfant(aidesPourCeMois, demandeurEmploi);
        if (isEligiblePensionsAlimentaires(demandeurEmploi))
            simulerPensionsAlimentaires(aidesPourCeMois, demandeurEmploi);
    }

    public void simulerAllocationSoutienFamilial(Map<String, Aide> aidesPourCeMois, DemandeurEmploi demandeurEmploi) {
        float montantAllocationSoutienFamilial = demandeurEmploi.getRessourcesFinancieres().getAidesCAF().getAidesFamiliales().getAllocationSoutienFamilial();
        ajouterAllocationSoutienFamilial(aidesPourCeMois, montantAllocationSoutienFamilial);
    }

    public void simulerAllocationsFamiliales(Map<String, Aide> aidesPourCeMois, DemandeurEmploi demandeurEmploi) {
        float montantAllocationsFamiliales = demandeurEmploi.getRessourcesFinancieres().getAidesCAF().getAidesFamiliales().getAllocationsFamiliales();
        ajouterAllocationsFamiliales(aidesPourCeMois, montantAllocationsFamiliales);
    }

    public void simulerComplementFamilial(Map<String, Aide> aidesPourCeMois, DemandeurEmploi demandeurEmploi) {
        float montantComplementFamilial = demandeurEmploi.getRessourcesFinancieres().getAidesCAF().getAidesFamiliales().getComplementFamilial();
        ajouterComplementFamilial(aidesPourCeMois, montantComplementFamilial);
    }

    public void simulerPrestationAccueilJeuneEnfant(Map<String, Aide> aidesPourCeMois, DemandeurEmploi demandeurEmploi) {
        float montantPrestationAccueilJeuneEnfant = demandeurEmploi.getRessourcesFinancieres().getAidesCAF().getAidesFamiliales().getPrestationAccueilJeuneEnfant();
        ajouterPrestationAccueilJeuneEnfant(aidesPourCeMois, montantPrestationAccueilJeuneEnfant);
    }

    public void simulerPensionsAlimentaires(Map<String, Aide> aidesPourCeMois, DemandeurEmploi demandeurEmploi) {
        float montantPensionsAlimentaires = demandeurEmploi.getRessourcesFinancieres().getAidesCAF().getAidesFamiliales().getPensionsAlimentairesFoyer();
        ajouterPensionsAlimentaires(aidesPourCeMois, montantPensionsAlimentaires);
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

    private void ajouterPrestationAccueilJeuneEnfant(Map<String, Aide> aidesPourCeMois, float montant) {
        Aide prestationAccueilJeuneEnfant = new Aide();
        prestationAccueilJeuneEnfant.setCode(Aides.PRESTATION_ACCUEIL_JEUNE_ENFANT.getCode());
        prestationAccueilJeuneEnfant.setMessageAlerte(MessagesInformatifs.CHANGEMENT_MONTANT_PRESTATIONS_FAMILIALES.getMessage());
        prestationAccueilJeuneEnfant.setMontant(montant);
        prestationAccueilJeuneEnfant.setNom(Aides.PRESTATION_ACCUEIL_JEUNE_ENFANT.getNom());
        prestationAccueilJeuneEnfant.setOrganisme(Organismes.CAF.getNom());
        prestationAccueilJeuneEnfant.setReportee(true);
        aidesPourCeMois.put(Aides.PRESTATION_ACCUEIL_JEUNE_ENFANT.getCode(), prestationAccueilJeuneEnfant);
    }

    private void ajouterPensionsAlimentaires(Map<String, Aide> aidesPourCeMois, float montant) {
        Aide pensionsAlimentaires = new Aide();
        pensionsAlimentaires.setCode(Aides.PENSIONS_ALIMENTAIRES.getCode());
        pensionsAlimentaires.setMessageAlerte(MessagesInformatifs.CHANGEMENT_MONTANT_PRESTATIONS_FAMILIALES.getMessage());
        pensionsAlimentaires.setMontant(montant);
        pensionsAlimentaires.setNom(Aides.PENSIONS_ALIMENTAIRES.getNom());
        pensionsAlimentaires.setOrganisme(Organismes.CAF.getNom());
        pensionsAlimentaires.setReportee(true);
        aidesPourCeMois.put(Aides.PENSIONS_ALIMENTAIRES.getCode(), pensionsAlimentaires);
    }

    public boolean isEligibleAllocationsFamiliales(DemandeurEmploi demandeurEmploi) {
        return ressourcesFinancieresUtile.hasAllocationsFamiliales(demandeurEmploi);
    }

    public boolean isEligibleAllocationSoutienFamilial(DemandeurEmploi demandeurEmploi) {
        return ressourcesFinancieresUtile.hasAllocationSoutienFamilial(demandeurEmploi);
    }

    public boolean isEligibleComplementFamilial(DemandeurEmploi demandeurEmploi) {
        return ressourcesFinancieresUtile.hasComplementFamilial(demandeurEmploi);
    }

    public boolean isEligiblePrestationAccueilJeuneEnfant(DemandeurEmploi demandeurEmploi, int numeroMoisSimule) {
        return ressourcesFinancieresUtile.hasPrestationAccueilJeuneEnfant(demandeurEmploi) && situationFamilialeUtile.hasEnfantMoinsDe3AnsPourMoisSimule(demandeurEmploi, numeroMoisSimule);
    }

    public boolean isEligiblePensionsAlimentaires(DemandeurEmploi demandeurEmploi) {
        return ressourcesFinancieresUtile.hasPensionsAlimentaires(demandeurEmploi);
    }
}