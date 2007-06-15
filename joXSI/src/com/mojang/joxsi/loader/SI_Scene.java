package com.mojang.joxsi.loader;

import java.util.Iterator;

/**
 * This class is a container for a template in the dotXSI file format, as specified by XSIFTK template reference.
 * 
 * <p>It's very sparsely documented.
 */
public class SI_Scene extends Template
{
	public String timing;
	public float start;
	public float end;
	public float frameRate;
	
	public void parse(RawTemplate block)
	{
		Iterator it = block.values.iterator();
		timing = (String)it.next();
		start = ((Number)it.next()).floatValue();
		end = ((Number)it.next()).floatValue();
		frameRate = ((Number)it.next()).floatValue();
	}
}