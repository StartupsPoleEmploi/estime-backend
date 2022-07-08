package fr.poleemploi.estime.clientsexternes.openfisca.mappeur;

import static fr.poleemploi.estime.clientsexternes.openfisca.mappeur.ParametresOpenFisca.DEMANDEUR;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fr.poleemploi.estime.clientsexternes.openfisca.ressources.OpenFiscaMenage;
import fr.poleemploi.estime.commun.utile.demandeuremploi.CodeDepartementUtile;
import fr.poleemploi.estime.commun.utile.demandeuremploi.InformationsPersonnellesUtile;
import fr.poleemploi.estime.services.ressources.DemandeurEmploi;
import fr.poleemploi.estime.services.ressources.Logement;

@Component
public class OpenFiscaMappeurMenage {

    @Autowired
    private OpenFiscaMappeurPeriode openFiscaPeriodeMappeur;

    @Autowired
    private InformationsPersonnellesUtile informationsPersonnellesUtile;

    @Autowired
    private CodeDepartementUtile codeDepartementUtile;

    private static final String RESIDENCE_METROPOLE = "metropole";
    private static final String RESIDENCE_DOM = "guadeloupe";

    public OpenFiscaMenage creerMenageOpenFisca(DemandeurEmploi demandeurEmploi, LocalDate dateDebutSimulation) {
	OpenFiscaMenage menageOpenFisca = new OpenFiscaMenage();
	Logement logement = demandeurEmploi.getInformationsPersonnelles().getLogement();

	if (logement != null) {
	    menageOpenFisca.setDepcom(openFiscaPeriodeMappeur.creerPeriodesOpenFisca(logement.getCoordonnees().getCodeInsee(), dateDebutSimulation));
	    menageOpenFisca.setResidenceMayotte(openFiscaPeriodeMappeur.creerPeriodesOpenFisca(logement.getCoordonnees().isDeMayotte(), dateDebutSimulation));
	    String lieuResidence = codeDepartementUtile.isDesDOM(demandeurEmploi.getInformationsPersonnelles().getLogement().getCoordonnees().getCodePostal()) ? RESIDENCE_DOM
		    : RESIDENCE_METROPOLE;
	    menageOpenFisca.setLieuResidence(openFiscaPeriodeMappeur.creerPeriodesOpenFisca(lieuResidence, dateDebutSimulation));
	    menageOpenFisca.setLoyer(openFiscaPeriodeMappeur.creerPeriodesOpenFisca(logement.getMontantLoyer(), dateDebutSimulation));
	    menageOpenFisca.setLogementChambre(openFiscaPeriodeMappeur.creerPeriodesOpenFisca(logement.isChambre(), dateDebutSimulation));
	    menageOpenFisca.setLogementCrous(openFiscaPeriodeMappeur.creerPeriodesOpenFisca(logement.isCrous(), dateDebutSimulation));
	    menageOpenFisca.setLogementConventionne(openFiscaPeriodeMappeur.creerPeriodesOpenFisca(logement.isConventionne(), dateDebutSimulation));
	    menageOpenFisca.setColoc(openFiscaPeriodeMappeur.creerPeriodesOpenFisca(logement.isColloc(), dateDebutSimulation));
	}

	List<String> personneDeReference = new ArrayList<>();
	personneDeReference.add(DEMANDEUR);
	menageOpenFisca.setPersonneDeReference(personneDeReference);
	menageOpenFisca.setStatutOccupationLogement(
		openFiscaPeriodeMappeur.creerPeriodesOpenFisca(informationsPersonnellesUtile.getStatutOccupationLogement(logement), dateDebutSimulation));

	return menageOpenFisca;
    }
}
