package fr.poleemploi.estime.commun.utile;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import fr.poleemploi.estime.clientsexternes.poleemploiio.ressources.UserInfoPEIO;
import fr.poleemploi.estime.commun.enumerations.Aides;
import fr.poleemploi.estime.commun.enumerations.Environnements;
import fr.poleemploi.estime.services.ressources.AidesCAF;
import fr.poleemploi.estime.services.ressources.AidesCPAM;
import fr.poleemploi.estime.services.ressources.AidesLogement;
import fr.poleemploi.estime.services.ressources.AidesPoleEmploi;
import fr.poleemploi.estime.services.ressources.AllocationASS;
import fr.poleemploi.estime.services.ressources.AllocationsLogement;
import fr.poleemploi.estime.services.ressources.BeneficiaireAides;
import fr.poleemploi.estime.services.ressources.Individu;
import fr.poleemploi.estime.services.ressources.RessourcesFinancieres;

@Component
public class StagingEnvironnementUtile {

    @Value("${spring.profiles.active}")
    private String environment;

    public void gererAccesAvecBouchon(Individu individu, UserInfoPEIO userInfo) {
            individu.setIdPoleEmploi(userInfo.getSub());
            individu.setPopulationAutorisee(isPopulationAutorisee(userInfo));
            String population = userInfo.getGivenName().substring(userInfo.getGivenName().length() - 3);
            addInfosIndemnisation(individu, population);
    }

    public boolean isStagingEnvironnement() {
        return environment.equals(Environnements.LOCALHOST.getLibelle()) || 
                environment.equals(Environnements.RECETTE.getLibelle());
    }
    
    private void addInfosIndemnisation(Individu individu, String population) {

        switch (population) {
        case "AAH":
            individu.setBeneficiaireAides(creerBouchonBeneficiaireAides(true, false, false, false));            
            break;
        case "ARE":
            individu.setBeneficiaireAides(creerBouchonBeneficiaireAides(false, true, false, false));
            break;
        case "ASS":
            individu.setBeneficiaireAides(creerBouchonBeneficiaireAides(false, false, true, false));
            individu.setRessourcesFinancieres(creerBouchonRessourcesFinancieresASS());
            break;
        case "RSA":
            individu.setBeneficiaireAides(creerBouchonBeneficiaireAides(false, false, false, true));
            break;
        default:
            individu.setBeneficiaireAides(creerBouchonBeneficiaireAides(false, false, false, false)); 
            break;
        }
    }

    private BeneficiaireAides creerBouchonBeneficiaireAides(boolean beneficiaireAAH, boolean beneficiaireARE, boolean beneficiaireASS, boolean beneficiaireRSA) {
        BeneficiaireAides beneficiaire = new BeneficiaireAides();
        beneficiaire.setBeneficiaireAAH(beneficiaireAAH);
        beneficiaire.setBeneficiaireASS(beneficiaireASS);
        beneficiaire.setBeneficiaireARE(beneficiaireARE);
        beneficiaire.setBeneficiaireRSA(beneficiaireRSA);
        return beneficiaire;
    }

    private RessourcesFinancieres creerBouchonRessourcesFinancieresASS() {
        RessourcesFinancieres ressourcesFinancieres = creerBouchonRessourcesFinancieres();
        ressourcesFinancieres.setAidesPoleEmploi(creerBouchonAidesPoleEmploiAvecASS(16.89f));
        return ressourcesFinancieres;
    }

    private AidesPoleEmploi creerBouchonAidesPoleEmploiAvecASS(float montant) {
        AidesPoleEmploi aidesPoleEmploi = new AidesPoleEmploi();
        AllocationASS allocationASS = new AllocationASS();
        allocationASS.setAllocationJournaliereNet(montant);
        aidesPoleEmploi.setAllocationASS(allocationASS);
        return aidesPoleEmploi;
    }

    private RessourcesFinancieres creerBouchonRessourcesFinancieres() {
        RessourcesFinancieres ressourcesFinancieres = new RessourcesFinancieres();
        ressourcesFinancieres.setNombreMoisTravaillesDerniersMois(0);
        creerBouchonAllocationCAF(ressourcesFinancieres);
        creerBouchonAllocationCPAM(ressourcesFinancieres);
        return ressourcesFinancieres;
    }

    private void creerBouchonAllocationCAF(RessourcesFinancieres ressourcesFinancieres) {
        AidesCAF aidesCAF = new AidesCAF();
        aidesCAF.setAidesLogement(creerBouconAidesLogement());
        ressourcesFinancieres.setAidesCAF(aidesCAF);
    }
    
    private AidesLogement creerBouconAidesLogement() {
        AidesLogement aidesLogement = new AidesLogement();
        aidesLogement.setAidePersonnaliseeLogement(creerBouchonAllocationsLogement());
        aidesLogement.setAllocationLogementFamiliale(creerBouchonAllocationsLogement());
        aidesLogement.setAllocationLogementSociale(creerBouchonAllocationsLogement());
        return aidesLogement;
    }

    private AllocationsLogement creerBouchonAllocationsLogement() {
        AllocationsLogement allocationsLogement = new AllocationsLogement();
        allocationsLogement.setMoisNMoins1(0);
        allocationsLogement.setMoisNMoins2(0);
        allocationsLogement.setMoisNMoins3(0);
        return allocationsLogement;
    }
    
    private void creerBouchonAllocationCPAM(RessourcesFinancieres ressourcesFinancieres) {
        AidesCPAM aidesCPAM = new AidesCPAM();
        aidesCPAM.setAllocationSupplementaireInvalidite(0f);
        ressourcesFinancieres.setAidesCPAM(aidesCPAM);
    }
    
    private boolean isPopulationAutorisee(UserInfoPEIO userInfoESD) {
        return userInfoESD.getGivenName().endsWith(Aides.ALLOCATION_SOLIDARITE_SPECIFIQUE.getCode()) 
                || userInfoESD.getGivenName().endsWith(Aides.ALLOCATION_ADULTES_HANDICAPES.getCode()) 
                || userInfoESD.getGivenName().endsWith(Aides.RSA.getCode());
    }
}
