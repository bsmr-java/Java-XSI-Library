package com.mojang.joxsi;

import java.util.logging.Logger;
import java.util.Iterator;
import java.util.Map;

import com.mojang.joxsi.loader.ColorRGB;
import com.mojang.joxsi.loader.ColorRGBA;
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
    /** logger - Logging instance. */
    private static Logger logger = Logger.getLogger(Material.class.getName());
    public String name;
    /** Name of the image. */
    public String imageName;
    /** Ambient colour. */
    public ColorRGB ambientColor;
    /** Diffuse colour. */
    public ColorRGBA diffuseColor;
    /** Emissive colour. */
    public ColorRGB emissiveColor;
    /** Specular colour. */
    public ColorRGB specularColor;
    /** True if this material is being used for bumpmapping. */
    public boolean bumpInUse;

    /**
     * Creates a new material based on an SI_Material.
     * 
     * <p>This is called automatically when the Scene is created, so there's rarely any need to call this manually.
     * 
     * @param material the SI_Material that contains the material information
     */
    public Material(SI_Material material)
    {
        name = material.template_info;
        final SI_Texture2D lTexture2D = (SI_Texture2D)material.get(Template.SI_Texture2D);
        if (lTexture2D != null)
        {
            imageName = lTexture2D.imageName;
        }
        else
        {
            // This was discovered in the models/sellsman.xsi model
            logger.warning("SI_Material does not contain a SI_Texture2D template so cannot get the image name of the texture. material: "
                            + material);
        }
        ambientColor = material.ambientColor;
        diffuseColor = material.faceColor;
        emissiveColor = material.emissiveColor;
        specularColor = material.specularColor;
    }

    /**
     * Creates a new material based on an XSI_Material.
     * 
     * <p>This is called automatically when the Scene is created, so there's rarely any need to call this manually.
     * 
     * @param material the SI_Material that contains the material information
     * @param images a map of String -> XSI_Image mappings, as defined by the XSI_ImageLibrary in the scene.
     */
    public Material(XSI_Material material, Map<String, XSI_Image> images)
    {
   	 	XSI_Image image;
        name = material.template_info;
        for (Iterator<Template> it = material.getAll(Template.XSI_Shader).iterator(); it.hasNext();)
        {
            XSI_Shader shader = (XSI_Shader)it.next();

            // A "bump_inuse" Parameter indicates that this image is used for bump mapping
            XSI_Shader.Parameter parm = shader.getParameter("bump_inuse");
            bumpInUse = (parm != null && parm.value != null && parm.value instanceof Integer && ((Integer)parm.value).intValue() == 1);

            // A "tex" connection maps to an entry in the ImageLibrary
            XSI_Shader.Connection conn = shader.getConnection("tex");
            if (conn!=null)
            {
                image = images.get(conn.point);
                if (image==null)
                {
                    Template template = material.getRoot();
                    logger.info("Null XSI_Image for tex \"" + conn.point + "\" in XSI_Shader part of " + template
                            + " of material: " + material + ". Loading model anyway.");
                }
                else
                {
                    imageName = image.filename;
                    
                    // TODO Egal
//                    Template template = material.getRoot();
//                    logger.warning("OK XSI_Image " + image + " for tex \"" + conn.point + "\" in XSI_Shader part of " + template
//                            + " of material: " + material + ". Loading model anyway.");

                    if(bumpInUse) new BumpMapEffect(image).generateBumpMap();
                }
            }
        }
    }
}