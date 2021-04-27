package fr.poleemploi.estime.services.ressources;

public class BeneficiaireAidesSociales {
    
    private boolean isBeneficiaireAAH;
    private boolean isBeneficiaireARE;
    private boolean isBeneficiaireASS;
    private boolean isBeneficiaireRSA;
    private boolean isBeneficiairePensionInvalidite;
    private boolean isTopAAHRecupererViaApiPoleEmploi;
    private boolean isTopARERecupererViaApiPoleEmploi;
    private boolean isTopASSRecupererViaApiPoleEmploi;
    private boolean isTopRSARecupererViaApiPoleEmploi;
    
    
    public boolean isTopAAHRecupererViaApiPoleEmploi() {
        return isTopAAHRecupererViaApiPoleEmploi;
    }
    public void setTopAAHRecupererViaApiPoleEmploi(boolean isTopAAHRecupererViaApiPoleEmploi) {
        this.isTopAAHRecupererViaApiPoleEmploi = isTopAAHRecupererViaApiPoleEmploi;
    }
    public boolean isTopARERecupererViaApiPoleEmploi() {
        return isTopARERecupererViaApiPoleEmploi;
    }
    public void setTopARERecupererViaApiPoleEmploi(boolean isTopARERecupererViaApiPoleEmploi) {
        this.isTopARERecupererViaApiPoleEmploi = isTopARERecupererViaApiPoleEmploi;
    }
    public boolean isTopASSRecupererViaApiPoleEmploi() {
        return isTopASSRecupererViaApiPoleEmploi;
    }
    public void setTopASSRecupererViaApiPoleEmploi(boolean isTopASSRecupererViaApiPoleEmploi) {
        this.isTopASSRecupererViaApiPoleEmploi = isTopASSRecupererViaApiPoleEmploi;
    }
    public boolean isTopRSARecupererViaApiPoleEmploi() {
        return isTopRSARecupererViaApiPoleEmploi;
    }
    public void setTopRSARecupererViaApiPoleEmploi(boolean isTopRSARecupererViaApiPoleEmploi) {
        this.isTopRSARecupererViaApiPoleEmploi = isTopRSARecupererViaApiPoleEmploi;
    }
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
        return "BeneficiaireAidesSociales [isBeneficiaireAAH=" + isBeneficiaireAAH + ", isBeneficiaireARE="
                + isBeneficiaireARE + ", isBeneficiaireASS=" + isBeneficiaireASS + ", isBeneficiaireRSA="
                + isBeneficiaireRSA + ", isBeneficiairePensionInvalidite=" + isBeneficiairePensionInvalidite
                + ", isTopAAHRecupererViaApiPoleEmploi=" + isTopAAHRecupererViaApiPoleEmploi
                + ", isTopARERecupererViaApiPoleEmploi=" + isTopARERecupererViaApiPoleEmploi
                + ", isTopASSRecupererViaApiPoleEmploi=" + isTopASSRecupererViaApiPoleEmploi
                + ", isTopRSARecupererViaApiPoleEmploi=" + isTopRSARecupererViaApiPoleEmploi + "]";
    }
}
