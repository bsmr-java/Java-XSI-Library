package com.mojang.joxsi.loader;

import java.io.Serializable;
import java.util.ListIterator;

/**
 * This class is a container for a template in the dotXSI file format, as specified by XSIFTK template reference.
 * 
 * <p>It's very sparsely documented.
 */
public class SI_TriangleList extends Template
{
	public static final String NORMAL = "NORMAL";
	public static final String COLOR = "COLOR";
	public static final String TEX_COORD_UV = "TEX_COORD_UV";

	public boolean newVersion;
	public int nbTriangles;
	public String elements;
	public String material;
	public Triangle[] triangles;

    /**
     * A triangle, with indexes for vertex position (v), normal (n), color (c) and several uv mappings (uv) 
     */
	public static class Triangle implements Serializable
	{
		public int[] v;
		public int[] n;
		public int[] c;

		public int[][] uv;
	}

	public void parse(RawTemplate block)
	{
		ListIterator it = block.values.listIterator();

		nbTriangles = ((Integer)it.next()).intValue();
		elements = (String)it.next();
		
		Object next = (Object)it.next();
		it.previous();

//		if (newVersion) // Material is only available in 3.5 and above
		if (next instanceof String) // Hack to support 3.0 files that include a material
			material = (String)it.next();

		triangles = new Triangle[nbTriangles];
		for (int i = 0; i < nbTriangles; i++)
		{
			triangles[i] = new Triangle();
			triangles[i].v = new int[3];
			for (int j = 0; j < 3; j++)
			{
				triangles[i].v[j] = ((Integer)it.next()).intValue();
			}
		}

		if (elements.indexOf(NORMAL) >= 0)
		{
			for (int i = 0; i < nbTriangles; i++)
			{
				triangles[i].n = new int[3];
				for (int j = 0; j < 3; j++)
				{
					triangles[i].n[j] = ((Integer)it.next()).intValue();
				}
			}
		}

		int uvN = 0;

		while (elements.indexOf(TEX_COORD_UV + uvN) >= 0)
			uvN++;

		if (uvN==0 && elements.indexOf(TEX_COORD_UV) >= 0)
			uvN++;

		for (int i = 0; i < nbTriangles; i++)
		{
			triangles[i].uv = new int[uvN][];
		}

		for (int uv = 0; uv < uvN; uv++)
		{
			for (int i = 0; i < nbTriangles; i++)
			{
				triangles[i].uv[uv] = new int[3];
				for (int j = 0; j < 3; j++)
				{
					triangles[i].uv[uv][j] = ((Integer)it.next()).intValue();
				}
			}
		}
	}
}