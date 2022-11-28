package testsunitaires.commun.utile;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import fr.poleemploi.estime.commun.utile.demandeuremploi.CodeDepartementUtile;


@ContextConfiguration
@SpringBootTest
@TestPropertySource(locations="classpath:application-test.properties")
class CodeDepartementUtileTests {

    @Autowired
    private CodeDepartementUtile codeDepartementUtile;
    
    @Configuration
    @ComponentScan({"utile.tests","fr.poleemploi.estime"})
    public static class SpringConfig {

    }
    
    @Test
    void getCodeDepartementTest1() {
        
        String codePostal = "72300";
        
        String codeDepartement = codeDepartementUtile.getCodeDepartement(codePostal);
        
        assertThat(codeDepartement).isEqualTo("72");
    }
    
    @Test
    void getCodeDepartementTest2() {
        
        String codePostal = "2A004";
        
        String codeDepartement = codeDepartementUtile.getCodeDepartement(codePostal);
        
        assertThat(codeDepartement).isEqualTo("2A");
    }
    
    @Test
    void getCodeDepartementTest3() {
        
        String codePostal = "97600";
        
        String codeDepartement = codeDepartementUtile.getCodeDepartement(codePostal);
        
        assertThat(codeDepartement).isEqualTo("976");
    }
    
    @Test
    void getCodeDepartementTest4() {
        
        String codePostal = "97126";
        
        String codeDepartement = codeDepartementUtile.getCodeDepartement(codePostal);
        
        assertThat(codeDepartement).isEqualTo("971");
    }
    
    @Test
    void getCodeDepartementTest5() {
        
        String codePostal = "97300";
        
        String codeDepartement = codeDepartementUtile.getCodeDepartement(codePostal);
        
        assertThat(codeDepartement).isEqualTo("973");
    }
    
    @Test
    void getCodeDepartementTest6() {
        
        String codePostal = "95400";
        
        String codeDepartement = codeDepartementUtile.getCodeDepartement(codePostal);
        
        assertThat(codeDepartement).isEqualTo("95");
    }
}
