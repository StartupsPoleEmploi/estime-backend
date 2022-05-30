package fr.poleemploi.estime.logique.simulateur.aides.poleemploi.utile;

import java.util.ArrayList;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fr.poleemploi.estime.commun.enumerations.AideEnum;
import fr.poleemploi.estime.commun.enumerations.MessageInformatifEnum;
import fr.poleemploi.estime.commun.enumerations.OrganismeEnum;
import fr.poleemploi.estime.logique.simulateur.aides.utile.AideUtile;
import fr.poleemploi.estime.logique.simulateur.aides.utile.SimulateurAidesUtile;
import fr.poleemploi.estime.services.ressources.Aide;

@Component
public class AgepiUtile {

    public static final int AGE_MAX_ENFANT = 10;

    @Autowired
    private SimulateurAidesUtile simulateurAidesUtile;

    @Autowired
    private AideUtile aideUtile;

    /** Qui peut percevoir l'Agepi ?
     *   Vous pouvez percevoir l'Agepi si vous remplissez toutes les conditions suivantes :
     *     Vous êtes demandeur d'emploi et vous allez reprendre une activité (qui est en cours d'activité dans le cadre d'un emploi d'avenir).
     *     Vous êtes créateur ou repreneur d'entreprise.
     *     Vous n'êtes pas indemnisé par Pôle emploi ou votre allocation chômage journalière est inférieure ou égale à 29,38€, 14.68€ pour Mayotte.
     *     Vous élevez seul 1 ou plusieurs enfants de moins de 10 ans dont vous avez la charge et la garde.
     * @param numeroMoisSimule 
     */
    public boolean isAgepiAVerser(int numeroMoisSimule) {
	return simulateurAidesUtile.isPremierMois(numeroMoisSimule);
    }

    public Aide creerAideAgepi(float montantAide) {
	ArrayList<String> messagesAlerte = new ArrayList<>();
	messagesAlerte.add(MessageInformatifEnum.AGEPI_IDF.getMessage());
	return aideUtile.creerAide(AideEnum.AGEPI, Optional.of(OrganismeEnum.PE), Optional.of(messagesAlerte), false, montantAide);
    }
}
