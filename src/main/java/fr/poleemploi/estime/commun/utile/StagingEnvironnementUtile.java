package fr.poleemploi.estime.commun.utile;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import fr.poleemploi.estime.clientsexternes.emploistoredev.ressources.UserInfoESD;
import fr.poleemploi.estime.commun.enumerations.AidesSociales;
import fr.poleemploi.estime.commun.enumerations.Environnements;
import fr.poleemploi.estime.services.ressources.AllocationsCAF;
import fr.poleemploi.estime.services.ressources.AllocationsCPAM;
import fr.poleemploi.estime.services.ressources.AllocationsLogementMensuellesNetFoyer;
import fr.poleemploi.estime.services.ressources.AllocationsPoleEmploi;
import fr.poleemploi.estime.services.ressources.BeneficiaireAidesSociales;
import fr.poleemploi.estime.services.ressources.Individu;
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
            individu.setBeneficiaireAidesSociales(creerBouchonBeneficiaireAidesSociales(true, false, false, false));            
            break;
        case "ARE":
            individu.setBeneficiaireAidesSociales(creerBouchonBeneficiaireAidesSociales(false, true, false, false));
            break;
        case "ASS":
            individu.setBeneficiaireAidesSociales(creerBouchonBeneficiaireAidesSociales(false, false, true, false));
            individu.setRessourcesFinancieres(creerBouchonRessourcesFinancieresASS());
            break;
        case "RSA":
            individu.setBeneficiaireAidesSociales(creerBouchonBeneficiaireAidesSociales(false, false, false, true));
            break;
        default:
            individu.setBeneficiaireAidesSociales(creerBouchonBeneficiaireAidesSociales(false, false, false, false)); 
            break;
        }
    }

    private BeneficiaireAidesSociales creerBouchonBeneficiaireAidesSociales(boolean beneficiaireAAH, boolean beneficiaireARE, boolean beneficiaireASS, boolean beneficiaireRSA) {
        BeneficiaireAidesSociales beneficiaireAidesSociales = new BeneficiaireAidesSociales();
        beneficiaireAidesSociales.setBeneficiaireAAH(beneficiaireAAH);
        beneficiaireAidesSociales.setBeneficiaireASS(beneficiaireASS);
        beneficiaireAidesSociales.setBeneficiaireARE(beneficiaireARE);
        beneficiaireAidesSociales.setBeneficiaireRSA(beneficiaireRSA);
        beneficiaireAidesSociales.setTopAAHRecupererViaApiPoleEmploi(beneficiaireAAH);
        beneficiaireAidesSociales.setTopARERecupererViaApiPoleEmploi(beneficiaireARE);
        beneficiaireAidesSociales.setTopASSRecupererViaApiPoleEmploi(beneficiaireASS);
        beneficiaireAidesSociales.setTopRSARecupererViaApiPoleEmploi(beneficiaireRSA);
        return beneficiaireAidesSociales;
    }

    private RessourcesFinancieres creerBouchonRessourcesFinancieresASS() {
        RessourcesFinancieres ressourcesFinancieres = creerBouchonRessourcesFinancieres();
        ressourcesFinancieres.setAllocationsPoleEmploi(creerBouchonAllocationsPoleEmploi(16.89f));
        return ressourcesFinancieres;
    }

    private AllocationsPoleEmploi creerBouchonAllocationsPoleEmploi(float montant) {
        AllocationsPoleEmploi allocationsPoleEmploi = new AllocationsPoleEmploi();
        allocationsPoleEmploi.setAllocationJournaliereNet(montant);
        return allocationsPoleEmploi;
    }

    private RessourcesFinancieres creerBouchonRessourcesFinancieres() {
        RessourcesFinancieres ressourcesFinancieres = new RessourcesFinancieres();
        ressourcesFinancieres.setNombreMoisTravaillesDerniersMois(0);
        creerBouchonAllocationCAF(ressourcesFinancieres);
        creerBouchonAllocationCPAM(ressourcesFinancieres);
        return ressourcesFinancieres;
    }

    private void creerBouchonAllocationCAF(RessourcesFinancieres ressourcesFinancieres) {
        AllocationsCAF allocationsCAF = new AllocationsCAF();
        allocationsCAF.setAllocationsFamilialesMensuellesNetFoyer(0);
        allocationsCAF.setAllocationsLogementMensuellesNetFoyer(creerBouchonAllocationsLogement());
        allocationsCAF.setPensionsAlimentairesFoyer(0);
        allocationsCAF.setPrestationAccueilJeuneEnfant(0);
        ressourcesFinancieres.setAllocationsCAF(allocationsCAF);
    }

    private AllocationsLogementMensuellesNetFoyer creerBouchonAllocationsLogement() {
        AllocationsLogementMensuellesNetFoyer allocationsLogementMensuellesNetFoyer = new AllocationsLogementMensuellesNetFoyer();
        allocationsLogementMensuellesNetFoyer.setMoisNMoins1(0);
        allocationsLogementMensuellesNetFoyer.setMoisNMoins2(0);
        allocationsLogementMensuellesNetFoyer.setMoisNMoins3(0);
        return allocationsLogementMensuellesNetFoyer;
    }
    
    private void creerBouchonAllocationCPAM(RessourcesFinancieres ressourcesFinancieres) {
        AllocationsCPAM allocationsCPAM = new AllocationsCPAM();
        allocationsCPAM.setAllocationSupplementaireInvalidite(0f);
        ressourcesFinancieres.setAllocationsCPAM(allocationsCPAM);
    }
    
    private boolean isPopulationAutorisee(UserInfoESD userInfoESD) {
        return userInfoESD.getGivenName().endsWith(AidesSociales.ALLOCATION_SOLIDARITE_SPECIFIQUE.getCode()) 
                || userInfoESD.getGivenName().endsWith(AidesSociales.ALLOCATION_ADULTES_HANDICAPES.getCode()) 
                || userInfoESD.getGivenName().endsWith(AidesSociales.RSA.getCode());
    }
}
