package fr.poleemploi.estime.commun.utile;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import fr.poleemploi.estime.clientsexternes.poleemploiio.ressources.UserInfoESD;
import fr.poleemploi.estime.commun.enumerations.Environnements;
import fr.poleemploi.estime.commun.enumerations.PrestationsSociales;
import fr.poleemploi.estime.services.ressources.AllocationASS;
import fr.poleemploi.estime.services.ressources.AllocationsLogementMensuellesNetFoyer;
import fr.poleemploi.estime.services.ressources.BeneficiairePrestationsSociales;
import fr.poleemploi.estime.services.ressources.Individu;
import fr.poleemploi.estime.services.ressources.PrestationsCAF;
import fr.poleemploi.estime.services.ressources.PrestationsCPAM;
import fr.poleemploi.estime.services.ressources.PrestationsPoleEmploi;
import fr.poleemploi.estime.services.ressources.RessourcesFinancieres;

@Component
public class StagingEnvironnementUtile {

    @Value("${spring.profiles.active}")
    private String environment;

    public void gererAccesAvecBouchon(Individu individu, UserInfoESD userInfo) {
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
            individu.setBeneficiairePrestationsSociales(creerBouchonBeneficiairePrestationsSociales(true, false, false, false));            
            break;
        case "ARE":
            individu.setBeneficiairePrestationsSociales(creerBouchonBeneficiairePrestationsSociales(false, true, false, false));
            break;
        case "ASS":
            individu.setBeneficiairePrestationsSociales(creerBouchonBeneficiairePrestationsSociales(false, false, true, false));
            individu.setRessourcesFinancieres(creerBouchonRessourcesFinancieresASS());
            break;
        case "RSA":
            individu.setBeneficiairePrestationsSociales(creerBouchonBeneficiairePrestationsSociales(false, false, false, true));
            break;
        default:
            individu.setBeneficiairePrestationsSociales(creerBouchonBeneficiairePrestationsSociales(false, false, false, false)); 
            break;
        }
    }

    private BeneficiairePrestationsSociales creerBouchonBeneficiairePrestationsSociales(boolean beneficiaireAAH, boolean beneficiaireARE, boolean beneficiaireASS, boolean beneficiaireRSA) {
        BeneficiairePrestationsSociales beneficiaire = new BeneficiairePrestationsSociales();
        beneficiaire.setBeneficiaireAAH(beneficiaireAAH);
        beneficiaire.setBeneficiaireASS(beneficiaireASS);
        beneficiaire.setBeneficiaireARE(beneficiaireARE);
        beneficiaire.setBeneficiaireRSA(beneficiaireRSA);
        beneficiaire.setTopAAHRecupererViaApiPoleEmploi(beneficiaireAAH);
        beneficiaire.setTopARERecupererViaApiPoleEmploi(beneficiaireARE);
        beneficiaire.setTopASSRecupererViaApiPoleEmploi(beneficiaireASS);
        beneficiaire.setTopRSARecupererViaApiPoleEmploi(beneficiaireRSA);
        return beneficiaire;
    }

    private RessourcesFinancieres creerBouchonRessourcesFinancieresASS() {
        RessourcesFinancieres ressourcesFinancieres = creerBouchonRessourcesFinancieres();
        ressourcesFinancieres.setPrestationsPoleEmploi(creerBouchonPrestationsPoleEmploiAvecASS(16.89f));
        return ressourcesFinancieres;
    }

    private PrestationsPoleEmploi creerBouchonPrestationsPoleEmploiAvecASS(float montant) {
        PrestationsPoleEmploi prestationsPoleEmploi = new PrestationsPoleEmploi();
        AllocationASS allocationASS = new AllocationASS();
        allocationASS.setAllocationJournaliereNet(montant);
        prestationsPoleEmploi.setAllocationASS(allocationASS);
        return prestationsPoleEmploi;
    }

    private RessourcesFinancieres creerBouchonRessourcesFinancieres() {
        RessourcesFinancieres ressourcesFinancieres = new RessourcesFinancieres();
        ressourcesFinancieres.setNombreMoisTravaillesDerniersMois(0);
        creerBouchonAllocationCAF(ressourcesFinancieres);
        creerBouchonAllocationCPAM(ressourcesFinancieres);
        return ressourcesFinancieres;
    }

    private void creerBouchonAllocationCAF(RessourcesFinancieres ressourcesFinancieres) {
        PrestationsCAF prestationsCAF = new PrestationsCAF();
        prestationsCAF.setAllocationsLogementMensuellesNetFoyer(creerBouchonAllocationsLogement());
        ressourcesFinancieres.setPrestationsCAF(prestationsCAF);
    }

    private AllocationsLogementMensuellesNetFoyer creerBouchonAllocationsLogement() {
        AllocationsLogementMensuellesNetFoyer allocationsLogementMensuellesNetFoyer = new AllocationsLogementMensuellesNetFoyer();
        allocationsLogementMensuellesNetFoyer.setMoisNMoins1(0);
        allocationsLogementMensuellesNetFoyer.setMoisNMoins2(0);
        allocationsLogementMensuellesNetFoyer.setMoisNMoins3(0);
        return allocationsLogementMensuellesNetFoyer;
    }
    
    private void creerBouchonAllocationCPAM(RessourcesFinancieres ressourcesFinancieres) {
        PrestationsCPAM prestationsCPAM = new PrestationsCPAM();
        prestationsCPAM.setAllocationSupplementaireInvalidite(0f);
        ressourcesFinancieres.setPrestationsCPAM(prestationsCPAM);
    }
    
    private boolean isPopulationAutorisee(UserInfoESD userInfoESD) {
        return userInfoESD.getGivenName().endsWith(PrestationsSociales.ALLOCATION_SOLIDARITE_SPECIFIQUE.getCode()) 
                || userInfoESD.getGivenName().endsWith(PrestationsSociales.ALLOCATION_ADULTES_HANDICAPES.getCode()) 
                || userInfoESD.getGivenName().endsWith(PrestationsSociales.RSA.getCode());
    }
}
