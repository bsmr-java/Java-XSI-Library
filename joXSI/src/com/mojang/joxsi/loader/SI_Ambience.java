package com.mojang.joxsi.loader;

import java.util.Iterator;

/**
 * This class is a container for a template in the dotXSI file format, as specified by XSIFTK template reference.
 * 
 * <p>It's very sparsely documented.
 */
public class SI_Ambience extends Template
{
	public float red;
	public float green;
	public float blue;

	public void parse(RawTemplate block)
	{
		Iterator it = block.values.iterator();
		red = ((Float)it.next()).floatValue();
		green = ((Float)it.next()).floatValue();
		blue = ((Float)it.next()).floatValue();
	}
}