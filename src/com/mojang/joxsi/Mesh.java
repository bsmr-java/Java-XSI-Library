package com.mojang.joxsi;

import java.util.ArrayList;
import java.util.List;

/** logger - Logging instance. Logging disabled until someone needs to use it*/
// import java.util.logging.Logger;
// import java.util.logging.Level;

import com.mojang.joxsi.loader.SI_Mesh;
import com.mojang.joxsi.loader.SI_PolygonList;
import com.mojang.joxsi.loader.SI_Shape;
import com.mojang.joxsi.loader.SI_TriangleList;
import com.mojang.joxsi.loader.Template;

/**
 * A mesh is a container for one or more TriangleLists.
 */
public class Mesh
{
    /** logger - Logging instance. Logging disabled until someone needs to use it*/
    // private static Logger logger = Logger.getLogger(Mesh.class.getName());
    /** Array of TriangleLists. */
    public TriangleList[] triangleLists;

    /**
     * Creates a new Mesh.
     * 
     * <p>This is called automatically when the Scene is created, so there's rarely any need to call this manually.
     * 
     * @param scene the Scene this Mesh is part of
     * @param mesh the SI_Mesh to extract the triangle lists from
     */    
    public Mesh(Scene scene, SI_Mesh mesh)
    {
        // The shape contains the actual vertex, color, normal and uv data.
        Shape shape = new Shape((SI_Shape)mesh.get(Template.SI_Shape));

        List<Template> triangleListTemplates = mesh.getAll(Template.SI_TriangleList);
        List<Template> polygonListTemplates = mesh.getAll(Template.SI_PolygonList);
        
        triangleLists = new TriangleList[triangleListTemplates.size() + polygonListTemplates.size()];
        
        // Add all existing SI_TriangleLists
        for (int i = 0; i < triangleListTemplates.size(); i++)
        {
            triangleLists[i] = new TriangleList(scene, shape, (SI_TriangleList)triangleListTemplates.get(i));
        }
        
        // Tesselate all SI_PolygonLists, and turn them into SI_TriangleLists. Then add those.
        for (int i = 0; i < polygonListTemplates.size(); i++)
        {
            triangleLists[triangleListTemplates.size() + i] = new TriangleList(scene, shape, tesselate((SI_PolygonList)polygonListTemplates.get(i)));
        }
        // TODO: Parse and handle TriangleStripLists. This includes adding support for rendering them.
    }

    /**
     * Tesselates an SI_PolygonList into an SI_TriangleList.
     * 
     * This method uses the Tesselator class.
     * 
     * @param list the SI_PolygonList to tesselate
     * @return the tesselated triangles, as an SI_TriangleList
     */
    private SI_TriangleList tesselate(SI_PolygonList list)
    {
        List<SI_TriangleList.Triangle> triangles = new ArrayList<SI_TriangleList.Triangle>();
        for (int i = 0; i < list.polygons.length; i++)
        {
            Tesselator.tesselate(list.polygons[i], triangles);
        }
     
        // Create a new SI_TriangleList and fill the values.
        SI_TriangleList triangleList = new SI_TriangleList();
        triangleList.nbTriangles = triangles.size();
        triangleList.elements = list.elements;
        triangleList.material = list.material;
        triangleList.triangles = triangles.toArray(new SI_TriangleList.Triangle[triangles.size()]);

        return triangleList;
    }

    /**
     * Sets the array of Envelopes that applies to this mesh.
     * 
     * This method just iterates over all TriangleLists and sets the envelopes 
     * 
     * @param envelopes the array of Envelopes that applies to this mesh
     */
    public void setEnvelopes(Envelope[] envelopes)
    {
        for (int i = 0; i < triangleLists.length; i++)
        {
            triangleLists[i].setEnvelopes(envelopes);
        }
    }
}