package fr.poleemploi.estime.logique.simulateur.aides.utile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDate;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import fr.poleemploi.estime.commun.enumerations.Aides;
import fr.poleemploi.estime.commun.utile.DateUtile;
import fr.poleemploi.estime.logique.simulateur.aides.poleemploi.utile.AllocationSolidariteSpecifiqueUtile;
import fr.poleemploi.estime.services.ressources.Aide;
import fr.poleemploi.estime.services.ressources.DemandeurEmploi;
import fr.poleemploi.estime.services.ressources.SimulationAides;
import fr.poleemploi.estime.services.ressources.SimulationMensuelle;

@Component
public class AideUtile {
    
    @Autowired
    private DateUtile dateUtile;
    
    @Autowired
    private AllocationSolidariteSpecifiqueUtile allocationSolidariteSpecifique;
    
    private static final String PATH_DIR_DETAIL_PRESTATION = "details-prestations/";
    
    private static final Logger LOGGER = LoggerFactory.getLogger(AideUtile.class);

    public Optional<String> getDescription(String nomFichier) {
        try {
            File resource = new ClassPathResource(PATH_DIR_DETAIL_PRESTATION + nomFichier).getFile();
            return Optional.of(new String(Files.readAllBytes(resource.toPath())));
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        }
        return Optional.empty();
    }
    
    public float getMontantAidePourCeMoisSimule(SimulationAides simulationAides, String codeAide, int numeroMoisMontantARecuperer) { 
        Optional<Aide> aidePourCeMois = getAidePourCeMoisSimule(simulationAides, codeAide, numeroMoisMontantARecuperer);
        if(aidePourCeMois.isPresent()) {
            return aidePourCeMois.get().getMontant();
        } 
        return 0;
    }
    
    public float getMontantAideeAvantSimulation(int numeroMoisMontantARecuperer, DemandeurEmploi demandeurEmploi, String codeAide, LocalDate dateDebutSimulation) {
        if(Aides.ALLOCATION_ADULTES_HANDICAPES.getCode().equals(codeAide)) {
            return demandeurEmploi.getRessourcesFinancieres().getAidesCAF().getAllocationAAH();
        }
        if(Aides.ALLOCATION_SOLIDARITE_SPECIFIQUE.getCode().equals(codeAide)) {
            LocalDate moisAvantPeriodeSimulation = getMoisAvantSimulation(numeroMoisMontantARecuperer, dateDebutSimulation);
            return allocationSolidariteSpecifique.calculerMontant(demandeurEmploi, moisAvantPeriodeSimulation);
        }
        return 0;
    }
    
    public Optional<Aide> getAidePourCeMoisSimule(SimulationAides simulationAides, String codeAide, int numeroMois) {        
        if(simulationAides != null && simulationAides.getSimulationsMensuelles() != null) {       
            int indexSimulationMensuelARecuperer = numeroMois - 1;            
            if(indexSimulationMensuelARecuperer >= 0 && indexSimulationMensuelARecuperer < simulationAides.getSimulationsMensuelles().size()) {                
                SimulationMensuelle simulationMensuelle = simulationAides.getSimulationsMensuelles().get(indexSimulationMensuelARecuperer);
                if(simulationMensuelle != null && simulationMensuelle.getMesAides() != null && !simulationMensuelle.getMesAides().isEmpty()) {
                    Aide aide = simulationMensuelle.getMesAides().get(codeAide);
                    if(aide != null) {
                        return Optional.of(aide);
                    }              
                }
            }
        }
        return Optional.empty();
    }
    
    private LocalDate getMoisAvantSimulation(int numeroMoisMontantARecuperer, LocalDate dateDebutSimulation) {
        LocalDate dateDemandeSimulation = dateUtile.enleverMoisALocalDate(dateDebutSimulation, 1);
        if(numeroMoisMontantARecuperer < 0) {
            return dateUtile.enleverMoisALocalDate(dateDemandeSimulation, Math.abs(numeroMoisMontantARecuperer));            
        }
        return dateDemandeSimulation;
    }
}
