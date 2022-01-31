package fr.poleemploi.estime.clientsexternes.poleemploiio.ressources;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UserInfoPEIOOut {

    private String sub;
    private String gender;
    @JsonProperty("family_name")
    private String familyName;
    @JsonProperty("given_name")
    private String givenName;
    private String email;

    public String getSub() {
	return sub;
    }
    public void setSub(String sub) {
	this.sub = sub;
    }
    public String getGender() {
	return gender;
    }
    public void setGender(String gender) {
	this.gender = gender;
    }
    public String getFamilyName() {
	return familyName;
    }
    public void setFamilyName(String familyName) {
	this.familyName = familyName;
    }
    public String getGivenName() {
	return givenName;
    }
    public void setGivenName(String givenName) {
	this.givenName = givenName;
    }
    public String getEmail() {
	return email;
    }
    public void setEmail(String email) {
	this.email = email;
    }

    @Override
    public String toString() {
	return "UserInfoRessource [sub=" + sub + ", gender=" + gender + ", familyName=" + familyName + ", givenName="
		+ givenName + ", email=" + email + "]";
    }
}
