package testsintegration.simulation.poleemploiio;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import fr.poleemploi.estime.clientsexternes.poleemploiio.PoleEmploiIOClient;
import fr.poleemploi.estime.clientsexternes.poleemploiio.ressources.AgepiPEIOIn;
import fr.poleemploi.estime.clientsexternes.poleemploiio.ressources.AgepiPEIOOut;
import fr.poleemploi.estime.clientsexternes.poleemploiio.ressources.AideMobilitePEIOIn;
import fr.poleemploi.estime.clientsexternes.poleemploiio.ressources.AideMobilitePEIOOut;


@SpringBootTest
@ContextConfiguration
@TestPropertySource(locations = "classpath:application-test.properties")
class InterfacageAPITest {
	
	@Autowired
	private PoleEmploiIOClient poleEmploiIOClient;
	
	private final String accessToken ="Bearer EnDH2mo82nNQBneADYkpK6cMN6s";
	
	 @Configuration
	 @ComponentScan({"utile.tests","fr.poleemploi.estime"})
	 public static class SpringConfig {

	 }
	 
	@Test
    void testInterfacageApiAGEPIValide(){	
		
		AgepiPEIOIn agepiPEIOIn = new AgepiPEIOIn();
    	agepiPEIOIn.setContexte("Reprise");
    	agepiPEIOIn.setDateActionReclassement("2021-11-04");
    	agepiPEIOIn.setDateDepot("2021-11-04");
    	agepiPEIOIn.setDureePeriodeEmploiOuFormation(90);
    	agepiPEIOIn.setEleveSeulEnfants(true);
    	agepiPEIOIn.setIntensite((int) Math.round(50));
    	agepiPEIOIn.setLieuFormationOuEmploi("France");
    	agepiPEIOIn.setNatureContratTravail("CDI");
    	agepiPEIOIn.setNombreEnfants(3);
    	agepiPEIOIn.setNombreEnfantsMoins10Ans(2);
    	agepiPEIOIn.setOrigine("c");
    	agepiPEIOIn.setTypeIntensite("Mensuelle");
		
		AgepiPEIOOut agepiOut = poleEmploiIOClient.callAgepiEndPoint(agepiPEIOIn, this.accessToken).get();
		float montant = agepiOut.getDecisionAGEPIAPI().getMontant();
        assertThat(montant).isPositive();
    }
	
	@Test
    void testInterfacageApiAGEPIInvalide(){		
		AgepiPEIOIn agepiPEIOIn = new AgepiPEIOIn();
    	agepiPEIOIn.setContexte("Reprise");
    	agepiPEIOIn.setDateActionReclassement("2021-11-04");
    	agepiPEIOIn.setDateDepot("2021-11-04");
    	agepiPEIOIn.setDureePeriodeEmploiOuFormation(90);
    	agepiPEIOIn.setEleveSeulEnfants(false);
    	agepiPEIOIn.setIntensite((int) Math.round(50));
    	agepiPEIOIn.setLieuFormationOuEmploi("France");
    	agepiPEIOIn.setNatureContratTravail("CDI");
    	agepiPEIOIn.setNombreEnfants(0);
    	agepiPEIOIn.setNombreEnfantsMoins10Ans(0);
    	agepiPEIOIn.setOrigine("c");
    	agepiPEIOIn.setTypeIntensite("Mensuelle");
	
		AgepiPEIOOut agepiOut = poleEmploiIOClient.callAgepiEndPoint(agepiPEIOIn, this.accessToken).get();
		float montant = agepiOut.getDecisionAGEPIAPI().getMontant();
        assertThat(montant).isZero();
    }
	
	@Test
    void testInterfacageApiAideMobValide(){		
		AideMobilitePEIOIn aideMobilitePEIOIn = new AideMobilitePEIOIn();
    	aideMobilitePEIOIn.setContexte("Reprise");
    	aideMobilitePEIOIn.setDateActionReclassement("2021-10-27");
    	aideMobilitePEIOIn.setDateDepot("2021-10-27");
    	aideMobilitePEIOIn.setDureePeriodeEmploiOuFormation(90);
    	aideMobilitePEIOIn.setNatureContratTravail("CDI");
    	aideMobilitePEIOIn.setOrigine("c");
    	aideMobilitePEIOIn.setCodePostalActionReclassement("28000");
    	aideMobilitePEIOIn.setDistanceDomicileActionReclassement(25000);
    	aideMobilitePEIOIn.setNombreAllersRetours(15);
    	aideMobilitePEIOIn.setNombreRepas(0);
    	aideMobilitePEIOIn.setLieuActionReclassement("France");
    	aideMobilitePEIOIn.setNombreNuitees(0);

    	aideMobilitePEIOIn.setCommuneActionReclassement("Chartres");
    	aideMobilitePEIOIn.setFinancementPEFormation(true);
		AideMobilitePEIOOut aideMobiliteOut = new AideMobilitePEIOOut();
		aideMobiliteOut = poleEmploiIOClient.callAideMobiliteEndPoint(aideMobilitePEIOIn, this.accessToken).get();

        assertThat(aideMobiliteOut.getMontant()).isPositive();
    }
	
	@Test
    void testInterfacageApiAideMobInvalide(){		
		AideMobilitePEIOIn aideMobilitePEIOIn = new AideMobilitePEIOIn();
    	aideMobilitePEIOIn.setContexte("Reprise");
    	aideMobilitePEIOIn.setDateActionReclassement("2021-10-27");
    	aideMobilitePEIOIn.setDateDepot("2021-10-27");
    	aideMobilitePEIOIn.setDureePeriodeEmploiOuFormation(4);
    	aideMobilitePEIOIn.setNatureContratTravail("CDI");
    	aideMobilitePEIOIn.setOrigine("c");
    	aideMobilitePEIOIn.setCodePostalActionReclassement("44000");
    	aideMobilitePEIOIn.setDistanceDomicileActionReclassement(0);
    	aideMobilitePEIOIn.setNombreAllersRetours(0);
    	aideMobilitePEIOIn.setNombreRepas(0);
    	aideMobilitePEIOIn.setLieuActionReclassement("France");
    	aideMobilitePEIOIn.setNombreNuitees(0);

    	aideMobilitePEIOIn.setCommuneActionReclassement("Nantes");
    	aideMobilitePEIOIn.setFinancementPEFormation(false);
		AideMobilitePEIOOut aideMobiliteOut = new AideMobilitePEIOOut();
		aideMobiliteOut = poleEmploiIOClient.callAideMobiliteEndPoint(aideMobilitePEIOIn, this.accessToken).get();

        assertThat(aideMobiliteOut.getMontant()).isZero();
    }
}
