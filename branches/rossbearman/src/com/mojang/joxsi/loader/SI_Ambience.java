package com.mojang.joxsi.loader;

import java.util.Iterator;

/**
 * Describes the ambient color in the scene.
 * <p>This class is a container for a template in the dotXSI file format, as specified by XSIFTK template reference.
 * <pre>
 * SI_Ambience
 * {
 *    &lt;red&gt;, 
 *    &lt;green&gt;, 
 *    &lt;blue&gt;,
 * }
 * 
 * SI_Ambience
 * {
 *    0.300000, 
 *    0.300000, 
 *    0.300000, 
 * }
 * </pre>
 * It's very sparsely documented.
 * @author Notch
 * @author Egal
 */
public class SI_Ambience extends Template
{
    /** The ambient red color in the scene. */
    public float red;
    /** The ambient green color in the scene. */
    public float green;
    /** The ambient blue color in the scene. */
    public float blue;

    @Override
    public void parse(RawTemplate block)
    {
        Iterator<Object> it = block.values.iterator();
        red = ((Float)it.next()).floatValue();
        green = ((Float)it.next()).floatValue();
        blue = ((Float)it.next()).floatValue();
    }

    @Override
    public String toString()
    {
        // TODO Auto-generated method stub
        return super.toString() + ", Red: " + red + ", Green: " + green + ", Blue: " + blue;
    }
}