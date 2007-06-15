package com.mojang.joxsi.loader;

import java.util.Iterator;

/**
 * This class is a container for a template in the dotXSI file format, as specified by XSIFTK template reference.
 * 
 * <p>It's very sparsely documented.
 */
public class SI_Transform extends Template
{
	public SI_Transform()
	{
	}
	
    public SI_Transform(SI_Transform trans)
    {
    	set(trans);
    }

	public void set(SI_Transform trans)
	{
		scalX = trans.scalX;
		scalY = trans.scalY;
		scalZ = trans.scalZ;
		rotX = trans.rotX;
		rotY = trans.rotY;
		rotZ = trans.rotZ;
		transX = trans.transX;
		transY = trans.transY;
		transZ = trans.transZ;
	}
    
    public float scalX, scalY, scalZ;  
	public float rotX, rotY, rotZ;  
	public float transX, transY, transZ;

	public void parse(RawTemplate block)
	{
		Iterator it = block.values.iterator();
		scalX = ((Float)it.next()).floatValue();
		scalY = ((Float)it.next()).floatValue();
		scalZ = ((Float)it.next()).floatValue();
		rotX = ((Float)it.next()).floatValue();
		rotY = ((Float)it.next()).floatValue();
		rotZ = ((Float)it.next()).floatValue();
		transX = ((Float)it.next()).floatValue();
		transY = ((Float)it.next()).floatValue();
		transZ = ((Float)it.next()).floatValue();
	}
}