package com.mariott.rms.elevate.display.domain;

public class GNRCount {
	
	String property;
	Integer gnrCount;
	
	public GNRCount() {}
	
	public GNRCount(String property, Integer gnrCount) {
		this.property = property;
		this.gnrCount = gnrCount;
	}
	
	public String getProperty() {
		return property;
	}
	public void setProperty(String property) {
		this.property = property;
	}
	public Integer getGnrCount() {
		return gnrCount;
	}
	public void setGnrCount(Integer gnrCount) {
		this.gnrCount = gnrCount;
	}
	
	

}
