package com.mojang.joxsi;

import java.util.logging.Logger;
import com.mojang.joxsi.loader.SI_FCurve;
import com.mojang.joxsi.loader.SI_Transform;

/**
 * An Interpolator interpolates between the values in an SI_FCurve and applies them to a target model.
 */
public class Interpolator
{
    private static Logger logger = Logger.getLogger("com.mojang.joxsi.demo");
    public static final int INTERPOLATION_CONSTANT = 0;
    public static final int INTERPOLATION_LINEAR = 1;
    public static final int INTERPOLATION_HERMITE = 2;
    public static final int INTERPOLATION_CUBIC = 3;

    private static final int SCALING_X = 0;
    private static final int SCALING_Y = 1;
    private static final int SCALING_Z = 2;

    private static final int TRANSLATION_X = 3;
    private static final int TRANSLATION_Y = 4;
    private static final int TRANSLATION_Z = 5;

    private static final int ROTATION_X = 6;
    private static final int ROTATION_Y = 7;
    private static final int ROTATION_Z = 8;

    private int interpolation = INTERPOLATION_LINEAR;
    private int target;

    private Model model;
    private SI_FCurve curve;
    private boolean loop;

    /**
     * Creates a new Interpolator.
     * 
     * <p>This is called automatically when the Scene is created, so there's rarely any need to call this manually.
     * 
     * @param curve
     *        the FCurve containing the interpolation targets
     * @param model
     *        the Model the animation should be applied to
     * @param loop
     *        true if the animation should repeat (wrap around) or false if it should stop on the last frame
     */
    public Interpolator(SI_FCurve curve, Model model, boolean loop)
    {
        this(curve.fcurve, curve, model, loop);
    }

    /**
     * Creates a new Interpolator.
     * 
     * <p>This is called automatically when the Scene is created, so there's rarely any need to call this manually.
     * 
     * @param targetName
     *        what property of the model to animate
     * @param curve
     *        the FCurve containing the interpolation targets
     * @param model
     *        the Model the animation should be applied to
     * @param loop
     *        true if the animation should repeat (wrap around) or false if it should stop on the last frame
     */
    public Interpolator(String targetName, SI_FCurve curve, Model model, boolean loop)
    {
        this.loop = loop;
        this.curve = curve;
        this.model = model;

        // Parse the target name. (can this be done with cleaner code somehow?)
        if (targetName.equals("SCALING-X"))
            target = SCALING_X;
        else if (targetName.equals("SCALING-Y"))
            target = SCALING_Y;
        else if (targetName.equals("SCALING-Z"))
            target = SCALING_Z;

        else if (targetName.equals("TRANSLATION-X"))
            target = TRANSLATION_X;
        else if (targetName.equals("TRANSLATION-Y"))
            target = TRANSLATION_Y;
        else if (targetName.equals("TRANSLATION-Z"))
            target = TRANSLATION_Z;

        else if (targetName.equals("ROTATION-X"))
            target = ROTATION_X;
        else if (targetName.equals("ROTATION-Y"))
            target = ROTATION_Y;
        else if (targetName.equals("ROTATION-Z"))
            target = ROTATION_Z;

        // Sometimes it's scl*, pos*, and rot* instead, so let's support that..
        else if (targetName.equals("sclx"))
            target = SCALING_X;
        else if (targetName.equals("scly"))
            target = SCALING_Y;
        else if (targetName.equals("sclz"))
            target = SCALING_Z;

        else if (targetName.equals("posx"))
            target = TRANSLATION_X;
        else if (targetName.equals("posy"))
            target = TRANSLATION_Y;
        else if (targetName.equals("posz"))
            target = TRANSLATION_Z;

        else if (targetName.equals("rotx"))
            target = ROTATION_X;
        else if (targetName.equals("roty"))
            target = ROTATION_Y;
        else if (targetName.equals("rotz"))
            target = ROTATION_Z;

        else
        {
            if (curve != null)
                throw new IllegalArgumentException("I have no idea how to animate " + curve.fcurve + "!");
            else
                throw new IllegalArgumentException("I have no idea how to animate a null curve - target: " + targetName + "!");
        }

        // Parse the interpolation type.
        if (curve == null || curve.interpolation == null)
            throw new IllegalArgumentException("I have no idea how to animate a null curve or null interpolation type - target: " + targetName + "!");
        else if (curve.interpolation.equals("CONSTANT"))
            interpolation = INTERPOLATION_CONSTANT;
        else if (curve.interpolation.equals("HERMITE"))
            interpolation = INTERPOLATION_HERMITE;
        else if (curve.interpolation.equals("CUBIC"))
            interpolation = INTERPOLATION_CUBIC;
        else
            interpolation = INTERPOLATION_LINEAR;
    }

    /**
     * Cubic interpolation.. Might be right, might be wrong. 
     * @return the interpolated value.
     */
    private float cubic(float xA, float yA, float xB, float yB, float xC, float yC, float xD, float yD, float t)
    {
        float yE = yA + (yB - yA) * t;
        float yF = yB + (yC - yB) * t;
        float yG = yC + (yD - yC) * t;
        float yH = yE + (yF - yE) * t;
        float yI = yF + (yG - yF) * t;
        float yJ = yH + (yI - yH) * t;

        return yJ;
    }

    /**
     * Hermite interpolation. How does this work?
     * 
     * @param vPrev
     * @param vNext
     * @param in
     * @param out
     * @param t
     *            How far along are we between last time and next time?
     * @return the interpolated value
     * 
     * TODO describe what is actually happening
     */
    private float hermite(float vPrev, float vNext, float in, float out, float t)
    {
        float vPrevMultiplier = 2.0F * t * t * t - 3.0F * t * t + 1.0F;
        float vNextMultiplier = -2.0F * t * t * t + 3.0F * t * t;
        float inMultiplier = t * t * t - 2.0F * t * t + t;
        float outMultiplier = t * t * t - t * t;
        return vPrevMultiplier * vPrev + inMultiplier * in + vNextMultiplier * vNext + outMultiplier * out;
    }

    /**
     * Sets the interpolation method to be used by this interpolator.
     * 
     * <p>Must be one of the INTERPOLATION_* constants defined in this class.
     * 
     * @param interpolation
     *        the interpolation to be used
     */
    public void setInterpolation(int interpolation)
    {
        if (interpolation < 0 || interpolation > 3) 
            throw new IllegalArgumentException("Bad interpolation type: " + interpolation);
        this.interpolation = interpolation;
    }

    /**
     * Applies the animation to the target model.
     * 
     * @param time
     *        how far into the animation we should interpolate
     */
    public void apply(float time)
    {
        SI_Transform result = model.animated;
        SI_FCurve.FCurve curCurve = curve.fcurves[0];

        // Only apply the animation if there are frames in it
		if (curCurve.frames.length != 0) {
            float val;

            // If we're before the start of the animation, just return the first value.
            if (time <= curCurve.frames[0])
            {
                val = curCurve.keyValues[0][0];
            }
            else
            {
                int pos = 0;
    
                // Find the current frame position.
                while (pos < curCurve.frames.length && time > curCurve.frames[pos])
                    pos++;
    
                // Set up last value, next value, last time, and next time.
                float prevVal = curCurve.keyValues[pos - 1][0];
                float nextVal = 0;
    
                float prevTime = curCurve.frames[pos - 1];
                float nextTime = 0;
    
                if (pos < curCurve.frames.length)
                {
                    nextVal = curCurve.keyValues[pos][0];
                    nextTime = curCurve.frames[pos];
                }
                else if (loop) // Loop back to the first frame if the animation is a loop. Is this needed?
                {
                    nextVal = curCurve.keyValues[0][0];
                    nextTime = curCurve.frames[0];
                }
    
                // How far along are we between last time and next time?
                float interp = (time - prevTime) / (nextTime - prevTime);
    
                // Apply interpolation. Inline the hermite and cubic methods?
                switch (interpolation)
                {
                    case INTERPOLATION_CONSTANT:
                        val = prevVal;
                        break;
                    case INTERPOLATION_HERMITE:
                        float in = curCurve.keyValues[pos - 1][1];
                        float out = curCurve.keyValues[pos - 1][2];
                        val = hermite(prevVal, nextVal, in, out, interp);
                        break;
                    case INTERPOLATION_LINEAR:
                        val = prevVal + (nextVal - prevVal) * interp;
                        break;
                    case INTERPOLATION_CUBIC:
                        float prevXa;
                        float prevYa;
                        float nextXa;
                        float nextYa;
                        try
                        {
                            prevXa = curCurve.keyValues[pos - 1][3];
                            prevYa = curCurve.keyValues[pos - 1][4];
                            // check added to prevent array out of bounds exception at end of animation
                            if (pos < curCurve.keyValues.length)
                            {
                                nextXa = curCurve.keyValues[pos][1];
                                nextYa = curCurve.keyValues[pos][2];
                            } else
                            {
                                nextXa = curCurve.keyValues[0][1];
                                nextYa = curCurve.keyValues[0][2];
                            }
    
                            val = cubic(prevTime, prevVal, prevTime + prevXa, prevVal + prevYa, nextTime + nextXa, nextVal + nextYa, nextTime, nextVal, interp);
                        } catch (ArrayIndexOutOfBoundsException e)
                        {
                            val = prevVal;
    
                            System.err.println("INTERPOLATION_CUBIC pos: " + pos);
                            e.printStackTrace();
                            throw e;
                        }
                        break;
                    default:
                        val = prevVal;
                }
            }
    
            // Copy the value to the correct value of the target transform
            switch (target)
            {
                case SCALING_X:
                    result.scalX = val;
                    break;
                case SCALING_Y:
                    result.scalY = val;
                    break;
                case SCALING_Z:
                    result.scalZ = val;
                    break;
                case TRANSLATION_X:
                    result.transX = val;
                    break;
                case TRANSLATION_Y:
                    result.transY = val;
                    break;
                case TRANSLATION_Z:
                    result.transZ = val;
                    break;
                case ROTATION_X:
                    result.rotX = val;
                    break;
                case ROTATION_Y:
                    result.rotY = val;
                    break;
                case ROTATION_Z:
                    result.rotZ = val;
                    break;
                default:
                    // This really shouldn't happen since the constructor throws an exception if target is none of the above.
                    logger.warning("Unrecognized transform");
            }
        }
    }
}