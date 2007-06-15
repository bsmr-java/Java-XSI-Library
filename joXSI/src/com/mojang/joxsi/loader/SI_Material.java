package com.mojang.joxsi.loader;

import java.util.Iterator;

/**
 * This class is a container for a template in the dotXSI file format, as specified by XSIFTK template reference.
 * 
 * <p>It's very sparsely documented.
 */
public class SI_Material extends Template implements Material
{
    public ColorRGBA faceColor;
    public float power;
    public ColorRGB specularColor;
    public ColorRGB emissiveColor;
    public int shadingModel;
    public ColorRGB ambientColor;

    public void parse(RawTemplate block)
    {
        Iterator it = block.values.iterator();
        faceColor = new ColorRGBA(it);
        power = ((Float)it.next()).floatValue();
        specularColor = new ColorRGB(it);
        emissiveColor = new ColorRGB(it);
        shadingModel = ((Integer)it.next()).intValue();
        ambientColor = new ColorRGB(it);
    }
}