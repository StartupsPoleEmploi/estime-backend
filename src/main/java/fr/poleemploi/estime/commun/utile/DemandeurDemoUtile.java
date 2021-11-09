package fr.poleemploi.estime.commun.utile;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fr.poleemploi.estime.clientsexternes.poleemploiio.ressources.CoordonneesESD;
import fr.poleemploi.estime.clientsexternes.poleemploiio.ressources.DetailIndemnisationESD;
import fr.poleemploi.estime.clientsexternes.poleemploiio.ressources.UserInfoESD;
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
    
    public boolean isDemandeurDemo(UserInfoESD userInfoESD) {
        return userInfoESD.getFamilyName() != null && userInfoESD.getFamilyName().toLowerCase().contains(nomDemandeurDemo);
    }

    public void addInformationsDetailIndemnisationPoleEmploi(Individu individu, DetailIndemnisationESD detailIndemnisationESD, CoordonneesESD coordonneesESD) {
        addInformationsBeneficiaireAides(individu, detailIndemnisationESD);
        addInformationsRessourcesFinancieresPoleEmploi(individu, detailIndemnisationESD);
        addInformationsPersonnelles(individu, coordonneesESD);        
    }
    
    private void addInformationsBeneficiaireAides(Individu individu, DetailIndemnisationESD detailIndemnisation) {
        BeneficiaireAides beneficiaireAides = new BeneficiaireAides();
        beneficiaireAides.setBeneficiaireAAH(false);
        beneficiaireAides.setBeneficiaireARE(false);
        beneficiaireAides.setBeneficiaireASS(true);
        beneficiaireAides.setBeneficiaireRSA(false);
        individu.setBeneficiaireAides(beneficiaireAides);
    }
    
    private void addInformationsRessourcesFinancieresPoleEmploi(Individu individu, DetailIndemnisationESD detailIndemnisation) {
        RessourcesFinancieres ressourcesFinancieres = new RessourcesFinancieres();       
        ressourcesFinancieres.setAidesPoleEmploi(creerAidePoleEmploi(detailIndemnisation));
        individu.setRessourcesFinancieres(ressourcesFinancieres);
    }
    
    private AidesPoleEmploi creerAidePoleEmploi(DetailIndemnisationESD detailIndemnisation) {
        AidesPoleEmploi aidesPoleEmploi = new AidesPoleEmploi();
        AllocationASS allocationASS = new AllocationASS();  
        aidesPoleEmploi.setAllocationASS(allocationASS);
        return aidesPoleEmploi;
    }

    private void addInformationsPersonnelles(Individu individu, CoordonneesESD coordonneesESD) {
        InformationsPersonnelles informationsPersonnelles = individuUtile.creerInformationsPersonnelles();       
        if(coordonneesESD.getCodePostal() != null) {
            informationsPersonnelles.setCodePostal(coordonneesESD.getCodePostal());
        } else {
            informationsPersonnelles.setCodePostal("44000");
        }
        individu.setInformationsPersonnelles(informationsPersonnelles);
    }

}
