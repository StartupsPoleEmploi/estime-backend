package fr.poleemploi.estime.logique.simulateur.aides.caf.utile;

import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fr.poleemploi.estime.commun.enumerations.AideEnum;
import fr.poleemploi.estime.commun.enumerations.OrganismeEnum;
import fr.poleemploi.estime.logique.simulateur.aides.utile.AideUtile;
import fr.poleemploi.estime.services.ressources.Aide;
import fr.poleemploi.estime.services.ressources.Simulation;

@Component
public class PrimeActiviteUtile {

    @Autowired
    private AideUtile aideUtile;

    public void reporterPrimeActivite(Simulation simulation, Map<String, Aide> aidesPourCeMois, int numeroMoisSimule) {
	Optional<Aide> primeActiviteMoisPrecedent = getPrimeActiviteMoisPrecedent(simulation, numeroMoisSimule);
	if (primeActiviteMoisPrecedent.isPresent()) {
	    aidesPourCeMois.put(AideEnum.PRIME_ACTIVITE.getCode(), primeActiviteMoisPrecedent.get());
	}
    }

    public Aide creerAidePrimeActivite(float montantPrimeActivite, boolean isAideReportee) {
	return aideUtile.creerAide(AideEnum.PRIME_ACTIVITE, Optional.of(OrganismeEnum.CAF), Optional.empty(), isAideReportee, montantPrimeActivite);
    }

    public Optional<Aide> getPrimeActiviteMoisPrecedent(Simulation simulation, int numeroMoisSimule) {
	int moisNMoins1 = numeroMoisSimule - 1;
	return aideUtile.getAidePourCeMoisSimule(simulation, AideEnum.PRIME_ACTIVITE.getCode(), moisNMoins1);
    }
}
