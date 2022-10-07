package fr.poleemploi.estime.donnees.entities;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "AutresSituations")
public class AutresSituationsEntity {

    @Id
    private String idEstime;

    @Column(name = "date_creation", columnDefinition = "TIMESTAMP")
    private LocalDateTime dateCreation;
    private boolean salaire;
    private boolean alternant;
    private boolean formation;
    private boolean cej;
    private boolean ada;
    private boolean securisationProfessionnelle;
    private boolean autre;
    private String autreContenu;

    public LocalDateTime getDateCreation() {
	return dateCreation;
    }

    public void setDateCreation(LocalDateTime dateCreation) {
	this.dateCreation = dateCreation;
    }

    public String getIdEstime() {
	return idEstime;
    }

    public void setIdEstime(String idEstime) {
	this.idEstime = idEstime;
    }

    public boolean isSalaire() {
	return salaire;
    }

    public void setSalaire(boolean salaire) {
	this.salaire = salaire;
    }

    public boolean isAlternant() {
	return alternant;
    }

    public void setAlternant(boolean alternant) {
	this.alternant = alternant;
    }

    public boolean isFormation() {
	return formation;
    }

    public void setFormation(boolean formation) {
	this.formation = formation;
    }

    public boolean isCej() {
	return cej;
    }

    public void setCej(boolean cej) {
	this.cej = cej;
    }

    public boolean isAda() {
	return ada;
    }

    public void setAda(boolean ada) {
	this.ada = ada;
    }

    public boolean isSecurisationProfessionnelle() {
	return securisationProfessionnelle;
    }

    public void setSecurisationProfessionnelle(boolean securisationProfessionnelle) {
	this.securisationProfessionnelle = securisationProfessionnelle;
    }

    public boolean isAutre() {
	return autre;
    }

    public void setAutre(boolean autre) {
	this.autre = autre;
    }

    public String getAutreContenu() {
	return autreContenu;
    }

    public void setAutreContenu(String autreContenu) {
	this.autreContenu = autreContenu;
    }

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = (prime * result) + ((this.idEstime == null) ? 0 : this.idEstime.hashCode());
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
	AutresSituationsEntity other = (AutresSituationsEntity) obj;
	if (this.idEstime == null) {
	    if (other.idEstime != null) {
		return false;
	    }
	} else if (!this.idEstime.equals(other.idEstime)) {
	    return false;
	}
	return true;
    }

    @Override
    public String toString() {
	return "AutresSituationsEntity [idEstime=" + idEstime + ", dateCreation=" + dateCreation + ", salaire=" + salaire + ", alternant=" + alternant + ", formation=" + formation
		+ ", cej=" + cej + ", ada=" + ada + ", securisationProfessionnelle=" + securisationProfessionnelle + ", autre=" + autre + ", autreContenu=" + autreContenu + "]";
    }
}
