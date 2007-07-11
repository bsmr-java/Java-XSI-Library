package com.mojang.joxsi.loader;

import java.util.Iterator;

/**
 * Specifies a list of materials that are global to the scene. 
 * Note that the nested templates are different forSOFTIMAGE|XSI.
 * <p>In v3.5 and 3.6, you can have a mix of {@link SI_Material } and {@link XSI_Material }.
 * <p>This class is a container for a template in the dotXSI file format, as specified by XSIFTK template reference.
 * 
 * <p>It's very sparsely documented.
 * @author Notch
 * @author Egal
 */
public class SI_MaterialLibrary extends Template
{
    /** Number of Materials. */
    public int nbMaterials;
    /** Array of Materials. */
    public Material[] materials;

    public void parse(RawTemplate block)
    {
        Iterator<Object> it = block.values.iterator();
		nbMaterials = ((Integer)it.next()).intValue();
		materials = new Material[nbMaterials];
		for (int i=0; i<nbMaterials; i++)
		{
			materials[i] = (Material)it.next();
		}
    }

    /**
     * Returns the number of Materials in the Library.
     * 
     * @return the number of Materials in the Library.
     */
    public String toString()
    {
        return super.toString() + ", Number of Materials: " + nbMaterials;
    }
}