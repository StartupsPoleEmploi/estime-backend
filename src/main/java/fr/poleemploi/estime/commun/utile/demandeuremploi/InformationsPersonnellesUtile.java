package fr.poleemploi.estime.commun.utile.demandeuremploi;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fr.poleemploi.estime.commun.enumerations.Nationalites;
import fr.poleemploi.estime.commun.enumerations.StatutsOccupationLogement;
import fr.poleemploi.estime.services.ressources.DemandeurEmploi;
import fr.poleemploi.estime.services.ressources.InformationsPersonnelles;

@Component
public class InformationsPersonnellesUtile {
    
    @Autowired
    private CodeDepartementUtile codeDepartementUtile;
    
    public boolean isFrancais(InformationsPersonnelles informationsPersonnelles) {
        return Nationalites.FRANCAISE.getValeur().equalsIgnoreCase(informationsPersonnelles.getNationalite());
    }
    
    public boolean isEuropeenOuSuisse(InformationsPersonnelles informationsPersonnelles) {
        return Nationalites.EUROPEEN_OU_SUISSE.getValeur().equalsIgnoreCase(informationsPersonnelles.getNationalite());
    }
    
    public boolean isNotFrancaisOuEuropeenOuSuisse(InformationsPersonnelles informationsPersonnelles) {
        return Nationalites.AUTRE.getValeur().equalsIgnoreCase(informationsPersonnelles.getNationalite());
    }

    public boolean isTitreSejourEnFranceValide(InformationsPersonnelles informationsPersonnelles) {
        return informationsPersonnelles.getTitreSejourEnFranceValide().booleanValue();
    }

    public boolean isDeFranceMetropolitaine(DemandeurEmploi demandeurEmploi) {
        String codeDepartement = codeDepartementUtile.getCodeDepartement(demandeurEmploi.getInformationsPersonnelles().getCodePostal());
        return codeDepartement.length() == 2;
    }
    
    public boolean isDesDOM(DemandeurEmploi demandeurEmploi) {
        String codeDepartement = codeDepartementUtile.getCodeDepartement(demandeurEmploi.getInformationsPersonnelles().getCodePostal());
        return codeDepartement.length() == 3;
    }
    
    public boolean isDeMayotte(DemandeurEmploi demandeurEmploi) {
        if(hasCodePostal(demandeurEmploi)) {
            String codeDepartement = codeDepartementUtile.getCodeDepartement(demandeurEmploi.getInformationsPersonnelles().getCodePostal());
            return CodeDepartementUtile.CODE_DEPARTEMENT_MAYOTTE.equals(codeDepartement);            
        }
        return false;
    }
    
    public String getStatutOccupationLogement(DemandeurEmploi demandeurEmploi) {
        Boolean isProprietaireSansPretOuLogeGratuit = demandeurEmploi.getInformationsPersonnelles().getIsProprietaireSansPretOuLogeGratuit();
        return isProprietaireSansPretOuLogeGratuit != null 
                && isProprietaireSansPretOuLogeGratuit.booleanValue() ? 
                        StatutsOccupationLogement.LOGE_GRATUITEMENT.getLibelle() : 
                            StatutsOccupationLogement.LOCATAIRE_VIDE.getLibelle();
    }
    
    public boolean hasCodePostal(DemandeurEmploi demandeurEmploi) {
        return demandeurEmploi.getInformationsPersonnelles().getCodePostal() != null;
    }
}
