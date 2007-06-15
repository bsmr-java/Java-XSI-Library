package com.mojang.joxsi.loader;

import java.util.Iterator;

/**
 * This class is a container for a template in the dotXSI file format, as specified by XSIFTK template reference.
 * 
 * <p>It's very sparsely documented.
 */
public class XSI_ImageFX extends Template
{
	public float hue;  
	public float gain;  
	public float saturation;  
	public float brightness;  
	public float radius;  
	public float amount;  
	public int blurAlpha;  
	public int type; 
	public int scaleX;  
	public int scaleY;  
	public int horizontal;  
	public int vertical;  
	public int conversion; 
	
	public void parse(RawTemplate block)
	{
		Iterator it = block.values.iterator();
		hue = ((Float)it.next()).floatValue();  
		gain = ((Float)it.next()).floatValue();  
		saturation = ((Float)it.next()).floatValue();  
		brightness = ((Float)it.next()).floatValue();  
		radius = ((Float)it.next()).floatValue();  
		amount = ((Float)it.next()).floatValue();  
		blurAlpha = ((Integer)it.next()).intValue();  
		type = ((Integer)it.next()).intValue(); 
		scaleX = ((Integer)it.next()).intValue();  
		scaleY = ((Integer)it.next()).intValue();  
		horizontal = ((Integer)it.next()).intValue();  
		vertical = ((Integer)it.next()).intValue();  
		conversion = ((Integer)it.next()).intValue(); 
	}
}