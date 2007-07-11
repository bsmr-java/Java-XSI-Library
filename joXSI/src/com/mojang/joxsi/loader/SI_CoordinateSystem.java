package com.mojang.joxsi.loader;

import java.util.Iterator;

/**
 * Specifies the coordinate system in which transformations are expressed. 
 * This template allows game developers to express data in any coordinate system.
 * This class is a container for a template in the dotXSI file format, as specified by XSIFTK template reference.
 * 
 * <p>It's very sparsely documented.
 * @author Notch
 * @author Egal
 */
public class SI_CoordinateSystem extends Template
{
    public static final int RIGHT = 0;
    public static final int LEFT = 1;
    public static final int UP = 2;
    public static final int DOWN = 3;
    public static final int IN = 4;
    public static final int OUT = 5;
    public static final int U_RIGHT = 0;
    public static final int U_LEFT = 1;
    public static final int V_DOWN = 0;
    public static final int V_UP = 1;

    /**
     * Specifies the coordinate system:<br>
     * 0 = left-handed (D3D, PlayStation)<br>
     * 1 = right-handed (SOFTIMAGE|3D, Nintendo)
     */
    public int handRotation;
    /**
     * Orientation of the U axis for texture coordinates:<br>
     * 0 = right (all)<br>
     * 1 = left
     */
    public int uAxis;
    /**
     * Orientation of the V axis for texture coordinates:<br>
     * 0 = down (D3D, Nintendo, PlayStation)<br>
     * 1 = up (SOFTIMAGE|3D)
     */
    public int vAxis;
    /**
     * Orientation of the X axis:<br>
     * 0 = right (all)<br>
     * 1 = left<br>
     * 2 = up<br>
     * 3 = down<br>
     * 4 = in<br>
     * 5 = out
     */
    public int xAxis;
    /**
     * Orientation of the Y axis:<br>
     * 0 = right<br>
     * 1 = left<br>
     * 2 = up (SOFTIMAGE|3D, D3D, Nintendo)<br>
     * 3 = down (PlayStation)<br>
     * 4 = in<br>
     * 5 = out
     */
    public int yAxis;
    /**
     * Orientation of the Z axis:<br>
     * 0 = right<br>
     * 1 = left<br>
     * 2 = up<br>
     * 3 = down<br>
     * 4 = in (D3D, PlayStation)<br>
     * 5 = out (SOFTIMAGE|3D, Nintendo)
     */
    public int zAxis;

    public void parse(RawTemplate block)
    {
        Iterator<Object> it = block.values.iterator();
        handRotation = ((Integer)it.next()).intValue();
        uAxis = ((Integer)it.next()).intValue();
        vAxis = ((Integer)it.next()).intValue();
        xAxis = ((Integer)it.next()).intValue();
        yAxis = ((Integer)it.next()).intValue();
        zAxis = ((Integer)it.next()).intValue();
    }

    public void convertPos(float value, int axis, float[] v)
    {
        switch (axis)
        {
            case RIGHT :
                v[0] = value;
                break;
            case LEFT :
                v[0] = -value;
                break;
            case UP :
                v[1] = value;
                break;
            case DOWN :
                v[1] = -value;
                break;
            case IN :
                v[2] = value;
                break;
            case OUT :
                v[2] = -value;
                break;
        }
    }

    public float[] convertPos(float[] v)
    {
/*      float x = v[0];
        float y = v[1];
        float z = v[2];

        convertPos(x, xAxis, v);
        convertPos(y, yAxis, v);
        convertPos(z, zAxis, v);*/

        return v;
    }

	public float[] convertAndNormalizePos(float[] v)
	{
		float len = v[0]*v[0]+v[1]*v[1]+v[2]*v[2];
		if (len>1.01 || len<0.99)
		{
			System.out.println("Normalizing normal with length "+len);
			len = 1/(float)Math.sqrt(len);
			v[0]*=len;
			v[1]*=len;
			v[2]*=len;
		} 
/*      float x = v[0];
		float y = v[1];
		float z = v[2];

		convertPos(x, xAxis, v);
		convertPos(y, yAxis, v);
		convertPos(z, zAxis, v);*/

		return v;
	}

    public float[] convertTex(float[] t)
    {
        if (uAxis == U_LEFT)
            t[0] = -t[0];

        if (vAxis == V_UP)
            t[1] = -t[1];

        return t;
    }

    /**
     * TODO convert(SI_Transform) is empty).
     * @param transform
     */
    public void convert(SI_Transform transform)
    {
/*      float[] scale = new float[3];
        float[] translate = new float[3];
        float[] rotate = new float[3];

        convertPos(transform.scalX, xAxis, scale);
        convertPos(transform.scalY, yAxis, scale);
        convertPos(transform.scalZ, zAxis, scale);

        convertPos(transform.transX, xAxis, translate);
        convertPos(transform.transY, yAxis, translate);
        convertPos(transform.transZ, zAxis, translate);

        convertPos(transform.rotX, xAxis, rotate);
        convertPos(transform.rotY, yAxis, rotate);
        convertPos(transform.rotZ, zAxis, rotate);

        transform.scalX = scale[0];
        transform.scalY = scale[1];
        transform.scalZ = scale[2];

        transform.transX = translate[0];
        transform.transY = translate[1];
        transform.transZ = translate[2];

        if (handRotation == 0)
        {
            transform.rotX = -rotate[0];
            transform.rotY = -rotate[1];
            transform.rotZ = -rotate[2];
        }
        else
        {
            transform.rotX = rotate[0];
            transform.rotY = rotate[1];
            transform.rotZ = rotate[2];
        }*/
    }

    @Override
    public String toString() {
        // TODO Auto-generated method stub
        return super.toString()
                + "handRotation: " + handRotation + ", uAxis: " + uAxis + ", vAxis: " + vAxis + "xAxis: " + xAxis + ", yAxis: "
                + yAxis + ", zAxis: " + zAxis;
    }
}