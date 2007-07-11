package com.mojang.joxsi.loader;

import java.io.Serializable;
import java.util.Iterator;

/**
 * This class is a container for a template in the dotXSI file format, as specified by XSIFTK template reference.
 * 
 * <p>It's very sparsely documented.
 */
public class Matrix4x4 implements Serializable
{
    public float[][] matrix = new float[4][4];

    public Matrix4x4(Iterator<Object> it)
    {
        for (int y = 0; y < 4; y++)
        {
            for (int x = 0; x < 4; x++)
            {
            	matrix[x][y] = ((Float)it.next()).floatValue();
            }
        }
    }
}