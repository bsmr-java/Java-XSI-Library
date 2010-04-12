package com.mojang.joxsi;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.mojang.joxsi.loader.DotXSILoader;
import com.mojang.joxsi.loader.ParseException;
import com.mojang.joxsi.loader.RootTemplate;
import com.mojang.joxsi.loader.SI_Envelope;
import com.mojang.joxsi.loader.SI_EnvelopeList;
import com.mojang.joxsi.loader.SI_Material;
import com.mojang.joxsi.loader.SI_MaterialLibrary;
import com.mojang.joxsi.loader.SI_Model;
import com.mojang.joxsi.loader.SI_Scene;
import com.mojang.joxsi.loader.Template;
import com.mojang.joxsi.loader.XSI_Image;
import com.mojang.joxsi.loader.XSI_ImageLibrary;
import com.mojang.joxsi.loader.XSI_Material;

/**
 * A Scene is result of any loaded XSI file.
 *
 * <p>It contains a list of models, a list of envelopes, a list of materials, and a list of images to be used by the materials.
 */
public class Scene
{
    /** logger - Logging instance. Commented out until used*/
    // private static Logger logger = Logger.getLogger(Scene.class.getName());
    /** Array of Models in the Scene. */
    public Model[] models;
    /** The RootTemplate of the Scene. */
    public RootTemplate root;
    /** Array of Envelopes in the Scene. */
    public Envelope[] envelopes;
    /** Map of Materials in the Scene. */
    public Map<String, Material> materials = new HashMap<String, Material>();
    /** Map of Images in the Scene. */
    public Map<String, XSI_Image> images = new HashMap<String, XSI_Image>();
    /** The base path of the Scene, used to convert relative (texture) paths to absolute. */
    public String basePath;
    /**
     * Template_info field from the <code>SI_Scene</code>, which normally contains a unique
     * identifier or name, which is very useful when logging an error so the
     * problematic dotXSI model can be found.
     */
    public String template_info;

    /**
     * Factory method for loading a Scene from an input stream.
     *
     * <p>This method calls new Scene(DotXSILoader.load(in));
     *
     * @param in the input stream to read the scene from. This input stream needs to contain a valid XSI file
     * @return a new Scene.
     * @throws IOException if there's an io error.
     * @throws ParseException if the parsing fails for any reason
     */
    public static Scene load(InputStream in) throws IOException, ParseException
    {
        return new Scene(DotXSILoader.load(in));
    }

    /**
     * Factory method for loading a Scene from an input stream.
     *
     * <p>This method calls new Scene(DotXSILoader.load(in), basePath);
     *
     * @param in the input stream to read the scene from. This input stream needs to contain a valid XSI file
     * @param basePath the absolute path to the scene, without the filename.
     * @return a new Scene.
     * @throws IOException if there's an io error.
     * @throws ParseException if the parsing fails for any reason
     */
    public static Scene load(InputStream in, String basePath) throws IOException, ParseException
    {
        return new Scene(DotXSILoader.load(in), basePath);
    }

    /**
     * Creates a new Scene from the specified root template.
     *
     * @param root the root template to build the scene from
     * @param basePath the absolute path to the scene, without the filename.
     */
    public Scene(RootTemplate root, String basePath)
    {
        this.initFromRootTemplate(root);
        this.basePath = basePath;
    }

    /**
     * Creates a new Scene from the specified root template.
     *
     * @param root the root template to build the scene from
     */
    public Scene(RootTemplate root)
    {
        this.initFromRootTemplate(root);
    }

    private void initFromRootTemplate(RootTemplate root)
    {
        this.root = root;

        // Get some information from the SI_Scene template
        final SI_Scene si_scene = (SI_Scene)root.get(Template.SI_Scene);
        template_info = si_scene.template_info;

        // Add all models
        List<Template> modelTemplates = root.getAll(Template.SI_Model);
        models = new Model[modelTemplates.size()];
        for (int i = 0; i < modelTemplates.size(); i++)
        {
            models[i] = new Model(this, (SI_Model)modelTemplates.get(i));
        }

        // Add all envelopes, and add them to their respective models.
        SI_EnvelopeList envelopeList = (SI_EnvelopeList)root.get(Template.SI_EnvelopeList);
        if (envelopeList != null)
        {
            envelopes = new Envelope[envelopeList.envelopes.length];
            for (int i = 0; i < envelopeList.envelopes.length; i++)
            {
                SI_Envelope envelope = envelopeList.envelopes[i];
                Model envelopeModel = getModelFullName(envelope.envelope); // Bone
                Model deformerModel = getModelFullName(envelope.deformer); // Skin mesh
                envelopes[i] = new Envelope(envelope.vertexWeights, envelopeModel, deformerModel);
                envelopeModel.addEnvelope(envelopes[i]); // Must compile() the model after this
            }
        }

        // Read the image library if there is one.
        XSI_ImageLibrary imageLibrary = (XSI_ImageLibrary)root.get(Template.XSI_ImageLibrary);
        if (imageLibrary != null)
        {
            for (Iterator<Template> it = imageLibrary.getAll(Template.XSI_Image).iterator(); it.hasNext();)
            {
                XSI_Image image = (XSI_Image)it.next();
                images.put(image.template_info, image);
            }
        }

        // Add all materials, if there is a materiallibrary. Both SI_Material and XSI_Materials get added.
        SI_MaterialLibrary materialLibrary = (SI_MaterialLibrary)root.get(Template.SI_MaterialLibrary);
        if (materialLibrary != null)
        {
            for (Iterator<Template> it = materialLibrary.getAll(Template.SI_Material).iterator(); it.hasNext();)
            {
                SI_Material material = (SI_Material)it.next();
                materials.put(material.template_info, new Material(material));
            }

            for (Iterator<Template> it = materialLibrary.getAll(Template.XSI_Material).iterator(); it.hasNext();)
            {
                XSI_Material material = (XSI_Material)it.next();
                materials.put(material.template_info, new Material(material, images));
            }
        }

        // Compile all models
        for (int i = 0; i < models.length; i++)
        {
            models[i].compile();
        }
    }

    /**
     * Searches the tree of models for the model with the specified full name.
     *
     * @param name the full name of the model
     * @return the model with the specified full name, or null if it can't be found
     */
    public Model getModelFullName(String name)
    {
        for (int i = 0; i < models.length; i++)
        {
            Model model = models[i].getModelFullName(name);
            if (model != null) return model;
        }

        return null;
    }

    /**
     * Returns the material with the specified name
     *
     * @param materialName the name of the requested material
     * @return the material with the specified name
     */
    public Material getMaterial(String materialName)
    {
        return materials.get(materialName);
    }

    @Override
    public String toString()
    {
        // TODO Auto-generated method stub
        return "Scene Info: " + template_info + ", path: " + basePath;
    }
}