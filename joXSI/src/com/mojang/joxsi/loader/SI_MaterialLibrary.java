package com.mojang.joxsi.loader;

import java.util.Iterator;

/**
 * This class is a container for a template in the dotXSI file format, as specified by XSIFTK template reference.
 * 
 * <p>It's very sparsely documented.
 */
public class SI_MaterialLibrary extends Template
{
    public int nbMaterials;
    public Material[] materials;

    public void parse(RawTemplate block)
    {
        Iterator it = block.values.iterator();
		nbMaterials = ((Integer)it.next()).intValue();
		materials = new Material[nbMaterials];
		for (int i=0; i<nbMaterials; i++)
		{
			materials[i] = (Material)it.next();
		}
    }

    public String toString()
    {
        return super.toString() + ", Number of Materials: " + nbMaterials;
    }
}