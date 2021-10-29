package fr.poleemploi.estime.commun.utile;

import org.springframework.stereotype.Component;

import fr.poleemploi.estime.clientsexternes.poleemploiio.ressources.DetailIndemnisationESD;
import fr.poleemploi.estime.clientsexternes.poleemploiio.ressources.UserInfoESD;
import fr.poleemploi.estime.services.ressources.AidesPoleEmploi;
import fr.poleemploi.estime.services.ressources.AllocationASS;
import fr.poleemploi.estime.services.ressources.BeneficiaireAides;
import fr.poleemploi.estime.services.ressources.Individu;
import fr.poleemploi.estime.services.ressources.RessourcesFinancieres;

@Component
public class DemandeurDemoUtile {

    private static String nomDemandeurDemo = "estimedemo";
    
    public boolean isDemandeurDemo(UserInfoESD userInfoESD) {
        return nomDemandeurDemo.equalsIgnoreCase(userInfoESD.getFamilyName());
    }

    public void addInformationsDetailIndemnisationPoleEmploi(Individu individu, DetailIndemnisationESD detailIndemnisationESD) {
        addInformationsBeneficiaireAides(individu, detailIndemnisationESD);
        addInformationsRessourcesFinancieresPoleEmploi(individu, detailIndemnisationESD);
        
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

}
