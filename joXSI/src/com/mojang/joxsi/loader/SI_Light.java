package com.mojang.joxsi.loader;

import java.util.Iterator;

/**
 * Describes a light. See also {@link SI_LightInfo }.
 * <p>This class is a container for a template in the dotXSI file format, as specified by XSIFTK template reference.
 * 
 * <p>It's very sparsely documented.
 * TODO Add constants for the light types.
 * @author Notch
 * @author Egal
 */
public class SI_Light extends Template
{
    /**
     * Specifies the light type:<br>
     * 0 = point or spot<br>
     * 1 = directional<br>
     * 2 = spot<br>
     * 3 = SOFTIMAGE|XSI infinite light
     */
    public int type;
    /** Color of the light. */
    public float red, green, blue;
    /** Specifies the light position. */
    public float posx, posy, posz;
    /** Orientation of the infinite light. */
    public float orix, oriy, oriz;
    /** Position of the spotlight interest. */
    public float intx, inty, intz;
    /** Cone Angle */
    public float coneAngle;
    /** Spread Angle */
    public float spreadAngle;

    public void parse(RawTemplate block)
    {
        Iterator it = block.values.iterator();

        type = ((Integer)it.next()).intValue();
        red = ((Float)it.next()).floatValue();
        green = ((Float)it.next()).floatValue();
        blue = ((Float)it.next()).floatValue();

        if (type == 0 || type == 1 || type == 2)
        {
            posx = ((Float)it.next()).floatValue();
            posy = ((Float)it.next()).floatValue();
            posz = ((Float)it.next()).floatValue();
        }

        if (type == 3)
        {
            orix = ((Float)it.next()).floatValue();
            oriy = ((Float)it.next()).floatValue();
            oriz = ((Float)it.next()).floatValue();
        }

        if (type == 2)
        {
            intx = ((Float)it.next()).floatValue();
            inty = ((Float)it.next()).floatValue();
            intz = ((Float)it.next()).floatValue();
            coneAngle = ((Float)it.next()).floatValue();
            spreadAngle = ((Float)it.next()).floatValue();
        }
    }
}