package com.mojang.joxsi.loader;

import java.util.Iterator;

/**
 * This class is a container for a template in the dotXSI file format, as specified by XSIFTK template reference.
 * 
 * <p>It's very sparsely documented.
 */
public class ColorRGBA
{
	public float r;
	public float g;
	public float b;
	public float a;
	
	public ColorRGBA(float r, float g, float b, float a)
	{
		this.r = r;
		this.g = g;
		this.b = b;
		this.a = a;
	}
	
	public ColorRGBA(Iterator it)
	{
		this(((Float)it.next()).floatValue(), ((Float)it.next()).floatValue(), ((Float)it.next()).floatValue(), ((Float)it.next()).floatValue());
	}
}