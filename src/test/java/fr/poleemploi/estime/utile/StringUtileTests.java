package fr.poleemploi.estime.utile;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import fr.poleemploi.estime.commun.utile.StringUtile;

@ExtendWith(SpringExtension.class)
@ContextConfiguration
@SpringBootTest
@AutoConfigureTestDatabase
class StringUtileTests {
    
    @Autowired
    private StringUtile stringUtile;
    
    @Configuration
    @ComponentScan({"fr.poleemploi.test","fr.poleemploi.estime"})
    public static class SpringConfig {

    }
    
    @Test
    void isNumericTest1() {
        
        String strintToChackt = "2A";
        
        boolean isNumeric = stringUtile.isNumeric(strintToChackt);
        
        assertThat(isNumeric).isFalse();
    }
    
    @Test
    void isNumericTest2() {
        
        String strintToChackt = "2B";
        
        boolean isNumeric = stringUtile.isNumeric(strintToChackt);
        
        assertThat(isNumeric).isFalse();
    }
    
    @Test
    void isNumericTest3() {
        
        String strintToChackt = "65";
        
        boolean isNumeric = stringUtile.isNumeric(strintToChackt);
        
        assertThat(isNumeric).isTrue();
    }
    
    @Test
    void isNumericTest4() {
        
        String strintToChackt = "97";
        
        boolean isNumeric = stringUtile.isNumeric(strintToChackt);
        
        assertThat(isNumeric).isTrue();
    }
    
    void getDeuxPremiersCaracteresTest1() {
        String stringValue = "72300";
        
        String deuxPremiersCaracteres = stringUtile.getPremiersCaracteres(stringValue, 2);
        
        assertThat(deuxPremiersCaracteres).isEqualTo("72");
    }
    
    void getDeuxPremiersCaracteresTest2() {
        String stringValue = "2A004";
        
        String deuxPremiersCaracteres = stringUtile.getPremiersCaracteres(stringValue, 2);
        
        assertThat(deuxPremiersCaracteres).isEqualTo("2A");
    }
    
    void getDeuxPremiersCaracteresTest3() {
        String stringValue = "97600";
        
        String deuxPremiersCaracteres = stringUtile.getPremiersCaracteres(stringValue, 2);
        
        assertThat(deuxPremiersCaracteres).isEqualTo("97600");
    }

}
