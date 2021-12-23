package fr.poleemploi.estime.commun.utile.demandeuremploi;

import java.time.LocalDate;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fr.poleemploi.estime.clientsexternes.poleemploiio.PoleEmploiIOClient;
import fr.poleemploi.estime.clientsexternes.poleemploiio.ressources.CoordonneesPEIO;
import fr.poleemploi.estime.clientsexternes.poleemploiio.ressources.EtatCivilPEIO;
import fr.poleemploi.estime.commun.enumerations.exceptions.LoggerMessages;
import fr.poleemploi.estime.commun.utile.DateUtile;
import fr.poleemploi.estime.commun.utile.StagingEnvironnementUtile;
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
    private PoleEmploiIOClient emploiStoreDevClient;

    @Autowired
    private DateUtile dateUtile;
    
    @Autowired
    private StagingEnvironnementUtile stagingEnvironnementUtile;

    private static final Logger LOGGER = LoggerFactory.getLogger(DemandeurEmploiUtile.class);

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

    public void addDateNaissance(InformationsPersonnelles informationsPersonnelles, String bearerToken) {
        try {
            Optional<EtatCivilPEIO> etatCivilPEIOOptional = emploiStoreDevClient.callEtatCivilEndPoint(bearerToken);
            if (etatCivilPEIOOptional.isPresent()) {
                EtatCivilPEIO etatCivilPEIO = etatCivilPEIOOptional.get();
                if (etatCivilPEIO.getDateDeNaissance() != null) {
                    LocalDate dateNaissanceLocalDate = dateUtile.convertDateToLocalDate(etatCivilPEIO.getDateDeNaissance());
                    informationsPersonnelles.setDateNaissance(dateNaissanceLocalDate);
                }
            }
        } catch (Exception e) {
            String messageError = String.format(LoggerMessages.RETOUR_SERVICE_KO.getMessage(), e.getMessage(), "peio api date de naissance");
            LOGGER.error(messageError);
        }
    }

    public void addCodeDepartement(InformationsPersonnelles informationsPersonnelles, String bearerToken) {
        try {
            Optional<CoordonneesPEIO> coordonneesESDOptional = emploiStoreDevClient.callCoordonneesAPI(bearerToken);
            if (coordonneesESDOptional.isPresent()) {
                CoordonneesPEIO coordonneesESD = coordonneesESDOptional.get();
                informationsPersonnelles.setCodePostal(coordonneesESD.getCodePostal());
            }
        } catch (Exception e) {
            String messageError = String.format(LoggerMessages.RETOUR_SERVICE_KO.getMessage(), e.getMessage(), "peio api coordonnees");
            LOGGER.error(messageError);
        }
    }
    
    public void addInformationsPersonnelles(DemandeurEmploi demandeurEmploi, Individu individu) {        
        InformationsPersonnelles informationsPersonnelles = new InformationsPersonnelles();
        Logement logement = creerLogement();
        StatutOccupationLogement statutOccupationLogement = creerStatutOccupationLogement();
        logement.setStatutOccupationLogement(statutOccupationLogement);
        informationsPersonnelles.setLogement(logement);
        
        if (stagingEnvironnementUtile.isNotLocalhostEnvironnement()) {
            String bearerToken = individu.getPeConnectAuthorization().getBearerToken();
            addCodeDepartement(informationsPersonnelles, bearerToken);
            addDateNaissance(informationsPersonnelles, bearerToken);
        } else {
            stagingEnvironnementUtile.bouchonnerCodeDepartementEtDateNaissance(informationsPersonnelles);
        }
        
        demandeurEmploi.setInformationsPersonnelles(informationsPersonnelles);
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
    
    private Logement creerLogement() {
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

    private StatutOccupationLogement creerStatutOccupationLogement() {
        StatutOccupationLogement statutOccupationLogement = new StatutOccupationLogement();
        statutOccupationLogement.setLocataireHLM(false);
        statutOccupationLogement.setLocataireMeuble(false);
        statutOccupationLogement.setLocataireNonMeuble(false);
        statutOccupationLogement.setLogeGratuitement(false);
        statutOccupationLogement.setProprietaire(false);
        statutOccupationLogement.setProprietaireAvecEmprunt(false);
        return statutOccupationLogement;
    }
}
