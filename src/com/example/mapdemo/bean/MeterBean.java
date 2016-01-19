package com.example.mapdemo.bean;

import java.util.List;

public class MeterBean {
	private List<RouterBean> routerBeans;
	private int index;
	public List<RouterBean> getRouterBeans() {
		return routerBeans;
	}
	public void setRouterBeans(List<RouterBean> routerBeans) {
		this.routerBeans = routerBeans;
	}
	public int getIndex() {
		return index;
	}
	public void setIndex(int index) {
		this.index = index;
	}
}
