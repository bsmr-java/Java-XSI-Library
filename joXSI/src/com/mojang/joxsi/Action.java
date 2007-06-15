package com.mojang.joxsi;

import java.util.List;
import java.util.StringTokenizer;

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
    private XSI_Action action;
    private Interpolator[] interpolators;

    /**
     * Creates a new Action object from an XSI_Action and a target Model.
     * 
     * <p>This is called automatically when the Scene is created, so there's rarely any need to call this manually.
     * 
     * @param action The XSI_Action template that contains the SI_FCurves in this animation
     * @param model The model this Action belongs to
     */
    public Action(XSI_Action action, Model model)
    {
        this.action = action;

        // TODO: Implement other types of animation than FCurves
        if (action.type == 0) // FCurve action source 
        {
            List fcurves = action.getAll(Template.SI_FCurve);
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
                interpolators[i] = new Interpolator(target, fcurve, targetModel, action.template_info.endsWith("L")); 
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
            // HACK: This is just odd. Get the framerate from the scene, and use the length of the Action instead.
            interpolators[i].apply(time*31);
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
}