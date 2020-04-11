package com.dpdocter.beans;

public class MonitoringChartJasperBean {

	private Integer no = 0;
	
	private String time="";
	
	private String intake;
	
	private String outputDrain;
	
    private String bP;
    
    private String hR;
    
    private String sPO2;
    
    private String  anySpecialEventsAndStatDrugs;
    
    

	public MonitoringChartJasperBean() {
		super();
	}

	public MonitoringChartJasperBean(Integer no, String time, String intake, String outputDrain, String bP, String hR,
			String sPO2, String anySpecialEventsAndStatDrugs) {
		super();
		this.no = no;
		this.time = time;
		this.intake = intake;
		this.outputDrain = outputDrain;
		this.bP = bP;
		this.hR = hR;
		this.sPO2 = sPO2;
		this.anySpecialEventsAndStatDrugs = anySpecialEventsAndStatDrugs;
	}

	public Integer getNo() {
		return no;
	}

	public void setNo(Integer no) {
		this.no = no;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getIntake() {
		return intake;
	}

	public void setIntake(String intake) {
		this.intake = intake;
	}

	public String getOutputDrain() {
		return outputDrain;
	}

	public void setOutputDrain(String outputDrain) {
		this.outputDrain = outputDrain;
	}

	public String getbP() {
		return bP;
	}

	public void setbP(String bP) {
		this.bP = bP;
	}

	public String gethR() {
		return hR;
	}

	public void sethR(String hR) {
		this.hR = hR;
	}

	public String getsPO2() {
		return sPO2;
	}

	public void setsPO2(String sPO2) {
		this.sPO2 = sPO2;
	}

	public String getAnySpecialEventsAndStatDrugs() {
		return anySpecialEventsAndStatDrugs;
	}

	public void setAnySpecialEventsAndStatDrugs(String anySpecialEventsAndStatDrugs) {
		this.anySpecialEventsAndStatDrugs = anySpecialEventsAndStatDrugs;
	}
    
    
    
}
