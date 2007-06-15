package com.mojang.joxsi.loader;

import java.io.Serializable;
import java.util.ListIterator;

/**
 * This class is a container for a template in the dotXSI file format, as specified by XSIFTK template reference.
 * 
 * <p>It's very sparsely documented.
 */
public class SI_PolygonList extends Template
{
    public static final String NORMAL = "NORMAL";
    public static final String COLOR = "COLOR";
    public static final String TEX_COORD_UV = "TEX_COORD_UV";

    public boolean newVersion;
    public int nbPolygons;
    public String elements;
    public String material;
    public int nbTotalVertices;
    public Polygon[] polygons;

    public class Polygon implements Serializable
    {
        public int nbVertices;
        public int[] v;
        public int[] n;
        public int[] c;

        public int[][] uv;
    }

    public void parse(RawTemplate block)
    {
        ListIterator it = block.values.listIterator();

//        boolean newVersion = dot_xsi_header.majorVersion > 3 || (dot_xsi_header.majorVersion == 3 && dot_xsi_header.minorVersion >= 5);

        nbPolygons = ((Integer)it.next()).intValue();
        elements = (String)it.next();

		Object next = (Object)it.next();
		it.previous();

//		if (newVersion) // Material is only available in 3.5 and above
		if (next instanceof String) // Hack to support 3.0 files that include a material
			material = (String)it.next();

        nbTotalVertices = ((Integer)it.next()).intValue();
        polygons = new Polygon[nbPolygons];
        for (int i = 0; i < nbPolygons; i++)
        {
            polygons[i] = new Polygon();
            polygons[i].nbVertices = ((Integer)it.next()).intValue();
        }

        for (int i = 0; i < nbPolygons; i++)
        {
            polygons[i].v = new int[polygons[i].nbVertices];
            for (int j = 0; j < polygons[i].nbVertices; j++)
            {
                polygons[i].v[j] = ((Integer)it.next()).intValue();
            }
        }

        if (elements.indexOf(NORMAL) >= 0)
        {
            for (int i = 0; i < nbPolygons; i++)
            {
                polygons[i].n = new int[polygons[i].nbVertices];
                for (int j = 0; j < polygons[i].nbVertices; j++)
                {
                    polygons[i].n[j] = ((Integer)it.next()).intValue();
                }
            }
        }

		int uvN = 0;

		while (elements.indexOf(TEX_COORD_UV + uvN) >= 0)
			uvN++;

		if (uvN==0 && elements.indexOf(TEX_COORD_UV) >= 0)
			uvN++;

		for (int i = 0; i < nbPolygons; i++)
		{
			polygons[i].uv = new int[uvN][];
		}

        for (int uv = 0; uv < uvN; uv++)
        {
            for (int i = 0; i < nbPolygons; i++)
            {
                polygons[i].uv[uv] = new int[polygons[i].nbVertices];
                for (int j = 0; j < polygons[i].nbVertices; j++)
                {
                    polygons[i].uv[uv][j] = ((Integer)it.next()).intValue();
                }
            }
        }
    }
}