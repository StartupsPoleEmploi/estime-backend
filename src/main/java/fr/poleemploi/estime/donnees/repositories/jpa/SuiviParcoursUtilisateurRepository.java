package fr.poleemploi.estime.donnees.repositories.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import fr.poleemploi.estime.donnees.entities.SuiviParcoursUtilisateurEntity;

public interface SuiviParcoursUtilisateurRepository extends JpaRepository<SuiviParcoursUtilisateurEntity, String>, JpaSpecificationExecutor<SuiviParcoursUtilisateurEntity> {

    void deleteByIdPoleEmploi(String idPoleEmploi);
}
