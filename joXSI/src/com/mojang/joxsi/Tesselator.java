package com.mojang.joxsi;

import java.util.logging.Logger;
import java.util.List;

import com.mojang.joxsi.loader.SI_PolygonList.Polygon;
import com.mojang.joxsi.loader.SI_TriangleList.Triangle;

/**
 * The Tesselator class converts a polygon into a list of triangles.
 * 
 * <p>It is used to convert an SI_PolygonList into an SI_TriangleList
 */
public class Tesselator
{
    private static Logger logger = Logger.getLogger("com.mojang.joxsi.demo");
   /**
     * Converts a polygon into triangles, and adds them to a List
     *  
     * @param p the Polygon to tesselate
     * @param triangles the list to add the triangles to
     */
    public static void tesselate(Polygon p, List<Triangle> triangles)
    {
        if (p.nbVertices == 3) // Three vertices/sides! Trivial case, just create a new triangle.
        {
            Triangle t0 = new Triangle();
            t0.v = new int[] {p.v[0], p.v[1], p.v[2]};
            if (p.n != null) // Has normals?
            {
                t0.n = new int[] {p.n[0], p.n[1], p.n[2]};
            }
            if (p.c != null) // Has colors?
            {
                t0.c = new int[] {p.c[0], p.c[1], p.c[2]};
            }
            if (p.uv != null) // Has uv coordinates?
            {
                t0.uv = new int[p.uv.length][];
                for (int j = 0; j < p.uv.length; j++)
                {
                    t0.uv[j] = new int[] {p.uv[j][0], p.uv[j][1], p.uv[j][2]};
                }
            }
            triangles.add(t0);
        }
        else if (p.nbVertices == 4) // Four vertices/sides! Also trivial. Make two triangles.
        {
            Triangle t0 = new Triangle();
            Triangle t1 = new Triangle();
            t0.v = new int[] {p.v[0], p.v[1], p.v[2]};
            t1.v = new int[] {p.v[2], p.v[3], p.v[0]};
            if (p.n != null) // Has normals?
            {
                t0.n = new int[] {p.n[0], p.n[1], p.n[2]};
                t1.n = new int[] {p.n[2], p.n[3], p.n[0]};
            }
            if (p.c != null) // Has colors?
            {
                t0.c = new int[] {p.c[0], p.c[1], p.c[2]};
                t1.c = new int[] {p.c[2], p.c[3], p.c[0]};
            }
            if (p.uv != null) // Has uv coordinates?
            {
                t0.uv = new int[p.uv.length][];
                t1.uv = new int[p.uv.length][];
                for (int j = 0; j < p.uv.length; j++)
                {
                    t0.uv[j] = new int[] {p.uv[j][0], p.uv[j][1], p.uv[j][2]};
                    t1.uv[j] = new int[] {p.uv[j][2], p.uv[j][3], p.uv[j][0]};
                }
            }
            triangles.add(t0);
            triangles.add(t1);
        }
        else // No longer trivial..
        {
            // TODO: Tesselate polygons with n sides. The special cases for 3 and 4 sided polygons above are probably faster, so leave them.
            logger.info("Only three or four sides polygons are supported yet!");
        }
    }
}