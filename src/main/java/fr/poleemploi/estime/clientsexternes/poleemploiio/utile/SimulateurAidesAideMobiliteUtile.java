package fr.poleemploi.estime.clientsexternes.poleemploiio.utile;

import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import fr.poleemploi.estime.clientsexternes.poleemploiio.ressources.simulateuraides.aidemobilite.AideMobilitePEIOIn;
import fr.poleemploi.estime.commun.enumerations.NombreJourIndemniseEnum;
import fr.poleemploi.estime.services.ressources.DemandeurEmploi;

@Component
public class SimulateurAidesAideMobiliteUtile {
    
    private static final int NOMBRE_MAX_JOURS_INDEMNISES = 20;
    
    public HttpEntity<String> createHttpEntityAgepiSimulateurAides(DemandeurEmploi demandeurEmploi) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", demandeurEmploi.getPeConnectAuthorization().getBearerToken());    
        return new HttpEntity<String>(createAideMobilitePEIOIn(demandeurEmploi), headers);
    }
    
    private String createAideMobilitePEIOIn(DemandeurEmploi demandeurEmploi) {
        AideMobilitePEIOIn aideMobilitePEIOIn = new AideMobilitePEIOIn();
        aideMobilitePEIOIn.setContexte("Reprise");
        aideMobilitePEIOIn.setDateActionReclassement(LocalDate.now().with(TemporalAdjusters.firstDayOfNextMonth()).toString());
        aideMobilitePEIOIn.setDateDepot(LocalDate.now().toString());
        aideMobilitePEIOIn.setDureePeriodeEmploiOuFormation(demandeurEmploi.getRessourcesFinancieres().getNombreMoisTravaillesDerniersMois());
        aideMobilitePEIOIn.setNatureContratTravail(demandeurEmploi.getFuturTravail().getTypeContrat());
        aideMobilitePEIOIn.setOrigine("W");
        aideMobilitePEIOIn.setDistanceDomicileActionReclassement(Math.round(demandeurEmploi.getFuturTravail().getDistanceKmDomicileTravail()));
        aideMobilitePEIOIn.setNombreAllersRetours(demandeurEmploi.getFuturTravail().getNombreTrajetsDomicileTravail());
        aideMobilitePEIOIn.setNombreRepas(getNombreRepas(demandeurEmploi));
        aideMobilitePEIOIn.setNombreNuitees(getNombreNuitees(demandeurEmploi));
        aideMobilitePEIOIn.setContexte("Reprise");
        aideMobilitePEIOIn.setLieuFormationOuEmploi("France");
        return aideMobilitePEIOIn.toString();
    }

    //TODO JLA ??
    private int getNombreNuitees(DemandeurEmploi demandeurEmploi) {
        int nombreJoursIndemnises = getNombrejoursIndemnises(demandeurEmploi.getFuturTravail().getNombreHeuresTravailleesSemaine());
        if (nombreJoursIndemnises - 1 > 0) {
            return nombreJoursIndemnises - 1;
        } else {
            return 0;
        }
    }

    //TODO JLA ??
    private int getNombreRepas(DemandeurEmploi demandeurEmploi) {
        float nombreHeuresHebdoTravaillees = demandeurEmploi.getFuturTravail().getNombreHeuresTravailleesSemaine();
        int nombreRepas = 0;
        for (NombreJourIndemniseEnum nombresJoursTravailles : NombreJourIndemniseEnum.values()) {
            if (nombreHeuresHebdoTravaillees >= nombresJoursTravailles.getNombreHeuresMinHebdo()
                    && nombreHeuresHebdoTravaillees <= nombresJoursTravailles.getNombreHeuresMaxHebdo()) {
                nombreRepas = nombresJoursTravailles.getNombreRepasIndemnises();
            }
        }
        return nombreRepas;
    }
    
    private int getNombrejoursIndemnises(float nombreHeuresHebdoTravaillees) {
        for (NombreJourIndemniseEnum nombresJoursTravailles : NombreJourIndemniseEnum.values()) {
            if (nombreHeuresHebdoTravaillees >= nombresJoursTravailles.getNombreHeuresMinHebdo()
                    && nombreHeuresHebdoTravaillees <= nombresJoursTravailles.getNombreHeuresMaxHebdo()) {
                return nombresJoursTravailles.getNombreRepasIndemnises();
            }
        }
        return NOMBRE_MAX_JOURS_INDEMNISES;
    }
}
