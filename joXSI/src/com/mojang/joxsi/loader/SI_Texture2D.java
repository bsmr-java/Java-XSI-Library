package com.mojang.joxsi.loader;

import java.util.Iterator;

/**
 * This class is a container for a template in the dotXSI file format, as specified by XSIFTK template reference.
 * 
 * <p>It's very sparsely documented.
 */
public class SI_Texture2D extends Template
{
    public String imageName;
    public int mappingType;
    public int width;
    public int height;
    public int cropUMin;
    public int cropUMax;
    public int cropVMin;
    public int cropVMax;
    public boolean uvSwap;
    public int uRepeat;
    public int vRepeat;
    public boolean uAlternate;
    public boolean vAlternate;
    public float uScale;
    public float vScale;
    public float uOffset;
    public float vOffset;
    public Matrix4x4 projectionMatrix;
    public int blendingType;

    public float blending;
    public float ambient;
    public float diffuse;
    public float specular;
    public float transparency;
    public float reflectivity;
    public float roughness;

    public void parse(RawTemplate block)
    {
        Iterator it = block.values.iterator();
        imageName = (String)it.next();
        mappingType = ((Integer)it.next()).intValue();

        width = ((Integer)it.next()).intValue();
        height = ((Integer)it.next()).intValue();

        cropUMin = ((Integer)it.next()).intValue();
        cropUMax = ((Integer)it.next()).intValue();
        cropVMin = ((Integer)it.next()).intValue();
        cropVMax = ((Integer)it.next()).intValue();

        uvSwap = ((Integer)it.next()).intValue() != 0;

        uRepeat = ((Integer)it.next()).intValue();
        vRepeat = ((Integer)it.next()).intValue();
        uAlternate = ((Integer)it.next()).intValue() != 0;
        vAlternate = ((Integer)it.next()).intValue() != 0;
        uScale = ((Float)it.next()).floatValue();
        vScale = ((Float)it.next()).floatValue();
        uOffset = ((Float)it.next()).floatValue();
        vOffset = ((Float)it.next()).floatValue();

        projectionMatrix = new Matrix4x4(it);

        blendingType = ((Integer)it.next()).intValue();

        blending = ((Float)it.next()).floatValue();
        ambient = ((Float)it.next()).floatValue();
        diffuse = ((Float)it.next()).floatValue();
        specular = ((Float)it.next()).floatValue();
        transparency = ((Float)it.next()).floatValue();
        reflectivity = ((Float)it.next()).floatValue();
        roughness = ((Float)it.next()).floatValue();
    }
}