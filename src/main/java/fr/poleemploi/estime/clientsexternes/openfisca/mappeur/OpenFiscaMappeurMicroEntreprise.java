package fr.poleemploi.estime.clientsexternes.openfisca.mappeur;

import static fr.poleemploi.estime.clientsexternes.openfisca.mappeur.ParametresOpenFisca.DEMANDEUR;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fr.poleemploi.estime.clientsexternes.openfisca.ressources.OpenFiscaIndividu;
import fr.poleemploi.estime.clientsexternes.openfisca.ressources.OpenFiscaPeriodes;
import fr.poleemploi.estime.clientsexternes.openfisca.ressources.OpenFiscaRoot;
import fr.poleemploi.estime.commun.enumerations.TypesBeneficesMicroEntrepriseEnum;
import fr.poleemploi.estime.commun.enumerations.exceptions.InternalServerMessages;
import fr.poleemploi.estime.commun.enumerations.exceptions.LoggerMessages;
import fr.poleemploi.estime.services.exceptions.InternalServerException;
import fr.poleemploi.estime.services.ressources.DemandeurEmploi;
import fr.poleemploi.estime.services.ressources.MicroEntreprise;

@Component
public class OpenFiscaMappeurMicroEntreprise {

    @Autowired
    private OpenFiscaMappeurPeriode openFiscaMappeurPeriode;

    private static final Logger LOGGER = LoggerFactory.getLogger(OpenFiscaMappeurMicroEntreprise.class);

    public float getMontantBeneficesMicroEntreprise(OpenFiscaRoot openFiscaRoot, LocalDate dateDebutSimulation) {
	try {
	    Map<String, OpenFiscaIndividu> openFiscaIndividus = openFiscaRoot.getIndividus();
	    OpenFiscaIndividu openFiscaIndividu = openFiscaIndividus.get(DEMANDEUR);
	    OpenFiscaPeriodes openFiscaBeneficesMicroEntreprise = openFiscaIndividu.getBeneficesMicroEntreprise();
	    String periodeFormateeBeneficesMicroEntreprise = openFiscaMappeurPeriode.getPeriodeFormatee(dateDebutSimulation);
	    Double montantBeneficesMicroEntreprise = (Double) openFiscaBeneficesMicroEntreprise.get(periodeFormateeBeneficesMicroEntreprise);

	    return BigDecimal.valueOf(montantBeneficesMicroEntreprise).setScale(0, RoundingMode.HALF_UP).floatValue();

	} catch (NullPointerException e) {
	    LOGGER.error(String.format(LoggerMessages.SIMULATION_IMPOSSIBLE_PROBLEME_TECHNIQUE.getMessage(), e.getMessage()));
	    throw new InternalServerException(InternalServerMessages.SIMULATION_IMPOSSIBLE.getMessage());
	}
    }

    public void addChiffreAffairesMicroEntreprise(OpenFiscaIndividu demandeurOpenFisca, DemandeurEmploi demandeurEmploi, LocalDate dateDebutSimulation) {
	MicroEntreprise microEntreprise = demandeurEmploi.getInformationsPersonnelles().getMicroEntreprise();
	String typesBeneficesMicroEntreprise = microEntreprise.getTypeBenefices();
	if (typesBeneficesMicroEntreprise.equals(TypesBeneficesMicroEntrepriseEnum.AR.getCode())) {
	    demandeurOpenFisca.setChiffreAffairesARMicroEntreprise(openFiscaMappeurPeriode.creerPeriodesChiffreAffairesMicroEntreprise(microEntreprise, dateDebutSimulation));
	} else if (typesBeneficesMicroEntreprise.equals(TypesBeneficesMicroEntrepriseEnum.BIC.getCode())) {
	    demandeurOpenFisca.setChiffreAffairesBICMicroEntreprise(openFiscaMappeurPeriode.creerPeriodesChiffreAffairesMicroEntreprise(microEntreprise, dateDebutSimulation));
	} else if (typesBeneficesMicroEntreprise.equals(TypesBeneficesMicroEntrepriseEnum.BNC.getCode())) {
	    demandeurOpenFisca.setChiffreAffairesBNCMicroEntreprise(openFiscaMappeurPeriode.creerPeriodesChiffreAffairesMicroEntreprise(microEntreprise, dateDebutSimulation));
	}
	demandeurOpenFisca.setBeneficesMicroEntreprise(openFiscaMappeurPeriode.creerPeriodesCalculeesEffectivePremierMoisOpenFisca(dateDebutSimulation));
    }
}
