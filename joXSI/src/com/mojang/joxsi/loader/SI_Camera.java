package com.mojang.joxsi.loader;

import java.util.Iterator;

/**
 * Describes the camera data.
 * This class is a container for a template in the dotXSI file format, as specified by XSIFTK template reference.
 * 
 * <p>It's very sparsely documented.
 * @author Notch
 * @author Egal
 */
public class SI_Camera extends Template
{
	public float posx, posy, posz;  
	public float intx, inty, intz;  
	public float roll; 
	public float fieldOfView;  
	public float nearPlane;  
	public float farPlane;  

	public void parse(RawTemplate block)
	{
		Iterator it = block.values.iterator();
		posx = ((Float)it.next()).floatValue();
		posy = ((Float)it.next()).floatValue();
		posz = ((Float)it.next()).floatValue();
		intx = ((Float)it.next()).floatValue();
		inty = ((Float)it.next()).floatValue();
		intz = ((Float)it.next()).floatValue();
		roll = ((Float)it.next()).floatValue();
		fieldOfView = ((Float)it.next()).floatValue();
		nearPlane = ((Float)it.next()).floatValue();
		farPlane = ((Float)it.next()).floatValue();
	}
}