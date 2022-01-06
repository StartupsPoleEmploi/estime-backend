package testsunitaires.clientsexternes.openfisca.mappeur;

import java.text.ParseException;
import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import fr.poleemploi.estime.commun.enumerations.AideEnum;
import fr.poleemploi.estime.commun.enumerations.NationaliteEnum;
import fr.poleemploi.estime.commun.enumerations.OrganismeEnum;
import fr.poleemploi.estime.commun.enumerations.TypeContratTravailEnum;
import fr.poleemploi.estime.commun.enumerations.TypePopulationEnum;
import fr.poleemploi.estime.services.ressources.Aide;
import fr.poleemploi.estime.services.ressources.AidesCAF;
import fr.poleemploi.estime.services.ressources.AidesFamiliales;
import fr.poleemploi.estime.services.ressources.AidesLogement;
import fr.poleemploi.estime.services.ressources.AllocationsLogement;
import fr.poleemploi.estime.services.ressources.DemandeurEmploi;
import fr.poleemploi.estime.services.ressources.InformationsPersonnelles;
import fr.poleemploi.estime.services.ressources.Personne;
import utile.tests.Utile;

public class Commun {

    @Autowired
    protected Utile utileTests;

    protected DemandeurEmploi createDemandeurEmploi() throws ParseException {
	boolean isEnCouple = false;
	int nbEnfant = 1;
	DemandeurEmploi demandeurEmploi = utileTests.creerBaseDemandeurEmploi(TypePopulationEnum.ASS.getLibelle(), isEnCouple, nbEnfant);
	demandeurEmploi.getInformationsPersonnelles().setDateNaissance(utileTests.getDate("05-07-1986"));
	demandeurEmploi.getInformationsPersonnelles().setNationalite(NationaliteEnum.FRANCAISE.getValeur());
	demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(0).getInformationsPersonnelles().setDateNaissance(utileTests.getDateNaissanceFromAge(9));
	demandeurEmploi.getFuturTravail().setTypeContrat(TypeContratTravailEnum.CDI.name());
	demandeurEmploi.getFuturTravail().setNombreHeuresTravailleesSemaine(20);
	demandeurEmploi.getFuturTravail().getSalaire().setMontantNet(1245);
	demandeurEmploi.getFuturTravail().getSalaire().setMontantBrut(1600);
	demandeurEmploi.getFuturTravail().setDistanceKmDomicileTravail(80);
	demandeurEmploi.getFuturTravail().setNombreTrajetsDomicileTravail(12);
	demandeurEmploi.getRessourcesFinancieres().setHasTravailleAuCoursDerniersMois(false);
	demandeurEmploi.getRessourcesFinancieres().getAidesPoleEmploi().getAllocationASS().setAllocationJournaliereNet(16.89f);
	demandeurEmploi.getRessourcesFinancieres().getAidesPoleEmploi().getAllocationASS().setDateDerniereOuvertureDroit(utileTests.getDate("14-04-2020"));

	return demandeurEmploi;
    }

    protected Aide getAideeAAH(float montant) {
	Aide aideAgepi = new Aide();
	aideAgepi.setCode(AideEnum.ALLOCATION_ADULTES_HANDICAPES.getCode());
	aideAgepi.setNom(AideEnum.ALLOCATION_ADULTES_HANDICAPES.getNom());
	aideAgepi.setOrganisme(OrganismeEnum.CAF.getNom());
	aideAgepi.setMontant(montant);
	aideAgepi.setReportee(false);
	return aideAgepi;
    }

    protected Aide getAideeASS(float montant) {
	Aide aideAgepi = new Aide();
	aideAgepi.setCode(AideEnum.ALLOCATION_SOLIDARITE_SPECIFIQUE.getCode());
	aideAgepi.setNom(AideEnum.ALLOCATION_SOLIDARITE_SPECIFIQUE.getNom());
	aideAgepi.setOrganisme(OrganismeEnum.PE.getNom());
	aideAgepi.setMontant(montant);
	aideAgepi.setReportee(false);
	return aideAgepi;
    }

    protected Personne createPersonne(LocalDate dateNaissance) {
	Personne personne1 = new Personne();
	InformationsPersonnelles informationsPersonnelles = new InformationsPersonnelles();
	informationsPersonnelles.setDateNaissance(dateNaissance);
	personne1.setInformationsPersonnelles(informationsPersonnelles);
	return personne1;
    }

    protected void createPersonne(List<Personne> personnesACharge, LocalDate dateNaissance) {
	Personne personne1 = new Personne();
	InformationsPersonnelles informationsPersonnelles = new InformationsPersonnelles();
	informationsPersonnelles.setDateNaissance(dateNaissance);
	personne1.setInformationsPersonnelles(informationsPersonnelles);
	personnesACharge.add(personne1);
    }

    protected AidesCAF createAidesCAF() {
	AidesCAF aidesCAF = new AidesCAF();
	AidesLogement aidesLogement = createAidesLogement();
	AidesFamiliales aidesFamiliales = createAidesFamiliales();
	aidesCAF.setAidesFamiliales(aidesFamiliales);
	aidesCAF.setAidesLogement(aidesLogement);
	return aidesCAF;
    }

    protected AidesLogement createAidesLogement() {
	AidesLogement aidesLogement = new AidesLogement();
	AllocationsLogement allocationsLogement = new AllocationsLogement();
	aidesLogement.setAidePersonnaliseeLogement(allocationsLogement);
	aidesLogement.setAllocationLogementFamiliale(allocationsLogement);
	aidesLogement.setAllocationLogementSociale(allocationsLogement);
	return aidesLogement;
    }

    protected AidesFamiliales createAidesFamiliales() {
	AidesFamiliales aidesFamiliales = new AidesFamiliales();
	aidesFamiliales.setAllocationsFamiliales(0);
	aidesFamiliales.setComplementFamilial(0);
	aidesFamiliales.setAllocationSoutienFamilial(0);
	aidesFamiliales.setPrestationAccueilJeuneEnfant(0);
	return aidesFamiliales;
    }
}
