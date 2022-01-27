package fr.poleemploi.estime.logique.simulateur.aides.caf.utile;

import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fr.poleemploi.estime.clientsexternes.openfisca.OpenFiscaClient;
import fr.poleemploi.estime.clientsexternes.openfisca.OpenFiscaRetourSimulation;
import fr.poleemploi.estime.commun.enumerations.AideEnum;
import fr.poleemploi.estime.commun.enumerations.MessageInformatifEnum;
import fr.poleemploi.estime.commun.enumerations.OrganismeEnum;
import fr.poleemploi.estime.logique.simulateur.aides.utile.AideUtile;
import fr.poleemploi.estime.services.ressources.Aide;
import fr.poleemploi.estime.services.ressources.DemandeurEmploi;
import fr.poleemploi.estime.services.ressources.SimulationAides;

@Component
public class PrimeActiviteUtile {

    @Autowired
    private OpenFiscaClient openFiscaClient;

    @Autowired
    private AideUtile aideeUtile;

    public void calculerPrimeActivite(SimulationAides simulationAides, Map<String, Aide> aidesPourCeMois, LocalDate dateDebutSimulation, int numeroMoisSimule, DemandeurEmploi demandeurEmploi) {
	OpenFiscaRetourSimulation openFiscaRetourSimulation = openFiscaClient.calculerPrimeActivite(simulationAides, demandeurEmploi, dateDebutSimulation, numeroMoisSimule);
	if (openFiscaRetourSimulation.getMontantPrimeActivite() > 0) {
	    Aide primeActivite = creerAidePrimeActivite(openFiscaRetourSimulation.getMontantPrimeActivite(), false);
	    aidesPourCeMois.put(AideEnum.PRIME_ACTIVITE.getCode(), primeActivite);
	}
    }

    void reporterPrimeActivite(SimulationAides simulationAides, Map<String, Aide> aidesPourCeMois, int numeroMoisSimule) {
	Optional<Aide> primeActiviteMoisPrecedent = getPrimeActiviteMoisPrecedent(simulationAides, numeroMoisSimule);
	if (primeActiviteMoisPrecedent.isPresent()) {
	    aidesPourCeMois.put(AideEnum.PRIME_ACTIVITE.getCode(), primeActiviteMoisPrecedent.get());
	}
    }

    public Aide creerAidePrimeActivite(float montantPrimeActivite, boolean isAideReportee) {
	Aide aidePrimeActivite = new Aide();
	aidePrimeActivite.setCode(AideEnum.PRIME_ACTIVITE.getCode());
	Optional<String> detailAideOptional = aideeUtile.getDescription(AideEnum.PRIME_ACTIVITE.getNomFichierDetail());
	if (detailAideOptional.isPresent()) {
	    aidePrimeActivite.setDetail(detailAideOptional.get());
	}
	aidePrimeActivite.setMessageAlerte(MessageInformatifEnum.PPA_AUTOMATIQUE_SI_BENEFICIAIRE_RSA.getMessage());
	aidePrimeActivite.setMontant(montantPrimeActivite);
	aidePrimeActivite.setNom(AideEnum.PRIME_ACTIVITE.getNom());
	aidePrimeActivite.setOrganisme(OrganismeEnum.CAF.getNomCourt());
	aidePrimeActivite.setReportee(isAideReportee);
	return aidePrimeActivite;
    }

    public Optional<Aide> getPrimeActiviteMoisPrecedent(SimulationAides simulationAides, int numeroMoisSimule) {
	int moisNMoins1 = numeroMoisSimule - 1;
	return aideeUtile.getAidePourCeMoisSimule(simulationAides, AideEnum.PRIME_ACTIVITE.getCode(), moisNMoins1);
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
    public void reporterPrimeActiviteMoisPrecedent(SimulationAides simulationAides, Map<String, Aide> aidesPourCeMois, LocalDate dateDebutSimulation, int numeroMoisSimule,
                                                   DemandeurEmploi demandeurEmploi, Optional<Aide> primeActiviteMoisPrecedentOptional) {

	// le montant calculé au mois N est reporté au mois N+1 et N+2, la validité du montant s'étale donc sur 3 mois
	int moisMoinsPeriodeValiditeMontant = numeroMoisSimule - 3;

	Optional<Aide> primeActiviteDebutPeriodeValiditeMontant = aideeUtile.getAidePourCeMoisSimule(simulationAides, AideEnum.PRIME_ACTIVITE.getCode(), moisMoinsPeriodeValiditeMontant);

	// si montant prime d'activité en N - NBR_MOIS_VALIDITE_MONTANT n'a pas été reporté alors on recalcule le montant
	// sinon on reporte le montant de la prime d'activité précédente si prime d'activité il y a eu.
	if (primeActiviteDebutPeriodeValiditeMontant.isPresent() && !primeActiviteDebutPeriodeValiditeMontant.get().isReportee()) {
	    calculerPrimeActivite(simulationAides, aidesPourCeMois, dateDebutSimulation, numeroMoisSimule, demandeurEmploi);
	} else if (primeActiviteMoisPrecedentOptional.isPresent()) {
	    Aide primeActiviteMoisPrecedent = primeActiviteMoisPrecedentOptional.get();
	    Aide primeActiviteMoisSimule = creerAidePrimeActivite(primeActiviteMoisPrecedent.getMontant(), true);
	    aidesPourCeMois.put(AideEnum.PRIME_ACTIVITE.getCode(), primeActiviteMoisSimule);
	}
    }
}
