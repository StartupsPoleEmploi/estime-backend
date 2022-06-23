package testsunitaires.clientsexternes.openfisca.mappeur;

import java.text.ParseException;
import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import fr.poleemploi.estime.commun.enumerations.AideEnum;
import fr.poleemploi.estime.commun.enumerations.OrganismeEnum;
import fr.poleemploi.estime.commun.enumerations.TypeContratTravailEnum;
import fr.poleemploi.estime.services.ressources.Aide;
import fr.poleemploi.estime.services.ressources.AidesCAF;
import fr.poleemploi.estime.services.ressources.AidesFamiliales;
import fr.poleemploi.estime.services.ressources.AidesLogement;
import fr.poleemploi.estime.services.ressources.AidesPoleEmploi;
import fr.poleemploi.estime.services.ressources.AllocationsLogement;
import fr.poleemploi.estime.services.ressources.Coordonnees;
import fr.poleemploi.estime.services.ressources.DemandeurEmploi;
import fr.poleemploi.estime.services.ressources.FuturTravail;
import fr.poleemploi.estime.services.ressources.InformationsPersonnelles;
import fr.poleemploi.estime.services.ressources.Logement;
import fr.poleemploi.estime.services.ressources.Personne;
import fr.poleemploi.estime.services.ressources.Salaire;
import fr.poleemploi.estime.services.ressources.StatutOccupationLogement;
import utile.tests.Utile;

public class Commun {

    @Autowired
    protected Utile utileTests;

    protected DemandeurEmploi createDemandeurEmploiCelibataireSansEnfant(String typePopulation) throws ParseException {
	return createDemandeurEmploi(typePopulation, false, 0);
    }

    protected DemandeurEmploi createDemandeurEmploiCelibataireAvecEnfant(String typePopulation) throws ParseException {
	DemandeurEmploi demandeurEmploi = createDemandeurEmploi(typePopulation, false, 1);
	demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(0).getInformationsPersonnelles().setDateNaissance(utileTests.getDate("22-05-2013"));

	return demandeurEmploi;
    }

    protected DemandeurEmploi createDemandeurEmploiEnCoupleSansEnfant(String typePopulation) throws ParseException {
	return createDemandeurEmploi(typePopulation, true, 0);
    }

    protected DemandeurEmploi createDemandeurEmploiEnCoupleAvecEnfant(String typePopulation) throws ParseException {
	DemandeurEmploi demandeurEmploi = createDemandeurEmploi(typePopulation, true, 1);
	demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(0).getInformationsPersonnelles().setDateNaissance(utileTests.getDate("22-05-2013"));

	return demandeurEmploi;
    }

    protected DemandeurEmploi createDemandeurEmploi(String typePopulation, boolean isEnCouple, int nbEnfant) throws ParseException {
	DemandeurEmploi demandeurEmploi = utileTests.creerBaseDemandeurEmploi(typePopulation, isEnCouple, nbEnfant);

	FuturTravail futurTravail = new FuturTravail();
	futurTravail.setTypeContrat(TypeContratTravailEnum.CDI.name());
	Salaire salaire = new Salaire();
	salaire.setMontantNet(900);
	salaire.setMontantBrut(1165);
	futurTravail.setSalaire(salaire);
	demandeurEmploi.setFuturTravail(futurTravail);
	demandeurEmploi.getFuturTravail().setTypeContrat(TypeContratTravailEnum.CDI.name());
	demandeurEmploi.getFuturTravail().setNombreHeuresTravailleesSemaine(20);
	demandeurEmploi.getFuturTravail().getSalaire().setMontantNet(1245);
	demandeurEmploi.getFuturTravail().getSalaire().setMontantBrut(1600);
	demandeurEmploi.getFuturTravail().setDistanceKmDomicileTravail(80);
	demandeurEmploi.getFuturTravail().setNombreTrajetsDomicileTravail(12);

	InformationsPersonnelles informationsPersonnelles = new InformationsPersonnelles();
	informationsPersonnelles.setDateNaissance(utileTests.getDate("05-07-1986"));
	Logement logement = new Logement();
	logement.setMontantLoyer(500f);
	logement.setMontantCharges(50f);
	Coordonnees coordonnees = new Coordonnees();
	coordonnees.setCodePostal("44000");
	coordonnees.setCodeInsee("44109");
	coordonnees.setDeMayotte(false);
	logement.setCoordonnees(coordonnees);
	StatutOccupationLogement statutOccupationLogement = new StatutOccupationLogement();
	statutOccupationLogement.setLocataireNonMeuble(true);
	logement.setStatutOccupationLogement(statutOccupationLogement);
	informationsPersonnelles.setLogement(logement);
	demandeurEmploi.setInformationsPersonnelles(informationsPersonnelles);

	return demandeurEmploi;
    }

    protected Aide getAideeAAH(float montant) {
	Aide aideAgepi = new Aide();
	aideAgepi.setCode(AideEnum.ALLOCATION_ADULTES_HANDICAPES.getCode());
	aideAgepi.setNom(AideEnum.ALLOCATION_ADULTES_HANDICAPES.getNom());
	aideAgepi.setOrganisme(OrganismeEnum.CAF.getNomCourt());
	aideAgepi.setMontant(montant);
	aideAgepi.setReportee(false);
	return aideAgepi;
    }

    protected Aide getAideeASS(float montant) {
	Aide aideAgepi = new Aide();
	aideAgepi.setCode(AideEnum.ALLOCATION_SOLIDARITE_SPECIFIQUE.getCode());
	aideAgepi.setNom(AideEnum.ALLOCATION_SOLIDARITE_SPECIFIQUE.getNom());
	aideAgepi.setOrganisme(OrganismeEnum.PE.getNomCourt());
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

    protected AidesPoleEmploi createAidesPE() {
	AidesPoleEmploi aidesPE = new AidesPoleEmploi();
	return aidesPE;
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
