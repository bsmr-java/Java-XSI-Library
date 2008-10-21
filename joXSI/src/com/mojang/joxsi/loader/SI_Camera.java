package com.mojang.joxsi.loader;

import java.util.Iterator;

/**
 * Describes the camera data.
 * This class is a container for a template in the dotXSI file format, as specified by XSIFTK template reference.
 * <pre>
 * SI_Camera &lt;cameraName&gt; {
 *    &lt;posx&gt;; &lt;posy&gt;; &lt;posz&gt;;;
 *    &lt;intx&gt;; &lt;inty&gt;; &lt;intz&gt;;;
 *    &lt;roll&gt;;
 *    &lt;fieldOfView&gt;;
 *    &lt;nearPlane&gt;;
 *    &lt;farPlane&gt;;
 * }
 * </pre>
 * It's very sparsely documented.
 * @author Notch
 * @author Egal
 */
public class SI_Camera extends Template
{
	public float posx, posy, posz;  
	public float intx, inty, intz;  
	public float roll; 
	public float fieldOfView;  
	public float nearPlane;  
	public float farPlane;  

	@Override
    public void parse(RawTemplate block)
	{
		Iterator<Object> it = block.values.iterator();
		posx = ((Float)it.next()).floatValue();
		posy = ((Float)it.next()).floatValue();
		posz = ((Float)it.next()).floatValue();
		intx = ((Float)it.next()).floatValue();
		inty = ((Float)it.next()).floatValue();
		intz = ((Float)it.next()).floatValue();
		roll = ((Float)it.next()).floatValue();
		fieldOfView = ((Float)it.next()).floatValue();
		nearPlane = ((Float)it.next()).floatValue();
		farPlane = ((Float)it.next()).floatValue();
	}

    @Override
    public String toString()
    {
        return template_type + " " + template_info + ", pos(xyz): " + posx + ", " + posy + ", " + posz + ", int(xyz): " + intx
                + ", " + inty + ", " + intz + ", roll: " + roll + ", fieldOfView: " + fieldOfView + ", nearPlane: " + nearPlane
                + ", farPlane: " + farPlane;
    }
}