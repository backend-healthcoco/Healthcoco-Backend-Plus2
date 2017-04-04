package common.util.web;

public enum DatePatternEnum {
	dd_MM_yyyy("dd-MM-yyyy"), dd_MM_yyyy_HH_mm_ss("dd-MM-yyyy HH:mm:ss"),
	dd_MM_yyyy_SLASH_SEPRATOR("dd/MM/yyyy");
	
	private String pattern;
	
	private DatePatternEnum(String pattern){
		this.pattern=pattern;
	}
	
	public String getPattern(){
		return pattern;
	}
}
