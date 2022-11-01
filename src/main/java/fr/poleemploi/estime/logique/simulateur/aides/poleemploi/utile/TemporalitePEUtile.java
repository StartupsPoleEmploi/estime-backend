package fr.poleemploi.estime.logique.simulateur.aides.poleemploi.utile;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fr.poleemploi.estime.logique.simulateur.aides.utile.SimulateurAidesUtile;
import fr.poleemploi.estime.services.ressources.DemandeurEmploi;

@Component
public class TemporalitePEUtile {

    @Autowired
    private AreUtile areUtile;

    @Autowired
    private SimulateurAidesUtile simulateurAidesUtile;

    public boolean isAREAReporter(DemandeurEmploi demandeurEmploi, int numeroMoisSimule) {
	return areUtile.isAreAReporter(demandeurEmploi, numeroMoisSimule);
    }

    public boolean isComplementAREAVerser(DemandeurEmploi demandeurEmploi, int numeroMoisSimule) {
	return areUtile.isComplementAREAVerser(demandeurEmploi, numeroMoisSimule);
    }

    public boolean isAgepiEtAideMobiliteAVerser(int numeroMoisSimule) {
	return simulateurAidesUtile.isSecondMois(numeroMoisSimule);
    }
}
