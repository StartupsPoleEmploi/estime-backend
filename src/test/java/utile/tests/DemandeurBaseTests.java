package utile.tests;

import java.text.ParseException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fr.poleemploi.estime.clientsexternes.emploistoredev.ressources.DetailIndemnisationESD;
import fr.poleemploi.estime.services.ressources.AllocationsCAF;
import fr.poleemploi.estime.services.ressources.AllocationsLogementMensuellesNetFoyer;
import fr.poleemploi.estime.services.ressources.AllocationsPoleEmploi;
import fr.poleemploi.estime.services.ressources.BeneficiaireAidesSociales;
import fr.poleemploi.estime.services.ressources.DemandeurEmploi;
import fr.poleemploi.estime.services.ressources.FuturTravail;
import fr.poleemploi.estime.services.ressources.InformationsPersonnelles;
import fr.poleemploi.estime.services.ressources.RessourcesFinancieres;
import fr.poleemploi.estime.services.ressources.Salaire;
import fr.poleemploi.estime.services.ressources.SituationFamiliale;

@Component
public class DemandeurBaseTests {
    
    public DemandeurEmploi creerBaseDemandeurEmploi(String population) throws ParseException {
        DemandeurEmploi demandeurEmploi = new DemandeurEmploi();

        InformationsPersonnelles informationsPersonnelles = new InformationsPersonnelles();
        informationsPersonnelles.setNom("DUPONT");
        informationsPersonnelles.setPrenom("DANIEL");
        demandeurEmploi.setInformationsPersonnelles(informationsPersonnelles);
        
        demandeurEmploi.setBeneficiaireAidesSociales(creerBeneficiaireAidesSociales(population));

        SituationFamiliale situationFamiliale = new SituationFamiliale();
        demandeurEmploi.setSituationFamiliale(situationFamiliale);

        FuturTravail futurTravail = new FuturTravail();
        Salaire salaire = new Salaire();
        futurTravail.setSalaire(salaire);
        demandeurEmploi.setFuturTravail(futurTravail);

        RessourcesFinancieres ressourcesFinancieres = new RessourcesFinancieres();
        initRessourcesFinancieres(ressourcesFinancieres, population);
        demandeurEmploi.setRessourcesFinancieres(ressourcesFinancieres); 

        return demandeurEmploi;
    }
    
    public BeneficiaireAidesSociales creerBeneficiaireAidesSociales(String population) {
        switch (population) {
        case "AAH":
            return creerBeneficiaireAidesSociales(true, false, false, false);    
        case "ARE":
            return creerBeneficiaireAidesSociales(false, true, false, false);
        case "ASS":
            return creerBeneficiaireAidesSociales(false, false, true, false);
        case "RSA":
            return creerBeneficiaireAidesSociales(false, false, false, true);
        default:
            return null;
        }
    }
    
    public DetailIndemnisationESD creerDetailIndemnisationESD(String population) {
        switch (population) {
        case "AAH":
            return creerDetailIndemnisationESD(true, false, false, false);    
        case "ARE":
            return creerDetailIndemnisationESD(false, true, false, false);
        case "ASS":
            return creerDetailIndemnisationESD(false, false, true, false);
        case "RSA":
            return creerDetailIndemnisationESD(false, false, false, true);
        default:
            return null;
        }
    }
    
    public AllocationsLogementMensuellesNetFoyer creerAllocationsLogementMensuellesNetFoyer(float montant) {
        AllocationsLogementMensuellesNetFoyer apl = new AllocationsLogementMensuellesNetFoyer();
        apl.setMoisN(montant);
        apl.setMoisNMoins1(montant);
        apl.setMoisNMoins2(montant);
        apl.setMoisNMoins3(montant);
        return apl;
    }
    
    private void initRessourcesFinancieres(RessourcesFinancieres ressourcesFinancieres, String population) {
        switch (population) {
        case "AAH" :
        case "RSA":
            AllocationsCAF allocationsCAF = new AllocationsCAF();
            ressourcesFinancieres.setAllocationsCAF(allocationsCAF);
            break;
        case "ARE":
        case "ASS":
            AllocationsPoleEmploi allocationsPoleEmploi = new AllocationsPoleEmploi();
            ressourcesFinancieres.setAllocationsPoleEmploi(allocationsPoleEmploi);
            break;
        default:
            break;
        }        
    }

    private BeneficiaireAidesSociales creerBeneficiaireAidesSociales(boolean beneficiaireAAH, boolean beneficiaireARE, boolean beneficiaireASS, boolean beneficiaireRSA) {
        BeneficiaireAidesSociales beneficiaireAidesSociales = new BeneficiaireAidesSociales();
        beneficiaireAidesSociales.setBeneficiaireAAH(beneficiaireAAH);
        beneficiaireAidesSociales.setBeneficiaireASS(beneficiaireASS);
        beneficiaireAidesSociales.setBeneficiaireARE(beneficiaireARE);
        beneficiaireAidesSociales.setBeneficiaireRSA(beneficiaireRSA);
        return beneficiaireAidesSociales;
    }
    
    private DetailIndemnisationESD creerDetailIndemnisationESD(boolean beneficiaireAAH, boolean beneficiaireARE, boolean beneficiaireASS, boolean beneficiaireRSA) {
        DetailIndemnisationESD detailIndemnisationESD = new DetailIndemnisationESD();
        detailIndemnisationESD.setBeneficiaireAAH(beneficiaireAAH);
        detailIndemnisationESD.setBeneficiaireAssuranceChomage(beneficiaireARE);
        detailIndemnisationESD.setBeneficiairePrestationSolidarite(beneficiaireASS);
        detailIndemnisationESD.setBeneficiaireRSA(beneficiaireRSA);
        return detailIndemnisationESD;
    }
}
