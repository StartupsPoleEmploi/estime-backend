package fr.poleemploi.estime.services.ressources;

public class BeneficiaireAides {
    
    private boolean isBeneficiaireAAH;
    private boolean isBeneficiaireARE;
    private boolean isBeneficiaireASS;
    private boolean isBeneficiaireRSA;
    private boolean isBeneficiairePensionInvalidite;
      
    public boolean isBeneficiairePensionInvalidite() {
        return isBeneficiairePensionInvalidite;
    }
    public void setBeneficiairePensionInvalidite(boolean isBeneficiairePensionInvalidite) {
        this.isBeneficiairePensionInvalidite = isBeneficiairePensionInvalidite;
    }
    public boolean isBeneficiaireAAH() {
        return isBeneficiaireAAH;
    }
    public void setBeneficiaireAAH(boolean isBeneficiaireAAH) {
        this.isBeneficiaireAAH = isBeneficiaireAAH;
    }
    public boolean isBeneficiaireARE() {
        return isBeneficiaireARE;
    }
    public void setBeneficiaireARE(boolean isBeneficiaireARE) {
        this.isBeneficiaireARE = isBeneficiaireARE;
    }
    public boolean isBeneficiaireASS() {
        return isBeneficiaireASS;
    }
    public void setBeneficiaireASS(boolean isBeneficiaireASS) {
        this.isBeneficiaireASS = isBeneficiaireASS;
    }
    public boolean isBeneficiaireRSA() {
        return isBeneficiaireRSA;
    }
    public void setBeneficiaireRSA(boolean isBeneficiaireRSA) {
        this.isBeneficiaireRSA = isBeneficiaireRSA;
    }
    
    @Override
    public String toString() {
        return "BeneficiaireAides [isBeneficiaireAAH=" + isBeneficiaireAAH + ", isBeneficiaireARE="
                + isBeneficiaireARE + ", isBeneficiaireASS=" + isBeneficiaireASS + ", isBeneficiaireRSA="
                + isBeneficiaireRSA + ", isBeneficiairePensionInvalidite=" + isBeneficiairePensionInvalidite + "]";
    }
}
