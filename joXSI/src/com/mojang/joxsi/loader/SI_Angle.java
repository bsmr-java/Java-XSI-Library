package com.mojang.joxsi.loader;

import java.util.Iterator;

/**
 * Indicates how angle values are expressed in the file.
 * If not specified, the values in the file should be considered as being expressed in degrees.
 * 
 * This class is a container for a template in the dotXSI file format, as specified by XSIFTK template reference.
 * 
 * <p>It's very sparsely documented.
 * @author Notch
 * @author Egal
 */
public class SI_Angle extends Template
{
    /**
     * Specifies angle representation:<br>
     * 0 = degrees<br>
     * 1 = radians
     */
	public int type;

	public void parse(RawTemplate block)
	{
		Iterator it = block.values.iterator();
		type = ((Integer)it.next()).intValue();
	}
}