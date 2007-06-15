package com.mojang.joxsi.loader;

import java.io.Serializable;
import java.util.Iterator;

/**
 * This class is a container for a template in the dotXSI file format, as specified by XSIFTK template reference.
 * 
 * <p>It's very sparsely documented.
 */
public class SI_Envelope extends Template
{
	public class VertexWeight implements Serializable
	{
		public int vertexIndex;
		public float weight;
	}
	 
	public String envelope;
	public String deformer;
	public int nVertices;
	public VertexWeight[] vertexWeights; 

	public void parse(RawTemplate block)
	{
		Iterator it = block.values.iterator();
		envelope = (String)it.next();
		deformer = (String)it.next();
		nVertices  = ((Integer)it.next()).intValue();
		vertexWeights = new VertexWeight[nVertices];
		for (int i=0; i<nVertices; i++)
		{
			vertexWeights[i] = new VertexWeight();
			vertexWeights[i].vertexIndex = ((Integer)it.next()).intValue();
			vertexWeights[i].weight = ((Float)it.next()).floatValue();
		}
	}
}