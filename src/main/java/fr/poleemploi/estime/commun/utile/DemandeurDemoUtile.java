package fr.poleemploi.estime.commun.utile;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fr.poleemploi.estime.clientsexternes.poleemploiio.ressources.CoordonneesPEIO;
import fr.poleemploi.estime.clientsexternes.poleemploiio.ressources.DetailIndemnisationPEIO;
import fr.poleemploi.estime.clientsexternes.poleemploiio.ressources.UserInfoPEIO;
import fr.poleemploi.estime.services.ressources.AidesPoleEmploi;
import fr.poleemploi.estime.services.ressources.AllocationASS;
import fr.poleemploi.estime.services.ressources.BeneficiaireAides;
import fr.poleemploi.estime.services.ressources.Individu;
import fr.poleemploi.estime.services.ressources.InformationsPersonnelles;
import fr.poleemploi.estime.services.ressources.RessourcesFinancieres;

@Component
public class DemandeurDemoUtile {

    @Autowired
    private IndividuUtile individuUtile;

    private static String nomDemandeurDemo = "estimedemo";
    
    public boolean isDemandeurDemo(UserInfoPEIO userInfoPEIO) {
        return userInfoPEIO.getFamilyName() != null && userInfoPEIO.getFamilyName().toLowerCase().contains(nomDemandeurDemo);
    }

    public void addInformationsDetailIndemnisationPoleEmploi(Individu individu, DetailIndemnisationPEIO detailIndemnisationPEIO, CoordonneesPEIO coordonneesPEIO) {
        addInformationsBeneficiaireAides(individu, detailIndemnisationPEIO);
        addInformationsRessourcesFinancieresPoleEmploi(individu, detailIndemnisationPEIO);
        addInformationsPersonnelles(individu, coordonneesPEIO);        
    }
    
    private void addInformationsBeneficiaireAides(Individu individu, DetailIndemnisationPEIO detailIndemnisationPEIO) {
        BeneficiaireAides beneficiaireAides = new BeneficiaireAides();
        beneficiaireAides.setBeneficiaireAAH(false);
        beneficiaireAides.setBeneficiaireARE(false);
        beneficiaireAides.setBeneficiaireASS(true);
        beneficiaireAides.setBeneficiaireRSA(false);
        individu.setBeneficiaireAides(beneficiaireAides);
    }
    
    private void addInformationsRessourcesFinancieresPoleEmploi(Individu individu, DetailIndemnisationPEIO detailIndemnisationPEIO) {
        RessourcesFinancieres ressourcesFinancieres = new RessourcesFinancieres();       
        ressourcesFinancieres.setAidesPoleEmploi(creerAidePoleEmploi(detailIndemnisationPEIO));
        individu.setRessourcesFinancieres(ressourcesFinancieres);
    }
    
    private AidesPoleEmploi creerAidePoleEmploi(DetailIndemnisationPEIO detailIndemnisationPEIO) {
        AidesPoleEmploi aidesPoleEmploi = new AidesPoleEmploi();
        AllocationASS allocationASS = new AllocationASS();  
        aidesPoleEmploi.setAllocationASS(allocationASS);
        return aidesPoleEmploi;
    }

    private void addInformationsPersonnelles(Individu individu, CoordonneesPEIO coordonneesPEIO) {
        InformationsPersonnelles informationsPersonnelles = individuUtile.creerInformationsPersonnelles();       
        if(coordonneesPEIO.getCodePostal() != null) {
            informationsPersonnelles.setCodePostal(coordonneesPEIO.getCodePostal());
        } else {
            informationsPersonnelles.setCodePostal("44000");
        }
        individu.setInformationsPersonnelles(informationsPersonnelles);
    }

}
