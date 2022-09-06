package fr.poleemploi.estime.logique.simulateur.aides.caf.utile;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fr.poleemploi.estime.commun.utile.demandeuremploi.BeneficiaireAidesUtile;
import fr.poleemploi.estime.commun.utile.demandeuremploi.RessourcesFinancieresAvantSimulationUtile;
import fr.poleemploi.estime.services.ressources.DemandeurEmploi;

@Component
public class TemporaliteCAFUtile {

    @Autowired
    private BeneficiaireAidesUtile beneficiaireAidesUtile;

    @Autowired
    private PrimeActiviteRSAUtile primeActiviteRSAUtile;

    @Autowired
    private AidesLogementUtile aidesLogementUtile;

    @Autowired
    private PrimeActiviteAAHUtile primeActiviteAAHUtile;

    @Autowired
    private PrimeActiviteAREUtile primeActiviteAREUtile;

    @Autowired
    private PrimeActiviteASSUtile primeActiviteASSUtile;

    @Autowired
    private PrimeActiviteUtile primeActiviteUtile;

    @Autowired
    private RessourcesFinancieresAvantSimulationUtile ressourcesFinancieresUtile;

    @Autowired
    private RSAUtile rsaUtile;

    public boolean isRSAAVerser(int numeroMoisSimule, DemandeurEmploi demandeurEmploi) {
	boolean isRSAAVerser = false;
	if (beneficiaireAidesUtile.isBeneficiaireRSA(demandeurEmploi)) {
	    isRSAAVerser = rsaUtile.isRSAAVerser(numeroMoisSimule, demandeurEmploi);
	}
	return isRSAAVerser;
    }

    public boolean isAideLogementAVerser(int numeroMoisSimule, DemandeurEmploi demandeurEmploi) {
	return aidesLogementUtile.isEligibleAidesLogement(demandeurEmploi) && aidesLogementUtile.isAideLogementAVerser(numeroMoisSimule, demandeurEmploi);
    }

    public boolean isPrimeActiviteAVerser(int numeroMoisSimule, DemandeurEmploi demandeurEmploi) {
	boolean isPrimeActiviteAVerser = false;

	if (beneficiaireAidesUtile.isBeneficiaireCAF(demandeurEmploi)) {
	    int prochaineDeclarationTrimestrielle = ressourcesFinancieresUtile.getProchaineDeclarationTrimestrielle(demandeurEmploi);
	    isPrimeActiviteAVerser = primeActiviteUtile.isPrimeActiviteAVerserDeclarationTrimestrielle(numeroMoisSimule, prochaineDeclarationTrimestrielle);
	} else if (beneficiaireAidesUtile.isBeneficiaireASS(demandeurEmploi)) {
	    isPrimeActiviteAVerser = primeActiviteASSUtile.isPrimeActiviteAVerser(numeroMoisSimule, demandeurEmploi);
	} else if (beneficiaireAidesUtile.isBeneficiaireARE(demandeurEmploi)) {
	    isPrimeActiviteAVerser = primeActiviteAREUtile.isPrimeActiviteAVerser(numeroMoisSimule);
	}
	//	if (beneficiaireAidesUtile.isBeneficiaireRSA(demandeurEmploi)) {
	//	    isPrimeActiviteAVerser = primeActiviteRSAUtile.isPrimeActiviteAVerser(numeroMoisSimule, demandeurEmploi);
	//	}
	//	else if (beneficiaireAidesUtile.isBeneficiaireAAH(demandeurEmploi)) {
	//	    isPrimeActiviteAVerser = primeActiviteAAHUtile.isPrimeActiviteAVerser(numeroMoisSimule, demandeurEmploi);
	//
	return isPrimeActiviteAVerser;
    }
}
