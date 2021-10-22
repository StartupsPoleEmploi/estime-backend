package fr.poleemploi.estime.commun.utile.demandeuremploi;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fr.poleemploi.estime.clientsexternes.poleemploiio.PoleEmploiIODevClient;
import fr.poleemploi.estime.clientsexternes.poleemploiio.ressources.CoordonneesESD;
import fr.poleemploi.estime.clientsexternes.poleemploiio.ressources.DateNaissanceESD;
import fr.poleemploi.estime.commun.utile.DateUtile;
import fr.poleemploi.estime.services.ressources.AidesCAF;
import fr.poleemploi.estime.services.ressources.AidesLogement;
import fr.poleemploi.estime.services.ressources.AllocationsLogement;
import fr.poleemploi.estime.services.ressources.DemandeurEmploi;
import fr.poleemploi.estime.services.ressources.Individu;
import fr.poleemploi.estime.services.ressources.InformationsPersonnelles;
import fr.poleemploi.estime.services.ressources.Logement;
import fr.poleemploi.estime.services.ressources.RessourcesFinancieres;
import fr.poleemploi.estime.services.ressources.StatutOccupationLogement;

@Component
public class DemandeurEmploiUtile {

    @Autowired
    private PoleEmploiIODevClient emploiStoreDevClient;

    @Autowired
    private DateUtile dateUtile;

    public void addDateNaissance(DemandeurEmploi demandeurEmploi, String bearerToken) {
        Optional<DateNaissanceESD> dateNaissanceESDOptional = emploiStoreDevClient.callDateNaissanceEndPoint(bearerToken);
        if (dateNaissanceESDOptional.isPresent()) {
            DateNaissanceESD dateNaissanceESD = dateNaissanceESDOptional.get();
            if (dateNaissanceESD.getDateDeNaissance() != null) {
                LocalDate dateNaissanceLocalDate = dateUtile.convertDateToLocalDate(dateNaissanceESD.getDateDeNaissance());
                if (demandeurEmploi.getInformationsPersonnelles() != null) {
                    demandeurEmploi.getInformationsPersonnelles().setDateNaissance(dateNaissanceLocalDate);
                } else {
                    InformationsPersonnelles informationsPersonnelles = creerInformationsPersonnelles();
                    informationsPersonnelles.setDateNaissance(dateNaissanceLocalDate);
                    demandeurEmploi.setInformationsPersonnelles(informationsPersonnelles);
                }
            }
        }
    }

    public void addCodeDepartement(DemandeurEmploi demandeurEmploi, String bearerToken) {
        Optional<CoordonneesESD> coordonneesESDOptional = emploiStoreDevClient.callCoordonneesAPI(bearerToken);
        if (coordonneesESDOptional.isPresent()) {
            CoordonneesESD coordonneesESD = coordonneesESDOptional.get();
            if (demandeurEmploi.getInformationsPersonnelles() != null) {
                demandeurEmploi.getInformationsPersonnelles().setCodePostal(coordonneesESD.getCodePostal());
            } else {
                InformationsPersonnelles informationsPersonnelles = creerInformationsPersonnelles();
                informationsPersonnelles.setCodePostal(coordonneesESD.getCodePostal());
                demandeurEmploi.setInformationsPersonnelles(informationsPersonnelles);
            }
        }
    }

    public InformationsPersonnelles creerInformationsPersonnelles() {
        InformationsPersonnelles informationsPersonnelles = new InformationsPersonnelles();
        Logement logement = creerLogement();
        StatutOccupationLogement statutOccupationLogement = creerStatutOccupationLogement();
        logement.setStatutOccupationLogement(statutOccupationLogement);
        informationsPersonnelles.setLogement(logement);

        return informationsPersonnelles;
    }
    
    public Logement creerLogement() {
        Logement logement = new Logement();
        logement.setChambre(false);
        logement.setCodeInsee(null);
        logement.setConventionne(false);
        logement.setColloc(false);
        logement.setCrous(false);
        logement.setDeMayotte(false);
        logement.setMontantCharges(0);
        logement.setMontantLoyer(0);
        logement.setStatutOccupationLogement(null);
        return logement;
    }
    
    public StatutOccupationLogement creerStatutOccupationLogement() {
        StatutOccupationLogement statutOccupationLogement = new StatutOccupationLogement();
        statutOccupationLogement.setLocataireHLM(false);
        statutOccupationLogement.setLocataireMeuble(false);
        statutOccupationLogement.setLocataireNonMeuble(false);
        statutOccupationLogement.setLogeGratuitement(false);
        statutOccupationLogement.setProprietaire(false);
        statutOccupationLogement.setProprietaireAvecEmprunt(false);
        return statutOccupationLogement;
    }

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

    private AidesCAF creerAidesCAF() {
        AidesCAF aidesCAF = new AidesCAF();
        aidesCAF.setAidesLogement(creerAidesLogement());
        return aidesCAF;
    }

    private AidesLogement creerAidesLogement() {
        AidesLogement aidesLogement = new AidesLogement();
        AllocationsLogement apl = new AllocationsLogement();
        apl.setMoisNMoins1(0);
        apl.setMoisNMoins2(0);
        apl.setMoisNMoins3(0);
        aidesLogement.setAidePersonnaliseeLogement(apl);
        AllocationsLogement alf = new AllocationsLogement();
        alf.setMoisNMoins1(0);
        alf.setMoisNMoins2(0);
        alf.setMoisNMoins3(0);
        aidesLogement.setAllocationLogementFamiliale(alf);
        AllocationsLogement als = new AllocationsLogement();
        als.setMoisNMoins1(0);
        als.setMoisNMoins2(0);
        als.setMoisNMoins3(0);
        aidesLogement.setAllocationLogementSociale(als);
        return aidesLogement;
    }
}
