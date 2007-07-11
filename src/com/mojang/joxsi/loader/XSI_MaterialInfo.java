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

    @Override
	public void parse(RawTemplate block)
	{
		Iterator<Object> it = block.values.iterator();
		uWrap = ((Integer)it.next()).intValue();
		vWrap = ((Integer)it.next()).intValue();
	}

    @Override
    public String toString() {
        return super.toString() + ", U-Wrapping: " + uWrap + ", V-Wrapping: " + vWrap;
    }
    
}