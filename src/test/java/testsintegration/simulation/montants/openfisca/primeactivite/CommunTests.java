package testsintegration.simulation.montants.openfisca.primeactivite;

import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;

import fr.poleemploi.estime.commun.enumerations.AidesSociales;
import fr.poleemploi.estime.commun.enumerations.Organismes;
import fr.poleemploi.estime.services.ressources.AideSociale;
import fr.poleemploi.estime.services.ressources.SimulationMensuelle;
import utile.tests.UtileTests;

public class CommunTests {
    
    @Autowired
    protected UtileTests utileTests;
    
    public SimulationMensuelle createSimulationMensuelleASS(float montantASS) {
        SimulationMensuelle simulationMensuelle = new SimulationMensuelle();
        HashMap<String, AideSociale> aides = new HashMap<>();
        aides.put(AidesSociales.ALLOCATION_SOLIDARITE_SPECIFIQUE.getCode(), createSimulationMensuelAideSocialASS(montantASS));
        simulationMensuelle.setMesAides(aides);
        return simulationMensuelle;
    }

    private AideSociale createSimulationMensuelAideSocialASS(float montantASS) {
        AideSociale aideSociale = new AideSociale();
        aideSociale.setCode(AidesSociales.ALLOCATION_SOLIDARITE_SPECIFIQUE.getCode());
        aideSociale.setOrganisme(Organismes.PE.getNomCourt());
        aideSociale.setMontant(montantASS);
        return aideSociale;
    }
}
