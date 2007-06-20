package com.mojang.joxsi.loader;

import java.util.Iterator;

/**
 * Stores additional information about the material.
 * 
 * <p>This class is a container for a template in the dotXSI file format, as specified by XSIFTK template reference.
 * 
 * <p>It's very sparsely documented.
 * @author Notch
 * @author Egal
 */
public class XSI_MaterialInfo extends Template
{
    /**
     * U-Wrapping information. Possible values are:<br>
     * • 0 = Clamp<br>
     * • 1 = Repeat
     */
	public int uWrap;
    /**
     * V-Wrapping information. Possible values are:<br>
     * • 0 = Clamp<br>
     * • 1 = Repeat
     */
	public int vWrap;

	public void parse(RawTemplate block)
	{
		Iterator it = block.values.iterator();
		uWrap = ((Integer)it.next()).intValue();
		vWrap = ((Integer)it.next()).intValue();
	}
}