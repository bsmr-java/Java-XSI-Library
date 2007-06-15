package com.mojang.joxsi.loader;

import java.io.Serializable;
import java.util.ListIterator;

/**
 * This class is a container for a template in the dotXSI file format, as specified by XSIFTK template reference.
 * 
 * <p>It's very sparsely documented.
 */
public class SI_Shape extends Template
{
	public static final String POSITION = "POSITION";
	public static final String NORMAL = "NORMAL";
	public static final String COLOR = "COLOR";
	public static final String TEX_COORD_UV = "TEX_COORD_UV";
	
	public static final String INDEXED  = "INDEXED";
	public static final String ORDERED = "ORDERED";

	public class ShapeArray implements Serializable
	{
		public int nbElements;
		public String elements;
		public int[] indexes;
		public float[] values;
		
		public boolean isType(String type)
		{
			return elements.startsWith(type);
		}
	}
	
    public int nbShapeArrays;
    public String layout;
    public ShapeArray[] shapeArrays;
    public boolean isIndexed; 

    public void parse(RawTemplate block)
    {
        ListIterator it = block.values.listIterator();
        nbShapeArrays = ((Integer)it.next()).intValue();
		layout = (String)it.next();
		isIndexed = layout.equals(INDEXED);
		
		shapeArrays = new ShapeArray[nbShapeArrays];
		for (int i=0; i<nbShapeArrays; i++)
		{
			shapeArrays[i] = new ShapeArray();
			shapeArrays[i].nbElements = ((Integer)it.next()).intValue();
			shapeArrays[i].elements = (String)it.next();

			int len = 0;			
			if (shapeArrays[i].isType(POSITION)) len = 3; // x, y, z 
			if (shapeArrays[i].isType(NORMAL)) len = 3; // x, y, z 
			if (shapeArrays[i].isType(COLOR)) len = 4; // r, g, b, a
			if (shapeArrays[i].isType(TEX_COORD_UV)) len = 2; // u, v
			
			shapeArrays[i].values = new float[shapeArrays[i].nbElements*len];
			if (isIndexed)
			{
				shapeArrays[i].indexes = new int[shapeArrays[i].nbElements];
			}
			
			Object o = it.next();
			if (o instanceof Float)
			{
				it.previous();
			}
			else
			{
//				System.out.println("Skipping "+o);
			}

			
			for (int j=0; j<shapeArrays[i].nbElements; j++)
			{
				if (isIndexed)
				{
					shapeArrays[i].indexes[j] = ((Number)it.next()).intValue();
				}

				for (int k=0; k<len; k++)
				{
					shapeArrays[i].values[j*len+k] = ((Float)it.next()).floatValue();
				}
			}
		}
    }
}