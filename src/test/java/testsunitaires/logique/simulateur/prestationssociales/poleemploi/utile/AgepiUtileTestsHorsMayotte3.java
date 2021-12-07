//package testsunitaires.logique.simulateur.prestationssociales.poleemploi.utile;
//
//import static org.assertj.core.api.Assertions.assertThat;
//
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.context.annotation.ComponentScan;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.test.context.ContextConfiguration;
//import org.springframework.test.context.TestPropertySource;
//
//import fr.poleemploi.estime.commun.enumerations.TypePopulation;
//import fr.poleemploi.estime.commun.enumerations.TypesContratTravail;
//import fr.poleemploi.estime.logique.simulateur.aides.poleemploi.utile.AgepiUtile;
//import fr.poleemploi.estime.services.ressources.Aide;
//import fr.poleemploi.estime.services.ressources.DemandeurEmploi;
//import utile.tests.Utile;
//
//@ContextConfiguration
//@SpringBootTest
//@TestPropertySource(locations = "classpath:application-test.properties")
//class AgepiUtileTestsHorsMayotte3 {
//
//    public static final String CODE_POSTAL_MAYOTTE = "97600";
//    public static final String CODE_POSTAL_METROPOLITAIN = "72300";
//
//    @Autowired
//    private AgepiUtile agepiUtile;
//
//    @Autowired
//    Utile utile;
//
//    @Configuration
//    @ComponentScan({ "utile.tests", "fr.poleemploi.estime" })
//    public static class SpringConfig {
//
//    }
//
//    /************* département hors Mayotte *********/
//
//    @Test
//    void calculerMontantAgepiTest10() {
//
//	//Si DE France métropolitaine, futur contrat CDI avec 15h/semaine, 1 enfant à charge de 12ans
//	boolean isEnCouple = false;
//	int nbEnfant = 1;
//	DemandeurEmploi demandeurEmploi = utile.creerBaseDemandeurEmploi(TypePopulation.ARE.getLibelle(), isEnCouple, nbEnfant);
//	demandeurEmploi.getFuturTravail().setTypeContrat(TypesContratTravail.CDI.name());
//	demandeurEmploi.getFuturTravail().setNombreHeuresTravailleesSemaine(15);
//	demandeurEmploi.getInformationsPersonnelles().setCodePostal(CODE_POSTAL_METROPOLITAIN);
//	demandeurEmploi.getRessourcesFinancieres().getAidesPoleEmploi().getAllocationARE().setAllocationJournaliereNet(30.28f);
//	demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(0).getInformationsPersonnelles().setDateNaissance(utile.getDateNaissanceFromAge(12));
//
//	//Lorsque l'on calcul le montant de l'agepi
//	Aide agepi = agepiUtile.simulerAide(demandeurEmploi);
//
//	//alors le montant retourné est de 0€
//	assertThat(agepi.getMontant()).isZero();
//    }
//
//    /***  dureeTravailEnHeuresSemaine 20h ****/
//
//    @Test
//    void calculerMontantAgepiTest11() {
//
//	//Si DE France métropolitaine, futur contrat CDI avec 20h/semaine, 1 enfant à charge de 9ans
//	boolean isEnCouple = false;
//	int nbEnfant = 1;
//	DemandeurEmploi demandeurEmploi = utile.creerBaseDemandeurEmploi(TypePopulation.ARE.getLibelle(), isEnCouple, nbEnfant);
//	demandeurEmploi.getFuturTravail().setTypeContrat(TypesContratTravail.CDI.name());
//	demandeurEmploi.getFuturTravail().setNombreHeuresTravailleesSemaine(20);
//	demandeurEmploi.getInformationsPersonnelles().setCodePostal(CODE_POSTAL_METROPOLITAIN);
//	demandeurEmploi.getRessourcesFinancieres().getAidesPoleEmploi().getAllocationARE().setAllocationJournaliereNet(30.28f);
//	demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(0).getInformationsPersonnelles().setDateNaissance(utile.getDateNaissanceFromAge(9));
//
//	//Lorsque l'on calcul le montant de l'agepi
//	Aide agepi = agepiUtile.simulerAide(demandeurEmploi);
//
//	//alors le montant retourné est de 400€
//	assertThat(agepi.getMontant()).isEqualTo(400);
//    }
//
//    @Test
//    void calculerMontantAgepiTest12() {
//
//	//Si DE France métropolitaine, futur contrat CDI avec 20h/semaine, 2 enfants à charge de 9ans et 8ans
//	boolean isEnCouple = false;
//	int nbEnfant = 2;
//	DemandeurEmploi demandeurEmploi = utile.creerBaseDemandeurEmploi(TypePopulation.ARE.getLibelle(), isEnCouple, nbEnfant);
//	demandeurEmploi.getFuturTravail().setTypeContrat(TypesContratTravail.CDI.name());
//	demandeurEmploi.getFuturTravail().setNombreHeuresTravailleesSemaine(20);
//	demandeurEmploi.getInformationsPersonnelles().setCodePostal(CODE_POSTAL_METROPOLITAIN);
//	demandeurEmploi.getRessourcesFinancieres().getAidesPoleEmploi().getAllocationARE().setAllocationJournaliereNet(30.28f);
//	demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(0).getInformationsPersonnelles().setDateNaissance(utile.getDateNaissanceFromAge(9));
//	demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(1).getInformationsPersonnelles().setDateNaissance(utile.getDateNaissanceFromAge(8));
//
//	//Lorsque l'on calcul le montant de l'agepi
//	Aide agepi = agepiUtile.simulerAide(demandeurEmploi);
//
//	//alors le montant retourné est de 460€
//	assertThat(agepi.getMontant()).isEqualTo(460);
//    }
//
//    @Test
//    void calculerMontantAgepiTest13() {
//
//	//Si DE France métropolitaine, futur contrat CDI avec 20h/semaine, 3 enfants à charge de 9ans, 8ans et 8ans
//	boolean isEnCouple = false;
//	int nbEnfant = 3;
//	DemandeurEmploi demandeurEmploi = utile.creerBaseDemandeurEmploi(TypePopulation.ARE.getLibelle(), isEnCouple, nbEnfant);
//	demandeurEmploi.getFuturTravail().setTypeContrat(TypesContratTravail.CDI.name());
//	demandeurEmploi.getFuturTravail().setNombreHeuresTravailleesSemaine(20);
//	demandeurEmploi.getInformationsPersonnelles().setCodePostal(CODE_POSTAL_METROPOLITAIN);
//	demandeurEmploi.getRessourcesFinancieres().getAidesPoleEmploi().getAllocationARE().setAllocationJournaliereNet(30.28f);
//	demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(0).getInformationsPersonnelles().setDateNaissance(utile.getDateNaissanceFromAge(9));
//	demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(1).getInformationsPersonnelles().setDateNaissance(utile.getDateNaissanceFromAge(8));
//	demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(2).getInformationsPersonnelles().setDateNaissance(utile.getDateNaissanceFromAge(8));
//
//	//Lorsque l'on calcul le montant de l'agepi
//	Aide agepi = agepiUtile.simulerAide(demandeurEmploi);
//
//	//alors le montant retourné est de 520€
//	assertThat(agepi.getMontant()).isEqualTo(520);
//    }
//
//    @Test
//    void calculerMontantAgepiTest14() {
//
//	//Si DE France métropolitaine, futur contrat CDI avec 20h/semaine, 4 enfants à charge de 9ans, 8ans, 8ans et 5ans
//	boolean isEnCouple = false;
//	int nbEnfant = 4;
//	DemandeurEmploi demandeurEmploi = utile.creerBaseDemandeurEmploi(TypePopulation.ARE.getLibelle(), isEnCouple, nbEnfant);
//	demandeurEmploi.getFuturTravail().setTypeContrat(TypesContratTravail.CDI.name());
//	demandeurEmploi.getFuturTravail().setNombreHeuresTravailleesSemaine(20);
//	demandeurEmploi.getInformationsPersonnelles().setCodePostal(CODE_POSTAL_METROPOLITAIN);
//	demandeurEmploi.getRessourcesFinancieres().getAidesPoleEmploi().getAllocationARE().setAllocationJournaliereNet(30.28f);
//	demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(0).getInformationsPersonnelles().setDateNaissance(utile.getDateNaissanceFromAge(9));
//	demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(1).getInformationsPersonnelles().setDateNaissance(utile.getDateNaissanceFromAge(8));
//	demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(2).getInformationsPersonnelles().setDateNaissance(utile.getDateNaissanceFromAge(8));
//	demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(3).getInformationsPersonnelles().setDateNaissance(utile.getDateNaissanceFromAge(5));
//
//	//Lorsque l'on calcul le montant de l'agepi
//	Aide agepi = agepiUtile.simulerAide(demandeurEmploi);
//
//	//alors le montant retourné est de 520€
//	assertThat(agepi.getMontant()).isEqualTo(520);
//    }
//
//    @Test
//    void calculerMontantAgepiTest15() {
//
//	//Si DE France métropolitaine, futur contrat CDI avec 20h/semaine, pas d'enfants à charge
//	boolean isEnCouple = false;
//	int nbEnfant = 1;
//	DemandeurEmploi demandeurEmploi = utile.creerBaseDemandeurEmploi(TypePopulation.ARE.getLibelle(), isEnCouple, nbEnfant);
//	demandeurEmploi.getFuturTravail().setTypeContrat(TypesContratTravail.CDI.name());
//	demandeurEmploi.getFuturTravail().setNombreHeuresTravailleesSemaine(20);
//	demandeurEmploi.getInformationsPersonnelles().setCodePostal(CODE_POSTAL_METROPOLITAIN);
//	demandeurEmploi.getRessourcesFinancieres().getAidesPoleEmploi().getAllocationARE().setAllocationJournaliereNet(30.28f);
//	demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(0).getInformationsPersonnelles().setDateNaissance(utile.getDateNaissanceFromAge(13));
//
//	//Lorsque l'on calcul le montant de l'agepi
//	Aide agepi = agepiUtile.simulerAide(demandeurEmploi);
//
//	//alors le montant retourné est de 0€
//	assertThat(agepi.getMontant()).isZero();
//    }
//
//    /***  dureeTravailEnHeuresSemaine = 35h ****/
//
//    @Test
//    void calculerMontantAgepiTest16() {
//
//	//Si DE France métropolitaine, futur contrat CDI avec 35h/semaine, 1 enfant à charge de 9ans
//	boolean isEnCouple = false;
//	int nbEnfant = 1;
//	DemandeurEmploi demandeurEmploi = utile.creerBaseDemandeurEmploi(TypePopulation.ARE.getLibelle(), isEnCouple, nbEnfant);
//	demandeurEmploi.getFuturTravail().setTypeContrat(TypesContratTravail.CDI.name());
//	demandeurEmploi.getFuturTravail().setNombreHeuresTravailleesSemaine(35);
//	demandeurEmploi.getInformationsPersonnelles().setCodePostal(CODE_POSTAL_METROPOLITAIN);
//	demandeurEmploi.getRessourcesFinancieres().getAidesPoleEmploi().getAllocationARE().setAllocationJournaliereNet(30.28f);
//	demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(0).getInformationsPersonnelles().setDateNaissance(utile.getDateNaissanceFromAge(9));
//
//	//Lorsque l'on calcul le montant de l'agepi
//	Aide agepi = agepiUtile.simulerAide(demandeurEmploi);
//
//	//alors le montant retourné est de 400€
//	assertThat(agepi.getMontant()).isEqualTo(400);
//    }
//
//    @Test
//    void calculerMontantAgepiTest17() {
//
//	//Si DE France métropolitaine, futur contrat CDI avec 35h/semaine, 2 enfants à charge de 9ans et 8ans
//	boolean isEnCouple = false;
//	int nbEnfant = 2;
//	DemandeurEmploi demandeurEmploi = utile.creerBaseDemandeurEmploi(TypePopulation.ARE.getLibelle(), isEnCouple, nbEnfant);
//	demandeurEmploi.getFuturTravail().setTypeContrat(TypesContratTravail.CDI.name());
//	demandeurEmploi.getFuturTravail().setNombreHeuresTravailleesSemaine(35);
//	demandeurEmploi.getInformationsPersonnelles().setCodePostal(CODE_POSTAL_METROPOLITAIN);
//	demandeurEmploi.getRessourcesFinancieres().getAidesPoleEmploi().getAllocationARE().setAllocationJournaliereNet(30.28f);
//	demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(0).getInformationsPersonnelles().setDateNaissance(utile.getDateNaissanceFromAge(9));
//	demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(1).getInformationsPersonnelles().setDateNaissance(utile.getDateNaissanceFromAge(8));
//
//	//Lorsque l'on calcul le montant de l'agepi
//	Aide agepi = agepiUtile.simulerAide(demandeurEmploi);
//
//	//alors le montant retourné est de 460€
//	assertThat(agepi.getMontant()).isEqualTo(460);
//    }
//
//    @Test
//    void calculerMontantAgepiTest18() {
//
//	//Si DE France métropolitaine, futur contrat CDI avec 35h/semaine, 3 enfants à charge de 9ans, 8ans et 8ans
//	boolean isEnCouple = false;
//	int nbEnfant = 3;
//	DemandeurEmploi demandeurEmploi = utile.creerBaseDemandeurEmploi(TypePopulation.ARE.getLibelle(), isEnCouple, nbEnfant);
//	demandeurEmploi.getFuturTravail().setTypeContrat(TypesContratTravail.CDI.name());
//	demandeurEmploi.getFuturTravail().setNombreHeuresTravailleesSemaine(35);
//	demandeurEmploi.getInformationsPersonnelles().setCodePostal(CODE_POSTAL_METROPOLITAIN);
//	demandeurEmploi.getRessourcesFinancieres().getAidesPoleEmploi().getAllocationARE().setAllocationJournaliereNet(30.28f);
//	demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(0).getInformationsPersonnelles().setDateNaissance(utile.getDateNaissanceFromAge(9));
//	demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(1).getInformationsPersonnelles().setDateNaissance(utile.getDateNaissanceFromAge(8));
//	demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(2).getInformationsPersonnelles().setDateNaissance(utile.getDateNaissanceFromAge(8));
//
//	//Lorsque l'on calcul le montant de l'agepi
//	Aide agepi = agepiUtile.simulerAide(demandeurEmploi);
//
//	//alors le montant retourné est de 520€
//	assertThat(agepi.getMontant()).isEqualTo(520);
//    }
//
//    @Test
//    void calculerMontantAgepiTest19() {
//
//	//Si DE France métropolitaine, futur contrat CDI avec 35h/semaine, 4 enfants à charge de 9ans, 8ans, 8ans et 5ans
//	boolean isEnCouple = false;
//	int nbEnfant = 4;
//	DemandeurEmploi demandeurEmploi = utile.creerBaseDemandeurEmploi(TypePopulation.ARE.getLibelle(), isEnCouple, nbEnfant);
//	demandeurEmploi.getFuturTravail().setTypeContrat(TypesContratTravail.CDI.name());
//	demandeurEmploi.getFuturTravail().setNombreHeuresTravailleesSemaine(35);
//	demandeurEmploi.getInformationsPersonnelles().setCodePostal(CODE_POSTAL_METROPOLITAIN);
//	demandeurEmploi.getRessourcesFinancieres().getAidesPoleEmploi().getAllocationARE().setAllocationJournaliereNet(30.28f);
//	demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(0).getInformationsPersonnelles().setDateNaissance(utile.getDateNaissanceFromAge(9));
//	demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(1).getInformationsPersonnelles().setDateNaissance(utile.getDateNaissanceFromAge(8));
//	demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(2).getInformationsPersonnelles().setDateNaissance(utile.getDateNaissanceFromAge(8));
//	demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(3).getInformationsPersonnelles().setDateNaissance(utile.getDateNaissanceFromAge(5));
//
//	//Lorsque l'on calcul le montant de l'agepi
//	Aide agepi = agepiUtile.simulerAide(demandeurEmploi);
//
//	//alors le montant retourné est de 520€
//	assertThat(agepi.getMontant()).isEqualTo(520);
//    }
//
//    @Test
//    void calculerMontantAgepiTest20() {
//
//	//Si DE France métropolitaine, futur contrat CDI avec 35h/semaine, 1 enfant à charge de 10ans
//	boolean isEnCouple = false;
//	int nbEnfant = 1;
//	DemandeurEmploi demandeurEmploi = utile.creerBaseDemandeurEmploi(TypePopulation.ARE.getLibelle(), isEnCouple, nbEnfant);
//	demandeurEmploi.getFuturTravail().setTypeContrat(TypesContratTravail.CDI.name());
//	demandeurEmploi.getFuturTravail().setNombreHeuresTravailleesSemaine(35);
//	demandeurEmploi.getInformationsPersonnelles().setCodePostal(CODE_POSTAL_METROPOLITAIN);
//	demandeurEmploi.getRessourcesFinancieres().getAidesPoleEmploi().getAllocationARE().setAllocationJournaliereNet(30.28f);
//	demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(0).getInformationsPersonnelles().setDateNaissance(utile.getDateNaissanceFromAge(10));
//
//	//Lorsque l'on calcul le montant de l'agepi
//	Aide agepi = agepiUtile.simulerAide(demandeurEmploi);
//
//	//alors le montant retourné est de 0€
//	assertThat(agepi.getMontant()).isZero();
//    }
//
//    /***  dureeTravailEnHeuresSemaine 40h ****/
//
//    @Test
//    void calculerMontantAgepiTest21() {
//
//	//Si DE France métropolitaine, futur contrat CDI avec 40h/semaine, 1 enfant à charge de 9ans
//	boolean isEnCouple = false;
//	int nbEnfant = 1;
//	DemandeurEmploi demandeurEmploi = utile.creerBaseDemandeurEmploi(TypePopulation.ARE.getLibelle(), isEnCouple, nbEnfant);
//	demandeurEmploi.getFuturTravail().setTypeContrat(TypesContratTravail.CDI.name());
//	demandeurEmploi.getFuturTravail().setNombreHeuresTravailleesSemaine(40);
//	demandeurEmploi.getInformationsPersonnelles().setCodePostal(CODE_POSTAL_METROPOLITAIN);
//	demandeurEmploi.getRessourcesFinancieres().getAidesPoleEmploi().getAllocationARE().setAllocationJournaliereNet(30.28f);
//	demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(0).getInformationsPersonnelles().setDateNaissance(utile.getDateNaissanceFromAge(9));
//
//	//Lorsque l'on calcul le montant de l'agepi
//	Aide agepi = agepiUtile.simulerAide(demandeurEmploi);
//
//	//alors le montant retourné est de 400€
//	assertThat(agepi.getMontant()).isEqualTo(400);
//    }
//
//    @Test
//    void calculerMontantAgepiTest22() {
//
//	//Si DE France métropolitaine, futur contrat CDI avec 40h/semaine, 2 enfants à charge de 9ans et 8ans
//	boolean isEnCouple = false;
//	int nbEnfant = 2;
//	DemandeurEmploi demandeurEmploi = utile.creerBaseDemandeurEmploi(TypePopulation.ARE.getLibelle(), isEnCouple, nbEnfant);
//	demandeurEmploi.getFuturTravail().setTypeContrat(TypesContratTravail.CDI.name());
//	demandeurEmploi.getFuturTravail().setNombreHeuresTravailleesSemaine(40);
//	demandeurEmploi.getInformationsPersonnelles().setCodePostal(CODE_POSTAL_METROPOLITAIN);
//	demandeurEmploi.getRessourcesFinancieres().getAidesPoleEmploi().getAllocationARE().setAllocationJournaliereNet(30.28f);
//	demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(0).getInformationsPersonnelles().setDateNaissance(utile.getDateNaissanceFromAge(9));
//	demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(1).getInformationsPersonnelles().setDateNaissance(utile.getDateNaissanceFromAge(8));
//
//	//Lorsque l'on calcul le montant de l'agepi
//	Aide agepi = agepiUtile.simulerAide(demandeurEmploi);
//
//	//alors le montant retourné est de 460€
//	assertThat(agepi.getMontant()).isEqualTo(460);
//    }
//
//    @Test
//    void calculerMontantAgepiTest23() {
//
//	//Si DE France métropolitaine, futur contrat CDI avec 40h/semaine, 3 enfants à charge de 9ans, 8ans et 8ans
//	boolean isEnCouple = false;
//	int nbEnfant = 3;
//	DemandeurEmploi demandeurEmploi = utile.creerBaseDemandeurEmploi(TypePopulation.ARE.getLibelle(), isEnCouple, nbEnfant);
//	demandeurEmploi.getFuturTravail().setTypeContrat(TypesContratTravail.CDI.name());
//	demandeurEmploi.getFuturTravail().setNombreHeuresTravailleesSemaine(40);
//	demandeurEmploi.getInformationsPersonnelles().setCodePostal(CODE_POSTAL_METROPOLITAIN);
//	demandeurEmploi.getRessourcesFinancieres().getAidesPoleEmploi().getAllocationARE().setAllocationJournaliereNet(30.28f);
//	demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(0).getInformationsPersonnelles().setDateNaissance(utile.getDateNaissanceFromAge(9));
//	demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(1).getInformationsPersonnelles().setDateNaissance(utile.getDateNaissanceFromAge(8));
//	demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(2).getInformationsPersonnelles().setDateNaissance(utile.getDateNaissanceFromAge(8));
//
//	//Lorsque l'on calcul le montant de l'agepi
//	Aide agepi = agepiUtile.simulerAide(demandeurEmploi);
//
//	//alors le montant retourné est de 520€
//	assertThat(agepi.getMontant()).isEqualTo(520);
//    }
//
//    @Test
//    void calculerMontantAgepiTest24() {
//
//	//Si DE France métropolitaine, futur contrat CDI avec 40h/semaine, 4 enfants à charge de 9ans, 8ans, 5ans et 8ans
//	boolean isEnCouple = false;
//	int nbEnfant = 4;
//	DemandeurEmploi demandeurEmploi = utile.creerBaseDemandeurEmploi(TypePopulation.ARE.getLibelle(), isEnCouple, nbEnfant);
//	demandeurEmploi.getFuturTravail().setTypeContrat(TypesContratTravail.CDI.name());
//	demandeurEmploi.getFuturTravail().setNombreHeuresTravailleesSemaine(40);
//	demandeurEmploi.getInformationsPersonnelles().setCodePostal(CODE_POSTAL_METROPOLITAIN);
//	demandeurEmploi.getRessourcesFinancieres().getAidesPoleEmploi().getAllocationARE().setAllocationJournaliereNet(30.28f);
//	demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(0).getInformationsPersonnelles().setDateNaissance(utile.getDateNaissanceFromAge(9));
//	demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(1).getInformationsPersonnelles().setDateNaissance(utile.getDateNaissanceFromAge(8));
//	demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(2).getInformationsPersonnelles().setDateNaissance(utile.getDateNaissanceFromAge(8));
//	demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(3).getInformationsPersonnelles().setDateNaissance(utile.getDateNaissanceFromAge(5));
//
//	//Lorsque l'on calcul le montant de l'agepi
//	Aide agepi = agepiUtile.simulerAide(demandeurEmploi);
//
//	//alors le montant retourné est de 520€
//	assertThat(agepi.getMontant()).isEqualTo(520);
//    }
//
//    @Test
//    void calculerMontantAgepiTest25() {
//
//	//Si DE France métropolitaine, futur contrat CDI avec 40h/semaine, pas d'enfants à charge
//	boolean isEnCouple = false;
//	int nbEnfant = 1;
//	DemandeurEmploi demandeurEmploi = utile.creerBaseDemandeurEmploi(TypePopulation.ARE.getLibelle(), isEnCouple, nbEnfant);
//	demandeurEmploi.getFuturTravail().setTypeContrat(TypesContratTravail.CDI.name());
//	demandeurEmploi.getFuturTravail().setNombreHeuresTravailleesSemaine(40);
//	demandeurEmploi.getInformationsPersonnelles().setCodePostal(CODE_POSTAL_METROPOLITAIN);
//	demandeurEmploi.getRessourcesFinancieres().getAidesPoleEmploi().getAllocationARE().setAllocationJournaliereNet(30.28f);
//	demandeurEmploi.getSituationFamiliale().getPersonnesACharge().get(0).getInformationsPersonnelles().setDateNaissance(utile.getDateNaissanceFromAge(16));
//
//	//Lorsque l'on calcul le montant de l'agepi
//	Aide agepi = agepiUtile.simulerAide(demandeurEmploi);
//
//	//alors le montant retourné est de 0€
//	assertThat(agepi.getMontant()).isZero();
//    }
//}
