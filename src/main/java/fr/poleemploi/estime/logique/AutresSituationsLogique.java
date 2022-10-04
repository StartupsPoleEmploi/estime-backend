package fr.poleemploi.estime.logique;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fr.poleemploi.estime.commun.utile.AutresSituationsUtile;
import fr.poleemploi.estime.commun.utile.StagingEnvironnementUtile;
import fr.poleemploi.estime.services.ressources.AutresSituations;

@Component
public class AutresSituationsLogique {
    @Autowired
    private StagingEnvironnementUtile stagingEnvironnementUtile;
    @Autowired
    private AutresSituationsUtile autresSituationsUtile;

    public void creerAutresSituations(String idEstime, AutresSituations autresSituations) {
	if (stagingEnvironnementUtile.isNotLocalhostEnvironnement()) {
	    autresSituationsUtile.tracerAutresSituations(idEstime, autresSituations);
	}
    }
}