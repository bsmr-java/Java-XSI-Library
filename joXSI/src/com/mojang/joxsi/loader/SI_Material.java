package com.mojang.joxsi.loader;

import java.util.Iterator;

/**
 * Defines a SOFTIMAGE|3D material.
 * <p>In v3.0 only <code>SI_Material</code> is exported. 
 * In v3.5 and 3.6, SoftMaterial is exported as <code>SI_Material</code>, 
 * and regular Material is exported as {@link XSI_Material }.
 * <p>SOFTIMAGE|XSI exports this template in v3.0. 
 * For v3.5/3.6, XSI exports it when there is a legacy shader imported from 
 * SOFTIMAGE|3D (via either dotXSI v3.0 or the ImportSi3D).
 * 
 * <p>This class is a container for a template in the dotXSI file format, as specified by XSIFTK template reference.
 * 
 * <p>It's very sparsely documented.
 * @author Notch
 * @author Egal
 * @see SI_Texture2D
 * @see SI_MaterialLibrary
 */
public class SI_Material extends Template implements Material
{
    /** Diffuse color. */
    public ColorRGBA faceColor;
    /** Specular decay. */
    public float power;
    /** Specular color */
    public ColorRGB specularColor;
    /** Emissive color. Not supported by SOFTIMAGE|3D. */
    public ColorRGB emissiveColor;
    /**
     * Defines the material shading model:<br>
     * 0 = Constant<br>
     * 1 = Lambert<br>
     * 2 = Phong<br>
     * 3 = Blinn<br>
     * 4 = Shadow Object<br>
     * 5 = Vertex Colour
     */
    public int shadingModel;
    /** Ambient color. */
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