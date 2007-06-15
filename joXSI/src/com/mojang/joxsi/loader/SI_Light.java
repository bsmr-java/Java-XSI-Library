package com.mojang.joxsi.loader;

import java.util.Iterator;

/**
 * This class is a container for a template in the dotXSI file format, as specified by XSIFTK template reference.
 * 
 * <p>It's very sparsely documented.
 */
public class SI_Light extends Template
{
    public int type;

    public float red, green, blue;
    public float posx, posy, posz;
    public float orix, oriy, oriz;
    public float intx, inty, intz;
    public float coneAngle;
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