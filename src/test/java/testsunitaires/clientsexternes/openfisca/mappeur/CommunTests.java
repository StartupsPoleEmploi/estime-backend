package testsunitaires.clientsexternes.openfisca.mappeur;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import fr.poleemploi.estime.commun.enumerations.AidesSociales;
import fr.poleemploi.estime.commun.enumerations.Organismes;
import fr.poleemploi.estime.services.ressources.AideSociale;
import fr.poleemploi.estime.services.ressources.InformationsPersonnelles;
import fr.poleemploi.estime.services.ressources.Personne;
import utile.tests.UtileTests;

public class CommunTests {    
    
    @Autowired
    protected UtileTests utileTests;
    
    protected AideSociale getAideSocialeAAH(float montant) {
        AideSociale aideAgepi = new AideSociale();
        aideAgepi.setCode(AidesSociales.ALLOCATION_ADULTES_HANDICAPES.getCode());
        aideAgepi.setNom(AidesSociales.ALLOCATION_ADULTES_HANDICAPES.getNom());
        aideAgepi.setOrganisme(Organismes.CAF.getNom());
        aideAgepi.setMontant(montant);
        aideAgepi.setReportee(false);
        return aideAgepi;
    }
    
    protected AideSociale getAideSocialeASS(float montant) {
        AideSociale aideAgepi = new AideSociale();
        aideAgepi.setCode(AidesSociales.ALLOCATION_SOLIDARITE_SPECIFIQUE.getCode());
        aideAgepi.setNom(AidesSociales.ALLOCATION_SOLIDARITE_SPECIFIQUE.getNom());
        aideAgepi.setOrganisme(Organismes.PE.getNom());
        aideAgepi.setMontant(montant);
        aideAgepi.setReportee(false);
        return aideAgepi;
    }
    
    protected Personne createPersonne(LocalDate dateNaissance) {
        Personne personne1 = new Personne();
        InformationsPersonnelles informationsPersonnelles = new InformationsPersonnelles();
        informationsPersonnelles.setDateNaissance(dateNaissance);
        personne1.setInformationsPersonnelles(informationsPersonnelles);
        return personne1;
    }
    
    protected void createPersonne(List<Personne> personnesACharge, LocalDate dateNaissance) {
        Personne personne1 = new Personne();
        InformationsPersonnelles informationsPersonnelles = new InformationsPersonnelles();
        informationsPersonnelles.setDateNaissance(dateNaissance);
        personne1.setInformationsPersonnelles(informationsPersonnelles);
        personnesACharge.add(personne1);
    }
}
