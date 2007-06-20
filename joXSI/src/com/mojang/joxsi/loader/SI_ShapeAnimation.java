package com.mojang.joxsi.loader;

import java.util.Iterator;

/**
 * <p>
 * Specifies shape animation, which is the animation of vertex positions,
 * normals, colors, and texture coordinates. Shape animation is stored as a
 * series of shapes and an fcurve that assigns the shapes to times/frames.
 * <p>
 * This class is a container for a template in the dotXSI file format, as
 * specified by XSIFTK template reference.
 * <p>
 * It's very sparsely documented.
 * @author Notch
 * @author Egal
 * 
 * @see SI_Shape
 * @see SI_FCurve
 */
public class SI_ShapeAnimation extends Template
{
    public int nbShapes;
    public SI_Shape[] shapes;
    public SI_FCurve fcurve;
    String type;

    public void parse(RawTemplate block)
    {
        Iterator it = block.values.iterator();
        type = (String)it.next();
        nbShapes = ((Number)it.next()).intValue();
        shapes = new SI_Shape[nbShapes];
        for (int i=0; i<nbShapes; i++)
        {
            shapes[i] = (SI_Shape)it.next();
        }
        fcurve = (SI_FCurve)it.next();
    }
}