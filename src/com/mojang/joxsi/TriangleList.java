package com.mojang.joxsi;

import com.mojang.joxsi.loader.SI_TriangleList;

/**
 * A triangle list contains pointers into a Shape buffer for a list of triangles.
 * 
 * <p>If a list of Envelopes is added to the TriangleList, it automatically builds buffers with bone deformation data in the following format:<br>
 * <code>envelopeCounts[<i>vertex_num</i>]</code>: The number of envelopes that affect vertex #<code><i>vertex_num</i></code>.<br> 
 * <code>envelopeIndexes[<i>bone_num</i>][<i>vertex_num</i>]</code>: The index of bone #<code><i>bone_num</i></code> for vertex #<code><i>vertex_num</i></code>. This points to an entry in the <code>envelopes</code> array.<br> 
 * <code>envelopeWeights[<i>bone_num</i>][<i>vertex_num</i>]</code>: The weight of bone #<code><i>bone_num</i></code> for vertex #<code><i>vertex_num</i></code>, in the range 0.0f-1.0f.<br> 
 */
public class TriangleList
{
    private static final int MAX_ENVELOPES = 4;
    private static final int MAX_TEXTURES = 4;

    public Shape shape;

    public int vertices;
    public int[] vertexIndexes;
    public int[] normalIndexes;
    public int[] colorIndexes;
    public int[][] texCoordIndexes;

    public int[][] envelopeIndexes;
    public int[] envelopeCounts;
    public float[][] envelopeWeights;
    public Envelope[] envelopes;

    public boolean hasNormals;
    public boolean hasColors;
    public boolean[] hasTexCoords = new boolean[MAX_TEXTURES];

    public String material;
    public Scene scene;

    /**
     * Creates a new Triangle list from a shape buffer and a SI_TriangleList object.
     * 
     * <p>This is called automatically when the Scene is created, so there's rarely any need to call this manually.
     *
     * @param scene the scene this TriangleList belongs to
     * @param shape the shape this TriangleList reads data from
     * @param triangleList the SI_TriangleList to use to build the TriangleList 
     */
    public TriangleList(Scene scene, Shape shape, SI_TriangleList triangleList)
    {
        this.scene = scene;
        this.shape = shape;

        material = triangleList.material;
        vertices = triangleList.nbTriangles * 3;
        
        String elements = triangleList.elements + "|";
        // Add a pipe at the end to be able to look for "TEX_COORD_UV1|" and "TEX_COORD_UV10|".
        // Not that I think xsi files with more than 9 texture units are common..

        // Create buffers
        hasNormals = elements.indexOf("NORMAL|") >= 0;
        hasColors = elements.indexOf("COLOR|") >= 0;
        
        vertexIndexes = new int[vertices];
        if (hasNormals) normalIndexes = new int[vertices];
        if (hasColors) colorIndexes = new int[vertices];
        texCoordIndexes = new int[MAX_TEXTURES][];
        for (int t = 0; t < MAX_TEXTURES; t++)
        {
            hasTexCoords[t] = elements.indexOf("TEX_COORD_UV" + t + "|") >= 0;
            if (hasTexCoords[t])
            {
                texCoordIndexes[t] = new int[vertices];
            }
        }

        // Convert the cumbersome Triangle objects in the Template into entries in a 1d array for
        // each element type.
        for (int i = 0; i < triangleList.nbTriangles; i++)
        {
            SI_TriangleList.Triangle triangle = triangleList.triangles[i];
            for (int j = 0; j < 3; j++)
            {
                vertexIndexes[i * 3 + j] = triangle.v[j];
                if (hasNormals) normalIndexes[i * 3 + j] = triangle.n[j];
                if (hasColors) colorIndexes[i * 3 + j] = triangle.c[j];
                for (int t = 0; t < MAX_TEXTURES; t++)
                {
                    if (hasTexCoords[t])
                    {
                        texCoordIndexes[t][i * 3 + j] = triangle.uv[t][j];
                    }
                }
            }
        }
    }

    /**
     * Sets the envelopes that affect this TriangleList.
     * 
     * <p>This builds the envelopeNums, envelopeWeights and envelopeCounts buffers.<br>
     * 
     * @param envelopes the list of Envelopes that affect this trianglelist
     */
    public void setEnvelopes(Envelope[] envelopes)
    {
        this.envelopes = envelopes;

        // Envelope index is first in the 2d array for memory reasons.
        // If we did new int[vertices][MAX_ENVELOPES], the array would eat up a lot of ram for large models.
        // (Instead of int[MAX_ENVELOPES] objects, we'd have int[vertices] objects)
        envelopeIndexes = new int[MAX_ENVELOPES][vertices];
        envelopeWeights = new float[MAX_ENVELOPES][vertices];
        envelopeCounts = new int[vertices];

        // Fill the envelope buffers
        for (int i = 0; i < envelopes.length; i++)
        {
            Envelope envelope = envelopes[i];
            for (int j = 0; j < envelope.vertexWeights.length; j++)
            {
                int index = envelope.vertexWeights[j].vertexIndex;
                float w = envelope.vertexWeights[j].weight / 100.0f;

                if (envelopeCounts[index] == MAX_ENVELOPES)
                {
                    System.out.println("Warning: More than " + MAX_ENVELOPES + " on vertex " + index + "!");
                }
                else
                {
                    int c = envelopeCounts[index];

                    envelopeIndexes[c][index] = i;
                    envelopeWeights[c][index] = w;

                    envelopeCounts[index]++;
                }
            }
        }
    }
}