package fr.poleemploi.estime.commun.utile;

import org.springframework.stereotype.Component;

import fr.poleemploi.estime.clientsexternes.poleemploiio.ressources.UserInfoPEIOOut;
import fr.poleemploi.estime.services.ressources.AidesPoleEmploi;
import fr.poleemploi.estime.services.ressources.AllocationASS;
import fr.poleemploi.estime.services.ressources.BeneficiaireAides;
import fr.poleemploi.estime.services.ressources.Individu;
import fr.poleemploi.estime.services.ressources.InformationsPersonnelles;
import fr.poleemploi.estime.services.ressources.RessourcesFinancieresAvantSimulation;

@Component
public class DemandeurDemoUtile {

    private static String nomDemandeurDemo = "estimedemo";

    public boolean isDemandeurDemo(UserInfoPEIOOut userInfoESD) {
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
	RessourcesFinancieresAvantSimulation ressourcesFinancieres = new RessourcesFinancieresAvantSimulation();
	ressourcesFinancieres.setAidesPoleEmploi(creerAidePoleEmploi());
	individu.setRessourcesFinancieresAvantSimulation(ressourcesFinancieres);
    }

    public void addInformationsPersonnelles(Individu individu, UserInfoPEIOOut userInfoPEIO) {
	InformationsPersonnelles informationsPersonnelles = new InformationsPersonnelles();
	if (userInfoPEIO.getEmail() != null) {
	    informationsPersonnelles.setEmail(userInfoPEIO.getEmail());
	}
	individu.setInformationsPersonnelles(informationsPersonnelles);
    }

    private AidesPoleEmploi creerAidePoleEmploi() {
	AidesPoleEmploi aidesPoleEmploi = new AidesPoleEmploi();
	AllocationASS allocationASS = new AllocationASS();
	aidesPoleEmploi.setAllocationASS(allocationASS);
	return aidesPoleEmploi;
    }
}
