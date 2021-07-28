package testsunitaires.commun.utile;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import fr.poleemploi.estime.commun.utile.StringUtile;


class StringUtileTests {
    
    private StringUtile stringUtile = new StringUtile();
    
   
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
