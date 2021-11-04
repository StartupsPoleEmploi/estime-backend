package testsunitaires.services.interfaceAPI;

import static org.assertj.core.api.Assertions.assertThat;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import fr.poleemploi.estime.clientsexternes.poleemploiio.PoleEmploiIOClient;
import fr.poleemploi.estime.clientsexternes.poleemploiio.ressources.AgepiPEIOIn;
import fr.poleemploi.estime.clientsexternes.poleemploiio.ressources.AgepiPEIOOut;
import fr.poleemploi.estime.clientsexternes.poleemploiio.ressources.AideMobilitePEIOIn;
import fr.poleemploi.estime.clientsexternes.poleemploiio.ressources.AideMobilitePEIOOut;

@Disabled
class InterfacageAPITest {
	private PoleEmploiIOClient poleEmploiIOClient;
	
	@Test
    void testInterfacageApiAGEPIValide(){		
		AgepiPEIOIn agepiPEIOIn = new AgepiPEIOIn();
    	agepiPEIOIn.setContexte("Reprise");
    	agepiPEIOIn.setDateActionReclassement("2021-10-27");
    	agepiPEIOIn.setDateDepot("2021-10-27");
    	agepiPEIOIn.setDureePeriodeEmploiOuFormation(90);
    	agepiPEIOIn.setEleveSeulEnfants(true);
    	agepiPEIOIn.setIntensite((int) Math.round(50));
    	agepiPEIOIn.setLieuFormationOuEmploi("France");
    	agepiPEIOIn.setNatureContratTravail("CDI");
    	agepiPEIOIn.setNombreEnfants(3);
    	agepiPEIOIn.setNombreEnfantsMoins10Ans(2);
    	agepiPEIOIn.setOrigine("c");
    	agepiPEIOIn.setTypeIntensite("Mensuelle");
		
		AgepiPEIOOut agepiOut = new AgepiPEIOOut();
		agepiOut = poleEmploiIOClient.callAgepiEndPoint(agepiPEIOIn);

        assertThat(agepiOut.getMontant()).isPositive();
    }
	
	@Test
    void testInterfacageApiAGEPIInvalide(){		
		AgepiPEIOIn agepiPEIOIn = new AgepiPEIOIn();
    	agepiPEIOIn.setContexte("Reprise");
    	agepiPEIOIn.setDateActionReclassement("2021-10-27");
    	agepiPEIOIn.setDateDepot("2021-10-27");
    	agepiPEIOIn.setDureePeriodeEmploiOuFormation(90);
    	agepiPEIOIn.setEleveSeulEnfants(false);
    	agepiPEIOIn.setIntensite((int) Math.round(50));
    	agepiPEIOIn.setLieuFormationOuEmploi("France");
    	agepiPEIOIn.setNatureContratTravail("CDI");
    	agepiPEIOIn.setNombreEnfants(0);
    	agepiPEIOIn.setNombreEnfantsMoins10Ans(0);
    	agepiPEIOIn.setOrigine("c");
    	agepiPEIOIn.setTypeIntensite("Mensuelle");
		
		AgepiPEIOOut agepiOut = new AgepiPEIOOut();
		agepiOut = poleEmploiIOClient.callAgepiEndPoint(agepiPEIOIn);

        assertThat(agepiOut.getMontant()).isZero();
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
		aideMobiliteOut = poleEmploiIOClient.callAideMobiliteEndPoint(aideMobilitePEIOIn);

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
		aideMobiliteOut = poleEmploiIOClient.callAideMobiliteEndPoint(aideMobilitePEIOIn);

        assertThat(aideMobiliteOut.getMontant()).isZero();
    }
}
