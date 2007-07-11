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
	
	public void parse(RawTemplate block)
	{
		Iterator<Object> it = block.values.iterator();
		timing = (String)it.next();
		start = ((Number)it.next()).floatValue();
		end = ((Number)it.next()).floatValue();
		frameRate = ((Number)it.next()).floatValue();
	}
}