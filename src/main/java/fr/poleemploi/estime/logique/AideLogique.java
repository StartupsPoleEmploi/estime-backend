package fr.poleemploi.estime.logique;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fr.poleemploi.estime.commun.enumerations.AideEnum;
import fr.poleemploi.estime.commun.enumerations.exceptions.RessourceNotFoundMessages;
import fr.poleemploi.estime.logique.simulateur.aides.utile.AideUtile;
import fr.poleemploi.estime.services.exceptions.ResourceNotFoundException;
import fr.poleemploi.estime.services.ressources.Aide;

@Component
public class AideLogique {
    
    @Autowired
    private AideUtile aideUtile;
    
    public Aide getAideByCode(String codeAide) {
    	Optional<AideEnum> aideEnumOptional = aideUtile.getAideEnumByCode(codeAide);
    	if(!aideEnumOptional.isEmpty()) {
    		return aideUtile.creerAide(aideEnumOptional.get());
    	} else {
    		throw new ResourceNotFoundException(RessourceNotFoundMessages.AIDE_NOT_FOUND.getMessage());
    	}      
    }
}