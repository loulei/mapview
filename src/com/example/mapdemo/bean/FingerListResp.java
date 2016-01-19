package com.example.mapdemo.bean;

import java.util.List;

public class FingerListResp extends BaseResp{

	private List<FingerPrint> fingers;

	public List<FingerPrint> getFingers() {
		return fingers;
	}

	public void setFingers(List<FingerPrint> fingers) {
		this.fingers = fingers;
	}
}
