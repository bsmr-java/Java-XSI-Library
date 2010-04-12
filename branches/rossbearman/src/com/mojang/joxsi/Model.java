package com.mojang.joxsi;

import java.util.List;
// Logger commented out until used
// import java.util.logging.Logger;

import com.mojang.joxsi.loader.SI_Mesh;
import com.mojang.joxsi.loader.SI_Model;
import com.mojang.joxsi.loader.SI_Scene;
import com.mojang.joxsi.loader.SI_Transform;
import com.mojang.joxsi.loader.Template;
import com.mojang.joxsi.loader.XSI_Action;
import com.mojang.joxsi.loader.XSI_Mixer;

/**
 * A model is a container for any number of meshes, and any number of sub models.
 *
 * <p>Each model has three transformations:
 * <ul>
 * <li>basepose - The default position of the model, relative to the scene root.
 * <li>transform - The default position of the model, relative to the parent model (root if there is no parent)
 * <li>animated - The current position after animation, relative to the parent model (root if there is no parent)
 * </ul>
 */
public class Model
{
    /** logger - Logging instance. Commented out until used*/
    // private static Logger logger = Logger.getLogger(Model.class.getName());
    /** Constant for converting Degrees to Radians = PI/180. Not used removed for time being*/
    // private static final float DEG_TO_RAD = (float)Math.PI / 180.0f;

    public Model[] models;
    public Mesh[] meshes;
    public Action[] actions;

    public String name;
    public String fullName;
    public SI_Transform transform;
    public SI_Transform basepose;
    public SI_Transform animated;

    public Envelope[] envelopes;
    public Model parent;
    public Scene scene;

    /**
     * Creates a new Model.
     *
     * <p>This is called automatically when the Scene is created, so there's rarely any need to call this manually.

     * @param scene the Scene this model belongs to
     * @param model the SI_Model data to read the model information from
     */
    public Model(Scene scene, SI_Model model)
    {
        this(scene, null, model);
    }

    /**
     * Creates a new Model.
     *
     * <p>This is called automatically when the Scene is created, so there's rarely any need to call this manually.
     *
     * @param scene the Scene this model belongs to
     * @param parent the parent model of this model
     * @param model the SI_Model data to read the model information from
     */
    public Model(Scene scene, Model parent, SI_Model model)
    {
        this.parent = parent;
        this.scene = scene;

        // Read short name (see javadoc for getModel() for details) and full name
        name = model.template_info;
        fullName = model.template_info;
        if (name.lastIndexOf('.') >= 0) name = name.substring(name.lastIndexOf('.') + 1);
        if (name.startsWith("MDL-")) name = name.substring("MDL-".length());

        // Add all sub models
        List<Template> modelTemplates = model.getAll(Template.SI_Model);
        models = new Model[modelTemplates.size()];
        for (int i = 0; i < modelTemplates.size(); i++)
        {
            models[i] = new Model(scene, this, (SI_Model)modelTemplates.get(i));
        }

        // Add all meshes
        List<Template> meshTemplates = model.getAll(Template.SI_Mesh);
        meshes = new Mesh[meshTemplates.size()];
        for (int i = 0; i < meshTemplates.size(); i++)
        {
            meshes[i] = new Mesh(scene, (SI_Mesh)meshTemplates.get(i));
        }

        // Add the basepose transform and local transform
        List<Template> transforms = model.getAll(Template.SI_Transform);
        // There two lines ensures the model gets a transform and a basepose if there's any transform
        // at all in the SI_Model.
        if (transforms.size() > 0) transform = (SI_Transform)transforms.get(0);
        basepose = transform;
        for (int i = 0; i < transforms.size(); i++)
        {
            SI_Transform transform = (SI_Transform)transforms.get(i);
            if (transform.template_info.startsWith("SRT-"))
            {
                this.transform = transform;
            }
            else if (transform.template_info.startsWith("BASEPOSE-"))
            {
                this.basepose = transform;
            }
            // TODO: If any additional transforms are found, store them in a Map
        }

        // Copy the local transform, if there is one, into the animated transform
        if (transform != null) animated = new SI_Transform(transform);

        // Add all actions (animations), if there is an XSI_Mixer
        XSI_Mixer mixer = (XSI_Mixer)model.get(Template.XSI_Mixer);
        if (mixer != null)
        {
            final SI_Scene si_scene = (SI_Scene)model.getRoot().get(Template.SI_Scene);
            final float frameRate = si_scene.getFrameRate();
            List<Template> xsiActions = mixer.getAll(Template.XSI_Action);
            actions = new Action[xsiActions.size()];
            for (int i = 0; i < xsiActions.size(); i++)
            {
                XSI_Action action = (XSI_Action)xsiActions.get(i);
                actions[i] = new Action(action, this);
                actions[i].setFrameRate(frameRate);
            }
        }
        else
        {
            actions = new Action[0];
        }
    }

    /**
     * Gets a sub model of this model, based on its short name.
     *
     * <p>The short name is everything in the long name after the last period, and without the "MDL-" prefix, if there is one.
     * So if the long name is "foo.bar.MDL-hello", the short name is "hello".
     *
     * <p>This method is recursive, and will search all grandchildren as well.
     *
     * @param aName the short name of the requested sub model
     * @return the sub model, or null if it wasn't found
     */
    public Model getModel(String aName)
    {
        if (aName.equals(this.name)) return this;

        for (int i = 0; i < models.length; i++)
        {
            Model model = models[i].getModel(aName);
            if (model != null) return model;
        }

        return null;
    }

    /**
     * Gets a sub model of this model, based on its long name name.
     *
     * <p>This method is recursive, and will search all grandchildren as well.
     *
     * @param aName the long name of the requested sub model
     * @return the sub model, or null if it wasn't found
     */
    public Model getModelFullName(String aName)
    {
        if (aName.equals(this.fullName)) return this;

        for (int i = 0; i < models.length; i++)
        {
            Model model = models[i].getModelFullName(aName);
            if (model != null) return model;
        }

        return null;
    }

    /**
     * Adds an Envelope to the list of envelopes that applies to this model.
     *
     * <p>After calling this model, you need to call compile() to rebuild the envelope data.
     *
     * @param envelope the Envelope that should be added
     */
    public void addEnvelope(Envelope envelope)
    {
        if (envelopes == null)
        {
            // There are no envelopes yet, so create an array, and add the new envelope
            envelopes = new Envelope[1];
            envelopes[0] = envelope;
        }
        else
        {
            // Grow the envelope list by one. Not very elegant.
            Envelope[] tmp = new Envelope[envelopes.length + 1];
            System.arraycopy(envelopes, 0, tmp, 0, envelopes.length);
            tmp[tmp.length - 1] = envelope;
            envelopes = tmp;
        }
    }

    /**
     * Compiles any data about the model that can be pre-computed.
     *
     * <p>This needs to be called after any important data about the model has changed, such as the list of envelopes.
     */
    public void compile()
    {
        if (envelopes != null)
        {
            // Set the envelopes for all meshes in this model.
            for (int i = 0; i < meshes.length; i++)
            {
                meshes[i].setEnvelopes(envelopes);
            }
        }

        // Recurses through all sub models and compiles them as well
        for (int i = 0; i < models.length; i++)
        {
            models[i].compile();
        }
    }

    /**
     * Sets the animated transformation to the local transformation.
     */
    public void resetAnimation()
    {
        animated.set(transform);
    }

    @Override
    public String toString()
    {
        return name + " " + fullName;
    }
}