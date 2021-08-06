package fr.poleemploi.estime.logique.simulateur.prestationssociales.utile;

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

import fr.poleemploi.estime.commun.enumerations.PrestationsSociales;
import fr.poleemploi.estime.commun.utile.DateUtile;
import fr.poleemploi.estime.logique.simulateur.prestationssociales.poleemploi.utile.AllocationSolidariteSpecifiqueUtile;
import fr.poleemploi.estime.services.ressources.DemandeurEmploi;
import fr.poleemploi.estime.services.ressources.PrestationSociale;
import fr.poleemploi.estime.services.ressources.SimulationMensuelle;
import fr.poleemploi.estime.services.ressources.SimulationPrestationsSociales;

@Component
public class PrestationSocialeUtile {
    
    @Autowired
    private DateUtile dateUtile;
    
    @Autowired
    private AllocationSolidariteSpecifiqueUtile allocationSolidariteSpecifique;
    
    private static final String PATH_DIR_DETAIL_PRESTATION = "details-prestations/";
    
    private static final Logger LOGGER = LoggerFactory.getLogger(PrestationSocialeUtile.class);

    public Optional<String> getDescription(String nomFichier) {
        try {
            File resource = new ClassPathResource(PATH_DIR_DETAIL_PRESTATION + nomFichier).getFile();
            return Optional.of(new String(Files.readAllBytes(resource.toPath())));
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        }
        return Optional.empty();
    }
    
    public float getMontantPrestationSocialePourCeMoisSimule(SimulationPrestationsSociales simulationPrestationsSociales, String codePrestationSociale, int numeroMoisMontantARecuperer) { 
        Optional<PrestationSociale> prestationSocialePourCeMois = getPrestationSocialePourCeMoisSimule(simulationPrestationsSociales, codePrestationSociale, numeroMoisMontantARecuperer);
        if(prestationSocialePourCeMois.isPresent()) {
            return prestationSocialePourCeMois.get().getMontant();
        } 
        return 0;
    }
    
    public float getMontantPrestationSocialeeAvantSimulation(int numeroMoisMontantARecuperer, DemandeurEmploi demandeurEmploi, String codePrestationSociale, LocalDate dateDebutSimulation) {
        if(PrestationsSociales.ALLOCATION_ADULTES_HANDICAPES.getCode().equals(codePrestationSociale)) {
            return demandeurEmploi.getRessourcesFinancieres().getPrestationsCAF().getAllocationMensuelleNetAAH();
        }
        if(PrestationsSociales.ALLOCATION_SOLIDARITE_SPECIFIQUE.getCode().equals(codePrestationSociale)) {
            LocalDate moisAvantPeriodeSimulation = getMoisAvantSimulation(numeroMoisMontantARecuperer, dateDebutSimulation);
            return allocationSolidariteSpecifique.calculerMontant(demandeurEmploi, moisAvantPeriodeSimulation);
        }
        return 0;
    }
    
    public Optional<PrestationSociale> getPrestationSocialePourCeMoisSimule(SimulationPrestationsSociales simulationPrestationsSociales, String codePrestationSociale, int numeroMois) {        
        if(simulationPrestationsSociales != null && simulationPrestationsSociales.getSimulationsMensuelles() != null) {       
            int indexSimulationMensuelARecuperer = numeroMois - 1;            
            if(indexSimulationMensuelARecuperer >= 0 && indexSimulationMensuelARecuperer < simulationPrestationsSociales.getSimulationsMensuelles().size()) {                
                SimulationMensuelle simulationMensuelle = simulationPrestationsSociales.getSimulationsMensuelles().get(indexSimulationMensuelARecuperer);
                if(simulationMensuelle != null && simulationMensuelle.getMesPrestationsSociales() != null && !simulationMensuelle.getMesPrestationsSociales().isEmpty()) {
                    PrestationSociale prestationSociale = simulationMensuelle.getMesPrestationsSociales().get(codePrestationSociale);
                    if(prestationSociale != null) {
                        return Optional.of(prestationSociale);
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
