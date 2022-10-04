package fr.poleemploi.estime.commun.utile;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fr.poleemploi.estime.donnees.entities.AutresSituationsEntity;
import fr.poleemploi.estime.donnees.managers.AutresSituationsManager;
import fr.poleemploi.estime.services.ressources.AutresSituations;

@Component
public class AutresSituationsUtile {

    @Autowired
    private DateUtile dateUtile;

    @Autowired
    private AutresSituationsManager autresSituationsManager;

    public void tracerAutresSituations(String idEstime, AutresSituations autresSituations) {
	AutresSituationsEntity autresSituationsEntity = creerAutresSituationsEntity(idEstime, autresSituations);

	autresSituationsManager.creerAutresSituations(autresSituationsEntity);
    }

    private AutresSituationsEntity creerAutresSituationsEntity(String idEstime, AutresSituations autresSituations) {
	AutresSituationsEntity autresSituationsEntity = new AutresSituationsEntity();
	autresSituationsEntity.setIdEstime(idEstime);
	autresSituationsEntity.setDateCreation(dateUtile.getDateTimeJour());
	autresSituationsEntity.setSalaire(autresSituations.isSalaire());
	autresSituationsEntity.setAlternant(autresSituations.isAlternant());
	autresSituationsEntity.setFormation(autresSituations.isFormation());
	autresSituationsEntity.setAda(autresSituations.isAda());
	autresSituationsEntity.setCej(autresSituations.isCej());
	autresSituationsEntity.setSecurisationProfessionnelle(autresSituations.isSecurisationProfessionnelle());
	autresSituationsEntity.setAucuneRessource(autresSituations.isAucuneRessource());
	autresSituationsEntity.setAutre(autresSituations.isAutre());
	autresSituationsEntity.setAutreContenu(autresSituations.getAutreContenu());

	return autresSituationsEntity;
    }
}
