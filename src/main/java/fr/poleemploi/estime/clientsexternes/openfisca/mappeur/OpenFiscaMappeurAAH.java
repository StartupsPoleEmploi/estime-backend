package fr.poleemploi.estime.clientsexternes.openfisca.mappeur;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fr.poleemploi.estime.clientsexternes.openfisca.ressources.OpenFiscaIndividu;
import fr.poleemploi.estime.services.ressources.DemandeurEmploi;

@Component
public class OpenFiscaMappeurAAH {

    @Autowired
    private OpenFiscaMappeurPeriode openFiscaMappeurPeriode;

    public OpenFiscaIndividu addAAHOpenFiscaIndividu(OpenFiscaIndividu openFiscaIndividu, DemandeurEmploi demandeurEmploi, LocalDate dateDebutSimulation) {
	openFiscaIndividu.setAllocationAdulteHandicape(openFiscaMappeurPeriode.creerPeriodesOpenFiscaAAH(demandeurEmploi, dateDebutSimulation));

	return openFiscaIndividu;
    }
}
