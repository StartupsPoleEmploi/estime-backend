package fr.poleemploi.test.utile;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fr.poleemploi.estime.commun.enumerations.AidesSociales;
import fr.poleemploi.estime.commun.enumerations.Organismes;
import fr.poleemploi.estime.logique.simulateuraidessociales.utile.AideSocialeUtile;
import fr.poleemploi.estime.services.ressources.AideSociale;

@Component
public class BouchonAideSociale {

    @Autowired
    private AideSocialeUtile aideSocialesUtile;
    
    public AideSociale getAideSocialeAgepi(float montant) {
        AideSociale aideAgepi = new AideSociale();
        aideAgepi.setCode(AidesSociales.AGEPI.getCode());
        Optional<String> detailAideOptional = aideSocialesUtile.getDescription(AidesSociales.AGEPI.getNomFichierDetail());
        if(detailAideOptional.isPresent()) {
            aideAgepi.setDetail(detailAideOptional.get());    
        }
        aideAgepi.setNom(AidesSociales.AGEPI.getNom());
        aideAgepi.setOrganisme(Organismes.PE.getNom());
        aideAgepi.setMontant(montant);
        aideAgepi.setReportee(false);
        return aideAgepi;
    }
    
    public AideSociale getAideSocialeAAH(float montant) {
        AideSociale aideAgepi = new AideSociale();
        aideAgepi.setCode(AidesSociales.ALLOCATION_ADULTES_HANDICAPES.getCode());
        aideAgepi.setNom(AidesSociales.ALLOCATION_ADULTES_HANDICAPES.getNom());
        aideAgepi.setOrganisme(Organismes.CAF.getNom());
        aideAgepi.setMontant(montant);
        aideAgepi.setReportee(false);
        return aideAgepi;
    }
    
    public AideSociale getAideSocialeASS(float montant) {
        AideSociale aideAgepi = new AideSociale();
        aideAgepi.setCode(AidesSociales.ALLOCATION_SOLIDARITE_SPECIFIQUE.getCode());
        aideAgepi.setNom(AidesSociales.ALLOCATION_SOLIDARITE_SPECIFIQUE.getNom());
        aideAgepi.setOrganisme(Organismes.PE.getNom());
        aideAgepi.setMontant(montant);
        aideAgepi.setReportee(false);
        return aideAgepi;
    }
}
