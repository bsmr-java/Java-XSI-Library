package com.mojang.joxsi;

import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.mojang.joxsi.loader.SI_FCurve;
import com.mojang.joxsi.loader.Template;
import com.mojang.joxsi.loader.XSI_Action;


/**
 * An Action contains and controls an animation.
 *
 * <p>To play an animation, get it from a Model, then call Apply before rendering the model.
 */
public class Action
{
    /** logger - Logging instance. */
    private static Logger logger = Logger.getLogger(Action.class.getName());
    private XSI_Action action;
    private Interpolator[] interpolators;
    /** Frames per second. The default is 30 FPS. */
    private float frameRate = 30.0F;

    /**
     * Creates a new Action object from an XSI_Action and a target Model.
     *
     * <p>This is called automatically when the Scene is created, so there's rarely any need to call this manually.
     *
     * @param aAction The XSI_Action template that contains the SI_FCurves in this animation
     * @param model The model this Action belongs to
     */
    public Action(XSI_Action aAction, Model model)
    {
        this.action = aAction;

        // TODO: Implement other types of animation than FCurves
        if (aAction.type == 0) // FCurve action source
        {
            List<Template> fcurves = aAction.getAll(Template.SI_FCurve);
            if (logger.isLoggable(Level.FINER))
            {
                logger.finer("Action, " + aAction.getTemplate_info() + ", has " + fcurves.size() + " fcurves");
            }
            interpolators = new Interpolator[fcurves.size()];
            for (int i=0; i<fcurves.size(); i++)
            {
                SI_FCurve fcurve = (SI_FCurve)fcurves.get(i);
                StringTokenizer st = new StringTokenizer(fcurve.fcurve, ".");
                String source = st.nextToken();
                st.nextToken(); // skip the middle | (HACK: I have no idea what kine.local means,
                st.nextToken(); // skip the middle | and noone feels like explaning it)
                String target = st.nextToken(); // Not used?

                Model targetModel = model.getModel(source);
                if (targetModel == null)
                {
                    logger.warning("Cannot create an Interpolator for SI_FCurve " + source + ".kine.local." + target
                            + " as there is no SI_Model whose name is: " + source + " in Scene: " + model.scene + ", Model: "
                            + model);
                }
                else
                {
                    interpolators[i] = new Interpolator(target, fcurve, targetModel, aAction.template_info.endsWith("L"));
                }
            }
        }
        else
        {
            if (logger.isLoggable(Level.FINE))
            {
                logger.fine("Unexpected action type: " + aAction.type);
            }
        }
    }

    /**
     * Applies the animation at a specific time to the target Model.
     *
     * <p>The resulting transformation is stored in the animated SI_Transform
     *
     * @param time How far into the animation the animation should be applied
     */
    public void apply(float time)
    {
        for (int i=0; i<interpolators.length; i++)
        {
            // The interpolator could be null if there was a problem creating the animation
            // The problem should already have been logged
            if (interpolators[i] != null)
            {
                // TODO HACK: This is just odd. Use the length of the Action instead.
                // This Hack is partially fixed by getting the framerate from the scene.
                interpolators[i].apply(time*frameRate);
            }
        }
    }

    /**
     * Gets the name of the animation.
     *
     * @return the name of the animation.
     */
    public String getName()
    {
        return action.template_info;
    }

    /**
     * Returns the length of the animation.
     *
     * <p>The type of the returned value is either in seconds or frames, depending on the timing of the scene.
     *
     * @return the length of the animation.
     */
    public float getLength()
    {
        return (action.end - action.start);
    }

    /**
     * Returns the frame rate of this action in frames per second (FPS).
     *
     * @return the frame rate of this action in frames per second (FPS).
     * @pre frameRate >= 0 // Must be positive
     * @post frameRate > 0 // Must be positive
     */
    public float getFrameRate()
    {
        return frameRate;
    }

    /**
     * Sets the frame rate of this action in frames per second (FPS).
     *
     * @param aFrameRate
     *            the frame rate of this action in frames per second (FPS).
     * @pre aFrameRate > 0 // Must be positive
     * @post frameRate > 0 // Must be positive
     */
    public void setFrameRate(float aFrameRate)
    {
        assert(aFrameRate > 0);
        frameRate = aFrameRate;
    }
}