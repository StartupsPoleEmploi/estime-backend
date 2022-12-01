package fr.poleemploi.estime.logique.simulateur.aides.utile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import fr.poleemploi.estime.commun.enumerations.AideEnum;
import fr.poleemploi.estime.commun.enumerations.OrganismeEnum;
import fr.poleemploi.estime.commun.utile.DateUtile;
import fr.poleemploi.estime.commun.utile.StringUtile;
import fr.poleemploi.estime.services.ressources.Aide;
import fr.poleemploi.estime.services.ressources.Simulation;
import fr.poleemploi.estime.services.ressources.SimulationMensuelle;

@Component
public class AideUtile {

    public static final String PATH_DIR_DETAIL_PRESTATION = "details-aides/";

    private static final Logger LOGGER = LoggerFactory.getLogger(AideUtile.class);

    @Autowired
    private DateUtile dateUtile;

    public Aide creerAide(AideEnum aideEnum, Optional<OrganismeEnum> organismeEnumOptional, Optional<List<String>> messageAlerteOptional, boolean isAideReportee, float montantAide) {
	Aide aide = new Aide();
	aide.setCode(aideEnum.getCode());
	Optional<String> detailAideOptional = Optional.empty();
	if (!aideEnum.getNomFichierDetail().isEmpty())
	    detailAideOptional = getDescription(aideEnum.getNomFichierDetail());
	if (detailAideOptional.isPresent()) {
	    aide.setDetail(detailAideOptional.get());
	}
	if (messageAlerteOptional.isPresent()) {
	    aide.setMessagesAlerte(messageAlerteOptional.get());
	}
	aide.setMontant(montantAide);
	aide.setNom(aideEnum.getNom());
	if (organismeEnumOptional.isPresent()) {
	    aide.setOrganisme(organismeEnumOptional.get().getNomCourt());
	}
	aide.setReportee(isAideReportee);
	aide.setLienExterne(getLienExterne(aideEnum));
	return aide;
    }

    public Aide creerAideVide(AideEnum aideEnum) {
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

    public float getMontantAidePourCeMoisSimule(Simulation simulation, String codeAide, int numeroMoisMontantARecuperer) {
	Optional<Aide> aidePourCeMois = getAidePourCeMoisSimule(simulation, codeAide, numeroMoisMontantARecuperer);
	if (aidePourCeMois.isPresent()) {
	    return aidePourCeMois.get().getMontant();
	}
	return 0;
    }

    public Optional<Aide> getAidePourCeMoisSimule(Simulation simulation, String codeAide, int numeroMois) {
	if (simulation != null && simulation.getSimulationsMensuelles() != null) {
	    int indexSimulationMensuelARecuperer = numeroMois - 1;
	    if (indexSimulationMensuelARecuperer >= 0 && indexSimulationMensuelARecuperer < simulation.getSimulationsMensuelles().size()) {
		SimulationMensuelle simulationMensuelle = simulation.getSimulationsMensuelles().get(indexSimulationMensuelARecuperer);
		if (simulationMensuelle != null && simulationMensuelle.getAides() != null && !simulationMensuelle.getAides().isEmpty()) {
		    Aide aide = simulationMensuelle.getAides().get(codeAide);
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

    public Optional<String> getDescription(String nomFichier) {
	try {
	    File resource = new ClassPathResource(PATH_DIR_DETAIL_PRESTATION + nomFichier).getFile();
	    return Optional.of(new String(Files.readAllBytes(resource.toPath())));
	} catch (IOException e) {
	    LOGGER.error(e.getMessage());
	}
	return Optional.empty();
    }

    public String getLienExterne(AideEnum aideEnum) {
	switch (aideEnum) {
	case AIDES_LOGEMENT:
	case AIDE_PERSONNALISEE_LOGEMENT:
	case ALLOCATION_LOGEMENT_SOCIALE:
	case ALLOCATION_LOGEMENT_FAMILIALE:
	    return "https://wwwd.caf.fr/wps/portal/caffr/aidesetservices/lesservicesenligne/faireunedemandedeprestation#DAL";
	case AGEPI:
	case AIDE_MOBILITE:
	    return "https://candidat.pole-emploi.fr/candidat/aides/mobilite/tableaudebord\\r\\n";
	case PRIME_ACTIVITE:
	    return "https://wwwd.caf.fr/wps/portal/caffr/aidesetservices/lesservicesenligne/faireunedemandedeprestation#/solidariteetactivite";
	default:
	    return StringUtile.EMPTY;
	}
    }

    public LocalDate getMoisAvantSimulation(int numeroMoisMontantARecuperer, LocalDate dateDebutSimulation) {
	LocalDate dateDemandeSimulation = dateUtile.enleverMoisALocalDate(dateDebutSimulation, 1);
	if (numeroMoisMontantARecuperer < 0) {
	    return dateUtile.enleverMoisALocalDate(dateDemandeSimulation, Math.abs(numeroMoisMontantARecuperer));
	}
	return dateDemandeSimulation;
    }
}
