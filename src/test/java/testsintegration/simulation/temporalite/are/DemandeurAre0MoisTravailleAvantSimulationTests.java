package testsintegration.simulation.temporalite.are;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import fr.poleemploi.estime.services.IndividuService;
import fr.poleemploi.estime.services.ressources.Aide;
import fr.poleemploi.estime.services.ressources.DemandeurEmploi;
import fr.poleemploi.estime.services.ressources.SimulationAides;
import fr.poleemploi.estime.services.ressources.SimulationMensuelle;

@SpringBootTest
@ContextConfiguration
@TestPropertySource(locations = "classpath:application-test.properties")
class DemandeurAre0MoisTravailleAvantSimulationTests extends Commun{
	
	@Configuration
    @ComponentScan({ "utile.tests", "fr.poleemploi.estime" })
    public static class SpringConfig {

    }

    @Autowired
    private IndividuService individuService;

    @Test
    void simulerPopulationAreNonTravailleAvantSimulationProchaineDeclarationMois0() throws Exception {
    	DemandeurEmploi demandeurEmploi = createDemandeurEmploi(0);
    	demandeurEmploi.getRessourcesFinancieres().setHasTravailleAuCoursDerniersMois(false);
        demandeurEmploi.getRessourcesFinancieres().setNombreMoisTravaillesDerniersMois(0);
        
        SimulationAides simulationAides = individuService.simulerAides(demandeurEmploi);
        List<SimulationMensuelle> listeAides = simulationAides.getSimulationsMensuelles();
        for(SimulationMensuelle simulationMensuelle: listeAides) {
        	Map<String, Aide> mesAIdes = simulationMensuelle.getMesAides();
        	for (Map.Entry<String, Aide> entry : mesAIdes.entrySet()) {
                if(entry.getValue().getCode().equals("ARE")) {
                	assertThat(entry.getValue().getMontant()).isEqualTo(Float.valueOf("567.0"));
                	
                }
            }
        }
       
        
    }

}
