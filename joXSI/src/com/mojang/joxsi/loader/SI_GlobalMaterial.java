package com.mojang.joxsi.loader;

import java.util.Iterator;

/**
 * This class is a container for a template in the dotXSI file format, as specified by XSIFTK template reference.
 * 
 * <p>It's very sparsely documented.
 */
public class SI_GlobalMaterial extends Template
{
    public String referencedMaterial;
    public String propagation;

    public void parse(RawTemplate block)
    {
        Iterator it = block.values.iterator();
        referencedMaterial = (String)it.next();
        propagation = (String)it.next();
    }

    public String toString() {
        return super.toString() + " - referencedMaterial: " + referencedMaterial + ", propagation: " + propagation;
    }
}