package fr.poleemploi.estime.commun.enumerations;

public enum HttpHeaderParams {

	LOCALHOST("localhost"),
	LOCALHOST_IP("127.0.0.1"),
	RECETTE("recette");

	private String param;

	HttpHeaderParams(String param) {
		this.param = param;
	}

	public String getParam() {
		return param;
	}

}
