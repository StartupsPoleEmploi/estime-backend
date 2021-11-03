package fr.poleemploi.estime.commun.utile;

import org.springframework.stereotype.Component;

import fr.poleemploi.estime.clientsexternes.poleemploiio.ressources.CoordonneesESD;
import fr.poleemploi.estime.clientsexternes.poleemploiio.ressources.DetailIndemnisationESD;
import fr.poleemploi.estime.commun.enumerations.Aides;
import fr.poleemploi.estime.commun.enumerations.TypePopulation;
import fr.poleemploi.estime.services.ressources.AidesPoleEmploi;
import fr.poleemploi.estime.services.ressources.AllocationARE;
import fr.poleemploi.estime.services.ressources.AllocationASS;
import fr.poleemploi.estime.services.ressources.BeneficiaireAides;
import fr.poleemploi.estime.services.ressources.Individu;
import fr.poleemploi.estime.services.ressources.InformationsPersonnelles;
import fr.poleemploi.estime.services.ressources.RessourcesFinancieres;

@Component
public class IndividuUtile {

    /**
     * Population autorisée ASS, RSA ou AAH, et pouvant cumuler ces 3 prestations.
     */
    public boolean isPopulationAutorisee(DetailIndemnisationESD detailIndemnisationESD) {
        return !isBeneficiaireARE(detailIndemnisationESD) && 
                (isBeneficiaireASS(detailIndemnisationESD) || 
                 detailIndemnisationESD.isBeneficiaireAAH() || 
                 detailIndemnisationESD.isBeneficiaireRSA());
    }

    public void addInformationsDetailIndemnisationPoleEmploi(Individu individu, DetailIndemnisationESD detailIndemnisationESD, CoordonneesESD coordonneesESD) {
        addInformationsBeneficiaireAides(individu, detailIndemnisationESD);
        addInformationsRessourcesFinancieresPoleEmploi(individu, detailIndemnisationESD);
        addInformationsPersonnelles(individu, coordonneesESD);
    }

    private void addInformationsBeneficiaireAides(Individu individu, DetailIndemnisationESD detailIndemnisation) {
        BeneficiaireAides beneficiaireAides = new BeneficiaireAides();
        beneficiaireAides.setBeneficiaireAAH(detailIndemnisation.isBeneficiaireAAH());
        beneficiaireAides.setBeneficiaireARE(isBeneficiaireARE(detailIndemnisation));
        beneficiaireAides.setBeneficiaireASS(isBeneficiaireASS(detailIndemnisation));
        beneficiaireAides.setBeneficiaireRSA(detailIndemnisation.isBeneficiaireRSA());
        individu.setBeneficiaireAides(beneficiaireAides);
    }

    private void addInformationsRessourcesFinancieresPoleEmploi(Individu individu, DetailIndemnisationESD detailIndemnisation) {
        RessourcesFinancieres ressourcesFinancieres = new RessourcesFinancieres();       
        if(detailIndemnisation.getCodeIndemnisation() != null) {
            ressourcesFinancieres.setAidesPoleEmploi(creerAidePoleEmploi(detailIndemnisation));
        }
        individu.setRessourcesFinancieres(ressourcesFinancieres);
    }

    private void addInformationsPersonnelles(Individu individu, CoordonneesESD coordonneesESD) {
        InformationsPersonnelles informationsPersonnelles = new InformationsPersonnelles();       
        if(coordonneesESD.getCodePostal() != null) {
            informationsPersonnelles.setCodePostal(coordonneesESD.getCodePostal());
        }
        individu.setInformationsPersonnelles(informationsPersonnelles);
    }

    private AidesPoleEmploi creerAidePoleEmploi(DetailIndemnisationESD detailIndemnisation) {
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

   

    private boolean isBeneficiaireARE(DetailIndemnisationESD detailIndemnisationESD) {
        return detailIndemnisationESD.getCodeIndemnisation() != null && TypePopulation.ARE.getLibelle().equals(detailIndemnisationESD.getCodeIndemnisation());
    }

    private boolean isBeneficiaireASS(DetailIndemnisationESD detailIndemnisationESD) {
        return detailIndemnisationESD.getCodeIndemnisation() != null && TypePopulation.ASS.getLibelle().equals(detailIndemnisationESD.getCodeIndemnisation());
    }

}
