package com.mojang.joxsi;

import java.util.logging.Level;
import java.util.logging.Logger;
import com.mojang.joxsi.loader.SI_Shape;

/**
 * A shape contains a buffer of vertices, normals, colors and uv coordinates.
 * 
 * <p>It is used as a data source by the TriangleLists. 
 */
public class Shape
{
    /** logger - Logging instance. */
    private static Logger logger = Logger.getLogger(Shape.class.getName());
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
                // Some dotXSI formats do not append a number if there is only one UV coordinate set
                // Example in the tools\spindle-pile.xsi model
                final String lUvCoordString = shapeArray.elements.substring("TEX_COORD_UV".length());
                int uvCoordSet = 0;
                try
                {
                    uvCoordSet = Integer.parseInt(lUvCoordString);
                }
                catch (NumberFormatException e)
                {
                    // Only log the exception if FINE is enabled as the stack trace can be confusing and worrying for normal users.
                    if (logger.isLoggable(Level.FINE))
                    {
                        logger.log(Level.WARNING, "Shape - possibly an old dotXSI format as the TEX_COORD_UV does not have a number, "
                                + shape, e);
                    }
                    else
                    {
                        logger.warning("Shape - possibly an old dotXSI format as the TEX_COORD_UV does not have a number, "
                                + shape);
                    }
                }
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