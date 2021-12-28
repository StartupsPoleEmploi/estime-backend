package fr.poleemploi.estime.commun.utile;

import org.springframework.stereotype.Component;

import fr.poleemploi.estime.clientsexternes.poleemploiio.ressources.DetailIndemnisationPEIO;
import fr.poleemploi.estime.commun.enumerations.AideEnum;
import fr.poleemploi.estime.commun.enumerations.TypePopulationEnum;
import fr.poleemploi.estime.services.ressources.AidesPoleEmploi;
import fr.poleemploi.estime.services.ressources.AllocationARE;
import fr.poleemploi.estime.services.ressources.AllocationASS;
import fr.poleemploi.estime.services.ressources.BeneficiaireAides;
import fr.poleemploi.estime.services.ressources.Individu;
import fr.poleemploi.estime.services.ressources.RessourcesFinancieres;

@Component
public class IndividuUtile {

	/**
	 * Population autorisée ASS, RSA ou AAH, et pouvant cumuler ces 3 prestations.
	 */
	public boolean isPopulationAutorisee(DetailIndemnisationPEIO detailIndemnisationPEIO) {
		return isBeneficiaireARE(detailIndemnisationPEIO) || isBeneficiaireASS(detailIndemnisationPEIO) || detailIndemnisationPEIO.isBeneficiaireAAH()
				|| detailIndemnisationPEIO.isBeneficiaireRSA();
	}

	public void addInformationsDetailIndemnisationPoleEmploi(Individu individu, DetailIndemnisationPEIO detailIndemnisationPEIO) {
		addInformationsBeneficiaireAides(individu, detailIndemnisationPEIO);
		addInformationsRessourcesFinancieresPoleEmploi(individu, detailIndemnisationPEIO);
	}

	private void addInformationsBeneficiaireAides(Individu individu, DetailIndemnisationPEIO detailIndemnisationPEIO) {
		BeneficiaireAides beneficiaireAides = new BeneficiaireAides();
		beneficiaireAides.setBeneficiaireAAH(detailIndemnisationPEIO.isBeneficiaireAAH());
		beneficiaireAides.setBeneficiaireARE(isBeneficiaireARE(detailIndemnisationPEIO));
		beneficiaireAides.setBeneficiaireASS(isBeneficiaireASS(detailIndemnisationPEIO));
		beneficiaireAides.setBeneficiaireRSA(detailIndemnisationPEIO.isBeneficiaireRSA());
		individu.setBeneficiaireAides(beneficiaireAides);
	}

	private void addInformationsRessourcesFinancieresPoleEmploi(Individu individu, DetailIndemnisationPEIO detailIndemnisationPEIO) {
		RessourcesFinancieres ressourcesFinancieres = new RessourcesFinancieres();
		if (detailIndemnisationPEIO.getCodeIndemnisation() != null) {
			ressourcesFinancieres.setAidesPoleEmploi(creerAidePoleEmploi(detailIndemnisationPEIO));
		}
		individu.setRessourcesFinancieres(ressourcesFinancieres);
	}

	private AidesPoleEmploi creerAidePoleEmploi(DetailIndemnisationPEIO detailIndemnisationPEIO) {
		AidesPoleEmploi aidesPoleEmploi = new AidesPoleEmploi();
		if (AideEnum.ALLOCATION_SOLIDARITE_SPECIFIQUE.getCode().equals(detailIndemnisationPEIO.getCodeIndemnisation())) {
			AllocationASS allocationASS = new AllocationASS();
			allocationASS.setAllocationJournaliereNet(detailIndemnisationPEIO.getIndemnisationJournalierNet());
			aidesPoleEmploi.setAllocationASS(allocationASS);
		}
		if (AideEnum.ALLOCATION_RETOUR_EMPLOI.getCode().equals(detailIndemnisationPEIO.getCodeIndemnisation())) {
			AllocationARE allocationARE = new AllocationARE();
			allocationARE.setAllocationJournaliereNet(detailIndemnisationPEIO.getIndemnisationJournalierNet());
			aidesPoleEmploi.setAllocationARE(allocationARE);
		}
		return aidesPoleEmploi;
	}

	private boolean isBeneficiaireARE(DetailIndemnisationPEIO detailIndemnisationPEIO) {
		return detailIndemnisationPEIO.getCodeIndemnisation() != null && TypePopulationEnum.ARE.getLibelle().equals(detailIndemnisationPEIO.getCodeIndemnisation());
	}

	private boolean isBeneficiaireASS(DetailIndemnisationPEIO detailIndemnisationPEIO) {
		return detailIndemnisationPEIO.getCodeIndemnisation() != null && TypePopulationEnum.ASS.getLibelle().equals(detailIndemnisationPEIO.getCodeIndemnisation());
	}
}
