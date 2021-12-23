package fr.poleemploi.estime.commun.utile;

import java.time.LocalDate;

import org.springframework.stereotype.Component;

import fr.poleemploi.estime.clientsexternes.poleemploiio.ressources.DetailIndemnisationPEIO;
import fr.poleemploi.estime.commun.enumerations.AideEnum;
import fr.poleemploi.estime.commun.enumerations.TypePopulation;
import fr.poleemploi.estime.services.ressources.AidesPoleEmploi;
import fr.poleemploi.estime.services.ressources.AllocationARE;
import fr.poleemploi.estime.services.ressources.AllocationASS;
import fr.poleemploi.estime.services.ressources.BeneficiaireAides;
import fr.poleemploi.estime.services.ressources.Individu;
import fr.poleemploi.estime.services.ressources.InformationsPersonnelles;
import fr.poleemploi.estime.services.ressources.Logement;
import fr.poleemploi.estime.services.ressources.RessourcesFinancieres;
import fr.poleemploi.estime.services.ressources.StatutOccupationLogement;

@Component
public class IndividuUtile {

    /**
     * Population autorisée ASS, RSA ou AAH, et pouvant cumuler ces 3 prestations.
     */
    public boolean isPopulationAutorisee(DetailIndemnisationPEIO detailIndemnisationESD) {
	return !isBeneficiaireARE(detailIndemnisationESD)
		&& (isBeneficiaireASS(detailIndemnisationESD) || detailIndemnisationESD.isBeneficiaireAAH() || detailIndemnisationESD.isBeneficiaireRSA());
    }

    public void addInformationsDetailIndemnisationPoleEmploi(Individu individu, DetailIndemnisationPEIO detailIndemnisationESD) {
	addInformationsBeneficiaireAides(individu, detailIndemnisationESD);
	addInformationsRessourcesFinancieresPoleEmploi(individu, detailIndemnisationESD);
    }

    private void addInformationsBeneficiaireAides(Individu individu, DetailIndemnisationPEIO detailIndemnisation) {
	BeneficiaireAides beneficiaireAides = new BeneficiaireAides();
	beneficiaireAides.setBeneficiaireAAH(detailIndemnisation.isBeneficiaireAAH());
	beneficiaireAides.setBeneficiaireARE(isBeneficiaireARE(detailIndemnisation));
	beneficiaireAides.setBeneficiaireASS(isBeneficiaireASS(detailIndemnisation));
	beneficiaireAides.setBeneficiaireRSA(detailIndemnisation.isBeneficiaireRSA());
	individu.setBeneficiaireAides(beneficiaireAides);
    }

    private void addInformationsRessourcesFinancieresPoleEmploi(Individu individu, DetailIndemnisationPEIO detailIndemnisation) {
	RessourcesFinancieres ressourcesFinancieres = new RessourcesFinancieres();
	if (detailIndemnisation.getCodeIndemnisation() != null) {
	    ressourcesFinancieres.setAidesPoleEmploi(creerAidePoleEmploi(detailIndemnisation));
	}
	individu.setRessourcesFinancieres(ressourcesFinancieres);
    }

    private AidesPoleEmploi creerAidePoleEmploi(DetailIndemnisationPEIO detailIndemnisation) {
	AidesPoleEmploi aidesPoleEmploi = new AidesPoleEmploi();
	if (AideEnum.ALLOCATION_SOLIDARITE_SPECIFIQUE.getCode().equals(detailIndemnisation.getCodeIndemnisation())) {
	    AllocationASS allocationASS = new AllocationASS();
	    allocationASS.setAllocationJournaliereNet(detailIndemnisation.getIndemnisationJournalierNet());
	    aidesPoleEmploi.setAllocationASS(allocationASS);
	}
	if (AideEnum.ALLOCATION_RETOUR_EMPLOI.getCode().equals(detailIndemnisation.getCodeIndemnisation())) {
	    AllocationARE allocationARE = new AllocationARE();
	    allocationARE.setAllocationJournaliereNet(detailIndemnisation.getIndemnisationJournalierNet());
	    aidesPoleEmploi.setAllocationARE(allocationARE);
	}
	return aidesPoleEmploi;
    }

    private boolean isBeneficiaireARE(DetailIndemnisationPEIO detailIndemnisationESD) {
	return detailIndemnisationESD.getCodeIndemnisation() != null && TypePopulation.ARE.getLibelle().equals(detailIndemnisationESD.getCodeIndemnisation());
    }

    private boolean isBeneficiaireASS(DetailIndemnisationPEIO detailIndemnisationESD) {
	return detailIndemnisationESD.getCodeIndemnisation() != null && TypePopulation.ASS.getLibelle().equals(detailIndemnisationESD.getCodeIndemnisation());
    }

    public InformationsPersonnelles creerInformationsPersonnelles() {
	InformationsPersonnelles informationsPersonnelles = new InformationsPersonnelles();
	informationsPersonnelles.setCodePostal("44000");
	informationsPersonnelles.setDateNaissance(LocalDate.of(1985, 8, 1));
	Logement logement = creerLogement();
	StatutOccupationLogement statutOccupationLogement = creerStatutOccupationLogement();
	logement.setStatutOccupationLogement(statutOccupationLogement);
	informationsPersonnelles.setLogement(logement);

	return informationsPersonnelles;
    }

    public Logement creerLogement() {
	Logement logement = new Logement();
	logement.setChambre(false);
	logement.setCodeInsee(null);
	logement.setConventionne(false);
	logement.setColloc(false);
	logement.setCrous(false);
	logement.setDeMayotte(false);
	logement.setMontantCharges(0);
	logement.setMontantLoyer(0);
	logement.setStatutOccupationLogement(null);
	return logement;
    }

    public StatutOccupationLogement creerStatutOccupationLogement() {
	StatutOccupationLogement statutOccupationLogement = new StatutOccupationLogement();
	statutOccupationLogement.setLocataireHLM(false);
	statutOccupationLogement.setLocataireMeuble(false);
	statutOccupationLogement.setLocataireNonMeuble(false);
	statutOccupationLogement.setLogeGratuitement(false);
	statutOccupationLogement.setProprietaire(false);
	statutOccupationLogement.setProprietaireAvecEmprunt(false);
	return statutOccupationLogement;
    }

}
