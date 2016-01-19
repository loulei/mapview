package com.example.mapdemo.utils;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.example.mapdemo.bean.RouterBean;

public class CachWifiQue {
	private static CachWifiQue cachque;
	
	public static List<LinkedList<RouterBean>>  cachlst; 
	
	private static boolean first=true;
	
	public static CachWifiQue getInstance(){
		if(cachque==null){
			cachque=new CachWifiQue(); 
		}
		return cachque;
	}
	
	private CachWifiQue(){} 
	
	public static void putLstQue(List<RouterBean> lst){
		if(first){
			first=false;
			if(lst!=null&&lst.size()>0){
				cachlst=new ArrayList<LinkedList<RouterBean>>(lst.size());
				for(int i=0;i<lst.size();i++){
					LinkedList<RouterBean> ll=new LinkedList<RouterBean>();
					cachlst.add(ll);
				}
			}
		}
		
		if(lst!=null&&cachlst.size()!=lst.size()){
			Logger.e("路由器数量和缓存队列数量不一致!");
			return ;
		}
		
		for(int i=0;i<cachlst.size();i++){
			cachlst.get(i).add(lst.get(i));
		}		
	}
	
	public static void poll(){
		for(int i=0;i<cachlst.size();i++){
			cachlst.get(i).poll();
		}	
	}
	
	public static int getQueSize(){ 
		for(int i=1;i<cachlst.size();i++){
			if(cachlst.get(0).size()!=cachlst.get(i).size()){
				return -1;
			}
		}
		return cachlst.get(0).size();
	}
	
	public static List<RouterBean> calAverage(){
		List<RouterBean> result=null;
		try{
			if(getQueSize()!=0&&getQueSize()!=-1){
				result=new ArrayList<RouterBean>();
				result.clear();
				for(int i=0;i<cachlst.size();i++){
					LinkedList<RouterBean> ls=cachlst.get(i);
					double sum=0;
					int indexsum = 0;
					RouterBean router=new RouterBean(ls.get(0));
					for(int j=0;j<ls.size();j++){//加权平均值，权重默认索引1-size
						 sum=sum+ls.get(j).getPercent()*(j+1)*(j+1);
						 indexsum += (j+1)*(j+1);
					}
					router.setPercent(sum/indexsum);
					result.add(router);
				}
				return result;
			}
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return result;		
	}
}
