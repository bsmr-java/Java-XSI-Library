package com.mojang.joxsi.loader;

import java.util.Iterator;

/**
 * Stores information about a specific image used in the scene. 
 * All crop parameters are animatable. The fcurve names are:<br>
 * <br>• CROP-MIN-X<br>
 * • CROP-MAX-X<br>
 * • CROP-MIN-Y<br>
 * • CROP-MAX-Y
 * 
 * <p>This class is a container for a template in the dotXSI file format, as specified by XSIFTK template reference.
 * 
 * <p>It's very sparsely documented.
 * @author Notch
 * @author Egal
 * @see XSI_ImageLibrary
 */
public class XSI_Image extends Template
{
    /** Name of source file. */
	public String filename;
    /** Image size in X. */
	public int imageX;
    /** Image size in Y. */
	public int imageY;
    /** Number of channels. Possible values are: 3 = RGB, 4 = RGBA. */
	public int channel_count;
    /** 
     * Number of bits per pixel. Possible values:<br>
     * • 24<br>
     * • 32
     */
	public int bitsPerPixels;
    /** Leftmost X of the cropping rectangle. */
	public float minimumX;
    /** Rightmost X of the cropping rectangle. */
	public float maximumX;
    /** Leftmost Y of the cropping rectangle. */
	public float minimumY;
    /** Rightmost Y of the cropping rectangle. */
	public float maximumY;
    /**
     * Specifies frame rate to use. If this value is 0, 
     * the scene frame rate is assumed; otherwise, this value is the new frame rate.
     * */
	public float frame_rate;
    /** Number of frames for sequence. */
	public int frame_count;
    /** Specifies frame number for first frame in sequence. */
	public int first;
    /** Specifies frame number for last frame in sequence. */
	public int last;
	
    @Override
	public void parse(RawTemplate block)
	{
		Iterator<Object> it = block.values.iterator();
		filename  = (String)it.next();
		imageX = ((Integer)it.next()).intValue();
		imageY = ((Integer)it.next()).intValue();
		channel_count = ((Integer)it.next()).intValue();
		bitsPerPixels = ((Integer)it.next()).intValue();
		minimumX = ((Float)it.next()).floatValue();
		maximumX = ((Float)it.next()).floatValue();
		minimumY = ((Float)it.next()).floatValue();
		maximumY = ((Float)it.next()).floatValue();
		frame_rate = ((Float)it.next()).floatValue();
		frame_count = ((Integer)it.next()).intValue();
		first = ((Integer)it.next()).intValue();
		last = ((Integer)it.next()).intValue();
	}

    @Override
    public String toString() {
        return super.toString() + ", Imagename: " + filename + " (" + imageX + ", " + imageY + ")";
    }
}