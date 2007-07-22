package com.mojang.joxsi.loader;

import java.util.Iterator;

/**
 * Stores SOFTIMAGE|XSI animation mixer track configuration and mixing parameters, 
 * the list of available actions, and their instantiation (as action clips) in the mixer track.
 * 
 * <p>This class is a container for a template in the dotXSI file format, as specified by XSIFTK template reference.
 * 
 * <p>It's very sparsely documented.
 * @author Notch
 * @author Egal
 */
public class XSI_Mixer extends Template
{
    /** Determines if inter-clip interpolation is enabled or not. */
	public boolean autoTransition;
    /** Determines if the animation mixer is active or not. */
	public boolean active;
    /**
     * Specifies how the mix is calculated. Possible values are:<br>
     * • True = Mixes are a weighted averages. The results are mixes 
     *     that fall in-between the values of the separate clips.<br>
     * • False = Mixes are additive. The values of the separate clips are 
     *    added on top of each other. 
     */
	public boolean normalize;
    /**
     * Quaternions usually result in smoother rotations; however, if you are 
     * mixing two or more rotations and using Quaternion, there may be some 
     * unexpected flips in the animation. Using Euler will probably solve this 
     * problem. Possible values are:<br>
     * • True = Rotation values are mixed according to their Quaternion values.<br>
     * • False = Rotation values are mixed according to their Euler values.
     */
	public boolean quaternionMixing;
    /** True to remove spins. */
	public boolean removeSpins;
    /**
     * When you select Quaternion for Rotation Mixing, this option generates more 
     * continuous rotation curves, especially for operations such as plotting/freezing 
     * rotation parameters driven by the animation mixer. Possible values are:<br>
     * • True = Maintain Continuity is enabled.<br>
     * • False = Maintain Continuity is disabled.
     */
	public boolean maintainContinuity;

	@Override
    public void parse(RawTemplate block)
	{
		Iterator<Object> it = block.values.iterator();
		autoTransition = ((Integer)it.next()).intValue()!=0;
		active = ((Integer)it.next()).intValue()!=0;
		normalize = ((Integer)it.next()).intValue()!=0;
		quaternionMixing = ((Integer)it.next()).intValue()!=0;
		removeSpins = ((Integer)it.next()).intValue()!=0;
		maintainContinuity = ((Integer)it.next()).intValue()!=0;
	}
}