package com.mojang.joxsi.loader;

import java.util.Iterator;

/**
 * This class represents an RGB colour from a dotXSI model.
 * 
 * <p>This class is a container for a template in the dotXSI file format, as specified by XSIFTK template reference.
 * 
 * <p>It's very sparsely documented.
 * @author Notch
 * @author Egal
 */
public class ColorRGB
{
    /** The red value of this colour. */
	public float r;
    /** The green value of this colour. */
	public float g;
    /** The blue value of this colour. */
	public float b;

    /**
     * Construct a new ColorRGB instance with the specified Red, Green, and Blue
     * values.
     * 
     * @param r
     *            The red value of this colour.
     * @param g
     *            The green value of this colour.
     * @param b
     *            The blue value of this colour.
     */
	public ColorRGB(float r, float g, float b)
	{
		this.r = r;
		this.g = g;
		this.b = b;
	}
	
    /**
     * Construct a new ColorRGB instance with the Red, Green, and Blue
     * values specified in the Iterator.
     * 
     * @param it
     *            an Iterator.
     */
	public ColorRGB(Iterator<Object> it)
	{
		this(((Float)it.next()).floatValue(), ((Float)it.next()).floatValue(), ((Float)it.next()).floatValue());
	}

    @Override
    public String toString() {
        return super.toString() + " - Red: " + r + ", Green: " + g + ", Blue: " + b;
    }
}