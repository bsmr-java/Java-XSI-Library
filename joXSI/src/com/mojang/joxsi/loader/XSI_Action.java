package com.mojang.joxsi.loader;

import java.util.Iterator;

/**
 * This class is a container for a template in the dotXSI file format, as specified by XSIFTK template reference.
 * 
 * <p>It's very sparsely documented.
 */
public class XSI_Action extends Template
{
	public float start;
	public float end;
	public int type;

	public void parse(RawTemplate block)
	{
		Iterator it = block.values.iterator();
		start = ((Float)it.next()).floatValue();
		end = ((Float)it.next()).floatValue();
		type = ((Integer)it.next()).intValue();
	}
}