package com.mojang.joxsi.loader;

import java.util.Iterator;

/**
 * This class is a container for a template in the dotXSI file format, as specified by XSIFTK template reference.
 * 
 * <p>It's very sparsely documented.
 */
public class SI_ShapeAnimation extends Template
{
    public int nbShapes;
    public SI_Shape[] shapes;
    public SI_FCurve fcurve;

    public void parse(RawTemplate block)
    {
        Iterator it = block.values.iterator();
        String type = (String)it.next();
        nbShapes = ((Number)it.next()).intValue();
        shapes = new SI_Shape[nbShapes];
        for (int i=0; i<nbShapes; i++)
        {
            shapes[i] = (SI_Shape)it.next();
        }
        fcurve = (SI_FCurve)it.next();
    }
}