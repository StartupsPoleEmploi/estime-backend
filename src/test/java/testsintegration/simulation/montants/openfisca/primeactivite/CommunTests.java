package testsintegration.simulation.montants.openfisca.primeactivite;

import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;

import fr.poleemploi.estime.commun.enumerations.PrestationsSociales;
import fr.poleemploi.estime.commun.enumerations.Organismes;
import fr.poleemploi.estime.services.ressources.PrestationSociale;
import fr.poleemploi.estime.services.ressources.SimulationMensuelle;
import utile.tests.UtileTests;

public class CommunTests {
    
    @Autowired
    protected UtileTests utileTests;
    
    public SimulationMensuelle createSimulationMensuelleASS(float montantASS) {
        SimulationMensuelle simulationMensuelle = new SimulationMensuelle();
        HashMap<String, PrestationSociale> prestations = new HashMap<>();
        prestations.put(PrestationsSociales.ALLOCATION_SOLIDARITE_SPECIFIQUE.getCode(), createSimulationMensuelPrestationSocialeASS(montantASS));
        simulationMensuelle.setMesPrestationsSociales(prestations);
        return simulationMensuelle;
    }

    private PrestationSociale createSimulationMensuelPrestationSocialeASS(float montantASS) {
        PrestationSociale ass = new PrestationSociale();
        ass.setCode(PrestationsSociales.ALLOCATION_SOLIDARITE_SPECIFIQUE.getCode());
        ass.setOrganisme(Organismes.PE.getNomCourt());
        ass.setMontant(montantASS);
        return ass;
    }
}
