package testsintegration.simulation.poleemploiio;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;

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
import fr.poleemploi.estime.clientsexternes.poleemploiio.ressources.ArePEIOIn;
import fr.poleemploi.estime.clientsexternes.poleemploiio.ressources.ArePEIOOut;


@SpringBootTest
@ContextConfiguration
@TestPropertySource(locations = "classpath:application-test.properties")
class InterfacageAPITest {
	
	@Autowired
	private PoleEmploiIOClient poleEmploiIOClient;
	
	private final String accessToken ="Bearer o-WI0G1dy6A8gwp1_VNIUW86lcU";
	
	 @Configuration
	 @ComponentScan({"utile.tests","fr.poleemploi.estime"})
	 public static class SpringConfig {

	 }
	 
	@Test
    void testInterfacageApiAGEPIValide(){	
		
		AgepiPEIOIn agepiPEIOIn = new AgepiPEIOIn();
    	agepiPEIOIn.setContexte("Reprise");
    	agepiPEIOIn.setDateActionReclassement("2021-11-08");
    	agepiPEIOIn.setDateDepot("2021-11-08");
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
    	aideMobilitePEIOIn.setDateActionReclassement("2021-11-09");
    	aideMobilitePEIOIn.setDateDepot("2021-11-09");
    	aideMobilitePEIOIn.setDureePeriodeEmploiOuFormation(90);
    	aideMobilitePEIOIn.setNatureContratTravail("CDI");
    	aideMobilitePEIOIn.setOrigine("c");
    	aideMobilitePEIOIn.setDistanceDomicileActionReclassement(65000);
    	aideMobilitePEIOIn.setNombreAllersRetours(15);
    	aideMobilitePEIOIn.setNombreRepas(0);
    	aideMobilitePEIOIn.setNombreNuitees(0);
    	aideMobilitePEIOIn.setCodeTerritoire("001");
    	aideMobilitePEIOIn.setDureePeriodeEmploiOuFormation(90);
    	aideMobilitePEIOIn.setEleveSeulEnfants(true);
    	aideMobilitePEIOIn.setFraisPrisEnChargeParTiers(false);
    	aideMobilitePEIOIn.setIntensite(50);
    	aideMobilitePEIOIn.setTypeIntensite("mensuelle");
    	aideMobilitePEIOIn.setNombreEnfantsMoins10Ans(2);
    	aideMobilitePEIOIn.setNombreEnfants(3);
    	aideMobilitePEIOIn.setLieuFormationOuEmploi("France");
    	
		AideMobilitePEIOOut aideMobiliteOut = poleEmploiIOClient.callAideMobiliteEndPoint(aideMobilitePEIOIn, this.accessToken).get();
		float montant = aideMobiliteOut.getDecisionAideMobiliteAPI().getMontant();
        assertThat(montant).isPositive();
    }
	
	@Test
    void testInterfacageApiAideMobInvalide(){		
		AideMobilitePEIOIn aideMobilitePEIOIn = new AideMobilitePEIOIn();
    	aideMobilitePEIOIn.setContexte("Reprise");
    	aideMobilitePEIOIn.setDateActionReclassement("2021-11-09");
    	aideMobilitePEIOIn.setDateDepot("2021-11-09");
    	aideMobilitePEIOIn.setDureePeriodeEmploiOuFormation(90);
    	aideMobilitePEIOIn.setNatureContratTravail("CDI");
    	aideMobilitePEIOIn.setOrigine("c");
    	aideMobilitePEIOIn.setDistanceDomicileActionReclassement(25000);
    	aideMobilitePEIOIn.setNombreAllersRetours(15);
    	aideMobilitePEIOIn.setNombreRepas(0);
    	aideMobilitePEIOIn.setNombreNuitees(0);
    	aideMobilitePEIOIn.setCodeTerritoire("001");
    	aideMobilitePEIOIn.setDureePeriodeEmploiOuFormation(90);
    	aideMobilitePEIOIn.setEleveSeulEnfants(true);
    	aideMobilitePEIOIn.setFraisPrisEnChargeParTiers(false);
    	aideMobilitePEIOIn.setIntensite(50);
    	aideMobilitePEIOIn.setTypeIntensite("mensuelle");
    	aideMobilitePEIOIn.setNombreEnfantsMoins10Ans(2);
    	aideMobilitePEIOIn.setNombreEnfants(3);
    	aideMobilitePEIOIn.setLieuFormationOuEmploi("France");
    	
		AideMobilitePEIOOut aideMobiliteOut = poleEmploiIOClient.callAideMobiliteEndPoint(aideMobilitePEIOIn, this.accessToken).get();
		float montant = aideMobiliteOut.getDecisionAideMobiliteAPI().getMontant();
        assertThat(montant).isZero();
    }
	
	@Test
    void testInterfacageApiAreValide(){	
		ArePEIOIn areIn = new ArePEIOIn();
		areIn.setAllocationBruteJournaliere(new BigDecimal(45));
		areIn.setGainBrut(new BigDecimal(27));
		areIn.setSalaireBrutJournalier(new BigDecimal(375));
		
		ArePEIOOut areOut = poleEmploiIOClient.callAreEndPoint(areIn, this.accessToken).get();
		float montant = areOut.getAllocationMensuelle().floatValue();
		assertThat(montant).isPositive();
	}
	
	@Test
    void testInterfacageApiAreInValide(){	
		ArePEIOIn areIn = new ArePEIOIn();
		areIn.setAllocationBruteJournaliere(new BigDecimal(0));
		areIn.setGainBrut(new BigDecimal(555555));
		areIn.setSalaireBrutJournalier(new BigDecimal(7777));
		
		ArePEIOOut areOut = poleEmploiIOClient.callAreEndPoint(areIn, this.accessToken).get();
		float montant = areOut.getAllocationMensuelle().floatValue();
		assertThat(montant).isZero();
	}
}
