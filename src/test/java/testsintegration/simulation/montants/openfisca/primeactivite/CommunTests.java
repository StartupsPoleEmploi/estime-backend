package testsintegration.simulation.montants.openfisca.primeactivite;

import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;

import fr.poleemploi.estime.commun.enumerations.Aides;
import fr.poleemploi.estime.commun.enumerations.Organismes;
import fr.poleemploi.estime.services.ressources.Aide;
import fr.poleemploi.estime.services.ressources.SimulationMensuelle;
import utile.tests.UtileTests;

public class CommunTests {
    
    @Autowired
    protected UtileTests utileTests;
    
    public SimulationMensuelle createSimulationMensuelleASS(float montantASS) {
        SimulationMensuelle simulationMensuelle = new SimulationMensuelle();
        HashMap<String, Aide> prestations = new HashMap<>();
        prestations.put(Aides.ALLOCATION_SOLIDARITE_SPECIFIQUE.getCode(), createSimulationMensuelAideASS(montantASS));
        simulationMensuelle.setMesAides(prestations);
        return simulationMensuelle;
    }

    private Aide createSimulationMensuelAideASS(float montantASS) {
        Aide ass = new Aide();
        ass.setCode(Aides.ALLOCATION_SOLIDARITE_SPECIFIQUE.getCode());
        ass.setOrganisme(Organismes.PE.getNomCourt());
        ass.setMontant(montantASS);
        return ass;
    }
}
