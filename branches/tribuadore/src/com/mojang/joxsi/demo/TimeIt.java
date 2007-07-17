package com.mojang.joxsi.demo;


/**
 * 
 * @author Milbo
 *just for time
 */
public class TimeIt {

	private long starttime;
	
	TimeIt(){
		resetTime();
	}
	
 	public void resetTime(){
		starttime=System.currentTimeMillis();
	}
	
	public long getTime(){
		return System.currentTimeMillis()- starttime ;		
	}
	
}
