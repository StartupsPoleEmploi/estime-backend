package fr.poleemploi.estime.commun.utile;

import org.springframework.stereotype.Component;

import fr.poleemploi.estime.clientsexternes.emploistoredev.ressources.DetailIndemnisationESD;
import fr.poleemploi.estime.commun.enumerations.AidesSociales;
import fr.poleemploi.estime.commun.enumerations.TypePopulation;
import fr.poleemploi.estime.services.ressources.AllocationARE;
import fr.poleemploi.estime.services.ressources.AllocationASS;
import fr.poleemploi.estime.services.ressources.AllocationsLogementMensuellesNetFoyer;
import fr.poleemploi.estime.services.ressources.BeneficiaireAidesSociales;
import fr.poleemploi.estime.services.ressources.Individu;
import fr.poleemploi.estime.services.ressources.PrestationsCAF;
import fr.poleemploi.estime.services.ressources.PrestationsCPAM;
import fr.poleemploi.estime.services.ressources.PrestationsPoleEmploi;
import fr.poleemploi.estime.services.ressources.RessourcesFinancieres;

@Component
public class IndividuUtile {

    /**
     * Population autorisée ASS, RSA ou AAH, et pouvant cumuler ces 3 aides.
     */
    public boolean isPopulationAutorisee(DetailIndemnisationESD detailIndemnisationESD) {
        return !isBeneficiaireARE(detailIndemnisationESD) && 
                (isBeneficiaireASS(detailIndemnisationESD) || 
                 detailIndemnisationESD.isBeneficiaireAAH() || 
                 detailIndemnisationESD.isBeneficiaireRSA());
    }

    public void addInformationsDetailIndemnisationPoleEmploi(Individu individu, DetailIndemnisationESD detailIndemnisationESD) {
        addInformationsBeneficiaireAidesSociales(individu, detailIndemnisationESD);
        addInformationsRessourcesFinancieresPoleEmploi(individu, detailIndemnisationESD);
    }

    private void addInformationsBeneficiaireAidesSociales(Individu individu, DetailIndemnisationESD detailIndemnisation) {
        BeneficiaireAidesSociales beneficiaireAidesSociales = new BeneficiaireAidesSociales();
        beneficiaireAidesSociales.setBeneficiaireAAH(detailIndemnisation.isBeneficiaireAAH());
        beneficiaireAidesSociales.setBeneficiaireARE(isBeneficiaireARE(detailIndemnisation));
        beneficiaireAidesSociales.setBeneficiaireASS(isBeneficiaireASS(detailIndemnisation));
        beneficiaireAidesSociales.setBeneficiaireRSA(detailIndemnisation.isBeneficiaireRSA());
        beneficiaireAidesSociales.setTopAAHRecupererViaApiPoleEmploi(detailIndemnisation.isBeneficiaireAAH());
        beneficiaireAidesSociales.setTopARERecupererViaApiPoleEmploi(detailIndemnisation.isBeneficiaireAssuranceChomage());
        beneficiaireAidesSociales.setTopASSRecupererViaApiPoleEmploi(detailIndemnisation.isBeneficiairePrestationSolidarite());
        beneficiaireAidesSociales.setTopRSARecupererViaApiPoleEmploi(detailIndemnisation.isBeneficiaireRSA());
        individu.setBeneficiaireAidesSociales(beneficiaireAidesSociales);
    }

    private void addInformationsRessourcesFinancieresPoleEmploi(Individu individu, DetailIndemnisationESD detailIndemnisation) {
        RessourcesFinancieres ressourcesFinancieres = new RessourcesFinancieres();
        ressourcesFinancieres.setNombreMoisTravaillesDerniersMois(0);

        if(detailIndemnisation.getCodeIndemnisation() != null) {
            ressourcesFinancieres.setPrestationsPoleEmploi(creerPrestationPoleEmploi(detailIndemnisation));
        }
        ressourcesFinancieres.setPrestationsCAF(creerPrestationsCAF());
        ressourcesFinancieres.setPrestationsCPAM(creerPrestationssCPAM());

        individu.setRessourcesFinancieres(ressourcesFinancieres);
    }

    private PrestationsPoleEmploi creerPrestationPoleEmploi(DetailIndemnisationESD detailIndemnisation) {
        PrestationsPoleEmploi prestationsPoleEmploi = new PrestationsPoleEmploi();
        if(AidesSociales.ALLOCATION_SOLIDARITE_SPECIFIQUE.getCode().equals(detailIndemnisation.getCodeIndemnisation())) {
            AllocationASS allocationASS = new AllocationASS();
            allocationASS.setAllocationJournaliereNet(detailIndemnisation.getIndemnisationJournalierNet());  
            prestationsPoleEmploi.setAllocationASS(allocationASS);
        }
        if(AidesSociales.ALLOCATION_RETOUR_EMPLOI.getCode().equals(detailIndemnisation.getCodeIndemnisation())) {
            AllocationARE allocationARE = new AllocationARE();
            allocationARE.setAllocationJournaliereNet(detailIndemnisation.getIndemnisationJournalierNet());  
            prestationsPoleEmploi.setAllocationARE(allocationARE);
        }         
        return prestationsPoleEmploi;
    }

    private PrestationsCAF creerPrestationsCAF() {
        PrestationsCAF prestationsCAF = new PrestationsCAF();        
        prestationsCAF.setAllocationsLogementMensuellesNetFoyer(creerAllocationsLogement());
        return prestationsCAF;
    }

    private AllocationsLogementMensuellesNetFoyer creerAllocationsLogement() {
        AllocationsLogementMensuellesNetFoyer allocationsLogementMensuellesNetFoyer = new AllocationsLogementMensuellesNetFoyer();
        allocationsLogementMensuellesNetFoyer.setMoisNMoins1(0);
        allocationsLogementMensuellesNetFoyer.setMoisNMoins2(0);
        allocationsLogementMensuellesNetFoyer.setMoisNMoins3(0);
        return allocationsLogementMensuellesNetFoyer;
    }

    private PrestationsCPAM creerPrestationssCPAM() {
        PrestationsCPAM prestationsCPAM = new PrestationsCPAM();
        prestationsCPAM.setAllocationSupplementaireInvalidite(0f);
        return prestationsCPAM;
    }

    private boolean isBeneficiaireARE(DetailIndemnisationESD detailIndemnisationESD) {
        return detailIndemnisationESD.getCodeIndemnisation() != null && TypePopulation.ARE.getLibelle().equals(detailIndemnisationESD.getCodeIndemnisation());
    }

    private boolean isBeneficiaireASS(DetailIndemnisationESD detailIndemnisationESD) {
        return detailIndemnisationESD.getCodeIndemnisation() != null && TypePopulation.ASS.getLibelle().equals(detailIndemnisationESD.getCodeIndemnisation());
    }

}
