package testsunitaires.commun.utile.demandeuremploi;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import fr.poleemploi.estime.commun.utile.demandeuremploi.InformationsPersonnellesUtile;
import fr.poleemploi.estime.services.ressources.Coordonnees;
import fr.poleemploi.estime.services.ressources.DemandeurEmploi;
import fr.poleemploi.estime.services.ressources.InformationsPersonnelles;
import fr.poleemploi.estime.services.ressources.Logement;

@ContextConfiguration
@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
class InformationsPersonnellesUtileTests {

    public static final String CODE_POSTAL_MAYOTTE = "97600";
    public static final String CODE_POSTAL_METROPOLITAIN = "72300";

    @Autowired
    private InformationsPersonnellesUtile informationsPersonnellesUtile;

    @Configuration
    @ComponentScan({ "utile.tests", "fr.poleemploi.estime" })
    public static class SpringConfig {

    }

    @Test
    void isFranceMetropolitaineTest1() {

	//si DE de france metropole
	DemandeurEmploi demandeurEmploi = new DemandeurEmploi();

	InformationsPersonnelles informationsPersonnelles = new InformationsPersonnelles();
	Logement logement = new Logement();
	Coordonnees coordonnees = new Coordonnees();
	coordonnees.setCodePostal(CODE_POSTAL_METROPOLITAIN);
	logement.setCoordonnees(coordonnees);
	informationsPersonnelles.setLogement(logement);
	demandeurEmploi.setInformationsPersonnelles(informationsPersonnelles);

	//lorsque l'on appelle isDeFranceMetropolitaine
	boolean deFranceMetropolitaine = informationsPersonnellesUtile.isDeFranceMetropolitaine(demandeurEmploi);

	//alors le résultat est DE est de france métropole
	assertThat(deFranceMetropolitaine).isTrue();
    }

    @Test
    void isFranceMetropolitaineTest2() {

	//si DE de Mayotte
	DemandeurEmploi demandeurEmploi = new DemandeurEmploi();

	InformationsPersonnelles informationsPersonnelles = new InformationsPersonnelles();
	Logement logement = new Logement();
	Coordonnees coordonnees = new Coordonnees();
	coordonnees.setCodePostal(CODE_POSTAL_MAYOTTE);
	logement.setCoordonnees(coordonnees);
	informationsPersonnelles.setLogement(logement);
	demandeurEmploi.setInformationsPersonnelles(informationsPersonnelles);

	//lorsque l'on appelle isDeFranceMetropolitaine
	boolean deFranceMetropolitaine = informationsPersonnellesUtile.isDeFranceMetropolitaine(demandeurEmploi);

	//alors le résultat est DE n'est pas de france métropole
	assertThat(deFranceMetropolitaine).isFalse();
    }

    @Test
    void isDesDOMTest1() {

	//si DE de Mayotte
	DemandeurEmploi demandeurEmploi = new DemandeurEmploi();

	InformationsPersonnelles informationsPersonnelles = new InformationsPersonnelles();
	Logement logement = new Logement();
	Coordonnees coordonnees = new Coordonnees();
	coordonnees.setCodePostal(CODE_POSTAL_MAYOTTE);
	logement.setCoordonnees(coordonnees);
	informationsPersonnelles.setLogement(logement);
	demandeurEmploi.setInformationsPersonnelles(informationsPersonnelles);

	//lorsque l'on appelle isDesDOM
	boolean desDOM = informationsPersonnellesUtile.isDesDOM(demandeurEmploi);

	//alors le résultat est DE est des DOM
	assertThat(desDOM).isTrue();
    }

    @Test
    void isDesDOMTest2() {

	//si DE de france Metropole
	DemandeurEmploi demandeurEmploi = new DemandeurEmploi();

	InformationsPersonnelles informationsPersonnelles = new InformationsPersonnelles();
	Logement logement = new Logement();
	Coordonnees coordonnees = new Coordonnees();
	coordonnees.setCodePostal(CODE_POSTAL_METROPOLITAIN);
	logement.setCoordonnees(coordonnees);
	informationsPersonnelles.setLogement(logement);
	demandeurEmploi.setInformationsPersonnelles(informationsPersonnelles);

	//lorsque l'on appelle isDesDOM
	boolean desDOM = informationsPersonnellesUtile.isDesDOM(demandeurEmploi);

	//alors le résultat est DE n'est pas des DOM
	assertThat(desDOM).isFalse();
    }

    @Test
    void isDeMayotteTest1() {

	//si DE de Mayotte
	DemandeurEmploi demandeurEmploi = new DemandeurEmploi();

	InformationsPersonnelles informationsPersonnelles = new InformationsPersonnelles();
	Logement logement = new Logement();
	Coordonnees coordonnees = new Coordonnees();
	coordonnees.setCodePostal(CODE_POSTAL_MAYOTTE);
	logement.setCoordonnees(coordonnees);
	informationsPersonnelles.setLogement(logement);
	demandeurEmploi.setInformationsPersonnelles(informationsPersonnelles);

	//lorsque l'on appelle isDesDOM
	boolean deMayotte = informationsPersonnellesUtile.isDeMayotte(demandeurEmploi);

	//alors le résultat est DE est de Mayotte
	assertThat(deMayotte).isTrue();
    }

    @Test
    void isDeMayotteTest2() {

	//si DE de France Metropole
	DemandeurEmploi demandeurEmploi = new DemandeurEmploi();

	InformationsPersonnelles informationsPersonnelles = new InformationsPersonnelles();
	Logement logement = new Logement();
	Coordonnees coordonnees = new Coordonnees();
	coordonnees.setCodePostal(CODE_POSTAL_METROPOLITAIN);
	logement.setCoordonnees(coordonnees);
	informationsPersonnelles.setLogement(logement);
	demandeurEmploi.setInformationsPersonnelles(informationsPersonnelles);

	//lorsque l'on appelle isDeMayotte
	boolean isDeMayotte = informationsPersonnellesUtile.isDeMayotte(demandeurEmploi);

	//alors le résultat est DE n'est pas de Mayotte
	assertThat(isDeMayotte).isFalse();
    }
}
