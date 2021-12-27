package fr.poleemploi.estime.logique;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fr.poleemploi.estime.logique.simulateur.aides.utile.AideUtile;
import fr.poleemploi.estime.services.ressources.Aide;

@Component
public class AideLogique {
    
    @Autowired
    private AideUtile aideUtile;
    
    public Aide getAideByCode(String codeAide) {
        return aideUtile.creerAide(aideUtile.getAideEnumByCode(codeAide));
    }
}