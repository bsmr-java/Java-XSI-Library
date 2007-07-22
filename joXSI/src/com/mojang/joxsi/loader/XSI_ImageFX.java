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
	
	@Override
    public void parse(RawTemplate block) throws ParseException
	{
		Iterator<Object> it = block.values.iterator();
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
		
		if(hue < 0.0f || hue > 100.0f)
		    throw new ParseException("Illegal hue in XSI_ImageFX: "+hue);
		if(gain < 0.0f || gain > 100.0f)
            throw new ParseException("Illegal gain in XSI_ImageFX: "+gain);
		if(saturation < 0.0f || saturation > 100.0f)
            throw new ParseException("Illegal saturation in XSI_ImageFX: "+saturation);
		if(brightness < -100.0f || brightness > 100.0f)
            throw new ParseException("Illegal brightness in XSI_ImageFX: "+brightness);
		if(radius < 0.0f || radius > 20.0f)
            throw new ParseException("Illegal radius in XSI_ImageFX: "+radius);
		if(amount < 0.0f || amount > 1.0f)
            throw new ParseException("Illegal amount in XSI_ImageFX: "+amount);
		if(blurAlpha != 0 && blurAlpha != 1)
            throw new ParseException("Illegal blurAlpha in XSI_ImageFX: "+blurAlpha);
		if(type < 0 || type > 7)
            throw new ParseException("Illegal type in XSI_ImageFX: "+type);
		if(conversion != 0 && conversion != 1)
            throw new ParseException("Illegal conversion in XSI_ImageFX: "+conversion);
		// TODO: Add the 16 bits per channel variable
		//if( < 0.0f ||  > 100.0f)
        //    throw new ParseException("Illegal  in XSI_ImageFX: "+);
		
	}
}