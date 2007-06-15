package com.mojang.joxsi.loader;

import java.util.Iterator;

/**
 * This class is a container for a template in the dotXSI file format, as specified by XSIFTK template reference.
 * 
 * <p>It's very sparsely documented.
 */
public class SI_Cluster extends Template
{
	public String referencedModel;
	public String weighting;
	public String clusterCenterReference;
	public int nbVertices;
	public int[] vertexIndexes;

	public void parse(RawTemplate block)
	{
		Iterator it = block.values.iterator();
		referencedModel = (String)it.next();
		weighting = (String)it.next();
		clusterCenterReference = (String)it.next();
		
		nbVertices = ((Integer)it.next()).intValue();
		vertexIndexes = new int[nbVertices]; 
		for (int i=0; i<nbVertices; i++)
		{
			vertexIndexes[i] = ((Integer)it.next()).intValue();
		}
	}
}