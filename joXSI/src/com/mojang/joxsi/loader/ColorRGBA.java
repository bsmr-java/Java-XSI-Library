package com.mojang.joxsi.loader;

import java.util.Iterator;

/**
 * This class represents an RGBA colour from a dotXSI model, which is RGB with Alpha channel.
 *
 * <p>This class is a container for a template in the dotXSI file format, as specified by XSIFTK template reference.
 * 
 * <p>It's very sparsely documented.
 * @author Notch
 * @author Egal
 */
public class ColorRGBA
{
    /** The red value of this colour. */
    public float r;
    /** The green value of this colour. */
    public float g;
    /** The blue value of this colour. */
    public float b;
    /** The alpha value of this colour. */
    public float a;

    /**
     * Construct a new ColorRGBA instance with the specified Red, Green, Blue, and Alpha
     * values.
     * 
     * @param r
     *            The red value of this colour.
     * @param g
     *            The green value of this colour.
     * @param b
     *            The blue value of this colour.
     * @param a
     *            The alpha value of this colour.
     */
    public ColorRGBA(float r, float g, float b, float a)
    {
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = a;
    }

    /**
     * Construct a new ColorRGBA instance with the Red, Green, Blue, Alpha
     * values specified in the Iterator..
     * 
     * @param it
     *            an Iterator.
     */
    public ColorRGBA(Iterator<Object> it)
    {
        this(((Float)it.next()).floatValue(), ((Float)it.next()).floatValue(), ((Float)it.next()).floatValue(), ((Float)it.next()).floatValue());
    }

    @Override
    public String toString()
    {
        return super.toString() + " - Red: " + r + ", Green: " + g + ", Blue: " + b + ", Alpha: " + a;
    }
}
