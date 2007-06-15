package com.mojang.joxsi.loader;

import java.util.Iterator;

/**
 * This class is a container for a template in the dotXSI file format, as specified by XSIFTK template reference.
 * 
 * <p>It's very sparsely documented.
 */
public class XSI_Mixer extends Template
{
	public boolean autoTransition;
	public boolean active;
	public boolean normalize;
	public boolean quaternionMixing;
	public boolean removeSpins;
	public boolean maintainContinuity;

	public void parse(RawTemplate block)
	{
		Iterator it = block.values.iterator();
		autoTransition = ((Integer)it.next()).intValue()!=0;
		active = ((Integer)it.next()).intValue()!=0;
		normalize = ((Integer)it.next()).intValue()!=0;
		quaternionMixing = ((Integer)it.next()).intValue()!=0;
		removeSpins = ((Integer)it.next()).intValue()!=0;
		maintainContinuity = ((Integer)it.next()).intValue()!=0;
	}
}