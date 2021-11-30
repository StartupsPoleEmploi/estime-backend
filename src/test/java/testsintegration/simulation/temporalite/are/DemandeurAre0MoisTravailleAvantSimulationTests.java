package testsintegration.simulation.temporalite.are;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@ContextConfiguration
@TestPropertySource(locations = "classpath:application-test.properties")
class DemandeurAre0MoisTravailleAvantSimulationTests extends Commun {

    @Configuration
    @ComponentScan({ "utile.tests", "fr.poleemploi.estime" })
    public static class SpringConfig {

    }

    //    @Autowired
    //    private IndividuService individuService;
    //
    //    @Test
    //    void simulerPopulationAreNonTravailleAvantSimulationProchaineDeclarationMois0() throws Exception {
    //	DemandeurEmploi demandeurEmploi = createDemandeurEmploi(0);
    //	demandeurEmploi.getRessourcesFinancieres().setHasTravailleAuCoursDerniersMois(false);
    //	demandeurEmploi.getRessourcesFinancieres().setNombreMoisTravaillesDerniersMois(0);
    //
    //	initMocksARE(true);
    //
    //	SimulationAides simulationAides = individuService.simulerAides(demandeurEmploi);
    //	List<SimulationMensuelle> listeAides = simulationAides.getSimulationsMensuelles();
    //	for (SimulationMensuelle simulationMensuelle : listeAides) {
    //	    Map<String, Aide> mesAides = simulationMensuelle.getMesAides();
    //	    for (Map.Entry<String, Aide> entry : mesAides.entrySet()) {
    //		if (entry.getValue().getCode().equals("ARE")) {
    //		    assertThat(entry.getValue().getMontant()).isEqualTo(Float.valueOf("400f"));
    //
    //		}
    //	    }
    //	}
    //
    //    }

}
