package fr.poleemploi.estime.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.github.tsohr.JSONObject;

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
			aide.setDetail(aideeUtile.getDescription(Aides.ALLOCATION_ADULTES_HANDICAPES.getNomFichierDetail()).get());
			aide.setLienExterne("https://www.wikipedia.com");
			aide.setIconeAide("AahIcon.svg");
			aide.setCouleurAide("background: #C7F0BD;");
		}else if(Aides.AIDE_MOBILITE.getCode().equals(codeAide)) {
			aide.setCode(codeAide);
			aide.setNom(Aides.AIDE_MOBILITE.getNom());
			aide.setDetail(aideeUtile.getDescription(Aides.AIDE_MOBILITE.getNomFichierDetail()).get());
			aide.setLienExterne("https://www.wikipedia.com");
			aide.setIconeAide("AmIcon.svg");
			aide.setCouleurAide("background: #F1A378;");
		}else if(Aides.AGEPI.getCode().equals(codeAide)) {
			aide.setCode(codeAide);
			aide.setNom(Aides.AGEPI.getNom());
			aide.setDetail(aideeUtile.getDescription(Aides.AGEPI.getNomFichierDetail()).get());
			aide.setLienExterne("https://www.wikipedia.com");
			aide.setIconeAide("AgepiIcon.svg");
			aide.setCouleurAide("background: #FFC6FF;");
		}else if(Aides.ALLOCATION_SOLIDARITE_SPECIFIQUE.getCode().equals(codeAide)) {
			aide.setCode(codeAide);
			aide.setNom(Aides.ALLOCATION_SOLIDARITE_SPECIFIQUE.getNom());
			aide.setDetail(aideeUtile.getDescription(Aides.ALLOCATION_SOLIDARITE_SPECIFIQUE.getNomFichierDetail()).get());
			aide.setLienExterne("https://www.pole-emploi.fr/candidat/mes-droits-aux-aides-et-allocati/aides-financieres-et-autres-allo/autres-allocations/lallocation-de-solidarite-specif.html#:~:text=En%20cas%20de%20reprise%20d'activit%C3%A9%20professionnelle%2C%20le%20demandeur%20d,mois%20(cons%C3%A9cutifs%20ou%20non).&text=Au%20terme%20des%203%20mois,si%20les%20conditions%20sont%20remplies");
			aide.setIconeAide("AssIcon.svg");
			aide.setCouleurAide("background: #f8cf8d;");
		}else if(Aides.PRIME_ACTIVITE.getCode().equals(codeAide)) {
			aide.setCode(codeAide);
			aide.setNom(Aides.PRIME_ACTIVITE.getNom());
			aide.setDetail(aideeUtile.getDescription(Aides.PRIME_ACTIVITE.getNomFichierDetail()).get());
			aide.setLienExterne("https://www.wikipedia.com");
			aide.setIconeAide("PaIcon.svg");
			aide.setCouleurAide("background: #BDB2FF;");
		}
		return aide;
	}
}