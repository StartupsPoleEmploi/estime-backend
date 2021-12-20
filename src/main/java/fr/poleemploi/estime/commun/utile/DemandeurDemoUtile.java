package fr.poleemploi.estime.commun.utile;

import org.springframework.stereotype.Component;

import fr.poleemploi.estime.clientsexternes.poleemploiio.ressources.DetailIndemnisationPEIO;
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

	public void addInformationsDetailIndemnisationPoleEmploi(Individu individu, DetailIndemnisationPEIO detailIndemnisationESD) {
		addInformationsBeneficiaireAides(individu, detailIndemnisationESD);
		addInformationsRessourcesFinancieresPoleEmploi(individu, detailIndemnisationESD);
	}

	private void addInformationsBeneficiaireAides(Individu individu, DetailIndemnisationPEIO detailIndemnisation) {
		BeneficiaireAides beneficiaireAides = new BeneficiaireAides();
		beneficiaireAides.setBeneficiaireAAH(false);
		beneficiaireAides.setBeneficiaireARE(false);
		beneficiaireAides.setBeneficiaireASS(true);
		beneficiaireAides.setBeneficiaireRSA(false);
		individu.setBeneficiaireAides(beneficiaireAides);
	}

	private void addInformationsRessourcesFinancieresPoleEmploi(Individu individu, DetailIndemnisationPEIO detailIndemnisation) {
		RessourcesFinancieres ressourcesFinancieres = new RessourcesFinancieres();
		ressourcesFinancieres.setAidesPoleEmploi(creerAidePoleEmploi(detailIndemnisation));
		individu.setRessourcesFinancieres(ressourcesFinancieres);
	}

	private AidesPoleEmploi creerAidePoleEmploi(DetailIndemnisationPEIO detailIndemnisation) {
		AidesPoleEmploi aidesPoleEmploi = new AidesPoleEmploi();
		AllocationASS allocationASS = new AllocationASS();
		aidesPoleEmploi.setAllocationASS(allocationASS);
		return aidesPoleEmploi;
	}
}
