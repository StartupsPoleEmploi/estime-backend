package fr.poleemploi.estime.clientsexternes.openfisca.mappeur;

import static fr.poleemploi.estime.clientsexternes.openfisca.mappeur.ParametresOpenFisca.DEMANDEUR;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fr.poleemploi.estime.clientsexternes.openfisca.ressources.OpenFiscaMenage;
import fr.poleemploi.estime.commun.utile.demandeuremploi.InformationsPersonnellesUtile;
import fr.poleemploi.estime.services.ressources.DemandeurEmploi;
import fr.poleemploi.estime.services.ressources.Logement;

@Component
public class OpenFiscaMappeurMenage {

    @Autowired
    private OpenFiscaMappeurPeriode openFiscaPeriodeMappeur;

    @Autowired
    private InformationsPersonnellesUtile informationsPersonnellesUtile;

    public OpenFiscaMenage creerMenageOpenFisca(DemandeurEmploi demandeurEmploi, LocalDate dateDebutSimulation, int numeroMoisSimule) {
	OpenFiscaMenage menageOpenFisca = new OpenFiscaMenage();
	Logement logement = demandeurEmploi.getInformationsPersonnelles().getLogement();

	if (logement != null) {
	    menageOpenFisca.setDepcom(openFiscaPeriodeMappeur.creerPeriodesOpenFisca(logement.getCoordonnees().getCodeInsee(), dateDebutSimulation, numeroMoisSimule,
		    OpenFiscaMappeurPeriode.NOMBRE_MOIS_PERIODE_OPENFISCA));
	    menageOpenFisca.setResidenceMayotte(openFiscaPeriodeMappeur.creerPeriodesOpenFisca(logement.getCoordonnees().isDeMayotte(), dateDebutSimulation, numeroMoisSimule,
		    OpenFiscaMappeurPeriode.NOMBRE_MOIS_PERIODE_OPENFISCA));
	    menageOpenFisca.setLoyer(openFiscaPeriodeMappeur.creerPeriodesOpenFisca(logement.getMontantLoyer(), dateDebutSimulation, numeroMoisSimule,
		    OpenFiscaMappeurPeriode.NOMBRE_MOIS_PERIODE_OPENFISCA));
	    menageOpenFisca.setChargesLocatives(openFiscaPeriodeMappeur.creerPeriodesOpenFisca(logement.getMontantCharges(), dateDebutSimulation, numeroMoisSimule,
		    OpenFiscaMappeurPeriode.NOMBRE_MOIS_PERIODE_OPENFISCA));
	    menageOpenFisca.setLogementChambre(openFiscaPeriodeMappeur.creerPeriodesOpenFisca(logement.isChambre(), dateDebutSimulation, numeroMoisSimule,
		    OpenFiscaMappeurPeriode.NOMBRE_MOIS_PERIODE_OPENFISCA));
	    menageOpenFisca.setLogementCrous(openFiscaPeriodeMappeur.creerPeriodesOpenFisca(logement.isCrous(), dateDebutSimulation, numeroMoisSimule,
		    OpenFiscaMappeurPeriode.NOMBRE_MOIS_PERIODE_OPENFISCA));
	    menageOpenFisca.setLogementConventionne(openFiscaPeriodeMappeur.creerPeriodesOpenFisca(logement.isConventionne(), dateDebutSimulation, numeroMoisSimule,
		    OpenFiscaMappeurPeriode.NOMBRE_MOIS_PERIODE_OPENFISCA));
	    menageOpenFisca.setColoc(openFiscaPeriodeMappeur.creerPeriodesOpenFisca(logement.isColloc(), dateDebutSimulation, numeroMoisSimule,
		    OpenFiscaMappeurPeriode.NOMBRE_MOIS_PERIODE_OPENFISCA));
	}

	List<String> personneDeReference = new ArrayList<>();
	personneDeReference.add(DEMANDEUR);
	menageOpenFisca.setPersonneDeReference(personneDeReference);
	menageOpenFisca.setStatutOccupationLogement(openFiscaPeriodeMappeur.creerPeriodesOpenFisca(informationsPersonnellesUtile.getStatutOccupationLogement(logement),
		dateDebutSimulation, numeroMoisSimule, OpenFiscaMappeurPeriode.NOMBRE_MOIS_PERIODE_OPENFISCA));

	return menageOpenFisca;
    }
}
