package fr.poleemploi.estime.clientsexternes.poleemploiio.utile;

import java.time.LocalDate;
import java.time.Period;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import fr.poleemploi.estime.clientsexternes.poleemploiio.ressources.simulateuraides.agepi.AgepiPEIOIn;
import fr.poleemploi.estime.commun.utile.DateUtile;
import fr.poleemploi.estime.services.ressources.DemandeurEmploi;
import fr.poleemploi.estime.services.ressources.Personne;

@Component
public class SimulateurAidesAgepiUtile {

    @Autowired
    private DateUtile dateUtile;

    private static final String CONTEXTE_REPRISE_EMPLOI = "Reprise";

    public HttpEntity<String> createHttpEntityAgepiSimulateurAides(DemandeurEmploi demandeurEmploi) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", demandeurEmploi.getPeConnectAuthorization().getBearerToken());
        return new HttpEntity<String>(createAgepiPEIOIn(demandeurEmploi), headers);
    }

    public String createAgepiPEIOIn(DemandeurEmploi demandeurEmploi) {
        AgepiPEIOIn agepiPEIOIn = new AgepiPEIOIn();
        agepiPEIOIn.setCodeTerritoire("001");
        agepiPEIOIn.setContexte(CONTEXTE_REPRISE_EMPLOI);
        agepiPEIOIn.setDateActionReclassement(dateUtile.getDateJour().toString());
        agepiPEIOIn.setDateDepot(dateUtile.getDateJour().toString());
        agepiPEIOIn.setDureePeriodeEmploiOuFormation(demandeurEmploi.getRessourcesFinancieres().getNombreMoisTravaillesDerniersMois());
        agepiPEIOIn.setEleveSeulEnfants(!demandeurEmploi.getSituationFamiliale().getIsEnCouple());
        agepiPEIOIn.setIntensite(Math.round(demandeurEmploi.getFuturTravail().getNombreHeuresTravailleesSemaine()));
        agepiPEIOIn.setLieuFormationOuEmploi("France");
        agepiPEIOIn.setNatureContratTravail(demandeurEmploi.getFuturTravail().getTypeContrat());

        //@TODO JLA à extraire
        int nombreEnfants = 0;
        int nombreEnfantsMoinsDixAns = 0;
        for (Personne personneACharge : demandeurEmploi.getSituationFamiliale().getPersonnesACharge()) {
            LocalDate today = LocalDate.now();
            int agePersonneACharge = Period.between(personneACharge.getInformationsPersonnelles().getDateNaissance(), today).getYears();
            if (agePersonneACharge < 10) {
                ++nombreEnfants;
                ++nombreEnfantsMoinsDixAns;
            } else if (agePersonneACharge < 18) {
                ++nombreEnfants;
            }
        }

        agepiPEIOIn.setNombreEnfants(nombreEnfants);
        agepiPEIOIn.setNombreEnfantsMoins10Ans(nombreEnfantsMoinsDixAns);
        agepiPEIOIn.setOrigine("c");
        agepiPEIOIn.setTypeIntensite("Mensuelle");
        return agepiPEIOIn.toString();
    }
}
