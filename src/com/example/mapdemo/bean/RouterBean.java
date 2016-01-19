package com.example.mapdemo.bean;

public class RouterBean {
	private String ssid;
	private String bssid;
	private double percent;
	public String getSsid() {
		return ssid;
	}
	public void setSsid(String ssid) {
		this.ssid = ssid;
	}
	public String getBssid() {
		return bssid;
	}
	public void setBssid(String bssid) {
		this.bssid = bssid;
	}
	public double getPercent() {
		return percent;
	}
	public void setPercent(double percent) {
		this.percent = percent;
	}
	public RouterBean() {
		super();
		// TODO Auto-generated constructor stub
	}
	public RouterBean(String ssid, String bssid, double percent) {
		super();
		this.ssid = ssid;
		this.bssid = bssid;
		this.percent = percent;
	}
	
	public RouterBean(RouterBean res){
		this.bssid = res.bssid;
		this.percent = res.percent;
		this.ssid = res.ssid;
	}
}
