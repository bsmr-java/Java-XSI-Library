package com.mojang.joxsi.loader;

import java.util.Iterator;

/**
 * This class is a container for a template in the dotXSI file format, as specified by XSIFTK template reference.
 * 
 * <p>It's very sparsely documented.
 */
public class XSI_MaterialInfo extends Template
{
	public int uWrap;
	public int vWrap;

	public void parse(RawTemplate block)
	{
		Iterator it = block.values.iterator();
		uWrap = ((Integer)it.next()).intValue();
		vWrap = ((Integer)it.next()).intValue();
	}
}