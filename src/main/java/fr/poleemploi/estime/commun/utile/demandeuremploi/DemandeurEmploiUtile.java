package fr.poleemploi.estime.commun.utile.demandeuremploi;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fr.poleemploi.estime.clientsexternes.poleemploiio.PoleEmploiIOClient;
import fr.poleemploi.estime.clientsexternes.poleemploiio.ressources.CoordonneesPEIO;
import fr.poleemploi.estime.clientsexternes.poleemploiio.ressources.DateNaissancePEIO;
import fr.poleemploi.estime.commun.utile.DateUtile;
import fr.poleemploi.estime.commun.utile.IndividuUtile;
import fr.poleemploi.estime.services.ressources.AidesCAF;
import fr.poleemploi.estime.services.ressources.AidesLogement;
import fr.poleemploi.estime.services.ressources.AllocationsLogement;
import fr.poleemploi.estime.services.ressources.DemandeurEmploi;
import fr.poleemploi.estime.services.ressources.Individu;
import fr.poleemploi.estime.services.ressources.InformationsPersonnelles;
import fr.poleemploi.estime.services.ressources.RessourcesFinancieres;

@Component
public class DemandeurEmploiUtile {

    @Autowired
    private PoleEmploiIOClient emploiStoreDevClient;

    @Autowired
    private DateUtile dateUtile;
    
    @Autowired
    private IndividuUtile individuUtile;

    public boolean isSansRessourcesFinancieres(DemandeurEmploi demandeurEmploi) {
	return demandeurEmploi.getRessourcesFinancieres() == null;
    }

    public void addRessourcesFinancieres(DemandeurEmploi demandeurEmploi, Individu individu) {
	if (individu.getRessourcesFinancieres() != null) {
	    demandeurEmploi.setRessourcesFinancieres(individu.getRessourcesFinancieres());
	} else {
	    demandeurEmploi.setRessourcesFinancieres(new RessourcesFinancieres());
	}
	demandeurEmploi.getRessourcesFinancieres().setAidesCAF(creerAidesCAF());
	demandeurEmploi.getRessourcesFinancieres().setNombreMoisTravaillesDerniersMois(0);
    }

    public void addInformationsPersonnelles(DemandeurEmploi demandeurEmploi, Individu individu, String bearerToken) {
	if (individu.getInformationsPersonnelles() != null) {
	    demandeurEmploi.setInformationsPersonnelles(individu.getInformationsPersonnelles());
	} else {
	    demandeurEmploi.setInformationsPersonnelles(individuUtile.creerInformationsPersonnelles());
	}
	addDateNaissance(demandeurEmploi, bearerToken);
    }

    private void addDateNaissance(DemandeurEmploi demandeurEmploi, String bearerToken) {
	Optional<DateNaissancePEIO> dateNaissancePEIOOptional = emploiStoreDevClient.callDateNaissanceEndPoint(bearerToken);
	if (dateNaissancePEIOOptional.isPresent()) {
	    DateNaissancePEIO dateNaissancePEIO = dateNaissancePEIOOptional.get();
	    if (dateNaissancePEIO.getDateDeNaissance() != null) {
		LocalDate dateNaissanceLocalDate = dateUtile.convertDateToLocalDate(dateNaissancePEIO.getDateDeNaissance());
		if (demandeurEmploi.getInformationsPersonnelles() != null) {
		    demandeurEmploi.getInformationsPersonnelles().setDateNaissance(dateNaissanceLocalDate);
		} else {
		    InformationsPersonnelles informationsPersonnelles = individuUtile.creerInformationsPersonnelles();
		    informationsPersonnelles.setDateNaissance(dateNaissanceLocalDate);
		    demandeurEmploi.setInformationsPersonnelles(informationsPersonnelles);
		}
	    }
	}
    }

    public void addCodeDepartement(DemandeurEmploi demandeurEmploi, String bearerToken) {
	Optional<CoordonneesPEIO> coordonneesPEIOOptional = emploiStoreDevClient.callCoordonneesAPI(bearerToken);
	if (coordonneesPEIOOptional.isPresent()) {
	    CoordonneesPEIO coordonneesPEIO = coordonneesPEIOOptional.get();
	    if (demandeurEmploi.getInformationsPersonnelles() != null) {
		demandeurEmploi.getInformationsPersonnelles().setCodePostal(coordonneesPEIO.getCodePostal());
	    } else {
		InformationsPersonnelles informationsPersonnelles = individuUtile.creerInformationsPersonnelles();
		informationsPersonnelles.setCodePostal(coordonneesPEIO.getCodePostal());
		demandeurEmploi.setInformationsPersonnelles(informationsPersonnelles);
	    }
	}
    }

    private AidesCAF creerAidesCAF() {
	AidesCAF aidesCAF = new AidesCAF();
	aidesCAF.setAidesLogement(creerAidesLogement());
	return aidesCAF;
    }

    private AidesLogement creerAidesLogement() {
	AidesLogement aidesLogement = new AidesLogement();
	aidesLogement.setAidePersonnaliseeLogement(creerAllocationsLogement());
	aidesLogement.setAllocationLogementFamiliale(creerAllocationsLogement());
	aidesLogement.setAllocationLogementSociale(creerAllocationsLogement());
	return aidesLogement;
    }

    private AllocationsLogement creerAllocationsLogement() {
	AllocationsLogement allocationsLogement = new AllocationsLogement();
	allocationsLogement.setMoisNMoins1(0);
	allocationsLogement.setMoisNMoins2(0);
	allocationsLogement.setMoisNMoins3(0);
	return allocationsLogement;
    }
}
