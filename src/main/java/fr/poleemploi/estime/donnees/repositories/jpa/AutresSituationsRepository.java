package fr.poleemploi.estime.donnees.repositories.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import fr.poleemploi.estime.donnees.entities.AutresSituationsEntity;

public interface AutresSituationsRepository extends JpaRepository<AutresSituationsEntity, String>, JpaSpecificationExecutor<AutresSituationsEntity> {

    void deleteByIdEstime(String idEstime);
}
