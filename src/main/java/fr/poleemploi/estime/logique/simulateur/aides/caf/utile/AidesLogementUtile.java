package fr.poleemploi.estime.logique.simulateur.aides.caf.utile;

import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
	int prochaineDeclarationTrimestrielle = demandeurEmploi.getRessourcesFinancieres().getAidesCAF().getProchaineDeclarationTrimestrielle();
	return ((numeroMoisSimule == 1) || (prochaineDeclarationTrimestrielle == numeroMoisSimule)
		|| (prochaineDeclarationTrimestrielle == numeroMoisSimule - 3) || (prochaineDeclarationTrimestrielle == numeroMoisSimule - 6));
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
	int prochaineDeclarationTrimestrielle = demandeurEmploi.getRessourcesFinancieres().getAidesCAF().getProchaineDeclarationTrimestrielle();
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
	Optional<Aide> aidePourCeMois = aideUtile.getAidePourCeMoisSimule(simulationAides, Aides.AIDE_PERSONNALISEE_LOGEMENT.getCode(), moisNMoins1);
	if (aidePourCeMois.isEmpty())
	    aidePourCeMois = aideUtile.getAidePourCeMoisSimule(simulationAides, Aides.ALLOCATION_LOGEMENT_FAMILIALE.getCode(), moisNMoins1);
	if (aidePourCeMois.isEmpty())
	    aidePourCeMois = aideUtile.getAidePourCeMoisSimule(simulationAides, Aides.ALLOCATION_LOGEMENT_SOCIALE.getCode(), moisNMoins1);
	return aidePourCeMois;
    }

    private boolean isEligiblePourReportAideLogementDeclare(DemandeurEmploi demandeurEmploi, int numeroMoisSimule) {
	return ressourcesFinancieresUtile.hasAidesLogement(demandeurEmploi) 
		&& demandeurEmploi.getRessourcesFinancieres().getAidesCAF().getProchaineDeclarationTrimestrielle() != null
		&& (numeroMoisSimule == 1 || numeroMoisSimule <= demandeurEmploi.getRessourcesFinancieres().getAidesCAF().getProchaineDeclarationTrimestrielle());
    }

    protected Aide creerAideLogement(float montantAideLogement, String typeAide, boolean isAideReportee) {
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
