package com.mojang.joxsi.loader;

import java.io.Serializable;
import java.util.ListIterator;

/**
 * Specifies the positions, normals, colors, and uv coordinates for polygon 
 * vertices by indexing into the SI_Shape template that defines the mesh.
 * <p>This class is a container for a template in the dotXSI file format, as specified by XSIFTK template reference.
 * 
 * <p>This template changed between version 3.0 and 3.5. 
 * In v3.0, the template consisted of the number of UV indices and then the indices. 
 * In v3.5 and beyond, the template now contains the number of indices, 
 * UV space ("Texture_Projection"), and then the indices.
 * 
 * <p>It's very sparsely documented.
 * @author Notch
 * @author Egal
 * TODO identify if this is 3.5 and newer and then fill <code>newVersion</code> and parse <code>Material</code>.
 */
public class SI_PolygonList extends Template
{
    public static final String NORMAL = "NORMAL";
    public static final String COLOR = "COLOR";
    public static final String TEX_COORD_UV = "TEX_COORD_UV";

    public boolean newVersion;
    /** Number of polygons in the mesh. */
    public int nbPolygons;
    /**
     * Specifies what information is stored in the template. Can be one or more of the following:<br>
     * • NORMAL = Template contains Normals information (see ni below).<br>
     * • COLOR = Template contains Color information (see ci below).<br>
     * • TEX_COORD_UV# = Template contains texture UV coordinates information (see uvi below). 
     * The number sign (#) represents the number of the texture UV coordinates starting at 0.
     * 
     * <p>Note: The TEX_COORD_UV# element is only available in v3.5 and beyond.
     * <p>If more than one of these is present, use a vertical bar to separate the strings 
     * (for example, "NORMAL|COLOR|TEX_COORD_UV0|TEX_COORD_UV1").
     */
    public String elements;
    /**
     * Name of the material.
     * <p>Note: This is only available in v3.5 and beyond.
     */
    public String material;
    /** Number of vertices in total for this mesh object. */
    public int nbTotalVertices;
    /** Array of Polygons. */
    public Polygon[] polygons;

    /** One Polygon in the mesh. */
    public class Polygon implements Serializable
    {
        /** Number of vertices for a specific polygon in the mesh. */
        public int nbVertices;
        /** Index of a vertex position in the POSITIONS section of the SI_Shape template for the mesh. */
        public int[] v;
        /** Index of a normal in the NORMAL section of the SI_Shape template for the mesh. */
        public int[] n;
        /** Index of a color in the COLOR section of the SI_Shape template for the mesh. */
        public int[] c;
        /** 
         * Index of a UV coordinate in the TEX_COORD_UV section of the {@link SI_Shape } template for the mesh.
         * <p>Note: The uv element is only available in v3.5 and beyond.
         */
        public int[][] uv;
    }

    @Override
    public void parse(RawTemplate block)
    {
        ListIterator<Object> it = block.values.listIterator();

        //        boolean newVersion = dot_xsi_header.majorVersion > 3 || (dot_xsi_header.majorVersion == 3 && dot_xsi_header.minorVersion >= 5);

        nbPolygons = ((Integer)it.next()).intValue();
        elements = (String)it.next();

        Object next = it.next();
        it.previous();

        //  if (newVersion) // Material is only available in 3.5 and above
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

        if (elements.indexOf(COLOR) >= 0)
        {
            for (int i = 0; i < nbPolygons; i++)
            {
                polygons[i].c = new int[polygons[i].nbVertices];
                for (int j = 0; j < polygons[i].nbVertices; j++)
                {
                    polygons[i].c[j] = ((Integer)it.next()).intValue();
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

    @Override
    public String toString()
    {
        return super.toString() + " - Number of Polyygons: " + nbPolygons + ", material: " + material + ", number of vertices: " + nbTotalVertices;
    }
}