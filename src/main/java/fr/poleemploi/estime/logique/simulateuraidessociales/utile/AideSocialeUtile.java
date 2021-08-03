package fr.poleemploi.estime.logique.simulateuraidessociales.utile;

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

import fr.poleemploi.estime.commun.enumerations.AidesSociales;
import fr.poleemploi.estime.commun.utile.DateUtile;
import fr.poleemploi.estime.logique.simulateuraidessociales.poleemploi.aides.AllocationSolidariteSpecifique;
import fr.poleemploi.estime.services.ressources.AideSociale;
import fr.poleemploi.estime.services.ressources.DemandeurEmploi;
import fr.poleemploi.estime.services.ressources.SimulationAidesSociales;
import fr.poleemploi.estime.services.ressources.SimulationMensuelle;

@Component
public class AideSocialeUtile {
    
    @Autowired
    private DateUtile dateUtile;
    
    @Autowired
    private AllocationSolidariteSpecifique allocationSolidariteSpecifique;
    
    private static final String PATH_DIR_DETAIL_AIDES = "details-aides/";
    
    private static final Logger LOGGER = LoggerFactory.getLogger(AideSocialeUtile.class);

    public Optional<String> getDescription(String nomFichier) {
        try {
            File resource = new ClassPathResource(PATH_DIR_DETAIL_AIDES + nomFichier).getFile();
            return Optional.of(new String(Files.readAllBytes(resource.toPath())));
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        }
        return Optional.empty();
    }
    
    public float getMontantAideSocialPourCeMoisSimule(SimulationAidesSociales simulationAidesSociales, String codeAideSociale, int numeroMoisMontantARecuperer) { 
        Optional<AideSociale> aideSocialPourCeMois = getAideSocialPourCeMoisSimule(simulationAidesSociales, codeAideSociale, numeroMoisMontantARecuperer);
        if(aideSocialPourCeMois.isPresent()) {
            return aideSocialPourCeMois.get().getMontant();
        } 
        return 0;
    }
    
    public float getMontantAideSocialeAvantSimulation(int numeroMoisMontantARecuperer, DemandeurEmploi demandeurEmploi, String codeAideSociale, LocalDate dateDebutSimulation) {
        if(AidesSociales.ALLOCATION_ADULTES_HANDICAPES.getCode().equals(codeAideSociale)) {
            return demandeurEmploi.getRessourcesFinancieres().getPrestationsCAF().getAllocationMensuelleNetAAH();
        }
        if(AidesSociales.ALLOCATION_SOLIDARITE_SPECIFIQUE.getCode().equals(codeAideSociale)) {
            LocalDate moisAvantPeriodeSimulation = getMoisAvantSimulation(numeroMoisMontantARecuperer, dateDebutSimulation);
            return allocationSolidariteSpecifique.calculerMontant(demandeurEmploi, moisAvantPeriodeSimulation);
        }
        return 0;
    }

    private Optional<AideSociale> getAideSocialPourCeMoisSimule(SimulationAidesSociales simulationAidesSociales, String codeAideSociale, int numeroMoisMontantARecuperer) {
        if(simulationAidesSociales != null 
                && simulationAidesSociales.getSimulationsMensuelles() != null) {       
            int indexSimulationMensuelARecuperer = numeroMoisMontantARecuperer - 1;
            if(indexSimulationMensuelARecuperer >= 0 && indexSimulationMensuelARecuperer < simulationAidesSociales.getSimulationsMensuelles().size()) {                
                SimulationMensuelle simulationMensuelle = simulationAidesSociales.getSimulationsMensuelles().get(indexSimulationMensuelARecuperer);
                if(simulationMensuelle != null && simulationMensuelle.getMesAides() != null && !simulationMensuelle.getMesAides().isEmpty()) {
                    AideSociale aideSocialeASS = simulationMensuelle.getMesAides().get(codeAideSociale);
                    if(aideSocialeASS != null) {
                        return Optional.of(aideSocialeASS);
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
