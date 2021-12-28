package fr.poleemploi.estime.clientsexternes.poleemploiio.utile;

import static fr.poleemploi.estime.clientsexternes.poleemploiio.ressources.ParametresPEIO.CODE_TERRITOIRE;
import static fr.poleemploi.estime.clientsexternes.poleemploiio.ressources.ParametresPEIO.CONTEXTE;
import static fr.poleemploi.estime.clientsexternes.poleemploiio.ressources.ParametresPEIO.DATE_ACTION_RECLASSEMENT;
import static fr.poleemploi.estime.clientsexternes.poleemploiio.ressources.ParametresPEIO.DATE_DEPOT;
import static fr.poleemploi.estime.clientsexternes.poleemploiio.ressources.ParametresPEIO.DUREE_PERIODE_EMPLOI_OU_FORMATION;
import static fr.poleemploi.estime.clientsexternes.poleemploiio.ressources.ParametresPEIO.ELEVE_SEUL_ENFANTS;
import static fr.poleemploi.estime.clientsexternes.poleemploiio.ressources.ParametresPEIO.INTENSITE;
import static fr.poleemploi.estime.clientsexternes.poleemploiio.ressources.ParametresPEIO.LIEU_FORMATION_OU_EMPLOI;
import static fr.poleemploi.estime.clientsexternes.poleemploiio.ressources.ParametresPEIO.NATURE_CONTRAT_TRAVAIL;
import static fr.poleemploi.estime.clientsexternes.poleemploiio.ressources.ParametresPEIO.NOMBRE_ENFANTS;
import static fr.poleemploi.estime.clientsexternes.poleemploiio.ressources.ParametresPEIO.NOMBRE_ENFANTS_MOINS_10_ANS;
import static fr.poleemploi.estime.clientsexternes.poleemploiio.ressources.ParametresPEIO.ORIGINE;
import static fr.poleemploi.estime.clientsexternes.poleemploiio.ressources.ParametresPEIO.TYPE_INTENSITE;

import java.time.LocalDate;
import java.time.Period;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import fr.poleemploi.estime.clientsexternes.poleemploiio.ressources.simulateuraides.agepi.AgepiPEIOIn;
import fr.poleemploi.estime.commun.utile.DateUtile;
import fr.poleemploi.estime.services.ressources.DemandeurEmploi;
import fr.poleemploi.estime.services.ressources.Personne;

@Component
public class SimulateurAidesAgepiUtile {

    @Autowired
    private DateUtile dateUtile;

    private static final String CONTEXTE_REPRISE_EMPLOI = "reprise";

    public HttpEntity<AgepiPEIOIn> createHttpEntityAgepiSimulateurAides(DemandeurEmploi demandeurEmploi) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", demandeurEmploi.getPeConnectAuthorization().getBearerToken());
        AgepiPEIOIn agepiPEIOIn = createAgepiPEIOIn(demandeurEmploi);
        return new HttpEntity<AgepiPEIOIn>(agepiPEIOIn, headers);
    }

    private AgepiPEIOIn createAgepiPEIOIn(DemandeurEmploi demandeurEmploi) {
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
        agepiPEIOIn.setTypeIntensite("mensuelle");
        return agepiPEIOIn;
    }
}
