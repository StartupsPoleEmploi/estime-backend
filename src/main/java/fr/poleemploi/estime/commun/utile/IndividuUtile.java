package fr.poleemploi.estime.commun.utile;

import org.springframework.stereotype.Component;

import fr.poleemploi.estime.clientsexternes.poleemploiio.ressources.DetailIndemnisationPEIO;
import fr.poleemploi.estime.commun.enumerations.Aides;
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
    public boolean isPopulationAutorisee(DetailIndemnisationPEIO detailIndemnisationPEIO) {
	return !isBeneficiaireARE(detailIndemnisationPEIO)
		&& (isBeneficiaireASS(detailIndemnisationPEIO) || detailIndemnisationPEIO.isBeneficiaireAAH() || detailIndemnisationPEIO.isBeneficiaireRSA());
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
	if (Aides.ALLOCATION_SOLIDARITE_SPECIFIQUE.getCode().equals(detailIndemnisationPEIO.getCodeIndemnisation())) {
	    AllocationASS allocationASS = new AllocationASS();
	    allocationASS.setAllocationJournaliereNet(detailIndemnisationPEIO.getIndemnisationJournalierNet());
	    aidesPoleEmploi.setAllocationASS(allocationASS);
	}
	if (Aides.ALLOCATION_RETOUR_EMPLOI.getCode().equals(detailIndemnisationPEIO.getCodeIndemnisation())) {
	    AllocationARE allocationARE = new AllocationARE();
	    allocationARE.setAllocationJournaliereNet(detailIndemnisationPEIO.getIndemnisationJournalierNet());
	    aidesPoleEmploi.setAllocationARE(allocationARE);
	}
	return aidesPoleEmploi;
    }

    private boolean isBeneficiaireARE(DetailIndemnisationPEIO detailIndemnisationPEIO) {
	return detailIndemnisationPEIO.getCodeIndemnisation() != null && TypePopulation.ARE.getLibelle().equals(detailIndemnisationPEIO.getCodeIndemnisation());
    }

    private boolean isBeneficiaireASS(DetailIndemnisationPEIO detailIndemnisationPEIO) {
	return detailIndemnisationPEIO.getCodeIndemnisation() != null && TypePopulation.ASS.getLibelle().equals(detailIndemnisationPEIO.getCodeIndemnisation());
    }

    public InformationsPersonnelles creerInformationsPersonnelles() {
	InformationsPersonnelles informationsPersonnelles = new InformationsPersonnelles();
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
