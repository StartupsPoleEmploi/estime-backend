package fr.poleemploi.estime.clientsexternes.openfisca.mappeur;

import static fr.poleemploi.estime.clientsexternes.openfisca.mappeur.ParametresOpenFisca.CHARGES_LOCATIVES;
import static fr.poleemploi.estime.clientsexternes.openfisca.mappeur.ParametresOpenFisca.COLOC;
import static fr.poleemploi.estime.clientsexternes.openfisca.mappeur.ParametresOpenFisca.DEPCOM;
import static fr.poleemploi.estime.clientsexternes.openfisca.mappeur.ParametresOpenFisca.LOGEMENT_CHAMBRE;
import static fr.poleemploi.estime.clientsexternes.openfisca.mappeur.ParametresOpenFisca.LOGEMENT_CONVENTIONNE;
import static fr.poleemploi.estime.clientsexternes.openfisca.mappeur.ParametresOpenFisca.LOGEMENT_CROUS;
import static fr.poleemploi.estime.clientsexternes.openfisca.mappeur.ParametresOpenFisca.LOYER;
import static fr.poleemploi.estime.clientsexternes.openfisca.mappeur.ParametresOpenFisca.PERSONNE_DE_REFERENCE;
import static fr.poleemploi.estime.clientsexternes.openfisca.mappeur.ParametresOpenFisca.RESIDENCE_MAYOTTE;
import static fr.poleemploi.estime.clientsexternes.openfisca.mappeur.ParametresOpenFisca.STATUT_OCCUPATION_LOGEMENT;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.github.tsohr.JSONArray;
import com.github.tsohr.JSONObject;

import fr.poleemploi.estime.commun.utile.demandeuremploi.InformationsPersonnellesUtile;
import fr.poleemploi.estime.services.ressources.DemandeurEmploi;
import fr.poleemploi.estime.services.ressources.Logement;

@Component
public class OpenFiscaMappeurMenage {

    @Autowired
    private OpenFiscaMappeurPeriode openFiscaPeriodeMappeur;

    @Autowired
    private InformationsPersonnellesUtile informationsPersonnellesUtile;

    public JSONObject creerMenageJSON(DemandeurEmploi demandeurEmploi, LocalDate dateDebutSimulation, int numeroMoisSimule) {
	JSONObject menage = new JSONObject();
	JSONArray personneReferenceJSON = new JSONArray();
	Logement logement = demandeurEmploi.getInformationsPersonnelles().getLogement();
	personneReferenceJSON.put("demandeur");
	menage.put(PERSONNE_DE_REFERENCE, personneReferenceJSON);
	menage.put(STATUT_OCCUPATION_LOGEMENT, openFiscaPeriodeMappeur.creerPeriodes(informationsPersonnellesUtile.getStatutOccupationLogement(logement), dateDebutSimulation, numeroMoisSimule, OpenFiscaMappeurPeriode.NOMBRE_MOIS_PERIODE_OPENFISCA));
	if(logement != null) {
	    menage.put(RESIDENCE_MAYOTTE, openFiscaPeriodeMappeur.creerPeriodes(logement.getCoordonnees().isDeMayotte(), dateDebutSimulation, numeroMoisSimule, OpenFiscaMappeurPeriode.NOMBRE_MOIS_PERIODE_OPENFISCA));
	    menage.put(LOYER, openFiscaPeriodeMappeur.creerPeriodes(logement.getMontantLoyer(), dateDebutSimulation, numeroMoisSimule, OpenFiscaMappeurPeriode.NOMBRE_MOIS_PERIODE_OPENFISCA));
	    menage.put(CHARGES_LOCATIVES, openFiscaPeriodeMappeur.creerPeriodes(logement.getMontantCharges(), dateDebutSimulation, numeroMoisSimule, OpenFiscaMappeurPeriode.NOMBRE_MOIS_PERIODE_OPENFISCA));
	    menage.put(LOGEMENT_CROUS, openFiscaPeriodeMappeur.creerPeriodes(logement.isCrous(), dateDebutSimulation, numeroMoisSimule, OpenFiscaMappeurPeriode.NOMBRE_MOIS_PERIODE_OPENFISCA));
	    menage.put(LOGEMENT_CONVENTIONNE, openFiscaPeriodeMappeur.creerPeriodes(logement.isConventionne(), dateDebutSimulation, numeroMoisSimule, OpenFiscaMappeurPeriode.NOMBRE_MOIS_PERIODE_OPENFISCA));
	    menage.put(DEPCOM, openFiscaPeriodeMappeur.creerPeriodes(logement.getCoordonnees().getCodeInsee(), dateDebutSimulation, numeroMoisSimule, OpenFiscaMappeurPeriode.NOMBRE_MOIS_PERIODE_OPENFISCA));
	    menage.put(COLOC, openFiscaPeriodeMappeur.creerPeriodes(logement.isColloc(), dateDebutSimulation, numeroMoisSimule, OpenFiscaMappeurPeriode.NOMBRE_MOIS_PERIODE_OPENFISCA));
	    menage.put(LOGEMENT_CHAMBRE, openFiscaPeriodeMappeur.creerPeriodes(logement.isChambre(), dateDebutSimulation, numeroMoisSimule, OpenFiscaMappeurPeriode.NOMBRE_MOIS_PERIODE_OPENFISCA));
	}
	return menage;
    }
}
