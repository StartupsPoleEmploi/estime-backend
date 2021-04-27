package fr.poleemploi.estime.commun.utile.demandeuremploi;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fr.poleemploi.estime.clientsexternes.emploistoredev.EmploiStoreDevClient;
import fr.poleemploi.estime.clientsexternes.emploistoredev.ressources.CoordonneesESD;
import fr.poleemploi.estime.clientsexternes.emploistoredev.ressources.DateNaissanceESD;
import fr.poleemploi.estime.commun.utile.DateUtile;
import fr.poleemploi.estime.services.ressources.DemandeurEmploi;
import fr.poleemploi.estime.services.ressources.InformationsPersonnelles;

@Component
public class DemandeurEmploiUtile {

    @Autowired
    private EmploiStoreDevClient emploiStoreDevClient;
    
    @Autowired
    private DateUtile dateUtile;    

    public void addDateNaissance(DemandeurEmploi demandeurEmploi, String bearerToken) {
        Optional<DateNaissanceESD> dateNaissanceESDOptional = emploiStoreDevClient.callDateNaissanceEndPoint(bearerToken);
        if(dateNaissanceESDOptional.isPresent()) {
            DateNaissanceESD dateNaissanceESD = dateNaissanceESDOptional.get();
            if(dateNaissanceESD.getDateDeNaissance() != null) {
                LocalDate dateNaissanceLocalDate = dateUtile.convertDateToLocalDate(dateNaissanceESD.getDateDeNaissance());
                if(demandeurEmploi.getInformationsPersonnelles() != null) {
                    demandeurEmploi.getInformationsPersonnelles().setDateNaissance(dateNaissanceLocalDate);                
                } else {
                    InformationsPersonnelles informationsPersonnelles = new InformationsPersonnelles();
                    informationsPersonnelles.setDateNaissance(dateNaissanceLocalDate);
                    demandeurEmploi.setInformationsPersonnelles(informationsPersonnelles);
                }
            }
        }
    }

    public void addCodeDepartement(DemandeurEmploi demandeurEmploi, String bearerToken) {
        Optional<CoordonneesESD> coordonneesESDOptional = emploiStoreDevClient.callCoordonneesAPI(bearerToken);
        if(coordonneesESDOptional.isPresent()) {
            CoordonneesESD coordonneesESD = coordonneesESDOptional.get();
            if(demandeurEmploi.getInformationsPersonnelles() != null) {
                demandeurEmploi.getInformationsPersonnelles().setCodePostal(coordonneesESD.getCodePostal());                            
            } else {
                InformationsPersonnelles informationsPersonnelles = new InformationsPersonnelles();
                informationsPersonnelles.setCodePostal(coordonneesESD.getCodePostal());
                demandeurEmploi.setInformationsPersonnelles(informationsPersonnelles);
            }
        }
    }   

    public boolean isSansRessourcesFinancieres(DemandeurEmploi demandeurEmploi) {
        return demandeurEmploi.getRessourcesFinancieres() == null;
    }   
}
