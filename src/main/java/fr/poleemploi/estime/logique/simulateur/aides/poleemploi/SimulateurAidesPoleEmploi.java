package fr.poleemploi.estime.logique.simulateur.aides.poleemploi;

import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fr.poleemploi.estime.clientsexternes.openfisca.OpenFiscaClient;
import fr.poleemploi.estime.clientsexternes.openfisca.OpenFiscaRetourSimulation;
import fr.poleemploi.estime.clientsexternes.openfisca.ressources.OpenFiscaRoot;
import fr.poleemploi.estime.commun.enumerations.AideEnum;
import fr.poleemploi.estime.commun.utile.demandeuremploi.BeneficiaireAidesUtile;
import fr.poleemploi.estime.logique.simulateur.aides.poleemploi.utile.AllocationSolidariteSpecifiqueUtile;
import fr.poleemploi.estime.logique.simulateur.aides.poleemploi.utile.AreUtile;
import fr.poleemploi.estime.services.ressources.Aide;
import fr.poleemploi.estime.services.ressources.DemandeurEmploi;

@Component
public class SimulateurAidesPoleEmploi {

    private static final int NUMERO_MOIS_RECUPERATION_COMPLEMENT_ARE = 1;

    @Autowired
    private AllocationSolidariteSpecifiqueUtile allocationSolidariteSpecifiqueUtile;

    @Autowired
    private AreUtile areUtile;

    @Autowired
    private BeneficiaireAidesUtile beneficiaireAidesUtile;

    @Autowired
    private OpenFiscaClient openFiscaClient;

    public void simuler(Map<String, Aide> aidesPourCeMois, int numeroMoisSimule, DemandeurEmploi demandeurEmploi, LocalDate dateDebutSimulation) {

	if (beneficiaireAidesUtile.isBeneficiaireASS(demandeurEmploi) && allocationSolidariteSpecifiqueUtile.isEligible(numeroMoisSimule, demandeurEmploi, dateDebutSimulation)) {
	    Optional<Aide> aideOptional = allocationSolidariteSpecifiqueUtile.simulerAide(demandeurEmploi, numeroMoisSimule, dateDebutSimulation);
	    if (aideOptional.isPresent()) {
		aidesPourCeMois.put(AideEnum.ALLOCATION_SOLIDARITE_SPECIFIQUE.getCode(), aideOptional.get());
	    }
	}
    }

    public void simulerComplementARE(OpenFiscaRoot openFiscaRoot, Map<String, Aide> aidesPourCeMois, LocalDate dateDebutSimulation) {
	OpenFiscaRetourSimulation openFiscaRetourSimulation = openFiscaClient.calculerComplementARE(openFiscaRoot, dateDebutSimulation, NUMERO_MOIS_RECUPERATION_COMPLEMENT_ARE);
	Aide complementARE = areUtile.creerComplementARE(openFiscaRetourSimulation.getMontantComplementAREBrut(), openFiscaRetourSimulation.getNombreJoursRestantsARE());
	Aide crc = areUtile.creerMontantCRCComplementARE(openFiscaRetourSimulation.getMontantCRCComplementARE());
	Aide crds = areUtile.creerMontantCRDSComplementARE(openFiscaRetourSimulation.getMontantCRDSComplementARE());
	Aide csg = areUtile.creerMontantCSGComplementARE(openFiscaRetourSimulation.getMontantCSGComplementARE());
	aidesPourCeMois.put(AideEnum.COMPLEMENT_AIDE_RETOUR_EMPLOI.getCode(), complementARE);
	aidesPourCeMois.put(AideEnum.CRC.getCode(), crc);
	aidesPourCeMois.put(AideEnum.CRDS.getCode(), crds);
	aidesPourCeMois.put(AideEnum.CSG.getCode(), csg);

    }
}
