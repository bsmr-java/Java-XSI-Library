package com.mojang.joxsi.loader;

import java.util.Iterator;

/**
 * Describes the ambient color in the scene.
 * <p>This class is a container for a template in the dotXSI file format, as specified by XSIFTK template reference.
 * 
 * <p>It's very sparsely documented.
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

	public void parse(RawTemplate block)
	{
		Iterator it = block.values.iterator();
		red = ((Float)it.next()).floatValue();
		green = ((Float)it.next()).floatValue();
		blue = ((Float)it.next()).floatValue();
	}
}