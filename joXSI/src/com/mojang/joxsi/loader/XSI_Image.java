package com.mojang.joxsi.loader;

import java.util.Iterator;

/**
 * This class is a container for a template in the dotXSI file format, as specified by XSIFTK template reference.
 * 
 * <p>It's very sparsely documented.
 */
public class XSI_Image extends Template
{
	public String filename;
	public int imageX;
	public int imageY;
	public int channel_count; 								
	public int bitsPerPixels; 								
	public float minimumX;
	public float maximumX;
	public float minimumY;
	public float maximumY;
	public float frame_rate; 
	public int frame_count;
	public int first;
	public int last;
	
	public void parse(RawTemplate block)
	{
		Iterator it = block.values.iterator();
		filename  = (String)it.next();
		imageX = ((Integer)it.next()).intValue();
		imageY = ((Integer)it.next()).intValue();
		channel_count = ((Integer)it.next()).intValue();
		bitsPerPixels = ((Integer)it.next()).intValue();
		minimumX = ((Float)it.next()).floatValue();
		maximumX = ((Float)it.next()).floatValue();
		minimumY = ((Float)it.next()).floatValue();
		maximumY = ((Float)it.next()).floatValue();
		frame_rate = ((Float)it.next()).floatValue();
		frame_count = ((Integer)it.next()).intValue();
		first = ((Integer)it.next()).intValue();
		last = ((Integer)it.next()).intValue();
	}
}