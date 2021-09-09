package fr.poleemploi.estime.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fr.poleemploi.estime.commun.enumerations.Aides;
import fr.poleemploi.estime.logique.simulateur.aides.utile.AideUtile;
import fr.poleemploi.estime.services.ressources.Aide;

@RestController
@RequestMapping("/aides")
public class AidesService {
    @Autowired
    private AideUtile aideeUtile;

    @GetMapping(value = "/{codeAide}/details", produces = MediaType.APPLICATION_JSON_VALUE)
    public Aide obtenirDetailsAides(@PathVariable String codeAide) {
	Aide aide = new Aide();
	if (Aides.ALLOCATION_ADULTES_HANDICAPES.getCode().equals(codeAide)) {
	    aide.setCode(codeAide);
	    aide.setNom(Aides.ALLOCATION_ADULTES_HANDICAPES.getNom());
	    aide.setDetail(this.getDescription(Aides.ALLOCATION_ADULTES_HANDICAPES.getNomFichierDetail()));
	    aide.setLienExterne("");
	} else if (Aides.AIDE_MOBILITE.getCode().equals(codeAide)) {
	    aide.setCode(codeAide);
	    aide.setNom(Aides.AIDE_MOBILITE.getNom());
	    aide.setDetail(this.getDescription(Aides.AIDE_MOBILITE.getNomFichierDetail()));
	    aide.setLienExterne("https://candidat.pole-emploi.fr/candidat/aides/mobilite/tableaudebord\\r\\n");
	} else if (Aides.AGEPI.getCode().equals(codeAide)) {
	    aide.setCode(codeAide);
	    aide.setNom(Aides.AGEPI.getNom());
	    aide.setDetail(this.getDescription(Aides.AGEPI.getNomFichierDetail()));
	    aide.setLienExterne("https://candidat.pole-emploi.fr/candidat/aides/mobilite/tableaudebord\r\n");
	} else if (Aides.ALLOCATION_SOLIDARITE_SPECIFIQUE.getCode().equals(codeAide)) {
	    aide.setCode(codeAide);
	    aide.setNom(Aides.ALLOCATION_SOLIDARITE_SPECIFIQUE.getNom());
	    aide.setDetail(this.getDescription(Aides.ALLOCATION_SOLIDARITE_SPECIFIQUE.getNomFichierDetail()));
	    aide.setLienExterne("");
	} else if (Aides.PRIME_ACTIVITE.getCode().equals(codeAide)) {
	    aide.setCode(codeAide);
	    aide.setNom(Aides.PRIME_ACTIVITE.getNom());
	    aide.setDetail(this.getDescription(Aides.PRIME_ACTIVITE.getNomFichierDetail()));
	    aide.setLienExterne("https://www.caf.fr/allocataires/mes-services-en-ligne/faire-une-demande-de-prestation");
	}
	return aide;
    }

    private String getDescription(String nomFichierDetail) {
	Optional<String> optionalDescription = aideeUtile.getDescription(nomFichierDetail);
	String description = "";
	if (optionalDescription.isPresent()) {
	    description = optionalDescription.get();
	}
	return description;
    }
}