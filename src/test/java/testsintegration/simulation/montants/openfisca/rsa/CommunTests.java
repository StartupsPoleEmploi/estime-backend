package testsintegration.simulation.montants.openfisca.rsa;

import java.time.LocalDate;
import java.util.HashMap;

import fr.poleemploi.estime.commun.enumerations.AidesSociales;
import fr.poleemploi.estime.commun.enumerations.Organismes;
import fr.poleemploi.estime.services.ressources.AideSociale;
import fr.poleemploi.estime.services.ressources.AllocationsLogementMensuellesNetFoyer;
import fr.poleemploi.estime.services.ressources.BeneficiaireAidesSociales;
import fr.poleemploi.estime.services.ressources.SimulationMensuelle;

public class CommunTests {    
    
    protected SimulationMensuelle createSimulationMensuelleASSetRSA(float montantASS, float montantRSA, LocalDate dateSimulation, boolean isRSAReportee) {
        SimulationMensuelle simulationMensuelle = new SimulationMensuelle();
        HashMap<String, AideSociale> aides = new HashMap<>();
        aides.put(AidesSociales.ALLOCATION_SOLIDARITE_SPECIFIQUE.getCode(), createSimulationMensuelAideSocialASS(montantASS));
        aides.put(AidesSociales.RSA.getCode(), createSimulationMensuelAideSocialRSA(montantRSA, isRSAReportee));
        simulationMensuelle.setDatePremierJourMoisSimule(dateSimulation);
        simulationMensuelle.setMesAides(aides);
        return simulationMensuelle;        
    }

    protected SimulationMensuelle createSimulationMensuelleRSA(float montantRSA, LocalDate dateSimulation, boolean isRSAReportee) {
        SimulationMensuelle simulationMensuelle = new SimulationMensuelle();
        HashMap<String, AideSociale> aides = new HashMap<>();
        aides.put(AidesSociales.RSA.getCode(), createSimulationMensuelAideSocialRSA(montantRSA, isRSAReportee));
        simulationMensuelle.setDatePremierJourMoisSimule(dateSimulation);
        simulationMensuelle.setMesAides(aides);
        return simulationMensuelle;        
    }
    
    protected BeneficiaireAidesSociales creerBeneficiaireRSA() {
        BeneficiaireAidesSociales beneficiaireAidesSociales = new BeneficiaireAidesSociales();
        beneficiaireAidesSociales.setBeneficiaireRSA(true);
        beneficiaireAidesSociales.setBeneficiaireARE(false);
        beneficiaireAidesSociales.setBeneficiaireAAH(false);
        beneficiaireAidesSociales.setBeneficiaireASS(false);
        return beneficiaireAidesSociales;
    }
    
    protected AllocationsLogementMensuellesNetFoyer creerAllocationsLogementMensuellesNetFoyer(int montant) {
        
        AllocationsLogementMensuellesNetFoyer allocationsLogementMensuellesNetFoyer = new AllocationsLogementMensuellesNetFoyer();
        allocationsLogementMensuellesNetFoyer.setMoisN(montant);
        allocationsLogementMensuellesNetFoyer.setMoisNMoins1(montant);
        allocationsLogementMensuellesNetFoyer.setMoisNMoins2(montant);
        allocationsLogementMensuellesNetFoyer.setMoisNMoins3(montant);
        return allocationsLogementMensuellesNetFoyer;
    }
    
    private AideSociale createSimulationMensuelAideSocialRSA(float montantRSA, boolean isRSAReportee) {
        AideSociale aideSociale = new AideSociale();
        aideSociale.setCode(AidesSociales.RSA.getCode());
        aideSociale.setReportee(isRSAReportee);
        aideSociale.setOrganisme(Organismes.CAF.getNomCourt());
        aideSociale.setMontant(montantRSA);
        return aideSociale;
    }
    
    private AideSociale createSimulationMensuelAideSocialASS(float montantASS) {
        AideSociale aideSociale = new AideSociale();
        aideSociale.setCode(AidesSociales.ALLOCATION_SOLIDARITE_SPECIFIQUE.getCode());
        aideSociale.setOrganisme(Organismes.PE.getNomCourt());
        aideSociale.setMontant(montantASS);
        return aideSociale;
    }
}
