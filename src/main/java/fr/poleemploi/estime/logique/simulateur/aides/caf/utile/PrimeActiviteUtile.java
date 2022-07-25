package fr.poleemploi.estime.logique.simulateur.aides.caf.utile;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fr.poleemploi.estime.commun.enumerations.AideEnum;
import fr.poleemploi.estime.commun.enumerations.MessageInformatifEnum;
import fr.poleemploi.estime.commun.enumerations.OrganismeEnum;
import fr.poleemploi.estime.commun.utile.DateUtile;
import fr.poleemploi.estime.logique.simulateur.aides.utile.AideUtile;
import fr.poleemploi.estime.services.ressources.Aide;
import fr.poleemploi.estime.services.ressources.Simulation;

@Component
public class PrimeActiviteUtile {

    @Autowired
    private AideUtile aideUtile;

    @Autowired
    private DateUtile dateUtile;

    public void reporterPrimeActivite(Simulation simulation, Map<String, Aide> aidesPourCeMois, int numeroMoisSimule) {
	Optional<Aide> primeActiviteMoisPrecedent = getPrimeActiviteMoisPrecedent(simulation, numeroMoisSimule);
	if (primeActiviteMoisPrecedent.isPresent()) {
	    aidesPourCeMois.put(AideEnum.PRIME_ACTIVITE.getCode(), primeActiviteMoisPrecedent.get());
	}
    }

    public Aide creerAidePrimeActivite(float montantPrimeActivite, boolean isAideReportee, LocalDate dateDebutSimulation, int numeroMoisSimule) {
	ArrayList<String> messagesAlerte = new ArrayList<>();
	messagesAlerte.add(getMessageAlerteDemandePPA(dateDebutSimulation, numeroMoisSimule));
	return aideUtile.creerAide(AideEnum.PRIME_ACTIVITE, Optional.of(OrganismeEnum.CAF), Optional.of(messagesAlerte), isAideReportee, montantPrimeActivite);
    }

    public Optional<Aide> getPrimeActiviteMoisPrecedent(Simulation simulation, int numeroMoisSimule) {
	int moisNMoins1 = numeroMoisSimule - 1;
	return aideUtile.getAidePourCeMoisSimule(simulation, AideEnum.PRIME_ACTIVITE.getCode(), moisNMoins1);
    }

    private String getMessageAlerteDemandePPA(LocalDate dateDebutSimulation, int numeroMoisSimule) {
	LocalDate dateMoisMMoins1 = dateUtile.ajouterMoisALocalDate(dateDebutSimulation, numeroMoisSimule - 2);
	String libelleMoisMMoins1 = dateUtile.getMonthNameFromLocalDate(dateMoisMMoins1);

	if (libelleMoisMMoins1.equals("avril") || libelleMoisMMoins1.equals("août") || libelleMoisMMoins1.equals("octobre")) {
	    return String.format(MessageInformatifEnum.DEMANDE_PPA.getMessage(), "'" + libelleMoisMMoins1);
	} else {
	    return String.format(MessageInformatifEnum.DEMANDE_PPA.getMessage(), "e " + libelleMoisMMoins1);
	}
    }
}
