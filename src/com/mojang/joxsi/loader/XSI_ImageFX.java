package com.mojang.joxsi.loader;

import java.util.Iterator;

/**
 * Stores the special effect parameters applied to the image.
 * All parameters are animatable. Fcurve names are:
 * <ul>
 * <li>HUE</li>
 * <li>GAIN</li>
 * <li>SATURATION</li>
 * <li>BRIGHTNESS</li>
 * <li>BLUR_RADIUS</li>
 * <li>BLUR_AMOUNT</li>
 * <li>BLUR_ALPHA</li>
 * <li>SCALING_TYPE</li>
 * <li>SCALE-X</li>
 * <li>SCALE-Y</li>
 * <li>FLIP-X</li>
 * <li>FLIP-Y</li>
 * <li>RGBA2GRAYSCALE</li>
 * <li>BITS_PER_CHANNEL</li>
 * </ul>
 * <pre>
 * XSI_ImageFX {
 *    // Color Correction
 *    &lt;Hue&gt;, 
 *    &lt;Gain&gt;, 
 *    &lt;Saturation&gt;, 
 *    &lt;Brightness&gt;, 
 *    // Blur
 *    &lt;Radius&gt;, 
 *    &lt;Amount&gt;, 
 *    &lt;blurAlpha&gt;, 
 *    // Scaling
 *    &lt;type&gt; 
 *    &lt;scaleX&gt;,&lt;scaleY&gt;,
 *    // Flip
 *    &lt;horizontal&gt;,&lt;vertical&gt;,
 *    // Conversion
 *    &lt;conversion&gt;,
 *    &lt;8 buts to 16 bits per channel&gt;
 *        }
 * <p>This class is a container for a template in the dotXSI file format, as specified by XSIFTK template reference.
 * <p>It's very sparsely documented.
 */
public class XSI_ImageFX extends Template
{
    /** Hue color correction. Possible values are between 0 (default) and 100. */
	public float hue;  
    /** Gain color correction. Possible values are between 0 (default) and 100. */
	public float gain;  
    /** Saturation color correction. Possible values are between 0 (default) and 100. */
	public float saturation;  
    /** Brightness color correction. Possible values are between -100 and 100 (with 0 being the default). */
	public float brightness;  
    /** Radius of blur. Possible values are between 0 and 20 (with 0 being the default). */
	public float radius;  
    /** Amount of blur. Possible values are 0 or 1 (default). */
	public float amount;  
    /** True to blur the Alpha channel. */
	public int blurAlpha;  
    /**
     * Scaling information. Possible values are:
     * <ul>
     * <li>0 = none (scaling is inactive)</li>
     * <li>1 = fullsize</li>
     * <li>2 = halfsize</li>
     * <li>3 = quartersize</li>
     * <li>4 = 128 X 128</li>
     * <li>5 = 256 X 256</li>
     * <li>6 = 512 X 512</li>
     * <li>7 = Custom (use scaleX and scaleY)</li>
     * </ul>
     */
	public int type; 
    /** Specify amount to scale in X (used only for Custom type of scaling). */
	public int scaleX;  
    /** Specify amount to scale in Y (used only for Custom type of scaling). */
	public int scaleY;  
    /** Specify horizontal flip value. */
	public int horizontal;  
    /** Specify vertical flip value. */
	public int vertical;
	/**
	 *  Specifies whether or not to convert RGBA to greyscale. Possible values are: 
	 *  <ul>
	 *  <li>1 = True (convert RGBA to greyscale)</li>
	 *  <li>0 = False (do not convert RGBA to greyscale)</li>
     *  </ul>
	 */
	public int conversion;
    /**
     *  Specifies whether to use 16 bits per channel or 8bits. Possible values are: 
     *  <ul>
     *  <li>1 = True (use 16 bits per channel)</li>
     *  <li>0 = False (use 8 bits per channel)</li>
     *  </ul>
     */
    public int sixteenBitsPerChannel;
	
	@Override
    public void parse(RawTemplate block) throws ParseException
	{
		Iterator<Object> it = block.values.iterator();
		hue = ((Float)it.next()).floatValue();  
		gain = ((Float)it.next()).floatValue();  
		saturation = ((Float)it.next()).floatValue();  
		brightness = ((Float)it.next()).floatValue();  
		radius = ((Float)it.next()).floatValue();  
		amount = ((Float)it.next()).floatValue();  
		blurAlpha = ((Integer)it.next()).intValue();  
		type = ((Integer)it.next()).intValue(); 
		scaleX = ((Integer)it.next()).intValue();  
		scaleY = ((Integer)it.next()).intValue();  
		horizontal = ((Integer)it.next()).intValue();  
		vertical = ((Integer)it.next()).intValue();
		conversion = ((Integer)it.next()).intValue();
        sixteenBitsPerChannel = ((Integer)it.next()).intValue();
		
		if(hue < 0.0f || hue > 100.0f)
		    throw new ParseException("Illegal hue in XSI_ImageFX: "+hue);
		if(gain < 0.0f || gain > 100.0f)
            throw new ParseException("Illegal gain in XSI_ImageFX: "+gain);
		if(saturation < 0.0f || saturation > 100.0f)
            throw new ParseException("Illegal saturation in XSI_ImageFX: "+saturation);
		if(brightness < -100.0f || brightness > 100.0f)
            throw new ParseException("Illegal brightness in XSI_ImageFX: "+brightness);
		if(radius < 0.0f || radius > 20.0f)
            throw new ParseException("Illegal radius in XSI_ImageFX: "+radius);
		if(amount < 0.0f || amount > 1.0f)
            throw new ParseException("Illegal amount in XSI_ImageFX: "+amount);
		if(blurAlpha != 0 && blurAlpha != 1)
            throw new ParseException("Illegal blurAlpha in XSI_ImageFX: "+blurAlpha);
		if(type < 0 || type > 7)
            throw new ParseException("Illegal type in XSI_ImageFX: "+type);
		if(conversion != 0 && conversion != 1)
            throw new ParseException("Illegal conversion in XSI_ImageFX: "+conversion);
        if(sixteenBitsPerChannel != 0 && sixteenBitsPerChannel != 1)
            throw new ParseException("Illegal sixteenBitsPerChannel in XSI_ImageFX: "+sixteenBitsPerChannel);
		//if( < 0.0f ||  > 100.0f)
        //    throw new ParseException("Illegal  in XSI_ImageFX: "+);
	}

    @Override
    public String toString()
    {
        return template_type + " " + template_info + ", hue: " + hue + ", gain: " + gain + ", saturation: " + saturation
                + ", brightness: " + brightness + ", radius: " + radius + ", amount: " + amount + ", blurAlpha: " + blurAlpha
                + ", type: " + type + ", scaleX: " + scaleX + ", scaleY: " + scaleY + ", horizontal: " + horizontal
                + ", vertical: " + vertical + ", sixteenBitsPerChannel: " + sixteenBitsPerChannel + ", conversion: " + conversion;
    }
}