package fr.poleemploi.estime.commun.utile;

import org.springframework.stereotype.Component;

import fr.poleemploi.estime.clientsexternes.poleemploiio.ressources.DetailIndemnisationPEIO;
import fr.poleemploi.estime.commun.enumerations.Aides;
import fr.poleemploi.estime.commun.enumerations.TypePopulation;
import fr.poleemploi.estime.services.ressources.AidesPoleEmploi;
import fr.poleemploi.estime.services.ressources.AllocationARE;
import fr.poleemploi.estime.services.ressources.AllocationASS;
import fr.poleemploi.estime.services.ressources.BeneficiaireAides;
import fr.poleemploi.estime.services.ressources.Individu;
import fr.poleemploi.estime.services.ressources.RessourcesFinancieres;

@Component
public class IndividuUtile {

    /**
     * Population autorisée ASS, RSA ou AAH, et pouvant cumuler ces 3 prestations.
     */
    public boolean isPopulationAutorisee(DetailIndemnisationPEIO detailIndemnisationESD) {
        return !isBeneficiaireARE(detailIndemnisationESD) && 
                (isBeneficiaireASS(detailIndemnisationESD) || 
                 detailIndemnisationESD.isBeneficiaireAAH() || 
                 detailIndemnisationESD.isBeneficiaireRSA());
    }

    public void addInformationsDetailIndemnisationPoleEmploi(Individu individu, DetailIndemnisationPEIO detailIndemnisationESD) {
        addInformationsBeneficiaireAides(individu, detailIndemnisationESD);
        addInformationsRessourcesFinancieresPoleEmploi(individu, detailIndemnisationESD);
    }

    private void addInformationsBeneficiaireAides(Individu individu, DetailIndemnisationPEIO detailIndemnisation) {
        BeneficiaireAides beneficiaireAides = new BeneficiaireAides();
        beneficiaireAides.setBeneficiaireAAH(detailIndemnisation.isBeneficiaireAAH());
        beneficiaireAides.setBeneficiaireARE(isBeneficiaireARE(detailIndemnisation));
        beneficiaireAides.setBeneficiaireASS(isBeneficiaireASS(detailIndemnisation));
        beneficiaireAides.setBeneficiaireRSA(detailIndemnisation.isBeneficiaireRSA());
        individu.setBeneficiaireAides(beneficiaireAides);
    }

    private void addInformationsRessourcesFinancieresPoleEmploi(Individu individu, DetailIndemnisationPEIO detailIndemnisation) {
        RessourcesFinancieres ressourcesFinancieres = new RessourcesFinancieres();       
        if(detailIndemnisation.getCodeIndemnisation() != null) {
            ressourcesFinancieres.setAidesPoleEmploi(creerAidePoleEmploi(detailIndemnisation));
        }
        individu.setRessourcesFinancieres(ressourcesFinancieres);
    }

    private AidesPoleEmploi creerAidePoleEmploi(DetailIndemnisationPEIO detailIndemnisation) {
        AidesPoleEmploi aidesPoleEmploi = new AidesPoleEmploi();
        if(Aides.ALLOCATION_SOLIDARITE_SPECIFIQUE.getCode().equals(detailIndemnisation.getCodeIndemnisation())) {
            AllocationASS allocationASS = new AllocationASS();
            allocationASS.setAllocationJournaliereNet(detailIndemnisation.getIndemnisationJournalierNet());  
            aidesPoleEmploi.setAllocationASS(allocationASS);
        }
        if(Aides.ALLOCATION_RETOUR_EMPLOI.getCode().equals(detailIndemnisation.getCodeIndemnisation())) {
            AllocationARE allocationARE = new AllocationARE();
            allocationARE.setAllocationJournaliereNet(detailIndemnisation.getIndemnisationJournalierNet());  
            aidesPoleEmploi.setAllocationARE(allocationARE);
        }         
        return aidesPoleEmploi;
    }

   

    private boolean isBeneficiaireARE(DetailIndemnisationPEIO detailIndemnisationESD) {
        return detailIndemnisationESD.getCodeIndemnisation() != null && TypePopulation.ARE.getLibelle().equals(detailIndemnisationESD.getCodeIndemnisation());
    }

    private boolean isBeneficiaireASS(DetailIndemnisationPEIO detailIndemnisationESD) {
        return detailIndemnisationESD.getCodeIndemnisation() != null && TypePopulation.ASS.getLibelle().equals(detailIndemnisationESD.getCodeIndemnisation());
    }

}
