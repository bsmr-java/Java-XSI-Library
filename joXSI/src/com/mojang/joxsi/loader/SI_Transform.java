package com.mojang.joxsi.loader;

import java.util.Iterator;

/**
 * Specifies an SRT transformation as three vectors. Provides a default (or base) 
 * transform for an object. SI_Transform can be used in place of the FrameTransformMatrix 
 * (for example, to avoid having to extract the vectors from a transformation matrix).
 * SOFTIMAGE|3D outputs a SI_Transform template when the Transforms option is set to SRT Values. 
 * Otherwise, a FrameTransformMatrix template is exported.
 * 
 * <p>This class is a container for a template in the dotXSI file format, as specified by XSIFTK template reference.
 * 
 * <p>It's very sparsely documented.
 * @author Notch
 * @author Egal
 */
public class SI_Transform extends Template
{
    /** Scaling vector. */
    public float scalX, scalY, scalZ;
    /** Rotation vector. */
    public float rotX, rotY, rotZ;
    /** Translation vector. */
    public float transX, transY, transZ;

	public SI_Transform()
	{
	}
	
    public SI_Transform(SI_Transform trans)
    {
    	set(trans);
    }

	public void set(SI_Transform trans)
	{
		scalX = trans.scalX;
		scalY = trans.scalY;
		scalZ = trans.scalZ;
		rotX = trans.rotX;
		rotY = trans.rotY;
		rotZ = trans.rotZ;
		transX = trans.transX;
		transY = trans.transY;
		transZ = trans.transZ;
	}
    
	public void parse(RawTemplate block)
	{
		Iterator<Object> it = block.values.iterator();
		scalX = ((Float)it.next()).floatValue();
		scalY = ((Float)it.next()).floatValue();
		scalZ = ((Float)it.next()).floatValue();
		rotX = ((Float)it.next()).floatValue();
		rotY = ((Float)it.next()).floatValue();
		rotZ = ((Float)it.next()).floatValue();
		transX = ((Float)it.next()).floatValue();
		transY = ((Float)it.next()).floatValue();
		transZ = ((Float)it.next()).floatValue();
	}
}