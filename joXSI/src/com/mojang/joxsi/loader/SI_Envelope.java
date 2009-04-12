package com.mojang.joxsi.loader;

import java.io.Serializable;
import java.util.Iterator;

/**
 * Defines an envelope (also known as a skin). If the enveloped object 
 * is the parent of the skeleton bones, then it is global, 
 * if it is a child of one of the skeleton bones, then it is local.
 * This class is a container for a template in the dotXSI file format, as specified by XSIFTK template reference.
 * 
 * <p>It's very sparsely documented.
 * @author Notch
 * @author Egal
 */
public class SI_Envelope extends Template
{
    public class VertexWeight implements Serializable
    {
        /** Index of the vertex.*/
        public int vertexIndex;
        /** Weight value in the range [0.0, 100.0]. */
        public float weight;
    }

    /** Name of the model to be used as an envelope. */
    public String envelope;
    /** Name of the object to be used as a deformer. */
    public String deformer;
    /** Number of weighted vertices in the envelope. */
    public int nVertices;
    /** Array of weight values associated to the vertices of the envelope. */
    public VertexWeight[] vertexWeights; 

    @Override
    public void parse(RawTemplate block) throws ParseException
    {
        Iterator<Object> it = block.values.iterator();
        envelope = (String)it.next();
        deformer = (String)it.next();
        nVertices  = ((Integer)it.next()).intValue();
        vertexWeights = new VertexWeight[nVertices];
        for (int i=0; i<nVertices; i++)
        {
            vertexWeights[i] = new VertexWeight();
            vertexWeights[i].vertexIndex = ((Integer)it.next()).intValue();
            vertexWeights[i].weight = ((Float)it.next()).floatValue();

            if(vertexWeights[i].weight < 0.0f ||
                    vertexWeights[i].weight > 100.0f)
                throw new ParseException("Illegal vertex weight in SI_Envelope: "+vertexWeights[i].weight);
        }
    }

    @Override
    public String toString()
    {
        return template_type + " " + template_info + ", envelope: " + envelope + ", deformer: " + deformer + ", nVertices: "
            + nVertices;
    }
}