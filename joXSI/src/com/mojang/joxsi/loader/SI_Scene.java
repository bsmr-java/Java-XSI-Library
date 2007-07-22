package com.mojang.joxsi.loader;

import java.util.Iterator;

/**
 * Contains global scene information.
 * <p>This class is a container for a template in the dotXSI file format, as specified by XSIFTK template reference.
 *
 * <p>It's very sparsely documented.
 * @author Notch
 * @author Egal
 */
public class SI_Scene extends Template
{
    /**
     * Specifies whether time values are expressed as frames or seconds.
     * <p>Possible values are: FRAMES | SECONDS
     */
	public String timing;
    /** Start time (in either frames or seconds, depending on the value of the Timing flag) of the scene. */
	public float start;
    /** End time (in either frames or seconds, depending on the value of the Timing flag) of the scene. */
	public float end;
    /** Frames per second. */
	public float frameRate;

    @Override
	public void parse(RawTemplate block) throws ParseException
	{
		Iterator<Object> it = block.values.iterator();
		timing = (String)it.next();
		start = ((Number)it.next()).floatValue();
		end = ((Number)it.next()).floatValue();
		frameRate = ((Number)it.next()).floatValue();

		if(!timing.equals("FRAMES") &&
		   !timing.equals("SECONDS"))
		    throw new ParseException("Illegal timing in SI_Scene: "+timing);
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
        frameRate = aFrameRate;
    }

    @Override
    public String toString()
    {
        return super.toString() + " timing: " + timing + ", start: " + start + ", end: " + end + ", FPS: " + frameRate;
    }
}