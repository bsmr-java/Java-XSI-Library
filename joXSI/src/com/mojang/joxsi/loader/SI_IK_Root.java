package com.mojang.joxsi.loader;

import java.util.Iterator;

/**
 * This class is a container for a template in the dotXSI file format, as specified by XSIFTK template reference.
 * 
 * <p>It's very sparsely documented.
 */
public class SI_IK_Root extends Template
{
	public int nbJoints;
	public String[] jointNames;
	public String effectorName;

	public void parse(RawTemplate block)
	{
		Iterator it = block.values.iterator();
		nbJoints = ((Integer)it.next()).intValue();
		jointNames = new String[nbJoints];
		for (int i=0; i<nbJoints; i++)
		{
			jointNames[i] = (String)it.next();
		}
		effectorName = (String)it.next();
	}
}