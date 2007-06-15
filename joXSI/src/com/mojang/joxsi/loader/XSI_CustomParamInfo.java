package com.mojang.joxsi.loader;

import java.util.Iterator;

/**
 * This class is a container for a template in the dotXSI file format, as specified by XSIFTK template reference.
 * 
 * <p>It's very sparsely documented.
 */
public class XSI_CustomParamInfo extends Template
{
	public float min;
	public float max;
	public int capabilities;

	public void parse(RawTemplate block)
	{
		Iterator it = block.values.iterator();
		min = ((Number)it.next()).floatValue();
		max = ((Number)it.next()).floatValue();
		capabilities = ((Integer)it.next()).intValue();
	}
}