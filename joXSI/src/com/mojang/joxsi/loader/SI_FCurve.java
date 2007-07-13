package com.mojang.joxsi.loader;

import java.io.Serializable;
import java.util.Iterator;

/**
 * Defines function curves.
 * <p>
 * Note Interpolation (how the value is evaluated between fcurve keys) can be
 * constant, linear, or cubic. Cubic means that a Bezier curve is calculated as
 * the interpolation between the keys. XSI uses cubic Bezier curves defined as
 * follows:
 * <p>
 * "Four points A, B, C and D in the plane or in three-dimensional space define
 * a cubic Bezier curve. The curve starts at A going toward B and arrives at D
 * coming from the direction of C. In general, it will not pass through B or C;
 * these points are only there to provide directional information. The distance
 * between A and B determines "how long" the curve moves into direction B before
 * turning towards D." (from de Casteljau's algorithm)
 * <p>
 * The parametric form of the curve is:
 * <p>
 * <code>B(t) = A(1-t)^3 + 3Bt(1-t)^2 + 3Ct^2(1-t) + Dt^3<code>
 * <p>
 * This class is a container for a template in the dotXSI file
 * format, as specified by XSIFTK template reference.
 * <p>
 * It's very sparsely documented.
 * 
 * @author Notch
 * @author Egal
 */
public class SI_FCurve extends Template
{
    /**
     * Represents a single FCurve.<br>
     * For each key, a comma-separated list beginning with the frame number (an
     * integer) or the time in seconds (a floating point value). The
     * {@link SI_Scene } template specifies whether time is expressed in frames
     * or time. If frames, you can cast the values from floats to integers.
     * <p>
     * The key values are floating point values.
     * <p>
     * The number of key values is given by (Dimension * NbKeyValues).
     */
	public class FCurve implements Serializable
	{
		public float[] frames;
		public float[][] keyValues;
	}
	
    /** Name of the object to which the function curves apply. */
	public String objectName;
    /**
     * Name of the function curves.
     * <p>For cameras (v2.0/v3.0): <br>
     * INTEREST | FAR | FOV | NEAR | POSITION | ROLL<br>
     * <p>For lights (v2.0):<br>
     * CONE | INTEREST | POSITION | SPREAD
     * <p>>For lights (v3.0):<br>
     * COLOR | CONE | INTEREST | POSITION | SPREAD | ORIENTATION (XSI Infinite lights only)
     * <p>For models (v2.0):<br>
     * SCALING | ROTATION | TRANSLATION
     * <p>For models (v3.0):<br>
     * SCALING | ROTATION | TRANSLATION | NODEVIS
     * <p>For Fog (v3.0 only - not supported by SOFTIMAGE|XSI):<br>
     * COLOR | END | START
     * <p>For materials (v3.0 only):<br>
     * AMBIENT | DIFFUSE | EMMISSIVE | POWER | SPECULAR
     */
	public String fcurve;
    /**
     * Possible values are:<br>
     * CONSTANT | HERMITE | LINEAR | CUBIC.
     */
	public String interpolation;
    /**
     * Number of vector component fcurves. For example, if there is only the X
     * translation fcurve, then the dimension is 1. If there are fcurves for X,
     * Y, and Z, then the dimension is 3.
     */
	public int nbFcurves;
    /**
     * Number of values stored for a vector component at a given key.<br>
     * CONSTANT and LINEAR fcurves have one key value for each vector component.<br>
     * HERMITE fcurves have three values for each vector component.<br>
     * At each key, a value, in-tangent, and out-tangent is required for each
     * vector component.<br>
     * The values are arranged in order of major first. For example: ( Xvalue,
     * Xin-tan, Xout-tan, Yvalue, Yvalue, Yin-tan, Yout-tan, ... )<br>
     * CUBIC fcurves are arranged as follows:
     * <ul>
     * <li>frame (in seconds)</li>
     * <li>value</li>
     * <li>left tan X value</li>
     * <li>left tan Y value</li>
     * <li>right tan X value</li>
     * <li>right tan Y value</li>
     * </ul>
     */
	public int nbKeyValues;
    /** Number of keys. */
	public int nbKeys;
    /** Array of FCurves. */
	public FCurve[] fcurves;

    /**
     * For each key, a comma-separated list beginning with the frame number (an
     * integer) or the time in seconds (a floating point value). The
     * {@link SI_Scene } template specifies whether time is expressed in frames
     * or time. If frames, you can cast the values from floats to integers.
     * <p>
     * The key values are floating point values.
     * <p>
     * The number of key values is given by (Dimension * NbKeyValues).
     */
	public void parse(RawTemplate block) throws ParseException
	{
		Iterator<Object> it = block.values.iterator();
		objectName = (String)it.next();
		fcurve = ((String)it.next()).intern();
		interpolation = (String)it.next();
		nbFcurves = ((Integer)it.next()).intValue();
		nbKeyValues = ((Integer)it.next()).intValue();
		nbKeys = ((Integer)it.next()).intValue();
		
		fcurves = new FCurve[nbFcurves];
		
		if(!fcurve.equals("INTEREST") &&    // Camera (v2.0, v3.0)
		   !fcurve.equals("FAR") &&
		   !fcurve.equals("FOV") &&
		   !fcurve.equals("NEAR") &&
		   !fcurve.equals("POSITION") &&
		   !fcurve.equals("ROLL") && 
		   !fcurve.equals("CONE") &&        // Lights (v2.0)
		   !fcurve.equals("SPREAD") &&
		   !fcurve.equals("COLOR") &&       // Lights (v3.0)
		   !fcurve.equals("ORIENTATION") &&
		   !fcurve.equals("SCALING") &&
		   !fcurve.equals("ROTATION") &&
		   !fcurve.equals("TRANSLATION") && // Models (v2.0)
		   !fcurve.equals("NODEVIS") &&     // Models (v3.0)
		   !fcurve.equals("START") &&       // Fog (v3.0)
		   !fcurve.equals("END") &&
		   !fcurve.equals("AMBIENT") &&     // Materials (v3.0)
		   !fcurve.equals("DIFFUSE") &&
		   !fcurve.equals("EMMISSIVE") &&
		   !fcurve.equals("POWER") &&
		   !fcurve.equals("SPECULAR") &&
		   !fcurve.endsWith("rotx") &&      // XSI Short Hacks?
		   !fcurve.endsWith("roty") &&
		   !fcurve.endsWith("rotz") &&
		   !fcurve.endsWith("posx") &&
           !fcurve.endsWith("posy") &&
           !fcurve.endsWith("posz"))
		    throw new ParseException("Illegal fcurve in SI_FCurve: "+fcurve);
		if(!interpolation.equals("CONSTANT") &&
		   !interpolation.equals("HERMITE") &&
		   !interpolation.equals("LINEAR") &&
		   !interpolation.equals("CUBIC"))
		    throw new ParseException("Illegal interpolation in SI_FCurve: "+interpolation);
		
		for (int i=0; i<nbFcurves; i++)
		{
			fcurves[i] = new FCurve();
			fcurves[i].frames = new float[nbKeys];
			fcurves[i].keyValues = new float[nbKeys][nbKeyValues];
			
			for (int j=0; j<nbKeys; j++)
			{
				fcurves[i].frames[j] = ((Float)it.next()).floatValue();
				for (int k=0; k<nbKeyValues; k++)
				{
					fcurves[i].keyValues[j][k] = ((Float)it.next()).floatValue();
				}
			}
		}
	}

    @Override
    public String toString() {
        // TODO Auto-generated method stub
        return super.toString()
                + " ObjectName: " + objectName + ", fcurve: " + fcurve + ", interpolation: " + interpolation
                + ", number of Fcurves: " + nbFcurves + ", number of keyvalues: " + nbKeyValues + ", nmber of keys: " + nbKeys;
    }
    
}