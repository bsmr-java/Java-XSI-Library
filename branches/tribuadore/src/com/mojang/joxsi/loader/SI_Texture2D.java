package com.mojang.joxsi.loader;

import java.util.Iterator;

/**
 * Defines a 2D texture.
 * <p>This class is a container for a template in the dotXSI file format, as specified by XSIFTK template reference.
 * 
 * <p>It's very sparsely documented.
 * @author Notch
 * @author Egal
 */
public class SI_Texture2D extends Template
{
    /** Name of the image file. */
    public String imageName;
    /**
     * Type of mapping used to associate texture image to the geometry.<br>
     * 0 = XY projection<br>
     * 1 = XZ projection<br>
     * 2 = YZ projection<br>
     * 3 = UV map (unwrapped)<br>
     * 4 = UV map (wrapped)<br>
     * 5 = Cylindrical projection<br>
     * 6 = Spherical projection<br>
     * 7 = Reflection map
     */
    public int mappingType;
    /** Number of horizontal pixels in image. */
    public int width;
    /** Number of vertical pixels in image. */
    public int height;
    /** Start of the cropping region in U. */
    public int cropUMin;
    /** End of the cropping region in U. */
    public int cropUMax;
    /** Start of the cropping region in V. */
    public int cropVMin;
    /** End of the cropping region in V. */
    public int cropVMax;
    /**
     * Indicates if the U and V orientations are swapped.<br>
     * When FALSE, U goes from 0.0 (left) to 1.0 (right) and V goes from 0.0 (bottom) to 1.0 (top).<br>
     * When TRUE, U goes from 0.0 (top) to 1.0 (bottom) and V goes from 0.0 (right) to 1.0 (left).
     */
    public boolean uvSwap;
    /** Number of horizontal repetitions of the image. */
    public int uRepeat;
    /** Number of vertical repetitions of the image. */
    public int vRepeat;
    /** 
     * Specifies whether to mirror the texture horizontally (U). 
     * Corresponds to the Tiling options in SOFTIMAGE|3D.
     */
    public boolean uAlternate;
    /**
     * Specifies whether to mirror the texture horizontally (U) and vertically (V). 
     * Corresponds to the Tiling options in SOFTIMAGE|3D.
     */
    public boolean vAlternate;
    /** Scaling of image in U. */
    public float uScale;
    /** Scaling of image in V. */
    public float vScale;
    /** Offset of image in U. */
    public float uOffset;
    /** Offset of image in V. */
    public float vOffset;
    /**
     * Texture projection matrix. To compute the UV texture coordinates, 
     * you need to transform the geometry using the inverse of the texture projection matrix. 
     * This generates vertex positions in the the projection coordinate system. 
     * Then you compute the actual projection of the geometry into the UV domain defined 
     * by the projection method.
     */
    public Matrix4x4 projectionMatrix;
    /**
     * Type of blending between texture and material attributes:<br>
     * 1 = Alpha Mask<br>
     * 2 = Intensity Mask<br>
     * 3 = No Mask<br>
     * 4 = RGB Modulation
     */
    public int blendingType;

    /**
     * Normalized contribution of texture attributes (ambient, diffuse, specular, transparency, reflectivity).
     * <p>Note: When blendingType is No Mask, this value corresponds to the Overall Blending parameter 
     * in the 2D Texture dialog box. Otherwise, this value is multiplied with either the alpha channel 
     * or the RGB intensity of the image pixels.
     */
    public float blending;
    /**
     * Normalized contribution of texture pixel colors to the material ambient color. 
     * <p>The weight used to normalize the contribution is a scalar and is multiplied by the 
     * average intensity of the global ambience.
     * <p>Note: When this value is 0.0, SOFTIMAGE|3D uses the material ambient value. 
     * Otherwise, the texture ambient value is multiplied with the atmosphere ambience, 
     * and then blended with the material ambient color according to the blending value.
     */
    public float ambient;
    /**
     * Normalized contribution of texture pixel colors to the material diffuse color.
     * <p>Note: If this value is 0.0, then SOFTIMAGE|3D uses the material diffuse value. 
     * Otherwise, it blends the texture diffuse value with the material diffuse color 
     * according to the blending value.
     */
    public float diffuse;
    /**
     * Normalized contribution of texture pixel colors to the material specular color.
     * <p>Note: If this value is 0.0, then SOFTIMAGE|3D uses the material specular value. 
     * Otherwise, it blends the texture specular value with the material specular color 
     * according to the blending value.
     */
    public float specular;
    /**
     * Normalized contribution of texture pixel colors to the material transparency level.
     * <p>Note: If this value is 0.0, then SOFTIMAGE|3D uses the material transparency value. 
     * Otherwise, it blends the texture transparency value with the material transparency color 
     * according to the blending value.
     */
    public float transparency;
    /**
     * Normalized contribution of texture pixel colors to the material reflectivity level.
     * <p>Note: If this value is 0.0, then SOFTIMAGE|3D uses the material reflectivity value. 
     * Otherwise, it blends the texture reflectivity value with the material reflectivity color 
     * according to the blending value.
     */
    public float reflectivity;
    /**
     * Bump mapping intensity and/or displacement of geometry along surface normals.
     * <p>For bump mapping, mappingType determines the axis frame or bump basis vectors, 
     * which displaces the normals (according to the differences in values between 
     * neighboring pixels along the U and V axes).
     */
    public float roughness;

    @Override
    public void parse(RawTemplate block) throws ParseException
    {
        Iterator<Object> it = block.values.iterator();
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
        
        if(mappingType < 0 || mappingType > 7)
            throw new ParseException("Illegal mappingType in SI_Texture2D: "+mappingType);
        if(blendingType < 1 || blendingType > 4)
            throw new ParseException("Illegal blendingType in SI_Texture2D: "+blendingType);
        
    }
}