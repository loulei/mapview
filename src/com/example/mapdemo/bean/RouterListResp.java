package com.example.mapdemo.bean;

import java.util.List;

public class RouterListResp extends BaseResp {

	private List<RouterBean> routers;

	public List<RouterBean> getRouters() {
		return routers;
	}

	public void setRouters(List<RouterBean> routers) {
		this.routers = routers;
	}
}
