package testsintegration.services.individuservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import fr.poleemploi.estime.services.IndividuService;

@SpringBootTest
@ContextConfiguration
@TestPropertySource(locations="classpath:application-test.properties")
class IndividuServiceControlerInformationsPersonnellesControleurTests extends CommunTests {

    @Autowired
    private IndividuService individuService;
    
    @Configuration
    @ComponentScan({"utile.tests","fr.poleemploi.estime"})
    public static class SpringConfig {

    }
  
    
}
