package fr.poleemploi.estime.commun.utile;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import fr.poleemploi.estime.clientsexternes.poleemploiio.ressources.UserInfoPEIOOut;
import fr.poleemploi.estime.commun.enumerations.EnvironnementEnum;
import fr.poleemploi.estime.commun.enumerations.TypePopulationEnum;
import fr.poleemploi.estime.services.ressources.AidesCAF;
import fr.poleemploi.estime.services.ressources.AidesCPAM;
import fr.poleemploi.estime.services.ressources.AidesLogement;
import fr.poleemploi.estime.services.ressources.AidesPoleEmploi;
import fr.poleemploi.estime.services.ressources.AllocationASS;
import fr.poleemploi.estime.services.ressources.AllocationsLogement;
import fr.poleemploi.estime.services.ressources.BeneficiaireAides;
import fr.poleemploi.estime.services.ressources.Coordonnees;
import fr.poleemploi.estime.services.ressources.DemandeurEmploi;
import fr.poleemploi.estime.services.ressources.Individu;
import fr.poleemploi.estime.services.ressources.InformationsPersonnelles;
import fr.poleemploi.estime.services.ressources.Logement;
import fr.poleemploi.estime.services.ressources.RessourcesFinancieresAvantSimulation;
import fr.poleemploi.estime.services.ressources.StatutOccupationLogement;

@Component
public class StagingEnvironnementUtile {

    private static final String ID_POLE_EMPLOI_FICTIF = "utilisateur_fictif";

    @Value("${spring.profiles.active}")
    private String environment;

    public void bouchonnerCodeDepartementEtDateNaissance(InformationsPersonnelles informationsPersonnelles) {
	creerBouchonLogement(informationsPersonnelles);
	informationsPersonnelles.setDateNaissance(LocalDate.of(1985, 8, 1));
    }

    public void gererAccesAvecBouchon(Individu individu, UserInfoPEIOOut userInfoPEIO) {
	individu.setIdPoleEmploi(ID_POLE_EMPLOI_FICTIF);
	individu.setPopulationAutorisee(true);
	addInfosIndemnisation(individu, getPopulationDeFictif(userInfoPEIO));
	addInformationsPersonnelles(individu, getEmail(userInfoPEIO));
    }

    public boolean isNotLocalhostEnvironnement() {
	return !environment.equals(EnvironnementEnum.LOCALHOST.getLibelle()) && !environment.equals(EnvironnementEnum.TESTS_INTEGRATION.getLibelle());
    }

    public boolean isStagingEnvironnement() {
	return environment.equals(EnvironnementEnum.LOCALHOST.getLibelle()) || environment.equals(EnvironnementEnum.RECETTE.getLibelle());
    }

    public boolean isUtilisateurFictif(UserInfoPEIOOut userInfo) {
	return isCandidatCaro(userInfo) || isDeFictifPoleemploiio(userInfo);
    }

    public boolean isNotDemandeurFictif(DemandeurEmploi demandeurEmploi) {
	return !ID_POLE_EMPLOI_FICTIF.equalsIgnoreCase(demandeurEmploi.getIdPoleEmploi());
    }

    private void addInfosIndemnisation(Individu individu, String population) {
	switch (population) {
	case "AAH":
	    individu.setBeneficiaireAides(creerBouchonBeneficiaireAides(true, false, false, false));
	    break;
	case "ARE":
	    individu.setBeneficiaireAides(creerBouchonBeneficiaireAides(false, true, false, false));
	    break;
	case "ASS":
	    individu.setBeneficiaireAides(creerBouchonBeneficiaireAides(false, false, true, false));
	    individu.setRessourcesFinancieresAvantSimulation(creerBouchonRessourcesFinancieresASS());
	    break;
	case "RSA":
	    individu.setBeneficiaireAides(creerBouchonBeneficiaireAides(false, false, false, true));
	    break;
	default:
	    individu.setBeneficiaireAides(creerBouchonBeneficiaireAides(false, false, false, false));
	    break;
	}
    }

    private void addInformationsPersonnelles(Individu individu, String email) {
	InformationsPersonnelles informationsPersonnelles = new InformationsPersonnelles();
	informationsPersonnelles.setEmail(email);
	individu.setInformationsPersonnelles(informationsPersonnelles);
    }

    private BeneficiaireAides creerBouchonBeneficiaireAides(boolean beneficiaireAAH, boolean beneficiaireARE, boolean beneficiaireASS, boolean beneficiaireRSA) {
	BeneficiaireAides beneficiaire = new BeneficiaireAides();
	beneficiaire.setBeneficiaireAAH(beneficiaireAAH);
	beneficiaire.setBeneficiaireASS(beneficiaireASS);
	beneficiaire.setBeneficiaireARE(beneficiaireARE);
	beneficiaire.setBeneficiaireRSA(beneficiaireRSA);
	return beneficiaire;
    }

    private RessourcesFinancieresAvantSimulation creerBouchonRessourcesFinancieresASS() {
	RessourcesFinancieresAvantSimulation ressourcesFinancieres = creerBouchonRessourcesFinancieres();
	ressourcesFinancieres.setAidesPoleEmploi(creerBouchonAidesPoleEmploiAvecASS(16.89f));
	return ressourcesFinancieres;
    }

    private AidesPoleEmploi creerBouchonAidesPoleEmploiAvecASS(float montant) {
	AidesPoleEmploi aidesPoleEmploi = new AidesPoleEmploi();
	AllocationASS allocationASS = new AllocationASS();
	allocationASS.setAllocationJournaliereNet(montant);
	aidesPoleEmploi.setAllocationASS(allocationASS);
	return aidesPoleEmploi;
    }

    private RessourcesFinancieresAvantSimulation creerBouchonRessourcesFinancieres() {
	RessourcesFinancieresAvantSimulation ressourcesFinancieres = new RessourcesFinancieresAvantSimulation();
	ressourcesFinancieres.setNombreMoisTravaillesDerniersMois(0);
	creerBouchonAllocationCAF(ressourcesFinancieres);
	creerBouchonAllocationCPAM(ressourcesFinancieres);
	return ressourcesFinancieres;
    }

    private void creerBouchonAllocationCAF(RessourcesFinancieresAvantSimulation ressourcesFinancieres) {
	AidesCAF aidesCAF = new AidesCAF();
	aidesCAF.setAidesLogement(creerBouchonAidesLogement());
	ressourcesFinancieres.setAidesCAF(aidesCAF);
    }

    private AidesLogement creerBouchonAidesLogement() {
	AidesLogement aidesLogement = new AidesLogement();
	aidesLogement.setAidePersonnaliseeLogement(creerBouchonAllocationsLogement());
	aidesLogement.setAllocationLogementFamiliale(creerBouchonAllocationsLogement());
	aidesLogement.setAllocationLogementSociale(creerBouchonAllocationsLogement());
	return aidesLogement;
    }

    private AllocationsLogement creerBouchonAllocationsLogement() {
	AllocationsLogement allocationsLogement = new AllocationsLogement();
	allocationsLogement.setMoisNMoins1(0);
	allocationsLogement.setMoisNMoins2(0);
	allocationsLogement.setMoisNMoins3(0);
	return allocationsLogement;
    }

    private void creerBouchonAllocationCPAM(RessourcesFinancieresAvantSimulation ressourcesFinancieres) {
	AidesCPAM aidesCPAM = new AidesCPAM();
	aidesCPAM.setAllocationSupplementaireInvalidite(0f);
	ressourcesFinancieres.setAidesCPAM(aidesCPAM);
    }

    private void creerBouchonLogement(InformationsPersonnelles informationsPersonnelles) {
	Logement logement = new Logement();
	logement.setChambre(false);
	logement.setConventionne(false);
	logement.setColloc(false);
	logement.setCrous(false);
	logement.setMontantCharges(null);
	logement.setMontantLoyer(null);
	logement.setCoordonnees(creerBouchonCoordonnees());
	logement.setStatutOccupationLogement(creerBouchonStatutOccupationLogement());
	informationsPersonnelles.setLogement(logement);
    }

    private StatutOccupationLogement creerBouchonStatutOccupationLogement() {
	StatutOccupationLogement statutOccupationLogement = new StatutOccupationLogement();
	statutOccupationLogement.setLocataireHLM(false);
	statutOccupationLogement.setLocataireMeuble(false);
	statutOccupationLogement.setLocataireNonMeuble(false);
	statutOccupationLogement.setLogeGratuitement(false);
	statutOccupationLogement.setProprietaire(false);
	statutOccupationLogement.setProprietaireAvecEmprunt(false);
	return statutOccupationLogement;
    }

    private Coordonnees creerBouchonCoordonnees() {
	Coordonnees coordonnees = new Coordonnees();
	coordonnees.setCodeInsee("");
	coordonnees.setCodePostal("44000");
	coordonnees.setDeMayotte(false);
	coordonnees.setDesDOM(false);
	return coordonnees;
    }

    private String getPopulationDeFictif(UserInfoPEIOOut userInfo) {
	if (isCandidatCaro(userInfo)) {
	    return userInfo.getGivenName().substring(userInfo.getGivenName().length() - 3);
	} else {
	    return TypePopulationEnum.ARE.getLibelle();
	}
    }

    /**
     * Les candidats "Caro" sont les candidats que l'on a créés en production 
     * afin de pouvoir tester l'application en étant connecté au api poleemploi.io de production.
     * 
     * Si on identifie un candidat "Caro", on bouchonne ses données d'indemnisation en fonction de son suffixe (ASS,AAH, RSA) afin de le laisser 
     * accéder à l'application.
     * @return
     */
    private boolean isCandidatCaro(UserInfoPEIOOut userInfo) {
	String givenName = userInfo.getGivenName();
	return givenName != null && (givenName.equalsIgnoreCase("caroass") || givenName.equalsIgnoreCase("julietteaah") || givenName.equalsIgnoreCase("carorsa"));
    }

    private boolean isDeFictifPoleemploiio(UserInfoPEIOOut userInfo) {
	return userInfo != null && userInfo.getEmail() != null && userInfo.getEmail().equalsIgnoreCase("emploistoredev@gmail.com");
    }

    private String getEmail(UserInfoPEIOOut userInfoPEIO) {
	if (userInfoPEIO.getEmail() != null) {
	    return userInfoPEIO.getEmail();
	} else {
	    return StringUtile.EMPTY;
	}
    }
}