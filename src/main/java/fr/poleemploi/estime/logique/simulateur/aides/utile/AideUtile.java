package fr.poleemploi.estime.logique.simulateur.aides.utile;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fr.poleemploi.estime.commun.enumerations.AideEnum;
import fr.poleemploi.estime.commun.enumerations.OrganismeEnum;
import fr.poleemploi.estime.commun.utile.DateUtile;
import fr.poleemploi.estime.services.ressources.Aide;
import fr.poleemploi.estime.services.ressources.Simulation;
import fr.poleemploi.estime.services.ressources.SimulationMensuelle;

@Component
public class AideUtile {

    @Autowired
    private DateUtile dateUtile;

    public Aide creerAide(AideEnum aideEnum, Optional<OrganismeEnum> organismeEnumOptional, Optional<List<String>> messageAlerteOptional, boolean isAideReportee, float montantAide) {
	Aide aide = new Aide();
	aide.setCode(aideEnum.getCode());
	if (messageAlerteOptional.isPresent()) {
	    aide.setMessagesAlerte(messageAlerteOptional.get());
	}
	aide.setMontant(montantAide);
	aide.setNom(aideEnum.getNom());
	if (organismeEnumOptional.isPresent()) {
	    aide.setOrganisme(organismeEnumOptional.get().getNomCourt());
	}
	aide.setReportee(isAideReportee);
	return aide;
    }

    public Aide creerAideVide(AideEnum aideEnum) {
	Aide aide = new Aide();
	aide.setCode(aideEnum.getCode());
	aide.setNom(aideEnum.getNom());
	return aide;
    }

    public float getMontantAidePourCeMoisSimule(Simulation simulation, String codeAide, int numeroMoisMontantARecuperer) {
	Optional<Aide> aidePourCeMois = getAidePourCeMoisSimule(simulation, codeAide, numeroMoisMontantARecuperer);
	if (aidePourCeMois.isPresent()) {
	    return aidePourCeMois.get().getMontant();
	}
	return 0;
    }

    public Optional<Aide> getAidePourCeMoisSimule(Simulation simulation, String codeAide, int numeroMois) {
	if (simulation != null && simulation.getSimulationsMensuelles() != null) {
	    int indexSimulationMensuelARecuperer = numeroMois - 1;
	    if (indexSimulationMensuelARecuperer >= 0 && indexSimulationMensuelARecuperer < simulation.getSimulationsMensuelles().size()) {
		SimulationMensuelle simulationMensuelle = simulation.getSimulationsMensuelles().get(indexSimulationMensuelARecuperer);
		if (simulationMensuelle != null && simulationMensuelle.getAides() != null && !simulationMensuelle.getAides().isEmpty()) {
		    Aide aide = simulationMensuelle.getAides().get(codeAide);
		    if (aide != null) {
			return Optional.of(aide);
		    }
		}
	    }
	}
	return Optional.empty();
    }

    public LocalDate getMoisAvantSimulation(int numeroMoisMontantARecuperer, LocalDate dateDebutSimulation) {
	LocalDate dateDemandeSimulation = dateUtile.enleverMoisALocalDate(dateDebutSimulation, 1);
	if (numeroMoisMontantARecuperer < 0) {
	    return dateUtile.enleverMoisALocalDate(dateDemandeSimulation, Math.abs(numeroMoisMontantARecuperer));
	}
	return dateDemandeSimulation;
    }
}
