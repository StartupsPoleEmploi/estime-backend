package fr.poleemploi.estime.clientsexternes.poleemploiio.ressources;

import java.util.Date;

public class DetailIndemnisationPEIO {
    
    private boolean beneficiaireAideSolidarite;
    private boolean beneficiaireAssuranceChomage;
    private float indemnisationJournalierNet;
    private int dureeReliquat;
    private Date dateReliquat;
    private String codeIndemnisation;
    private boolean beneficiaireRSA;
    private boolean beneficiaireAAH;
    
    public boolean isBeneficiaireAssuranceChomage() {
        return beneficiaireAssuranceChomage;
    }
    public float getIndemnisationJournalierNet() {
        return indemnisationJournalierNet;
    }

    public void setIndemnisationJournalierNet(float indemnisationJournalierNet) {
        this.indemnisationJournalierNet = indemnisationJournalierNet;
    }
    public int getDureeReliquat() {
        return dureeReliquat;
    }
    public void setDureeReliquat(int dureeReliquat) {
        this.dureeReliquat = dureeReliquat;
    }
    public boolean isBeneficiaireAideSolidarite() {
        return beneficiaireAideSolidarite;
    }
    public void setBeneficiaireAideSolidarite(boolean beneficiaireAideSolidarite) {
        this.beneficiaireAideSolidarite = beneficiaireAideSolidarite;
    }
    public Date getDateReliquat() {
        return dateReliquat;
    }
    public void setDateReliquat(Date dateReliquat) {
        this.dateReliquat = dateReliquat;
    }
    public void setBeneficiaireAssuranceChomage(boolean beneficiaireAssuranceChomage) {
        this.beneficiaireAssuranceChomage = beneficiaireAssuranceChomage;
    }
    public String getCodeIndemnisation() {
        return codeIndemnisation;
    }
    public void setCodeIndemnisation(String codeIndemnisation) {
        this.codeIndemnisation = codeIndemnisation;
    }
    public boolean isBeneficiaireRSA() {
        return beneficiaireRSA;
    }
    public void setBeneficiaireRSA(boolean beneficiaireRSA) {
        this.beneficiaireRSA = beneficiaireRSA;
    }
    public boolean isBeneficiaireAAH() {
        return beneficiaireAAH;
    }
    public void setBeneficiaireAAH(boolean beneficiaireAAH) {
        this.beneficiaireAAH = beneficiaireAAH;
    }
    @Override
    public String toString() {
        return "DetailIndemnisationESD [beneficiaireAideSolidarite=" + beneficiaireAideSolidarite
                + ", beneficiaireAssuranceChomage=" + beneficiaireAssuranceChomage + ", indemnisationJournalierNet="
                + indemnisationJournalierNet + ", dureeReliquat=" + dureeReliquat + ", dateReliquat=" + dateReliquat
                + ", codeIndemnisation=" + codeIndemnisation + ", beneficiaireRSA=" + beneficiaireRSA
                + ", beneficiaireAAH=" + beneficiaireAAH + "]";
    }
}
