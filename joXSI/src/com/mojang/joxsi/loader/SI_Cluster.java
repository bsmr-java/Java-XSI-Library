package com.mojang.joxsi.loader;

import java.util.Iterator;

/**
 * Stores groups of vertices on a model.
 * This is a sublevel template of the {@link SI_Model } template. 
 * See also {@link XSI_ClusterInfo }.
 * This class is a container for a template in the dotXSI file format, as specified by XSIFTK template reference.
 * 
 * <p>It's very sparsely documented.
 * @author Notch
 * @author Egal
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
		Iterator<Object> it = block.values.iterator();
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