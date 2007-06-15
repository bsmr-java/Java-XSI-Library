package com.mojang.joxsi.loader;

import java.io.Serializable;
import java.util.Iterator;

/**
 * This class is a container for a template in the dotXSI file format, as specified by XSIFTK template reference.
 * 
 * <p>It's very sparsely documented.
 */
public class SI_FCurve extends Template
{
	public class FCurve implements Serializable
	{
		public float[] frames;
		public float[][] keyValues;
	}
	
	public String objectName;
	public String fcurve;
	public String interpolation;
	public int nbFcurves;
	public int nbKeyValues;
	public int nbKeys;
	public FCurve[] fcurves;

	public void parse(RawTemplate block)
	{
		Iterator it = block.values.iterator();
		objectName = (String)it.next();
		fcurve = ((String)it.next()).intern();
		interpolation = (String)it.next();
		nbFcurves = ((Integer)it.next()).intValue();
		nbKeyValues = ((Integer)it.next()).intValue();
		nbKeys = ((Integer)it.next()).intValue();
		
		fcurves = new FCurve[nbFcurves];
		
		for (int i=0; i<nbFcurves; i++)
		{
			fcurves[i] = new FCurve();
			fcurves[i].frames = new float[nbKeys];
			fcurves[i].keyValues = new float[nbKeys][nbKeyValues];
			
			for (int j=0; j<nbKeys; j++)
			{
				fcurves[i].frames[j] = ((Float)it.next()).floatValue();
				for (int k=0; k<nbKeyValues; k++)
				{
					fcurves[i].keyValues[j][k] = ((Float)it.next()).floatValue();
				}
			}
		}
	}
}