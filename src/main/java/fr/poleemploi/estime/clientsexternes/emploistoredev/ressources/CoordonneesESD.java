package fr.poleemploi.estime.clientsexternes.emploistoredev.ressources;

public class CoordonneesESD {

    private String adresse1;
    private String adresse2;
    private String adresse3;
    private String adresse4;
    private String codePostal;
    private String codeINSEE;
    private String libelleCommune;
    private String codePays;
    private String libellePays;
    public String getAdresse1() {
        return adresse1;
    }
    public void setAdresse1(String adresse1) {
        this.adresse1 = adresse1;
    }
    public String getAdresse2() {
        return adresse2;
    }
    public void setAdresse2(String adresse2) {
        this.adresse2 = adresse2;
    }
    public String getAdresse3() {
        return adresse3;
    }
    public void setAdresse3(String adresse3) {
        this.adresse3 = adresse3;
    }
    public String getAdresse4() {
        return adresse4;
    }
    public void setAdresse4(String adresse4) {
        this.adresse4 = adresse4;
    }
    public String getCodePostal() {
        return codePostal;
    }
    public void setCodePostal(String codePostal) {
        this.codePostal = codePostal;
    }
    public String getCodeINSEE() {
        return codeINSEE;
    }
    public void setCodeINSEE(String codeINSEE) {
        this.codeINSEE = codeINSEE;
    }
    public String getLibelleCommune() {
        return libelleCommune;
    }
    public void setLibelleCommune(String libelleCommune) {
        this.libelleCommune = libelleCommune;
    }
    public String getCodePays() {
        return codePays;
    }
    public void setCodePays(String codePays) {
        this.codePays = codePays;
    }
    public String getLibellePays() {
        return libellePays;
    }
    public void setLibellePays(String libellePays) {
        this.libellePays = libellePays;
    }
    @Override
    public String toString() {
        return "CoordonneesESD [adresse1=" + adresse1 + ", adresse2=" + adresse2 + ", adresse3=" + adresse3
                + ", adresse4=" + adresse4 + ", codePostal=" + codePostal + ", codeINSEE=" + codeINSEE
                + ", libelleCommune=" + libelleCommune + ", codePays=" + codePays + ", libellePays=" + libellePays
                + "]";
    }
}
