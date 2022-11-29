package fr.poleemploi.estime.commun.enumerations;

public enum TypesBeneficesMicroEntrepriseEnum {
    AR("achat_revente"), BIC("bic"), BNC("bnc");

    private String code;

    TypesBeneficesMicroEntrepriseEnum(String code) {
	this.code = code;
    }

    public String getCode() {
	return code;
    }
}
