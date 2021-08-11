package testsunitaires.clientsexternes.openfisca.mappeur;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import fr.poleemploi.estime.commun.enumerations.Aides;
import fr.poleemploi.estime.commun.enumerations.Organismes;
import fr.poleemploi.estime.services.ressources.Aide;
import fr.poleemploi.estime.services.ressources.InformationsPersonnelles;
import fr.poleemploi.estime.services.ressources.Personne;
import utile.tests.UtileTests;

public class CommunTests {    
    
    @Autowired
    protected UtileTests utileTests;
    
    protected Aide getAideeAAH(float montant) {
        Aide aideAgepi = new Aide();
        aideAgepi.setCode(Aides.ALLOCATION_ADULTES_HANDICAPES.getCode());
        aideAgepi.setNom(Aides.ALLOCATION_ADULTES_HANDICAPES.getNom());
        aideAgepi.setOrganisme(Organismes.CAF.getNom());
        aideAgepi.setMontant(montant);
        aideAgepi.setReportee(false);
        return aideAgepi;
    }
    
    protected Aide getAideeASS(float montant) {
        Aide aideAgepi = new Aide();
        aideAgepi.setCode(Aides.ALLOCATION_SOLIDARITE_SPECIFIQUE.getCode());
        aideAgepi.setNom(Aides.ALLOCATION_SOLIDARITE_SPECIFIQUE.getNom());
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
