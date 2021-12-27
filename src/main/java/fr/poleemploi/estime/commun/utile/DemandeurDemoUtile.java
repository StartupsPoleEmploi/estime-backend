package fr.poleemploi.estime.commun.utile;

import org.springframework.stereotype.Component;

import fr.poleemploi.estime.clientsexternes.poleemploiio.ressources.UserInfoPEIO;
import fr.poleemploi.estime.services.ressources.AidesPoleEmploi;
import fr.poleemploi.estime.services.ressources.AllocationASS;
import fr.poleemploi.estime.services.ressources.BeneficiaireAides;
import fr.poleemploi.estime.services.ressources.Individu;
import fr.poleemploi.estime.services.ressources.RessourcesFinancieres;

@Component
public class DemandeurDemoUtile {

	private static String nomDemandeurDemo = "estimedemo";

	public boolean isDemandeurDemo(UserInfoPEIO userInfoESD) {
		return userInfoESD.getFamilyName() != null && userInfoESD.getFamilyName().toLowerCase().contains(nomDemandeurDemo);
	}

	public void addInformationsDetailIndemnisationPoleEmploi(Individu individu) {
		addInformationsBeneficiaireAides(individu);
		addInformationsRessourcesFinancieresPoleEmploi(individu);
	}

	private void addInformationsBeneficiaireAides(Individu individu) {
		BeneficiaireAides beneficiaireAides = new BeneficiaireAides();
		beneficiaireAides.setBeneficiaireAAH(false);
		beneficiaireAides.setBeneficiaireARE(false);
		beneficiaireAides.setBeneficiaireASS(true);
		beneficiaireAides.setBeneficiaireRSA(false);
		individu.setBeneficiaireAides(beneficiaireAides);
	}

	private void addInformationsRessourcesFinancieresPoleEmploi(Individu individu) {
		RessourcesFinancieres ressourcesFinancieres = new RessourcesFinancieres();
		ressourcesFinancieres.setAidesPoleEmploi(creerAidePoleEmploi());
		individu.setRessourcesFinancieres(ressourcesFinancieres);
	}

	private AidesPoleEmploi creerAidePoleEmploi() {
		AidesPoleEmploi aidesPoleEmploi = new AidesPoleEmploi();
		AllocationASS allocationASS = new AllocationASS();
		aidesPoleEmploi.setAllocationASS(allocationASS);
		return aidesPoleEmploi;
	}
}
