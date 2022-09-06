package fr.poleemploi.estime.clientsexternes.openfisca.mappeur;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fr.poleemploi.estime.clientsexternes.openfisca.ressources.OpenFiscaIndividu;
import fr.poleemploi.estime.services.ressources.DemandeurEmploi;

@Component
public class OpenFiscaMappeurASS {

    @Autowired
    private OpenFiscaMappeurPeriode openFiscaMappeurPeriode;

    public OpenFiscaIndividu addASSOpenFiscaIndividu(OpenFiscaIndividu openFiscaIndividu, DemandeurEmploi demandeurEmploi, LocalDate dateDebutSimulation) {
	openFiscaIndividu.setAllocationSolidariteSpecifique(openFiscaMappeurPeriode.creerPeriodesOpenFiscaASS(demandeurEmploi, dateDebutSimulation));

	return openFiscaIndividu;
    }
}
