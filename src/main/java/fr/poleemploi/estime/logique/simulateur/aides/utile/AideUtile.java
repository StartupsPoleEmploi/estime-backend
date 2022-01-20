package fr.poleemploi.estime.logique.simulateur.aides.utile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import fr.poleemploi.estime.commun.enumerations.AideEnum;
import fr.poleemploi.estime.commun.utile.DateUtile;
import fr.poleemploi.estime.commun.utile.StringUtile;
import fr.poleemploi.estime.logique.simulateur.aides.poleemploi.utile.AllocationSolidariteSpecifiqueUtile;
import fr.poleemploi.estime.logique.simulateur.aides.poleemploi.utile.AreUtile;
import fr.poleemploi.estime.services.ressources.Aide;
import fr.poleemploi.estime.services.ressources.DemandeurEmploi;
import fr.poleemploi.estime.services.ressources.SimulationAides;
import fr.poleemploi.estime.services.ressources.SimulationMensuelle;

@Component
public class AideUtile {

    @Autowired
    private DateUtile dateUtile;

    @Autowired
    private AllocationSolidariteSpecifiqueUtile allocationSolidariteSpecifiqueUtile;

    @Autowired
    private AreUtile areUtile;

    public static final String PATH_DIR_DETAIL_PRESTATION = "details-prestations/";

    private static final Logger LOGGER = LoggerFactory.getLogger(AideUtile.class);

    public Aide creerAide(AideEnum aideEnum) {
	Aide aide = new Aide();
	aide.setCode(aideEnum.getCode());
	aide.setNom(aideEnum.getNom());
	Optional<String> description = getDescription(aideEnum.getNomFichierDetail());
	if (description.isPresent()) {
	    aide.setDetail(description.get());
	}
	aide.setLienExterne(getLienExterne(aideEnum));
	return aide;
    }

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
	if (aidePourCeMois.isPresent()) {
	    return aidePourCeMois.get().getMontant();
	}
	return 0;
    }

    public float getMontantAideAvantSimulation(int numeroMoisMontantARecuperer, DemandeurEmploi demandeurEmploi, String codeAide, LocalDate dateDebutSimulation) {
	if (AideEnum.ALLOCATION_ADULTES_HANDICAPES.getCode().equals(codeAide)) {
	    return demandeurEmploi.getRessourcesFinancieres().getAidesCAF().getAllocationAAH();
	}
	if (AideEnum.ALLOCATION_SOLIDARITE_SPECIFIQUE.getCode().equals(codeAide)) {
	    LocalDate moisAvantPeriodeSimulation = getMoisAvantSimulation(numeroMoisMontantARecuperer, dateDebutSimulation);
	    return allocationSolidariteSpecifiqueUtile.calculerMontant(demandeurEmploi, moisAvantPeriodeSimulation);
	}
	if (AideEnum.ALLOCATION_RETOUR_EMPLOI.getCode().equals(codeAide)) {
	    LocalDate moisAvantPeriodeSimulation = getMoisAvantSimulation(numeroMoisMontantARecuperer, dateDebutSimulation);
	    return areUtile.calculerMontantAreAvantSimulation(demandeurEmploi, moisAvantPeriodeSimulation);
	}
	return 0;
    }

    public Optional<Aide> getAidePourCeMoisSimule(SimulationAides simulationAides, String codeAide, int numeroMois) {
	if (simulationAides != null && simulationAides.getSimulationsMensuelles() != null) {
	    int indexSimulationMensuelARecuperer = numeroMois - 1;
	    if (indexSimulationMensuelARecuperer >= 0 && indexSimulationMensuelARecuperer < simulationAides.getSimulationsMensuelles().size()) {
		SimulationMensuelle simulationMensuelle = simulationAides.getSimulationsMensuelles().get(indexSimulationMensuelARecuperer);
		if (simulationMensuelle != null && simulationMensuelle.getMesAides() != null && !simulationMensuelle.getMesAides().isEmpty()) {
		    Aide aide = simulationMensuelle.getMesAides().get(codeAide);
		    if (aide != null) {
			return Optional.of(aide);
		    }
		}
	    }
	}
	return Optional.empty();
    }

    public boolean isCodeAideNotExit(String codeAide) {
	Stream<AideEnum> aidesStream = Arrays.stream(AideEnum.values());
	return aidesStream.noneMatch(aide -> aide.getCode().equalsIgnoreCase(codeAide));
    }

    public Optional<AideEnum> getAideEnumByCode(String code) {
	return Arrays.stream(AideEnum.values()).filter(aideEnum -> aideEnum.getCode().equals(code)).findFirst();
    }

    public String getListeFormateeCodesAidePossibles() {
	Stream<AideEnum> aidesStream = Arrays.stream(AideEnum.values());
	return String.join(" / ", aidesStream.map(AideEnum::getCode).collect(Collectors.toList()));
    }

    private String getLienExterne(AideEnum aideEnum) {
	switch (aideEnum) {
	case AGEPI:
	case AIDE_MOBILITE:
	    return "https://candidat.pole-emploi.fr/candidat/aides/mobilite/tableaudebord\\r\\n";
	case PRIME_ACTIVITE:
	    return "https://www.caf.fr/allocataires/mes-services-en-ligne/faire-une-demande-de-prestation";
	default:
	    return StringUtile.EMPTY;
	}
    }

    private LocalDate getMoisAvantSimulation(int numeroMoisMontantARecuperer, LocalDate dateDebutSimulation) {
	LocalDate dateDemandeSimulation = dateUtile.enleverMoisALocalDate(dateDebutSimulation, 1);
	if (numeroMoisMontantARecuperer < 0) {
	    return dateUtile.enleverMoisALocalDate(dateDemandeSimulation, Math.abs(numeroMoisMontantARecuperer));
	}
	return dateDemandeSimulation;
    }
}
