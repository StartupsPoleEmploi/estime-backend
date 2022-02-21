package fr.poleemploi.estime.logique.simulateur.aides.caf.utile;

import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fr.poleemploi.estime.commun.enumerations.AideEnum;
import fr.poleemploi.estime.commun.enumerations.MessageInformatifEnum;
import fr.poleemploi.estime.commun.enumerations.OrganismeEnum;
import fr.poleemploi.estime.commun.utile.demandeuremploi.RessourcesFinancieresUtile;
import fr.poleemploi.estime.logique.simulateur.aides.utile.AideUtile;
import fr.poleemploi.estime.services.ressources.Aide;
import fr.poleemploi.estime.services.ressources.DemandeurEmploi;
import fr.poleemploi.estime.services.ressources.SimulationAides;

@Component
public class AidesLogementUtile {

    @Autowired
    private RessourcesFinancieresUtile ressourcesFinancieresUtile;

    @Autowired
    private AideUtile aideUtile;

    /**
     * Fonction permettant de déterminer si le montant des aides au logement doit être calculé ce mois-ci
     * 
     * @param numeroMoisSimule
     * @param prochaineDeclarationTrimestrielle
     * @return  
     *       _________________________________________________________________________________________
     *      |            |          |          |          |          |          |          |          |
     *      | Mois décla |    M0    |    M1    |    M2    |    M3    |    M4    |    M5    |    M6    |
     *      |            |          |          |          |          |          |          |          |  
     *      |  -  M0     | (C1)/R0  | (C2)/V1  |    V2    | (C3)/R2  |    V3    |    R3    | (C4)/R3  |              
     *      |  -  M1     |    R0    | (C1)/R0  |    V1    |    R1    | (C2)/R1  |    V2    |    R2    |          
     *      |  -  M2     |    R0    | (C1)/R0  | (C2)/V1  |    V2    |    R2    | (C3)/R2  |    V3    |          
     *      |  -  M3     |    R0    | (C1)/R0  |    V1    | (C2)/R1  |    V2    |    R2    | (C3)/R2  |  
     *      |____________|__________|__________|__________|__________|__________|__________|__________|    
     */
    boolean isAideLogementACalculer(int numeroMoisSimule, DemandeurEmploi demandeurEmploi) {
	int prochaineDeclarationTrimestrielle = ressourcesFinancieresUtile.getProchaineDeclarationTrimestrielle(demandeurEmploi);
	return ((numeroMoisSimule == 1) || (prochaineDeclarationTrimestrielle == numeroMoisSimule) || (prochaineDeclarationTrimestrielle == numeroMoisSimule - 3)
		|| (prochaineDeclarationTrimestrielle == numeroMoisSimule - 6));
    }

    /**
     * Fonction permettant de déterminer si on a calculé le montant des aides au logement le mois précédent et s'il doit être versé ce mois-ci
     * 
     * @param numeroMoisSimule
     * @param prochaineDeclarationTrimestrielle
     * @return  
     *       _________________________________________________________________________________________
     *      |            |          |          |          |          |          |          |          |
     *      | Mois décla |    M0    |    M1    |    M2    |    M3    |    M4    |    M5    |    M6    |
     *      |            |          |          |          |          |          |          |          |  
     *      |  -  M0     |  C1/R0   |  C2/(V1) |   (V2)   |  C3/R2   |   (V3)   |    R3    |  C4/R3   |              
     *      |  -  M1     |    R0    |  C1/R0   |   (V1)   |    R1    |  C2/R1   |   (V2)   |    R2    |          
     *      |  -  M2     |    R0    |  C1/R0   |  C2/(V1) |   (V2)   |    R2    |  C3/R2   |   (V3)   |          
     *      |  -  M3     |    R0    |  C1/R0   |   (V1)   |  C2/R1   |   (V2)   |    R2    |  C3/R2   |  
     *      |____________|__________|__________|__________|__________|__________|__________|__________|  
     *      
     */
    boolean isAideLogementAVerser(int numeroMoisSimule, DemandeurEmploi demandeurEmploi) {
	int prochaineDeclarationTrimestrielle = ressourcesFinancieresUtile.getProchaineDeclarationTrimestrielle(demandeurEmploi);
	return (numeroMoisSimule == 2 || ((prochaineDeclarationTrimestrielle == 0) && (numeroMoisSimule == 1 || numeroMoisSimule == 4))
		|| ((prochaineDeclarationTrimestrielle == 1) && (numeroMoisSimule == 5))
		|| ((prochaineDeclarationTrimestrielle == 2) && (numeroMoisSimule == 3 || numeroMoisSimule == 6))
		|| ((prochaineDeclarationTrimestrielle == 3) && (numeroMoisSimule == 4)));
    }

    void reporterAideLogement(SimulationAides simulationAides, Map<String, Aide> aidesPourCeMois, int numeroMoisSimule, DemandeurEmploi demandeurEmploi) {
	Optional<Aide> aideLogementMoisPrecedent = getAideLogementSimuleeMoisPrecedent(simulationAides, numeroMoisSimule);
	if (aideLogementMoisPrecedent.isPresent()) {
	    aidesPourCeMois.put(aideLogementMoisPrecedent.get().getCode(), aideLogementMoisPrecedent.get());
	} else if (isEligiblePourReportAideLogementDeclare(demandeurEmploi, numeroMoisSimule)) {
	    Aide aideLogement = getAideLogementDeclare(demandeurEmploi);
	    aidesPourCeMois.put(aideLogement.getCode(), aideLogement);
	}
    }

    private Aide getAideLogementDeclare(DemandeurEmploi demandeurEmploi) {
	float montantDeclare = ressourcesFinancieresUtile.getMontantAideLogementDeclare(demandeurEmploi);
	String typeAideLogement = ressourcesFinancieresUtile.getTypeAideLogementDeclare(demandeurEmploi);
	return creerAideLogement(montantDeclare, typeAideLogement, true);
    }

    private Optional<Aide> getAideLogementSimuleeMoisPrecedent(SimulationAides simulationAides, int numeroMoisSimule) {
	int moisNMoins1 = numeroMoisSimule - 1;
	Optional<Aide> aidePourCeMois = aideUtile.getAidePourCeMoisSimule(simulationAides, AideEnum.AIDE_PERSONNALISEE_LOGEMENT.getCode(), moisNMoins1);
	if (aidePourCeMois.isEmpty())
	    aidePourCeMois = aideUtile.getAidePourCeMoisSimule(simulationAides, AideEnum.ALLOCATION_LOGEMENT_FAMILIALE.getCode(), moisNMoins1);
	if (aidePourCeMois.isEmpty())
	    aidePourCeMois = aideUtile.getAidePourCeMoisSimule(simulationAides, AideEnum.ALLOCATION_LOGEMENT_SOCIALE.getCode(), moisNMoins1);
	return aidePourCeMois;
    }

    private boolean isEligiblePourReportAideLogementDeclare(DemandeurEmploi demandeurEmploi, int numeroMoisSimule) {
	return ressourcesFinancieresUtile.hasAidesLogement(demandeurEmploi)
		&& demandeurEmploi.getRessourcesFinancieres().getAidesCAF().getProchaineDeclarationTrimestrielle() != null
		&& (numeroMoisSimule == 1 || numeroMoisSimule <= demandeurEmploi.getRessourcesFinancieres().getAidesCAF().getProchaineDeclarationTrimestrielle());
    }

    protected Aide creerAideLogement(float montantAideLogement, String typeAide, boolean isAideReportee) {
	Aide aideLogement = new Aide();
	if (typeAide.equals(AideEnum.AIDE_PERSONNALISEE_LOGEMENT.getCode())) {
	    aideLogement = creerAidePersonnaliseeLogement(montantAideLogement, isAideReportee);
	}
	if (typeAide.equals(AideEnum.ALLOCATION_LOGEMENT_FAMILIALE.getCode())) {
	    aideLogement = creerAllocationLogementFamiliale(montantAideLogement, isAideReportee);
	}
	if (typeAide.equals(AideEnum.ALLOCATION_LOGEMENT_SOCIALE.getCode())) {
	    aideLogement = creerAllocationLogementSociale(montantAideLogement, isAideReportee);
	}
	return aideLogement;
    }

    private Aide creerAidePersonnaliseeLogement(float montant, boolean isReportee) {
	return aideUtile.creerAide(AideEnum.AIDE_PERSONNALISEE_LOGEMENT, Optional.of(OrganismeEnum.CAF),
		Optional.of(MessageInformatifEnum.CHANGEMENT_MONTANT_PRESTATIONS_FAMILIALES.getMessage()), isReportee, montant);
    }

    private Aide creerAllocationLogementFamiliale(float montant, boolean isReportee) {
	return aideUtile.creerAide(AideEnum.ALLOCATION_LOGEMENT_FAMILIALE, Optional.of(OrganismeEnum.CAF),
		Optional.of(MessageInformatifEnum.CHANGEMENT_MONTANT_PRESTATIONS_FAMILIALES.getMessage()), isReportee, montant);
    }

    private Aide creerAllocationLogementSociale(float montant, boolean isReportee) {
	return aideUtile.creerAide(AideEnum.ALLOCATION_LOGEMENT_SOCIALE, Optional.of(OrganismeEnum.CAF),
		Optional.of(MessageInformatifEnum.CHANGEMENT_MONTANT_PRESTATIONS_FAMILIALES.getMessage()), isReportee, montant);
    }

    public boolean isEligibleAidesLogement(DemandeurEmploi demandeurEmploi) {
	boolean isEligible = false;
	if (demandeurEmploi.getInformationsPersonnelles() != null && demandeurEmploi.getInformationsPersonnelles().getLogement() != null
		&& demandeurEmploi.getInformationsPersonnelles().getLogement().getStatutOccupationLogement() != null
		&& (demandeurEmploi.getInformationsPersonnelles().getLogement().getStatutOccupationLogement().isLocataireHLM()
			|| demandeurEmploi.getInformationsPersonnelles().getLogement().getStatutOccupationLogement().isLocataireMeuble()
			|| demandeurEmploi.getInformationsPersonnelles().getLogement().getStatutOccupationLogement().isLocataireNonMeuble())) {
	    isEligible = true;
	}
	return isEligible;
    }

}
