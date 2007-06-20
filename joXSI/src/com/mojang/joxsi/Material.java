package com.mojang.joxsi;

import java.util.Iterator;
import java.util.Map;

import com.mojang.joxsi.loader.SI_Material;
import com.mojang.joxsi.loader.SI_Texture2D;
import com.mojang.joxsi.loader.Template;
import com.mojang.joxsi.loader.XSI_Image;
import com.mojang.joxsi.loader.XSI_Material;
import com.mojang.joxsi.loader.XSI_Shader;

/**
 * A Material defines the properties for a mesh.
 * 
 * <p>Right now, only material name and texture names are supported.
 */
public class Material
{
    public String name;
    public String imageName;

    /**
     * Creates a new material based on an SI_Material
     * 
     * <p>This is called automatically when the Scene is created, so there's rarely any need to call this manually.
     * 
     * @param material the SI_Material that contains the material information
     */
    public Material(SI_Material material)
    {
        name = material.template_info;
        imageName = ((SI_Texture2D)material.get(Template.SI_Texture2D)).imageName;
    }
    
    /**
     * Creates a new material based on an XSI_Material
     * 
     * <p>This is called automatically when the Scene is created, so there's rarely any need to call this manually.
     * 
     * @param material the SI_Material that contains the material information
     * @param images a map of String -> XSI_Image mappings, as defined by the XSI_ImageLibrary in the scene.
     */
    public Material(XSI_Material material, Map images)
    {
        name = material.template_info;
        for (Iterator it = material.getAll(Template.XSI_Shader).iterator(); it.hasNext();)
        {
            XSI_Shader shader = (XSI_Shader)it.next();
            
            // A "tex" connection maps to an entry in the ImageLibrary
            XSI_Shader.Connection conn = shader.getConnection("tex");
            if (conn!=null)
            {
                XSI_Image image = ((XSI_Image)images.get(conn.point));
                if (image==null)
                {
                    Template template = material.getRoot();
                    System.out.println("Null XSI_Image for \"" + conn.point + "\" in " + template + ". Loading model anyway..");
                }
                else
                {
                    imageName = image.filename;
                }
            }
        }
    }
}