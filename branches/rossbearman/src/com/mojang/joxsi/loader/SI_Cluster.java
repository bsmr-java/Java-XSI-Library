package com.mojang.joxsi.loader;

import java.util.Iterator;

/**
 * Stores groups of vertices on a model.
 * This is a sublevel template of the {@link SI_Model } template. 
 * See also {@link XSI_ClusterInfo }.
 * This class is a container for a template in the dotXSI file format, as specified by XSIFTK template reference.
 * <pre>
 * SI_Cluster &lt;cluster name&gt; {
 *    &lt;refmodel&gt;,
 *    &lt;weighting&gt;,
 *    &lt;cls_ctr_ref&gt;,
 *    &lt;iVertices&gt;,
 *    // indices of vertices; see example below
 * }
 * </pre>
 * It's very sparsely documented.
 * @author Notch
 * @author Egal
 */
public class SI_Cluster extends Template
{
    /** Name of the reference model. */
    public String referencedModel;
    /** Weighting. Possible values include:
     * <ul>
     * <li>AVERAGE</li> 
     * <li>ADDITIVE</li>
     * </ul>
     */
    public String weighting;
    /**
     * Cluster center reference. If there is no cluster center, use an empty
     * string instead.
     */
    public String clusterCenterReference;
    /** Number of vertices. */
    public int nbVertices;
    /**  . */
    public int[] vertexIndexes;

    @Override
    public void parse(RawTemplate block) throws ParseException
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

        if(!weighting.equals("AVERAGE") &&
                !weighting.equals("ADDITIVE"))
            throw new ParseException("Illegal weighting in SI_Cluster: "+weighting);
    }
}