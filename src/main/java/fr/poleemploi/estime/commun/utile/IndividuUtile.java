package fr.poleemploi.estime.commun.utile;

import org.springframework.stereotype.Component;

import fr.poleemploi.estime.clientsexternes.poleemploiio.ressources.DetailIndemnisationPEIOOut;
import fr.poleemploi.estime.clientsexternes.poleemploiio.ressources.UserInfoPEIOOut;
import fr.poleemploi.estime.commun.enumerations.TypePopulationEnum;
import fr.poleemploi.estime.services.ressources.AidesPoleEmploi;
import fr.poleemploi.estime.services.ressources.AllocationARE;
import fr.poleemploi.estime.services.ressources.AllocationASS;
import fr.poleemploi.estime.services.ressources.BeneficiaireAides;
import fr.poleemploi.estime.services.ressources.Individu;
import fr.poleemploi.estime.services.ressources.InformationsPersonnelles;
import fr.poleemploi.estime.services.ressources.RessourcesFinancieresAvantSimulation;

@Component
public class IndividuUtile {

    public void addInformationsDetailIndemnisationPoleEmploi(Individu individu, DetailIndemnisationPEIOOut detailIndemnisationPEIO) {
	addInformationsBeneficiaireAides(individu, detailIndemnisationPEIO);
	addInformationsRessourcesFinancieresPoleEmploi(individu, detailIndemnisationPEIO);
    }

    private void addInformationsBeneficiaireAides(Individu individu, DetailIndemnisationPEIOOut detailIndemnisationPEIO) {
	BeneficiaireAides beneficiaireAides = new BeneficiaireAides();
	beneficiaireAides.setBeneficiaireAAH(detailIndemnisationPEIO.isBeneficiaireAAH());
	beneficiaireAides.setBeneficiaireARE(isBeneficiaireARE(detailIndemnisationPEIO));
	beneficiaireAides.setBeneficiaireASS(isBeneficiaireASS(detailIndemnisationPEIO));
	beneficiaireAides.setBeneficiaireRSA(detailIndemnisationPEIO.isBeneficiaireRSA());
	individu.setBeneficiaireAides(beneficiaireAides);
    }

    private void addInformationsRessourcesFinancieresPoleEmploi(Individu individu, DetailIndemnisationPEIOOut detailIndemnisationPEIO) {
	RessourcesFinancieresAvantSimulation ressourcesFinancieres = new RessourcesFinancieresAvantSimulation();
	if (detailIndemnisationPEIO.getCodeIndemnisation() != null) {
	    ressourcesFinancieres.setAidesPoleEmploi(creerAidePoleEmploi(detailIndemnisationPEIO));
	}
	individu.setRessourcesFinancieresAvantSimulation(ressourcesFinancieres);
    }

    public void addInformationsPersonnelles(Individu individu, UserInfoPEIOOut userInfoPEIO) {
	InformationsPersonnelles informationsPersonnelles = new InformationsPersonnelles();
	if (userInfoPEIO.getEmail() != null) {
	    informationsPersonnelles.setEmail(userInfoPEIO.getEmail());
	}
	individu.setInformationsPersonnelles(informationsPersonnelles);
    }

    private AidesPoleEmploi creerAidePoleEmploi(DetailIndemnisationPEIOOut detailIndemnisationPEIO) {
	AidesPoleEmploi aidesPoleEmploi = new AidesPoleEmploi();
	if (TypePopulationEnum.ASS.getLibelle().equals(detailIndemnisationPEIO.getCodeIndemnisation())) {
	    AllocationASS allocationASS = new AllocationASS();
	    allocationASS.setAllocationJournaliereNet(detailIndemnisationPEIO.getIndemnisationJournalierNet());
	    aidesPoleEmploi.setAllocationASS(allocationASS);
	}
	if (TypePopulationEnum.ARE.getLibelle().equals(detailIndemnisationPEIO.getCodeIndemnisation())
		|| TypePopulationEnum.AREF.getLibelle().equals(detailIndemnisationPEIO.getCodeIndemnisation())) {
	    AllocationARE allocationARE = new AllocationARE();
	    aidesPoleEmploi.setAllocationARE(allocationARE);
	}
	return aidesPoleEmploi;
    }

    private boolean isBeneficiaireARE(DetailIndemnisationPEIOOut detailIndemnisationPEIO) {
	return detailIndemnisationPEIO.getCodeIndemnisation() != null && (TypePopulationEnum.ARE.getLibelle().equals(detailIndemnisationPEIO.getCodeIndemnisation())
		|| TypePopulationEnum.AREF.getLibelle().equals(detailIndemnisationPEIO.getCodeIndemnisation()));
    }

    private boolean isBeneficiaireASS(DetailIndemnisationPEIOOut detailIndemnisationPEIO) {
	return detailIndemnisationPEIO.getCodeIndemnisation() != null && TypePopulationEnum.ASS.getLibelle().equals(detailIndemnisationPEIO.getCodeIndemnisation());
    }

    public Individu creerIndividuNonConnecte() {
	Individu individu = new Individu();
	individu.setBeneficiaireAides(new BeneficiaireAides());
	individu.setInformationsPersonnelles(new InformationsPersonnelles());
	individu.setRessourcesFinancieresAvantSimulation(new RessourcesFinancieresAvantSimulation());

	return individu;
    }
}
