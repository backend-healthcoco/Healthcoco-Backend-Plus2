package com.dpdocter.request;

public class ConsentPermissionRequest {
	
	private String accessMode = "VIEW";
	private DateRangeDataFlow dateRange;
    private String dataEraseAt;
    private ConsentFrequencyRequest frequency;
	public String getAccessMode() {
		return accessMode;
	}
	public void setAccessMode(String accessMode) {
		this.accessMode = accessMode;
	}
	public DateRangeDataFlow getDateRange() {
		return dateRange;
	}
	public void setDateRange(DateRangeDataFlow dateRange) {
		this.dateRange = dateRange;
	}
	public String getDataEraseAt() {
		return dataEraseAt;
	}
	public void setDataEraseAt(String dataEraseAt) {
		this.dataEraseAt = dataEraseAt;
	}
	public ConsentFrequencyRequest getFrequency() {
		return frequency;
	}
	public void setFrequency(ConsentFrequencyRequest frequency) {
		this.frequency = frequency;
	}
    
    
      
   
}
