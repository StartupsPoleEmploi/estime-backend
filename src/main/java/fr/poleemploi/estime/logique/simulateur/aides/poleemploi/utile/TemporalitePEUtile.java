package fr.poleemploi.estime.logique.simulateur.aides.poleemploi.utile;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fr.poleemploi.estime.commun.utile.demandeuremploi.BeneficiaireAidesUtile;
import fr.poleemploi.estime.logique.simulateur.aides.utile.SimulateurAidesUtile;
import fr.poleemploi.estime.services.ressources.DemandeurEmploi;

@Component
public class TemporalitePEUtile {

    @Autowired
    private BeneficiaireAidesUtile beneficiaireAidesUtile;

    @Autowired
    private SimulateurAidesUtile simulateurAidesUtile;

    public boolean isConcerneParComplementARE(DemandeurEmploi demandeurEmploi) {
	return beneficiaireAidesUtile.isBeneficiaireARE(demandeurEmploi);
    }

    public boolean isAREAReporter(DemandeurEmploi demandeurEmploi, int numeroMoisSimule) {
	return simulateurAidesUtile.isPremierMois(numeroMoisSimule) && isConcerneParComplementARE(demandeurEmploi);
    }

    public boolean isComplementAREAVerser(DemandeurEmploi demandeurEmploi, int numeroMoisSimule) {
	return !simulateurAidesUtile.isPremierMois(numeroMoisSimule) && isConcerneParComplementARE(demandeurEmploi);
    }

    public boolean isAgepiEtAideMobiliteAVerser(int numeroMoisSimule) {
	return simulateurAidesUtile.isSecondMois(numeroMoisSimule);
    }
}
