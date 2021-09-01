package fr.poleemploi.estime.services.ressources;

import org.springframework.web.servlet.ModelAndView;

public class AideDetails {
    private String code;
    private ModelAndView detail;
    
    
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public ModelAndView getDetail() {
		return detail;
	}
	public void setDetail(ModelAndView detail) {
		this.detail = detail;
	}
}
