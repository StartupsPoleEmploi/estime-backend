package fr.poleemploi.estime.donnees.entities;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name = "SuiviParcoursUtilisateur")
public class SuiviParcoursUtilisateurEntity {

    @Id
    @SequenceGenerator(name = "id_suivi_parcours_utilisateur_seq", sequenceName = "suivi_parcours_utilisateur_id_suivi_parcours_utilisateur_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "id_suivi_parcours_utilisateur_seq")
    private Integer idSuiviParcoursUtilisateur;

    @Column(name = "date_creation", columnDefinition = "TIMESTAMP")
    private LocalDateTime dateCreation;  

    private String idPoleEmploi;
    private String nom;
    private String email;
    private boolean esdBeneficiaireAssuranceChomage;
    private String esdCodeIndemnisation;
    private String prenom;
    private String suiviParcours;
    private String typePopulation;
    private String codePostal;


    public boolean isEsdBeneficiaireAssuranceChomage() {
	return esdBeneficiaireAssuranceChomage;
    }

    public void setEsdBeneficiaireAssuranceChomage(boolean esdBeneficiaireAssuranceChomage) {
	this.esdBeneficiaireAssuranceChomage = esdBeneficiaireAssuranceChomage;
    }

    public String getEsdCodeIndemnisation() {
	return esdCodeIndemnisation;
    }

    public void setEsdCodeIndemnisation(String esdCodeIndemnisation) {
	this.esdCodeIndemnisation = esdCodeIndemnisation;
    }

    public String getNom() {
	return nom;
    }

    public void setNom(String nom) {
	this.nom = nom;
    }

    public String getEmail() {
	return email;
    }

    public void setEmail(String email) {
	this.email = email;
    }

    public String getPrenom() {
	return prenom;
    }

    public void setPrenom(String prenom) {
	this.prenom = prenom;
    }

    public Integer getIdSuiviParcoursUtilisateur() {
	return idSuiviParcoursUtilisateur;
    }

    public void setIdSuiviParcoursUtilisateur(Integer idSuiviParcoursUtilisateur) {
	this.idSuiviParcoursUtilisateur = idSuiviParcoursUtilisateur;
    }

    public String getIdPoleEmploi() {
	return idPoleEmploi;
    }

    public void setIdPoleEmploi(String idPoleEmploi) {
	this.idPoleEmploi = idPoleEmploi;
    }

    public String getSuiviParcours() {
	return suiviParcours;
    }

    public void setSuiviParcours(String suiviParcours) {
	this.suiviParcours = suiviParcours;
    }

    public String getTypePopulation() {
	return typePopulation;
    }

    public void setTypePopulation(String typePopulation) {
	this.typePopulation = typePopulation;
    }

    public LocalDateTime getDateCreation() {
	return dateCreation;
    }

    public void setDateCreation(LocalDateTime dateCreation) {
	this.dateCreation = dateCreation;
    }

    public String getCodePostal() {
	return codePostal;
    }

    public void setCodePostal(String codePostal) {
	this.codePostal = codePostal;
    }

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = (prime * result) + ((this.idSuiviParcoursUtilisateur == null) ? 0 : this.idSuiviParcoursUtilisateur.hashCode());
	return result;
    }

    @Override
    public boolean equals(Object obj) {
	if (this == obj) {
	    return true;
	}
	if (obj == null) {
	    return false;
	}
	if (this.getClass() != obj.getClass()) {
	    return false;
	}
	SuiviParcoursUtilisateurEntity other = (SuiviParcoursUtilisateurEntity) obj;
	if (this.idSuiviParcoursUtilisateur == null) {
	    if (other.idSuiviParcoursUtilisateur != null) {
		return false;
	    }
	} else if (!this.idSuiviParcoursUtilisateur.equals(other.idSuiviParcoursUtilisateur)) {
	    return false;
	}
	return true;
    }

    @Override
    public String toString() {
	return "SuiviParcoursUtilisateurEntity [idSuiviParcoursUtilisateur=" + idSuiviParcoursUtilisateur + ", dateCreation=" + dateCreation + ", idPoleEmploi=" + idPoleEmploi + ", nom=" + nom
		+ ", email=" + email + ", esdBeneficiaireAssuranceChomage=" + esdBeneficiaireAssuranceChomage + ", esdCodeIndemnisation=" + esdCodeIndemnisation + ", prenom=" + prenom
		+ ", suiviParcours=" + suiviParcours + ", typePopulation=" + typePopulation + ", codePostal=" + codePostal + "]";
    }
}
