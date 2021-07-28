package fr.poleemploi.estime.commun.utile;

import org.springframework.stereotype.Component;

import fr.poleemploi.estime.clientsexternes.emploistoredev.ressources.DetailIndemnisationESD;
import fr.poleemploi.estime.commun.enumerations.TypePopulation;
import fr.poleemploi.estime.services.ressources.AllocationsCAF;
import fr.poleemploi.estime.services.ressources.AllocationsCPAM;
import fr.poleemploi.estime.services.ressources.AllocationsLogementMensuellesNetFoyer;
import fr.poleemploi.estime.services.ressources.AllocationsPoleEmploi;
import fr.poleemploi.estime.services.ressources.BeneficiaireAidesSociales;
import fr.poleemploi.estime.services.ressources.Individu;
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

        if((detailIndemnisation.isBeneficiaireAssuranceChomage() || detailIndemnisation.isBeneficiairePrestationSolidarite())) {
            ressourcesFinancieres.setAllocationsPoleEmploi(creerAllocationsPoleEmploi(detailIndemnisation));
        }
        ressourcesFinancieres.setAllocationsCAF(creerAllocationCAF());
        ressourcesFinancieres.setAllocationsCPAM(creerAllocationsCPAM());

        individu.setRessourcesFinancieres(ressourcesFinancieres);
    }

    private AllocationsPoleEmploi creerAllocationsPoleEmploi(DetailIndemnisationESD detailIndemnisation) {
        AllocationsPoleEmploi allocationsPoleEmploi = new AllocationsPoleEmploi();
        allocationsPoleEmploi.setAllocationJournaliereNet(detailIndemnisation.getIndemnisationJournalierNet());             
        return allocationsPoleEmploi;
    }

    private AllocationsCAF creerAllocationCAF() {
        AllocationsCAF allocationsCAF = new AllocationsCAF();
        allocationsCAF.setAllocationsFamilialesMensuellesNetFoyer(0);
        allocationsCAF.setAllocationsLogementMensuellesNetFoyer(creerAllocationsLogement());
        allocationsCAF.setPensionsAlimentairesFoyer(0);
        allocationsCAF.setPrestationAccueilJeuneEnfant(0);
        return allocationsCAF;
    }

    private AllocationsLogementMensuellesNetFoyer creerAllocationsLogement() {
        AllocationsLogementMensuellesNetFoyer allocationsLogementMensuellesNetFoyer = new AllocationsLogementMensuellesNetFoyer();
        allocationsLogementMensuellesNetFoyer.setMoisNMoins1(0);
        allocationsLogementMensuellesNetFoyer.setMoisNMoins2(0);
        allocationsLogementMensuellesNetFoyer.setMoisNMoins3(0);
        return allocationsLogementMensuellesNetFoyer;
    }

    private AllocationsCPAM creerAllocationsCPAM() {
        AllocationsCPAM allocationsCPAM = new AllocationsCPAM();
        allocationsCPAM.setAllocationSupplementaireInvalidite(0f);
        return allocationsCPAM;
    }

    private boolean isBeneficiaireARE(DetailIndemnisationESD detailIndemnisationESD) {
        return detailIndemnisationESD.getCodeIndemnisation() != null && TypePopulation.ARE.getLibelle().equals(detailIndemnisationESD.getCodeIndemnisation());
    }

    private boolean isBeneficiaireASS(DetailIndemnisationESD detailIndemnisationESD) {
        return detailIndemnisationESD.getCodeIndemnisation() != null && TypePopulation.ASS.getLibelle().equals(detailIndemnisationESD.getCodeIndemnisation());
    }

}
