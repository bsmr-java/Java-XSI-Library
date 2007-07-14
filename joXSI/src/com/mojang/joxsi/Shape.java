package com.mojang.joxsi;

import java.util.logging.Logger;
import com.mojang.joxsi.loader.SI_Shape;

/**
 * A shape contains a buffer of vertices, normals, colors and uv coordinates.
 * 
 * <p>It is used as a data source by the TriangleLists. 
 */
public class Shape
{
    private static Logger logger = Logger.getLogger("com.mojang.joxsi.demo");
    public float[] vertexBuffer;
    public float[] normalBuffer;
    public float[] colorBuffer;
    /** the forest-giant.xsi has TEX_COORD_UVs that reach 42 so 16 is not enough. */
    public float[][] texCoordBuffer = new float[44][];

    /**
     * Creates a new Shape from an SI_Shape object.
     * 
     * <p>This is called automatically when the Scene is created, so there's rarely any need to call this manually.
     * 
     * @param shape the SI_Shape to read the information from
     */
    public Shape(SI_Shape shape)
    {
        for (int i = 0; i < shape.nbShapeArrays; i++)
        {
            // Get the elements and copy them to the right buffer.
            SI_Shape.ShapeArray shapeArray = shape.shapeArrays[i];
            if (shapeArray.elements.equals("POSITION"))
            {
                vertexBuffer = shapeArray.values;
            }
            else if (shapeArray.elements.equals("NORMAL"))
            {
                normalBuffer = shapeArray.values;
            }
            else if (shapeArray.elements.equals("COLOR"))
            {
                colorBuffer = shapeArray.values;
            }
            else if (shapeArray.elements.startsWith("TEX_COORD_UV"))
            {
                int uvCoordSet = Integer.parseInt(shapeArray.elements.substring("TEX_COORD_UV".length()));
                texCoordBuffer[uvCoordSet] = shapeArray.values;
            }
            else
            {
                // TODO: Store unknown element type in a map
                logger.info("Unknown shape element type: " + shapeArray.elements);
            }
        }
    }
}