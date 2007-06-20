package com.mojang.joxsi.loader;

import java.util.Iterator;

/**
 * Stores source action parameters.
 * <p>SOFTIMAGE|3D does not export this template. 
 * If an XSI file contains this template, SOFTIMAGE|3D ignores it.
 * 
 * <p>This class is a container for a template in the dotXSI file format, as specified by XSIFTK template reference.
 * 
 * <p>It's very sparsely documented.
 * @author Notch
 * @author Egal
 * @see XSI_Mixer
 */
public class XSI_Action extends Template
{
    /** Start time. */
	public float start;
    /** End time. */
	public float end;
    /**
     * 0 = FCurve action source<br>
     * 1 = Static value action source<br>
     * 2 = Expression source<br>
     * 3 = ClusterKey source<br>
     * 4 = Constraint source<br>
     * 5 = Compound action item source<br>
     * 6 = Shape compound action item source<br>
     * 7 = no flagged source
     */
	public int type;

	public void parse(RawTemplate block)
	{
		Iterator it = block.values.iterator();
		start = ((Float)it.next()).floatValue();
		end = ((Float)it.next()).floatValue();
		type = ((Integer)it.next()).intValue();
	}
}