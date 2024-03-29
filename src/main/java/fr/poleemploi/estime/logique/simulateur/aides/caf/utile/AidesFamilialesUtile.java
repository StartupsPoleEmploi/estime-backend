package fr.poleemploi.estime.logique.simulateur.aides.caf.utile;

import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fr.poleemploi.estime.commun.enumerations.AideEnum;
import fr.poleemploi.estime.commun.enumerations.MessageInformatifEnum;
import fr.poleemploi.estime.commun.enumerations.OrganismeEnum;
import fr.poleemploi.estime.commun.utile.demandeuremploi.RessourcesFinancieresAvantSimulationUtile;
import fr.poleemploi.estime.commun.utile.demandeuremploi.SituationFamilialeUtile;
import fr.poleemploi.estime.logique.simulateur.aides.utile.AideUtile;
import fr.poleemploi.estime.services.ressources.Aide;
import fr.poleemploi.estime.services.ressources.DemandeurEmploi;

@Component
public class AidesFamilialesUtile {

    @Autowired
    private RessourcesFinancieresAvantSimulationUtile ressourcesFinancieresUtile;

    @Autowired
    private SituationFamilialeUtile situationFamilialeUtile;

    @Autowired
    private AideUtile aideUtile;

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

    private void simulerAllocationSoutienFamilial(Map<String, Aide> aidesPourCeMois, DemandeurEmploi demandeurEmploi) {
	float montantAllocationSoutienFamilial = demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesCAF().getAidesFamiliales().getAllocationSoutienFamilial();
	aidesPourCeMois.put(AideEnum.ALLOCATION_SOUTIEN_FAMILIAL.getCode(), creerAideAllocationSoutientFamilial(montantAllocationSoutienFamilial));
    }

    private void simulerAllocationsFamiliales(Map<String, Aide> aidesPourCeMois, DemandeurEmploi demandeurEmploi) {
	float montantAllocationsFamiliales = demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesCAF().getAidesFamiliales().getAllocationsFamiliales();
	aidesPourCeMois.put(AideEnum.ALLOCATIONS_FAMILIALES.getCode(), creerAideAllocationsFamiliales(montantAllocationsFamiliales));

    }

    private void simulerComplementFamilial(Map<String, Aide> aidesPourCeMois, DemandeurEmploi demandeurEmploi) {
	float montantComplementFamilial = demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesCAF().getAidesFamiliales().getComplementFamilial();
	aidesPourCeMois.put(AideEnum.COMPLEMENT_FAMILIAL.getCode(), creerAideComplementFamilial(montantComplementFamilial));

    }

    private void simulerPrestationAccueilJeuneEnfant(Map<String, Aide> aidesPourCeMois, DemandeurEmploi demandeurEmploi) {
	float montantPrestationAccueilJeuneEnfant = demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesCAF().getAidesFamiliales().getPrestationAccueilJeuneEnfant();
	aidesPourCeMois.put(AideEnum.PRESTATION_ACCUEIL_JEUNE_ENFANT.getCode(), creerAidePrestationAccueilJeuneEnfant(montantPrestationAccueilJeuneEnfant));

    }

    private void simulerPensionsAlimentaires(Map<String, Aide> aidesPourCeMois, DemandeurEmploi demandeurEmploi) {
	float montantPensionsAlimentaires = demandeurEmploi.getRessourcesFinancieresAvantSimulation().getAidesCAF().getAidesFamiliales().getPensionsAlimentairesFoyer();
	aidesPourCeMois.put(AideEnum.PENSIONS_ALIMENTAIRES.getCode(), creerAidePensionsAlimentaires(montantPensionsAlimentaires));

    }

    private Aide creerAideAllocationSoutientFamilial(float montant) {
	ArrayList<String> messagesAlerte = new ArrayList<>();
	messagesAlerte.add(MessageInformatifEnum.CHANGEMENT_MONTANT_PRESTATIONS_FAMILIALES.getMessage());
	return aideUtile.creerAide(AideEnum.ALLOCATION_SOUTIEN_FAMILIAL, Optional.of(OrganismeEnum.CAF), Optional.of(messagesAlerte), true, montant);
    }

    private Aide creerAideAllocationsFamiliales(float montant) {
	ArrayList<String> messagesAlerte = new ArrayList<>();
	messagesAlerte.add(MessageInformatifEnum.CHANGEMENT_MONTANT_PRESTATIONS_FAMILIALES.getMessage());
	return aideUtile.creerAide(AideEnum.ALLOCATIONS_FAMILIALES, Optional.of(OrganismeEnum.CAF), Optional.of(messagesAlerte), true, montant);
    }

    private Aide creerAideComplementFamilial(float montant) {
	ArrayList<String> messagesAlerte = new ArrayList<>();
	messagesAlerte.add(MessageInformatifEnum.CHANGEMENT_MONTANT_PRESTATIONS_FAMILIALES.getMessage());
	return aideUtile.creerAide(AideEnum.COMPLEMENT_FAMILIAL, Optional.of(OrganismeEnum.CAF), Optional.of(messagesAlerte), true, montant);
    }

    private Aide creerAidePrestationAccueilJeuneEnfant(float montant) {
	ArrayList<String> messagesAlerte = new ArrayList<>();
	messagesAlerte.add(MessageInformatifEnum.CHANGEMENT_MONTANT_PRESTATIONS_FAMILIALES.getMessage());
	return aideUtile.creerAide(AideEnum.PRESTATION_ACCUEIL_JEUNE_ENFANT, Optional.of(OrganismeEnum.CAF), Optional.of(messagesAlerte), true, montant);
    }

    private Aide creerAidePensionsAlimentaires(float montant) {
	ArrayList<String> messagesAlerte = new ArrayList<>();
	messagesAlerte.add(MessageInformatifEnum.CHANGEMENT_MONTANT_PRESTATIONS_FAMILIALES.getMessage());
	return aideUtile.creerAide(AideEnum.PENSIONS_ALIMENTAIRES, Optional.of(OrganismeEnum.CAF), Optional.of(messagesAlerte), true, montant);
    }

    public boolean isEligibleAidesFamiliales(DemandeurEmploi demandeurEmploi, int numeroMoisSimule) {
	return isEligibleAllocationsFamiliales(demandeurEmploi) || isEligibleAllocationSoutienFamilial(demandeurEmploi) || isEligibleComplementFamilial(demandeurEmploi)
		|| isEligiblePrestationAccueilJeuneEnfant(demandeurEmploi, numeroMoisSimule) || isEligiblePensionsAlimentaires(demandeurEmploi);
    }

    boolean isEligibleAllocationsFamiliales(DemandeurEmploi demandeurEmploi) {
	return ressourcesFinancieresUtile.hasAllocationsFamiliales(demandeurEmploi);
    }

    boolean isEligibleAllocationSoutienFamilial(DemandeurEmploi demandeurEmploi) {
	return ressourcesFinancieresUtile.hasAllocationSoutienFamilial(demandeurEmploi);
    }

    boolean isEligibleComplementFamilial(DemandeurEmploi demandeurEmploi) {
	return ressourcesFinancieresUtile.hasComplementFamilial(demandeurEmploi);
    }

    boolean isEligiblePrestationAccueilJeuneEnfant(DemandeurEmploi demandeurEmploi, int numeroMoisSimule) {
	return ressourcesFinancieresUtile.hasPrestationAccueilJeuneEnfant(demandeurEmploi)
		&& situationFamilialeUtile.hasEnfantMoinsDe3AnsPourMoisSimule(demandeurEmploi, numeroMoisSimule);
    }

    public boolean isEligiblePensionsAlimentaires(DemandeurEmploi demandeurEmploi) {
	return ressourcesFinancieresUtile.hasPensionsAlimentaires(demandeurEmploi);
    }
}
