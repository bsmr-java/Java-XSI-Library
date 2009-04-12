package com.mojang.joxsi.renderer;

import java.nio.FloatBuffer;

import com.mojang.joxsi.Envelope;
import com.mojang.joxsi.Shape;
import com.mojang.joxsi.TriangleList;
import com.sun.opengl.util.BufferUtil;


/**
 * Applies envelopes to models and stores the result in direct FloatBuffers.
 * 
 * <p>This does no caching of the results at the moment, so many cycles get wasted.
 */
public class EnvelopeBuilder
{
    public FloatBuffer vertexBuffer2;
    public FloatBuffer normalBuffer2;
    public FloatBuffer colorBuffer2;
    public FloatBuffer[] texCoordBuffers2;

    private float[] vertexBuffer;
    private float[] normalBuffer;
    private float[] colorBuffer;
    private float[][] texCoordBuffers;

    private boolean normalize = false;

    /**
     * Creates a new EnvelopeBuilder
     */
    public EnvelopeBuilder()
    {
        texCoordBuffers2 = new FloatBuffer[4];
        texCoordBuffers = new float[4][];
    }

    /**
     * Makes sure the buffers can hold enough vertices.
     * 
     * <p>Grows the buffers if needed.
     * @param vertices the number of vertices the buffers need to hold
     */
    private void ensureBufferSize(int vertices)
    {
        if (vertexBuffer == null || vertexBuffer.length < vertices * 3)
        {
            // Direct buffers
            // TODO: Uses JOGL code.. reimplement BufferUtils to get rid of this dependency
            vertexBuffer2 = BufferUtil.newFloatBuffer(vertices * 3);
            normalBuffer2 = BufferUtil.newFloatBuffer(vertices * 3);
            colorBuffer2 = BufferUtil.newFloatBuffer(vertices * 4);
            for (int i = 0; i < 4; i++)
            {
                texCoordBuffers2[i] = BufferUtil.newFloatBuffer(vertices * 2);
            }

            // Arrays for much (MUCH) faster access.
            vertexBuffer = new float[vertices * 3];
            normalBuffer = new float[vertices * 3];
            colorBuffer = new float[vertices * 4];
            for (int i = 0; i < 4; i++)
            {
                texCoordBuffers[i] = new float[vertices * 2];
            }
        }
    }

    /**
     * Builds the buffers for rendering the supplied TriangleList
     * 
     * @param triangleList the TriangleList that will be rendered
     */
    public void buildBuffers(TriangleList triangleList)
    {
        // Make sure the buffers have room
        ensureBufferSize(triangleList.vertices);

        if (triangleList.envelopes != null)
        {
            // Apply envelopes!
            applyEnvelopes(triangleList);
        }
        else
        {
            Shape shape = triangleList.shape;

            // Look up the vertex position from the shape, and add to the buffer
            for (int i = 0; i < triangleList.vertices; i++)
            {
                vertexBuffer[i * 3 + 0] = shape.vertexBuffer[triangleList.vertexIndexes[i] * 3 + 0];
                vertexBuffer[i * 3 + 1] = shape.vertexBuffer[triangleList.vertexIndexes[i] * 3 + 1];
                vertexBuffer[i * 3 + 2] = shape.vertexBuffer[triangleList.vertexIndexes[i] * 3 + 2];
            }
            // A single bulk add to the direct buffer is much faster than many small. Much.
            vertexBuffer2.clear();
            vertexBuffer2.put(vertexBuffer);

            if (triangleList.hasNormals)
            {
                // Look up the normal vector from the shape, and add to the buffer
                for (int i = 0; i < triangleList.vertices; i++)
                {
                    normalBuffer[i * 3 + 0] = shape.normalBuffer[triangleList.normalIndexes[i] * 3 + 0];
                    normalBuffer[i * 3 + 1] = shape.normalBuffer[triangleList.normalIndexes[i] * 3 + 1];
                    normalBuffer[i * 3 + 2] = shape.normalBuffer[triangleList.normalIndexes[i] * 3 + 2];
                }
                // A single bulk add to the direct buffer is much faster than many small. Much.
                normalBuffer2.clear();
                normalBuffer2.put(normalBuffer);
            }
        }

        // Build the static buffers.
        buildColorAndUvBuffers(triangleList);
    }

    /**
     * Applies the envelopes for the supplied TriangleList, modifying vertex positions and normal vectors
     * 
     * @param triangleList the TriangleList that should get its envelopes applied
     */
    private void applyEnvelopes(TriangleList triangleList)
    {
        Envelope[] envelopes = triangleList.envelopes;

        boolean hasNormals = triangleList.hasNormals;
        Shape shape = triangleList.shape;

        int vertex;
        float xv;
        float yv;
        float zv;
        float x = 0;
        float y = 0;
        float z = 0;
        int j;
        float weight;
        float[] mtrx;

        int normal;
        float xn;
        float yn;
        float zn;
        // Iterate over all vertices
        for (int i = 0; i < triangleList.vertices; i++)
        {
            vertex = triangleList.vertexIndexes[i];

            // Get original position
            xv = shape.vertexBuffer[vertex * 3 + 0];
            yv = shape.vertexBuffer[vertex * 3 + 1];
            zv = shape.vertexBuffer[vertex * 3 + 2];

            x = 0;
            y = 0;
            z = 0;
            // Iterate over all envelopes that affect this envelope
            for (j = 0; j < triangleList.envelopeCounts[vertex]; j++)
            {
                weight = triangleList.envelopeWeights[j][vertex];
                mtrx = envelopes[triangleList.envelopeIndexes[j][vertex]].deformationMatrix;

                // Multiply original position by the deformer matrix, multiply result by weight, and add to output values.
                x += (mtrx[0] * xv + mtrx[4] * yv + mtrx[8] * zv + mtrx[12]) * weight;
                y += (mtrx[1] * xv + mtrx[5] * yv + mtrx[9] * zv + mtrx[13]) * weight;
                z += (mtrx[2] * xv + mtrx[6] * yv + mtrx[10] * zv + mtrx[14]) * weight;
            }
            vertexBuffer[i * 3 + 0] = x;
            vertexBuffer[i * 3 + 1] = y;
            vertexBuffer[i * 3 + 2] = z;

            if (hasNormals)
            {
                normal = triangleList.normalIndexes[i];

                // Get original normal
                xn = shape.normalBuffer[normal * 3 + 0];
                yn = shape.normalBuffer[normal * 3 + 1];
                zn = shape.normalBuffer[normal * 3 + 2];

                x = 0;
                y = 0;
                z = 0;
                // Iterate over all envelopes that affect this envelope
                for (j = 0; j < triangleList.envelopeCounts[vertex]; j++)
                {
                    weight = triangleList.envelopeWeights[j][vertex];
                    mtrx = envelopes[triangleList.envelopeIndexes[j][vertex]].deformationMatrix;

                    // Multiply original normal by the deformer matrix (sans translation), multiply result by weight, and add to output values.
                    x += (mtrx[0] * xn + mtrx[4] * yn + mtrx[8] * zn) * weight;
                    y += (mtrx[1] * xn + mtrx[5] * yn + mtrx[9] * zn) * weight;
                    z += (mtrx[2] * xn + mtrx[6] * yn + mtrx[10] * zn) * weight;
                }

                if (normalize)
                {
                    // Normalize the normal
                    float l = (float)(1/Math.sqrt(x * x + y * y + z * z));
                    normalBuffer[i * 3 + 0] = x*l;
                    normalBuffer[i * 3 + 1] = y*l;
                    normalBuffer[i * 3 + 2] = z*l;
                }
                else
                {
                    // Just add the normal
                    normalBuffer[i * 3 + 0] = x;
                    normalBuffer[i * 3 + 1] = y;
                    normalBuffer[i * 3 + 2] = z;
                }
            }
        }

        // A single bulk add to the direct buffer is much faster than many small. Much.
        vertexBuffer2.clear();
        vertexBuffer2.put(vertexBuffer);
        if (hasNormals)
        {
            normalBuffer2.clear();
            normalBuffer2.put(normalBuffer);
        }
    }

    /**
     * Builds the color and uv buffers for rendering the supplied TriangleList
     * 
     * @param triangleList the TriangleList that will be rendered
     */
    private void buildColorAndUvBuffers(TriangleList triangleList)
    {
        boolean hasColors = triangleList.hasColors;
        Shape shape = triangleList.shape;

        if (hasColors)
        {
            int colorIndex;
            // Look up the colors from the shape, and add to the buffer
            for (int i = 0; i < triangleList.vertices; i++)
            {
                colorIndex = triangleList.colorIndexes[i];
                colorBuffer[i * 4 + 0] = shape.colorBuffer[colorIndex * 4 + 0];
                colorBuffer[i * 4 + 1] = shape.colorBuffer[colorIndex * 4 + 1];
                colorBuffer[i * 4 + 2] = shape.colorBuffer[colorIndex * 4 + 2];
                colorBuffer[i * 4 + 3] = shape.colorBuffer[colorIndex * 4 + 3];
            }
            colorBuffer2.clear();
            colorBuffer2.put(colorBuffer);
        }

        for (int t = 0; t < triangleList.hasTexCoords.length; t++)
        {
            if (triangleList.hasTexCoords[t])
            {
                int texCoordIndex;
                // Look up the texture coordinates from the shape, and add to the buffer
                for (int i = 0; i < triangleList.vertices; i++)
                {
                    texCoordIndex = triangleList.texCoordIndexes[t][i];
                    texCoordBuffers[t][i * 2 + 0] = shape.texCoordBuffer[t][texCoordIndex * 2 + 0];
                    texCoordBuffers[t][i * 2 + 1] = shape.texCoordBuffer[t][texCoordIndex * 2 + 1];
                }
                texCoordBuffers2[t].clear();
                texCoordBuffers2[t].put(texCoordBuffers[t]);
            }
        }
    }
}